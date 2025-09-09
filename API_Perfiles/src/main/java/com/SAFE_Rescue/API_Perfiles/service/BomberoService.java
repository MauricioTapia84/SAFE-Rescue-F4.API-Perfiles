package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.Bombero;
import com.SAFE_Rescue.API_Perfiles.repositoy.BomberoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión de entidades Bombero.
 * Maneja operaciones CRUD y validaciones de negocio.
 */
@Service
public class BomberoService {

    @Autowired
    private BomberoRepository bomberoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EquipoService equipoService;

    /**
     * Obtiene todos los bomberos registrados.
     *
     * @return Una lista de todos los bomberos.
     */
    public List<Bombero> findAll() {
        return bomberoRepository.findAll();
    }

    /**
     * Busca un bombero por su ID único.
     *
     * @param id El ID del bombero.
     * @return El bombero encontrado.
     * @throws NoSuchElementException Si el bombero no es encontrado.
     */
    public Bombero findById(Integer id) {
        return bomberoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bombero no encontrado con ID: " + id));
    }

    /**
     * Guarda un nuevo bombero.
     *
     * @param bombero El objeto Bombero a guardar.
     * @return El bombero guardado.
     * @throws IllegalArgumentException Si los datos del bombero son inválidos.
     */
    public Bombero save(Bombero bombero) {
        if (bombero == null) {
            throw new IllegalArgumentException("El objeto Bombero no puede ser nulo.");
        }

        // Se utilizan las validaciones del servicio padre para los atributos de Usuario
        usuarioService.validarAtributosUsuario(bombero);

        // Se valida la existencia de la relación específica de Bombero
        validarRelacionesBombero(bombero);

        try {
            return bomberoRepository.save(bombero);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El RUN, correo o teléfono ya existen.");
        }
    }

    /**
     * Actualiza un bombero existente.
     *
     * @param bombero El objeto Bombero con los datos actualizados.
     * @param id      El ID del bombero a actualizar.
     * @return El bombero actualizado.
     * @throws IllegalArgumentException Si los datos del bombero son inválidos.
     * @throws NoSuchElementException   Si el bombero no es encontrado.
     */
    public Bombero update(Bombero bombero, Integer id) {
        if (bombero == null) {
            throw new IllegalArgumentException("El objeto Bombero a actualizar no puede ser nulo.");
        }

        // Se validan los atributos de Usuario y las relaciones específicas de Bombero
        usuarioService.validarAtributosUsuario(bombero);
        validarRelacionesBombero(bombero);

        Bombero bomberoExistente = bomberoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bombero no encontrado con ID: " + id));

        // Actualizar los campos del objeto Bombero existente
        bomberoExistente.setRun(bombero.getRun());
        bomberoExistente.setDv(bombero.getDv());
        bomberoExistente.setNombre(bombero.getNombre());
        bomberoExistente.setAPaterno(bombero.getAPaterno());
        bomberoExistente.setAMaterno(bombero.getAMaterno());
        bomberoExistente.setFechaRegistro(bombero.getFechaRegistro());
        bomberoExistente.setTelefono(bombero.getTelefono());
        bomberoExistente.setCorreo(bombero.getCorreo());
        bomberoExistente.setContrasenia(bombero.getContrasenia());
        bomberoExistente.setIntentosFallidos(bombero.getIntentosFallidos());
        bomberoExistente.setRazonBaneo(bombero.getRazonBaneo());
        bomberoExistente.setDiasBaneo(bombero.getDiasBaneo());
        bomberoExistente.setTipoUsuario(bombero.getTipoUsuario());
        bomberoExistente.setEstado(bombero.getEstado());
        bomberoExistente.setEquipo(bombero.getEquipo());

        try {
            return bomberoRepository.save(bomberoExistente);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El RUN, correo o teléfono ya existen.");
        }
    }

    /**
     * Elimina un bombero por su ID.
     *
     * @param id El ID del bombero a eliminar.
     * @throws NoSuchElementException Si el bombero no es encontrado.
     */
    public void delete(Integer id) {
        if (!bomberoRepository.existsById(id)) {
            throw new NoSuchElementException("Bombero no encontrado con ID: " + id);
        }
        bomberoRepository.deleteById(id);
    }

    /**
     * Valida las relaciones específicas de Bombero, como la existencia de un equipo.
     *
     * @param bombero El objeto Bombero a validar.
     * @throws IllegalArgumentException Si el equipo no existe.
     */
    private void validarRelacionesBombero(Bombero bombero) {
        if (bombero.getEquipo() != null) {
            try {
                equipoService.findById(bombero.getEquipo().getIdEquipo());
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("El equipo asociado no existe.");
            }
        }
    }
}