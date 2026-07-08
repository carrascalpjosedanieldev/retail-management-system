package ProyectoPropio1.dominio;

public class Inventario {

    //ATRIBUTOS:

    private String nombre;

    private final Integer idInventario;

    private final int capacidadMaxima;

    private int capacidadOcupada;

    //GETTERS Y SETTERS:

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getCapacidadOcupada() {
        return capacidadOcupada;
    }

    public void setCapacidadOcupada(int capacidadOcupada) {
        this.capacidadOcupada = capacidadOcupada;
    }

    //CONSTRUCTORES:

    private Inventario(String nombre, int capacidadMaxima){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Inventario Invalido");
        }
        if (capacidadMaxima<=0){
            throw new IllegalArgumentException("Capacidad Maxima del Inventario Invalida");
        }
        this.nombre = nombre;
        this.idInventario = null;
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadOcupada = 0;
    }

    public static Inventario crearNuevo(String nombre, int capacidadMaxima) {
        return new Inventario(nombre, capacidadMaxima);
    }

    private Inventario(int idInventario, String nombre, int capacidadMaxima, int capacidadOcupada){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Inventario Invalido");
        }
        if (capacidadMaxima<=0){
            throw new IllegalArgumentException("Capacidad Maxima del Inventario Invalida");
        }
        this.nombre = nombre;
        this.idInventario = idInventario;
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadOcupada = capacidadOcupada;
    }

    public static Inventario reconstruirDesdeBD(int idInventario, String nombre, int capacidadMaxima, int capacidadOcupada) {
        return new Inventario(idInventario, nombre, capacidadMaxima, capacidadOcupada);
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreInventario(String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre de Inventario Vacio");
        }
        if (nuevoNombre.length() > 100) {
            throw new IllegalArgumentException("El Nombre no puede superar los 50 Caracteres.");
        }
        setNombre(nuevoNombre);
    }

    //METODOS DE VALIDACION:

    public int calcularCapacidadLibre() {
        return this.capacidadMaxima - this.capacidadOcupada;
    }

    public void validarEspacioDisponible(int stockNuevo) {
        if (stockNuevo > this.calcularCapacidadLibre()) {
            throw new IllegalStateException("Capacidad del Inventario Insuficiente. Libre: " + this.calcularCapacidadLibre() +
                            ", Solicitado: " + stockNuevo);
        }
    }

}

