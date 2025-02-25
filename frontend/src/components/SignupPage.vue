<template>
    <div class="signup-page">
        <div class="logo">
          <svg width="122" height="37" viewBox="0 0 203 52" xmlns="http://www.w3.org/2000/svg">
            <path d="M109.374-.25H68.0791V15.3654H80.3558V52.1734H97.0968V15.3654H109.374V-.25ZM156.249-.25H114.954V15.3654H127.231V52.1734H143.972V15.3654H156.249V-.25ZM203.123-.25H161.829V15.3654H174.105V52.1734H190.846V15.3654H203.123V-.25ZM16.741-.25H0V52.1734H16.741V-.25ZM62.4997-.25H24.5535V52.1734H41.2945V37.6734H55.8033V20.9425H41.2945V15.3654H62.4997V-.25Z" fill="#222"></path>
          </svg>
        </div>
        <h1 class="main-title">Sign up</h1>
        <form>
            <input type="email" placeholder="Email" required />
            <div class="password-wrapper">
                <input type="password" placeholder="Password" required />
                <span class="show-password-icon">👁️</span>
            </div>
            <button type="submit" class="signup-btn">Get started</button>
        </form>
        <p class="login-link">Already have an account? <a href="/login" class="login-btn">Log in here.</a></p>
    </div>
</template>

<script>
export default {
    name: 'SignUpPage',
};
</script>

<style scoped>
.signup-page {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%; /* Full height within #app */
    width: 100%;
    background-color: var(--color-background-soft); /* Optional background */
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

.main-title {
  font-weight: 800;
}

.show-password-icon {
    position: absolute;
    right: 10px;
    top: 50%;
    transform: translateY(-50%);
    cursor: pointer;
}

.signup-btn {
    background-color: black;
    color: white;
    border: none;
    padding: 0.75rem;
    font-size: 1rem;
    border-radius: 20px;
    cursor: pointer;
}

.login-link {
    margin-top: 1rem;
    font-size: 1rem;
    text-align: center;
}

.login-btn {
    color: black;
    text-decoration: underline;
    cursor: pointer;
}

@media (max-width: 768px) {
    form {
        width: 100%;
    }

    .signup-btn {
        padding: 0.5rem;
    }
}
</style>