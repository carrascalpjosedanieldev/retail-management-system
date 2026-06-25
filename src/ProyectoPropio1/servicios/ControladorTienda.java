package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dto.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControladorTienda {

    //ATRIBUTOS:

    private final Tienda miTienda;

    private final GestorVentas miGestorDeVentas;

    private Carrito miCarrito;

    private final EnsambladorDTOProducto miEnsambladorDTOProducto;

    private final EnsambladorDTOInventario miEnsambladorDTOInventario;

    private final EnsambladorDTOFactura miEnsambladorDTOFactura;

    private final EnsambladorDTOServicio miEnsambladorDTOServicio;

    private final EnsambladorDTOTienda miEnsambladorDTOTienda;

    private final EnsambladorDTOHistorialFacturas miEnsambladorDTOHistorialFacturas;

    //CONSTRUCTOR:

    public ControladorTienda(Tienda tienda, GestorVentas gestorVentas) {
        this.miTienda = tienda;
        this.miGestorDeVentas = gestorVentas;
        this.miCarrito = new Carrito();
        this.miEnsambladorDTOProducto = new EnsambladorDTOProducto();
        this.miEnsambladorDTOInventario = new EnsambladorDTOInventario(this.miEnsambladorDTOProducto);
        this.miEnsambladorDTOFactura = new EnsambladorDTOFactura();
        this.miEnsambladorDTOServicio = new EnsambladorDTOServicio();
        this.miEnsambladorDTOTienda = new EnsambladorDTOTienda(this.miEnsambladorDTOServicio, this.miEnsambladorDTOInventario);
        this.miEnsambladorDTOHistorialFacturas = new EnsambladorDTOHistorialFacturas(this.miEnsambladorDTOFactura);
    }

    //METODOS DE VALIDACION:

    public boolean tiendaNoTieneInventarios(){
        return this.miTienda.tiendaNoTieneInventarios();
    }

    public boolean inventarioNoTieneProductos(int idInventario){
        return !this.miTienda.inventarioTieneProductos(idInventario);
    }

    public boolean tiendaNoTieneServicios(){
        return this.miTienda.tiendaNoTieneServicios();
    }

    public boolean noExisteProductoEnInventario(int idInventario, int codigoProducto){
        return this.miTienda.noExisteProductoEnInventario(idInventario, codigoProducto);
    }

    public boolean ningunInventarioTieneProductos(){
        return this.miTienda.ningunInventarioTieneProductos();
    }

    // METODOS PARA EXPORTAR DATOS DTO:

    public DatosTotalesProductoDTO obtenerDatosTotalesProducto (int idInventario, int codigoProducto){
        LocalDate fecha = LocalDate.now();
        Producto producto = this.miTienda.obtenerProducto(idInventario, codigoProducto);
        return this.miEnsambladorDTOProducto.ensamblarDatosTotalesProducto(producto, fecha);
    }

    public DatosVentaProductoDTO obtenerDatosVentaProducto (int idInventario, int codigoProducto){
        LocalDate fecha = LocalDate.now();
        Producto producto = this.miTienda.obtenerProducto(idInventario, codigoProducto);
        return this.miEnsambladorDTOProducto.ensamblarDatosVentaProducto(producto, fecha);
    }

    public DatosInventarioDTO obtenerDatosInventario (int idInventario){
        Inventario inventario = this.miTienda.obtenerInventario(idInventario);
        return this.miEnsambladorDTOInventario.ensamblarDatosInventario(inventario);
    }

    public VistaPreviaCarritoDTO obtenerVistaPreviaCarrito() {
        List<ItemCarritoDTO> itemsDelCarrito = new ArrayList<>();
        List<DatosServicioDTO> serviciosAdicionales = new ArrayList<>();
        double totalAproximado = 0;
        LocalDate fecha = LocalDate.now();
        for (Map.Entry<ReferenciaItem,Integer> entry :this.miCarrito.getItems().entrySet()){
            ReferenciaItem referenciaItem = entry.getKey();
            int cantidad = entry.getValue();
            Producto producto = this.miTienda.obtenerProducto(referenciaItem.idInventario(), referenciaItem.codigoProducto());
            DatosVentaProductoDTO datosProducto = this.miEnsambladorDTOProducto.ensamblarDatosVentaProducto(producto, fecha);
            double precioUnitario = datosProducto.precio();
            double subTotal = cantidad*datosProducto.precio();
            totalAproximado += subTotal;
            ItemCarritoDTO itemCarritoDTO = new ItemCarritoDTO(datosProducto.nombre(), cantidad, precioUnitario, subTotal);
            itemsDelCarrito.add(itemCarritoDTO);
        }
        for (int codigoServicio:this.miCarrito.getCodigosServiciosAdicionales()){
            Servicio servicio = this.miTienda.obtenerServicio(codigoServicio);
            DatosServicioDTO datosServicio = this.miEnsambladorDTOServicio.ensamblarServicio(servicio);
            totalAproximado += datosServicio.precioFinal();
            serviciosAdicionales.add(datosServicio);
        }
        return new VistaPreviaCarritoDTO(itemsDelCarrito, serviciosAdicionales, totalAproximado);
    }

    public DetalleInventarioGeneralDTO obtenerDetalleInventarioGeneral(){
        return this.miEnsambladorDTOTienda.ensamblarDetalleInventarioGeneral(this.miTienda);
    }

    public DetalleInventarioDTO obtenerDetalleInventario(int idInventario){
        LocalDate fecha = LocalDate.now();
        Inventario inventario = this.miTienda.obtenerInventario(idInventario);
        return this.miEnsambladorDTOInventario.ensamblarDetalleInventario(inventario, fecha);
    }

    public DatosCatalogoServiciosDTO obtenerCatalogoServicios(){
        return this.miEnsambladorDTOTienda.ensamblarDatosCatalogoServicios(this.miTienda);
    }

    public FacturaDTO obtenerDatosFactura(int idFactura){
        return this.miEnsambladorDTOFactura.ensamblarFactura(this.miGestorDeVentas.obtenerFactura(idFactura));
    }

    public HistorialVentasDTO obtenerHistorialVentas(){
        return this.miEnsambladorDTOHistorialFacturas.ensamblarHistorialFactura(this.miGestorDeVentas.obtenerHistorial());
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

    public void cambiarNombreAUnInventario(int id, String nombreNuevo){
        this.miTienda.cambiarNombreAUnInventario(id, nombreNuevo);
    }

    public int registrarProductoRopa(int id, String nombre, double valorCompra, int stock, String tallaString){
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La talla ingresada no esta entre las opciones (Usa S, M, L o XL).");
        }
        Inventario inventario = this.miTienda.obtenerInventario(id);
        Producto producto = new ProductoRopa(inventario.asignarCodigoProducto(), nombre, valorCompra, stock, talla);
        return this.miTienda.agregarProductoAUnInv(id, producto);
    }

    public int registrarProductoPerecedero(int id, String nombre, double valorCompra, int stock, LocalDate fechaVencimiento){
        Inventario inventario = this.miTienda.obtenerInventario(id);
        Producto producto = new ProductoPerecedero(inventario.asignarCodigoProducto(), nombre, valorCompra, stock, fechaVencimiento);
        return this.miTienda.agregarProductoAUnInv(id, producto);
    }

    public void cambiarNombreDeProductoDeInventario(int id, int codigo, String nombreNuevo){
        this.miTienda.cambiarNombreDeProductoDeInventario(id, codigo, nombreNuevo);
    }

    public void actualizarValorVentaPorPorcentajeDeProductoDeInventario(int id, int codigo, double porcentaje){
        this.miTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(id, codigo, porcentaje);
    }

    public void actualizarValorCompraDeProductoDeInventario(int id, int codigo, double valorNuevo){
        this.miTienda.actualizarValorCompraDeProductoDeInventario(id, codigo, valorNuevo);
    }

    public void aumentarStockDeProductoDeInventario(int id, int codigo, int cantidad){
        this.miTienda.aumentarStockDeProductoDeInventario(id, codigo, cantidad);
    }

    public void reducirStockDeProductoDeInventario(int id, int codigo, int cantidad){
        this.miTienda.reducirStockDeProductoDeInventario(id, codigo, cantidad);
    }

    public void eliminarProductoAInventario(int id, int codigo){
        this.miTienda.eliminarProductoAUnInv(id, codigo);
    }

    public boolean buscarProductoAInventario(int id, int codigo){
        return this.miTienda.buscarProductoAUnInv(id ,codigo);
    }

    public void moverProductoAInventario(int idSalida, int idLlegada, int codigo){
        this.miTienda.moverProductoAOtroInventario(idSalida, idLlegada, codigo);
    }

    public void eliminarInventarioVacio(int id){
        this.miTienda.eliminarInventarioVacio(id);
    }

    public void registrarServicioNuevo(String nombreServicio, double precioBase){
        Servicio servicio = new Servicio(this.miTienda.asignarCodigoServicio(), nombreServicio, precioBase);
        this.miTienda.registrarServicioAlCatalogo(servicio);
    }

    public void eliminarServicioDeTienda(int codigoServicio){
        this.miTienda.eliminarServicioDelCatalogo(codigoServicio);
    }

    public void cambiarNombreServicio(int codigoServicio, String nombreNuevo){
        this.miTienda.cambiarNombreServicio(codigoServicio, nombreNuevo);
    }

    public void cambiarPrecioServicio(int codigoServicio, double precioNuevo){
        this.miTienda.cambiarPrecioServicio(codigoServicio, precioNuevo);
    }

    private void obtenerServicio(int codigoServicio){
        this.miTienda.obtenerServicio(codigoServicio);
    }

    public void agregarServicioAlCarrito(int codigoServicio){
        obtenerServicio(codigoServicio);
        this.miCarrito.agregarServicio(codigoServicio);
    }

    //METODOS CONTROL CARRITO:

    public void abrirCarritoSesion(){
        this.miCarrito = new Carrito();
    }

    public void agregarItemASesion(int idInv, int codigoProd, int cantidad){
        ReferenciaItem referenciaItem = new ReferenciaItem(idInv, codigoProd);
        this.miCarrito.agregarItem(referenciaItem, cantidad);
    }

    //METODOS CONTROL DE GESTOR DE VENTAS:

    public boolean registroVentasEstaVacio(){
        return this.miGestorDeVentas.registroVentasEstaVacio();
    }

    public int confirmarYProcesarVentaActual(){
        LocalDate fecha = LocalDate.now();
        Factura facturaProcesada = this.miGestorDeVentas.procesarVentaMultiproducto(this.miCarrito, fecha);
        abrirCarritoSesion();
        return facturaProcesada.getIdFactura();
    }

}

