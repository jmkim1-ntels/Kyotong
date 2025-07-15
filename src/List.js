import logo from './logo.svg';
import './App.css';
import React, {useState, useEffect} from 'react';
import axios from 'axios';
import '../src/style/Postform.css';
import { useNavigate } from 'react-router-dom'; // 라우터 훅 import

function List() {
    
    const [list, setList] = useState([]);

    //const apiUrl = 'http://localhost:4000/board'
    const navigate = useNavigate(); // 네비게이션 훅 사용

    const apiBaseUrl = window.__ENV__?.API_BASE_URL;

    const handleAdd = () => {
        navigate('/app');
    };

    useEffect(()  => {
        const fetchList = async() => {
            try {
                const response = await axios.get(`${apiBaseUrl}/board`, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                setList(response.data);
            } catch (error) {
                console.error("Error Fetching List", error);
            }
        };
        fetchList();
    }, []);
    
    
    return (
        <div>
            {/* <div>
            <button type="submit" className='submit-btn' onClick={handleAdd}>게시물 추가</button>
            </div> */}
            <div className="post-form">
                <h2>전체 리스트 조회 페이지 v1</h2>

                <button type="submit" className='submit-btn' onClick={handleAdd}>게시글 추가하기</button>

                <table className='post-table'>
                    <thead>
                        <tr>
                            <th>No.</th>
                            <th>제목</th>
                            <th>내용</th>
                            <th>작성일</th>
                        </tr>
                    </thead>
                    <tbody>
                    {list.length === 0 ? (
                        <tr>
                            <td colSpan="4" style={{textAlign: "center"}}>게시글이 없습니다.</td>
                        </tr>
                    ) : (
                        list.map((item, index) => (
                            <tr key={item.id}>
                                <td>{index+1}</td>
                                <td>{item.title}</td>
                                <td>{item.content}</td>
                                <td>{new Date(item.createdAt).toLocaleString()}</td>
                            </tr>
                        ))
                    )}
                </tbody>
                </table>


                {/* <div className='form-group'>
                    {list.map(item => (
                        <div>
                            {item.id}.  {item.title}
                        </div>
                    ))}
                </div> */}
                
                </div>


        </div>
        

    );
}

export default List;