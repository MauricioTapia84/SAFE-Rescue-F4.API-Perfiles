package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Perfiles.repositoy.TipoEquipoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoEquipoServiceTest {

    @Mock
    private TipoEquipoRepository tipoEquipoRepository;

    @InjectMocks
    private TipoEquipoService tipoEquipoService;

    private TipoEquipo tipoEquipo;
    private Faker faker;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = faker.number().numberBetween(1, 100);

        tipoEquipo = new TipoEquipo();
        tipoEquipo.setIdTipoEquipo(id);
        tipoEquipo.setNombre(faker.job().field());
    }

    // --- Pruebas de operaciones exitosas (Happy Path) ---

    @Test
    public void findAll_shouldReturnAllTeamTypes() {
        // Arrange
        when(tipoEquipoRepository.findAll()).thenReturn(List.of(tipoEquipo));

        // Act
        List<TipoEquipo> tipos = tipoEquipoService.findAll();

        // Assert
        assertNotNull(tipos);
        assertFalse(tipos.isEmpty());
        assertEquals(1, tipos.size());
        assertEquals(tipoEquipo.getNombre(), tipos.get(0).getNombre());
        verify(tipoEquipoRepository, times(1)).findAll();
    }

    @Test
    public void findById_shouldReturnTeamType_whenFound() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.of(tipoEquipo));

        // Act
        TipoEquipo encontrado = tipoEquipoService.findById(id);

        // Assert
        assertNotNull(encontrado);
        assertEquals(tipoEquipo.getNombre(), encontrado.getNombre());
        verify(tipoEquipoRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldReturnSavedTeamType_whenValid() {
        // Arrange
        when(tipoEquipoRepository.save(any(TipoEquipo.class))).thenReturn(tipoEquipo);

        // Act
        TipoEquipo guardado = tipoEquipoService.save(tipoEquipo);

        // Assert
        assertNotNull(guardado);
        assertEquals(tipoEquipo.getNombre(), guardado.getNombre());
        verify(tipoEquipoRepository, times(1)).save(tipoEquipo);
    }

    @Test
    public void update_shouldReturnUpdatedTeamType_whenValid() {
        // Arrange
        TipoEquipo tipoEquipoActualizado = new TipoEquipo();
        tipoEquipoActualizado.setNombre("Nombre Actualizado");

        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.of(tipoEquipo));
        when(tipoEquipoRepository.save(any(TipoEquipo.class))).thenReturn(tipoEquipoActualizado);

        // Act
        TipoEquipo actualizado = tipoEquipoService.update(tipoEquipoActualizado, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals("Nombre Actualizado", actualizado.getNombre());
        verify(tipoEquipoRepository, times(1)).findById(id);
        verify(tipoEquipoRepository, times(1)).save(tipoEquipo);
    }

    @Test
    public void delete_shouldDeleteTeamType_whenExists() {
        // Arrange
        when(tipoEquipoRepository.existsById(id)).thenReturn(true);
        doNothing().when(tipoEquipoRepository).deleteById(id);

        // Act & Assert
        assertDoesNotThrow(() -> tipoEquipoService.delete(id));
        verify(tipoEquipoRepository, times(1)).existsById(id);
        verify(tipoEquipoRepository, times(1)).deleteById(id);
    }

    // --- Pruebas de escenarios de error ---

    @Test
    public void findById_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> tipoEquipoService.findById(id));
        verify(tipoEquipoRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldThrowException_whenNameIsNull() {
        // Arrange
        tipoEquipo.setNombre(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.save(tipoEquipo));
        verify(tipoEquipoRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenNameIsTooLong() {
        // Arrange
        tipoEquipo.setNombre(faker.lorem().characters(51));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.save(tipoEquipo));
        verify(tipoEquipoRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenDataIntegrityViolation() {
        // Arrange
        when(tipoEquipoRepository.save(any(TipoEquipo.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.save(tipoEquipo));
        verify(tipoEquipoRepository, times(1)).save(tipoEquipo);
    }

    @Test
    public void update_shouldThrowException_whenTeamTypeNotFound() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> tipoEquipoService.update(tipoEquipo, id));
        verify(tipoEquipoRepository, times(1)).findById(id);
        verify(tipoEquipoRepository, never()).save(any());
    }

    @Test
    public void update_shouldThrowException_whenNameIsNull() {
        // Arrange
        tipoEquipo.setNombre(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.update(tipoEquipo, id));
        verify(tipoEquipoRepository, never()).findById(any());
        verify(tipoEquipoRepository, never()).save(any());
    }

    @Test
    public void update_shouldThrowException_whenDataIntegrityViolation() {
        // Arrange
        when(tipoEquipoRepository.findById(id)).thenReturn(Optional.of(tipoEquipo));
        when(tipoEquipoRepository.save(any(TipoEquipo.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoEquipoService.update(tipoEquipo, id));
        verify(tipoEquipoRepository, times(1)).findById(id);
        verify(tipoEquipoRepository, times(1)).save(tipoEquipo);
    }

    @Test
    public void delete_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoEquipoRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> tipoEquipoService.delete(id));
        verify(tipoEquipoRepository, times(1)).existsById(id);
        verify(tipoEquipoRepository, never()).deleteById(any());
    }
}