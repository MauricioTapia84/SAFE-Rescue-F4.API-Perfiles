package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un tipo de usuario en el sistema.
 * Contiene información sobre la composición y estado del tipo de usuario
 */
@Entity
@Table(name = "tipo_usuario")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Entidad que representa un tipo de usuario")
public class TipoUsuario {

    @Id
    @Column(name = "id_tipo_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del tipo de usuario", example = "1")
    private int idTipoUsuario;

    /**
     * Nombre del Tipo usuario
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres
     */
    @Schema(description = "Nombre del tipo de usuario", example = "Admin")
    @Column(length = 50, nullable = false)
    private String nombre;


}
