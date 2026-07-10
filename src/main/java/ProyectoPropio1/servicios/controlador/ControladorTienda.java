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

    private final EnsambladorDTODescuento miEnsambladorDTODescuento;

    private final ServicioFacturas servicioFacturas;

    private final ServicioImpuestos servicioImpuestos;

    private final ServicioConfiguraciones servicioConfiguraciones;

    private final ServicioInventario servicioInventario;

    private final ServicioProductos servicioProductos;

    private final ServicioServicios servicioServicios;

    private final ServicioDescuentos servicioDescuentos;

    //CONSTRUCTOR:

    public ControladorTienda(
            Tienda tienda,
            EnsambladorDTOProducto ensambladorDTOProducto, EnsambladorDTOInventario ensambladorDTOInventario,
            EnsambladorDTOFactura ensambladorDTOFactura, EnsambladorDTOCarrito ensambladorDTOCarrito,
            EnsambladorDTOServicio ensambladorDTOServicio, EnsambladorDTOImpuesto ensambladorDTOImpuesto,
            EnsambladorDTODescuento ensambladorDTODescuento,
            GestorVentas gestorVentas,
            ServicioFacturas servicioFacturas, ServicioImpuestos servicioImpuestos,
            ServicioConfiguraciones servicioConfiguraciones, ServicioInventario servicioInventario,
            ServicioProductos servicioProductos, ServicioServicios servicioServicios, ServicioDescuentos servicioDescuentos
    ) {
        this.miTienda = tienda;

        this.miEnsambladorDTOProducto = ensambladorDTOProducto;
        this.miEnsambladorDTOInventario = ensambladorDTOInventario;
        this.miEnsambladorDTOFactura = ensambladorDTOFactura;
        this.miEnsambladorDTOCarrito = ensambladorDTOCarrito;
        this.miEnsambladorDTOServicio = ensambladorDTOServicio;
        this.miEnsambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.miEnsambladorDTODescuento = ensambladorDTODescuento;

        this.miGestorDeVentas = gestorVentas;

        this.servicioFacturas = servicioFacturas;
        this.servicioImpuestos = servicioImpuestos;
        this.servicioConfiguraciones = servicioConfiguraciones;
        this.servicioInventario = servicioInventario;
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
        this.servicioDescuentos = servicioDescuentos;
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

    public List<DescuentoDTO> obtenerDetalleDescuentosActivos(){
        List<Descuento> descuentos = this.servicioDescuentos.obtenerDescuentosActivos();
        if (descuentos.isEmpty()){
            throw new IllegalStateException("NO hay Descuentos Activos");
        }
        return this.miEnsambladorDTODescuento.ensamblarDetalleDescuentos(descuentos);
    }

    public List<DescuentoDTO> obtenerDetalleDescuentosInactivos(){
        List<Descuento> descuentos = this.servicioDescuentos.obtenerDescuentosInactivos();
        if (descuentos.isEmpty()){
            throw new IllegalStateException("NO hay Descuentos Inactivos");
        }
        return this.miEnsambladorDTODescuento.ensamblarDetalleDescuentos(descuentos);
    }

    public ServicioDTO obtenerDatosServicio(String codigoServicio){
        LocalDate fecha = obtenerFecha();
        Servicio servicio = this.servicioServicios.obtenerServicio(codigoServicio);
        return this.miEnsambladorDTOServicio.ensamblarServicio(servicio, fecha);
    }

    public CatalogoServiciosDTO obtenerCatalogoServicios(){
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

    public ReporteRecaudoDTO obtenerReporteRecaudo(LocalDate fechaInicio, LocalDate fechaFin){
        ReporteRecaudo reporteRecaudo = this.servicioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin);
        return this.miEnsambladorDTOFactura.ensamblarReporteRecaudo(reporteRecaudo);
    }

    //METODOS IMPUESTOS:

    public int registrarImpuesto(String nombre, BigDecimal porcentaje){
        return this.servicioImpuestos.registrarImpuesto(nombre, porcentaje);
    }

    public void desactivarImpuesto(int idImpuesto){
        this.servicioImpuestos.desactivarImpuesto(idImpuesto);
    }

    public void activarImpuesto(int idImpuesto){
        this.servicioImpuestos.activarImpuesto(idImpuesto);
    }

    public void cambiarNombreImpuesto(int idImpuesto, String nombreNuevo){
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        impuesto.cambiarNombre(nombreNuevo);
        this.servicioImpuestos.actualizarImpuesto(impuesto);
    }

    public void cambiarPorcentajeImpuesto(int idImpuesto, BigDecimal porcentajeNuevo){
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        impuesto.cambiarPorcentaje(porcentajeNuevo);
        this.servicioImpuestos.actualizarImpuesto(impuesto);
    }

    //METODOS DESCUENTOS:

    public int registrarDescuento(String nombre, BigDecimal porcentaje){
        return this.servicioDescuentos.registrarDescuento(nombre, porcentaje);
    }

    public void desactivarDescuento(int idDescuento){
        this.servicioDescuentos.desactivarDescuento(idDescuento);
    }

    public void activarDescuento(int idDescuento){
        this.servicioDescuentos.activarDescuento(idDescuento);
    }

    public void cambiarNombreDescuento(int idDescuento, String  nombreNuevo){
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        descuento.cambiarNombre(nombreNuevo);
        this.servicioDescuentos.actualizarDescuento(descuento);
    }

    public void cambiarPorcentajeDescuento(int idDescuento, BigDecimal porcentajeNuevo){
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        descuento.cambiarPorcentaje(porcentajeNuevo);
        this.servicioDescuentos.actualizarDescuento(descuento);
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

    public String registrarProductoRopa(int idInventario, String nombre, BigDecimal valorCompra,
                                        BigDecimal porcentajeGanancia, int stock, int idImpuesto, int idDescuento,
                                        String tallaString){
        this.servicioInventario.verificarEspacioDisponible(idInventario, stock);
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        if (!impuesto.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Impuesto -" + impuesto.getNombre() +
                    "- porque se encuentra Inactivo.");
        }
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        if (!descuento.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Descuento -" + descuento.getNombre() +
                    "- porque se encuentra Inactivo.");
        }
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La talla ingresada no esta entre las opciones (Usa S, M, L o XL).");
        }
        Producto producto = ProductoRopa.crearNuevo(nombre, valorCompra, porcentajeGanancia, stock, impuesto,
                descuento, talla);
        return this.servicioProductos.registrarProducto(idInventario, producto);
    }

    public String registrarProductoPerecedero(int idInventario, String nombre, BigDecimal valorCompra,
                                              BigDecimal porcentajeGanancia, int stock, int idImpuesto,
                                              int idDescuento, LocalDate fechaVencimiento){
        this.servicioInventario.verificarEspacioDisponible(idInventario, stock);
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        if (!impuesto.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Impuesto -" + impuesto.getNombre() + "- porque se encuentra Inactivo.");
        }
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        if (!descuento.isActivo()) {
            throw new IllegalArgumentException("No se puede asignar el Descuento -" + descuento.getNombre() +
                    "- porque se encuentra Inactivo.");
        }
        LocalDate fecha = obtenerFecha();
        if (fechaVencimiento.isBefore(fecha)){
            throw new IllegalArgumentException("NO se puede Registrar el Producto porque ya esta Vencido");
        }
        Producto producto = ProductoPerecedero.crearNuevo(nombre, valorCompra, porcentajeGanancia, stock, impuesto,
                descuento, fechaVencimiento);
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

    public void cambiarImpuestoAProducto(String codigoProducto, int idInventario, int idImpuesto){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        Impuesto impuesto = this.servicioImpuestos.obtenerImpuesto(idImpuesto);
        producto.cambiarImpuesto(impuesto);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void cambiarDescuentoAProducto(String codigoProducto, int idInventario, int idDescuento){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        Descuento descuento = this.servicioDescuentos.obtenerDescuento(idDescuento);
        producto.cambiarDescuento(descuento);
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void activarProducto(String codigoProducto, int idInventario){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.activarProducto();
        this.servicioProductos.actualizarProductoDeInventario(idInventario, producto);
    }

    public void desactivarProducto(String codigoProducto, int idInventario){
        Producto producto = this.obtenerProductoDeInventario(idInventario, codigoProducto);
        producto.desactivarProducto();
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

    public String registrarServicioNuevo(String nombreServicio, BigDecimal precioBase, int idImpuesto, int idDescuento){
        return this.servicioServicios.registrarServicioNuevo(nombreServicio, precioBase, idImpuesto, idDescuento);
    }

    public void desactivarServicioDeTienda(String codigoServicio){
        this.servicioServicios.desactivarServicio(codigoServicio);
    }

    public void activarServicioDeTienda(String codigoServicio){
        this.servicioServicios.activarServicio(codigoServicio);
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

    public void cambiarDescuentoDeServicio(String codigoServicio, int idImpuesto){
        this.servicioServicios.cambiarDescuentoDeServicio(codigoServicio, idImpuesto);
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

