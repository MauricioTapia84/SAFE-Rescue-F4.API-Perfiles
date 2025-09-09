package com.SAFE_Rescue.API_Perfiles.repositoy;

import com.SAFE_Rescue.API_Perfiles.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de Usuarios
 * Maneja operaciones CRUD desde la base de datos usando Jakarta
 * Maneja validadores para encontrar el run y telefono
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByRun(String run);

    boolean existsByTelefono(String telefono);
}