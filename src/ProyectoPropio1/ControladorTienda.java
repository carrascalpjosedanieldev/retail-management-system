package ProyectoPropio1;

import java.time.LocalDate;

public class ControladorTienda {

    //ATRIBUTOS:

    private Tienda miTienda;

    private GestorVentas miGestorDeVentas;

    private int contadorDeTiendas;

    private Carrito miCarrito;

    //CONSTRUCTOR:

    public ControladorTienda() {
        this.miTienda = null;
        this.miGestorDeVentas = null;
        this.miCarrito = null;
        this.contadorDeTiendas = 0;
    }

    public boolean puedeCrearTienda() {
        return this.contadorDeTiendas == 0;
    }

    public boolean noTieneTienda(){
        return this.contadorDeTiendas != 1;
    }

    public boolean tiendaNoTieneInventarios(){
        return this.miTienda.tiendaNoTieneInventarios();
    }

    public boolean inventarioTieneProductos(int idInventario){
        return this.miTienda.inventarioTieneProductos(idInventario);
    }

    public boolean tiendaNoTieneServicios(){
        return this.miTienda.tiendaNoTieneServicios();
    }

    //------------> METODOS DE CONTROL DE TIENDA:

    public String obtenerNombreTienda() {
        return this.miTienda.getNombreTienda();
    }

    public void crearTienda(String nombreTienda) throws IllegalArgumentException{
        this.miTienda = new Tienda(nombreTienda);
        this.miGestorDeVentas = new GestorVentas(this.miTienda);
        this.contadorDeTiendas = 1;
    }

    public void cambiarNombreTienda(String nombreNuevo) throws IllegalArgumentException{
        this.miTienda.cambiarNombreTienda(nombreNuevo);
    }

    public void agregarInventarioATienda(String nombre, int capacidad) throws IllegalArgumentException{
        this.miTienda.agregarInventario(nombre,capacidad);
    }

    public String mostrarInfoInventariosDeTienda() throws IllegalArgumentException{
        return this.miTienda.mostrarInfoInventarios();
    }

    public boolean noExisteProductoEnInventario(int idInventario, int codigoProducto) {
        return !this.miTienda.obtenerInventario(idInventario).buscarProducto(codigoProducto);
    }

    public String obtenerDetalleInventarioDeTienda(int id) throws IllegalArgumentException{
        return this.miTienda.obtenerDetalleInventario(id);
    }

    public String mostraInfoInventarioDeTienda() throws IllegalArgumentException{
        return this.miTienda.mostrarInfoInventarios();
    }

    public void cambiarNombreAUnInventario(int id, String nombreNuevo) throws IllegalArgumentException{
        this.miTienda.cambiarNombreAUnInventario(id, nombreNuevo);
    }

    public Producto registrarProductoRopa(int id, String nombre, double valorCompra, int stock, String tallaString) throws IllegalArgumentException{
        Talla talla;
        try {
            talla = Talla.valueOf(tallaString);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("La talla ingresada no esta entre las opciones (Usa S, M, L o XL).");
        }
        Producto producto = new ProductoRopa(nombre, valorCompra, stock, talla);
        return this.miTienda.agregarProductoAUnInv(id, producto);
    }

    public Producto registrarProductoPerecedero(int id, String nombre, double valorCompra, int stock, LocalDate fechaVencimiento) throws IllegalArgumentException{
        Producto producto = new ProductoPerecedero(nombre, valorCompra, stock, fechaVencimiento);
        return this.miTienda.agregarProductoAUnInv(id, producto);
    }

    public void cambiarNombreDeProductoDeInventario(int id, int codigo, String nombreNuevo) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).actualizarNombreProducto(codigo, nombreNuevo);
    }

    public void actualizarValorVentaPorPorcentajeDeProductoDeInventario(int id, int codigo, double porcentaje) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).actualizarValorVentaPorPorcentaje(codigo, porcentaje);
    }

    public void actualizarValorCompraDeProductoDeInventario(int id, int codigo, double valorNuevo) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).actualizarValorCompra(codigo, valorNuevo);
    }

    public void aumentarStockDeProductoDeInventario(int id, int codigo, int cantidad) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).agregarStockProducto(codigo, cantidad);
    }

    public void reducirStockDeProductoDeInventario(int id, int codigo, int cantidad) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).reducirStockProducto(codigo, cantidad);
    }

    public void eliminarProductoAInventario(int id, int codigo) throws IllegalArgumentException{
        this.miTienda.eliminarProductoAUnInv(id, codigo);
    }

    public boolean buscarProductoAInventario(int id, int codigo) throws IllegalArgumentException{
        return this.miTienda.buscarProductoAUnInv(id ,codigo);
    }

    public void moverProductoAInventario(int idSalida, int idLlegada, int codigo) throws IllegalArgumentException{
        this.miTienda.moverProductoAOtroInventario(idSalida, idLlegada, codigo);
    }

    public String mostrarInfoStockInventario(int id) throws IllegalArgumentException{
        return this.miTienda.mostrarInfoStockInventario(id);
    }

    public void eliminarInventarioVacio(int id) throws IllegalArgumentException{
        this.miTienda.eliminarInventarioVacio(id);
    }

    public String mostrarInventarioGeneralDeTienda() throws IllegalArgumentException{
        return this.miTienda.mostrarInventarioGeneral();
    }

    public void registrarServicioNuevo(String nombreServicio, double precioBase) throws IllegalArgumentException{
        Servicio servicio = new Servicio(nombreServicio, precioBase);
        this.miTienda.registrarServicioAlCatalogo(servicio);
    }

    public void eliminarServicioDeTienda(int codigoServicio) throws IllegalArgumentException{
        this.miTienda.eliminarServicioDelCatalogo(codigoServicio);
    }

    public void cambiarNombreServicio(int codigoServicio, String nombreNuevo) throws IllegalArgumentException{
        this.miTienda.cambiarNombreServicio(codigoServicio, nombreNuevo);
    }

    public void cambiarPrecioServicio(int codigoServicio, double precioNuevo) throws IllegalArgumentException{
        this.miTienda.cambiarPrecioServicio(codigoServicio, precioNuevo);
    }

    public String mostrarServiciosDeLaTienda() throws IllegalArgumentException{
        return this.miTienda.mostrarServiciosDeLaTienda();
    }

    public Servicio obtenerServicio(int codigoServicio) throws IllegalArgumentException{
        return this.miTienda.obtenerServicio(codigoServicio);
    }

    public void agregarServicioAlCarrito(Servicio servicio) throws IllegalArgumentException{
        this.miCarrito.agregarServicio(servicio);
    }



    //METODOS CONTROL CARRITO:

    public void abrirCarritoSesion(){
        this.miCarrito = new Carrito();
    }

    public void agregarItemASesion(int idInv, int codigoProd, int cantidad) throws IllegalArgumentException{
        SolicitudItem solicitudItem = new SolicitudItem(idInv, codigoProd, cantidad);
        this.miCarrito.agregarItem(solicitudItem);
    }

    public String mostrarDatosServiciosDelCarrito() throws IllegalArgumentException{
        return this.miCarrito.mostrarDatosServiciosDeCarrito();
    }

    public String obtenerVistaPreviaDelCarrito() throws IllegalArgumentException{
        return this.miCarrito.mostrarCarrito();
    }



    //METODOS CONTROL DE GESTOR DE VENTAS:

    public String obtenerTotalRecaudoVentas() throws IllegalArgumentException{
        double totalFacturas = this.miGestorDeVentas.totalFacturas();
        return " $" + totalFacturas ;
    }

    public Factura confirmarYProcesarVentaActual() throws IllegalArgumentException{
        Factura factura = this.miGestorDeVentas.procesarVentaMultiproducto(this.miCarrito);
        this.miCarrito=null;
        return factura;
    }

    public String obtenerHistoralGestor() throws IllegalArgumentException{
        return this.miGestorDeVentas.obtenerHistorial();
    }

}

