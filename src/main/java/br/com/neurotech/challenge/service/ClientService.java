package br.com.neurotech.challenge.service;

import org.springframework.stereotype.Service;

import br.com.neurotech.challenge.entity.NeurotechClient;

import java.util.UUID;

@Service
public interface ClientService {

	UUID save(NeurotechClient client);

	NeurotechClient get(UUID id);

}
