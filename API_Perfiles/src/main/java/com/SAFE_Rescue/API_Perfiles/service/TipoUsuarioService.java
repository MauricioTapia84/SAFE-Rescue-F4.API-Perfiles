package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoUsuario;
import com.SAFE_Rescue.API_Perfiles.repositoy.TipoUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión integral de Tipos de usuario
 * Maneja operaciones CRUD, asignación de tipoUsuario
 * y validación de datos para credencial
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
     * @throws RuntimeException Si ocurre algún error durante el proceso
     * @throws IllegalArgumentException Si el tipo de usuario no cumple con los parametros
     */
    public TipoUsuario save(TipoUsuario tipoUsuario) {
        try {
            validarTipoUsuario(tipoUsuario);
            return tipoUsuarioRepository.save(tipoUsuario);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error al guardar el tipo de usuario: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de un tipo de usuario existente.
     * @param tipoUsuario Datos actualizados del tipo de usuario
     * @param id Identificador del tipo de usuario a actualizar
     * @return tipo de usuario actualizado
     * @throws IllegalArgumentException Si el tipo de usuario es nulo o si el nombre del tipo de usuario es nulo o excede los 50 caracteres
     * @throws NoSuchElementException Si no se encuentra el tipo de usuario a actualizar
     * @throws RuntimeException Si ocurre algún error durante la actualización
     */
    public TipoUsuario update(TipoUsuario tipoUsuario ,Integer id) {
        try {
            if (tipoUsuario == null) {
                throw new IllegalArgumentException("El tipo de usuario no puede ser nulo");
            }

            TipoUsuario antiguoTipoUsuario = tipoUsuarioRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Tipo de usuario no encontrado"));

            //Control de errores
            if (tipoUsuario.getNombre() == null) {
                throw new IllegalArgumentException("El Nombre no puede ser nulo");
            }

            if (tipoUsuario.getNombre().length() > 50) {
                throw new IllegalArgumentException("El Nombre no puede exceder los 50 caracteres");
            }

            antiguoTipoUsuario.setNombre(tipoUsuario.getNombre());

            return tipoUsuarioRepository.save(antiguoTipoUsuario);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error al actualizar el tipo de usuario: " + e.getMessage());
        } catch (NoSuchElementException  f) {
            throw new NoSuchElementException("Error al actualizar el tipo de usuario: " + f.getMessage());
        } catch (Exception g) {
            throw new RuntimeException("Error al actualizar el tipo de usuario: " + g.getMessage());
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
     * Valida el tipo de usuario
     * @param tipoUsuario tipo de usuario
     * @throws IllegalArgumentException Si el tipo de usuario no cumple con las reglas de validación
     */
    public void validarTipoUsuario(TipoUsuario tipoUsuario) {
        if (tipoUsuario.getNombre() == null) {
            throw new IllegalArgumentException("El nombre del tipo de usuario es requerido");
        }

        if (tipoUsuario.getNombre().length() > 50) {
            throw new IllegalArgumentException("El valor nombre del tipo de usuario excede máximo de caracteres (50)");
        }
    }
}