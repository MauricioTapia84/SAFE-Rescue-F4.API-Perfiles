package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.Compania;
import com.SAFE_Rescue.API_Perfiles.modelo.Equipo;
import com.SAFE_Rescue.API_Perfiles.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Perfiles.repositoy.EquipoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipoServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private WebClient companiaWebClient;

    @Mock
    private TipoEquipoService tipoEquipoService;

    // Mocks para simular la cadena de llamadas de WebClient
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EquipoService equipoService;

    private Equipo equipo;
    private Faker faker;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = faker.number().numberBetween(1, 100);

        // Crear objetos de dependencia
        TipoEquipo tipoEquipo = new TipoEquipo(1, "Rescate Urbano");
        Compania compania = new Compania(1, "Primera Compañía"); // Asume que la Compañía tiene un ID

        // Crear objeto Equipo con datos simulados
        equipo = new Equipo();
        equipo.setIdEquipo(id);
        equipo.setNombre(faker.team().name());
        equipo.setTipoEquipo(tipoEquipo);
        equipo.setCompania(compania);
        equipo.setLider(null); // No es relevante para estas pruebas
    }

    // --- Pruebas de operaciones exitosas ---

    @Test
    public void findAll_shouldReturnAllTeams() {
        // Arrange
        when(equipoRepository.findAll()).thenReturn(List.of(equipo));

        // Act
        List<Equipo> equipos = equipoService.findAll();

        // Assert
        assertNotNull(equipos);
        assertFalse(equipos.isEmpty());
        assertEquals(1, equipos.size());
        assertEquals(equipo.getNombre(), equipos.get(0).getNombre());
        verify(equipoRepository, times(1)).findAll();
    }

    @Test
    public void findById_shouldReturnTeam_whenTeamExists() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.of(equipo));

        // Act
        Equipo encontrado = equipoService.findById(id);

        // Assert
        assertNotNull(encontrado);
        assertEquals(equipo.getNombre(), encontrado.getNombre());
        verify(equipoRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldReturnSavedTeam_whenValid() {
        // Arrange
        when(tipoEquipoService.findById(any())).thenReturn(equipo.getTipoEquipo());

        // Simular la llamada exitosa a la API externa
        when(companiaWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipo);

        // Act
        Equipo guardado = equipoService.save(equipo);

        // Assert
        assertNotNull(guardado);
        assertEquals(equipo.getNombre(), guardado.getNombre());
        verify(equipoRepository, times(1)).save(equipo);
        verify(tipoEquipoService, times(1)).findById(equipo.getTipoEquipo().getIdTipoEquipo());
        verify(companiaWebClient, times(1)).get();
    }

    @Test
    public void update_shouldReturnUpdatedTeam_whenTeamExists() {
        // Arrange
        Equipo equipoExistente = new Equipo();
        equipoExistente.setIdEquipo(id);
        equipoExistente.setNombre("Nombre Antiguo");

        when(equipoRepository.findById(id)).thenReturn(Optional.of(equipoExistente));
        when(tipoEquipoService.findById(any())).thenReturn(equipo.getTipoEquipo());

        // Simular la llamada exitosa a la API externa
        when(companiaWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        when(equipoRepository.save(any(Equipo.class))).thenReturn(equipo);

        // Act
        Equipo actualizado = equipoService.update(equipo, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals(equipo.getNombre(), actualizado.getNombre());
        verify(equipoRepository, times(1)).findById(id);
        verify(equipoRepository, times(1)).save(equipoExistente);
        verify(tipoEquipoService, times(1)).findById(equipo.getTipoEquipo().getIdTipoEquipo());
        verify(companiaWebClient, times(1)).get();
    }

    @Test
    public void delete_shouldDeleteTeam_whenTeamExists() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.of(equipo));
        doNothing().when(equipoRepository).delete(any(Equipo.class));

        // Act
        assertDoesNotThrow(() -> equipoService.delete(id));

        // Assert
        verify(equipoRepository, times(1)).findById(id);
        verify(equipoRepository, times(1)).delete(equipo);
    }

    // --- Pruebas de escenarios de error ---

    @Test
    public void findById_shouldThrowException_whenTeamNotFound() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> equipoService.findById(id));
        verify(equipoRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldThrowException_whenTeamIsNull() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.save(null));
        verify(equipoRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenInvalidAttributes() {
        // Arrange
        equipo.setNombre(""); // Atributo inválido

        // Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.save(equipo));
        verify(equipoRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenTipoEquipoNotFound() {
        // Arrange
        when(tipoEquipoService.findById(any())).thenThrow(new NoSuchElementException());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.save(equipo));
        verify(tipoEquipoService, times(1)).findById(equipo.getTipoEquipo().getIdTipoEquipo());
        verify(equipoRepository, never()).save(any());
    }

    // Corrected test code for EquipoServiceTest
// ...
    @Test
    public void save_shouldThrowException_whenExternalCompanyNotFound() {
        // Arrange
        when(tipoEquipoService.findById(any())).thenReturn(equipo.getTipoEquipo());

        // Simular la cadena de WebClient
        when(companiaWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        // Configurar onStatus para que lance la excepción esperada.
        // Usamos thenAnswer para simular el comportamiento de `onStatus` que lanza una excepción basada en el estado.
        when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
            // La excepción se lanza aquí, el código no continuará a bodyToMono
            throw new IllegalArgumentException("La compañía asociada al equipo no existe en la API externa.");
        });

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> equipoService.save(equipo));

        // Verify
        verify(equipoRepository, never()).save(any());
    }


    @Test
    public void update_shouldThrowException_whenTeamNotFound() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> equipoService.update(equipo, id));
        verify(equipoRepository, times(1)).findById(id);
        verify(equipoRepository, never()).save(any());
    }

    @Test
    public void delete_shouldThrowException_whenTeamNotFound() {
        // Arrange
        when(equipoRepository.findById(id)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NoSuchElementException.class, () -> equipoService.delete(id));
        verify(equipoRepository, times(1)).findById(id);
        verify(equipoRepository, never()).delete(any());
    }
}