import java.util.*;

public class Main {

    public static class Paciente {
        private String nombre;
        private String apellido;
        private String id;
        private int categoria;
        private long tiempoLlegada;
        private String estado;
        public String Area;
        private Stack<String> historialCambios = new Stack<>();

        public Paciente(String nombre, String apellido, String id, String estado, String area, long tiempoLlegada) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.estado = estado;
            this.Area = area;
            this.id = id;
            this.tiempoLlegada = tiempoLlegada;
        }

        public long getTiempoLlegada() {
            return tiempoLlegada;
        }

        public String getEstado() {
            return estado;
        }

        public void setTiempoLlegada(long tiempoLlegada) {
            this.tiempoLlegada = tiempoLlegada;
        }

        public int getCategoria() {
            return categoria;
        }

        public void setCategoria(int categoria) {
            this.categoria = categoria;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public long tiempoEsperaActual(long tiempoActual) {
            return tiempoActual - tiempoLlegada;
        }

        public void registrarCambio(String descripcion) {
            historialCambios.push(descripcion);
        }

        public String obtenerUltimoCambio() {
            return historialCambios.pop();
        }

        public String getId() {
            return id;
        }
    }

    public static class AreaAtencion {
        private String nombre;
        private PriorityQueue<Paciente> pacientesHeap;
        private int capacidadMaxima;

        public AreaAtencion(String nombre, int capacidadMaxima) {
            this.nombre = nombre;
            this.capacidadMaxima = capacidadMaxima;
            this.pacientesHeap = new PriorityQueue<>((a, b) -> {
                if (a.getCategoria() != b.getCategoria()) {
                    return Integer.compare(a.getCategoria(), b.getCategoria());
                } else {
                    return Long.compare(a.getTiempoLlegada(), b.getTiempoLlegada());
                }
            });
        }

        public void ingresarPaciente(Paciente p) {
            if (!estaSaturada()) {
                pacientesHeap.add(p);
            } else {
                System.out.println("La lista ya está saturada, no puede ingresar el paciente");
            }
        }

        public Paciente atenderPaciente() {
            Paciente p = pacientesHeap.poll();
            if (p != null) {
                System.out.println("Paciente atendido");
            }
            return p;
        }

        public boolean estaSaturada() {
            if (pacientesHeap.size() >= capacidadMaxima) {
                return true;
            }
            return false;
        }

        public List<Paciente> obtenerPacientesPorHeapSort() {
            List<Paciente> lista = new ArrayList<>(pacientesHeap);
            lista.sort(pacientesHeap.comparator());
            return lista;
        }
    }

    public static class Hospital {
        private Map<String, Paciente> pacientesTotales = new HashMap<>();
        private Map<String, AreaAtencion> areasAtencion = new HashMap<>();
        private List<Paciente> pacientesAtendidos = new ArrayList<>();
        private PriorityQueue<Paciente> colaAtencion = new PriorityQueue<>(
                (a, b) -> {
                    if (a.getCategoria() != b.getCategoria()) {
                        return Integer.compare(a.getCategoria(), b.getCategoria());
                    } else {
                        return Long.compare(a.getTiempoLlegada(), b.getTiempoLlegada());
                    }
                }
        );

        public Hospital() {
            areasAtencion.put("SAPU", new AreaAtencion("SAPU", 100));
            areasAtencion.put("urgencia_adulto", new AreaAtencion("urgencia_adulto", 100));
            areasAtencion.put("infantil", new AreaAtencion("infantil", 100));
        }

        public Collection<Paciente> obtenerTodosPacientes() {
            return pacientesTotales.values();
        }

        public void registrarPaciente(Paciente p) {
            pacientesTotales.put(p.getId(), p);
            colaAtencion.add(p);
            AreaAtencion area = areasAtencion.get(p.Area);
            if (area != null) {
                area.ingresarPaciente(p);
            } else {
                System.out.println("Área " + p.Area + " no encontrada.");
            }
        }

        public List<Paciente> getPacientesAtendidos() {
            return pacientesAtendidos;
        }

        public void reasignarCategoria(String id, int nuevaCategoria) {
            Paciente p = pacientesTotales.get(id);
            if (p != null) {
                String descripcion = "El paciente tiene una nueva categoría: " + nuevaCategoria;
                p.registrarCambio(descripcion);
                p.setCategoria(nuevaCategoria);
            } else {
                System.out.println("Paciente de ID: " + id + " no encontrado");
            }
        }

        public Paciente atenderSiguiente() {
            Paciente p = colaAtencion.poll();
            if (p != null) {
                p.registrarCambio("Paciente atendido");
                p.setEstado("Atendido");
                pacientesAtendidos.add(p);
            }
            return p;
        }

        public List<Paciente> obtenerPacientesPorCategoria(int categoria) {
            List<Paciente> resultado = new ArrayList<>();
            PriorityQueue<Paciente> copia = new PriorityQueue<>(colaAtencion);

            while (!copia.isEmpty()) {
                Paciente actual = copia.poll();
                if (actual.getCategoria() == categoria) {
                    resultado.add(actual);
                }
            }
            return resultado;
        }

        public AreaAtencion obtenerArea(String nombre) {
            return areasAtencion.get(nombre);
        }
    }

    public static class GeneradorPacientes {
        private static final List<String> nombres = Arrays.asList(
                "Juan", "María", "Carlos", "Ana", "Pedro", "Sofía", "Luis", "Valentina", "Diego", "Camila"
        );

        private static final List<String> apellidos = Arrays.asList(
                "Gómez", "Pérez", "Rodríguez", "López", "Martínez", "García", "Ramírez", "Fernández", "Torres", "Sánchez"
        );

        private static final List<String> areas = Arrays.asList(
                "SAPU", "urgencia_adulto", "infantil"
        );

        private static final String estado_inicial = "en_espera";
        private int contadorId;
        private Random random;

        public GeneradorPacientes() {
            this.contadorId = 20000000;
            this.random = new Random();
        }

        public List<Main.Paciente> generarPacientes(int n) {
            List<Main.Paciente> pacientes = new ArrayList<>();
            long timestampInicio = 0L;

            for (int i = 0; i < n; i++) {
                String nombre = nombres.get(random.nextInt(nombres.size()));
                String apellido = apellidos.get(random.nextInt(apellidos.size()));
                int categoria = generarCategoriaAleatoria();
                long tiempoLlegada = timestampInicio + (i * 600);
                String area = areas.get(random.nextInt(areas.size()));
                String id = Integer.toString(contadorId++);

                Main.Paciente p = new Main.Paciente(nombre, apellido, id, estado_inicial, area, tiempoLlegada);
                p.setCategoria(categoria);
                pacientes.add(p);
            }

            return pacientes;
        }

        private int generarCategoriaAleatoria() {
            int valor = random.nextInt(100) + 1;
            if (valor <= 10) return 1;
            else if (valor <= 25) return 2;
            else if (valor <= 43) return 3;
            else if (valor <= 70) return 4;
            else return 5;
        }
    }

    public static class SimuladorUrgencia {
        private Hospital hospital;
        private GeneradorPacientes generador;

        private Map<Integer, Integer> pacientesAtendidosPorCategoria = new HashMap<>();
        private Map<Integer, Long> sumaEsperaPorCategoria = new HashMap<>();
        private Map<Integer, Integer> conteoEsperaPorCategoria = new HashMap<>();
        private List<Main.Paciente> pacientesEsperaExcedida = new ArrayList<>();
        private Map<Integer, Long> maxEsperaPorCategoria = new HashMap<>();

        private Map<Integer, Integer> tiempoMaximoPorCategoria = Map.of(
                1, 30,
                2, 60,
                3, 120,
                4, 180,
                5, 240
        );

        public SimuladorUrgencia(Hospital hospital, GeneradorPacientes generador) {
            this.hospital = hospital;
            this.generador = generador;
        }

        public void simular(int pacientesPorDia) {
            int minutosTotales = 24 * 60;
            int pacientesLlegados = 0;
            int pacientesNuevosAcumulados = 0;

            for (int minuto = 0; minuto < minutosTotales; minuto++) {
                if (minuto % 10 == 0 && pacientesLlegados < pacientesPorDia) {
                    List<Main.Paciente> nuevos = generador.generarPacientes(1);
                    Main.Paciente paciente = nuevos.get(0);
                    paciente.setTiempoLlegada(minuto);
                    hospital.registrarPaciente(paciente);
                    pacientesLlegados++;
                    pacientesNuevosAcumulados++;
                }

                if (minuto % 15 == 0) {
                    atenderPaciente(minuto);
                }

                if (pacientesNuevosAcumulados >= 3) {
                    atenderPaciente(minuto);
                    atenderPaciente(minuto);
                    pacientesNuevosAcumulados = 0;
                }

                verificarPacientesEsperaExcedida(minuto);
            }

            mostrarResultados();
        }

        private void atenderPaciente(int minutoActual) {
            Main.Paciente paciente = hospital.atenderSiguiente();
            if (paciente != null) {
                paciente.setEstado("Atendido");
                paciente.registrarCambio("Paciente atendido en minuto " + minutoActual);

                int cat = paciente.getCategoria();
                pacientesAtendidosPorCategoria.put(cat, pacientesAtendidosPorCategoria.getOrDefault(cat, 0) + 1);
                long espera = minutoActual - paciente.getTiempoLlegada();
                sumaEsperaPorCategoria.put(cat, sumaEsperaPorCategoria.getOrDefault(cat, 0L) + espera);
                conteoEsperaPorCategoria.put(cat, conteoEsperaPorCategoria.getOrDefault(cat, 0) + 1);

                long maxActual = maxEsperaPorCategoria.getOrDefault(cat, 0L);
                if (espera > maxActual) {
                    maxEsperaPorCategoria.put(cat, espera);
                }
            }
        }

        private void verificarPacientesEsperaExcedida(int minutoActual) {
            for (Main.Paciente p : hospital.obtenerTodosPacientes()) {
                if (!p.getEstado().equals("Atendido")) {
                    int cat = p.getCategoria();
                    int maxEspera = tiempoMaximoPorCategoria.getOrDefault(cat, 240);
                    long espera = minutoActual - p.getTiempoLlegada();
                    if (espera > maxEspera && !pacientesEsperaExcedida.contains(p)) {
                        pacientesEsperaExcedida.add(p);
                    }
                }
            }
        }

        public void mostrarResultados() {
            System.out.println("\n--- RESULTADOS DE LA SIMULACIÓN ---");
            int totalAtendidos = pacientesAtendidosPorCategoria.values().stream().mapToInt(Integer::intValue).sum();
            System.out.println("Total pacientes atendidos: " + totalAtendidos);

            for (int categoria = 1; categoria <= 5; categoria++) {
                int atendidos = pacientesAtendidosPorCategoria.getOrDefault(categoria, 0);
                System.out.println("Pacientes atendidos categoría C" + categoria + ": " + atendidos);
            }

            System.out.println("\nTiempo promedio de espera por categoría:");
            for (int categoria = 1; categoria <= 5; categoria++) {
                long suma = sumaEsperaPorCategoria.getOrDefault(categoria, 0L);
                int cantidad = conteoEsperaPorCategoria.getOrDefault(categoria, 0);
                if (cantidad > 0) {
                    double promedio = (double) suma / cantidad;
                    System.out.printf("  C%d: %.2f minutos\n", categoria, promedio);
                } else {
                    System.out.println("  C" + categoria + ": No se atendieron pacientes");
                }
            }

            System.out.println("\nPacientes que excedieron el tiempo máximo de espera:");
            Map<Integer, Integer> excedidosPorCategoria = new HashMap<>();
            for (Main.Paciente p : pacientesEsperaExcedida) {
                int cat = p.getCategoria();
                excedidosPorCategoria.put(cat, excedidosPorCategoria.getOrDefault(cat, 0) + 1);
            }
            for (int categoria = 1; categoria <= 5; categoria++) {
                int cantidad = excedidosPorCategoria.getOrDefault(categoria, 0);
                System.out.println("  C" + categoria + ": " + cantidad);
            }

            System.out.println("\nPeores tiempos de espera registrados por categoría:");
            for (int categoria = 1; categoria <= 5; categoria++) {
                long max = maxEsperaPorCategoria.getOrDefault(categoria, 0L);
                System.out.println("  C" + categoria + ": " + max + " minutos");
            }
        }
    }

    public static void main(String[] args) {
        Hospital hospital = new Hospital();
        GeneradorPacientes generador = new GeneradorPacientes();
        SimuladorUrgencia simulador = new SimuladorUrgencia(hospital, generador);
        simulador.simular(100);
    }
}
//