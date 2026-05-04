import { ref } from 'vue'

export const dynamicBreadcrumb = ref<string | null>(null)

export function useBreadcrumbs() {
  const setDynamicBreadcrumb = (title: string | null) => {
    dynamicBreadcrumb.value = title
  }

  return {
    dynamicBreadcrumb,
    setDynamicBreadcrumb
  }
}
