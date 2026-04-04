package com.example.tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.tracker.entity.Contrato;
import com.example.tracker.security.JwtService;
import com.example.tracker.service.ContratoService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ContratoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContratoService contratoService;

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    void setupAuthentication() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@tracker.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        given(jwtService.parseToken(anyString())).willReturn(authentication);
    }

    @Test
    void deveListarContratos() throws Exception {
        Contrato contrato = criarContrato(1, 10, "ATIVO");
        given(contratoService.listarTodos()).willReturn(List.of(contrato));

        mockMvc.perform(get("/contratos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andExpect(jsonPath("$[0].codigoCliente").value(10))
                .andExpect(jsonPath("$[0].status").value("ATIVO"));
    }

    @Test
    void deveBuscarContratoPorId() throws Exception {
        Contrato contrato = criarContrato(2, 20, "ENCERRADO");
        given(contratoService.buscarPorId(2)).willReturn(contrato);

        mockMvc.perform(get("/contratos/2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(2))
                .andExpect(jsonPath("$.codigoCliente").value(20))
                .andExpect(jsonPath("$.status").value("ENCERRADO"));
    }

    @Test
    void deveBuscarContratosPorCliente() throws Exception {
        Contrato contrato = criarContrato(3, 30, "ATIVO");
        given(contratoService.buscarPorCliente(30)).willReturn(List.of(contrato));

        mockMvc.perform(get("/contratos/cliente/30")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(3))
                .andExpect(jsonPath("$[0].codigoCliente").value(30));
    }

    @Test
    void deveCriarContrato() throws Exception {
        Contrato contratoCriado = criarContrato(4, 40, "ATIVO");
        given(contratoService.cadastrar(any())).willReturn(contratoCriado);

        String payload = """
                {
                  "codigoCliente": 40,
                  "dataInicio": "2026-04-01",
                  "dataFim": "2026-12-01",
                  "status": "ATIVO",
                  "periodoManutencaoPreventiva": 30,
                  "conexaoInternet": true,
                  "vencimentoManutencaoPreventiva": "2026-05-01"
                }
                """;

        mockMvc.perform(post("/contratos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(4))
                .andExpect(jsonPath("$.codigoCliente").value(40))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void deveAtualizarContrato() throws Exception {
        Contrato contratoAtualizado = criarContrato(5, 50, "RENOVADO");
        given(contratoService.atualizar(eq(5), any())).willReturn(contratoAtualizado);

        String payload = """
                {
                  "codigoCliente": 50,
                  "dataInicio": "2026-04-01",
                  "dataFim": "2027-04-01",
                  "status": "RENOVADO",
                  "periodoManutencaoPreventiva": 45,
                  "conexaoInternet": false,
                  "vencimentoManutencaoPreventiva": "2026-06-01"
                }
                """;

        mockMvc.perform(put("/contratos/5")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(5))
                .andExpect(jsonPath("$.status").value("RENOVADO"));
    }

    @Test
    void deveDeletarContrato() throws Exception {
        willDoNothing().given(contratoService).deletar(6);

        mockMvc.perform(delete("/contratos/6")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400QuandoPayloadForInvalidoNoPost() throws Exception {
        String payloadInvalido = """
                {
                  "codigoCliente": 40,
                  "dataInicio": "2026-04-01"
                }
                """;

        mockMvc.perform(post("/contratos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Dados invalidos."))
                .andExpect(jsonPath("$.status").value("O status do contrato e obrigatorio."));
    }

    @Test
    void deveRetornar404QuandoContratoNaoForEncontrado() throws Exception {
        given(contratoService.buscarPorId(999))
                .willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));

        mockMvc.perform(get("/contratos/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar403SemToken() throws Exception {
        mockMvc.perform(get("/contratos"))
                .andExpect(status().isForbidden());
    }

    private Contrato criarContrato(Integer codigo, Integer codigoCliente, String status) {
        Contrato contrato = new Contrato();
        contrato.setCodigo(codigo);
        contrato.setDataInicio(LocalDate.of(2026, 4, 1));
        contrato.setDataFim(LocalDate.of(2026, 12, 1));
        contrato.setStatus(status);
        contrato.setPeriodoManutencaoPreventiva(30);
        contrato.setConexaoInternet(true);
        contrato.setVencimentoManutencaoPreventiva(LocalDate.of(2026, 5, 1));
        return contrato;
    }
}
