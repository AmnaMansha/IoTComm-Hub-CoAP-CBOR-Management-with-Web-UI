<template>
  <div class="action-card" :style="{ backgroundColor: color }">
    <h3 class="title">{{ action.name }}</h3>
    <p class="description">{{ action.description }}</p>
  </div>
</template>

<script setup>
defineProps({
  action: {
    type: Object,
    required: true
  },
  color: {
    type: String,
    required: true
  }
});
</script>

<style scoped>
.action-card {
  padding: 1.5rem;
  border-radius: 0.5rem;
  cursor: pointer;
  transition: transform 0.3s ease;
  color: white;
  margin-bottom: 1rem;
}

.action-card:hover {
  transform: scale(1.02);
}

.title {
  font-size: 1.25rem;
  font-weight: bold;
  margin: 0;
  margin-bottom: 0.5rem;
}

.description {
  margin: 0;
  opacity: 0.9;
}
</style>