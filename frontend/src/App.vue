<script setup lang="ts">
import Layout from '@/components/layout/Sidebar.vue'
import { useRoute } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { onMounted } from 'vue'

const route = useRoute()
const themeStore = useThemeStore()

onMounted(() => {
  themeStore.updateThemeClass()
})
</script>

<template>
  <router-view v-if="route.path === '/login'" />
  
  <Layout v-else />
</template>