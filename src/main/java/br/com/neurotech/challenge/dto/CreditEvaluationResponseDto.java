// src/main/java/.../dto/CreditEvaluationResponseDto.java
package br.com.neurotech.challenge.dto;


import br.com.neurotech.challenge.entity.CreditType;
import br.com.neurotech.challenge.entity.VehicleModel;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditEvaluationResponseDto {
    private UUID clientId;
    private VehicleModel vehicleModel;
    private boolean eligibleForVehicle;
    private CreditType creditType;
}
