import { ref } from 'vue'

/**
 * Composable to store and control the draggable state of tables across
 * components.  When used in both the table plan view and in the
 * application header, the boolean ref returned here will remain in
 * sync because it is defined at the module scope.
 */
const draggable = ref(false)

/**
 * Toggles the global draggable value on or off.  Invoking this
 * function from any component will update the shared state.
 */
function toggleDraggable() {
  draggable.value = !draggable.value
}

export default function useDrag() {
  return {
    draggable,
    toggleDraggable,
  }
}