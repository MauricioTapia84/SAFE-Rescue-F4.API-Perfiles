package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.Bombero;
import com.SAFE_Rescue.API_Perfiles.modelo.Equipo;
import com.SAFE_Rescue.API_Perfiles.modelo.Estado;
import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.repositoy.BomberoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BomberoServiceTest {

    @Mock
    private BomberoRepository bomberoRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private EquipoService equipoService;

    @InjectMocks
    private BomberoService bomberoService;

    private Bombero bombero;
    private Faker faker;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = faker.number().numberBetween(1, 100);

        // Objetos de dependencia para la prueba
        Estado estado = new Estado(1, "Activo", "Descripción");
        TipoUsuario tipoUsuario = new TipoUsuario(1, "Bombero");
        Equipo equipo = new Equipo(1, "Equipo 1", null, null, null, null);

        // Crear el objeto Bombero con datos simulados
        bombero = new Bombero();
        bombero.setIdUsuario(id);
        bombero.setRun(String.valueOf(faker.number().numberBetween(1000000, 99999999)));
        bombero.setDv(String.valueOf(faker.code().hashCode()));
        bombero.setNombre(faker.name().firstName());
        bombero.setAPaterno(faker.name().lastName());
        bombero.setAMaterno(faker.name().lastName());
        bombero.setFechaRegistro(LocalDate.now());
        bombero.setTelefono(faker.phoneNumber().phoneNumber());
        bombero.setCorreo(faker.internet().emailAddress());
        bombero.setContrasenia(faker.internet().password());
        bombero.setIntentosFallidos(0);
        bombero.setRazonBaneo(null);
        bombero.setDiasBaneo(0);
        bombero.setTipoUsuario(tipoUsuario);
        bombero.setEstado(estado);
        bombero.setEquipo(equipo);
    }

    // --- Pruebas de operaciones exitosas ---

    @Test
    public void findAllTest() {
        when(bomberoRepository.findAll()).thenReturn(List.of(bombero));
        List<Bombero> bomberos = bomberoService.findAll();
        assertNotNull(bomberos);
        assertFalse(bomberos.isEmpty());
        assertEquals(1, bomberos.size());
        assertEquals(bombero.getNombre(), bomberos.get(0).getNombre());
        verify(bomberoRepository, times(1)).findAll();
    }

    @Test
    public void findByIdTest() {
        when(bomberoRepository.findById(id)).thenReturn(Optional.of(bombero));
        Bombero encontrado = bomberoService.findById(id);
        assertNotNull(encontrado);
        assertEquals(bombero.getNombre(), encontrado.getNombre());
        verify(bomberoRepository, times(1)).findById(id);
    }

    @Test
    public void saveTest() {
        // Simular dependencias
        doNothing().when(usuarioService).validarAtributosUsuario(any(Bombero.class));
        when(equipoService.findById(any())).thenReturn(bombero.getEquipo()); // Simular que encuentra el equipo
        when(bomberoRepository.save(bombero)).thenReturn(bombero);

        // Ejecutar y verificar
        Bombero guardado = bomberoService.save(bombero);
        assertNotNull(guardado);
        assertEquals(bombero.getNombre(), guardado.getNombre());
        verify(bomberoRepository, times(1)).save(bombero);
        verify(usuarioService, times(1)).validarAtributosUsuario(bombero);
        verify(equipoService, times(1)).findById(bombero.getEquipo().getIdEquipo());
    }

    @Test
    public void updateTest() {
        // Preparar un bombero existente para la simulación
        Bombero bomberoExistente = new Bombero();
        bomberoExistente.setIdUsuario(id);
        bomberoExistente.setNombre("Nombre Antiguo");

        // Simular dependencias
        when(bomberoRepository.findById(id)).thenReturn(Optional.of(bomberoExistente));
        when(equipoService.findById(any())).thenReturn(bombero.getEquipo()); // Simular que encuentra el equipo
        doNothing().when(usuarioService).validarAtributosUsuario(any(Bombero.class));
        when(bomberoRepository.save(any(Bombero.class))).thenReturn(bombero);

        // Ejecutar y verificar
        Bombero actualizado = bomberoService.update(bombero, id);
        assertNotNull(actualizado);
        assertEquals(bombero.getNombre(), actualizado.getNombre());
        verify(bomberoRepository, times(1)).findById(id);
        verify(equipoService, times(1)).findById(bombero.getEquipo().getIdEquipo());
        verify(bomberoRepository, times(1)).save(bomberoExistente);
    }

    @Test
    public void deleteTest() {
        when(bomberoRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> bomberoService.delete(id));
        verify(bomberoRepository, times(1)).deleteById(id);
    }

    // --- Pruebas de escenarios de error ---

    @Test
    public void findByIdTest_BomberoNoEncontrado() {
        when(bomberoRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> bomberoService.findById(id));
    }

    @Test
    public void saveTest_ObjetoNulo() {
        assertThrows(IllegalArgumentException.class, () -> bomberoService.save(null));
    }

    @Test
    public void saveTest_ErrorIntegridadDatos() {
        doNothing().when(usuarioService).validarAtributosUsuario(any());
        when(equipoService.findById(any())).thenReturn(bombero.getEquipo());
        doThrow(new DataIntegrityViolationException("Duplicado")).when(bomberoRepository).save(any());
        assertThrows(IllegalArgumentException.class, () -> bomberoService.save(bombero));
    }

    @Test
    public void saveTest_EquipoNoExistente() {
        doNothing().when(usuarioService).validarAtributosUsuario(any(Bombero.class));
        when(equipoService.findById(any())).thenThrow(new NoSuchElementException());
        assertThrows(IllegalArgumentException.class, () -> bomberoService.save(bombero));
    }

    @Test
    public void updateTest_BomberoNoEncontrado() {
        when(bomberoRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> bomberoService.update(bombero, id));
    }

    @Test
    public void updateTest_ErrorIntegridadDatos() {
        when(bomberoRepository.findById(id)).thenReturn(Optional.of(new Bombero()));
        doNothing().when(usuarioService).validarAtributosUsuario(any());
        when(equipoService.findById(any())).thenReturn(bombero.getEquipo());
        doThrow(new DataIntegrityViolationException("Duplicado")).when(bomberoRepository).save(any());
        assertThrows(IllegalArgumentException.class, () -> bomberoService.update(bombero, id));
    }

    @Test
    public void updateTest_ObjetoNulo() {
        assertThrows(IllegalArgumentException.class, () -> bomberoService.update(null, id));
    }

    @Test
    public void deleteTest_BomberoNoExistente() {
        when(bomberoRepository.existsById(id)).thenReturn(false);
        assertThrows(NoSuchElementException.class, () -> bomberoService.delete(id));
        verify(bomberoRepository, never()).deleteById(any());
    }
}