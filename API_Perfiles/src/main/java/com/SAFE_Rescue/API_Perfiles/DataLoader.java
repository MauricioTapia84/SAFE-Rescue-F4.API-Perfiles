package com.SAFE_Rescue.API_Perfiles;

import com.SAFE_Rescue.API_Perfiles.modelo.*;
import com.SAFE_Rescue.API_Perfiles.repositoy.*;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    // Repositorios y WebClients inyectados
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private BomberoRepository bomberoRepository;
    @Autowired private TipoUsuarioRepository tipoUsuarioRepository;
    @Autowired private TipoEquipoRepository tipoEquipoRepository;
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private WebClient estadoWebClient;
    @Autowired private WebClient companiaWebClient;

    private final Faker faker = new Faker(new Locale("es"));
    private final Set<String> uniqueRuns = new HashSet<>();
    private final Set<String> uniqueTelefonos = new HashSet<>();
    private final Set<String> uniqueCorreos = new HashSet<>();

    private static final String BOMBERO_TIPO = "Bombero en Terreno";
    private static final String OPERADOR_TIPO = "Operador de Sala";

    @Override
    public void run(String... args) {
        System.out.println("Cargando datos de prueba...");

        try {
            // Generar y obtener entidades de catálogo locales
            List<TipoUsuario> tiposUsuario = crearTiposUsuario();
            List<TipoEquipo> tiposEquipo = crearTiposEquipo();

            // Obtener entidades de APIs externas
            List<Estado> estados = obtenerEntidadesExternas(estadoWebClient, "/estados", Estado.class);
            List<Compania> companias = obtenerEntidadesExternas(companiaWebClient, "/companias", Compania.class);

            if (tiposUsuario.isEmpty() || tiposEquipo.isEmpty() || estados.isEmpty() || companias.isEmpty()) {
                System.err.println("Error: No se pudieron obtener entidades de catálogo. Deteniendo la carga.");
                return;
            }

            // Generar Equipos
            List<Equipo> equipos = crearEquipos(tiposEquipo, companias, estados);

            // Generar Usuarios y Bomberos
            crearUsuarios(tiposUsuario, estados, equipos);

            System.out.println("Carga de datos finalizada.");
        } catch (Exception e) {
            System.err.println("Un error inesperado ocurrió durante la carga de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos para crear entidades locales

    private List<TipoUsuario> crearTiposUsuario() {
        List<String> nombres = Arrays.asList("Jefe de Compañía", BOMBERO_TIPO, OPERADOR_TIPO, "Administrador", "Ciudadano");
        List<TipoUsuario> tiposUsuario = new ArrayList<>();
        for (String nombre : nombres) {
            TipoUsuario tipo = new TipoUsuario();
            tipo.setNombre(nombre);
            tiposUsuario.add(tipoUsuarioRepository.save(tipo));
        }
        return tiposUsuario;
    }

    private List<TipoEquipo> crearTiposEquipo() {
        List<String> nombres = Arrays.asList(
                "Médico", "Administrativo", "Forestales", "Rescate Urbano",
                "Materiales Peligrosos", "Alturas", "Subacuático", "Logístico"
        );
        List<TipoEquipo> tiposEquipo = new ArrayList<>();
        for (String nombre : nombres) {
            TipoEquipo tipo = new TipoEquipo();
            tipo.setNombre(nombre);
            tiposEquipo.add(tipoEquipoRepository.save(tipo));
        }
        return tiposEquipo;
    }

    private List<Equipo> crearEquipos(List<TipoEquipo> tiposEquipo, List<Compania> companias, List<Estado> estados) {
        List<Equipo> equipos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Equipo equipo = new Equipo();
            equipo.setNombre(faker.team().name());
            equipo.setTipoEquipo(tiposEquipo.get(faker.random().nextInt(tiposEquipo.size())));
            equipo.setCompania(companias.get(faker.random().nextInt(companias.size())));
            equipo.setEstado(estados.get(faker.random().nextInt(estados.size())));
            equipos.add(equipoRepository.save(equipo));
        }
        return equipos;
    }

    // Método para crear usuarios y bomberos

    private void crearUsuarios(List<TipoUsuario> tiposUsuario, List<Estado> estados, List<Equipo> equipos) {
        for (TipoUsuario tipo : tiposUsuario) {
            int cantidad = 2; // Cantidad de usuarios por tipo
            for (int i = 0; i < cantidad; i++) {
                Usuario usuario;
                if (tipo.getNombre().equalsIgnoreCase(BOMBERO_TIPO) || tipo.getNombre().equalsIgnoreCase(OPERADOR_TIPO)) {
                    usuario = new Bombero();
                    // Casting seguro para asignar el equipo
                    ((Bombero) usuario).setEquipo(equipos.get(faker.random().nextInt(equipos.size())));
                    bomberoRepository.save((Bombero) usuario);
                } else {
                    usuario = new Usuario();
                    usuarioRepository.save(usuario);
                }

                // Asignar los atributos base a la instancia recién creada (Usuario o Bombero)
                usuario.setRun(crearRunUnico());
                usuario.setDv(calcularDv(usuario.getRun()));
                usuario.setNombre(faker.name().firstName());
                usuario.setAPaterno(faker.name().lastName());
                usuario.setAMaterno(faker.name().lastName());
                usuario.setFechaRegistro(Date.from(faker.timeAndDate().past(5, java.util.concurrent.TimeUnit.DAYS)));
                usuario.setTelefono(crearTelefonoUnico());
                usuario.setCorreo(crearCorreoUnico());
                usuario.setContrasenia("password123");
                usuario.setIntentosFallidos(0);
                usuario.setRazonBaneo(null);
                usuario.setDiasBaneo(null);
                usuario.setTipoUsuario(tipo);
                usuario.setEstado(estados.get(faker.random().nextInt(estados.size())));
            }
        }
    }

    // Métodos de utilidad para crear datos únicos
    private String crearRunUnico() {
        String run;
        do {
            run = faker.number().digits(8);
        } while (!uniqueRuns.add(run)); // Usa 'add' para evitar duplicados y verificar al mismo tiempo
        return run;
    }

    private String crearTelefonoUnico() {
        String telefono;
        do {
            telefono = "9" + faker.number().digits(8);
        } while (!uniqueTelefonos.add(telefono));
        return telefono;
    }

    private String crearCorreoUnico() {
        String correo;
        do {
            correo = faker.internet().emailAddress();
        } while (!uniqueCorreos.add(correo));
        return correo;
    }

    // Método para obtener datos de APIs externas
    private <T> List<T> obtenerEntidadesExternas(WebClient client, String uri, Class<T> clazz) {
        try {
            return client.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(clazz)
                    .collectList()
                    .toFuture()
                    .get();
        } catch (WebClientResponseException e) {
            System.err.println("Error en la respuesta de la API para " + uri + ": " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error de conexión o inesperado al obtener datos de la API: " + uri + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Método de utilidad para calcular el DV del RUN
    private String calcularDv(String runStr) {
        int run = Integer.parseInt(runStr);
        int suma = 0;
        int multiplicador = 2;

        while (run > 0) {
            suma += (run % 10) * multiplicador;
            run /= 10;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
        }

        int dv = 11 - (suma % 11);
        if (dv == 11) return "0";
        if (dv == 10) return "K";
        return String.valueOf(dv);
    }
}