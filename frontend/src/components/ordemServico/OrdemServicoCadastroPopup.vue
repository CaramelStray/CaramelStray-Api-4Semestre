<template>
  <!-- Steps Header -->
  <div class="flex items-center gap-2 mb-6 border-b border-border pb-6 mt-2 overflow-x-auto whitespace-nowrap scrollbar-hide">
    <template v-for="(s, index) in stepsList" :key="s.id">
      <div :class="['flex items-center gap-2 transition-colors', step === s.id ? 'text-blue-400 font-bold' : step > s.id ? 'text-blue-400/60' : 'text-muted-foreground']">
        <div
          class="flex items-center justify-center w-8 h-8 rounded-full border shadow-sm transition-all"
          :class="step === s.id
            ? 'border-blue-500 bg-blue-500/20 text-blue-400'
            : step > s.id
              ? 'border-blue-500/40 bg-blue-500/10 text-blue-400/60'
              : 'border-muted-foreground/40 bg-muted/20 text-muted-foreground'"
        >
          <CheckCircle2 v-if="step > s.id" class="w-4 h-4 text-blue-400/60" />
          <component :is="s.icon" v-else class="w-4 h-4" />
        </div>
        <span class="text-sm hidden sm:inline-block">{{ s.name }}</span>
      </div>
      <ChevronRight v-if="(index as number) < stepsList.length - 1" class="w-4 h-4 mx-1 text-muted-foreground/30 shrink-0" />
    </template>
  </div>

  <form @submit="onSubmit">

    <!-- STEP 1: Identificação -->
    <div v-show="step === 1" class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-6">

      <!-- Cliente -->
      <FormField v-slot="{ value, handleChange }" name="codigoCliente">
        <FormItem>
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Cliente <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            @update:model-value="(val: string) => { handleChange(val); onClienteChange(val) }"
          >
            <FormControl>
              <SelectTrigger class="w-full bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                <SelectValue placeholder="Selecione um cliente..." />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200] max-h-[200px]">
              <SelectItem v-for="c in clientes" :key="c.id" :value="c.id.toString()">
                {{ c.nomeEmpresa }} ({{ c.documento }})
              </SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Contrato -->
      <FormField v-slot="{ value, handleChange }" name="codigoContrato">
        <FormItem>
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Contrato <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            :disabled="!selectedClienteId"
            @update:model-value="(val: string) => { handleChange(val); onContratoChange(val) }"
          >
            <FormControl>
              <SelectTrigger class="w-full bg-muted/20 border-border hover:border-blue-500/50 transition-colors disabled:opacity-50">
                <SelectValue :placeholder="selectedClienteId ? 'Selecione um contrato...' : 'Selecione um cliente primeiro'" />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200] max-h-[200px]">
              <SelectItem v-for="ct in contratosFiltrados" :key="ct.codigo" :value="ct.codigo.toString()">
                {{ ct.descricao }} — {{ ct.status }} ({{ ct.dataInicio }} até {{ ct.dataFim }})
              </SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Criticidade -->
      <FormField v-slot="{ value, handleChange }" name="criticidade">
        <FormItem>
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Criticidade <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            @update:model-value="(val: string) => handleChange(val)"
          >
            <FormControl>
              <SelectTrigger class="w-full bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                <SelectValue placeholder="Selecione a criticidade..." />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200]">
              <SelectItem value="CRITICA">Crítica</SelectItem>
              <SelectItem value="ALTA">Alta</SelectItem>
              <SelectItem value="MEDIA">Média</SelectItem>
              <SelectItem value="BAIXA">Baixa</SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Tipo de Ordem -->
      <FormField v-slot="{ value, handleChange }" name="tipoOrdem">
        <FormItem>
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Tipo de Ordem <span class="text-red-500 font-bold">*</span>
          </FormLabel>
          <Select
            :model-value="value"
            @update:model-value="(val: string) => handleChange(val)"
          >
            <FormControl>
              <SelectTrigger class="w-full bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                <SelectValue placeholder="Selecione o tipo de ordem..." />
              </SelectTrigger>
            </FormControl>
            <SelectContent class="z-[200]">
              <SelectItem value="MANUTENCAO_PREVENTIVA">Manutenção Preventiva</SelectItem>
              <SelectItem value="MANUTENCAO_CORRETIVA">Manutenção Corretiva</SelectItem>
              <SelectItem value="INSTALACAO">Instalação</SelectItem>
            </SelectContent>
          </Select>
          <FormMessage />
        </FormItem>
      </FormField>

      <!-- Data de Abertura — input nativo readonly -->
      <div class="space-y-2">
        <label class="flex items-center gap-1 text-sm font-medium text-foreground/80">
          Data de Abertura
        </label>
        <input
          :value="todayDisplayString"
          readonly
          class="flex h-9 w-full rounded-md border border-border/50 bg-muted/10 px-3 py-1 text-sm text-muted-foreground shadow-sm cursor-not-allowed"
        />
      </div>

      <!-- Observação Geral -->
      <FormField v-slot="{ componentField }" name="observacaoGeral">
        <FormItem class="md:col-span-2">
          <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
            Observação Geral
          </FormLabel>
          <FormControl>
            <Textarea
              placeholder="Descreva o problema ou motivo da ordem de serviço..."
              class="resize-y min-h-[90px] bg-muted/20 border-border hover:border-blue-500/50 transition-colors focus:border-blue-500"
              v-bind="componentField"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

    </div>

    <!-- STEP 2: Máquina -->
    <div v-show="step === 2" class="space-y-6">

      <div v-if="!selectedContratoId" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/5">
        <Server class="w-8 h-8 mx-auto text-muted-foreground/40 mb-3" />
        <p class="text-sm text-muted-foreground">Selecione um contrato no passo anterior para ver as máquinas disponíveis.</p>
      </div>

      <div v-else>
        <FormField v-slot="{ value, handleChange }" name="codigoMaquinaContrato">
          <FormItem>
            <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
              Máquina do Contrato <span class="text-red-500 font-bold">*</span>
            </FormLabel>
            <Select
              :model-value="value"
              @update:model-value="(val: string) => { handleChange(val); onMaquinaChange(val) }"
            >
              <FormControl>
                <SelectTrigger class="w-full bg-muted/20 border-border hover:border-blue-500/50 transition-colors">
                  <SelectValue placeholder="Selecione uma máquina..." />
                </SelectTrigger>
              </FormControl>
              <SelectContent class="z-[200] max-h-[200px]">
                <SelectItem v-for="m in maquinasFiltradas" :key="m.codigo" :value="m.codigo.toString()">
                  Máquina #{{ m.codigo }} — Cat. {{ m.codigoCatalogoMaquina }} (instalada em {{ m.dataInstalacao }})
                </SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <!-- Painel de Software Instalado -->
        <div v-if="selectedMaquinaId" class="mt-5">
          <div v-if="loadingSoftware" class="flex items-center gap-3 text-sm text-muted-foreground p-4 border border-border rounded-lg bg-muted/10">
            <Loader2 class="w-4 h-4 animate-spin text-blue-400" />
            Verificando software instalado...
          </div>

          <div v-else-if="softwareInstalado" class="p-5 border rounded-lg bg-blue-500/10 border-blue-500/25">
            <div class="flex items-center gap-2 mb-4">
              <div class="flex items-center justify-center w-8 h-8 rounded-full bg-blue-500/20">
                <PackageCheck class="w-4 h-4 text-blue-400" />
              </div>
              <h4 class="font-semibold text-sm text-blue-400">Software Instalado Detectado</h4>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <div class="bg-blue-500/5 rounded-md p-3 border border-blue-500/15">
                <p class="text-[10px] uppercase tracking-wider text-muted-foreground font-medium mb-1">Software</p>
                <p class="text-sm font-semibold text-foreground">{{ softwareInstalado.nomeSoftware ?? `#${softwareInstalado.codigoSoftware}` }}</p>
              </div>
              <div class="bg-blue-500/5 rounded-md p-3 border border-blue-500/15">
                <p class="text-[10px] uppercase tracking-wider text-muted-foreground font-medium mb-1">Versão Instalada</p>
                <p class="text-sm font-semibold text-foreground">{{ softwareInstalado.versaoInstalada }}</p>
              </div>
              <div class="bg-blue-500/5 rounded-md p-3 border border-blue-500/15">
                <p class="text-[10px] uppercase tracking-wider text-muted-foreground font-medium mb-1">Código</p>
                <p class="text-sm font-semibold text-foreground font-mono">#{{ softwareInstalado.codigo }}</p>
              </div>
            </div>
            <p class="text-xs text-blue-400/70 mt-3 flex items-center gap-1.5">
              <span class="w-1.5 h-1.5 rounded-full bg-blue-400/70 inline-block"></span>
              Este software será vinculado automaticamente à ordem de serviço.
            </p>
          </div>

          <div v-else class="p-5 border border-dashed border-border rounded-lg bg-muted/5">
            <div class="flex items-center gap-3">
              <div class="flex items-center justify-center w-8 h-8 rounded-full bg-muted/30">
                <PackageX class="w-4 h-4 text-muted-foreground/60" />
              </div>
              <div>
                <p class="text-sm font-medium text-foreground/70">Sem software instalado</p>
                <p class="text-xs text-muted-foreground">Nenhum software encontrado para esta máquina.</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- STEP 3: Técnico e Agendamento -->
    <div v-show="step === 3" class="space-y-5">

      <!-- Seção de Data/Período -->
      <div class="space-y-3">
        <div class="flex items-center gap-2">
          <label class="text-sm font-semibold text-foreground">Agendamento</label>
          <!-- Toggle modo -->
          <div class="flex items-center gap-0.5 p-0.5 rounded-md bg-muted/40 border border-border ml-auto">
            <button
              type="button"
              :class="['px-3 py-1 rounded text-xs font-semibold transition-all', modoAgendamento === 'data' ? 'bg-sidebar-primary text-white shadow-sm' : 'text-muted-foreground hover:text-foreground']"
              @click="modoAgendamento = 'data'"
            >
              Data específica
            </button>
            <button
              type="button"
              :class="['px-3 py-1 rounded text-xs font-semibold transition-all', modoAgendamento === 'periodo' ? 'bg-sidebar-primary text-white shadow-sm' : 'text-muted-foreground hover:text-foreground']"
              @click="modoAgendamento = 'periodo'"
            >
              Ver período
            </button>
          </div>
        </div>

        <!-- Data específica -->
        <div v-if="modoAgendamento === 'data'" class="space-y-3">
          <FormField v-slot="{ value, handleChange }" name="dataAgendamento">
            <FormItem>
              <FormLabel class="flex items-center gap-1 text-sm font-medium text-foreground/80">
                Data de Agendamento <span class="text-red-500 font-bold">*</span>
              </FormLabel>
              <FormControl>
                <DatePickerInput :model-value="value" @update:model-value="handleChange" :min-value="amanhaToCal" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <!-- Horário -->
          <div>
            <label class="flex items-center gap-1 text-sm font-medium text-foreground/80 mb-1.5">
              Horário <span class="text-xs text-muted-foreground font-normal">(opcional)</span>
            </label>
            <div class="flex items-center gap-2">
              <select
                v-model="horaHH"
                class="h-9 w-20 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors"
              >
                <option value="">--</option>
                <option v-for="h in 24" :key="h - 1" :value="String(h - 1).padStart(2, '0')">
                  {{ String(h - 1).padStart(2, '0') }}
                </option>
              </select>
              <span class="font-bold text-muted-foreground text-lg leading-none">:</span>
              <select
                v-model="horaMM"
                :disabled="horaHH === ''"
                class="h-9 w-20 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors disabled:opacity-40"
              >
                <option value="00">00</option>
                <option value="15">15</option>
                <option value="30">30</option>
                <option value="45">45</option>
              </select>
              <span class="text-xs text-muted-foreground">h</span>
              <button
                v-if="horaHH !== ''"
                type="button"
                class="ml-auto text-xs text-muted-foreground/60 hover:text-red-400 transition-colors"
                @click="limparHora"
              >
                Limpar
              </button>
            </div>
          </div>

          <!-- Previsão de Manutenção -->
          <div>
            <label class="flex items-center gap-1 text-sm font-medium text-foreground/80 mb-1.5">
              Previsão de Manutenção <span class="text-xs text-muted-foreground font-normal">(opcional)</span>
            </label>
            <div class="flex items-center gap-2">
              <select
                v-model="previsaoHoras"
                class="h-9 w-24 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors"
              >
                <option v-for="h in 13" :key="h - 1" :value="h - 1">{{ h - 1 }}h</option>
              </select>
              <select
                v-model="previsaoMinutos"
                class="h-9 w-24 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors"
              >
                <option :value="0">00 min</option>
                <option :value="15">15 min</option>
                <option :value="30">30 min</option>
                <option :value="45">45 min</option>
              </select>
              <span v-if="previsaoTotalMinutos > 0" class="text-xs text-muted-foreground">
                = {{ previsaoTotalMinutos }} min
              </span>
            </div>
          </div>
        </div>

        <!-- Período -->
        <div v-else class="grid grid-cols-2 gap-3">
          <div>
            <label class="flex items-center gap-1 text-sm font-medium text-foreground/80 mb-1.5">
              Início <span class="text-red-500 font-bold">*</span>
            </label>
            <DatePickerInput :model-value="periodoInicio" @update:model-value="onPeriodoInicioChange" :min-value="amanhaToCal" />
          </div>
          <div>
            <label class="flex items-center gap-1 text-sm font-medium text-foreground/80 mb-1.5">
              Fim <span class="text-red-500 font-bold">*</span>
            </label>
            <DatePickerInput :model-value="periodoFim" @update:model-value="v => periodoFim = v" :min-value="amanhaToCal" />
          </div>
          <!-- Chips de seleção do dia de agendamento -->
          <div v-if="periodoFim && periodoInicio" class="col-span-2 space-y-2">
            <p class="text-xs font-medium text-foreground/80">
              Selecione o dia de agendamento: <span class="text-red-500 font-bold">*</span>
            </p>
            <div class="flex flex-wrap gap-1.5">
              <button
                v-for="dia in diasDoPeriodo(periodoInicio, periodoFim)"
                :key="dia"
                type="button"
                :class="[
                  'px-3 py-1.5 rounded-lg text-sm font-bold border transition-all',
                  form.values.dataAgendamento === dia
                    ? 'bg-sidebar-primary text-white border-sidebar-primary shadow-md shadow-blue-900/20'
                    : 'bg-muted/20 text-foreground border-border hover:border-blue-500/50 hover:bg-muted/30',
                ]"
                @click="form.setFieldValue('dataAgendamento', dia)"
              >
                {{ formatDayChip(dia) }}
              </button>
            </div>
            <p v-if="!form.values.dataAgendamento" class="text-xs text-amber-400 flex items-center gap-1">
              <span class="w-1.5 h-1.5 rounded-full bg-amber-400 inline-block shrink-0" />
              Selecione um dia para definir o agendamento
            </p>
          </div>
          <FormField name="dataAgendamento"><FormItem><FormMessage /></FormItem></FormField>

          <!-- Horário (período) -->
          <div v-if="form.values.dataAgendamento" class="col-span-2">
            <label class="flex items-center gap-1 text-sm font-medium text-foreground/80 mb-1.5">
              Horário <span class="text-xs text-muted-foreground font-normal">(opcional)</span>
            </label>
            <div class="flex items-center gap-2">
              <select
                v-model="horaHH"
                class="h-9 w-20 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors"
              >
                <option value="">--</option>
                <option v-for="h in 24" :key="h - 1" :value="String(h - 1).padStart(2, '0')">
                  {{ String(h - 1).padStart(2, '0') }}
                </option>
              </select>
              <span class="font-bold text-muted-foreground text-lg leading-none">:</span>
              <select
                v-model="horaMM"
                :disabled="horaHH === ''"
                class="h-9 w-20 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors disabled:opacity-40"
              >
                <option value="00">00</option>
                <option value="15">15</option>
                <option value="30">30</option>
                <option value="45">45</option>
              </select>
              <span class="text-xs text-muted-foreground">h</span>
              <button
                v-if="horaHH !== ''"
                type="button"
                class="ml-auto text-xs text-muted-foreground/60 hover:text-red-400 transition-colors"
                @click="limparHora"
              >
                Limpar
              </button>
            </div>
          </div>

          <!-- Previsão de Manutenção (período) -->
          <div v-if="form.values.dataAgendamento" class="col-span-2">
            <label class="flex items-center gap-1 text-sm font-medium text-foreground/80 mb-1.5">
              Previsão de Manutenção <span class="text-xs text-muted-foreground font-normal">(opcional)</span>
            </label>
            <div class="flex items-center gap-2">
              <select
                v-model="previsaoHoras"
                class="h-9 w-24 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors"
              >
                <option v-for="h in 13" :key="h - 1" :value="h - 1">{{ h - 1 }}h</option>
              </select>
              <select
                v-model="previsaoMinutos"
                class="h-9 w-24 rounded-md border border-border bg-muted/20 px-2 text-sm text-foreground hover:border-blue-500/50 focus:border-blue-500 focus:outline-none transition-colors"
              >
                <option :value="0">00 min</option>
                <option :value="15">15 min</option>
                <option :value="30">30 min</option>
                <option :value="45">45 min</option>
              </select>
              <span v-if="previsaoTotalMinutos > 0" class="text-xs text-muted-foreground">
                = {{ previsaoTotalMinutos }} min
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Lista de técnicos -->
      <div class="space-y-3">
        <div class="flex items-center justify-between">
          <label class="text-sm font-semibold text-foreground">
            Técnicos <span class="text-red-500 font-bold">*</span>
          </label>
          <span v-if="tecnicosSelecionados.size > 0" class="text-xs text-muted-foreground">
            {{ tecnicosSelecionados.size }} selecionado(s)
            <span v-if="tecnicoResponsavelId" class="text-blue-400"> · 1 responsável</span>
          </span>
        </div>

        <FormField name="codigoFuncionario"><FormItem><FormMessage /></FormItem></FormField>

        <div v-if="loadingTecnicos" class="flex items-center gap-3 text-sm text-muted-foreground p-4 border border-border rounded-lg bg-muted/10">
          <Loader2 class="w-4 h-4 animate-spin text-blue-400" />
          Carregando técnicos...
        </div>

        <div v-else-if="tecnicosDisponiveis.length === 0" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/5">
          <UserX class="w-8 h-8 mx-auto text-muted-foreground/40 mb-3" />
          <p class="text-sm font-medium text-foreground/70">Nenhum técnico disponível</p>
        </div>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div
            v-for="tec in tecnicosDisponiveis"
            :key="tec.id"
            :class="[
              'border rounded-lg p-4 transition-all select-none',
              // Indisponível no dia selecionado → bloqueado
              !tecnicoDisponivelNoDia(tec.id)
                ? 'opacity-55 cursor-not-allowed border-red-500/30 bg-red-500/5'
                : tecnicoResponsavelId === tec.id
                  ? 'cursor-pointer border-blue-600 bg-blue-500/15 ring-2 ring-blue-500/40'
                  : tecnicosSelecionados.has(tec.id)
                    ? 'cursor-pointer border-blue-500/40 bg-blue-500/8 ring-1 ring-blue-500/20'
                    : 'cursor-pointer border-border bg-muted/10 hover:border-blue-500/30 hover:bg-muted/20',
            ]"
            @click="tecnicoDisponivelNoDia(tec.id) ? toggleTecnico(tec.id) : undefined"
          >
            <!-- Cabeçalho do card -->
            <div class="flex items-start gap-3">
              <!-- Avatar -->
              <div
                :class="[
                  'flex items-center justify-center w-10 h-10 rounded-full font-bold text-sm shrink-0 transition-all',
                  tecnicosSelecionados.has(tec.id) ? 'bg-blue-500/30 text-blue-300' : 'bg-muted/40 text-foreground/70',
                ]"
              >
                {{ getInitials(tec.nome) }}
              </div>
              <!-- Info -->
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-sm text-foreground truncate">{{ tec.nome }}</p>
                <p class="text-xs text-muted-foreground truncate">{{ tec.cargo || 'Técnico' }}</p>
                <p class="text-xs text-muted-foreground/60 truncate">{{ tec.email }}</p>
              </div>
              <!-- Botão responsável (só aparece quando selecionado) -->
              <button
                v-if="tecnicosSelecionados.has(tec.id)"
                type="button"
                :title="tecnicoResponsavelId === tec.id ? 'Responsável' : 'Definir como responsável'"
                :class="[
                  'shrink-0 p-1.5 rounded-md transition-all',
                  tecnicoResponsavelId === tec.id
                    ? 'text-yellow-400 bg-yellow-400/10'
                    : 'text-muted-foreground/50 hover:text-yellow-400 hover:bg-yellow-400/10',
                ]"
                @click.stop="setResponsavel(tec.id)"
              >
                <Star class="w-4 h-4" :fill="tecnicoResponsavelId === tec.id ? 'currentColor' : 'none'" />
              </button>
              <!-- Badge responsável -->
              <span
                v-if="tecnicoResponsavelId === tec.id"
                class="text-[9px] font-bold uppercase tracking-wider bg-yellow-400/15 text-yellow-400 border border-yellow-400/30 px-1.5 py-0.5 rounded-full shrink-0"
              >
                Resp.
              </span>
              <!-- Badge indisponível -->
              <span
                v-if="!tecnicoDisponivelNoDia(tec.id)"
                class="text-[9px] font-bold uppercase tracking-wider bg-red-500/15 text-red-400 border border-red-500/30 px-1.5 py-0.5 rounded-full shrink-0"
              >
                Indisponível
              </span>
            </div>

            <!-- Seção de disponibilidade -->
            <div v-if="form.values.dataAgendamento" class="mt-3 pt-3 border-t border-border/60">
              <!-- Carregando -->
              <template v-if="loadingAgenda">
                <div class="animate-pulse flex gap-1.5">
                  <div class="h-3 bg-muted/40 rounded w-1/2" />
                </div>
              </template>

              <!-- Modo data única -->
              <template v-else-if="modoAgendamento === 'data'">
                <!-- Disponível neste dia -->
                <div v-if="estaNoDia(tec.id, form.values.dataAgendamento)">
                  <div class="flex items-center gap-2">
                    <div class="w-2 h-2 rounded-full bg-blue-500 shrink-0" />
                    <span class="text-xs font-semibold text-blue-400">Disponível neste dia</span>
                  </div>
                  <!-- Info de ocupações existentes mas que não conflitam -->
                  <div
                    v-for="ocup in ocupacoesParaTecnicoNoDia(tec.id, form.values.dataAgendamento)"
                    :key="ocup.inicio"
                    class="flex items-center gap-1.5 mt-1"
                  >
                    <div class="w-1.5 h-1.5 rounded-full bg-muted-foreground/40 shrink-0" />
                    <span class="text-xs text-muted-foreground/70">
                      Ocupado das {{ ocup.inicio }}<template v-if="ocup.fim"> às {{ ocup.fim }}</template>
                    </span>
                  </div>
                </div>

                <!-- Ocupado mas tem horário livre no mesmo dia -->
                <template v-else-if="horaLivreParaTecnicoNoDia(tec.id, form.values.dataAgendamento)">
                  <!-- Janelas de ocupação -->
                  <div
                    v-for="ocup in ocupacoesParaTecnicoNoDia(tec.id, form.values.dataAgendamento)"
                    :key="ocup.inicio"
                    class="flex items-center gap-1.5 mb-1"
                  >
                    <div class="w-1.5 h-1.5 rounded-full bg-red-400/80 shrink-0" />
                    <span class="text-xs text-red-300/80">
                      Ocupado das {{ ocup.inicio }}<template v-if="ocup.fim"> às {{ ocup.fim }}</template>
                    </span>
                  </div>
                  <!-- Horário livre (com buffer) -->
                  <div class="flex items-center gap-2 mt-0.5">
                    <div class="w-2 h-2 rounded-full bg-amber-500 shrink-0" />
                    <span class="text-xs font-semibold text-amber-400">
                      Disponível neste dia a partir das {{ horaLivreParaTecnicoNoDia(tec.id, form.values.dataAgendamento) }}
                    </span>
                  </div>
                  <div
                    v-if="!horaAgendamento || horaAgendamento < (horaLivreParaTecnicoNoDia(tec.id, form.values.dataAgendamento) ?? '')"
                    class="mt-1 text-xs text-muted-foreground"
                  >
                    Selecione um horário a partir das {{ horaLivreParaTecnicoNoDia(tec.id, form.values.dataAgendamento) }} para este técnico
                  </div>
                </template>

                <!-- Sem horário livre no dia (dia totalmente bloqueado) -->
                <template v-else>
                  <div class="flex items-center gap-2">
                    <div class="w-2 h-2 rounded-full bg-red-500 shrink-0" />
                    <span class="text-xs font-semibold text-red-400">Ocupado neste dia</span>
                  </div>
                  <div
                    v-if="proximoDiaDisponivel.get(tec.id)"
                    class="mt-1.5 flex items-center gap-1.5 text-xs text-amber-400"
                  >
                    <CalendarClock class="w-3 h-3 shrink-0" />
                    Próximo disponível: <span class="font-semibold ml-0.5">{{ formatDayChip(proximoDiaDisponivel.get(tec.id)!) }}</span>
                  </div>
                </template>
              </template>

              <!-- Modo período — sempre mostra TODOS os dias do período -->
              <template v-else-if="periodoFim && periodoInicio">
                <div class="flex flex-wrap gap-1 mb-1.5">
                  <div
                    v-for="dia in diasDoPeriodo(periodoInicio, periodoFim)"
                    :key="dia"
                    :class="[
                      'text-[10px] font-bold px-1.5 py-0.5 rounded border transition-all',
                      // Dia selecionado como agendamento — destaca com sólido
                      dia === form.values.dataAgendamento
                        ? estaNoDia(tec.id, dia)
                          ? 'bg-blue-500 text-white border-blue-600 ring-1 ring-blue-400/60'
                          : 'bg-red-500 text-white border-red-600 ring-1 ring-red-400/60'
                        : estaNoDia(tec.id, dia)
                          ? 'bg-blue-500/20 text-blue-300 border-blue-500/30'
                          : 'bg-red-500/20 text-red-300 border-red-500/30',
                    ]"
                    :title="dia"
                  >
                    {{ formatDayChip(dia) }}
                  </div>
                </div>
                <p class="text-xs text-muted-foreground">
                  Disponível em
                  <span class="font-semibold text-foreground">{{ diasDisponiveis(tec.id) }}</span>
                  de {{ diasDoPeriodo(periodoInicio, periodoFim).length }} dias
                </p>
                <!-- Info do dia selecionado quando ocupado -->
                <template v-if="form.values.dataAgendamento && !estaNoDia(tec.id, form.values.dataAgendamento)">
                  <!-- Janelas de ocupação no dia selecionado -->
                  <div
                    v-for="ocup in ocupacoesParaTecnicoNoDia(tec.id, form.values.dataAgendamento)"
                    :key="ocup.inicio"
                    class="flex items-center gap-1.5 mt-1"
                  >
                    <div class="w-1.5 h-1.5 rounded-full bg-red-400/80 shrink-0" />
                    <span class="text-xs text-red-300/80">
                      Ocupado das {{ ocup.inicio }}<template v-if="ocup.fim"> às {{ ocup.fim }}</template>
                    </span>
                  </div>
                  <!-- Disponível mais tarde no mesmo dia -->
                  <div
                    v-if="horaLivreParaTecnicoNoDia(tec.id, form.values.dataAgendamento)"
                    class="flex items-center gap-1.5 mt-1"
                  >
                    <div class="w-1.5 h-1.5 rounded-full bg-amber-400/80 shrink-0" />
                    <span class="text-xs font-semibold text-amber-400">
                      Disponível neste dia a partir das {{ horaLivreParaTecnicoNoDia(tec.id, form.values.dataAgendamento) }}
                    </span>
                  </div>
                  <!-- Próximo dia livre (quando dia completamente bloqueado sem horário) -->
                  <div
                    v-else-if="proximoDiaDisponivel.get(tec.id)"
                    class="flex items-center gap-1.5 mt-1 text-xs text-amber-400"
                  >
                    <CalendarClock class="w-3 h-3 shrink-0" />
                    Próximo disponível: <span class="font-semibold ml-0.5">{{ formatDayChip(proximoDiaDisponivel.get(tec.id)!) }}</span>
                  </div>
                </template>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- STEP 4: Checklist de Ativos (creation only) -->
    <div v-show="step === 4" class="space-y-4">
      <p class="text-sm text-muted-foreground">
        Selecione pelo menos um ativo que será levado nesta ordem de serviço.
      </p>
      <div v-if="checklistAtivosError" class="flex items-center gap-2 mt-1 text-sm font-medium bg-red-500/10 text-red-500 px-3 py-2 rounded-md border border-red-500/20">
        <AlertTriangle class="w-4 h-4 shrink-0" />
        <p>{{ checklistAtivosError }}</p>
      </div>

      <div v-if="loadingAtivos" class="flex items-center gap-3 text-sm text-muted-foreground p-4 border border-border rounded-lg bg-muted/10">
        <Loader2 class="w-4 h-4 animate-spin text-blue-400" />
        Carregando ativos disponíveis...
      </div>

      <template v-else>
        <!-- Added items list -->
        <div v-if="checklistItems.length > 0" class="space-y-2">
          <div
            v-for="(item, idx) in checklistItems"
            :key="idx"
            class="flex items-center justify-between gap-3 p-3 border border-border rounded-lg bg-muted/10"
          >
            <div class="min-w-0 flex-1">
              <p class="text-sm font-medium text-foreground truncate">
                {{ item.descricaoProduto ?? `Ativo #${item.codigoAtivo}` }}
                <span v-if="item.numeroSerie" class="text-xs text-muted-foreground ml-1">(S/N: {{ item.numeroSerie }})</span>
              </p>
              <p v-if="item.descricaoAtivo" class="text-xs text-muted-foreground truncate mt-0.5">{{ item.descricaoAtivo }}</p>
            </div>
            <Button
              type="button"
              variant="ghost"
              size="icon"
              class="h-8 w-8 text-muted-foreground hover:text-red-400 shrink-0"
              @click="removerItem(idx)"
            >
              <Trash2 class="w-4 h-4" />
            </Button>
          </div>
        </div>

        <div v-else-if="!showAddItemForm" class="text-center p-8 border border-dashed border-border rounded-lg bg-muted/5">
          <ListChecks class="w-8 h-8 mx-auto text-muted-foreground/40 mb-3" />
          <p class="text-sm font-medium text-foreground/70">Nenhum ativo adicionado</p>
          <p class="text-xs text-muted-foreground mt-1">Adicione ativos que serão levados nesta ordem ou pule esta etapa.</p>
        </div>

        <!-- Add item inline form -->
        <div v-if="showAddItemForm" class="p-4 border border-blue-500/30 rounded-lg bg-blue-500/5 space-y-4">
          <h4 class="text-sm font-semibold text-blue-400">Adicionar Ativo ao Checklist</h4>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground/80">
              Ativo <span class="text-red-500 font-bold">*</span>
            </label>

            <!-- Ativo selecionado -->
            <div
              v-if="ativoSelecionadoObj"
              class="flex items-center gap-2 px-3 py-2 rounded-md bg-blue-500/10 border border-blue-500/30 text-sm"
            >
              <CheckCircle2 class="w-4 h-4 text-blue-400 shrink-0" />
              <span class="text-foreground font-medium flex-1 truncate">
                {{ ativoSelecionadoObj.descricaoProduto ?? `Ativo #${ativoSelecionadoObj.codigo}` }}
                <span v-if="ativoSelecionadoObj.numeroSerie" class="text-xs text-muted-foreground ml-1">(S/N: {{ ativoSelecionadoObj.numeroSerie }})</span>
              </span>
              <Button
                type="button" variant="ghost" size="icon"
                class="h-5 w-5 shrink-0 text-muted-foreground hover:text-foreground"
                @click="newItemCodigoAtivo = ''; newItemSearchQuery = ''"
              >
                <X class="w-3 h-3" />
              </Button>
            </div>

            <!-- Campo de busca (oculto quando há seleção) -->
            <div v-if="!ativoSelecionadoObj" class="relative">
              <Search class="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground pointer-events-none" />
              <Input
                v-model="newItemSearchQuery"
                placeholder="Buscar por produto, série, marca, modelo..."
                class="pl-9 bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
              />
            </div>

            <!-- Lista filtrada -->
            <div
              v-if="!ativoSelecionadoObj && newItemSearchQuery && ativosFilterados.length > 0"
              class="max-h-[180px] overflow-y-auto rounded-md border border-border bg-sidebar divide-y divide-border"
            >
              <div
                v-for="a in ativosFilterados"
                :key="a.codigo"
                class="flex items-center gap-3 px-3 py-2.5 cursor-pointer hover:bg-muted/30 transition-colors"
                @click="onAtivoSelect(a.codigo.toString())"
              >
                <div class="min-w-0 flex-1">
                  <p class="text-sm font-medium text-foreground truncate">
                    {{ a.descricaoProduto ?? 'Sem produto' }}
                    <span class="text-xs text-muted-foreground ml-1">#{{ a.codigo }}</span>
                  </p>
                  <p class="text-xs text-muted-foreground truncate">
                    <span v-if="a.marca">{{ a.marca }}</span>
                    <span v-if="a.modelo"> — {{ a.modelo }}</span>
                    <span v-if="a.numeroSerie"> · S/N: {{ a.numeroSerie }}</span>
                  </p>
                </div>
                <span
                  v-if="a.tipo"
                  class="text-[10px] font-semibold uppercase tracking-wider px-1.5 py-0.5 rounded bg-muted/30 text-muted-foreground shrink-0"
                >{{ a.tipo }}</span>
              </div>
            </div>

            <!-- Sem resultados -->
            <p
              v-else-if="!ativoSelecionadoObj && newItemSearchQuery && ativosFilterados.length === 0"
              class="text-xs text-muted-foreground px-1"
            >
              Nenhum ativo encontrado para "{{ newItemSearchQuery }}"
            </p>

            <!-- Estado inicial -->
            <p v-else-if="!ativoSelecionadoObj && !newItemSearchQuery" class="text-xs text-muted-foreground px-1">
              Digite acima para buscar ativos disponíveis.
            </p>
          </div>

          <div class="space-y-2">
            <label class="text-sm font-medium text-foreground/80">Descrição do Ativo</label>
            <Input
              v-model="newItemDescricaoAtivo"
              placeholder="Descreva a intervenção ou observação para este ativo..."
              class="bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
            />
          </div>

          <div class="flex items-center gap-2 justify-end">
            <Button type="button" variant="ghost" size="sm" @click="cancelarAddItem">Cancelar</Button>
            <Button
              type="button"
              size="sm"
              class="bg-blue-600 hover:bg-blue-500 text-white"
              :disabled="!newItemCodigoAtivo"
              @click="adicionarItem"
            >
              <Plus class="w-4 h-4 mr-1" /> Adicionar
            </Button>
          </div>
        </div>

        <Button
          v-if="!showAddItemForm"
          type="button"
          variant="outline"
          class="border-border hover:bg-muted/30 w-full"
          @click="showAddItemForm = true"
        >
          <Plus class="w-4 h-4 mr-2" /> Adicionar Ativo
        </Button>
      </template>
    </div>

    <!-- STEP 5: Checklist de Manutenção -->
    <div v-show="step === 5" class="space-y-5">
      <div class="flex items-center gap-3">
        <Wrench class="size-5 text-blue-400" />
        <div>
          <h3 class="text-sm font-semibold text-foreground">Checklist de Manutenção</h3>
          <p class="text-xs text-muted-foreground">Selecione as tarefas que o técnico deverá executar nesta ordem.</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loadingChecklistMaquina" class="flex items-center gap-2 p-4 text-muted-foreground text-sm">
        <Loader2 class="size-4 animate-spin" /> Carregando tarefas da máquina...
      </div>

      <template v-else>
        <!-- Tarefas padrão da máquina -->
        <div v-if="checklistMaquinaItens.length > 0" class="space-y-2">
          <p class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Padrão da máquina</p>
          <div
            v-for="tarefa in checklistMaquinaItens"
            :key="tarefa.codigoTarefa"
            class="flex items-start gap-3 p-3 rounded-lg border cursor-pointer transition-colors"
            :class="selectedTarefas.has(tarefa.codigoTarefa)
              ? 'border-blue-500/40 bg-blue-500/10'
              : 'border-border bg-sidebar hover:bg-muted/20'"
            @click="toggleTarefa(tarefa.codigoTarefa)"
          >
            <div
              class="mt-0.5 size-4 rounded border-2 shrink-0 flex items-center justify-center transition-colors"
              :class="selectedTarefas.has(tarefa.codigoTarefa) ? 'border-blue-500 bg-blue-500' : 'border-muted-foreground/40'"
            >
              <CheckCircle2 v-if="selectedTarefas.has(tarefa.codigoTarefa)" class="size-3 text-white" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-foreground leading-snug">{{ tarefa.descricaoTarefa }}</p>
              <p v-if="tarefa.categoria" class="text-xs text-muted-foreground mt-0.5">{{ tarefa.categoria }}</p>
            </div>
          </div>
          <p class="text-xs text-muted-foreground pt-0.5">
            {{ selectedTarefas.size }} de {{ checklistMaquinaItens.length }} tarefas padrão selecionadas
          </p>
        </div>
        <div v-else class="text-center p-6 border border-dashed border-border rounded-lg bg-muted/5">
          <Wrench class="w-7 h-7 mx-auto text-muted-foreground/40 mb-2" />
          <p class="text-sm text-muted-foreground">Esta máquina não possui checklist padrão.</p>
        </div>

        <!-- Tarefas extras já adicionadas -->
        <div v-if="tarefasExtrasAdicionadas.length > 0" class="space-y-2">
          <p class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Tarefas extras</p>
          <div
            v-for="tarefa in tarefasExtrasAdicionadas"
            :key="tarefa.id"
            class="flex items-center justify-between gap-3 p-3 rounded-lg border border-emerald-500/30 bg-emerald-500/5"
          >
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-foreground">{{ tarefa.descricao }}</p>
              <p v-if="tarefa.categoria" class="text-xs text-muted-foreground mt-0.5">{{ tarefa.categoria }}</p>
            </div>
            <button type="button" class="text-muted-foreground hover:text-red-400 shrink-0" @click="removerTarefaExtra(tarefa.id)">
              <X class="w-4 h-4" />
            </button>
          </div>
        </div>

        <!-- Buscar e adicionar tarefas extras -->
        <div class="border-t border-border pt-4 space-y-3">
          <p class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Adicionar mais tarefas</p>
          <div class="relative">
            <Search class="absolute left-3 top-2.5 h-4 w-4 text-muted-foreground pointer-events-none" />
            <Input
              v-model="filtroTarefaExtra"
              placeholder="Buscar no catálogo de tarefas..."
              class="pl-9 bg-muted/20 border-border hover:border-blue-500/50 transition-colors"
            />
          </div>
          <div
            v-if="filtroTarefaExtra && tarefasParaAdicionar.length > 0"
            class="max-h-[160px] overflow-y-auto rounded-md border border-border bg-sidebar divide-y divide-border"
          >
            <div
              v-for="tarefa in tarefasParaAdicionar"
              :key="tarefa.id"
              class="flex items-center justify-between px-3 py-2.5 cursor-pointer hover:bg-muted/30 transition-colors"
              @click="adicionarTarefaExtra(tarefa)"
            >
              <div class="min-w-0 flex-1">
                <p class="text-sm font-medium text-foreground truncate">{{ tarefa.descricao }}</p>
                <p v-if="tarefa.categoria" class="text-xs text-muted-foreground">{{ tarefa.categoria }}</p>
              </div>
              <Plus class="w-4 h-4 text-blue-400 shrink-0 ml-3" />
            </div>
          </div>
          <p v-else-if="filtroTarefaExtra && tarefasParaAdicionar.length === 0" class="text-xs text-muted-foreground px-1">
            Nenhuma tarefa encontrada para "{{ filtroTarefaExtra }}"
          </p>
        </div>

        <!-- Criar nova tarefa -->
        <div class="border-t border-border pt-3">
          <div v-if="!showCriarTarefa">
            <Button
              type="button"
              variant="outline"
              class="w-full border-dashed border-border hover:border-blue-500/50 hover:bg-blue-500/5 text-muted-foreground hover:text-blue-400"
              @click="showCriarTarefa = true"
            >
              <Plus class="w-4 h-4 mr-2" /> Criar nova tarefa
            </Button>
          </div>
          <div v-else class="p-4 rounded-lg border border-blue-500/30 bg-blue-500/5 space-y-3">
            <div class="flex items-center justify-between">
              <h4 class="text-sm font-semibold text-blue-400">Nova Tarefa</h4>
              <button type="button" class="text-muted-foreground hover:text-foreground" @click="showCriarTarefa = false; novaTarefaDescricao = ''; novaTarefaCategoria = ''">
                <X class="w-4 h-4" />
              </button>
            </div>
            <div class="space-y-2">
              <label class="text-sm font-medium text-foreground/80">Descrição <span class="text-red-500 font-bold">*</span></label>
              <Input v-model="novaTarefaDescricao" placeholder="Ex: Verificar tensão elétrica" class="bg-muted/20" />
            </div>
            <div class="space-y-2">
              <label class="text-sm font-medium text-foreground/80">Categoria</label>
              <Input v-model="novaTarefaCategoria" placeholder="Ex: Elétrica, Mecânica, Software..." class="bg-muted/20" />
            </div>
            <div class="flex justify-end gap-2 pt-1">
              <Button type="button" variant="ghost" size="sm" @click="showCriarTarefa = false; novaTarefaDescricao = ''; novaTarefaCategoria = ''">Cancelar</Button>
              <Button
                type="button"
                size="sm"
                class="bg-blue-600 hover:bg-blue-500 text-white"
                :disabled="!novaTarefaDescricao.trim() || criandoTarefa"
                @click="criarEAdicionarTarefa"
              >
                <Loader2 v-if="criandoTarefa" class="w-3 h-3 mr-1.5 animate-spin" />
                Criar e Adicionar
              </Button>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- Footer Buttons -->
    <div class="flex items-center justify-end border-t border-border mt-10 pt-6">
      <div class="flex gap-3">
        <Button v-if="step > 1" type="button" variant="outline" class="border-border hover:bg-muted/30" @click="step--">
          Voltar
        </Button>

        <Button
          v-if="step < maxStep"
          type="button"
          class="bg-blue-600 hover:bg-blue-500 text-white px-8 shadow-md shadow-blue-900/20"
          @click="nextStep"
        >
          Próximo <ArrowRight class="w-4 h-4 ml-2" />
        </Button>

        <Button
          v-if="step === maxStep"
          type="submit"
          class="bg-emerald-600 hover:bg-emerald-500 text-white px-8 shadow-md shadow-emerald-900/20"
          :disabled="loading"
        >
          <Loader2 v-if="loading" class="w-4 h-4 mr-2 animate-spin" />
          {{ loading ? 'Salvando...' : isEditMode ? 'Salvar Alterações' : 'Abrir Ordem de Serviço' }}
        </Button>
      </div>
    </div>

  </form>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { CalendarDate } from '@internationalized/date'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { DatePickerInput } from '@/components/ui/date-picker'
import {
  ChevronRight, FileText, Server, UserCheck, ArrowRight,
  Loader2, PackageCheck, PackageX, UserX, CheckCircle2,
  ListChecks, Plus, Trash2, Search, X, Wrench, AlertTriangle, Star, CalendarClock,
} from 'lucide-vue-next'

import { clienteService } from '@/services/clienteService'
import { maquinaSoftwareInstaladoService } from '@/services/maquinaSoftwareInstaladoService'
import { tecnicoService, type TecnicoResponseDTO } from '@/services/tecnicoService'
import { ordemServicoService, type OrdemServicoResponseDTO } from '@/services/ordemServicoService'
import type { TecnicoAgendaItem } from '@/services/ordemServicoService'
import { ativoService, type AtivoResponseDTO } from '@/services/ativoService'
import {
  catalogoMaquinaService,
  type CatalogoMaquinaChecklistPadraoResponseDTO,
} from '@/services/catalogoMaquinaService'
import {
  catalogoTarefaService,
  type CatalogoTarefaResponseDTO,
} from '@/services/catalogoTarefaService'

interface ChecklistItemLocal {
  codigoAtivo: number
  descricaoAtivo: string
  descricaoProduto?: string
  numeroSerie?: string
}

const props = defineProps<{
  initialData?: OrdemServicoResponseDTO | null
}>()

const isEditMode = computed(() => !!props.initialData)
const maxStep = computed(() => 5)

const emit = defineEmits(['fechar', 'success'])

const step = ref(1)
const stepsList = [
  { id: 1, name: 'Identificação', icon: FileText },
  { id: 2, name: 'Máquina', icon: Server },
  { id: 3, name: 'Agendamento', icon: UserCheck },
  { id: 4, name: 'Ativos', icon: ListChecks },
  { id: 5, name: 'Manutenção', icon: Wrench },
]
const loading = ref(false)
const loadingSoftware = ref(false)
const loadingTecnicos = ref(false)
const loadingAtivos = ref(false)
const checklistAtivosError = ref('')

// ─── Seleção múltipla de técnicos ─────────────────────────────────────────────
const tecnicosSelecionados = ref(new Set<number>())
const tecnicoResponsavelId  = ref<number | null>(null)

function toggleTecnico(id: number) {
  const s = new Set(tecnicosSelecionados.value)
  if (s.has(id)) {
    s.delete(id)
    if (tecnicoResponsavelId.value === id) tecnicoResponsavelId.value = null
  } else {
    s.add(id)
    if (!tecnicoResponsavelId.value) tecnicoResponsavelId.value = id
  }
  tecnicosSelecionados.value = s
  form.setFieldValue('codigoFuncionario', tecnicoResponsavelId.value?.toString() ?? '')
}

function setResponsavel(id: number) {
  const s = new Set(tecnicosSelecionados.value)
  s.add(id)
  tecnicosSelecionados.value = s
  tecnicoResponsavelId.value = id
  form.setFieldValue('codigoFuncionario', id.toString())
}

// ─── Modo de agendamento: data específica ou período ─────────────────────────
const modoAgendamento = ref<'data' | 'periodo'>('data')
const periodoInicio   = ref<string>('')
const periodoFim      = ref<string>('')

// ─── Horário e previsão de manutenção ────────────────────────────────────────
const horaHH = ref<string>('')
const horaMM = ref<string>('00')
const horaAgendamento = computed<string>(() =>
  horaHH.value !== '' ? `${horaHH.value}:${horaMM.value}` : ''
)
function limparHora() {
  horaHH.value = ''
  horaMM.value = '00'
}

const previsaoHoras   = ref<number>(0)
const previsaoMinutos = ref<number>(0)
const previsaoTotalMinutos = computed<number>(() => previsaoHoras.value * 60 + previsaoMinutos.value)

function onPeriodoInicioChange(v: string) {
  periodoInicio.value = v
  form.setFieldValue('dataAgendamento', v)
}

// ─── Disponibilidade de técnicos ──────────────────────────────────────────────
const diasOcupados         = ref(new Map<number, Set<string>>())
// Map<techId, Map<YYYY-MM-DD, 'HH:MM'>> — horário a partir do qual o técnico fica livre naquele dia
const horaLivreNoDia       = ref(new Map<number, Map<string, string>>())
// Map<techId, Map<YYYY-MM-DD, {inicio, fim}[]>> — janelas de ocupação (sem buffer)
const ocupacaoNoDia        = ref(new Map<number, Map<string, Array<{inicio: string, fim: string}>>>())
const proximoDiaDisponivel = ref(new Map<number, string | null>())
const loadingAgenda        = ref(false)

function horaLivreParaTecnicoNoDia(tecId: number, dia: string): string | null {
  return horaLivreNoDia.value.get(tecId)?.get(dia) ?? null
}

function ocupacoesParaTecnicoNoDia(tecId: number, dia: string): Array<{inicio: string, fim: string}> {
  return ocupacaoNoDia.value.get(tecId)?.get(dia) ?? []
}

function estaNoDia(tecId: number, dia: string): boolean {
  const diaOcupado = diasOcupados.value.get(tecId)?.has(dia) ?? false
  if (!diaOcupado) return true

  const ocupacoes = ocupacoesParaTecnicoNoDia(tecId, dia)

  // Ordem sem horário → bloqueia o dia inteiro
  if (ocupacoes.length === 0) return false

  // Tem ordens com horário mas nenhum horário selecionado → indisponível
  if (!horaAgendamento.value) return false

  // Mesma lógica de sobreposição de janelas do backend: [A, A+durA+2h) vs [B, B+durB+2h)
  const novaParts = horaAgendamento.value.split(':')
  const novaInicioMin = Number(novaParts[0] ?? 0) * 60 + Number(novaParts[1] ?? 0)
  const novaFimMin = novaInicioMin + previsaoTotalMinutos.value + 120

  for (const ocup of ocupacoes) {
    const oParts = ocup.inicio.split(':')
    const oInicioMin = Number(oParts[0] ?? 0) * 60 + Number(oParts[1] ?? 0)
    let oFimMin: number
    if (ocup.fim) {
      const fParts = ocup.fim.split(':')
      oFimMin = Number(fParts[0] ?? 0) * 60 + Number(fParts[1] ?? 0) + 120
    } else {
      oFimMin = oInicioMin + 120
    }
    if (novaInicioMin < oFimMin && oInicioMin < novaFimMin) return false
  }

  return true
}

/** Retorna false quando o técnico está ocupado no dia de agendamento selecionado */
function tecnicoDisponivelNoDia(tecId: number): boolean {
  const dia = form.values.dataAgendamento
  if (!dia || !diasOcupados.value.has(tecId)) return true
  return estaNoDia(tecId, dia)
}

function diasDoPeriodo(inicio: string, fim: string): string[] {
  const dias: string[] = []
  const d = new Date(inicio + 'T00:00:00')
  const f = new Date(fim + 'T00:00:00')
  while (d <= f) {
    dias.push(d.toISOString().slice(0, 10))
    d.setDate(d.getDate() + 1)
  }
  return dias
}

function diasDisponiveis(tecId: number): number {
  // Sempre usa os extremos reais do período, nunca o chip selecionado
  const inicio = modoAgendamento.value === 'periodo' ? periodoInicio.value : form.values.dataAgendamento
  const fim    = modoAgendamento.value === 'periodo' ? periodoFim.value    : form.values.dataAgendamento
  if (!inicio || !fim) return 0
  const todosDias = diasDoPeriodo(inicio, fim)
  const ocupados  = [...(diasOcupados.value.get(tecId) ?? new Set())]
    .filter(d => todosDias.includes(d)).length
  return Math.max(0, todosDias.length - ocupados)
}

function formatDayChip(dia: string): string {
  const partes = dia.split('-')
  const m = partes[1] ?? ''
  const d = partes[2] ?? ''
  const nomes = ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez']
  return `${d}/${nomes[Number(m) - 1] ?? m}`
}

async function carregarAgenda() {
  // No modo período usa sempre o início do período — não o chip selecionado
  const inicio = modoAgendamento.value === 'periodo'
    ? periodoInicio.value
    : form.values.dataAgendamento
  if (!inicio || tecnicosDisponiveis.value.length === 0) return

  // Sempre busca 30 dias para calcular "próximo disponível"
  const d30 = new Date(inicio + 'T00:00:00')
  d30.setDate(d30.getDate() + 30)
  const fim30 = d30.toISOString().slice(0, 10)

  loadingAgenda.value = true
  const novoMapa      = new Map<number, Set<string>>()
  const novoMapaHoras = new Map<number, Map<string, string>>()
  const novoOcupacao  = new Map<number, Map<string, Array<{inicio: string, fim: string}>>>()
  const novoProximo   = new Map<number, string | null>()

  await Promise.all(
    tecnicosDisponiveis.value.map(async (t) => {
      try {
        const agenda = await ordemServicoService.buscarAgenda(inicio, fim30, t.id)
        const item: TecnicoAgendaItem | undefined = agenda.find(a => a.id === t.id)
        const ordens = item?.ordens ?? []

        // Mapa de datas ocupadas (para compatibilidade com chips do período)
        const ocupados = new Set<string>(
          ordens.map(o => o.dataAgendamento?.slice(0, 10)).filter((d): d is string => !!d),
        )
        novoMapa.set(t.id, ocupados)

        // Mapa de hora livre por dia (considerando previsão + 2h de buffer)
        const mapaHoras = new Map<string, string>()
        for (const ordem of ordens) {
          if (!ordem.dataAgendamento) continue
          const date = ordem.dataAgendamento.slice(0, 10)
          const timePart = ordem.dataAgendamento.slice(11, 16) // 'HH:MM'
          if (!timePart || timePart === '00:00') continue
          const parts = timePart.split(':')
          const hh = Number(parts[0] ?? 0)
          const mm = Number(parts[1] ?? 0)
          const duracaoMin = ordem.previsaoManutencao ?? 0
          const totalMin = hh * 60 + mm + duracaoMin + 120 // + 2h buffer
          const livreHH = String(Math.floor(totalMin / 60)).padStart(2, '0')
          const livreMM = String(totalMin % 60).padStart(2, '0')
          const livreTime = `${livreHH}:${livreMM}`
          const atual = mapaHoras.get(date)
          if (!atual || livreTime > atual) mapaHoras.set(date, livreTime)
        }
        novoMapaHoras.set(t.id, mapaHoras)

        // Janelas de ocupação por dia (início e fim natural da ordem, sem buffer)
        const ocupacoes = new Map<string, Array<{inicio: string, fim: string}>>()
        for (const ordem of ordens) {
          if (!ordem.dataAgendamento) continue
          const date = ordem.dataAgendamento.slice(0, 10)
          const tp = ordem.dataAgendamento.slice(11, 16)
          if (!tp || tp === '00:00') continue
          const duracaoMin = ordem.previsaoManutencao ?? 0
          const tParts = tp.split(':')
          const tHH = Number(tParts[0] ?? 0)
          const tMM = Number(tParts[1] ?? 0)
          const fimMin = tHH * 60 + tMM + duracaoMin
          const fimStr = duracaoMin > 0
            ? `${String(Math.floor(fimMin / 60)).padStart(2, '0')}:${String(fimMin % 60).padStart(2, '0')}`
            : ''
          const lista = ocupacoes.get(date) ?? []
          lista.push({ inicio: tp, fim: fimStr })
          ocupacoes.set(date, lista)
        }
        novoOcupacao.set(t.id, ocupacoes)

        // Próximo dia completamente livre (sem nenhuma ordem)
        const cursor = new Date(inicio + 'T00:00:00')
        let proximo: string | null = null
        while (cursor <= d30) {
          const dStr = cursor.toISOString().slice(0, 10)
          if (!ocupados.has(dStr)) { proximo = dStr; break }
          cursor.setDate(cursor.getDate() + 1)
        }
        novoProximo.set(t.id, proximo)
      } catch {
        novoMapa.set(t.id, new Set())
        novoMapaHoras.set(t.id, new Map())
        novoOcupacao.set(t.id, new Map())
        novoProximo.set(t.id, null)
      }
    }),
  )

  diasOcupados.value         = novoMapa
  horaLivreNoDia.value       = novoMapaHoras
  ocupacaoNoDia.value        = novoOcupacao
  proximoDiaDisponivel.value = novoProximo
  loadingAgenda.value        = false
}

const clientes = ref<any[]>([])
const tecnicosDisponiveis = ref<TecnicoResponseDTO[]>([])
const softwareInstalado = ref<any | null>(null)
const ativos = ref<AtivoResponseDTO[]>([])

const selectedClienteId = ref<string | null>(null)
const selectedContratoId = ref<string | null>(null)
const selectedMaquinaId = ref<string | null>(null)

const checklistItems = ref<ChecklistItemLocal[]>([])
const showAddItemForm = ref(false)
const newItemCodigoAtivo = ref('')
const newItemDescricaoAtivo = ref('')
const newItemSearchQuery = ref('')

const checklistMaquinaItens = ref<CatalogoMaquinaChecklistPadraoResponseDTO[]>([])
const selectedTarefas = ref<Set<number>>(new Set())
const loadingChecklistMaquina = ref(false)

// Extra tasks added on top of the standard checklist
const todasTarefas = ref<CatalogoTarefaResponseDTO[]>([])
const tarefasExtrasAdicionadas = ref<CatalogoTarefaResponseDTO[]>([])
const filtroTarefaExtra = ref('')
const showCriarTarefa = ref(false)
const novaTarefaDescricao = ref('')
const novaTarefaCategoria = ref('')
const criandoTarefa = ref(false)

const codigosStandard = computed(() =>
  new Set(checklistMaquinaItens.value.map((i: CatalogoMaquinaChecklistPadraoResponseDTO) => i.codigoTarefa))
)
const codigosExtras = computed(() =>
  new Set(tarefasExtrasAdicionadas.value.map((t: CatalogoTarefaResponseDTO) => t.id))
)

const tarefasParaAdicionar = computed(() => {
  return todasTarefas.value.filter((t: CatalogoTarefaResponseDTO) => {
    if (codigosStandard.value.has(t.id)) return false
    if (codigosExtras.value.has(t.id)) return false
    if (!filtroTarefaExtra.value) return true
    const q = filtroTarefaExtra.value.toLowerCase()
    return t.descricao.toLowerCase().includes(q) || (t.categoria ?? '').toLowerCase().includes(q)
  })
})

const ativosFilterados = computed(() => {
  const q = newItemSearchQuery.value.toLowerCase().trim()
  if (!q) return ativos.value
  return ativos.value.filter(a =>
    a.descricaoProduto?.toLowerCase().includes(q) ||
    a.numeroSerie?.toLowerCase().includes(q) ||
    a.marca?.toLowerCase().includes(q) ||
    a.modelo?.toLowerCase().includes(q) ||
    a.codigo.toString().includes(q)
  )
})

const ativoSelecionadoObj = computed(() =>
  ativos.value.find(a => a.codigo.toString() === newItemCodigoAtivo.value) ?? null
)

const clienteSelecionado = computed(() =>
  clientes.value.find(c => c.id.toString() === selectedClienteId.value) ?? null
)
const contratosFiltrados = computed(() =>
  clienteSelecionado.value?.contratos ?? []
)
const contratoSelecionado = computed(() =>
  contratosFiltrados.value.find((ct: any) => ct.codigo.toString() === selectedContratoId.value) ?? null
)
const maquinasFiltradas = computed(() =>
  contratoSelecionado.value?.maquinas ?? []
)
const maquinaSelecionada = computed(() =>
  maquinasFiltradas.value.find((m: any) => m.codigo.toString() === selectedMaquinaId.value) ?? null
)

const todayDisplayString = computed(() => {
  const now = new Date()
  const day   = String(now.getDate()).padStart(2, '0')
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const year  = now.getFullYear()
  return `${day}/${month}/${year}`
})

function toLocalDateTimeString(isoDate: string): string {
  if (!isoDate) return ''
  if (isoDate.includes('T')) return isoDate
  const time = horaAgendamento.value ? `${horaAgendamento.value}:00` : '00:00:00'
  return `${isoDate}T${time}`
}

function todayLocalDateTime(): string {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())}T${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

const amanhaToCal = computed(() => {
  const d = new Date()
  d.setDate(d.getDate() + 1)
  return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate())
})

const formSchema = toTypedSchema(z.object({
  codigoCliente:         z.string({ required_error: '*' }).min(1, 'Selecione um cliente'),
  codigoContrato:        z.string({ required_error: '*' }).min(1, 'Selecione um contrato'),
  criticidade:           z.string({ required_error: '*' }).min(1, 'Selecione a criticidade'),
  tipoOrdem:             z.string({ required_error: '*' }).min(1, 'Selecione o tipo de ordem'),
  dataAgendamento:       z.string({ required_error: '*' }).min(1, 'Selecione a data de agendamento').refine((v: string) => v === '' || /^\d{4}-\d{2}-\d{2}$/.test(v), 'Data inválida'),
  observacaoGeral:       z.string().optional().or(z.literal('')),
  codigoMaquinaContrato: z.string({ required_error: '*' }).min(1, 'Selecione uma máquina'),
  codigoFuncionario:     z.string({ required_error: '*' }).min(1, 'Selecione um técnico'),
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    codigoCliente: '',
    codigoContrato: '',
    criticidade: '',
    tipoOrdem: '',
    dataAgendamento: '',
    observacaoGeral: '',
    codigoMaquinaContrato: '',
    codigoFuncionario: '',
  }
})


onMounted(async () => {
  try {
    clientes.value = await clienteService.listar()
  } catch (error) {
    console.error('Erro ao carregar dependências:', error)
  }

  if (props.initialData) {
    await nextTick()
    await popularFormEdicao(props.initialData)
  }
})

const popularFormEdicao = async (data: OrdemServicoResponseDTO) => {
  selectedClienteId.value = String(data.codigoCliente)
  selectedContratoId.value = String(data.codigoContrato)
  selectedMaquinaId.value = String(data.codigoMaquinaContrato)

  form.resetForm({
    values: {
      codigoCliente: String(data.codigoCliente),
      codigoContrato: String(data.codigoContrato),
      criticidade: data.criticidade ?? '',
      tipoOrdem: data.tipoOrdem ?? '',
      dataAgendamento: data.dataAgendamento?.split('T')[0] ?? '',
      observacaoGeral: data.observacaoGeral ?? '',
      codigoMaquinaContrato: String(data.codigoMaquinaContrato),
      codigoFuncionario: data.codigoFuncionario ? String(data.codigoFuncionario) : '',
    }
  })

  if (data.dataAgendamento && data.dataAgendamento.includes('T')) {
    const timePart = data.dataAgendamento.slice(11, 16)
    if (timePart && timePart !== '00:00') {
      horaHH.value = timePart.slice(0, 2)
      horaMM.value = timePart.slice(3, 5)
    }
  }

  if (data.previsaoManutencao && data.previsaoManutencao > 0) {
    previsaoHoras.value   = Math.floor(data.previsaoManutencao / 60)
    previsaoMinutos.value = data.previsaoManutencao % 60
  }

  if (data.codigoMaquinaContrato) {
    loadingSoftware.value = true
    try {
      const resultados = await maquinaSoftwareInstaladoService.buscarPorMaquinaContrato(data.codigoMaquinaContrato)
      softwareInstalado.value = resultados.length > 0 ? resultados[0] : null
    } catch { softwareInstalado.value = null }
    finally { loadingSoftware.value = false }
  }

  try {
    const itensExistentes = await ordemServicoService.listarChecklistAtivos(data.codigo)
    checklistItems.value = itensExistentes.map(i => ({
      codigoAtivo: i.codigoAtivo,
      descricaoAtivo: i.descricaoAtivo ?? '',
      descricaoProduto: i.descricaoProduto,
      numeroSerie: i.numeroSerie,
    }))
  } catch { checklistItems.value = [] }
}

function onClienteChange(val: string) {
  selectedClienteId.value = val
  selectedContratoId.value = null
  selectedMaquinaId.value = null
  softwareInstalado.value = null
  form.setFieldValue('codigoContrato', '')
  form.setFieldValue('codigoMaquinaContrato', '')
}

function onContratoChange(val: string) {
  selectedContratoId.value = val
  selectedMaquinaId.value = null
  softwareInstalado.value = null
  form.setFieldValue('codigoMaquinaContrato', '')
}

async function onMaquinaChange(val: string) {
  selectedMaquinaId.value = val
  softwareInstalado.value = null
  if (!val) return

  loadingSoftware.value = true
  try {
    const resultados = await maquinaSoftwareInstaladoService.buscarPorMaquinaContrato(Number(val))
    softwareInstalado.value = resultados.length > 0 ? resultados[0] : null
  } catch (error: any) {
    const is404 = error?.message?.includes('404') || error?.status === 404
    if (!is404) console.error('Erro ao buscar software instalado:', error)
    softwareInstalado.value = null
  } finally {
    loadingSoftware.value = false
  }
}

// Recarrega quando: período muda, modo muda, ou data muda no modo data-específica
// Selecionar um chip (dataAgendamento) no modo período NÃO recarrega a agenda
watch(
  () => [periodoInicio.value, periodoFim.value, modoAgendamento.value] as const,
  () => { if (step.value === 3) carregarAgenda() },
)
watch(
  () => form.values.dataAgendamento,
  (data) => {
    if (step.value !== 3) return
    // Desfaz seleções ao trocar o dia de agendamento
    tecnicosSelecionados.value = new Set()
    tecnicoResponsavelId.value = null
    form.setFieldValue('codigoFuncionario', '')
    if (data && modoAgendamento.value === 'data') carregarAgenda()
  },
)

watch(step, async (newStep) => {
  if (newStep === 3 && tecnicosDisponiveis.value.length === 0) {
    loadingTecnicos.value = true
    try {
      const todos = await tecnicoService.listarSelecionaveis()
      tecnicosDisponiveis.value = todos
      // Se já há data selecionada, carrega agenda imediatamente
      if (form.values.dataAgendamento) carregarAgenda()
    } catch (error) {
      console.error('Erro ao carregar técnicos:', error)
    } finally {
      loadingTecnicos.value = false
    }
  }

  if (newStep === 4 && ativos.value.length === 0) {
    loadingAtivos.value = true
    try {
      ativos.value = await ativoService.listar() ?? []
    } catch (error) {
      console.error('Erro ao carregar ativos:', error)
    } finally {
      loadingAtivos.value = false
    }
  }

  if (newStep === 5) {
    const codigoCatalogo = (maquinaSelecionada.value as any)?.codigoCatalogoMaquina
    if (codigoCatalogo && checklistMaquinaItens.value.length === 0) {
      loadingChecklistMaquina.value = true
      try {
        const itens = await catalogoMaquinaService.listarChecklistPadrao(codigoCatalogo) ?? []
        checklistMaquinaItens.value = itens
        selectedTarefas.value = new Set(itens.map((i: CatalogoMaquinaChecklistPadraoResponseDTO) => i.codigoTarefa))
      } catch (error) {
        console.error('Erro ao carregar checklist padrão:', error)
      } finally {
        loadingChecklistMaquina.value = false
      }
    }
    if (todasTarefas.value.length === 0) {
      try {
        todasTarefas.value = await catalogoTarefaService.listar()
      } catch (error) {
        console.error('Erro ao carregar catálogo de tarefas:', error)
      }
    }
  }
})

const STEP_FIELDS: Record<number, string[]> = {
  1: ['codigoCliente', 'codigoContrato', 'criticidade', 'tipoOrdem', 'observacaoGeral'],
  2: ['codigoMaquinaContrato'],
  3: ['codigoFuncionario', 'dataAgendamento'],
  4: [],
  5: [],
}

const nextStep = async () => {
  // Step 4: validação customizada (mínimo 1 ativo)
  if (step.value === 4) {
    if (checklistItems.value.length === 0) {
      checklistAtivosError.value = 'Adicione pelo menos um ativo ao checklist antes de prosseguir.'
      return
    }
    checklistAtivosError.value = ''
    step.value++
    return
  }

  const currentFields = STEP_FIELDS[step.value] ?? []
  const results = await Promise.all(
    currentFields.map(field => form.validateField(field as any))
  )
  const hasErrors = results.some(r => !r.valid)
  if (!hasErrors) {
    step.value++
    // Limpar erros do novo step para não exibir pré-emptivamente
    await nextTick()
    const incomingFields = STEP_FIELDS[step.value] ?? []
    incomingFields.forEach(field => form.setFieldError(field as any, undefined))
  }
}

function getInitials(nome: string): string {
  return nome.split(' ').filter(Boolean).slice(0, 2).map(n => n[0].toUpperCase()).join('')
}

function onAtivoSelect(val: string) {
  newItemCodigoAtivo.value = val
  newItemSearchQuery.value = ''
  const ativo = ativos.value.find((a: AtivoResponseDTO) => a.codigo.toString() === val)
  if (ativo?.descricao) {
    newItemDescricaoAtivo.value = ativo.descricao
  }
}

function adicionarItem() {
  if (!newItemCodigoAtivo.value) return
  const ativo = ativos.value.find((a: AtivoResponseDTO) => a.codigo.toString() === newItemCodigoAtivo.value)
  checklistItems.value.push({
    codigoAtivo: Number(newItemCodigoAtivo.value),
    descricaoAtivo: newItemDescricaoAtivo.value,
    descricaoProduto: ativo?.descricaoProduto,
    numeroSerie: ativo?.numeroSerie,
  })
  checklistAtivosError.value = ''
  cancelarAddItem()
}

function cancelarAddItem() {
  newItemCodigoAtivo.value = ''
  newItemDescricaoAtivo.value = ''
  newItemSearchQuery.value = ''
  showAddItemForm.value = false
}

function removerItem(idx: number) {
  checklistItems.value.splice(idx, 1)
}

function toggleTarefa(codigoTarefa: number) {
  const s = new Set(selectedTarefas.value)
  if (s.has(codigoTarefa)) {
    s.delete(codigoTarefa)
  } else {
    s.add(codigoTarefa)
  }
  selectedTarefas.value = s
}

function adicionarTarefaExtra(tarefa: CatalogoTarefaResponseDTO) {
  tarefasExtrasAdicionadas.value.push(tarefa)
  filtroTarefaExtra.value = ''
}

function removerTarefaExtra(id: number) {
  tarefasExtrasAdicionadas.value = tarefasExtrasAdicionadas.value.filter(
    (t: CatalogoTarefaResponseDTO) => t.id !== id
  )
}

async function criarEAdicionarTarefa() {
  if (!novaTarefaDescricao.value.trim()) return
  criandoTarefa.value = true
  try {
    const tarefa = await catalogoTarefaService.criar({
      descricao: novaTarefaDescricao.value.trim(),
      categoria: novaTarefaCategoria.value.trim() || undefined,
    })
    todasTarefas.value.push(tarefa)
    adicionarTarefaExtra(tarefa)
    novaTarefaDescricao.value = ''
    novaTarefaCategoria.value = ''
    showCriarTarefa.value = false
  } catch (e: any) {
    alert('Erro ao criar tarefa: ' + (e.response?.data?.message || e.message))
  } finally {
    criandoTarefa.value = false
  }
}

const onSubmit = form.handleSubmit(async (values) => {
  loading.value = true
  try {
    const payload = {
      codigoCliente:           Number(values.codigoCliente),
      codigoContrato:          Number(values.codigoContrato),
      codigoMaquinaContrato:   Number(values.codigoMaquinaContrato),
      codigoFuncionario:       tecnicoResponsavelId.value ?? Number(values.codigoFuncionario),
      codigosFuncionarios:     tecnicosSelecionados.value.size > 0
                                 ? [...tecnicosSelecionados.value]
                                 : [Number(values.codigoFuncionario)],
      codigoSoftwareInstalado: softwareInstalado.value?.codigo ?? undefined,
      criticidade:             values.criticidade,
      tipoOrdem:               values.tipoOrdem || undefined,
      dataAgendamento:         toLocalDateTimeString(values.dataAgendamento ?? ''),
      observacaoGeral:         values.observacaoGeral,
      previsaoManutencao:      previsaoTotalMinutos.value > 0 ? previsaoTotalMinutos.value : undefined,
    }

    if (isEditMode.value && props.initialData) {
      const statusAtual = props.initialData.status
      const novoStatus = statusAtual === 'ABERTA' ? 'AGENDADO' : statusAtual

      await ordemServicoService.atualizar(props.initialData.codigo, {
        ...payload,
        status: novoStatus,
        dataAbertura: props.initialData.dataAbertura,
      })

      await ordemServicoService.substituirChecklistAtivos(
        props.initialData.codigo,
        checklistItems.value.map(item => ({
          codigoAtivo: item.codigoAtivo,
          descricaoAtivo: item.descricaoAtivo || undefined,
          codigoFuncionario: Number(values.codigoFuncionario) || undefined,
        }))
      )
    } else {
      const ordemCriada = await ordemServicoService.criar({
        ...payload,
        status: 'AGENDADO',
        dataAbertura: todayLocalDateTime(),
      })

      if (ordemCriada && checklistItems.value.length > 0) {
        const codigoFuncionario = Number(values.codigoFuncionario)
        for (const item of checklistItems.value) {
          await ordemServicoService.adicionarChecklistAtivo(ordemCriada.codigo, {
            codigoAtivo: item.codigoAtivo,
            descricaoAtivo: item.descricaoAtivo || undefined,
            codigoFuncionario,
          })
        }
      }

      if (ordemCriada?.codigoHistoricoManutencao) {
        const historicoId = ordemCriada.codigoHistoricoManutencao
        const todasSelecionadas = [
          ...selectedTarefas.value,
          ...tarefasExtrasAdicionadas.value.map((t: CatalogoTarefaResponseDTO) => t.id),
        ]
        for (const codigoTarefa of todasSelecionadas) {
          await ordemServicoService.adicionarChecklistMaquinaItem(historicoId, codigoTarefa)
        }
      }
    }

    emit('fechar')
    emit('success')
  } catch (error: any) {
    console.error('Erro ao salvar ordem de serviço:', error)
    alert('Ocorreu um erro ao salvar a ordem de serviço. Verifique o console.')
  } finally {
    loading.value = false
  }
})
</script>
