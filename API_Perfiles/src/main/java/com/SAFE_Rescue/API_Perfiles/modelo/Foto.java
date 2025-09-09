package com.SAFE_Rescue.API_Perfiles.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa la foto de perfil de un usuario en el sistema.
 * Esta clase almacena la URL de la imagen y se asocia con la entidad Usuario.
 */
@Entity
@Table(name = "foto")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Foto {

    /**
     * Identificador único de la foto.
     */
    @Id
    @Column(name = "id_foto")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la foto", example = "1")
    private int idFoto;

    /**
     * URL que apunta a la ubicación de la foto.
     * Es un valor no nulo, que se utiliza para cargar la imagen del usuario.
     */
    @Column(name = "url", length = 255, nullable = false)
    @Schema(description = "URL de la foto del usuario", example = "http://api-fotos.com/fotos/user123.jpg")
    private String url;

    // Puedes agregar más atributos aquí, como un timestamp o metadatos de la imagen.
}