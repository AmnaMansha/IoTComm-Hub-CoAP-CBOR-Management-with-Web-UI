<script setup>
import { ref } from 'vue'

const actions = ref([
  { id: 1, name: 'Turn on Light' },
  { id: 2, name: 'Send Notification' },
  { id: 3, name: 'Log Event' },
])

const emit = defineEmits(['select'])

const selectAction = (action) => {
  emit('select', action)
}
</script>

<template>
  <div class="action-selector">
    <h3>Select an Action</h3>
    <ul>
      <li
        v-for="action in actions"
        :key="action.id"
        @click="selectAction(action)"
        class="action-item"
      >
        {{ action.name }}
      </li>
    </ul>
  </div>
</template>

<style scoped>
.action-item {
  cursor: pointer;
  margin: 0.5rem 0;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.action-item:hover {
  background-color: #f0f0f0;
}
</style>
