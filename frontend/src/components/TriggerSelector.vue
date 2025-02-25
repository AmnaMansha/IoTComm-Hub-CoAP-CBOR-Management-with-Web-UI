<script setup>
import { ref } from 'vue'

const triggers = ref([
  { id: 1, name: 'Temperature Above' },
  { id: 2, name: 'Time of Day' },
  { id: 3, name: 'Temperature' },
])

const emit = defineEmits(['select'])

const selectTrigger = (trigger) => {
  emit('select', trigger)
}
</script>

<template>
  <div class="trigger-selector">
    <h3>Select a Trigger</h3>
    <ul>
      <li
        v-for="trigger in triggers"
        :key="trigger.id"
        @click="selectTrigger(trigger)"
        class="trigger-item"
      >
        {{ trigger.name }}
      </li>
    </ul>
  </div>
</template>

<style scoped>
.trigger-item {
  cursor: pointer;
  margin: 0.5rem 0;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.trigger-item:hover {
  background-color: #f0f0f0;
}
</style>
