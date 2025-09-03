package br.com.neurotech.challenge.service;

import br.com.neurotech.challenge.entity.NeurotechClient;
import br.com.neurotech.challenge.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ImplClientService implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public UUID save(NeurotechClient client) {
        return clientRepository.save(client).getId();
    }

    @Override
    public NeurotechClient get(UUID id) {
        return clientRepository.findById(id).orElse(null);
    }
}
