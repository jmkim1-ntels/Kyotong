def repoUrl = 'http://192.168.22.62:32164/admin/kotonPJ.git'
def manifestUrl = 'http://192.168.22.63:32164/admin/argoTest.git'
def images = ['frontend']
def imageTag = "${env.BUILD_NUMBER}"

pipeline {
    agent none
    environment {
        ARGOCD_URL = 'http://192.168.22.62:32080'
        ARGOCD_TOKEN = credentials('ARGOCD_TOKEN_ID')
        APP_NAME = 'frontend-app'
    }

    stages {
        stage('Clone Source Repo') {
            agent { label 'master1-test' }
            steps {
                dir('source') {
                    git url: repoUrl, branch: 'frontend', credentialsId: 'd6c2b94a-b122-4a3d-a5b8-380f9a2bf7a2'
                }
            }
        }

        stage('Build & Push Docker Images') {
            agent { label 'master1-test' }
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'harbor-credential',
                        usernameVariable: 'USERNAME',
                        passwordVariable: 'PASSWORD'
                    )]) {
                        sh """
                            echo "$PASSWORD" | docker login 192.168.22.62:30002 -u "$USERNAME" --password-stdin
                        """
                    }

                    def jobs = images.collectEntries { img ->
                        ["Build & Push ${img}": {
                            dir('source') {
                                sh """
                                    docker build -t 192.168.22.62:30002/frontend/${img}:${imageTag} -f Dockerfile .
                                    docker push 192.168.22.62:30002/frontend/${img}:${imageTag}
                                """
                            }
                        }]
                    }

                    parallel jobs
                }
            }
        }

        stage('Update Manifest Repo & Conditional Apply') {
            agent { label 'master1-test' }
            steps {
                dir('manifest') {
                    git url: manifestUrl, branch: 'frontend', credentialsId: 'd6c2b94a-b122-4a3d-a5b8-380f9a2bf7a2'

                    script {
                        images.each { img ->
                            sh """
                                sed -i 's|image: .*${img}.*|image: 192.168.22.62:30002/frontend/${img}:${imageTag}|' frontend-deploy.yaml
                                echo "[INFO] YAML after sed ========================="
                                cat frontend-deploy.yaml
                            """
                        }

                        // 변경 사항 감지
                        sh 'git diff --quiet frontend-deploy.yaml || echo "changed" > .changed'
                        def changed = fileExists('.changed')

                        if (changed) {
                            echo "[INFO] YAML 변경 감지됨. git push 및 kubectl apply 실행."

                            withCredentials([usernamePassword(
                                credentialsId: 'd6c2b94a-b122-4a3d-a5b8-380f9a2bf7a2',
                                usernameVariable: 'GIT_USERNAME',
                                passwordVariable: 'GIT_PASSWORD'
                            )]) {
                                sh """
                                    git config user.email "hsjeong@ntels.com"
                                    git config user.name "admin"
                                    git remote set-url origin http://$GIT_USERNAME:$GIT_PASSWORD@192.168.22.63:32164/admin/argoTest.git

                                    git add frontend-deploy.yaml
                                    git commit -m "Update frontend image tag to ${imageTag}" || true
                                    git push origin frontend
                                """
                            }
                  
                            // 실제 배포
                            sh """
                                echo "======== KUBECTL APPLY ========"
                                kubectl apply -f frontend-deploy.yaml
                                kubectl rollout status deployment front -n frontend
                                kubectl get pods -n frontend -o wide
                            """
                       /*
                            sh """
                                curl -k -X POST \\
                                -H 'Authorization: Bearer ${ARGOCD_TOKEN}' \\
                                -H 'Content-Type: application/json' \\
                                -d '{}' \\
                                http://192.168.22.62:32443/api/v1/applications/${APP_NAME}/sync
                            """
                            */
                        } else {
                            echo "[INFO] YAML 변경 없음. git push 및 kubectl apply 생략."
                        }
                    }
                }
            }
        }

        // Optional: ArgoCD 연동 원하면 주석 해제
        /*
        stage('ArgoCD Sync') {
            agent { label 'master1-test' }
            steps {
                sh """
                    curl -k -X POST \\
                    -H 'Authorization: Bearer ${ARGOCD_TOKEN}' \\
                    -H 'Content-Type: application/json' \\
                    -d '{}' \\
                    http://192.168.22.62:32443/api/v1/applications/${APP_NAME}/sync
                """
            }
        }
        */
    }
}