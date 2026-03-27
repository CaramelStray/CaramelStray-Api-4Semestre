<script setup lang="ts">
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

const formSchema = toTypedSchema(z.object({
  nomeEmpresa: z.string({ required_error: 'Obrigatório.' }).min(2, 'Nome muito curto.'),
  documento: z.string({ required_error: 'Obrigatório.' }).min(5, 'Documento inválido.'),
  email: z.string({ required_error: 'Obrigatório.' }).email('E-mail inválido.'),
  telefone: z.string({ required_error: 'Obrigatório.' }).min(8, 'Telefone inválido.'),
  responsavel: z.string({ required_error: 'Obrigatório.' }),
  pais: z.string({ required_error: 'Obrigatório.' }),
  estado: z.string({ required_error: 'Obrigatório.' }),
  cidade: z.string({ required_error: 'Obrigatório.' }),
  classificacaoDistancia: z.string({ required_error: 'Selecione uma opção.' }),
  fusoHorario: z.string({ required_error: 'Selecione um fuso.' }),
  criticidade: z.string({ required_error: 'Selecione a criticidade.' }),
}))

const form = useForm({
  validationSchema: formSchema,
})

const onSubmit = form.handleSubmit((values) => {
  console.log('Dados prontos para envio:', values)
})
</script>

<template>
  <div class="cadastro-card">
    <h1 class="cadastro-title">Novo Cliente</h1>
    <p class="cadastro-subtitle">Preencha os dados abaixo para cadastrar um novo cliente no sistema.</p>

    <form @submit="onSubmit" class="mt-8">
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-6 gap-y-4">
        
        <FormField v-slot="{ componentField }" name="nomeEmpresa">
          <FormItem>
            <FormLabel>Nome da Empresa</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Razão Social ou Nome Fantasia" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="documento">
          <FormItem>
            <FormLabel>CNPJ / Documento</FormLabel>
            <FormControl>
              <Input type="text" placeholder="00.000.000/0000-00" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="responsavel">
          <FormItem>
            <FormLabel>Nome do Responsável</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Responsável pelo contrato" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel>E-mail de Contato</FormLabel>
            <FormControl>
              <Input type="email" placeholder="contato@empresa.com" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="telefone">
          <FormItem>
            <FormLabel>Telefone de Contato</FormLabel>
            <FormControl>
              <Input type="tel" placeholder="(00) 00000-0000" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="pais">
          <FormItem>
            <FormLabel>País</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: Brasil" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="estado">
          <FormItem>
            <FormLabel>Estado / Região</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: São Paulo (SP)" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="cidade">
          <FormItem>
            <FormLabel>Cidade</FormLabel>
            <FormControl>
              <Input type="text" placeholder="Ex: São José dos Campos" v-bind="componentField" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="classificacaoDistancia">
          <FormItem>
            <FormLabel>Classificação de Distância</FormLabel>
            <Select v-bind="componentField">
              <FormControl>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectGroup>
                  <SelectItem value="local">Local</SelectItem>
                  <SelectItem value="regional">Regional</SelectItem>
                  <SelectItem value="nacional">Nacional</SelectItem>
                  <SelectItem value="internacional">Internacional</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="fusoHorario">
          <FormItem>
            <FormLabel>Fuso Horário</FormLabel>
            <Select v-bind="componentField">
              <FormControl>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectGroup>
                  <SelectItem value="utc-3">UTC -3 (Horário de Brasília)</SelectItem>
                  <SelectItem value="utc-4">UTC -4 (Manaus/Amazonas)</SelectItem>
                  <SelectItem value="utc-5">UTC -5 (Acre)</SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="criticidade">
          <FormItem>
            <FormLabel>Criticidade</FormLabel>
            <Select v-bind="componentField">
              <FormControl>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectGroup>
                  <SelectItem value="baixa">
                    <span class="flex items-center gap-2"><div class="w-2 h-2 rounded-full bg-green-500"></div> Baixa</span>
                  </SelectItem>
                  <SelectItem value="media">
                    <span class="flex items-center gap-2"><div class="w-2 h-2 rounded-full bg-yellow-500"></div> Média</span>
                  </SelectItem>
                  <SelectItem value="alta">
                    <span class="flex items-center gap-2"><div class="w-2 h-2 rounded-full bg-red-500"></div> Alta</span>
                  </SelectItem>
                </SelectGroup>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

      </div> <div class="form-actions border-t mt-8 pt-6">
        <Button type="button" variant="outline" @click="form.resetForm()">
          Limpar Campos
        </Button>
        <Button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white">
          Salvar Cliente
        </Button>
      </div>

    </form>
  </div>
</template>

<style scoped>

.cadastro-card {
  width: 100%;
  background-color: hsl(var(--card));
  color: hsl(var(--card-foreground));
  border: 1px solid hsl(var(--border));
  border-radius: var(--radius);
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
}

.cadastro-title {
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: -0.025em;
  margin-bottom: 0.5rem;
}

.cadastro-subtitle {
  font-size: 0.875rem;
  color: hsl(var(--muted-foreground));
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}
</style>