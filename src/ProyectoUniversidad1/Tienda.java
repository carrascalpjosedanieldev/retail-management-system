package ProyectoUniversidad1;

import java.util.HashMap;
import java.util.Map;

public class Tienda { //x LINEAS NETAS DE 202 LINEAS TOTALES

    //ATRIBUTOS:

    private String nombreTienda;

    private final Map<Integer,Inventario> misInventarios;

    //GETTERS Y SETTERS:

    public String getNombreTienda() {
        return nombreTienda;
    }

    private void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    //CONSTRUCTOR:

    public Tienda(String nombreTienda) throws IllegalArgumentException{
        if (nombreTienda==null || nombreTienda.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        this.nombreTienda = nombreTienda;
        this.misInventarios = new HashMap<>();
    }

    //METODOS:

    public boolean tiendaTieneInventarios(){
        return !this.misInventarios.isEmpty();
    }

    public boolean inventarioTieneProductos(int id){
        Inventario inventario = obtenerInventarioValido(id);
        return inventario.tieneProductos();
    }

    private Inventario obtenerInventarioValido(int id) throws IllegalArgumentException{
        Inventario inventario = this.misInventarios.get(id);
        if (inventario==null){
            throw new IllegalArgumentException("Ese Inventario No existe");
        }
        return inventario;
    }

    private void validarQueExistanInventarios(){
        if (!tiendaTieneInventarios()){
            throw new IllegalArgumentException("No Hay Inventarios");
        }
    }

    public String mostrarInfoInventarios() throws IllegalArgumentException{
        validarQueExistanInventarios();
        StringBuilder informacion = new StringBuilder();
        informacion.append("----------------------------------------------------------------------------------------------------------------------------------");
        informacion.append(System.lineSeparator());
        for (Inventario inventario :this.misInventarios.values()){
            informacion.append(inventario.informacionMinima());
        }
        informacion.append(System.lineSeparator());
        informacion.append("----------------------------------------------------------------------------------------------------------------------------------");
        return informacion.toString();
    }

    public String obtenerDetalleUnInventario(int id) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        return inventario.obtenerDetalle();
    }

    public String mostrarInfoStockUnInventario(int id) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        return inventario.mostrarInformacionStock();
    }

    //METODOS MODIFICAR TIENDA:

    public void cambiarNombreTienda(String nuevoNombre) throws IllegalArgumentException{
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombreTienda(nuevoNombre);
    }

    public void agregarInventario(String nombre,int capacidadMaxima) throws IllegalArgumentException{
        Inventario inventario = new Inventario(nombre, capacidadMaxima);
        this.misInventarios.put(inventario.getNumeroId(),inventario);
    }

    public boolean eliminarInventarioVacio(int id) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        if (!inventario.tieneProductos()){
            this.misInventarios.remove(id);
            return true;
        }
        return false;
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public void moverProductoAOtroInventario(int idSalida, int idLlegada, int codigo) throws IllegalArgumentException{
        Inventario inventarioSalida = obtenerInventarioValido(idSalida);
        Inventario inventarioLlegada = obtenerInventarioValido(idLlegada);
        if (idSalida == idLlegada) {
            throw new IllegalArgumentException("Inventario De Salida Y De Llegada Iguales");
        }
        if (inventarioLlegada.buscarProducto(codigo)) {
            throw new IllegalArgumentException("El producto ya existe en el inventario de destino");
        }
        Producto producto = inventarioSalida.obtenerProducto(codigo);
        inventarioLlegada.agregarProductoHecho(producto);
        inventarioSalida.eliminarUnProducto(codigo);
    }

    //METODOS UTILIZAR TIENDA:

    public String mostrarInventarioGeneral() throws IllegalArgumentException{
        validarQueExistanInventarios();
        StringBuilder inventarioGeneral = new StringBuilder();
        for (Inventario inventario: this.misInventarios.values()){
            inventarioGeneral.append(inventario.obtenerDetalle());
        }
        return inventarioGeneral.toString();
    }

    //METODOS VENDER PRODUCTO:

    public double venderProducto(int id, int codigo,int cantidad) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        return inventario.venderProducto(codigo,cantidad);
    }

    //METODOS MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int id , String nombreNuevoInv) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        inventario.cambiarNombreInventario(nombreNuevoInv);
    }

    public Producto agregarProductoAUnInv(int id, String nombreProducto, double valorCompra) throws IllegalArgumentException{
        return agregarProductoAUnInv(id, nombreProducto, valorCompra, 0);
    }

    public Producto agregarProductoAUnInv(int id,String nombreProducto,double valorCompra,int stock) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        return inventario.agregarUnProducto(nombreProducto,valorCompra,stock);
    }

    public void eliminarProductoAUnInv(int id, int codigo) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        inventario.eliminarUnProducto(codigo);
    }

    public void buscarProductoAUnInv(int id, int codigo) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        inventario.buscarProducto(codigo);
    }

    //METODOS MODIFICAR PRODUCTO:

    public void actualizarNombreInventarioProducto(int id, int codigo, String nombre) throws IllegalArgumentException{
        Inventario inventario = obtenerInventarioValido(id);
        inventario.actualizarNombreProducto(codigo, nombre);
    }

    public void actualizarValorVentaPorcentajeInventarioProducto(int id, int codigo, double porcentaje){
        Inventario inventario = obtenerInventarioValido(id);
        inventario.actualizarValorVentaPorPorcentaje(codigo, porcentaje);
    }

    public void actualizarValorVentaPrecioInventarioProducto(int id, int codigo, double precio){
        Inventario inventario = obtenerInventarioValido(id);
        inventario.actualizarValorVentaPorPrecio(codigo, precio);
    }

    public void actualizarValorCompraInventarioProducto(int id, int codigo, double valorNuevo){
        Inventario inventario = obtenerInventarioValido(id);
        inventario.actualizarValorCompra(codigo, valorNuevo);
    }

    public void actualizarStockInventarioProducto(int id, int codigo, int cantidad){
        Inventario inventario = obtenerInventarioValido(id);
        inventario.agregarStockProducto(codigo, cantidad);
    }

    public void reducirStockInventarioProducto(int id, int codigo, int cantidad){
        Inventario inventario = obtenerInventarioValido(id);
        inventario.reducirStockProducto(codigo, cantidad);
    }

}

