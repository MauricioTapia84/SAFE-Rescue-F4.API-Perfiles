package com.SAFE_Rescue.API_Perfiles.controller;

import com.SAFE_Rescue.API_Perfiles.modelo.Bombero;
import com.SAFE_Rescue.API_Perfiles.service.BomberoService;
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
 * Controlador REST para la gestión de bomberos
 * Proporciona endpoints para operaciones CRUD y gestión de relaciones de bomberos
 */
@RestController
@RequestMapping("/api-administrador/v1/bomberos")
@Tag(name = "Bomberos", description = "Operaciones de CRUD relacionadas con Bomberos")
public class BomberoController {

    @Autowired
    private BomberoService bomberoService;

    // OPERACIONES CRUD BÁSICAS

    /**
     * Obtiene todos los bomberos registrados en el sistema.
     * @return ResponseEntity con lista de bomberos o estado NO_CONTENT si no hay registros
     */
    @GetMapping
    @Operation(summary = "Obtener todos los bomberos", description = "Obtiene una lista con todos los bomberos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bomberos obtenida exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Bombero.class))),
            @ApiResponse(responseCode = "204", description = "No hay bomberos registrados.")
    })
    public ResponseEntity<List<Bombero>> listar() {
        List<Bombero> bomberos = bomberoService.findAll();
        if (bomberos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(bomberos);
    }

    /**
     * Busca un bombero por su ID.
     * @param id ID del bombero a buscar
     * @return ResponseEntity con el bombero encontrado o mensaje de error
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un bombero por su ID", description = "Obtiene un bombero al buscarlo por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bombero encontrado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Bombero.class))),
            @ApiResponse(responseCode = "404", description = "Bombero no encontrado.")
    })
    public ResponseEntity<?> buscarBombero(@Parameter(description = "ID del bombero a buscar", required = true)
                                           @PathVariable int id) {
        Bombero bombero;
        try {
            bombero = bomberoService.findById(id);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Bombero no encontrado", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bombero);
    }

    /**
     * Crea un nuevo bombero.
     * @param bombero Datos del bombero a crear
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo bombero", description = "Crea un nuevo bombero en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bombero creado con éxito."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> agregarBombero(@RequestBody @Parameter(description = "Datos del bombero a crear", required = true)
                                                 Bombero bombero) {
        try {
            bomberoService.save(bombero);
            return ResponseEntity.status(HttpStatus.CREATED).body("Bombero creado con éxito.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Actualiza un bombero existente.
     * @param id ID del bombero a actualizar
     * @param bombero Datos actualizados del bombero
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un bombero existente", description = "Actualiza los datos de un bombero por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bombero actualizado con éxito."),
            @ApiResponse(responseCode = "404", description = "Bombero no encontrado."),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> actualizarBombero(@Parameter(description = "ID del bombero a actualizar", required = true)
                                                    @PathVariable Integer id,
                                                    @RequestBody @Parameter(description = "Datos actualizados del bombero", required = true)
                                                    Bombero bombero) {
        try {
            bomberoService.update(bombero, id);
            return ResponseEntity.ok("Actualizado con éxito");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bombero no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }

    /**
     * Elimina un bombero del sistema.
     * @param id ID del bombero a eliminar
     * @return ResponseEntity con mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un bombero", description = "Elimina un bombero del sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bombero eliminado con éxito."),
            @ApiResponse(responseCode = "404", description = "Bombero no encontrado."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.")
    })
    public ResponseEntity<String> eliminarBombero(@Parameter(description = "ID del bombero a eliminar", required = true)
                                                  @PathVariable Integer id) {
        try {
            bomberoService.delete(id);
            return ResponseEntity.ok("Bombero eliminada con éxito.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bombero no encontrada");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
        }
    }
}