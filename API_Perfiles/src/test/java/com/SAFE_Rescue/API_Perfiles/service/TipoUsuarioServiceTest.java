package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.repositoy.TipoUsuarioRepository;
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
public class TipoUsuarioServiceTest {

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @InjectMocks
    private TipoUsuarioService tipoUsuarioService;

    private TipoUsuario tipoUsuario;
    private Faker faker;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = faker.number().numberBetween(1, 100);

        tipoUsuario = new TipoUsuario();
        tipoUsuario.setIdTipoUsuario(id);
        tipoUsuario.setNombre(faker.job().position());
    }

    // --- Pruebas de operaciones exitosas (Happy Path) ---

    @Test
    public void findAll_shouldReturnAllUserTypes() {
        // Arrange
        when(tipoUsuarioRepository.findAll()).thenReturn(List.of(tipoUsuario));

        // Act
        List<TipoUsuario> tipos = tipoUsuarioService.findAll();

        // Assert
        assertNotNull(tipos);
        assertFalse(tipos.isEmpty());
        assertEquals(1, tipos.size());
        assertEquals(tipoUsuario.getNombre(), tipos.get(0).getNombre());
        verify(tipoUsuarioRepository, times(1)).findAll();
    }

    @Test
    public void findById_shouldReturnUserType_whenFound() {
        // Arrange
        when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.of(tipoUsuario));

        // Act
        TipoUsuario encontrado = tipoUsuarioService.findById(id);

        // Assert
        assertNotNull(encontrado);
        assertEquals(tipoUsuario.getNombre(), encontrado.getNombre());
        verify(tipoUsuarioRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldReturnSavedUserType_whenValid() {
        // Arrange
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(tipoUsuario);

        // Act
        TipoUsuario guardado = tipoUsuarioService.save(tipoUsuario);

        // Assert
        assertNotNull(guardado);
        assertEquals(tipoUsuario.getNombre(), guardado.getNombre());
        verify(tipoUsuarioRepository, times(1)).save(tipoUsuario);
    }

    @Test
    public void update_shouldReturnUpdatedUserType_whenValid() {
        // Arrange
        TipoUsuario tipoUsuarioActualizado = new TipoUsuario();
        tipoUsuarioActualizado.setNombre("Nombre Actualizado");

        when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.of(tipoUsuario));
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenReturn(tipoUsuarioActualizado);

        // Act
        TipoUsuario actualizado = tipoUsuarioService.update(tipoUsuarioActualizado, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals("Nombre Actualizado", actualizado.getNombre());
        verify(tipoUsuarioRepository, times(1)).findById(id);
        verify(tipoUsuarioRepository, times(1)).save(tipoUsuario);
    }

    @Test
    public void delete_shouldDeleteUserType_whenExists() {
        // Arrange
        when(tipoUsuarioRepository.existsById(id)).thenReturn(true);
        doNothing().when(tipoUsuarioRepository).deleteById(id);

        // Act & Assert
        assertDoesNotThrow(() -> tipoUsuarioService.delete(id));
        verify(tipoUsuarioRepository, times(1)).existsById(id);
        verify(tipoUsuarioRepository, times(1)).deleteById(id);
    }

    // --- Pruebas de escenarios de error ---

    @Test
    public void findById_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> tipoUsuarioService.findById(id));
        verify(tipoUsuarioRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldThrowException_whenNameIsNull() {
        // Arrange
        tipoUsuario.setNombre(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoUsuarioService.save(tipoUsuario));
        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenNameIsTooLong() {
        // Arrange
        tipoUsuario.setNombre(faker.lorem().characters(51));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoUsuarioService.save(tipoUsuario));
        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenDataIntegrityViolation() {
        // Arrange
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoUsuarioService.save(tipoUsuario));
        verify(tipoUsuarioRepository, times(1)).save(tipoUsuario);
    }

    @Test
    public void update_shouldThrowException_whenUserTypeNotFound() {
        // Arrange
        when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> tipoUsuarioService.update(tipoUsuario, id));
        verify(tipoUsuarioRepository, times(1)).findById(id);
        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    public void update_shouldThrowException_whenNameIsNull() {
        // Arrange
        tipoUsuario.setNombre(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoUsuarioService.update(tipoUsuario, id));
        verify(tipoUsuarioRepository, never()).findById(any()); // No se llama a findById porque falla en la validación inicial
        verify(tipoUsuarioRepository, never()).save(any());
    }

    @Test
    public void update_shouldThrowException_whenDataIntegrityViolation() {
        // Arrange
        when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.of(tipoUsuario));
        when(tipoUsuarioRepository.save(any(TipoUsuario.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tipoUsuarioService.update(tipoUsuario, id));
        verify(tipoUsuarioRepository, times(1)).findById(id);
        verify(tipoUsuarioRepository, times(1)).save(tipoUsuario);
    }

    @Test
    public void delete_shouldThrowException_whenNotFound() {
        // Arrange
        when(tipoUsuarioRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> tipoUsuarioService.delete(id));
        verify(tipoUsuarioRepository, times(1)).existsById(id);
        verify(tipoUsuarioRepository, never()).deleteById(any());
    }
}