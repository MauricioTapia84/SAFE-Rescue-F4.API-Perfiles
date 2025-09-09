package com.SAFE_Rescue.API_Perfiles.controller;

import com.SAFE_Rescue.API_Perfiles.modelo.Usuario;
import com.SAFE_Rescue.API_Perfiles.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión de usuarios
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de usuarios
 */
@RestController
@RequestMapping("/api-administrador/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones de CRUD relacionadas con Usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     * @return ResponseEntity con lista de usuarios o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Obtiene una lista con todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "204", description = "No hay usuarios registrados.")
    })
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca un usuario por su ID.
     * @param id ID del usuario a buscar
     * @return ResponseEntity con el usuario encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario por su ID", description = "Obtiene un usuario al buscarlo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    public ResponseEntity<?> buscarUsuario(@Parameter(description = "ID del usuario a buscar", required = true)
                                           @PathVariable int id) {
        Usuario usuario;
        try {
            usuario = usuarioService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Crea un nuevo usuario.
     * @param usuario Datos del usuario a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado con éxito."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> agregarUsuario(@RequestBody @Parameter(description = "Datos del usuario a crear", required = true)
                                                 Usuario usuario) {
        try {
            usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un usuario existente.
     * @param id ID del usuario a actualizar
     * @param usuario Datos actualizados del usuario
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los datos de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> actualizarUsuario(@Parameter(description = "ID del usuario a actualizar", required = true)
                                                    @PathVariable Integer id,
                                                    @RequestBody @Parameter(description = "Datos actualizados del usuario", required = true)
                                                    Usuario usuario) {
        try {
            usuarioService.update(usuario, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un usuario del sistema.
     * @param id ID del usuario a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado con éxito."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> eliminarUsuario(@Parameter(description = "ID del usuario a eliminar", required = true)
                                                  @PathVariable Integer id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.ok("Usuario eliminada con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    @PostMapping("/{id}/subir-foto")
    public ResponseEntity<String> subirFotoUsuario(@PathVariable Integer id, @RequestParam("foto") MultipartFile archivo) {
        try {
            // Se delega la lógica de subir y guardar la URL al servicio
            String fotoUrl = usuarioService.subirYActualizarFotoUsuario(id, archivo);
            return ResponseEntity.ok("Foto subida y URL guardada con éxito: " + fotoUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la foto: " + e.getMessage());
        }
    }
}