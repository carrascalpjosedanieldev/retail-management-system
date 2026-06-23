package ProyectoPropio1.dominio;

import ProyectoPropio1.excepciones.*;
import ProyectoPropio1.dto.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tienda {

    //ATRIBUTOS:

    private String nombreTienda;

    private final Map<Integer, Inventario> misInventarios;

    private final Map<Integer, Servicio> serviciosOfrecidos;

    //GETTERS Y SETTERS:

    public String getNombreTienda() {
        return nombreTienda;
    }

    private void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
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
        boolean hayProductos = false;
        if (tiendaNoTieneInventarios()){
            return false;
        }
        for (Inventario inventario:this.misInventarios.values()){
            if (inventario.tieneProductos()){
                hayProductos = true;
                break;
            }
        }
        return hayProductos;
    }

    private Inventario obtenerInventario(int id) throws InventarioNoEncontradoException {
        Inventario inventario = this.misInventarios.get(id);
        if (inventario==null){
            throw new InventarioNoEncontradoException("No se encontró ningún Inventario con el ID: " + id);
        }
        return inventario;
    }

    public boolean noExisteProductoEnInventario(int idInventario, int codigoProducto) throws InventarioNoEncontradoException{
        return !obtenerInventario(idInventario).buscarProducto(codigoProducto);
    }

    //METODOS PARA MOSTRAR INFORMACION:

    public DetalleInventarioGeneralDTO exportarDatosInventarioGeneral(){
        if (tiendaNoTieneInventarios()){
            throw new IllegalStateException("No hay Inventarios");
        }
        List<DatosInventarioDTO> inventarioGeneral = new ArrayList<>();
        for (Inventario inventario:this.misInventarios.values()){
            inventarioGeneral.add(inventario.exportarDatosInventario());
        }
        return new DetalleInventarioGeneralDTO(inventarioGeneral);
    }

    public DetalleInventarioDTO exportarDetalleUnInventario(int id) throws InventarioNoEncontradoException{
        Inventario inventario = obtenerInventario(id);
        return inventario.exportarDetalleInventario();
    }

    public DatosInventarioDTO exportarDatosUnInventario(int id) throws InventarioNoEncontradoException{
        Inventario inventario = obtenerInventario(id);
        return inventario.exportarDatosInventario();
    }

    //METODOS PARA MODIFICAR TIENDA:

    public void cambiarNombreTienda(String nuevoNombre){
        if (nuevoNombre==null || nuevoNombre.isBlank()){
            throw new IllegalArgumentException("Nombre Vacio");
        }
        setNombreTienda(nuevoNombre);
    }

    public void agregarInventario(String nombre,int capacidadMaxima){
        Inventario inventario = new Inventario(nombre, capacidadMaxima);
        this.misInventarios.put(inventario.getNumeroId(),inventario);
    }

    public void eliminarInventarioVacio(int id) throws InventarioNoVacioException, InventarioNoEncontradoException{
        Inventario inventario = obtenerInventario(id);
        if (inventario.tieneProductos()){
            throw new InventarioNoVacioException("El Inventario de ID -" + id + "-NO Esta Vacio");
        }
        this.misInventarios.remove(id);
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int id , String nombreNuevoInv) throws InventarioNoEncontradoException{
        Inventario inventario = obtenerInventario(id);
        inventario.cambiarNombreInventario(nombreNuevoInv);
    }

    public DatosTotalesProductoDTO agregarProductoAUnInv(int id, Producto producto) throws InventarioNoEncontradoException, CapacidadExcedidaException {
        Inventario inventario = obtenerInventario(id);
        return inventario.agregarUnProducto(producto);
    }

    public void eliminarProductoAUnInv(int id, int codigo)  throws InventarioNoEncontradoException, ProductoNoEncontradoException {
        Inventario inventario = obtenerInventario(id);
        inventario.eliminarUnProducto(codigo);
    }

    public boolean buscarProductoAUnInv(int id, int codigo) throws InventarioNoEncontradoException{
        Inventario inventario = obtenerInventario(id);
        return inventario.buscarProducto(codigo);
    }

    public void moverProductoAOtroInventario(int idSalida, int idLlegada, int codigo) throws InventarioNoEncontradoException,CapacidadExcedidaException,ProductoNoEncontradoException{
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

    public DatosVentaProductoDTO obtenerDatosProductoDeInventario(int idInv, int codigoProd) throws InventarioNoEncontradoException,ProductoNoEncontradoException{
        Inventario inventario = obtenerInventario(idInv);
        return inventario.obtenerDatosProducto(codigoProd);
    }

    public void verificarStockProductoParaVenta(int id, int codigo, int cantidad) throws InventarioNoEncontradoException,StockInsuficienteException,ProductoNoEncontradoException{
        Inventario inventario = obtenerInventario(id);
        inventario.verificarStockProductoDisponible(codigo, cantidad);
    }

    public Venta reducirStockProductoParaVenta(int idInv, int codigoProd, int cantidad) throws InventarioNoEncontradoException,ProductoNoEncontradoException,StockInsuficienteException{
        Inventario inventario = obtenerInventario(idInv);
        return inventario.reducirStockProductoPorVenta(codigoProd,cantidad);
    }

    public void cambiarNombreDeProductoDeInventario(int id, int codigo, String nombreNuevo) throws InventarioNoEncontradoException,ProductoNoEncontradoException{
        obtenerInventario(id).actualizarNombreProducto(codigo, nombreNuevo);
    }

    public void actualizarValorVentaPorPorcentajeDeProductoDeInventario(int id, int codigo, double porcentaje) throws InventarioNoEncontradoException,ProductoNoEncontradoException{
        obtenerInventario(id).actualizarValorVentaPorPorcentaje(codigo, porcentaje);
    }

    public void actualizarValorCompraDeProductoDeInventario(int id, int codigo, double valorNuevo) throws InventarioNoEncontradoException,ProductoNoEncontradoException{
        obtenerInventario(id).actualizarValorCompra(codigo, valorNuevo);
    }

    public void aumentarStockDeProductoDeInventario(int id, int codigo, int cantidad) throws InventarioNoEncontradoException,ProductoNoEncontradoException,CapacidadExcedidaException{
        obtenerInventario(id).agregarStockProducto(codigo, cantidad);
    }

    public void reducirStockDeProductoDeInventario(int id, int codigo, int cantidad) throws InventarioNoEncontradoException,ProductoNoEncontradoException,StockInsuficienteException{
        obtenerInventario(id).reducirStockProducto(codigo, cantidad);
    }

    //METODOS SERVICIOS:

    public boolean tiendaNoTieneServicios(){
        return this.serviciosOfrecidos.isEmpty();
    }

    public Servicio obtenerServicio(int codigo) throws ServicioNoEncontradoException {
        Servicio servicio = this.serviciosOfrecidos.get(codigo);
        if (servicio==null){
            throw new ServicioNoEncontradoException("El Servicio de Codigo-" + codigo + "- No se encuentra en la Tienda");
        }
        return servicio;
    }

    public DatosServicioDTO obtenerDatosUnServicio(int codigoServicio) throws ServicioNoEncontradoException{
        Servicio servicio = obtenerServicio(codigoServicio);
        return new DatosServicioDTO(servicio.getCodigoServicio(), servicio.getNombre(), servicio.getValorCobrado());
    }

    public void registrarServicioAlCatalogo(Servicio servicio){
        this.serviciosOfrecidos.put(servicio.getCodigoServicio(), servicio);
    }

    public void eliminarServicioDelCatalogo(int codigoServicio) throws ServicioNoEncontradoException{
        Servicio servicio = obtenerServicio(codigoServicio);
        this.serviciosOfrecidos.remove(servicio.getCodigoServicio());
    }

    public void cambiarNombreServicio(int codigoServicio, String nombreServicio) throws ServicioNoEncontradoException{
        Servicio servicio = obtenerServicio(codigoServicio);
        servicio.cambiarNombreServicio(nombreServicio);
    }

    public void cambiarPrecioServicio(int codigoServicio, double precioNuevo) throws ServicioNoEncontradoException{
        Servicio servicio = obtenerServicio(codigoServicio);
        servicio.cambiarPrecioBase(precioNuevo);
    }

    public DatosCatalogoServiciosDTO exportarCatalogoServicios(){
        if (tiendaNoTieneServicios()){
            throw new IllegalStateException("No hay Servicios disponibles");
        }
        List<DatosServicioDTO> listaServicios = new ArrayList<>();
        for (Servicio servicio: this.serviciosOfrecidos.values()){
            listaServicios.add(servicio.exportarDatosServicio());
        }
        return new DatosCatalogoServiciosDTO(listaServicios);
    }

}

