package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.CapacidadExcedidaException;
import ProyectoPropio1.excepciones.ProductoNoEncontradoException;
import ProyectoPropio1.excepciones.StockInsuficienteException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Inventario {

    //ATRIBUTOS:

    private int contadorProductos = 1;

    private String nombre;

    private final int numeroId;

    private final int capacidadMaxima;

    private final Map<Integer, Producto> misProductos;

    //GETTERS Y SETTERS:

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumeroId() {
        return numeroId;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public Map<Integer, Producto> getMisProductos() {
        return Map.copyOf(this.misProductos);
    }

    //CONSTRUCTORES:

    public Inventario(int numeroId, String nombre, int capacidadMaxima){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Inventario Invalido");
        }
        if (capacidadMaxima<=0){
            throw new IllegalArgumentException("Capacidad Maxima del Inventario Invalida");
        }
        this.nombre = nombre;
        this.numeroId = numeroId;
        this.capacidadMaxima = capacidadMaxima;
        this.misProductos = new HashMap<>();
    }

    //METODOS DE VALIDACION:

    public boolean tieneProductos(){
        return !this.misProductos.isEmpty();
    }

    public int calcularCapacidadLibre(){
        return this.capacidadMaxima - this.getCapacidadOcupada();
    }

    public int getCapacidadOcupada(){
        int capacidadOcupada = 0;
        for (Producto producto:this.misProductos.values()){
            capacidadOcupada+=producto.getStock();
        }
        return capacidadOcupada;
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreInventario(String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombre(nuevoNombre);
    }

    public int agregarUnProducto(Producto producto){
        if (producto==null){
            throw new IllegalArgumentException("No puedes agregar un Producto Nulo");
        }
        int capacidadLibre = calcularCapacidadLibre();
        if (producto.getStock()>capacidadLibre){
            throw new CapacidadExcedidaException("El Stock a ingresar Excede La Capacidad");
        }
        this.misProductos.put(producto.getCodigo(),producto);
        return producto.getCodigo();
    }

    public void eliminarUnProducto(int codigo){
        Producto producto = obtenerProducto(codigo);
        this.misProductos.remove(codigo);
    }

    public boolean buscarProducto(int codigo){
        return this.misProductos.containsKey(codigo);
    }

    public Producto obtenerProducto(int codigo){
        Producto producto = this.misProductos.get(codigo);
        if (producto==null){
            throw new ProductoNoEncontradoException("El Producto de Codigo -" + codigo + "- No Se Encuentra");
        }
        return producto;
    }

    //METODOS PARA MODIFICAR PRODUCTO:

    public int asignarCodigoProducto(){
        return this.contadorProductos++;
    }

    public void actualizarValorVentaPorPorcentaje(int codigo, double porcentaje){
        Producto producto = obtenerProducto(codigo);
        producto.cambiarValorVentaPorPorcentaje(porcentaje);
    }

    public void actualizarNombreProducto(int codigo,String nombre){
        Producto producto = obtenerProducto(codigo);
        producto.cambiarNombreProducto(nombre);
    }

    public void actualizarValorCompra(int codigo,double valorNuevo){
        Producto producto = obtenerProducto(codigo);
        producto.cambiarValorCompra(valorNuevo);
    }

    public void agregarStockProducto(int codigo, int cantidad){
        Producto producto = obtenerProducto(codigo);
        int capacidadLibre = calcularCapacidadLibre();
        if (cantidad>capacidadLibre){
            throw new CapacidadExcedidaException("La Cantidad A Agregar Excede La Capacidad Maxima");
        }
        producto.aumentarStock(cantidad);
    }

    public Producto reducirStockProducto(int codigo, int cantidad){
        Producto producto = obtenerProducto(codigo);
        producto.reducirStock(cantidad);
        return producto;
    }

    public void verificarStockProductoDisponible(int codigo, int cantidad, LocalDate fecha){
        Producto producto = obtenerProducto(codigo);
        producto.validarEstadoParaVenta(fecha);
        if (cantidad > producto.getStock()){
            throw new StockInsuficienteException("La Cantidad A Reducir Es Mayor A La Cantidad Existente");
        }
    }

}

