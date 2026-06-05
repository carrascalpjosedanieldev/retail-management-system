package ProyectoUniversidad1;

public class ControladorTienda {

    private Tienda miTienda;

    private GestorVentas miGestorDeVentas;

    private int contadorDeTiendas;

    public Tienda getMiTienda() {
        return miTienda;
    }

    public GestorVentas getMiGestorDeVentas() {
        return miGestorDeVentas;
    }

    public int getContadorDeTiendas() {
        return contadorDeTiendas;
    }

    public ControladorTienda() {
        this.miTienda = null;
        this.miGestorDeVentas = null;
        this.contadorDeTiendas = 0;
    }

    public boolean puedeCrearTienda() {
        return this.contadorDeTiendas == 0;
    }

    public boolean tieneTienda(){
        return this.contadorDeTiendas == 1;
    }

    public boolean tiendaTieneInventarios(){
        return this.miTienda.tiendaTieneInventarios();
    }

    public boolean inventarioTieneProductos(int idInventario){
        return this.miTienda.inventarioTieneProductos(idInventario);
    }

    public boolean existeProductoEnInventario(int idInventario, int codigoProducto) {
        return this.miTienda.obtenerInventario(idInventario).buscarProducto(codigoProducto);
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

    public String obtenerDetalleInventarioDeTienda(int id) throws IllegalArgumentException{
        return this.miTienda.obtenerDetalleInventario(id);
    }

    public String mostraInfoInventarioDeTienda() throws IllegalArgumentException{
        return this.miTienda.mostrarInfoInventarios();
    }

    public void cambiarNombreAUnInventario(int id, String nombreNuevo) throws IllegalArgumentException{
        this.miTienda.cambiarNombreAUnInventario(id, nombreNuevo);
    }

    public Producto agregarPorductoAInventario(int id, String nombre, double valorCompra, int stock) throws IllegalArgumentException{
        return this.miTienda.agregarProductoAUnInv(id, nombre, valorCompra, stock);
    }

    public void cambiarNombreDeProductoDeInventario(int id, int codigo, String nombreNuevo) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).actualizarNombreProducto(codigo, nombreNuevo);
    }

    public void actualizarValorVentaPorPrecioDeProductoDeInventario(int id, int codigo, double valorNuevo) throws IllegalArgumentException{
        this.miTienda.obtenerInventario(id).actualizarValorVentaPorPrecio(codigo, valorNuevo);
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

}
