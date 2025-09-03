package br.com.neurotech.challenge.controller;

import br.com.neurotech.challenge.dto.CreditEvaluationResponseDto;
import br.com.neurotech.challenge.entity.NeurotechClient;
import br.com.neurotech.challenge.entity.VehicleModel;
import br.com.neurotech.challenge.service.ClientService;
import br.com.neurotech.challenge.service.CreditService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/client")
@AllArgsConstructor
public class ClientController {

    private final ClientService service;
    private final CreditService creditService;

    @Operation(
            summary = "Cadastrar cliente",
            description = "Cria um novo cliente. O ID é retornado no header Location."
    )
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody NeurotechClient body) {
        UUID id = service.save(body);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os dados de um cliente cadastrado com base no ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<NeurotechClient> get(@PathVariable UUID id) {
        NeurotechClient client = service.get(id);
        if (client == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(client);
    }

    @Operation(
            summary = "Verificar crédito do cliente para um modelo de veículo",
            description = "Verifica se o cliente é elegivel a receber crédito para um modelo de veículo especificado."
    )
    @GetMapping({"/{id}/credit/{modelo}"})
    public ResponseEntity<CreditEvaluationResponseDto> verificarCredito(@PathVariable UUID id, @PathVariable VehicleModel modelo) {

        NeurotechClient client = service.get(id);
        if (client == null) return ResponseEntity.notFound().build();

        boolean eligible = creditService.checkCredit(id, modelo);
        var creditType = creditService.classifyCredit(id).orElse(null);

        var resp = new CreditEvaluationResponseDto(id, modelo, eligible, creditType);
        return ResponseEntity.ok(resp);
    }

}
