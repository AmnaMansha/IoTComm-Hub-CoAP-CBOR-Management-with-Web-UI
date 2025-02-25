import { defineStore } from 'pinia'

export const useServiceRuleStore = defineStore('serviceRuleStore', {
    state: () => ({
        selectedTrigger: null,
        triggers: [], // Store multiple triggers
        editingIndex: null // Track which trigger is being edited
    }),

    actions: {
        setTrigger(trigger) {
            this.selectedTrigger = trigger;
        },
        addTrigger(trigger) {
            this.triggers.push(trigger);
        },
        deleteTrigger(index) {
            this.triggers.splice(index, 1); // Remove the trigger at the specified index
        },
        setTriggerForEdit(index) {
            this.selectedTrigger = this.triggers[index];
            this.editingIndex = index; // Save index for later updates
        },
        updateTrigger(updatedTrigger) {
            if (this.editingIndex !== null) {
                this.triggers[this.editingIndex] = updatedTrigger; // Update the trigger
                this.editingIndex = null; // Reset after editing
                this.selectedTrigger = null; // Clear the selected trigger
            }
        },
        clearTriggers() {
            this.triggers = []; // Clear all triggers
            this.selectedTrigger = null;
            this.editingIndex = null;
        }
    }
})
