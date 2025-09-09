package com.SAFE_Rescue.API_Perfiles.controller;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Perfiles.service.TipoEquipoService;
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
 * Controlador REST para la gestión de tipos de equipo
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de tipos de equipo
 */
@RestController
@RequestMapping("/api-administrador/v1/tipos-equipo")
@Tag(name = "Tipos de Equipo", description = "Operaciones de CRUD relacionadas con Tipos de Equipo")
public class TipoEquipoController {

    @Autowired
    private TipoEquipoService tipoEquipoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los tipos de equipo registrados en el sistema.
     * @return ResponseEntity con lista de tipos de equipo o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    @Operation(summary = "Obtener todos los tipos de equipo", description = "Obtiene una lista con todos los tipos de equipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de equipo obtenida exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoEquipo.class))),
            @ApiResponse(responseCode = "204", description = "No hay tipos de equipo registrados.")
    })
    public ResponseEntity<List<TipoEquipo>> listar() {
        List<TipoEquipo> tiposEquipo = tipoEquipoService.findAll();
        if (tiposEquipo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(tiposEquipo);
    }

    /**
     * Busca un tipo de equipo por su ID.
     * @param id ID del tipo de equipo a buscar
     * @return ResponseEntity con el tipo de equipo encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un tipo de equipo por su ID", description = "Obtiene un tipo de equipo al buscarlo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de equipo encontrado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TipoEquipo.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de equipo no encontrado.")
    })
    public ResponseEntity<?> buscarTipoEquipo(@Parameter(description = "ID del tipo de equipo a buscar", required = true)
                                              @PathVariable int id) {
        TipoEquipo tipoEquipo;
        try {
            tipoEquipo = tipoEquipoService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Tipo de equipo no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(tipoEquipo);
    }

    /**
     * Crea un nuevo tipo de equipo.
     * @param tipoEquipo Datos del tipo de equipo a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo tipo de equipo", description = "Crea un nuevo tipo de equipo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de equipo creado con éxito."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> agregarTipoEquipo(@RequestBody @Parameter(description = "Datos del tipo de equipo a crear", required = true)
                                                    TipoEquipo tipoEquipo) {
        try {
            tipoEquipoService.save(tipoEquipo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Tipo de equipo creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un tipo de equipo existente.
     * @param id ID del tipo de equipo a actualizar
     * @param tipoEquipo Datos actualizados del tipo de equipo
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tipo de equipo existente", description = "Actualiza los datos de un tipo de equipo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de equipo actualizado con éxito."),
            @ApiResponse(responseCode = "404", description = "Tipo de equipo no encontrado."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> actualizarTipoEquipo(@Parameter(description = "ID del tipo de equipo a actualizar", required = true)
                                                       @PathVariable Integer id,
                                                       @RequestBody @Parameter(description = "Datos actualizados del tipo de equipo", required = true)
                                                       TipoEquipo tipoEquipo) {
        try {
            tipoEquipoService.update(tipoEquipo, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de equipo no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un tipo de equipo del sistema.
     * @param id ID del tipo de equipo a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tipo de equipo", description = "Elimina un tipo de equipo del sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de equipo eliminado con éxito."),
            @ApiResponse(responseCode = "404", description = "Tipo de equipo no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> eliminarTipoEquipo(@Parameter(description = "ID del tipo de equipo a eliminar", required = true)
                                                     @PathVariable Integer id) {
        try {
            tipoEquipoService.delete(id);
            return ResponseEntity.ok("Tipo de equipo eliminada con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tipo de equipo no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}