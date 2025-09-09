package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un estado en el sistema.
 * Contiene información sobre la composición y estado del estado
 */
@Entity
@Table(name = "estado")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Entidad que representa un estado")
public class Estado {

    @Id
    @Column(name = "id_estado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del estado", example = "1")
    private int idEstado;

    /**
     * Nombre del Estado
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Schema(description = "Nombre del tipo de equipo", example = "Baneado")
    @Column(length = 50, nullable = false)
    private String nombre;

}
