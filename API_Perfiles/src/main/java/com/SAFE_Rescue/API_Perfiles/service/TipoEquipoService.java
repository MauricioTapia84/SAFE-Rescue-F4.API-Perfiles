package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.TipoEquipo;
import com.SAFE_Rescue.API_Perfiles.repositoy.TipoEquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Servicio para la gestión integral de Tipos de equipo
 * Maneja operaciones CRUD y validación de datos.
 */
@Service
public class TipoEquipoService {

    // REPOSITORIOS INYECTADOS
    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;

    // MÉTODOS CRUD PRINCIPALES

    /**
     * Obtiene todos los tipos de equipo registrados en el sistema.
     *
     * @return Lista completa de tipos de equipo
     */
    public List<TipoEquipo> findAll() {
        return tipoEquipoRepository.findAll();
    }

    /**
     * Busca un tipo de equipo por su ID único.
     *
     * @param id Identificador del tipo de equipo
     * @return tipo de equipo encontrado
     * @throws NoSuchElementException Si no se encuentra el tipo de equipo
     */
    public TipoEquipo findById(Integer id) {
        return tipoEquipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el tipo de equipo con ID: " + id));
    }

    /**
     * Guarda un nuevo tipo de equipo en el sistema.
     *
     * @param tipoEquipo Datos del tipo de equipo a guardar
     * @return tipo de equipo guardado con ID generado
     * @throws IllegalArgumentException Si el tipo de equipo no cumple con los parámetros
     */
    public TipoEquipo save(TipoEquipo tipoEquipo) {
        validarTipoEquipo(tipoEquipo);
        try {
            return tipoEquipoRepository.save(tipoEquipo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El tipo de equipo ya existe o tiene valores inválidos.");
        }
    }

    /**
     * Actualiza los datos de un tipo de equipo existente.
     *
     * @param tipoEquipo Datos actualizados del tipo de equipo
     * @param id         Identificador del tipo de equipo a actualizar
     * @return tipo de equipo actualizado
     * @throws IllegalArgumentException Si el tipo de equipo es nulo o si el nombre no cumple con los parámetros
     * @throws NoSuchElementException   Si no se encuentra el tipo de equipo a actualizar
     */
    public TipoEquipo update(TipoEquipo tipoEquipo, Integer id) {
        if (tipoEquipo == null) {
            throw new IllegalArgumentException("El tipo de equipo no puede ser nulo");
        }

        validarTipoEquipo(tipoEquipo);

        TipoEquipo antiguoTipoEquipo = tipoEquipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tipo de equipo no encontrado"));

        antiguoTipoEquipo.setNombre(tipoEquipo.getNombre());

        try {
            return tipoEquipoRepository.save(antiguoTipoEquipo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El nombre del tipo de equipo ya existe.");
        }
    }

    /**
     * Elimina un tipo de equipo del sistema.
     *
     * @param id Identificador del tipo de equipo a eliminar
     * @throws NoSuchElementException Si no se encuentra el tipo de equipo
     */
    public void delete(Integer id) {
        if (!tipoEquipoRepository.existsById(id)) {
            throw new NoSuchElementException("Tipo de equipo no encontrado");
        }
        tipoEquipoRepository.deleteById(id);
    }

    // MÉTODOS PRIVADOS DE VALIDACIÓN Y UTILIDADES

    /**
     * Valida el tipo de equipo.
     *
     * @param tipoEquipo tipo de equipo
     * @throws IllegalArgumentException Si el tipo de equipo no cumple con las reglas de validación
     */
    private void validarTipoEquipo(TipoEquipo tipoEquipo) {
        if (tipoEquipo.getNombre() == null || tipoEquipo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo de equipo es requerido");
        }

        if (tipoEquipo.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre del tipo de equipo excede el máximo de 50 caracteres");
        }
    }
}