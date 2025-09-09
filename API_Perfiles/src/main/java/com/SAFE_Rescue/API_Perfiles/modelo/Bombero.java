package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id_usuario")
@Data
public class Bombero extends Usuario {

    /**
     * Equipo.
     * Relación Muchos-a-uno con la entidad Equipo que pertenece a la API Turnos.
     */
    @ManyToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "id_equipo")
    @Schema(description = "Equipo asociado al usuario")
    private Equipo equipo;
}