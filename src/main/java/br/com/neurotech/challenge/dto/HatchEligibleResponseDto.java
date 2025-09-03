package br.com.neurotech.challenge.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HatchEligibleResponseDto {
    private String name;
    private Double income;
}