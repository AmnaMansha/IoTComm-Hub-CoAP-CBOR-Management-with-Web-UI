<template>
  <button @click="router.back()" class="back-button" :style="{ color }">
    ← Back
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

defineProps({
  color: {
    type: String,
    required: true
  }
});
</script>

<style scoped>
.back-button {
  background: none;
  border: none;
  font-size: 1rem;
  font-weight: bold;
  cursor: pointer;
  padding: 0.5rem 0;
  margin-bottom: 1rem;
  transition: opacity 0.3s ease;
}

.back-button:hover {
  opacity: 0.8;
}
</style>