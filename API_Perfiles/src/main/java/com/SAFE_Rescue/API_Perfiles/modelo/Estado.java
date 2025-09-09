package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Estado {

    @Id
    @Column(name = "id_estado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del estado", example = "1")
    private int idEstado;

    @Column(name = "nombre", unique = true, length = 50, nullable = false)
    @Schema(description = "Nombre del estado", example = "Activo")
    private String nombre;

    @Column(name = "descripcion", length = 100, nullable = true)
    @Schema(description = "Descripción del estado", example = "El usuario está activo en el sistema.")
    private String descripcion;
}