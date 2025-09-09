package com.SAFE_Rescue.API_Perfiles.repositoy;

import com.SAFE_Rescue.API_Perfiles.modelo.Bombero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de Bomberos
 * Maneja operaciones CRUD desde la base de datos usando Jakarta
 */
@Repository
public interface BomberoRepository extends JpaRepository<Bombero, Integer> {

}
