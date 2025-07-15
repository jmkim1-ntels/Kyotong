import logo from './logo.svg';
import './App.css';
import React, {useState} from 'react';
import axios from 'axios';
import '../src/style/Postform.css';
import { useNavigate } from 'react-router-dom'; // 라우터 훅 import

function App() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [status, setStatus] = useState("");

  const navigate = useNavigate(); // 네비게이션 훅 사용
  
  const apiBaseUrl = window.__ENV__?.API_BASE_URL;
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    try{
      const response = await axios.post(
        `${apiBaseUrl}/board/posts`,
        {
          "title": title,
          "content": content
        },
        {
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );
      if(response.status === 200) {
        alert("게시글 작성 완료!");
        setTitle('');
        setContent('');
        navigate('/');
      } else {
        setStatus("전송 실패!");
        alert("게시글 작성 중 오류가 발생했습니다. error1");
      }
    } catch (err) {
        console.error(err);
        alert("게시글 작성 중 오류가 발생했습니다. error2");
    }
  };

  return (

    <div>
    <form className="post-form" onSubmit={handleSubmit}>
    <h2>게시글 작성</h2>
      <div className='form-group'>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="제목을 입력하세요"
          required
        />
      </div>
      <div className='form-group'>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="내용을 입력하세요"
          required
        />
      </div>
      <button type="submit" className='submit-btn'>게시하기</button>
    </form>
    </div>    
  );
}

export default App;
