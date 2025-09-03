package br.com.neurotech.challenge.service;

import br.com.neurotech.challenge.entity.CreditType;
import org.springframework.stereotype.Service;

import br.com.neurotech.challenge.entity.VehicleModel;

import java.util.Optional;
import java.util.UUID;

@Service
public interface CreditService {
	
	/**
	 * Efetua a checagem se o cliente está apto a receber crédito
	 * para um determinado modelo de veículo
	 */
	boolean checkCredit(UUID clientId, VehicleModel model);

	Optional<CreditType> classifyCredit(UUID clientId);
	
}
