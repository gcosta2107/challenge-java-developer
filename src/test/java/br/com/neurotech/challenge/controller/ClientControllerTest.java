package br.com.neurotech.challenge.controller;

import br.com.neurotech.challenge.entity.CreditType;
import br.com.neurotech.challenge.entity.NeurotechClient;
import br.com.neurotech.challenge.entity.VehicleModel;
import br.com.neurotech.challenge.repository.ClientRepository;
import br.com.neurotech.challenge.service.ClientService;
import br.com.neurotech.challenge.service.CreditService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class ClientControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ClientService clientService;

    @MockBean
    CreditService creditService;

    @MockBean
    ClientRepository clientRepository;

    // criar um cliente em memória
    private NeurotechClient novo(String name, Integer age, Double income) {
        var cliente = new NeurotechClient();
        cliente.setId(UUID.randomUUID());
        cliente.setName(name);
        cliente.setAge(age);
        cliente.setIncome(income);
        return cliente;
    }

    @Test
    void postCreate_retorna_HeaderLocation() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(clientService.save(Mockito.any(NeurotechClient.class))).thenReturn(id);

        mvc.perform(post("/api/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TESTE\",\"age\":19,\"income\":5000.00}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/client/" + id));
    }

    @Test
    void getById_retornaObjeto() throws Exception {
        UUID id = UUID.randomUUID();
        NeurotechClient c = new NeurotechClient();
        c.setName("TESTE_NAME"); c.setAge(21); c.setIncome(10000.0);
        Mockito.when(clientService.get(id)).thenReturn(c);

        mvc.perform(get("/api/client/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("TESTE_NAME")))
                .andExpect(jsonPath("$.age", is(21)))
                .andExpect(jsonPath("$.income", is(10000.0)));
    }

    @Test
    void getById_404_clientNaoExiste() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(clientService.get(id)).thenReturn(null);

        mvc.perform(get("/api/client/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void verificarCredito_ok() throws Exception {
        UUID id = UUID.randomUUID();
        NeurotechClient c = new NeurotechClient();
        c.setAge(22); c.setIncome(7000.0);
        Mockito.when(clientService.get(id)).thenReturn(c);
        Mockito.when(creditService.checkCredit(id, VehicleModel.HATCH)).thenReturn(true);
        Mockito.when(creditService.classifyCredit(id)).thenReturn(Optional.of(CreditType.JUROS_FIXOS));

        mvc.perform(get("/api/client/{id}/credit/{modelo}", id, "HATCH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId", is(id.toString())))
                .andExpect(jsonPath("$.vehicleModel", is("HATCH")))
                .andExpect(jsonPath("$.eligibleForVehicle", is(true)))
                .andExpect(jsonPath("$.creditType", is("JUROS_FIXOS")));
    }

    @Test
    void verificarCredito_404_ClienteNaoExiste() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(clientService.get(id)).thenReturn(null);

        mvc.perform(get("/api/client/{id}/credit/{modelo}", id, "HATCH"))
                .andExpect(status().isNotFound());
    }

    @Test
    void verificarCredito_400_ModeloInvalido() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.when(clientService.get(id)).thenReturn(new NeurotechClient());

        mvc.perform(get("/api/client/{id}/credit/{modelo}", id, "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listEligibleHatchFixed_retornaClientesEsperados() throws Exception {
        var alessandra = novo("Alessandra",   24,  7_000.0);
        var bruno = novo("Bruno", 23, 12_000.0);
        var carla = novo("Carla", 26,  9_000.0);  // fora da regra FIXOS (18–25) -> não deve aparecer
        var diego = novo("Diego", 25,  3_000.0);  // renda fora da regra HATCH -> não deve aparecer

        Mockito.when(clientRepository.findByAgeBetween(23, 49))
                .thenReturn(List.of(alessandra, bruno, carla, diego));
        
        Mockito.when(creditService.classifyCredit(alessandra.getId()))
                .thenReturn(Optional.of(CreditType.JUROS_FIXOS));
        Mockito.when(creditService.classifyCredit(bruno.getId()))
                .thenReturn(Optional.of(CreditType.JUROS_FIXOS));
        Mockito.when(creditService.classifyCredit(carla.getId()))
                .thenReturn(Optional.of(CreditType.JUROS_VARIAVEIS));
        Mockito.when(creditService.classifyCredit(diego.getId()))
                .thenReturn(Optional.empty());

        // --- stubs de elegibilidade HATCH (somente Alessandra e Bruno)
        Mockito.when(creditService.checkCredit(alessandra.getId(),   VehicleModel.HATCH)).thenReturn(true);
        Mockito.when(creditService.checkCredit(bruno.getId(), VehicleModel.HATCH)).thenReturn(true);
        Mockito.when(creditService.checkCredit(carla.getId(), VehicleModel.HATCH)).thenReturn(false);
        Mockito.when(creditService.checkCredit(diego.getId(), VehicleModel.HATCH)).thenReturn(false);

        mvc.perform(get("/api/client/eligible/hatch-fixed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alessandra", "Bruno")))
                .andExpect(jsonPath("$[*].income", containsInAnyOrder(7000.0, 12000.0)));
    }

    @Test
    void listEligibleHatchFixed_NenhumClienteAtende_retornaListaVazia() throws Exception {
        var eva = novo("Eva", 30, 10_000.0); // idade fora de FIXOS
        Mockito.when(clientRepository.findByAgeBetween(23, 49))
                .thenReturn(List.of(eva));

        Mockito.when(creditService.classifyCredit(eva.getId()))
                .thenReturn(Optional.of(CreditType.JUROS_VARIAVEIS));
        Mockito.when(creditService.checkCredit(eva.getId(), VehicleModel.HATCH))
                .thenReturn(false);

        mvc.perform(get("/api/client/eligible/hatch-fixed"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
