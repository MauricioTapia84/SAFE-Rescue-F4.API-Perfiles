package com.SAFE_Rescue.API_Perfiles;

import com.SAFE_Rescue.API_Perfiles.modelo.*;
import com.SAFE_Rescue.API_Perfiles.repositoy.*;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Clase encargada de cargar datos iniciales en la base de datos para el perfil de desarrollo.
 * Esta clase se ejecuta solo en el perfil 'dev' y utiliza Faker para generar datos ficticios.
 * Usa WebClient para obtener datos de APIs externas (Compania y Estado).
 */
@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private BomberoRepository bomberoRepository;
    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;
    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private WebClient estadoWebClient;
    @Autowired
    private WebClient companiaWebClient;

    private final Faker faker = new Faker(new Locale("es"));
    private final Set<String> uniqueRuns = new HashSet<>();
    private final Set<String> uniqueTelefonos = new HashSet<>();
    private final Set<String> uniqueCorreos = new HashSet<>();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Cargando datos de prueba...");

        // Generar y obtener entidades de catálogo locales
        List<TipoUsuario> tiposUsuario = crearTiposUsuario();
        List<TipoEquipo> tiposEquipo = crearTiposEquipo();

        if (tiposUsuario.isEmpty() || tiposEquipo.isEmpty()) {
            System.err.println("Error: No se pudieron crear los tipos de usuario o tipos de equipo. Deteniendo carga.");
            return;
        }

        // Obtener entidades de APIs externas
        List<Estado> estados = obtenerEntidadesExternas(estadoWebClient, "/estados", Estado.class);
        List<Compania> companias = obtenerEntidadesExternas(companiaWebClient, "/companias", Compania.class);

        if (estados.isEmpty() || companias.isEmpty()) {
            System.err.println("Error: No se pudieron obtener estados o compañías de las APIs externas. Deteniendo carga.");
            return;
        }

        // Generar Equipos
        List<Equipo> equipos = crearEquipos(tiposEquipo, companias, estados);

        // Generar Usuarios y Bomberos
        crearUsuarios(tiposUsuario, estados, equipos);

        System.out.println("Carga de datos finalizada.");
    }

    // Métodos para crear entidades locales

    private List<TipoUsuario> crearTiposUsuario() {
        List<String> nombres = Arrays.asList("Jefe de Compañía", "Administrador", "Bombero en Terreno", "Operador de Sala", "Ciudadano");
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
                // Se crea un usuario base con datos únicos
                Usuario usuarioBase = crearUsuarioBase();
                usuarioBase.setTipoUsuario(tipo);
                usuarioBase.setEstado(estados.get(faker.random().nextInt(estados.size())));

                // Lógica de asignación de equipo según el tipo de usuario
                if (tipo.getNombre().equalsIgnoreCase("Bombero en Terreno") || tipo.getNombre().equalsIgnoreCase("Operador de Sala")) {
                    Bombero bombero = new Bombero();
                    // Copiar los atributos del usuario base al bombero
                    bombero.setRun(usuarioBase.getRun());
                    bombero.setDv(usuarioBase.getDv());
                    bombero.setNombre(usuarioBase.getNombre());
                    bombero.setAPaterno(usuarioBase.getAPaterno());
                    bombero.setAMaterno(usuarioBase.getAMaterno());
                    bombero.setFechaRegistro(usuarioBase.getFechaRegistro());
                    bombero.setTelefono(usuarioBase.getTelefono());
                    bombero.setCorreo(usuarioBase.getCorreo());
                    bombero.setContrasenia(usuarioBase.getContrasenia());
                    bombero.setIntentosFallidos(usuarioBase.getIntentosFallidos());
                    bombero.setRazonBaneo(usuarioBase.getRazonBaneo());
                    bombero.setDiasBaneo(usuarioBase.getDiasBaneo());
                    bombero.setEstado(usuarioBase.getEstado());
                    bombero.setTipoUsuario(tipo);

                    // Asignar un equipo al bombero
                    bombero.setEquipo(equipos.get(faker.random().nextInt(equipos.size())));
                    bomberoRepository.save(bombero);
                } else {
                    // Si no es un bombero en terreno u operador, se guarda como Usuario
                    usuarioRepository.save(usuarioBase);
                }
            }
        }
    }

    // Método para generar un Usuario base con datos aleatorios únicos
    private Usuario crearUsuarioBase() {
        Usuario usuario = new Usuario();

        // Asegurar RUN único
        String run;
        do {
            run = faker.number().digits(8);
        } while (uniqueRuns.contains(run));
        uniqueRuns.add(run);
        usuario.setRun(run);

        // Asegurar Teléfono único
        String telefono;
        do {
            telefono = "9" + faker.number().digits(8);
        } while (uniqueTelefonos.contains(telefono));
        uniqueTelefonos.add(telefono);
        usuario.setTelefono(telefono);

        // Asegurar Correo único
        String correo;
        do {
            correo = faker.internet().emailAddress();
        } while (uniqueCorreos.contains(correo));
        uniqueCorreos.add(correo);
        usuario.setCorreo(correo);

        usuario.setDv(calcularDv(usuario.getRun()));
        usuario.setNombre(faker.name().firstName());
        usuario.setAPaterno(faker.name().lastName());
        usuario.setAMaterno(faker.name().lastName());
        usuario.setFechaRegistro(faker.date().past(5, java.util.concurrent.TimeUnit.DAYS));
        usuario.setContrasenia("password123");
        usuario.setIntentosFallidos(0);
        usuario.setRazonBaneo(null);
        usuario.setDiasBaneo(null);

        return usuario;
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
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error al obtener datos de la API: " + uri);
            e.printStackTrace();
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