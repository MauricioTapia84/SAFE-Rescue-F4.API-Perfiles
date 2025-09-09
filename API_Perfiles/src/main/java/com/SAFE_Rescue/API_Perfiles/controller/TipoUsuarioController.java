package com.SAFE_Rescue.API_Perfiles.controller;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.service.TipoUsuarioService;
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
 * Controlador REST para la gestión de tipos de usuario
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de tipos de usuario
 */
@RestController
@RequestMapping("/api-administrador/v1/tipos-usuario")
@Tag(name = "Tipos de Usuario", description = "Operaciones de CRUD relacionadas con Tipos de Usuario")
public class TipoUsuarioController {

    @Autowired
    private TipoUsuarioService tipoUsuarioService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los tipos de usuario registrados en el sistema.
     * @return ResponseEntity con lista de tipos de usuario o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    @Operation(summary = "Obtener todos los tipos de usuario", description = "Obtiene una lista con todos los tipos de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de usuario obtenida exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoUsuario.class))),
            @ApiResponse(responseCode = "204", description = "No hay tipos de usuario registrados.")
    })
    public ResponseEntity<List<TipoUsuario>> listar() {
        List<TipoUsuario> tiposUsuario = tipoUsuarioService.findAll();
        if (tiposUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(tiposUsuario);
    }

    /**
     * Busca un tipo de usuario por su ID.
     * @param id ID del tipo de usuario a buscar
     * @return ResponseEntity con el tipo de usuario encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un tipo de usuario por su ID", description = "Obtiene un tipo de usuario al buscarlo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de usuario encontrado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoUsuario.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de usuario no encontrado.")
    })
    public ResponseEntity<?> buscarTipoUsuario(@Parameter(description = "ID del tipo de usuario a buscar", required = true)
                                               @PathVariable int id) {
        TipoUsuario tipoUsuario;
        try {
            tipoUsuario = tipoUsuarioService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Tipo de usuario no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(tipoUsuario);
    }

    /**
     * Crea un nuevo tipo de usuario.
     * @param tipoUsuario Datos del tipo de usuario a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo tipo de usuario", description = "Crea un nuevo tipo de usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de usuario creado con éxito."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> agregarTipoUsuario(@RequestBody @Parameter(description = "Datos del tipo de usuario a crear", required = true)
                                                     TipoUsuario tipoUsuario) {
        try {
            tipoUsuarioService.save(tipoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Tipo de usuario creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un tipo de usuario existente.
     * @param id ID del tipo de usuario a actualizar
     * @param tipoUsuario Datos actualizados del tipo de usuario
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tipo de usuario existente", description = "Actualiza los datos de un tipo de usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de usuario actualizado con éxito."),
            @ApiResponse(responseCode = "404", description = "Tipo de usuario no encontrado."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> actualizarTipoUsuario(@Parameter(description = "ID del tipo de usuario a actualizar", required = true)
                                                        @PathVariable Integer id,
                                                        @RequestBody @Parameter(description = "Datos actualizados del tipo de usuario", required = true)
                                                        TipoUsuario tipoUsuario) {
        try {
            tipoUsuarioService.update(tipoUsuario, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de usuario no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un tipo de usuario del sistema.
     * @param id ID del tipo de usuario a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tipo de usuario", description = "Elimina un tipo de usuario del sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de usuario eliminado con éxito."),
            @ApiResponse(responseCode = "404", description = "Tipo de usuario no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> eliminarTipoUsuario(@Parameter(description = "ID del tipo de usuario a eliminar", required = true)
                                                      @PathVariable Integer id) {
        try {
            tipoUsuarioService.delete(id);
            return ResponseEntity.ok("Tipo de usuario eliminada con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de usuario no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}
