package com.SAFE_Rescue.API_Perfiles.controller;

import com.SAFE_Rescue.API_Perfiles.modelo.Bombero;
import com.SAFE_Rescue.API_Perfiles.modelo.Estado;
import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.service.BomberoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BomberoController.class)
public class BomberoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BomberoService bomberoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Faker faker;
    private Bombero bombero;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = 1;

        // Register the module for Java 8 Date and Time API.
        objectMapper.registerModule(new JavaTimeModule());

        // Create the Bombero object.
        bombero = new Bombero();
        bombero.setIdUsuario(id);
        bombero.setRun(String.valueOf(faker.number().numberBetween(10000000, 99999999)));
        bombero.setDv("9");
        bombero.setNombre(faker.name().firstName());
        bombero.setAPaterno(faker.name().lastName());
        bombero.setAMaterno(faker.name().lastName());
        bombero.setFechaRegistro(LocalDate.now()); // Correctly using LocalDate
        bombero.setTelefono(String.valueOf(faker.number().numberBetween(100000000, 999999999)));
        bombero.setCorreo(faker.internet().emailAddress());
        bombero.setContrasenia(faker.internet().password());
        bombero.setIntentosFallidos(0);
        bombero.setEstado(new Estado(1, "Activo", "Descripción"));
        bombero.setTipoUsuario(new TipoUsuario(1, "Bombero"));
    }

    @Test
    public void listarTest() throws Exception {
        when(bomberoService.findAll()).thenReturn(List.of(bombero));

        mockMvc.perform(get("/api-perfiles/v1/bomberos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(bombero.getIdUsuario()))
                .andExpect(jsonPath("$[0].run").value(bombero.getRun()));
    }

    @Test
    public void buscarBomberoTest() throws Exception {
        when(bomberoService.findById(id)).thenReturn(bombero);

        mockMvc.perform(get("/api-perfiles/v1/bomberos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(bombero.getIdUsuario()))
                .andExpect(jsonPath("$.run").value(bombero.getRun()));
    }

    @Test
    public void agregarBomberoTest() throws Exception {
        when(bomberoService.save(any(Bombero.class))).thenReturn(bombero);

        mockMvc.perform(post("/api-perfiles/v1/bomberos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bombero)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Bombero creado con éxito."));
    }

    @Test
    public void actualizarBomberoTest() throws Exception {
        when(bomberoService.update(any(Bombero.class), eq(id))).thenReturn(bombero);

        mockMvc.perform(put("/api-perfiles/v1/bomberos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bombero)))
                .andExpect(status().isOk())
                .andExpect(content().string("Actualizado con éxito"));
    }

    @Test
    public void eliminarBomberoTest() throws Exception {
        doNothing().when(bomberoService).delete(id);

        mockMvc.perform(delete("/api-perfiles/v1/bomberos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Bombero eliminada con éxito."));
    }

    // --- Pruebas de escenarios de error ---

    @Test
    public void listarTest_BomberosNoExistentes() throws Exception {
        when(bomberoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api-perfiles/v1/bomberos"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void buscarBomberoTest_BomberoNoExistente() throws Exception {
        when(bomberoService.findById(id)).thenThrow(new NoSuchElementException("Bombero no encontrado"));

        mockMvc.perform(get("/api-perfiles/v1/bomberos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Bombero no encontrado"));
    }

    @Test
    public void agregarBomberoTest_Error() throws Exception {
        when(bomberoService.save(any(Bombero.class))).thenThrow(new RuntimeException("Error al crear el bombero"));

        mockMvc.perform(post("/api-perfiles/v1/bomberos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bombero)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear el bombero"));
    }

    @Test
    public void actualizarBomberoTest_BomberoNoExistente() throws Exception {
        when(bomberoService.update(any(Bombero.class), eq(id))).thenThrow(new NoSuchElementException("Bombero no encontrado"));

        mockMvc.perform(put("/api-perfiles/v1/bomberos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bombero)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Bombero no encontrado"));
    }

    @Test
    public void eliminarBomberoTest_BomberoNoExistente() throws Exception {
        doThrow(new NoSuchElementException("Bombero no encontrada")).when(bomberoService).delete(id);

        mockMvc.perform(delete("/api-perfiles/v1/bomberos/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Bombero no encontrada"));
    }
}