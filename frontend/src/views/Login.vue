<script setup lang="ts">
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { useRouter } from 'vue-router'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { LogIn } from 'lucide-vue-next'

import { authService } from '@/services/authService'

const router = useRouter()

const formSchema = toTypedSchema(z.object({
  email: z
    .string({ required_error: 'O email é obrigatório.' })
    .email('Informe um email válido.'),
  password: z
    .string({ required_error: 'A senha é obrigatória.' })
    .min(1, 'A senha é obrigatória.')
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    email: '',
    password: '',
  },
})

const onSubmit = form.handleSubmit(async (values) => {
  try {
    const response = await authService.login({
      email: values.email,
      password: values.password,
    })
    
    localStorage.setItem('token', response.token)
    localStorage.setItem('user_email', response.email)
    localStorage.setItem('user_role', response.auth)

    router.push('/')
    
  } catch (error: any) {
    const status = error.response?.status
    if (status === 401 || status === 403) {
      form.setFieldError('email', 'Email ou senha incorretos.')
      form.setFieldError('password', 'Email ou senha incorretos.')
    } else {
      alert('Erro ao tentar fazer login. Tente novamente mais tarde.')
    }
  }
})
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-background p-4">
    <div class="w-full max-w-md bg-card p-8 rounded-xl border border-border shadow-lg">
      
      <div class="flex flex-col items-center gap-2 mb-8 border-b border-border pb-6 text-center">
        <div class="flex items-center justify-center w-12 h-12 rounded-full border shadow-sm border-blue-500 bg-blue-500/20 text-blue-400 mb-2">
          <LogIn class="w-6 h-6" />
        </div>
        <h2 class="text-xl font-bold text-foreground">Acesso ao Sistema</h2>
        <p class="text-sm text-muted-foreground">Insira suas credenciais para continuar</p>
      </div>

      <form @submit="onSubmit">
        <div class="grid grid-cols-1 gap-y-6">

          <FormField v-slot="{ componentField }" name="email">
            <FormItem>
              <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                Email <span class="text-red-500 font-bold">*</span>
              </FormLabel>
              <FormControl>
                <Input
                  type="email"
                  placeholder="exemplo@email.com"
                  class="bg-muted/20 border-border hover:border-blue-500/50 focus:border-blue-500 transition-colors"
                  v-bind="componentField"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="password">
            <FormItem>
              <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                Senha <span class="text-red-500 font-bold">*</span>
              </FormLabel>
              <FormControl>
                <Input
                  type="password"
                  placeholder="Sua senha secreta"
                  class="bg-muted/20 border-border hover:border-blue-500/50 focus:border-blue-500 transition-colors"
                  v-bind="componentField"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

        </div>

        <div class="mt-10 pt-6">
          <Button 
            type="submit" 
            class="w-full bg-blue-600 hover:bg-blue-500 text-white shadow-md shadow-blue-900/20 h-11"
          >
            Entrar
          </Button>
        </div>
      </form>
      
    </div>
  </div>
</template>