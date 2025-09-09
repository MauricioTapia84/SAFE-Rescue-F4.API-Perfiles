package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.repositoy.TipoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión integral de Tipos de usuario
 * Maneja operaciones CRUD, asignación de tipoUsuario
 * y validación de datos.
 */
@Service
public class TipoUsuarioService {

    // REPOSITORIOS INYECTADOS
    @Autowired private TipoUsuarioRepository tipoUsuarioRepository;

    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todos los tipos de usuario registrados en el sistema.
     * @return Lista completa de tipos de usuario
     */
    public List<TipoUsuario> findAll() {
        return tipoUsuarioRepository.findAll();
    }

    /**
     * Busca un tipo de usuario por su ID único.
     * @param id Identificador del tipo de usuario
     * @return tipo de usuario encontrado
     * @throws NoSuchElementException Si no se encuentra el tipo de usuario
     */
    public TipoUsuario findById(Integer id){
        return tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el tipo de usuario con ID: " + id));
    }

    /**
     * Guarda un nuevo tipo de usuario en el sistema.
     * Realiza validaciones y guarda relaciones con otros componentes.
     * @param tipoUsuario Datos del tipo de usuario a guardar
     * @return tipo de usuario guardado con ID generado
     * @throws IllegalArgumentException Si el tipo de usuario no cumple con los parámetros
     */
    public TipoUsuario save(TipoUsuario tipoUsuario) {
        validarTipoUsuario(tipoUsuario);
        try {
            return tipoUsuarioRepository.save(tipoUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El tipo de usuario ya existe o tiene valores inválidos.");
        }
    }

    /**
     * Actualiza los datos de un tipo de usuario existente.
     * @param tipoUsuario Datos actualizados del tipo de usuario
     * @param id Identificador del tipo de usuario a actualizar
     * @return tipo de usuario actualizado
     * @throws IllegalArgumentException Si el tipo de usuario es nulo o si el nombre no cumple con los parámetros
     * @throws NoSuchElementException Si no se encuentra el tipo de usuario a actualizar
     */
    public TipoUsuario update(TipoUsuario tipoUsuario, Integer id) {
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("El tipo de usuario no puede ser nulo");
        }

        // Se valida el nuevo tipo de usuario
        validarTipoUsuario(tipoUsuario);

        TipoUsuario antiguoTipoUsuario = tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de usuario no encontrado"));

        antiguoTipoUsuario.setNombre(tipoUsuario.getNombre());

        try {
            return tipoUsuarioRepository.save(antiguoTipoUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El nombre del tipo de usuario ya existe.");
        }
    }

    /**
     * Elimina un tipo de usuario del sistema.
     * @param id Identificador del tipo de usuario a eliminar
     * @throws NoSuchElementException Si no se encuentra el tipo de usuario
     */
    public void delete(Integer id){
        if (!tipoUsuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Tipo de usuario no encontrado");
        }
        tipoUsuarioRepository.deleteById(id);
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN Y UTILIDADES

    /**
     * Valida el tipo de usuario.
     * @param tipoUsuario tipo de usuario
     * @throws IllegalArgumentException Si el tipo de usuario no cumple con las reglas de validación
     */
    private void validarTipoUsuario(TipoUsuario tipoUsuario) {
        if (tipoUsuario.getNombre() == null || tipoUsuario.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo de usuario es requerido");
        }

        if (tipoUsuario.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre del tipo de usuario excede el máximo de 50 caracteres");
        }
    }
}