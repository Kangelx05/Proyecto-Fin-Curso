<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'

const navOpen = ref(false)
const route = useRoute()

function toggleNav() {
  navOpen.value = !navOpen.value
  localStorage.setItem('navOpen', navOpen.value ? '1' : '0')
}

function closeNav() {
  navOpen.value = false
  localStorage.setItem('navOpen', '0')
}

// Cierra el menú al cambiar de ruta (solo si estamos en móvil)
let stopWatch: null | (() => void) = null
onMounted(() => {
  stopWatch = watch(
    () => route.fullPath,
    () => {
      if (window.innerWidth < 1024) closeNav()
    },
  )

  // Estado inicial: recordar o abrir en escritorio
  const saved = localStorage.getItem('navOpen')
  if (saved) {
    navOpen.value = saved === '1'
  } else {
    navOpen.value = window.innerWidth >= 1024
  }

  // Ajustar al redimensionar
  window.addEventListener('resize', handleResize)
})
onBeforeUnmount(() => {
  if (stopWatch) stopWatch()
  window.removeEventListener('resize', handleResize)
})

function handleResize() {
  if (window.innerWidth >= 1024) {
    navOpen.value = true
  } else {
    // Mantener el valor guardado en móvil
    const saved = localStorage.getItem('navOpen')
    navOpen.value = saved === '1'
  }
}

// Cierra con ESC
function onKey(e: KeyboardEvent) {
  if (e.key === 'Escape') closeNav()
}
onMounted(() => window.addEventListener('keydown', onKey))
onBeforeUnmount(() => window.removeEventListener('keydown', onKey))
</script>

<template>
  <div class="app-shell" :class="{ 'nav-open': navOpen }">
    <!-- Topbar -->
    <header class="topbar">
      <button class="icon-btn" aria-label="Abrir menú" :aria-expanded="navOpen" @click="toggleNav">
        <span class="burger" />
      </button>

      <div class="brand">
        <span class="logo">🍽</span>
        <span>Restaurante</span>
      </div>
    </header>

    <!-- Sidenav -->
    <aside class="sidenav" :aria-hidden="!navOpen">
      <div class="sidenav-header">
        <div class="brand small">
          <span class="logo">🍽</span>
          <span>Restaurante</span>
        </div>
        <button class="icon-btn close" aria-label="Cerrar menú" @click="closeNav">×</button>
      </div>

      <nav class="menu">
        <RouterLink to="/application" class="menu-item">Plano de mesas</RouterLink>
        <RouterLink to="/card" class="menu-item">Vista tarjetas</RouterLink>
      </nav>

      <div class="sidenav-footer">
        <div class="hint">v1.0 • {{ new Date().getFullYear() }}</div>
      </div>
    </aside>

    <!-- Backdrop -->
    <div class="backdrop" v-if="navOpen" @click="closeNav" />

    <!-- Contenido -->
    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
:root {
  --nav-width: 260px;
  --topbar-h: 56px;
  --bg: #0f172a;
  --bg-elev: #111827;
  --panel: #1f2937;
  --text: #e5e7eb;
  --muted: #9ca3af;
  --primary: #3b82f6;
  --primary-700: #2563eb;
  --border: rgba(255, 255, 255, 0.08);
  --shadow: 0 10px 30px rgba(0, 0, 0, 0.35);
}

.app-shell {
  background: var(--bg);
  color: var(--text);
  min-height: 100vh;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 40;
  height: var(--topbar-h);
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0 0.75rem;
  background: var(--bg-elev);
  border-bottom: 1px solid var(--border);
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 700;
}
.brand.small {
  font-size: 0.95rem;
}

.icon-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--text);
  display: grid;
  place-items: center;
  cursor: pointer;
  transition:
    background 0.2s ease,
    transform 0.06s ease,
    border-color 0.2s ease;
}
.icon-btn:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.18);
}
.icon-btn:active {
  transform: translateY(1px);
}
.icon-btn.close {
  font-size: 1.25rem;
  line-height: 1;
}

.burger,
.burger::before,
.burger::after {
  content: '';
  display: block;
  width: 18px;
  height: 2px;
  background: currentColor;
  border-radius: 2px;
  position: relative;
}
.burger::before {
  position: absolute;
  top: -6px;
}
.burger::after {
  position: absolute;
  top: 6px;
}

/* Sidenav */
.sidenav {
  position: fixed;
  z-index: 50;
  top: var(--topbar-h);
  left: 0;
  bottom: 0;
  width: var(--nav-width);
  background: var(--panel);
  border-right: 1px solid var(--border);
  box-shadow: var(--shadow);
  transform: translateX(-100%);
  transition: transform 0.25s ease;
  display: grid;
  grid-template-rows: auto 1fr auto;
}
.app-shell.nav-open .sidenav {
  transform: translateX(0%);
}

.backdrop {
  position: fixed;
  z-index: 45;
  inset: var(--topbar-h) 0 0 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(1px);
}

.sidenav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
  padding: 0.75rem;
  border-bottom: 1px solid var(--border);
}

.menu {
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  overflow: auto;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 0.65rem;
  border-radius: 10px;
  text-decoration: none;
  color: var(--text);
  border: 1px solid transparent;
  transition:
    background 0.18s ease,
    border-color 0.18s ease,
    transform 0.06s ease;
}
.menu-item:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.12);
}
.menu-item.router-link-exact-active {
  background: rgba(59, 130, 246, 0.12);
  border-color: rgba(59, 130, 246, 0.35);
  color: #fff;
}

.sidenav-footer {
  padding: 0.75rem;
  border-top: 1px solid var(--border);
  color: var(--muted);
  font-size: 0.85rem;
}

.content {
  min-height: calc(100vh - var(--topbar-h));
  height: calc(100vh - var(--topbar-h)); /* <- altura explícita */
  padding: 0; /* <- sin padding para evitar scroll fantasma */
  overflow: hidden; /* <- evita barras por 1px */
  transition: margin-left 0.25s ease;
}

/* Escritorio: también plegable */
@media (min-width: 1024px) {
  .backdrop {
    display: none !important;
  }
  .content {
    margin-left: 0;
    padding: 0;
  }
  .app-shell.nav-open .content {
    margin-left: var(--nav-width);
  }
  .icon-btn.close {
    display: inline-grid;
  }
}
</style>

<style>
#app {
  max-width: none;
  margin: 0;
  padding: 0;
}
</style>
