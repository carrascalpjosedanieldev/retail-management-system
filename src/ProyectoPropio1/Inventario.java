package ProyectoPropio1;

import java.util.HashMap;
import java.util.Map;

public class Inventario {

    //ATRIBUTOS:

    private String nombre;

    private final int numeroId;

    private static int numeroIdSiguiente = 1;

    private final int capacidadMaxima;

    private int capacidadOcupada;

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

    public int getCapacidadOcupada() {
        return capacidadOcupada;
    }

    //CONSTRUCTORES:

    public Inventario(String nombre, int capacidadMaxima) throws IllegalArgumentException{
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
        this.capacidadOcupada=0;
        numeroIdSiguiente++;
    }

    //METODOS DE VALIDACION:

    public boolean tieneProductos(){
        return !this.misProductos.isEmpty();
    }

    private int calcularCapacidadLibre(){
        return this.capacidadMaxima - this.capacidadOcupada;
    }

    //METODOS PARA MOSTRAR INFORMACION:

    public String obtenerDetalle(){
        StringBuilder detalle = new StringBuilder();
        detalle.append("----------------------------------------------------------------------------------------------------------------------------------");
        detalle.append(System.lineSeparator());
        detalle.append("---> INVENTARIO:  -").append(getNombre()).append("-  NUMERO ID:  -").append(getNumeroId()).append("-");
        detalle.append(System.lineSeparator());
        if (this.misProductos.isEmpty()){
            detalle.append("---> ESTE INVENTARIO NO TIENE PRODUCTOS");
        } else {
            for (Producto producto:this.misProductos.values()){
                detalle.append(producto.describirProducto());
            }
        }
        detalle.append("----------------------------------------------------------------------------------------------------------------------------------");
        detalle.append(System.lineSeparator());
        return detalle.toString();
    }

    public String informacionMinima(){
        String informacion;
        informacion = String.format("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d%n",
                this.getNombre(),this.getNumeroId(),this.getCapacidadMaxima(),this.getCapacidadOcupada());
        return informacion;
    }

    public String mostrarInformacionStock(){
        String informacion;
        int capacidadLibre = calcularCapacidadLibre();
        informacion = "Inventario: -" + getNombre() + "-\n" +
                "Capacidad Maxima:  " + this.capacidadMaxima + "  Capacidad Libre:  " + capacidadLibre + "  Capacidad Ocupada:  " + this.capacidadOcupada;
        return informacion;
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreInventario(String nuevoNombre) throws IllegalArgumentException{
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombre(nuevoNombre);
    }

    public Producto agregarUnProducto(Producto producto) throws IllegalArgumentException{
        if (producto==null){
            throw new IllegalArgumentException("No puedes agregar un Producto Nulo");
        }
        int capacidadLibre = calcularCapacidadLibre();
        if (producto.getStock()>capacidadLibre){
            throw new IllegalArgumentException("El Stock Excede La Capacidad");
        }
        this.misProductos.put(producto.getCodigo(),producto);
        this.capacidadOcupada+=producto.getStock();
        return producto;
    }

    public void eliminarUnProducto(int codigo) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto ==null){
            throw new IllegalArgumentException("El Producto No Se Encuentra");
        }
        this.capacidadOcupada-= producto.getStock();
        this.misProductos.remove(codigo);
    }

    public boolean buscarProducto(int codigo){
        return this.misProductos.containsKey(codigo);
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public Producto obtenerProducto(int codigo) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto==null){
            throw new IllegalArgumentException("El Producto No Se Encuentra");
        }
        return producto;
    }

    //METODOS PARA MODIFICAR PRODUCTO:

    public void actualizarValorVentaPorPorcentaje(int codigo, double porcentaje) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto==null){
            throw new IllegalArgumentException("El Producto No Esta");
        }
        producto.cambiarValorVentaPorPorcentaje(porcentaje);
    }

    public void actualizarNombreProducto(int codigo,String nombre) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto ==null){
            throw new IllegalArgumentException("No Se Encuentra Ese Producto");
        }
        producto.cambiarNombreProducto(nombre);
    }

    public void actualizarValorCompra(int codigo,double valorNuevo) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto==null){
            throw new IllegalArgumentException("No Se Encuentra Ese Producto");
        }
        producto.cambiarValorCompra(valorNuevo);
    }

    public void agregarStockProducto(int codigo, int cantidad) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto ==null){
            throw new IllegalArgumentException("No Se Encuentra Ese Producto");
        }
        int capacidadLibre = calcularCapacidadLibre();
        if (cantidad>capacidadLibre){
            throw new IllegalArgumentException("La Cantidad A Agregar Excede La Capacidad Maxima");
        }
        producto.aumentarStock(cantidad);
        this.capacidadOcupada += cantidad;
    }

    public void reducirStockProducto(int codigo, int cantidad) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto ==null){
            throw new IllegalArgumentException("No Se Encuentra Ese Producto");
        }
        producto.reducirStock(cantidad);
        this.capacidadOcupada -= cantidad;
    }

    public Venta reducirStockProductoPorVenta(int codigo, int cantidad) throws IllegalArgumentException{
        reducirStockProducto(codigo, cantidad);
        Producto producto = obtenerProducto(codigo);
        double valorCobrado = producto.getValorVenta()*cantidad;
        return new Venta(producto.getNombre(), cantidad, valorCobrado);
    }

    public void verificarStockProductoDisponible(int codigo, int cantidad) throws IllegalArgumentException{
        Producto producto = this.misProductos.get(codigo);
        if (producto ==null){
            throw new IllegalArgumentException("No Se Encuentra Ese Producto");
        }
        producto.validarEstadoParaVenta();
        if (cantidad > producto.getStock()){
            throw new IllegalArgumentException("La Cantidad A Reducir Es Mayor A La Cantidad Existente");
        }
    }

}

