package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.excepciones.*;
import ProyectoPropio1.dto.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorTienda {

    //ATRIBUTOS:

    private final Tienda miTienda;

    private final GestorVentas miGestorDeVentas;

    private Carrito miCarrito;

    //CONSTRUCTOR:

    public ControladorTienda(Tienda tienda, GestorVentas gestorVentas) {
        this.miTienda = tienda;
        this.miGestorDeVentas = gestorVentas;
        this.miCarrito = new Carrito();
    }

    public boolean tiendaNoTieneInventarios(){
        return this.miTienda.tiendaNoTieneInventarios();
    }

    public boolean inventarioNoTieneProductos(int idInventario){
        return !this.miTienda.inventarioTieneProductos(idInventario);
    }

    public boolean tiendaNoTieneServicios(){
        return this.miTienda.tiendaNoTieneServicios();
    }

    public boolean noExisteProductoEnInventario(int idInventario, int codigoProducto) throws InventarioNoEncontradoException {
        return this.miTienda.noExisteProductoEnInventario(idInventario, codigoProducto);
    }

    public boolean ningunInventarioTieneProductos(){
        return this.miTienda.ningunInventarioTieneProductos();
    }

    //------------> METODOS DE CONTROL DE TIENDA:

    public String obtenerNombreTienda() {
        return this.miTienda.getNombreTienda();
    }

    public void cambiarNombreTienda(String nombreNuevo){
        this.miTienda.cambiarNombreTienda(nombreNuevo);
    }

    public void agregarInventarioATienda(String nombre, int capacidad){
        this.miTienda.agregarInventario(nombre,capacidad);
    }


    public DetalleInventarioGeneralDTO mostrarInfoInventariosDeTienda(){
        return this.miTienda.exportarDatosInventarioGeneral();
    }

    public DetalleInventarioDTO obtenerDetalleInventarioDeTienda(int id) throws InventarioNoEncontradoException{
        return this.miTienda.exportarDetalleUnInventario(id);
    }

    public DatosInventarioDTO mostraInfoInventarioDeTienda(int id) throws InventarioNoEncontradoException{
        return this.miTienda.exportarDatosUnInventario(id);
    }

    public void cambiarNombreAUnInventario(int id, String nombreNuevo) throws InventarioNoEncontradoException{
        this.miTienda.cambiarNombreAUnInventario(id, nombreNuevo);
    }

    public DatosTotalesProductoDTO registrarProductoRopa(int id, String nombre, double valorCompra, int stock, String tallaString) throws InventarioNoEncontradoException, CapacidadExcedidaException {
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La talla ingresada no esta entre las opciones (Usa S, M, L o XL).");
        }
        Producto producto = new ProductoRopa(nombre, valorCompra, stock, talla);
        return this.miTienda.agregarProductoAUnInv(id, producto);
    }

    public DatosTotalesProductoDTO registrarProductoPerecedero(int id, String nombre, double valorCompra, int stock, LocalDate fechaVencimiento) throws InventarioNoEncontradoException, CapacidadExcedidaException {
        Producto producto = new ProductoPerecedero(nombre, valorCompra, stock, fechaVencimiento);
        return this.miTienda.agregarProductoAUnInv(id, producto);
    }

    public void cambiarNombreDeProductoDeInventario(int id, int codigo, String nombreNuevo) throws InventarioNoEncontradoException, ProductoNoEncontradoException {
        this.miTienda.cambiarNombreDeProductoDeInventario(id, codigo, nombreNuevo);
    }

    public void actualizarValorVentaPorPorcentajeDeProductoDeInventario(int id, int codigo, double porcentaje) throws InventarioNoEncontradoException, ProductoNoEncontradoException {
        this.miTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(id, codigo, porcentaje);
    }

    public void actualizarValorCompraDeProductoDeInventario(int id, int codigo, double valorNuevo) throws InventarioNoEncontradoException, ProductoNoEncontradoException {
        this.miTienda.actualizarValorCompraDeProductoDeInventario(id, codigo, valorNuevo);
    }

    public void aumentarStockDeProductoDeInventario(int id, int codigo, int cantidad) throws InventarioNoEncontradoException, ProductoNoEncontradoException, CapacidadExcedidaException {
        this.miTienda.aumentarStockDeProductoDeInventario(id, codigo, cantidad);
    }

    public void reducirStockDeProductoDeInventario(int id, int codigo, int cantidad) throws InventarioNoEncontradoException, ProductoNoEncontradoException, StockInsuficienteException {
        this.miTienda.reducirStockDeProductoDeInventario(id, codigo, cantidad);
    }

    public void eliminarProductoAInventario(int id, int codigo) throws InventarioNoEncontradoException, ProductoNoEncontradoException {
        this.miTienda.eliminarProductoAUnInv(id, codigo);
    }

    public boolean buscarProductoAInventario(int id, int codigo) throws InventarioNoEncontradoException{
        return this.miTienda.buscarProductoAUnInv(id ,codigo);
    }

    public void moverProductoAInventario(int idSalida, int idLlegada, int codigo) throws InventarioNoEncontradoException, ProductoNoEncontradoException,CapacidadExcedidaException {
        this.miTienda.moverProductoAOtroInventario(idSalida, idLlegada, codigo);
    }

    public DatosCatalogoServiciosDTO exportarCatalogoServicios(){
        return this.miTienda.exportarCatalogoServicios();
    }

    public void eliminarInventarioVacio(int id) throws InventarioNoEncontradoException, InventarioNoVacioException {
        this.miTienda.eliminarInventarioVacio(id);
    }

    public void registrarServicioNuevo(String nombreServicio, double precioBase){
        Servicio servicio = new Servicio(nombreServicio, precioBase);
        this.miTienda.registrarServicioAlCatalogo(servicio);
    }

    public void eliminarServicioDeTienda(int codigoServicio) throws ServicioNoEncontradoException {
        this.miTienda.eliminarServicioDelCatalogo(codigoServicio);
    }

    public void cambiarNombreServicio(int codigoServicio, String nombreNuevo) throws ServicioNoEncontradoException {
        this.miTienda.cambiarNombreServicio(codigoServicio, nombreNuevo);
    }

    public void cambiarPrecioServicio(int codigoServicio, double precioNuevo) throws ServicioNoEncontradoException {
        this.miTienda.cambiarPrecioServicio(codigoServicio, precioNuevo);
    }

    public Servicio obtenerServicio(int codigoServicio) throws ServicioNoEncontradoException {
        return this.miTienda.obtenerServicio(codigoServicio);
    }

    public void agregarServicioAlCarrito(int codigoServicio){
        this.miCarrito.agregarServicio(codigoServicio);
    }

    //METODOS CONTROL CARRITO:

    public void abrirCarritoSesion(){
        this.miCarrito = new Carrito();
    }

    public void agregarItemASesion(int idInv, int codigoProd, int cantidad){
        SolicitudItemDTO solicitudItem = new SolicitudItemDTO(idInv, codigoProd, cantidad);
        this.miCarrito.agregarItem(solicitudItem);
    }

    public VistaPreviaCarritoDTO obtenerVistaPreviaCarrito() throws InventarioNoEncontradoException,ProductoNoEncontradoException,ServicioNoEncontradoException{
        List<ItemCarritoDTO> itemsDelCarrito = new ArrayList<>();
        List<DatosServicioDTO> serviciosAdicionales = new ArrayList<>();
        double totalAproximado = 0;
        for (SolicitudItemDTO solicitudItem:this.miCarrito.getItems()){
            DatosVentaProductoDTO datosProducto = miTienda.obtenerDatosProductoDeInventario(solicitudItem.idInventario(), solicitudItem.codigoProducto());
            double precioUnitario = datosProducto.precio();
            double subTotal = solicitudItem.cantidad()*datosProducto.precio();
            totalAproximado+=subTotal;
            ItemCarritoDTO itemCarritoDTO = new ItemCarritoDTO(datosProducto.nombre(), solicitudItem.cantidad(), precioUnitario, subTotal);
            itemsDelCarrito.add(itemCarritoDTO);
        }
        for (int codigoServicio:this.miCarrito.getCodigosServiciosAdicionales()){
            DatosServicioDTO datosServicio = this.miTienda.obtenerDatosUnServicio(codigoServicio);
            totalAproximado+=datosServicio.precioFinal();
            serviciosAdicionales.add(datosServicio);
        }
        return new VistaPreviaCarritoDTO(itemsDelCarrito, serviciosAdicionales, totalAproximado);
    }


    //METODOS CONTROL DE GESTOR DE VENTAS:

    public boolean registroVentasEstaVacio(){
        return this.miGestorDeVentas.registroVentasEstaVacio();
    }

    public HistorialVentasDTO obtenerHistoralGestor(){
        return this.miGestorDeVentas.obtenerHistorial();
    }

    public FacturaDTO confirmarYProcesarVentaActual() throws CarritoVacioException,InventarioNoEncontradoException,StockInsuficienteException,ProductoNoEncontradoException,ServicioNoEncontradoException,CapacidadExcedidaException {
        FacturaDTO datosFacturaProcesada = this.miGestorDeVentas.procesarVentaMultiproducto(this.miCarrito);
        this.miCarrito=null;
        return datosFacturaProcesada;
    }

}

