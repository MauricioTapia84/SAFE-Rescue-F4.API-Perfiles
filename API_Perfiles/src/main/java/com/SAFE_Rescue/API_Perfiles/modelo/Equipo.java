package com.SAFE_Rescue.API_Perfiles.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Entidad que representa un equipo de bomberos en el sistema.
 * Contiene información sobre la composición y estado del equipo.
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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental
    @Schema(description = "Identificador único del equipo", example = "1")
    private Integer id;

    /**
     * Nombre del equipo (máximo 50 caracteres).
     */
    @Column(name = "nombre_equipo", length = 50, nullable = false)
    @Schema(description = "Nombre del equipo", example = "Equipo A", required = true, maxLength = 50)
    private String nombre;

    /**
     * Cantidad de miembros en el equipo (hasta 99).
     */
    @Column(name = "cantidad_miembros", length = 2, nullable = true)
    @Schema(description = "Cantidad de miembros en el equipo", example = "5", minimum = "0", maximum = "99")
    private Integer cantidadMiembros;

    /**
     * Estado actual del equipo (activo/inactivo).
     */
    @Column(nullable = false)
    @Schema(description = "Estado del equipo", example = "true")
    private boolean estado;

    /**
     * Nombre del líder del equipo (máximo 50 caracteres).
     */
    @Column(name = "nombre_lider", length = 50, nullable = true)
    @Schema(description = "Nombre del líder del equipo", example = "Juan Pérez", maxLength = 50)
    private String lider;


    /**
     * Lista de personal.
     * Relación muchos-a-muchos con la entidad Bombero.
     */
    @ManyToMany
    @JoinTable(
            name = "equipo_personal",
            joinColumns = @JoinColumn(name = "equipo_id"),
            inverseJoinColumns = @JoinColumn(name = "personal_id")
    )
    @Schema(description = "Lista de bomberos asignados al equipo")
    private List<Bombero> personal;

    /**
     * Compañía a la que pertenece el equipo.
     * Relación muchos-a-uno con la entidad Compania.
     */
    @ManyToOne
    @JoinColumn(name = "compania_id", referencedColumnName = "id")
    @Schema(description = "Compañía a la que pertenece el equipo")
    private Compania compania;

    /**
     * Tipo de equipo (especialización).
     * Relación muchos-a-uno con la entidad TipoEquipo.
     */
    @ManyToOne
    @JoinColumn(name = "tipo_equipo_id", referencedColumnName = "id")
    @Schema(description = "Tipo de equipo asignado")
    private TipoEquipo tipoEquipo;

}
