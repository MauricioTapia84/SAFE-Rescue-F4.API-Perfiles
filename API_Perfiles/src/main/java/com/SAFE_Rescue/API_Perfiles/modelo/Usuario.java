package com.SAFE_Rescue.API_Perfiles.modelo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

/**
 * Entidad que representa un usuario en el sistema.
 * Contiene información sobre la composición y estado del usuario.
 */
@Entity
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    /**
     * Identificador único del usuario.
     */
    @Id
    @Column(name = "id_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1")
    private int idUsuario;

    /**
     * Run del usuario.
     * Debe ser un valor no nulo, único y con una longitud máxima recomendada de 8 caracteres.
     */
    @Column(unique = true, length = 8, nullable = false)
    @Schema(description = "Run del usuario", example = "12345678", required = true)
    @Size(min = 7, max = 8)
    private String run;

    /**
     * Dígito verificador del usuario.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 1 carácter.
     */
    @Column(length = 1, nullable = false)
    @Schema(description = "Dígito verificador del usuario", example = "K", required = true)
    @Size(min = 1, max = 1)
    private String dv;

    /**
     * Nombre descriptivo del usuario.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(length = 50, nullable = false)
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    @Size(max = 50)
    private String nombre;

    /**
     * Apellido paterno descriptivo del usuario.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(name = "a_paterno", length = 50, nullable = false)
    @Schema(description = "Apellido paterno del usuario", example = "Pérez", required = true)
    @Size(max = 50)
    private String aPaterno;

    /**
     * Apellido materno descriptivo del usuario.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 50 caracteres.
     */
    @Column(name = "a_materno", length = 50, nullable = false)
    @Schema(description = "Apellido materno del usuario", example = "González", required = true)
    @Size(max = 50)
    private String aMaterno;

    /**
     * Fecha de registro del usuario.
     * Debe ser un valor no nulo.
     */
    @Column(name = "fecha_registro", nullable = false)
    @Schema(description = "Fecha de registro del usuario", example = "2022-01-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "GMT-4")
    private Date fechaRegistro;

    /**
     * Teléfono disponible del usuario.
     * Valor entero no negativo (>= 0).
     */
    @Column(unique = true, length = 9, nullable = false)
    @Schema(description = "Teléfono del usuario", example = "987654321", required = true)
    @Size(max = 9)
    private String telefono;

    /**
     * Correo del usuario.
     * Debe ser un valor no nulo y con una longitud máxima recomendada de 80 caracteres.
     */
    @Column(unique = true, length = 80, nullable = false)
    @Schema(description = "Correo del usuario", example = "usuario@ejemplo.com", required = true)
    @Email(message = "El correo debe tener un formato válido.")
    @Size(max = 80)
    private String correo;

    /**
     * Contraseña del usuario.
     * Debe ser un valor no nulo y con una longitud máxima recomendada para almacenar un hash seguro.
     */
    @Column(length = 70, nullable = false)
    @Schema(description = "Contraseña del usuario", example = "hash-seguro-de-contraseña", required = true)
    @Size(max = 70)
    private String contrasenia;

    /**
     * Intentos fallidos del usuario al iniciar sesión.
     * Valor entero no negativo (>= 0).
     */
    @Column(name="intentos_fallidos", nullable = false)
    @Schema(description = "Número de intentos fallidos de inicio de sesión", example = "0")
    private int intentosFallidos = 0;

    /**
     * Razón de baneo.
     * Puede ser un valor nulo y con una longitud máxima de 100 caracteres.
     */
    @Column(name="razon_baneo", length = 100, nullable = true)
    @Schema(description = "Razón de baneo del usuario", example = "Spam")
    @Size(max = 100)
    private String razonBaneo;

    /**
     * Días de baneo.
     * Puede ser un valor nulo y valor entero no negativo (>= 0).
     */
    @Column(name="dias_baneo", nullable = true)
    @Schema(description = "Número de días de baneo", example = "0")
    private Integer diasBaneo;

    /**
     * Estado usuario.
     * Relación Muchos-a-uno con la entidad Estado usuario que pertenece a la API Configuraciones.
     */
    @ManyToOne
    @JoinColumn(name = "estado_id", referencedColumnName = "id_estado")
    @Schema(description = "Estado del usuario")
    private Estado estado;

    /**
     * Tipo usuario.
     * Relación Muchos-a-uno con la entidad Tipo usuario.
     */
    @ManyToOne
    @JoinColumn(name = "tipo_usuario_id", referencedColumnName = "id_tipo_usuario")
    @Schema(description = "Tipo de usuario asociado al usuario")
    private TipoUsuario tipoUsuario;
}