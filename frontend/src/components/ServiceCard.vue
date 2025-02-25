<template>
  <div class="service-card" :style="{ backgroundColor: service.color }">
    <div class="icon">{{ service.icon }}</div>
    <h3 class="title">{{ service.name }}</h3>
    <span class="category">{{ service.category }}</span>
  </div>
</template>

<script setup>
defineProps({
  service: {
    type: Object,
    required: true
  }
});
</script>

<style scoped>
.service-card {
  padding: 2rem;
  border-radius: 0.5rem;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.3s ease;
  color: white;
}

.service-card:hover {
  transform: scale(1.05);
}

.icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.title {
  font-size: 1.25rem;
  font-weight: bold;
  text-align: center;
  margin: 0;
  margin-bottom: 0.5rem;
}

.category {
  font-size: 0.875rem;
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  background-color: rgba(255, 255, 255, 0.2);
}
</style>