package ProyectoPropio1;

import Excepciones.CapacidadExcedidaException;
import Excepciones.ProductoNoEncontradoException;
import Excepciones.StockInsuficienteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventario {

    //ATRIBUTOS:

    private String nombre;

    private final int numeroId;

    private static int numeroIdSiguiente = 1;

    private final int capacidadMaxima;

    private final Map<Integer,Producto> misProductos;

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

    //CONSTRUCTORES:

    public Inventario(String nombre, int capacidadMaxima){
        if (nombre==null || nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Inventario Invalido");
        }
        if (capacidadMaxima<=0){
            throw new IllegalArgumentException("Capacidad Maxima del Inventario Invalida");
        }
        this.nombre = nombre;
        this.numeroId = numeroIdSiguiente;
        this.capacidadMaxima = capacidadMaxima;
        this.misProductos = new HashMap<>();
        numeroIdSiguiente++;
    }

    //METODOS DE VALIDACION:

    public boolean tieneProductos(){
        return !this.misProductos.isEmpty();
    }

    private int calcularCapacidadLibre(){
        return this.capacidadMaxima - this.getCapacidadOcupada();
    }

    private int getCapacidadOcupada(){
        int capacidadOcupada = 0;
        for (Producto producto:this.misProductos.values()){
            capacidadOcupada+=producto.getStock();
        }
        return capacidadOcupada;
    }

    //METODOS PARA MOSTRAR INFORMACION:

    public DetalleInventarioDTO exportarDetalleInventario(){
        List<DatosTotalesProductoDTO> datosProductosDeInventario = new ArrayList<>();
        for (Producto producto:this.misProductos.values()){
            datosProductosDeInventario.add(producto.exportarDatosTotales());
        }
        return new DetalleInventarioDTO(this.numeroId, this.nombre, this.capacidadMaxima, this.getCapacidadOcupada(), datosProductosDeInventario);
    }

    public DatosInventarioDTO exportarDatosInventario(){
        return new DatosInventarioDTO(this.numeroId, this.nombre, this.capacidadMaxima, this.getCapacidadOcupada(), this.calcularCapacidadLibre());
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreInventario(String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombre(nuevoNombre);
    }

    public DatosTotalesProductoDTO agregarUnProducto(Producto producto) throws CapacidadExcedidaException{
        if (producto==null){
            throw new IllegalArgumentException("No puedes agregar un Producto Nulo");
        }
        int capacidadLibre = calcularCapacidadLibre();
        if (producto.getStock()>capacidadLibre){
            throw new CapacidadExcedidaException("El Stock a ingresar Excede La Capacidad");
        }
        this.misProductos.put(producto.getCodigo(),producto);
        return producto.exportarDatosTotales();
    }

    public void eliminarUnProducto(int codigo) throws ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        this.misProductos.remove(codigo);
    }

    public boolean buscarProducto(int codigo){
        return this.misProductos.containsKey(codigo);
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public Producto obtenerProducto(int codigo) throws ProductoNoEncontradoException{
        Producto producto = this.misProductos.get(codigo);
        if (producto==null){
            throw new ProductoNoEncontradoException("El Producto de Codigo -" + codigo + "- No Se Encuentra");
        }
        return producto;
    }

    //METODOS PARA MODIFICAR PRODUCTO:

    public DatosVentaProductoDTO obtenerDatosProducto(int codigo) throws ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        return producto.exportarDatosVenta();
    }

    public void actualizarValorVentaPorPorcentaje(int codigo, double porcentaje) throws ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        producto.cambiarValorVentaPorPorcentaje(porcentaje);
    }

    public void actualizarNombreProducto(int codigo,String nombre) throws ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        producto.cambiarNombreProducto(nombre);
    }

    public void actualizarValorCompra(int codigo,double valorNuevo) throws ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        producto.cambiarValorCompra(valorNuevo);
    }

    public void agregarStockProducto(int codigo, int cantidad) throws CapacidadExcedidaException,ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        int capacidadLibre = calcularCapacidadLibre();
        if (cantidad>capacidadLibre){
            throw new CapacidadExcedidaException("La Cantidad A Agregar Excede La Capacidad Maxima");
        }
        producto.aumentarStock(cantidad);
    }

    public void reducirStockProducto(int codigo, int cantidad) throws ProductoNoEncontradoException,StockInsuficienteException{
        Producto producto = obtenerProducto(codigo);
        producto.reducirStock(cantidad);
    }

    public Venta reducirStockProductoPorVenta(int codigo, int cantidad) throws ProductoNoEncontradoException,StockInsuficienteException{
        reducirStockProducto(codigo, cantidad);
        Producto producto = obtenerProducto(codigo);
        double valorCobrado = producto.getValorVenta()*cantidad;
        return new Venta(producto, cantidad, valorCobrado);
    }

    public void verificarStockProductoDisponible(int codigo, int cantidad) throws StockInsuficienteException,ProductoNoEncontradoException{
        Producto producto = obtenerProducto(codigo);
        producto.validarEstadoParaVenta();
        if (cantidad > producto.getStock()){
            throw new StockInsuficienteException("La Cantidad A Reducir Es Mayor A La Cantidad Existente");
        }
    }

}

