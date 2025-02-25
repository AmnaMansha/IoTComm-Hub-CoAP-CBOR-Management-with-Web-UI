<template>
    <div class="forgot-password-page">
        <div class="logo">
          <svg width="122" height="37" viewBox="0 0 203 52" xmlns="http://www.w3.org/2000/svg">
            <path d="M109.374-.25H68.0791V15.3654H80.3558V52.1734H97.0968V15.3654H109.374V-.25ZM156.249-.25H114.954V15.3654H127.231V52.1734H143.972V15.3654H156.249V-.25ZM203.123-.25H161.829V15.3654H174.105V52.1734H190.846V15.3654H203.123V-.25ZM16.741-.25H0V52.1734H16.741V-.25ZM62.4997-.25H24.5535V52.1734H41.2945V37.6734H55.8033V20.9425H41.2945V15.3654H62.4997V-.25Z" fill="#222"></path>
          </svg>
        </div>
        <h1 class="main-title">Forgot Password</h1>
        <form @submit.prevent="handlePasswordReset">
            <input type="email" placeholder="Enter your email" required v-model="email" />
            <button type="submit" class="reset-btn">Reset Password</button>
        </form>
        <p class="back-to-login-link">Remembered your password? <a href="/login" class="login-btn">Back to login</a></p>
    </div>
  </template>
  
  <script>
  export default {
    name: 'ForgotPasswordPage',
    mounted() {
    document.title = 'IFTTT - Forgot Password';  // Set the title for this page
  },
    data() {
      return {
        email: '',
      };
    },
    methods: {
      handlePasswordReset() {
        // Handle password reset logic here
        console.log('Password reset for:', this.email);
      }
    }
  };
  </script>
  
  <style scoped>
  .forgot-password-page {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh;
    width: 100%;
    background-color: var(--color-background-soft);
    padding: 1rem;
  }
  
  .logo {
    font-size: 1.5rem;
    font-weight: bold;
    margin-bottom: 1rem;
    text-align: center;
  }
  
  h1 {
    font-size: 2rem;
    margin-bottom: 2rem;
  }

  .main-title {
  font-weight: 800;
}
  
  form {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    width: 90%;
    max-width: 400px;
  }
  
  input {
    padding: .5em .75em;
    font-size: 1rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    width: 100%;
    font-weight: 700;
  }
  
  .reset-btn {
    background-color: black;
    color: white;
    border: none;
    padding: 0.75rem;
    font-size: 1rem;
    border-radius: 20px;
    cursor: pointer;
  }
  
  .back-to-login-link {
    margin-top: 1rem;
    font-size: 1rem;
    text-align: center;
  }
  
  .login-btn {
    color: black;
    text-decoration: underline;
    cursor: pointer;
  }
  </style>
  