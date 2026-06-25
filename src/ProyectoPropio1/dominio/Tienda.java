package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.*;

import java.time.LocalDate;
import java.util.*;

public class Tienda {

    //ATRIBUTOS:

    private String nombreTienda;

    private final Map<Integer, Inventario> misInventarios;

    private final Map<Integer, Servicio> serviciosOfrecidos;

    private int contadorInventarios = 0;

    private int contadorServicios = 1;

    //GETTERS Y SETTERS:

    public String getNombreTienda() {
        return nombreTienda;
    }

    private void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    public List<Inventario> getMisInventarios(){
        return List.copyOf(this.misInventarios.values());
    }

    public List<Servicio> getServiciosOfrecidos(){
        return List.copyOf(this.serviciosOfrecidos.values());
    }

    //CONSTRUCTOR:

    public Tienda(String nombreTienda){
        if (nombreTienda==null || nombreTienda.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        this.nombreTienda = nombreTienda;
        this.misInventarios = new LinkedHashMap<>();
        this.serviciosOfrecidos = new LinkedHashMap<>();
    }

    //METODOS DE VALIDACION PARA INVENTARIO Y PRODUCTOS:

    public boolean tiendaNoTieneInventarios(){
        return this.misInventarios.isEmpty();
    }

    public boolean inventarioTieneProductos(int id){
        Inventario inventario = obtenerInventario(id);
        return inventario.tieneProductos();
    }

    public boolean ningunInventarioTieneProductos(){
        boolean noHayProductos = true;
        if (tiendaNoTieneInventarios()){
            return false;
        }
        for (Inventario inventario:this.misInventarios.values()){
            if (inventario.tieneProductos()){
                noHayProductos = false;
                break;
            }
        }
        return noHayProductos;
    }

    public Inventario obtenerInventario(int id){
        Inventario inventario = this.misInventarios.get(id);
        if (inventario==null){
            throw new InventarioNoEncontradoException("No se encontró ningún Inventario con el ID: " + id);
        }
        return inventario;
    }

    public boolean noExisteProductoEnInventario(int idInventario, int codigoProducto){
        return !obtenerInventario(idInventario).buscarProducto(codigoProducto);
    }

    //METODOS PARA MODIFICAR TIENDA:

    public void cambiarNombreTienda(String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombreTienda(nuevoNombre);
    }

    public void agregarInventario(String nombre,int capacidadMaxima){
        contadorInventarios ++;
        Inventario inventario = new Inventario(contadorInventarios, nombre, capacidadMaxima);
        this.misInventarios.put(inventario.getNumeroId(),inventario);
    }

    public void eliminarInventarioVacio(int id){
        Inventario inventario = obtenerInventario(id);
        if (inventario.tieneProductos()){
            throw new InventarioNoVacioException("El Inventario de ID -" + id + "-NO Esta Vacio");
        }
        this.misInventarios.remove(id);
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int id , String nombreNuevoInv){
        Inventario inventario = obtenerInventario(id);
        inventario.cambiarNombreInventario(nombreNuevoInv);
    }

    public int agregarProductoAUnInv(int id, Producto producto){
        Inventario inventario = obtenerInventario(id);
        return inventario.agregarUnProducto(producto);
    }

    public void eliminarProductoAUnInv(int id, int codigo){
        Inventario inventario = obtenerInventario(id);
        inventario.eliminarUnProducto(codigo);
    }

    public boolean buscarProductoAUnInv(int id, int codigo){
        Inventario inventario = obtenerInventario(id);
        return inventario.buscarProducto(codigo);
    }

    public void moverProductoAOtroInventario(int idSalida, int idLlegada, int codigo){
        Inventario inventarioSalida = obtenerInventario(idSalida);
        Inventario inventarioLlegada = obtenerInventario(idLlegada);
        if (idSalida == idLlegada) {
            throw new IllegalArgumentException("Inventario De Salida Y De Llegada Iguales");
        }
        if (inventarioLlegada.buscarProducto(codigo)) {
            throw new IllegalArgumentException("El Producto ya existe en el Inventario de destino");
        }
        Producto producto = inventarioSalida.obtenerProducto(codigo);
        inventarioLlegada.agregarUnProducto(producto);
        inventarioSalida.eliminarUnProducto(codigo);
    }

    //METODOS DE MODIFICAR PRODUCTO:

    public Producto pedirProducto(int idInventario, int codigoProducto, int cantidad, LocalDate fecha){
        verificarStockProductoParaVenta(idInventario, codigoProducto, cantidad, fecha);
        return reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidad);
    }

    public Producto obtenerProducto(int idInventario, int codigoProducto){
        return obtenerInventario(idInventario).obtenerProducto(codigoProducto);
    }

    public void verificarStockProductoParaVenta(int id, int codigo, int cantidad, LocalDate fecha){
        Inventario inventario = obtenerInventario(id);
        inventario.verificarStockProductoDisponible(codigo, cantidad, fecha);
    }

    public void cambiarNombreDeProductoDeInventario(int id, int codigo, String nombreNuevo){
        obtenerInventario(id).actualizarNombreProducto(codigo, nombreNuevo);
    }

    public void actualizarValorVentaPorPorcentajeDeProductoDeInventario(int id, int codigo, double porcentaje){
        obtenerInventario(id).actualizarValorVentaPorPorcentaje(codigo, porcentaje);
    }

    public void actualizarValorCompraDeProductoDeInventario(int id, int codigo, double valorNuevo){
        obtenerInventario(id).actualizarValorCompra(codigo, valorNuevo);
    }

    public void aumentarStockDeProductoDeInventario(int id, int codigo, int cantidad){
        obtenerInventario(id).agregarStockProducto(codigo, cantidad);
    }

    public Producto reducirStockDeProductoDeInventario(int id, int codigo, int cantidad){
        return obtenerInventario(id).reducirStockProducto(codigo, cantidad);
    }

    //METODOS SERVICIOS:

    public int asignarCodigoServicio(){
        return this.contadorServicios++;
    }

    public boolean tiendaNoTieneServicios(){
        return this.serviciosOfrecidos.isEmpty();
    }

    public Servicio obtenerServicio(int codigo){
        Servicio servicio = this.serviciosOfrecidos.get(codigo);
        if (servicio==null){
            throw new ServicioNoEncontradoException("El Servicio de Codigo-" + codigo + "- No se encuentra en la Tienda");
        }
        return servicio;
    }

    public void registrarServicioAlCatalogo(Servicio servicio){
        this.serviciosOfrecidos.put(servicio.getCodigoServicio(), servicio);
    }

    public void eliminarServicioDelCatalogo(int codigoServicio){
        Servicio servicio = obtenerServicio(codigoServicio);
        this.serviciosOfrecidos.remove(servicio.getCodigoServicio());
    }

    public void cambiarNombreServicio(int codigoServicio, String nombreServicio){
        Servicio servicio = obtenerServicio(codigoServicio);
        servicio.cambiarNombreServicio(nombreServicio);
    }

    public void cambiarPrecioServicio(int codigoServicio, double precioNuevo){
        Servicio servicio = obtenerServicio(codigoServicio);
        servicio.cambiarPrecioBase(precioNuevo);
    }

}

