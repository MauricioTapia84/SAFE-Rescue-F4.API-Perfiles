package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un tipo de equipo en el sistema.
 * Contiene información sobre la composición y estado del tipo de equipo
 */
@Entity
@Table(name = "tipo_equipo")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Entidad que representa un tipo de equipo")
public class TipoEquipo {

    @Id
    @Column(name = "id_tipo_equipo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del tipo de equipo", example = "1")
    private int idTipoEquipo;

    /**
     * Nombre del Tipo equipo
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Schema(description = "Nombre del tipo de equipo", example = "Médico")
    @Column(length = 50, nullable = false)
    private String nombre;

}
