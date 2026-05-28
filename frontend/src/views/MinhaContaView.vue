<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Input } from '@/components/ui/input'
import {
  Loader2, CheckCircle2, AlertCircle,
  Eye, EyeOff, KeyRound, Check, X, Camera
} from 'lucide-vue-next'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { useLocalStorage } from '@vueuse/core'

const userRole  = useLocalStorage('user_role', '')
const userEmail = useLocalStorage('user_email', '')
const userName  = useLocalStorage('user_name', '')
const avatarUrl = useLocalStorage('user_avatar', '')
const isTecnico = computed(() => userRole.value === 'ROLE_TECNICO')
const isGestor  = computed(() => userRole.value === 'ROLE_ADMIN')

const loading      = ref(true)
const tecnicoData  = ref<TecnicoResponseDTO | null>(null)
const perfil       = ref({ id: null as number | null, nome: '', email: userEmail.value, telefone: '', cpf: '' })
const globalErro   = ref('')
const globalSucesso = ref('')

type Tab = 'perfil' | 'seguranca'
const activeTab = ref<Tab>('perfil')

type FieldKey = 'nome' | 'email' | 'telefone' | 'cpf'
const editingField = ref<FieldKey | null>(null)
const fieldDraft   = ref('')
const savingField  = ref(false)

function startEdit(field: FieldKey) {
  editingField.value = field
  fieldDraft.value   = perfil.value[field] || ''
}
function cancelEdit() {
  editingField.value = null
  fieldDraft.value   = ''
}
async function saveField(field: FieldKey) {
  savingField.value = true
  globalErro.value  = ''
  try {
    perfil.value[field] = fieldDraft.value
    if (isTecnico.value && perfil.value.id) {
      await tecnicoService.atualizar(perfil.value.id, {
        email: perfil.value.email, nome: perfil.value.nome,
        telefone: perfil.value.telefone, cpf: perfil.value.cpf,
      } as any)
    }
    if (field === 'email') userEmail.value = perfil.value.email
    if (field === 'nome') userName.value = perfil.value.nome
    globalSucesso.value = 'Alteração salva.'
    editingField.value  = null
    setTimeout(() => { globalSucesso.value = '' }, 3500)
  } catch (e: any) {
    globalErro.value = e.message || 'Erro ao salvar.'
  } finally {
    savingField.value = false
  }
}

const savingSenha   = ref(false)
const senhaForm     = ref({ atual: '', nova: '', confirmar: '' })
const senhaErro     = ref('')
const senhaSucesso  = ref('')
const showAtual     = ref(false)
const showNova      = ref(false)
const showConfirmar = ref(false)
const editingSenha  = ref(false)

const senhaForca = computed(() => {
  const s = senhaForm.value.nova
  if (!s) return 0
  let n = 0
  if (s.length >= 8) n++
  if (/[A-Z]/.test(s)) n++
  if (/[0-9]/.test(s)) n++
  if (/[^A-Za-z0-9]/.test(s)) n++
  return n
})
const forcaLabel = ['', 'Fraca', 'Razoável', 'Boa', 'Forte']
const forcaColor = ['', '#ef4444', '#f59e0b', '#3b82f6', '#10b981']

async function alterarSenha() {
  senhaErro.value = ''
  senhaSucesso.value = ''
  if (!senhaForm.value.atual) { senhaErro.value = 'Informe a senha atual.'; return }
  if (senhaForm.value.nova.length < 6) { senhaErro.value = 'Mínimo 6 caracteres.'; return }
  if (senhaForm.value.nova !== senhaForm.value.confirmar) { senhaErro.value = 'As senhas não coincidem.'; return }
  savingSenha.value = true
  try {
    if (isTecnico.value && perfil.value.id) {
      await tecnicoService.atualizar(perfil.value.id, {
        email: perfil.value.email, nome: perfil.value.nome, senha: senhaForm.value.nova,
      } as any)
    }
    senhaSucesso.value = 'Senha alterada com sucesso.'
    senhaForm.value = { atual: '', nova: '', confirmar: '' }
    editingSenha.value = false
    setTimeout(() => { senhaSucesso.value = '' }, 4000)
  } catch (e: any) {
    senhaErro.value = e.message || 'Erro ao alterar senha.'
  } finally {
    savingSenha.value = false
  }
}

function onAvatarChange(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  const reader = new FileReader()
  reader.onload = () => {
    avatarUrl.value = reader.result as string
  }
  reader.readAsDataURL(file)
}

const avatarColors = ['#2563eb', '#059669', '#7c3aed', '#db2777']
const avatarBg = computed(() => avatarColors[(perfil.value.nome || 'U').length % avatarColors.length])
const initials = computed(() => {
  const n = perfil.value.nome || perfil.value.email || 'U'
  const p = n.trim().split(' ')
  return p.length >= 2 ? (p[0][0] + p[1][0]).toUpperCase() : n.substring(0, 2).toUpperCase()
})

onMounted(async () => {
  try {
    if (isTecnico.value) {
      const todos = await tecnicoService.listar()
      const t = todos.find(x => x.email === userEmail.value)
      if (t) {
        tecnicoData.value = await tecnicoService.buscarPorId(t.id)
        perfil.value = {
          id: tecnicoData.value.id, nome: tecnicoData.value.nome, email: tecnicoData.value.email,
          telefone: tecnicoData.value.telefone || '', cpf: tecnicoData.value.cpf || '',
        }
        userName.value = tecnicoData.value.nome
      }
    } else {
      perfil.value.nome = userName.value || 'Administrador'
    }
  } catch (e: any) {
    globalErro.value = e.message || 'Erro ao carregar.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="min-h-full bg-background">
    <div v-if="loading" class="flex items-center justify-center py-40 gap-3 text-muted-foreground">
      <Loader2 class="size-4 animate-spin" />
      <span class="text-sm">Carregando...</span>
    </div>

    <template v-else>
      <div class="w-full px-8 py-10">

        <div class="mb-7">
          <h1 class="text-2xl font-semibold text-foreground tracking-tight">Minha Conta</h1>
          <p class="text-sm text-muted-foreground mt-1">Gerencie seus dados e preferências de acesso.</p>
        </div>

        <div class="flex items-center gap-0.5 border-b border-border mb-8">
          <button
            v-for="tab in [{ key: 'perfil', label: 'Meu perfil' }, { key: 'seguranca', label: 'Segurança' }]"
            :key="tab.key"
            @click="activeTab = tab.key as Tab"
            :class="['px-4 py-2.5 text-sm transition-colors relative', activeTab === tab.key ? 'text-foreground font-medium' : 'text-muted-foreground hover:text-foreground/80']"
          >
            {{ tab.label }}
            <span v-if="activeTab === tab.key" class="absolute bottom-0 left-0 right-0 h-px bg-foreground rounded-t-full"></span>
          </button>
        </div>

        <Transition name="slide">
          <div v-if="globalSucesso || senhaSucesso" class="flex items-center gap-2.5 rounded-lg border border-emerald-500/20 bg-emerald-500/10 px-4 py-3 mb-6">
            <CheckCircle2 class="size-4 text-emerald-400 shrink-0" />
            <p class="text-sm text-emerald-300">{{ globalSucesso || senhaSucesso }}</p>
          </div>
        </Transition>
        <Transition name="slide">
          <div v-if="globalErro" class="flex items-center gap-2.5 rounded-lg border border-red-500/20 bg-red-500/10 px-4 py-3 mb-6">
            <AlertCircle class="size-4 text-red-400 shrink-0" />
            <p class="text-sm text-red-300">{{ globalErro }}</p>
          </div>
        </Transition>

        <!-- TAB PERFIL -->
        <div v-if="activeTab === 'perfil'">
          <h2 class="text-base font-semibold text-foreground mb-6">Meu perfil</h2>

          <!-- Foto -->
          <div class="flex items-start gap-8 py-5 border-b border-border">
            <div class="w-56 shrink-0">
              <p class="text-sm font-medium text-foreground">Foto</p>
              <p class="text-xs text-muted-foreground mt-0.5">Será exibida no seu perfil.</p>
            </div>
            <div class="flex-1 flex items-center gap-4">

              <label class="cursor-pointer group relative shrink-0">
                <input type="file" accept="image/*" class="hidden" @change="onAvatarChange" />

                <img
                  v-if="avatarUrl"
                  :src="avatarUrl"
                  class="size-11 rounded-full object-cover ring-2 ring-border group-hover:ring-blue-400 transition-all"
                />
                <div
                  v-else
                  class="size-11 rounded-full flex items-center justify-center text-sm font-bold text-white ring-2 ring-border group-hover:ring-blue-400 transition-all select-none"
                  :style="{ backgroundColor: avatarBg }"
                >
                  {{ initials }}
                </div>

                <div class="absolute inset-0 rounded-full bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <Camera class="size-4 text-white" />
                </div>
              </label>

              <div>
                <p class="text-sm font-medium text-foreground">{{ perfil.nome || 'Usuário' }}</p>
                <p class="text-xs text-muted-foreground mt-0.5">{{ isGestor ? 'Gestor Admin' : 'Técnico' }}</p>
              </div>
            </div>
          </div>

          <!-- Nome -->
          <div class="flex items-start gap-8 py-5 border-b border-border">
            <div class="w-56 shrink-0">
              <p class="text-sm font-medium text-foreground">Nome completo</p>
              <p class="text-xs text-muted-foreground mt-0.5">Será exibido no sistema.</p>
            </div>
            <div class="flex-1">
              <p v-if="editingField !== 'nome'" class="text-sm text-foreground">{{ perfil.nome || '\u2014' }}</p>
              <div v-else class="flex items-center gap-2 max-w-xs">
                <Input v-model="fieldDraft" class="h-8 text-sm bg-muted/20 border-border" autofocus @keyup.enter="saveField('nome')" @keyup.esc="cancelEdit" />
                <button @click="saveField('nome')" :disabled="savingField" class="size-8 rounded-md flex items-center justify-center hover:bg-emerald-500/10 text-emerald-400 transition-colors disabled:opacity-40 shrink-0">
                  <Loader2 v-if="savingField" class="size-3.5 animate-spin" /><Check v-else class="size-3.5" />
                </button>
                <button @click="cancelEdit" class="size-8 rounded-md flex items-center justify-center hover:bg-muted/30 text-muted-foreground transition-colors shrink-0"><X class="size-3.5" /></button>
              </div>
            </div>
            <button v-if="editingField !== 'nome'" @click="startEdit('nome')" class="text-xs text-blue-400 hover:text-blue-300 transition-colors shrink-0 mt-0.5">Editar</button>
          </div>

          <!-- Email -->
          <div class="flex items-start gap-8 py-5 border-b border-border">
            <div class="w-56 shrink-0">
              <p class="text-sm font-medium text-foreground">E-mail</p>
              <p class="text-xs text-muted-foreground mt-0.5">Usado para login e notificações.</p>
            </div>
            <div class="flex-1">
              <p v-if="editingField !== 'email'" class="text-sm text-foreground">{{ perfil.email || '\u2014' }}</p>
              <div v-else class="flex items-center gap-2 max-w-xs">
                <Input v-model="fieldDraft" type="email" class="h-8 text-sm bg-muted/20 border-border" autofocus @keyup.enter="saveField('email')" @keyup.esc="cancelEdit" />
                <button @click="saveField('email')" :disabled="savingField" class="size-8 rounded-md flex items-center justify-center hover:bg-emerald-500/10 text-emerald-400 transition-colors disabled:opacity-40 shrink-0">
                  <Loader2 v-if="savingField" class="size-3.5 animate-spin" /><Check v-else class="size-3.5" />
                </button>
                <button @click="cancelEdit" class="size-8 rounded-md flex items-center justify-center hover:bg-muted/30 text-muted-foreground transition-colors shrink-0"><X class="size-3.5" /></button>
              </div>
            </div>
            <button v-if="editingField !== 'email'" @click="startEdit('email')" class="text-xs text-blue-400 hover:text-blue-300 transition-colors shrink-0 mt-0.5">Editar</button>
          </div>

          <template v-if="isTecnico">
            <!-- Telefone -->
            <div class="flex items-start gap-8 py-5 border-b border-border">
              <div class="w-56 shrink-0">
                <p class="text-sm font-medium text-foreground">Telefone</p>
                <p class="text-xs text-muted-foreground mt-0.5">Número para contato direto.</p>
              </div>
              <div class="flex-1">
                <p v-if="editingField !== 'telefone'" class="text-sm text-foreground">{{ perfil.telefone || '\u2014' }}</p>
                <div v-else class="flex items-center gap-2 max-w-xs">
                  <Input v-model="fieldDraft" placeholder="(00) 00000-0000" class="h-8 text-sm bg-muted/20 border-border" autofocus @keyup.enter="saveField('telefone')" @keyup.esc="cancelEdit" />
                  <button @click="saveField('telefone')" :disabled="savingField" class="size-8 rounded-md flex items-center justify-center hover:bg-emerald-500/10 text-emerald-400 transition-colors disabled:opacity-40 shrink-0">
                    <Loader2 v-if="savingField" class="size-3.5 animate-spin" /><Check v-else class="size-3.5" />
                  </button>
                  <button @click="cancelEdit" class="size-8 rounded-md flex items-center justify-center hover:bg-muted/30 text-muted-foreground transition-colors shrink-0"><X class="size-3.5" /></button>
                </div>
              </div>
              <button v-if="editingField !== 'telefone'" @click="startEdit('telefone')" class="text-xs text-blue-400 hover:text-blue-300 transition-colors shrink-0 mt-0.5">Editar</button>
            </div>

            <!-- CPF -->
            <div class="flex items-start gap-8 py-5 border-b border-border">
              <div class="w-56 shrink-0">
                <p class="text-sm font-medium text-foreground">CPF <span class="text-muted-foreground/40">*</span></p>
                <p class="text-xs text-muted-foreground mt-0.5">Documento de identificação.</p>
              </div>
              <div class="flex-1">
                <p v-if="editingField !== 'cpf'" class="text-sm font-mono text-foreground">{{ perfil.cpf || '\u2014' }}</p>
                <div v-else class="flex items-center gap-2 max-w-xs">
                  <Input v-model="fieldDraft" placeholder="000.000.000-00" class="h-8 text-sm font-mono bg-muted/20 border-border" autofocus @keyup.enter="saveField('cpf')" @keyup.esc="cancelEdit" />
                  <button @click="saveField('cpf')" :disabled="savingField" class="size-8 rounded-md flex items-center justify-center hover:bg-emerald-500/10 text-emerald-400 transition-colors disabled:opacity-40 shrink-0">
                    <Loader2 v-if="savingField" class="size-3.5 animate-spin" /><Check v-else class="size-3.5" />
                  </button>
                  <button @click="cancelEdit" class="size-8 rounded-md flex items-center justify-center hover:bg-muted/30 text-muted-foreground transition-colors shrink-0"><X class="size-3.5" /></button>
                </div>
              </div>
              <button v-if="editingField !== 'cpf'" @click="startEdit('cpf')" class="text-xs text-blue-400 hover:text-blue-300 transition-colors shrink-0 mt-0.5">Editar</button>
            </div>

            <div v-if="tecnicoData" class="flex items-start gap-8 py-5 border-b border-border">
              <div class="w-56 shrink-0">
                <p class="text-sm font-medium text-foreground">Certificações</p>
                <p class="text-xs text-muted-foreground mt-0.5">Habilidades registradas.</p>
              </div>
              <p class="text-sm text-foreground flex-1">{{ tecnicoData.habilidades?.length || 0 }} ativas</p>
            </div>

            <div v-if="tecnicoData" class="flex items-start gap-8 py-5">
              <div class="w-56 shrink-0">
                <p class="text-sm font-medium text-foreground">Estado</p>
                <p class="text-xs text-muted-foreground mt-0.5">Disponibilidade atual.</p>
              </div>
              <div class="flex items-center gap-2 flex-1">
                <span :class="['size-1.5 rounded-full shrink-0', tecnicoData.estado ? 'bg-emerald-500' : 'bg-slate-500']"></span>
                <p class="text-sm text-foreground">{{ tecnicoData.estado || 'Indefinido' }}</p>
              </div>
            </div>
          </template>

          <template v-if="isGestor">
            <div class="flex items-start gap-8 py-5">
              <div class="w-56 shrink-0">
                <p class="text-sm font-medium text-foreground">Função</p>
                <p class="text-xs text-muted-foreground mt-0.5">Nível de acesso ao sistema.</p>
              </div>
              <div class="flex-1">
                <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-semibold border border-violet-500/25 bg-violet-500/10 text-violet-400">Gestor Admin</span>
              </div>
            </div>
          </template>
        </div>

        <!-- TAB SEGURANCA -->
        <div v-if="activeTab === 'seguranca'">
          <h2 class="text-base font-semibold text-foreground mb-6">Segurança</h2>

          <div class="flex items-start gap-8 py-5 border-b border-border">
            <div class="w-56 shrink-0">
              <p class="text-sm font-medium text-foreground">Senha</p>
              <p class="text-xs text-muted-foreground mt-0.5">Altere sua senha de acesso.</p>
            </div>
            <div class="flex-1">
              <p class="text-sm tracking-widest text-muted-foreground/30 select-none">&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;</p>
            </div>
            <button @click="editingSenha = !editingSenha" class="text-xs text-blue-400 hover:text-blue-300 transition-colors shrink-0 mt-0.5">
              {{ editingSenha ? 'Cancelar' : 'Alterar' }}
            </button>
          </div>

          <Transition name="expand">
            <div v-if="editingSenha" class="py-6 border-b border-border space-y-5">
              <Transition name="slide">
                <div v-if="senhaErro" class="flex items-center gap-2.5 rounded-lg border border-red-500/20 bg-red-500/10 px-4 py-3">
                  <AlertCircle class="size-4 text-red-400 shrink-0" /><p class="text-sm text-red-300">{{ senhaErro }}</p>
                </div>
              </Transition>

              <div class="max-w-sm space-y-4">
                <div class="space-y-1.5">
                  <label class="text-xs font-medium text-muted-foreground">Senha atual</label>
                  <div class="relative">
                    <Input :type="showAtual ? 'text' : 'password'" v-model="senhaForm.atual" placeholder="Sua senha atual" class="h-9 text-sm bg-muted/20 border-border pr-10" />
                    <button type="button" @click="showAtual = !showAtual" class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/40 hover:text-muted-foreground transition-colors">
                      <Eye v-if="!showAtual" class="size-3.5" /><EyeOff v-else class="size-3.5" />
                    </button>
                  </div>
                </div>
                <div class="space-y-1.5">
                  <label class="text-xs font-medium text-muted-foreground">Nova senha</label>
                  <div class="relative">
                    <Input :type="showNova ? 'text' : 'password'" v-model="senhaForm.nova" placeholder="Mínimo 6 caracteres" class="h-9 text-sm bg-muted/20 border-border pr-10" />
                    <button type="button" @click="showNova = !showNova" class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/40 hover:text-muted-foreground transition-colors">
                      <Eye v-if="!showNova" class="size-3.5" /><EyeOff v-else class="size-3.5" />
                    </button>
                  </div>
                  <div v-if="senhaForm.nova" class="space-y-1">
                    <div class="flex gap-1 h-0.5 max-w-48">
                      <div v-for="i in 4" :key="i" class="flex-1 rounded-full transition-all duration-300" :style="{ backgroundColor: i <= senhaForca ? forcaColor[senhaForca] : 'rgba(255,255,255,0.08)' }"></div>
                    </div>
                    <p class="text-[10px]" :style="{ color: forcaColor[senhaForca] }">{{ forcaLabel[senhaForca] }}</p>
                  </div>
                </div>
                <div class="space-y-1.5">
                  <label class="text-xs font-medium text-muted-foreground">Confirmar nova senha</label>
                  <div class="relative">
                    <Input :type="showConfirmar ? 'text' : 'password'" v-model="senhaForm.confirmar" placeholder="Repita a senha" class="h-9 text-sm bg-muted/20 border-border pr-10" :class="senhaForm.confirmar && senhaForm.confirmar !== senhaForm.nova ? 'border-red-500/40' : ''" />
                    <button type="button" @click="showConfirmar = !showConfirmar" class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground/40 hover:text-muted-foreground transition-colors">
                      <Eye v-if="!showConfirmar" class="size-3.5" /><EyeOff v-else class="size-3.5" />
                    </button>
                  </div>
                  <p v-if="senhaForm.confirmar && senhaForm.confirmar !== senhaForm.nova" class="text-[10px] text-red-400">As senhas não coincidem</p>
                </div>
              </div>

              <div class="flex items-center gap-3 pt-1">
                <button @click="alterarSenha" :disabled="savingSenha" class="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-foreground text-background text-xs font-medium hover:bg-foreground/90 transition-colors disabled:opacity-50">
                  <Loader2 v-if="savingSenha" class="size-3 animate-spin" /><KeyRound v-else class="size-3" />
                  {{ savingSenha ? 'Salvando...' : 'Salvar nova senha' }}
                </button>
                <button @click="() => { editingSenha = false; senhaForm = { atual: '', nova: '', confirmar: '' }; senhaErro = '' }" class="text-xs text-muted-foreground hover:text-foreground transition-colors">
                  Cancelar
                </button>
              </div>
            </div>
          </Transition>

          <div class="flex items-start gap-8 py-5">
            <div class="w-56 shrink-0">
              <p class="text-sm font-medium text-foreground">Sessão ativa</p>
              <p class="text-xs text-muted-foreground mt-0.5">Conta com acesso autenticado.</p>
            </div>
            <div class="flex-1 flex items-center gap-2">
              <span class="size-1.5 rounded-full bg-emerald-500 shrink-0"></span>
              <p class="text-sm text-foreground">{{ perfil.email }}</p>
            </div>
          </div>
        </div>

      </div>
    </template>
  </div>
</template>

<style scoped>
.slide-enter-active, .slide-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateY(-4px); }
.expand-enter-active, .expand-leave-active { transition: opacity 0.2s ease, max-height 0.3s ease; overflow: hidden; max-height: 700px; }
.expand-enter-from, .expand-leave-to { opacity: 0; max-height: 0; }
</style>