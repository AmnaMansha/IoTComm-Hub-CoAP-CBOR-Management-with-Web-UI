<template>
  <div class="explore-page">
      <header>
        <div class="logo">
          <svg width="122" height="37" viewBox="0 0 203 52" xmlns="http://www.w3.org/2000/svg">
            <path d="M109.374-.25H68.0791V15.3654H80.3558V52.1734H97.0968V15.3654H109.374V-.25ZM156.249-.25H114.954V15.3654H127.231V52.1734H143.972V15.3654H156.249V-.25ZM203.123-.25H161.829V15.3654H174.105V52.1734H190.846V15.3654H203.123V-.25ZM16.741-.25H0V52.1734H16.741V-.25ZM62.4997-.25H24.5535V52.1734H41.2945V37.6734H55.8033V20.9425H41.2945V15.3654H62.4997V-.25Z" fill="#222"></path>
          </svg>
        </div>
          <nav>
              <a href="/" class="nav-link link-menu">Explore</a>
              <a href="#" class="nav-link link-menu">Plans</a>
              <button @click="goToCreate" class="get-started-btn link-menu">Create</button>
              <button  @click="goToApplet"class="get-started-btn link-menu">Applets</button>
          </nav>
      </header>

      <main>
          <h1 class="main-title">Explore</h1>
          <div class="tabs">
              <a href="#" class="tab active">All</a>
              <a href="#" class="tab">Applets</a>
              <a href="#" class="tab">Services</a>
              <a href="#" class="tab">Stories</a>
          </div>
          <div class="search-bar">
              <input type="text" placeholder="Search Applets or services" />
              <span class="search-icon">🔍</span>
          </div>

          <!-- <WeatherCard /> -->
      </main>
  </div>
</template>

<script>
import WeatherCard from '@/views/WeatherCard.vue';
export default {
  name: 'ExplorePage',
  components: {
    WeatherCard,
  },
  methods: {
      goToApplet() {
          this.$router.push('/applets');
      },
      goToCreate() {
        this.$router.push('/create');
      }
  },
};
</script>

<style scoped>
.explore-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin: 0 auto; /* Center the content horizontally */
  padding: 1rem;
  background-color: white !important;
  min-height: 100vh;
}

header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  font-weight: bold; /* Make header text bold */
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
}

nav {
  display: flex;
  align-items: center;
}

.nav-link {
  margin: 0 1rem;
  text-decoration: none;
  color: black;
  font-size: 1rem;
}

.link-menu {
  font-weight: 700;
  color: black;
}

.login-btn,
.get-started-btn {
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  cursor: pointer;
  font-size: 1rem;
}

.login-btn {
  background-color: black;
  color: white;
  margin-right: 1rem;
}

.get-started-btn {
  background-color: black;
  color: white;
  margin-right: 1rem;
}

main {
  width: 100%;
  padding: 2rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.main-title {
  color: black;
  font-weight: 800; /* Make Explore bold */
}

.tabs {
  display: flex;
  justify-content: center;
  gap: 1.5rem; /* Adjust spacing */
  margin: 1rem 0;
}

.tab {
  text-decoration: none;
  color: black;
  font-weight: bold;
  font-size: 1rem;
  padding: 0.5rem;
  border-bottom: 2px solid transparent;
  transition: border-color 0.3s;
}

.tab.active {
  border-bottom: 2px solid black;
}

.search-bar {
  width: 100%;
  max-width: 800px; /* Constrain search bar width */
  margin-top: 1rem;
  position: relative; /* To position the icon */
}

.search-bar input {
  width: 100%;
  padding: 0.75rem 2.5rem 0.75rem 0.75rem; /* Add padding for the icon */
  font-size: 1rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
}

.search-bar .search-icon {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.25rem;
  color: #888;
  pointer-events: none; /* Prevent interaction */
}

@media (max-width: 768px) {
  header {
      flex-direction: column;
  }

  nav {
      margin-top: 1rem;
  }

  .tabs {
      flex-wrap: wrap;
      gap: 1rem;
  }

  .search-bar input {
      font-size: 0.9rem;
  }
}
</style>
