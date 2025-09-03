package br.com.neurotech.challenge.service;

import br.com.neurotech.challenge.entity.CreditType;
import br.com.neurotech.challenge.entity.NeurotechClient;
import br.com.neurotech.challenge.entity.VehicleModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImplCreditService implements CreditService {
    private final ImplClientService clientService;

    @Override
    public boolean checkCredit(UUID clientId, VehicleModel model) {
        NeurotechClient cliente = clientService.get(clientId);
        if (cliente == null) {
            return false;
        }

        switch (model) {
            case HATCH:
                return cliente.getIncome() >= 5000 && cliente.getIncome() <= 15000;
            case SUV:
                return cliente.getIncome() >= 8000 && cliente.getAge() > 20;
            default:
                return false;
        }
    }

    @Override
    public Optional<CreditType> classifyCredit(UUID clientId) {
        var client = clientService.get(clientId);
        if (client == null) return Optional.empty();

        int age = Optional.ofNullable(client.getAge()).orElse(0);
        double income = Optional.ofNullable(client.getIncome()).orElse(0.0);

        if (age >= 18 && age <= 25) return Optional.of(CreditType.JUROS_FIXOS);
        if (age >= 21 && age <= 65 && income >= 5_000 && income <= 15_000) return Optional.of(CreditType.JUROS_VARIAVEIS);
        if (age > 65) return Optional.of(CreditType.CONSIGNADO);
        return Optional.empty();
    }

}
