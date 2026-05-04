import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref(localStorage.getItem('theme') !== 'light')

  const toggleTheme = () => {
    isDark.value = !isDark.value
    localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
    updateThemeClass()
  }

  const updateThemeClass = () => {
    const root = window.document.documentElement
    if (isDark.value) {
      root.classList.remove('light')
      root.classList.add('dark')
    } else {
      root.classList.add('light')
      root.classList.remove('dark')
    }
  }

  // Initialize theme on store creation
  updateThemeClass()

  return {
    isDark,
    toggleTheme,
    updateThemeClass
  }
})
