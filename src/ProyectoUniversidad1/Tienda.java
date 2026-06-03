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
        Inventario inventario = obtenerInventario(id);
        return inventario.tieneProductos();
    }

    public Inventario obtenerInventario(int id) throws IllegalArgumentException{
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

    public String obtenerDetalleInventario(int id) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        return inventario.obtenerDetalle();
    }

    public String mostrarInfoStockInventario(int id) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
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
        Inventario inventario = obtenerInventario(id);
        if (!inventario.tieneProductos()){
            this.misInventarios.remove(id);
            return true;
        }
        return false;
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public void moverProductoAOtroInventario(int idSalida, int idLlegada, int codigo) throws IllegalArgumentException{
        Inventario inventarioSalida = obtenerInventario(idSalida);
        Inventario inventarioLlegada = obtenerInventario(idLlegada);
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

    //METODOS MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int id , String nombreNuevoInv) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        inventario.cambiarNombreInventario(nombreNuevoInv);
    }

    public Producto agregarProductoAUnInv(int id, String nombreProducto, double valorCompra) throws IllegalArgumentException{
        return agregarProductoAUnInv(id, nombreProducto, valorCompra, 0);
    }

    public Producto agregarProductoAUnInv(int id,String nombreProducto,double valorCompra,int stock) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        return inventario.agregarUnProducto(nombreProducto,valorCompra,stock);
    }

    public void eliminarProductoAUnInv(int id, int codigo) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        inventario.eliminarUnProducto(codigo);
    }

    public void buscarProductoAUnInv(int id, int codigo) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        inventario.buscarProducto(codigo);
    }

}

