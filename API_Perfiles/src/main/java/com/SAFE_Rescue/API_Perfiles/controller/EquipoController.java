package com.SAFE_Rescue.API_Perfiles.controller;

import com.SAFE_Rescue.API_Perfiles.modelo.Equipo;
import com.SAFE_Rescue.API_Perfiles.service.EquipoService;
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
 * Controlador REST para la gestión de equipos
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de equipos
 */
@RestController
@RequestMapping("/api-perfiles/v1/equipos")
@Tag(name = "Equipos", description = "Operaciones de CRUD relacionadas con Equipos")
public class EquipoController {

    @Autowired
    private EquipoService equipoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los equipos registrados en el sistema.
     * @return ResponseEntity con lista de equipos o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    @Operation(summary = "Obtener todos los equipos", description = "Obtiene una lista con todos los equipos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de equipos obtenida exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Equipo.class))),
            @ApiResponse(responseCode = "204", description = "No hay equipos registrados.")
    })
    public ResponseEntity<List<Equipo>> listar() {
        List<Equipo> equipos = equipoService.findAll();
        if (equipos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(equipos);
    }

    /**
     * Busca un equipo por su ID.
     * @param id ID del equipo a buscar
     * @return ResponseEntity con el equipo encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un equipo por su ID", description = "Obtiene un equipo al buscarlo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo encontrado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Equipo.class))),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado.")
    })
    public ResponseEntity<?> buscarEquipo(@Parameter(description = "ID del equipo a buscar", required = true)
                                          @PathVariable int id) {
        Equipo equipo;
        try {
            equipo = equipoService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Equipo no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(equipo);
    }

    /**
     * Crea un nuevo equipo.
     * @param equipo Datos del equipo a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo equipo", description = "Crea un nuevo equipo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipo creado con éxito."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> agregarEquipo(@RequestBody @Parameter(description = "Datos del equipo a crear", required = true)
                                                Equipo equipo) {
        try {
            equipoService.save(equipo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Equipo creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un equipo existente.
     * @param id ID del equipo a actualizar
     * @param equipo Datos actualizados del equipo
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un equipo existente", description = "Actualiza los datos de un equipo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo actualizado con éxito."),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> actualizarEquipo(@Parameter(description = "ID del equipo a actualizar", required = true)
                                                   @PathVariable Integer id,
                                                   @RequestBody @Parameter(description = "Datos actualizados del equipo", required = true)
                                                   Equipo equipo) {
        try {
            equipoService.update(equipo, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un equipo del sistema.
     * @param id ID del equipo a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un equipo", description = "Elimina un equipo del sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo eliminado con éxito."),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> eliminarEquipo(@Parameter(description = "ID del equipo a eliminar", required = true)
                                                 @PathVariable Integer id) {
        try {
            equipoService.delete(id);
            return ResponseEntity.ok("Equipo eliminada con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Equipo no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}