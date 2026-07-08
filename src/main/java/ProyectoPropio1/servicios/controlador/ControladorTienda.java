package ProyectoPropio1.servicios.controlador;

import ProyectoPropio1.dominio.*;
import ProyectoPropio1.dominio.enums.Talla;
import ProyectoPropio1.dto.*;
import ProyectoPropio1.servicios.aplicacion.*;
import ProyectoPropio1.servicios.ensambladores.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ControladorTienda {

    //ATRIBUTOS:

    private final Tienda miTienda;

    private final GestorVentas miGestorDeVentas;

    private final EnsambladorDTOProducto miEnsambladorDTOProducto;

    private final EnsambladorDTOInventario miEnsambladorDTOInventario;

    private final EnsambladorDTOFactura miEnsambladorDTOFactura;

    private final EnsambladorDTOCarrito miEnsambladorDTOCarrito;

    private final EnsambladorDTOServicio miEnsambladorDTOServicio;

    private final EnsambladorDTOImpuesto miEnsambladorDTOImpuesto;

    private final ServicioImpuestos servicioImpuestos;

    private final ServicioConfiguraciones servicioConfiguraciones;

    private final ServicioInventario servicioInventario;

    private final ServicioProductos servicioProductos;

    private final ServicioServicios servicioServicios;

    //CONSTRUCTOR:

    public ControladorTienda(
            Tienda tienda,
            EnsambladorDTOProducto ensambladorDTOProducto, EnsambladorDTOInventario ensambladorDTOInventario,
            EnsambladorDTOFactura ensambladorDTOFactura, EnsambladorDTOCarrito ensambladorDTOCarrito,
            EnsambladorDTOServicio ensambladorDTOServicio, EnsambladorDTOImpuesto ensambladorDTOImpuesto,
            GestorVentas gestorVentas, ServicioImpuestos servicioImpuestos,
            ServicioConfiguraciones servicioConfiguraciones, ServicioInventario servicioInventario,
            ServicioProductos servicioProductos, ServicioServicios servicioServicios
    ) {
        this.miTienda = tienda;

        this.miEnsambladorDTOProducto = ensambladorDTOProducto;
        this.miEnsambladorDTOInventario = ensambladorDTOInventario;
        this.miEnsambladorDTOFactura = ensambladorDTOFactura;
        this.miEnsambladorDTOCarrito = ensambladorDTOCarrito;
        this.miEnsambladorDTOServicio = ensambladorDTOServicio;
        this.miEnsambladorDTOImpuesto = ensambladorDTOImpuesto;

        this.miGestorDeVentas = gestorVentas;
        this.servicioImpuestos = servicioImpuestos;
        this.servicioConfiguraciones = servicioConfiguraciones;
        this.servicioInventario = servicioInventario;
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
    }

    //METODOS DE VALIDACION:

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

    // METODOS PARA EXPORTAR DATOS DTO:

    public DatosTotalesProductoDTO obtenerDatosTotalesProducto (int idInventario, String codigoProducto){
        LocalDate fecha = obtenerFecha();
        Producto producto = this.servicioProductos.obtenerProductoDeInventario(idInventario, codigoProducto);
        return this.miEnsambladorDTOProducto.ensamblarDatosTotalesProducto(producto, fecha);
    }

    public DatosVentaProductoDTO obtenerDatosVentaProducto (int idInventario, String codigoProducto){
        LocalDate fecha = obtenerFecha();
        Producto producto = this.servicioProductos.obtenerProductoDeInventario(idInventario, codigoProducto);
        return this.miEnsambladorDTOProducto.ensamblarDatosVentaProducto(producto, fecha);
    }

    public DatosInventarioDTO obtenerDatosInventario (int idInventario){
        Inventario inventario = this.servicioInventario.obtenerInventario(idInventario);
        return this.miEnsambladorDTOInventario.ensamblarDatosInventario(inventario);
    }

    public List<DatosInventarioDTO> obtenerDetalleInventarioGeneral(){
        List<Inventario> inventarios = this.servicioInventario.obtenerInventarios();
        return this.miEnsambladorDTOInventario.ensamblarDetalleInventarioGeneral(inventarios);
    }

    public DetalleInventarioDTO obtenerDetalleInventario(int idInventario){
        LocalDate fecha = obtenerFecha();
        Inventario inventario = this.servicioInventario.obtenerInventario(idInventario);
        List<Producto> productos = this.servicioProductos.obtenerProductosDeInventario(idInventario);
        return this.miEnsambladorDTOInventario.ensamblarDetalleInventario(inventario, productos, fecha);
    }

    public List<ImpuestoDTO> obtenerDetalleImpuestosActivos(){
        List<Impuesto> impuestos = this.servicioImpuestos.obtenerImpuestosActivos();
        if (impuestos.isEmpty()){
            throw new IllegalStateException("NO hay Impuestos Activos");
        }
        return this.miEnsambladorDTOImpuesto.ensamblarDetalleImpuestos(impuestos);
    }

    public List<ImpuestoDTO> obtenerDetalleImpuestosInactivos(){
        List<Impuesto> impuestos = this.servicioImpuestos.obtenerImpuestosInactivos();
        if (impuestos.isEmpty()){
            throw new IllegalStateException("NO hay Impuestos Inactivos");
        }
        return this.miEnsambladorDTOImpuesto.ensamblarDetalleImpuestos(impuestos);
    }

    public ServicioDTO obtenerDatosServicio(String codigoServicio){
        LocalDate fecha = obtenerFecha();
        Servicio servicio = this.servicioServicios.obtenerServicio(codigoServicio);
        return this.miEnsambladorDTOServicio.ensamblarServicio(servicio, fecha);
    }

    public DatosCatalogoServiciosDTO obtenerCatalogoServicios(){
        LocalDate fecha = obtenerFecha();
        List<Servicio> servicios = this.servicioServicios.obtenerServicios();
        if (servicios.isEmpty()){
            throw new IllegalStateException("NO hay Servicios");
        }
        return this.miEnsambladorDTOServicio.ensamblarDatosCatalogoServicios(servicios, fecha);
    }

    public VistaPreviaCarritoDTO obtenerVistaPreviaCarrito(){
        LocalDate fecha = obtenerFecha();
        Carrito carrito = this.miGestorDeVentas.getCarrito();
        return this.miEnsambladorDTOCarrito.ensamblarVistaPreviaCarritoDTO(carrito, fecha);
    }

    //METODOS IMPUESTOS:

    public int registrarImpuesto(String nombre, BigDecimal porcentaje){
        return this.servicioImpuestos.registrarImpuesto(nombre, porcentaje);
    }

    public void eliminarImpuesto(int idImpuesto){
        this.servicioImpuestos.eliminarImpuesto(idImpuesto);
    }

    //METODOS DE TIENDA:

    public String obtenerNombreTienda() {
        return this.miTienda.getNombreTienda();
    }

    public void cambiarNombreTienda(String nombreNuevo){
        this.servicioConfiguraciones.cambiarNombreTienda(nombreNuevo, this.miTienda);
    }

    //METODOS DE INVENTARIO:

    public int agregarInventarioATienda(String nombre, int capacidad){
        return this.servicioInventario.agregarInventario(nombre, capacidad);
    }

    public void cambiarNombreInventario(int idInventario, String nombreNuevo){
        this.servicioInventario.cambiarNombreInventario(idInventario, nombreNuevo);
    }

    public void eliminarInventarioVacio(int idInventario){
        this.servicioInventario.eliminarInventarioVacio(idInventario);
    }

    //METODOS PRODUCTOS:

    public String registrarProductoRopa(int idInventario, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, int idImpuesto, String tallaString){
        this.servicioInventario.verificarEspacioDisponible(idInventario, stock);
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        if (!impuesto.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Impuesto -" + impuesto.getNombre() + "- porque se encuentra Inactivo.");
        }
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La talla ingresada no esta entre las opciones (Usa S, M, L o XL).");
        }
        Producto producto = new ProductoRopa(nombre, valorCompra, porcentajeGanancia, stock, impuesto, talla);
        return this.servicioProductos.registrarProducto(idInventario, producto);
    }

    public String registrarProductoPerecedero(int idInventario, String nombre, BigDecimal valorCompra, BigDecimal porcentajeGanancia, int stock, int idImpuesto, LocalDate fechaVencimiento){
        this.servicioInventario.verificarEspacioDisponible(idInventario, stock);
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        if (!impuesto.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Impuesto -" + impuesto.getNombre() + "- porque se encuentra Inactivo.");
        }
        Producto producto = new ProductoPerecedero(nombre, valorCompra, porcentajeGanancia, stock, impuesto, fechaVencimiento);
        return this.servicioProductos.registrarProducto(idInventario, producto);
    }

    public void eliminarProductoDeInventario(String codigo, int idInventario){
        this.servicioProductos.eliminarProductoDeInventario(codigo, idInventario);
    }

    public void estaProductoEnInventario(int idInventario, String codigoProducto){
        this.servicioProductos.obtenerProductoDeInventario(idInventario, codigoProducto);
    }

    private Producto obtenerProductoDeInventario(int idInventario, String codigoProducto){
        return this.servicioProductos.obtenerProductoDeInventario(idInventario, codigoProducto);
    }

    public void actualizarNombreDeProductoDeInventario(int idInventario, String  codigoProducto, String nombreNuevo){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarNombreProducto(nombreNuevo);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void actualizarPorcentajeGananciaDeProductoDeInventario(int idInventario, String  codigoProducto, BigDecimal porcentaje){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarValorVentaPorPorcentaje(porcentaje);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void actualizarValorCompraDeProductoDeInventario(int idInventario, String codigoProducto, BigDecimal valorNuevo){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.cambiarValorCompra(valorNuevo);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void aumentarStockDeProductoDeInventario(int idInventario, String codigoProducto, int cantidad){
        this.servicioInventario.verificarEspacioDisponible(idInventario, cantidad);
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.aumentarStock(cantidad);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void reducirStockDeProductoDeInventario(int idInventario, String codigoProducto, int cantidad){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.reducirStock(cantidad);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void moverProductoAInventario(int idInventarioOrigen, int idInventarioDestino, String codigoProducto){
        if (idInventarioOrigen == idInventarioDestino) {
            throw new IllegalArgumentException("El inventario de destino debe ser diferente al de origen.");
        }
        Producto producto = this.servicioProductos.obtenerProductoDeInventario(idInventarioOrigen, codigoProducto);
        int stockAMover = producto.getStock();
        this.servicioInventario.verificarEspacioDisponible(idInventarioDestino, stockAMover);
        this.servicioProductos.moverProductoAInventario(idInventarioOrigen, idInventarioDestino, codigoProducto);
    }

    //SERVICIOS

    public String registrarServicioNuevo(String nombreServicio, BigDecimal precioBase, int idImpuesto){
        return this.servicioServicios.registrarServicioNuevo(nombreServicio, precioBase, idImpuesto);
    }

    public void eliminarServicioDeTienda(String codigoServicio){
        this.servicioServicios.eliminarServicio(codigoServicio);
    }

    public void cambiarNombreServicio(String codigoServicio, String nombreNuevo){
        this.servicioServicios.cambiarNombreServicio(codigoServicio, nombreNuevo);
    }

    public void cambiarPrecioServicio(String codigoServicio, BigDecimal precioNuevo){
        this.servicioServicios.cambiarPrecioServicio(codigoServicio, precioNuevo);
    }

    public void cambiarImpuestoDeServicio(String codigoServicio, int idImpuesto){
        this.servicioServicios.cambiarImpuestoDeServicio(codigoServicio, idImpuesto);
    }

    //METODOS CONTROL DE GESTOR DE VENTAS:

    public void abrirCarritoSesion(){
        this.miGestorDeVentas.abrirCarritoSesion();
    }

    public void agregarProductoACarritoSesion(int idIventario, String codigoProducto, int cantidad){
        this.miGestorDeVentas.agregarProductoAlCarrito(idIventario, codigoProducto, cantidad);
    }

    public void reducirCantidadProductoACarritoSesion(String codigoProducto, int cantidadAReducir){
        this.miGestorDeVentas.reducirCantidadProducto(codigoProducto, cantidadAReducir);
    }

    public void eliminarProductoACarritoSesion(String codigoProducto){
        this.miGestorDeVentas.eliminarProductoAlCarrito(codigoProducto);
    }

    public void agregarServicioACarritoSesion(String codigoServicio){
        this.miGestorDeVentas.agregarServicioAlCarrito(codigoServicio);
    }

    public void reducirCantidadServicioACarritoSesion(String codigoServicio, int cantidadAReducir){
        this.miGestorDeVentas.reducirCantidadServicio(codigoServicio, cantidadAReducir);
    }

    public void eliminarServicioACarritoSesion(String codigoServicio){
        this.miGestorDeVentas.eliminarServicioAlCarrito(codigoServicio);
    }

    public FacturaDTO confirmarProcesarVentaActualYGenerarFactura(){
        LocalDate fecha = obtenerFecha();
        Factura factura = this.miGestorDeVentas.procesarVentaYObtenerFactura(fecha);
        return this.miEnsambladorDTOFactura.ensamblarFactura(factura);
    }

    public void cancelarCompraTotal(){
        this.miGestorDeVentas.cancelarCompraTotal();
    }

}

