package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.config.WebClienteConfig;
import com.SAFE_Rescue.API_Perfiles.modelo.Usuario;
import com.SAFE_Rescue.API_Perfiles.repositoy.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión de usuarios.
 * Maneja operaciones CRUD y validaciones de negocio.
 */
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WebClient estadoWebClient;

    @Autowired
    private TipoUsuarioService tipoUsuarioService;

    @Autowired
    private WebClienteConfig webClienteConfig;

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     *
     * @return Lista de todos los usuarios.
     */
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Busca un usuario por su ID único.
     *
     * @param id El ID del usuario.
     * @return El usuario encontrado.
     * @throws NoSuchElementException Si el usuario no es encontrado.
     */
    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto Usuario a guardar.
     * @return El usuario guardado.
     * @throws IllegalArgumentException Si el usuario no cumple con las validaciones o si las entidades relacionadas no existen.
     */
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        validarAtributosUsuario(usuario);
        validarExistencia(usuario);

        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El RUN o correo electrónico ya existen.");
        }
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuario El objeto Usuario con los datos actualizados.
     * @param id      El ID del usuario a actualizar.
     * @return El usuario actualizado.
     * @throws IllegalArgumentException Si los datos del usuario son inválidos o si las entidades relacionadas no existen.
     * @throws NoSuchElementException   Si el usuario a actualizar no es encontrado.
     */
    public Usuario update(Usuario usuario, Integer id) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario a actualizar no puede ser nulo.");
        }

        // Se valida el objeto usuario, incluyendo la existencia de sus relaciones
        validarAtributosUsuario(usuario);
        validarExistencia(usuario);

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));

        // Actualizar los campos del usuario existente con los nuevos valores
        usuarioExistente.setRun(usuario.getRun());
        usuarioExistente.setDv(usuario.getDv());
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setAPaterno(usuario.getAPaterno());
        usuarioExistente.setAMaterno(usuario.getAMaterno());
        usuarioExistente.setFechaRegistro(usuario.getFechaRegistro());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setContrasenia(usuario.getContrasenia());
        usuarioExistente.setIntentosFallidos(usuario.getIntentosFallidos());
        usuarioExistente.setRazonBaneo(usuario.getRazonBaneo());
        usuarioExistente.setDiasBaneo(usuario.getDiasBaneo());
        usuarioExistente.setTipoUsuario(usuario.getTipoUsuario());
        usuarioExistente.setEstado(usuario.getEstado());

        try {
            return usuarioRepository.save(usuarioExistente);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El RUN, teléfono o correo ya existen.");
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id El ID del usuario a eliminar.
     * @throws NoSuchElementException Si el usuario no es encontrado.
     */
    public void delete(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // Métodos de validación y utilidades

    /**
     * Valida los atributos obligatorios del usuario.
     *
     * @param usuario El objeto Usuario a validar.
     * @throws IllegalArgumentException Si algún atributo es nulo o no cumple las reglas de negocio.
     */
    public void validarAtributosUsuario(Usuario usuario) {
        if (usuario.getRun() == null || usuario.getRun().trim().isEmpty() ||
                usuario.getDv() == null || usuario.getDv().trim().isEmpty() ||
                usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
                usuario.getAPaterno() == null || usuario.getAPaterno().trim().isEmpty() ||
                usuario.getAMaterno() == null || usuario.getAMaterno().trim().isEmpty() ||
                usuario.getFechaRegistro() == null ||
                usuario.getTelefono() == null || usuario.getTelefono().trim().isEmpty() ||
                usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty() ||
                usuario.getContrasenia() == null || usuario.getContrasenia().trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos obligatorios del usuario deben ser proporcionados.");
        }
    }

    /**
     * Valida que las entidades relacionadas (Estado y TipoUsuario) existan.
     * Se comunica con la API externa para validar la existencia del estado.
     *
     * @param usuario El objeto Usuario a validar.
     * @throws IllegalArgumentException Si alguna de las entidades relacionadas no existe.
     */
    private void validarExistencia(Usuario usuario) {
        // Valida la existencia del Tipo de Usuario (local)
        if (usuario.getTipoUsuario() != null) {
            try {
                tipoUsuarioService.findById(usuario.getTipoUsuario().getIdTipoUsuario());
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("El tipo de usuario asociado no existe.");
            }
        } else {
            throw new IllegalArgumentException("El tipo de usuario es un campo obligatorio.");
        }

        if (usuario.getEstado() != null) {
            try {
                // Llama y espera la respuesta. El onStatus es redundante si capturas la excepción.
                estadoWebClient.get()
                        .uri("/{id}", usuario.getEstado().getIdEstado())
                        .retrieve()
                        .toBodilessEntity() // Llama al servicio sin importar el cuerpo.
                        .block();
            } catch (WebClientResponseException.NotFound e) {
                // Captura el error 404 (Not Found)
                throw new IllegalArgumentException("El estado asociado al usuario no existe en la API externa.", e);
            } catch (Exception e) {
                // Captura otros errores de conexión
                throw new IllegalArgumentException("Error al comunicarse con la API de estados.", e);
            }
        } else {
            throw new IllegalArgumentException("El estado es un campo obligatorio.");
        }

    }

    /**
     * Sube un archivo de foto a la API de fotos y actualiza la URL en el perfil del usuario.
     * @param id El ID del usuario al que se le asociará la foto.
     * @param archivo El archivo de la foto a subir.
     * @return La URL de la foto guardada.
     */
    public String subirYActualizarFotoUsuario(Integer id, MultipartFile archivo) {

        // 1. Lógica para subir el archivo a la otra API
        // Esta parte es la que hace la llamada HTTP. La responsabilidad es del servicio.
        String fotoUrl = webClienteConfig.uploadFoto(archivo);

        // 2. Buscar al usuario y actualizar su URL
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        usuario.getFoto().setUrl(fotoUrl);
        usuarioRepository.save(usuario);

        return fotoUrl;
    }
}