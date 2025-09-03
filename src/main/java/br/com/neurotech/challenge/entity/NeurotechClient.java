package br.com.neurotech.challenge.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class NeurotechClient {

	@Id
	@UuidGenerator
	@GeneratedValue
	@Column(columnDefinition = "uuid", nullable = false)
	private UUID id;

	@JsonAlias({"Name", "name"})
	@JsonProperty("name")
	@Column(nullable = false)
	private String name;

	@JsonAlias({"Age", "age"})
	@JsonProperty("age")
	@Column(nullable = false)
	private Integer age;

	@JsonAlias({"Income", "income"})
	@JsonProperty("income")
	private Double income;

}