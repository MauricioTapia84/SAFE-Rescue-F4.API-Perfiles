package com.SAFE_Rescue.API_Perfiles.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Entidad que representa una Compañía de bomberos.
 * Contiene información básica de identificación y ubicación.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "compania")
public class Compania {

    /**
     * Identificador único de la compañía.
     * Se genera automáticamente mediante estrategia de identidad.
     */
    @Id
    @Column(name="id_compania")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la compañía", example = "1")
    private int idCompania;

    /**
     * Nombre de la compañía (debe ser único).
     * Restricciones:
     * - Máximo 50 caracteres
     * - No puede ser nulo
     */
    @Column(unique = true, length = 50, nullable = false)
    @Schema(description = "Nombre de la compañía", example = "Compañía 13", required = true, maxLength = 50)
    @Size(max = 50)
    private String nombre;

}