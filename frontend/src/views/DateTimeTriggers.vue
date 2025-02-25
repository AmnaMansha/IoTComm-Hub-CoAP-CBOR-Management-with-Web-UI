<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const triggers = ref([
  {
    id: 1,
    title: "Building Temperature Control",
    description: "Set temperature control based on time range and setpoint values.",
    route: "/set-trigger-building",
    icon: "🏢"
  }
]);

const handleTriggerClick = (trigger) => {
  router.push(trigger.route);
};
</script>

<template>
  <div class="date-time-triggers">
    <!-- Header -->
    <header class="header">
      <button class="back-btn" @click="$router.back()">Back</button>
      <h1>Choose a trigger</h1>
      <button class="help-btn">?</button>
    </header>

    <!-- Trigger Grid -->
    <div class="trigger-grid">
      <div
        class="trigger-card"
        v-for="trigger in triggers"
        :key="trigger.id"
        @click="handleTriggerClick(trigger)"
      >
        <div class="trigger-icon">{{ trigger.icon }}</div>
        <h3>{{ trigger.title }}</h3>
        <p>{{ trigger.description }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.date-time-triggers {
  font-family: Arial, sans-serif;
  background-color: #1a1a1a;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1rem;
  color: #fff;
}

.header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  max-width: 1200px;
}

.back-btn, .help-btn {
  background: none;
  border: 1px solid #fff;
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.trigger-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
  width: 100%;
  max-width: 1200px;
  padding: 1rem;
}

.trigger-card {
  background-color: #2d2d2d;
  border-radius: 8px;
  padding: 1.5rem;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.trigger-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.trigger-icon {
  font-size: 2rem;
  margin-bottom: 1rem;
}

h1 {
  font-size: 1.8rem;
  margin: 0;
}

h3 {
  font-size: 1.2rem;
  margin: 0 0 0.5rem 0;
  color: #fff;
}

p {
  margin: 0;
  color: #ccc;
  line-height: 1.4;
}
</style>
