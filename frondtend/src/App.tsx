import { ConfirmProvider } from 'material-ui-confirm';
import './App.css';
import { AuthProvider } from './context/AuthContext';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import SignUpPage from './pages/SignUpPage';
import { ToastContainer } from 'react-toastify';
import * as React from "react";
import LoginPage from './pages/LoginPage';
import HomePage from './pages/HomePage';
import ValidOtp from './pages/ValidOtp';
import ActivatedAccount from './pages/ActivatedAccount';
import SendActiveAccount from './pages/SendActiveAccount';

const MyRoutes = () => {
  return(
    <AuthProvider>
      <ConfirmProvider>
        <div className='content' style={{flex:1}}>
            <Routes>
              <Route path="/signup" element={<SignUpPage/>} />
              <Route path="/login" element={<LoginPage/>} />
              <Route path="/" element={<HomePage/>} />
              <Route path="/user/validate_otp" element={<ValidOtp/>}/>
              <Route path="/send_active_account" element={<SendActiveAccount/>}/>
              <Route path="/user/active_account" element={<ActivatedAccount/>}/>
            </Routes>
        </div>
        <ToastContainer
        position='bottom-center'
        autoClose={3000}
        pauseOnFocusLoss={false}
        />
      </ConfirmProvider>
    </AuthProvider>
  )
}

function App() {
  return (
   <BrowserRouter>
      <MyRoutes/>
   </BrowserRouter>
  );
}

export default App;
