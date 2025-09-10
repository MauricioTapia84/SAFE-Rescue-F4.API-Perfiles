package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.config.WebClienteConfig;
import com.SAFE_Rescue.API_Perfiles.modelo.Estado;
import com.SAFE_Rescue.API_Perfiles.modelo.Foto;
import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.modelo.Usuario;
import com.SAFE_Rescue.API_Perfiles.repositoy.UsuarioRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoUsuarioService tipoUsuarioService;

    @Mock
    private WebClient estadoWebClient;

    @Mock
    private WebClienteConfig webClienteConfig;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RequestBodySpec requestBodySpec;
    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Faker faker;
    private Integer id;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        id = faker.number().numberBetween(1, 100);

        // Crear objetos de dependencia
        TipoUsuario tipoUsuario = new TipoUsuario(1, "Bombero");
        Estado estado = new Estado(1, "Activo", "Descripción");

        // Crear objeto Usuario con datos simulados
        usuario = new Usuario();
        usuario.setIdUsuario(id);
        usuario.setRun(faker.idNumber().valid());
        usuario.setDv("1");
        usuario.setNombre(faker.name().firstName());
        usuario.setAPaterno(faker.name().lastName());
        usuario.setAMaterno(faker.name().lastName());
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setTelefono(faker.phoneNumber().cellPhone());
        usuario.setCorreo(faker.internet().emailAddress());
        usuario.setContrasenia(faker.internet().password());
        usuario.setIntentosFallidos(0);
        usuario.setRazonBaneo(null);
        usuario.setDiasBaneo(0);
        usuario.setTipoUsuario(tipoUsuario);
        usuario.setEstado(estado);
        usuario.setFoto(new Foto()); // Inicializar el objeto Foto para evitar NullPointerException
    }

    // --- Pruebas de operaciones CRUD exitosas ---

    @Test
    public void findAll_shouldReturnAllUsers() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        List<Usuario> usuarios = usuarioService.findAll();
        assertNotNull(usuarios);
        assertFalse(usuarios.isEmpty());
        assertEquals(1, usuarios.size());
        assertEquals(usuario.getNombre(), usuarios.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    public void findById_shouldReturnUser_whenUserExists() {
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        Usuario encontrado = usuarioService.findById(id);
        assertNotNull(encontrado);
        assertEquals(usuario.getNombre(), encontrado.getNombre());
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldReturnSavedUser_whenValid() {
        // Arrange
        when(tipoUsuarioService.findById(any())).thenReturn(usuario.getTipoUsuario());

        // Simular la llamada a la API externa para el estado
        when(estadoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestBodySpec); // <-- Corregido
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Act
        Usuario guardado = usuarioService.save(usuario);

        // Assert
        assertNotNull(guardado);
        assertEquals(usuario.getNombre(), guardado.getNombre());
        verify(usuarioRepository, times(1)).save(usuario);
        verify(tipoUsuarioService, times(1)).findById(usuario.getTipoUsuario().getIdTipoUsuario());
        verify(estadoWebClient, times(1)).get();
    }

    @Test
    public void update_shouldReturnUpdatedUser_whenUserExists() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setIdUsuario(id);
        usuarioExistente.setNombre("Nombre Antiguo");
        usuarioExistente.setFoto(new Foto()); // Inicializar foto para evitar NullPointerException

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(tipoUsuarioService.findById(any())).thenReturn(usuario.getTipoUsuario());

        // Simular llamada a WebClient para el estado
        when(estadoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestBodySpec); // <-- Corregido
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Act
        Usuario actualizado = usuarioService.update(usuario, id);

        // Assert
        assertNotNull(actualizado);
        assertEquals(usuario.getNombre(), actualizado.getNombre());
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(usuarioExistente);
        verify(tipoUsuarioService, times(1)).findById(usuario.getTipoUsuario().getIdTipoUsuario());
        verify(estadoWebClient, times(1)).get();
    }

    @Test
    public void delete_shouldDeleteUser_whenUserExists() {
        when(usuarioRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> usuarioService.delete(id));
        verify(usuarioRepository, times(1)).existsById(id);
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    // --- Pruebas de escenarios de error ---

    @Test
    public void findById_shouldThrowException_whenUserNotFound() {
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> usuarioService.findById(id));
        verify(usuarioRepository, times(1)).findById(id);
    }

    @Test
    public void save_shouldThrowException_whenUserIsNull() {
        assertThrows(IllegalArgumentException.class, () -> usuarioService.save(null));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenDataIntegrityViolation() {
        // Arrange
        when(tipoUsuarioService.findById(any())).thenReturn(usuario.getTipoUsuario());

        // Simular llamada a WebClient
        when(estadoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestBodySpec); // <-- Corregido
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new DataIntegrityViolationException("RUN o correo duplicado"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> usuarioService.save(usuario));
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    public void save_shouldThrowException_whenTipoUsuarioNotFound() {
        when(tipoUsuarioService.findById(any())).thenThrow(new NoSuchElementException());
        assertThrows(IllegalArgumentException.class, () -> usuarioService.save(usuario));
        verify(tipoUsuarioService, times(1)).findById(usuario.getTipoUsuario().getIdTipoUsuario());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    public void save_shouldThrowException_whenEstadoNotFound() {
        when(tipoUsuarioService.findById(any())).thenReturn(usuario.getTipoUsuario());

        // Simular respuesta 404 (NotFound) de la API externa
        when(estadoWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestBodySpec); // <-- Corregido
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenThrow(new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> usuarioService.save(usuario));
        verify(tipoUsuarioService, times(1)).findById(usuario.getTipoUsuario().getIdTipoUsuario());
        verify(usuarioRepository, never()).save(any());
    }

    // --- Pruebas del método de subir foto ---

    @Test
    public void subirYActualizarFotoUsuario_shouldUpdateUserWithPhotoUrl() {
        // Arrange
        String mockPhotoUrl = "http://api.mock.com/photos/123";
        MultipartFile mockFile = mock(MultipartFile.class);
        when(webClienteConfig.uploadFoto(mockFile)).thenReturn(mockPhotoUrl);

        // El objeto usuario del setUp ya tiene una foto, lo que resuelve el NullPointerException
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        String returnedUrl = usuarioService.subirYActualizarFotoUsuario(id, mockFile);

        // Assert
        assertEquals(mockPhotoUrl, returnedUrl);
        verify(webClienteConfig, times(1)).uploadFoto(mockFile);
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    public void subirYActualizarFotoUsuario_shouldThrowException_whenUserNotFound() {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> usuarioService.subirYActualizarFotoUsuario(id, mockFile));
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, never()).save(any());
    }
}