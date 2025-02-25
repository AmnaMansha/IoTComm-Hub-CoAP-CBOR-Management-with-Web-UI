<template>
  <div class="login-page">
    <div class="logo">
          <svg width="122" height="37" viewBox="0 0 203 52" xmlns="http://www.w3.org/2000/svg">
            <path d="M109.374-.25H68.0791V15.3654H80.3558V52.1734H97.0968V15.3654H109.374V-.25ZM156.249-.25H114.954V15.3654H127.231V52.1734H143.972V15.3654H156.249V-.25ZM203.123-.25H161.829V15.3654H174.105V52.1734H190.846V15.3654H203.123V-.25ZM16.741-.25H0V52.1734H16.741V-.25ZM62.4997-.25H24.5535V52.1734H41.2945V37.6734H55.8033V20.9425H41.2945V15.3654H62.4997V-.25Z" fill="#222"></path>
          </svg>
        </div>
      <h1 class="main-title">Log in</h1>
      <form @submit.prevent="handleLogin">
          <input type="email" placeholder="Email" required v-model="email" />
          <div class="password-wrapper">
              <input :type="passwordVisible ? 'text' : 'password'" placeholder="Password" required v-model="password" />
              <span @click="togglePasswordVisibility" class="show-password-icon">👁️</span>
          </div>
          <a href="/forgot-password" class="forgot-password">Forgot your password?</a>
          <button type="submit" class="login-btn">Log in</button>
      </form>
      <p class="signup-link">New to IFTTT? <a href="/signup" class="signup-btn">Sign up here.</a></p>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      email: '',
      password: '',
      passwordVisible: false,
    };
  },
  methods: {
    handleLogin() {
      // Handle login logic here
      console.log('Logging in with:', this.email, this.password);
    },
    togglePasswordVisibility() {
      this.passwordVisible = !this.passwordVisible;
    }
  }
};
</script>

<style scoped>
.login-page {
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

.password-wrapper {
  position: relative;
}

.show-password-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
}

.forgot-password {
  font-size: 0.9rem;
  color: black;
  text-decoration: none;
  margin-top: -0.5rem;
  text-align: center;
}

.login-btn {
  background-color: black;
  color: white;
  border: none;
  padding: 0.75rem;
  font-size: 1rem;
  border-radius: 20px;
  cursor: pointer;
}

.signup-link {
  margin-top: 1rem;
  font-size: 1rem;
  text-align: center;
}

.signup-btn {
  color: black;
  text-decoration: underline;
  cursor: pointer;
}

.main-title {
  font-weight: 800;
}

@media (max-width: 768px) {
  form {
    width: 100%;
  }

  .login-btn {
    padding: 0.5rem;
  }
}
</style>
