package RetailManagementSystem.dominio.entidades.gestion;

import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;

import java.util.Objects;

public class Inventario {

    //ATRIBUTOS:

    private String nombre;

    private final Integer idInventario;

    private Integer capacidadMaxima;

    private final Integer capacidadOcupada;

    //GETTERS Y SETTERS:

    public String getNombre() {
        return nombre;
    }
    private void setNombre(String nombre) {
        this.nombre = nombre.trim();
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getCapacidadOcupada() {
        return capacidadOcupada;
    }

    //VALIDACIONES:

    private void validarNombre(String nombre){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("El Nombre del Inventario NO puede estar Vacío");
        }
    }

    private void validarCapacidadMaxima(Integer capacidadMaxima){
        if (capacidadMaxima == null || capacidadMaxima<=0){
            throw new IllegalArgumentException("La Capacidad Maxima del Inventario es Invalida");
        }
    }

    private void validarCapacidadOcupada(Integer capacidadOcupada, int capacidadMaxima){
        if (capacidadOcupada == null || capacidadOcupada < 0){
            throw new IllegalArgumentException("La Capacidad Ocupada del Inventario es Invalida");
        }
        if (capacidadOcupada > capacidadMaxima){
            throw new CapacidadInventarioExcedidaException("La Capacidad Ocupada es Mayor a la Capacidad Maxima");
        }
    }

    //CONSTRUCTORES:

    private Inventario(Integer idInventario, String nombre, Integer capacidadMaxima, Integer capacidadOcupada){
        validarNombre(nombre);
        validarCapacidadMaxima(capacidadMaxima);
        validarCapacidadOcupada(capacidadOcupada, capacidadMaxima);
        this.idInventario = idInventario;
        setNombre(nombre);
        this.capacidadMaxima = capacidadMaxima;
        this.capacidadOcupada = capacidadOcupada;
    }

    public static Inventario reconstruirDesdeBD(
            Integer idInventario, String nombre, Integer capacidadMaxima, Integer capacidadOcupada
    ) {
        return new Inventario(idInventario, nombre, capacidadMaxima, capacidadOcupada);
    }

    public static Inventario crearNuevo(String nombre, Integer capacidadMaxima) {
        return new Inventario(null, nombre, capacidadMaxima, 0);
    }

    //MÉTODOS:

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inventario inventario = (Inventario) o;
        if (this.idInventario == null || inventario.getIdInventario() == null) {
            return false;
        }
        return Objects.equals(this.idInventario, inventario.getIdInventario());
    }

    @Override
    public int hashCode() {
        return idInventario != null ? idInventario.hashCode() : getClass().hashCode();
    }

    public void cambiarNombreInventario(String nuevoNombre){
        validarNombre(nuevoNombre);
        setNombre(nuevoNombre);
    }

    public void aumentarCapacidadMaxima(Integer capacidadExtra){
        if (capacidadExtra == null || capacidadExtra <= 0) {
            throw new IllegalArgumentException(
                    "La Capacidad Extra para expandir la Capacidad Maxima del Inventario debe ser Mayor a 0"
            );
        }
        this.capacidadMaxima += capacidadExtra;
    }

    public int calcularCapacidadLibre() {
        return this.capacidadMaxima - this.capacidadOcupada;
    }

}//===================================================================================================================//

