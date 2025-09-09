package com.SAFE_Rescue.API_Perfiles.service;

import com.SAFE_Rescue.API_Perfiles.modelo.Equipo;
import com.SAFE_Rescue.API_Perfiles.repositoy.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private WebClient companiaWebClient;

    @Autowired
    private TipoEquipoService tipoEquipoService;

    public List<Equipo> findAll() {
        return equipoRepository.findAll();
    }

    public Equipo findById(Integer id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + id));
    }

    public Equipo save(Equipo equipo) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo no puede ser nulo.");
        }

        validarAtributosEquipo(equipo);

        // Se valida que el tipo de equipo exista en la API local
        validarTipoEquipo(equipo);
        // Se valida que la compañía exista en la API externa
        validarCompaniaExterna(equipo);

        try {
            return equipoRepository.save(equipo);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. El equipo no cumple con las restricciones de la base de datos.");
        }
    }

    public Equipo update(Equipo equipo, Integer id) {
        if (equipo == null) {
            throw new IllegalArgumentException("El equipo a actualizar no puede ser nulo.");
        }

        // Se validan los atributos y las relaciones
        validarAtributosEquipo(equipo);
        validarTipoEquipo(equipo);
        validarCompaniaExterna(equipo);

        Equipo equipoExistente = equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + id));

        equipoExistente.setNombre(equipo.getNombre());
        equipoExistente.setCompania(equipo.getCompania());
        equipoExistente.setTipoEquipo(equipo.getTipoEquipo());
        equipoExistente.setLider(equipo.getLider());

        try {
            return equipoRepository.save(equipoExistente);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos. No se puede actualizar el equipo, puede que ya exista.");
        }
    }

    public void delete(Integer id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + id));
        equipoRepository.delete(equipo);
    }

    private void validarAtributosEquipo(Equipo equipo) {
        if (equipo.getNombre() == null || equipo.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo es requerido.");
        }
        if (equipo.getNombre().length() > 50) {
            throw new IllegalArgumentException("El nombre del equipo no puede exceder los 50 caracteres.");
        }
    }

    private void validarTipoEquipo(Equipo equipo) {
        if (equipo.getTipoEquipo() != null) {
            try {
                tipoEquipoService.findById(equipo.getTipoEquipo().getIdTipoEquipo());
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("El tipo de equipo asociado no existe.");
            }
        }
    }

    // Método para validar la existencia de la compañía en la API externa
    private void validarCompaniaExterna(Equipo equipo) {
        if (equipo.getCompania() != null) {
            try {
                // Se realiza la llamada a la API externa
                this.companiaWebClient.get()
                        .uri("/{id}", equipo.getCompania().getIdCompania())
                        .retrieve()
                        // Si el estado es un error 4xx, lanza una excepción
                        .onStatus(status -> status.is4xxClientError(),
                                response -> Mono.error(new IllegalArgumentException("La compañía asociada al equipo no existe en la API externa.")))
                        // bodyToMono(String.class) consume el cuerpo y el .block() espera a que llegue la respuesta
                        .bodyToMono(String.class)
                        .block(); // <-- Aquí se bloquea el hilo de ejecución
            } catch (Exception e) {
                // Captura la excepción lanzada por onStatus o cualquier otro error de comunicación
                throw new IllegalArgumentException("Error al comunicarse con la API de compañías.", e);
            }
        }
    }

}