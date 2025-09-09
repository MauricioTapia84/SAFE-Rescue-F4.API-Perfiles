package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un equipo en el sistema.
 * Contiene información sobre la composición y el estado del equipo.
 */
@Entity
@Table(name = "equipo")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Equipo {

    /**
     * Identificador único del equipo.
     */
    @Id
    @Column(name = "id_equipo")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del equipo", example = "1")
    private int idEquipo;

    /**
     * Nombre del equipo (máximo 50 caracteres).
     */
    @Column(name = "nombre_equipo", length = 50, nullable = false)
    @Schema(description = "Nombre del equipo", example = "Equipo Alfa", required = true)
    @Size(max = 50)
    private String nombre;

    /**
     * Líder del equipo.
     * Relación uno-a-uno con la entidad Usuario.
     */
    @OneToOne
    @JoinColumn(name = "lider_id", referencedColumnName = "id_usuario", nullable = true)
    @Schema(description = "Líder del equipo", example = "Usuario líder del equipo")
    private Usuario lider;

    /**
     * Compañía a la que pertenece el equipo.
     * Relación muchos-a-uno con la entidad Compania.
     */
    @ManyToOne
    @JoinColumn(name = "compania_id", referencedColumnName = "id_compania")
    @Schema(description = "Compañía a la que pertenece el equipo")
    private Compania compania;

    /**
     * Tipo de equipo (especialización).
     * Relación muchos-a-uno con la entidad TipoEquipo.
     */
    @ManyToOne
    @JoinColumn(name = "tipo_equipo_id", referencedColumnName = "id_tipo_equipo")
    @Schema(description = "Tipo de equipo asignado")
    private TipoEquipo tipoEquipo;

    /**
     * Estado equipo.
     * Relación Muchos-a-uno con la entidad Estado equipo que pertenece a la API Configuraciones.
     */
    @ManyToOne
    @JoinColumn(name = "estado_id", referencedColumnName = "id_estado")
    @Schema(description = "Estado del equipo")
    private Estado estado;
}