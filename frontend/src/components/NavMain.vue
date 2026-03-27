<script setup lang="ts">
import type { LucideIcon } from "lucide-vue-next"
import { ChevronRight } from "lucide-vue-next"
import { useRoute } from 'vue-router'
import {
  Collapsible, CollapsibleContent, CollapsibleTrigger,
} from '@/components/ui/collapsible'
import {
  SidebarGroup, SidebarGroupLabel, SidebarMenu,
  SidebarMenuButton, SidebarMenuItem, SidebarMenuSub,
  SidebarMenuSubButton, SidebarMenuSubItem,
} from '@/components/ui/sidebar'

defineProps<{
  label?: string
  items: {
    title: string
    url: string
    icon?: LucideIcon
    isActive?: boolean
    items?: { title: string; url: string }[]
  }[]
}>()

const route = useRoute()
const isActive = (url: string) => route.path.startsWith(url)
</script>

<template>
  <SidebarGroup>
    <SidebarGroupLabel v-if="label">{{ label }}</SidebarGroupLabel>
    <SidebarMenu>
      <template v-for="item in items" :key="item.title">
        <Collapsible
          v-if="item.items?.length"
          as-child
          :default-open="item.isActive"
          class="group/collapsible"
        >
          <SidebarMenuItem>
            <CollapsibleTrigger as-child>
              <SidebarMenuButton :tooltip="item.title" :is-active="isActive(item.url)">
                <component :is="item.icon" v-if="item.icon" />
                <span>{{ item.title }}</span>
                <ChevronRight class="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
              </SidebarMenuButton>
            </CollapsibleTrigger>
            <CollapsibleContent>
              <SidebarMenuSub>
                <SidebarMenuSubItem v-for="subItem in item.items" :key="subItem.title">
                  <SidebarMenuSubButton as-child :is-active="isActive(subItem.url)">
                    <RouterLink :to="subItem.url">
                      <span>{{ subItem.title }}</span>
                    </RouterLink>
                  </SidebarMenuSubButton>
                </SidebarMenuSubItem>
              </SidebarMenuSub>
            </CollapsibleContent>
          </SidebarMenuItem>
        </Collapsible>

        <SidebarMenuItem v-else>
          <SidebarMenuButton as-child :tooltip="item.title" :is-active="isActive(item.url)">
            <RouterLink :to="item.url">
              <component :is="item.icon" v-if="item.icon" />
              <span>{{ item.title }}</span>
            </RouterLink>
          </SidebarMenuButton>
        </SidebarMenuItem>
      </template>
    </SidebarMenu>
  </SidebarGroup>
</template>