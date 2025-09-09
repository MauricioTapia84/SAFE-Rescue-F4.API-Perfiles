package com.SAFE_Rescue.API_Perfiles.repositoy;

import com.SAFE_Rescue.API_Perfiles.modelo.Compania;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de Compania
 * Maneja operaciones CRUD desde la base de datos usando Jakarta
 */
@Repository
public interface CompaniaRepository extends JpaRepository<Compania, Integer> {

}
