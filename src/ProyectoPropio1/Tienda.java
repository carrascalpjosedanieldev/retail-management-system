package ProyectoPropio1;

import java.util.LinkedHashMap;
import java.util.Map;

public class Tienda {

    //ATRIBUTOS:

    private String nombreTienda;

    private final Map<Integer,Inventario> misInventarios;

    private final Map<Integer, Servicio> serviciosOfrecidos;

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

    public Inventario obtenerInventario(int id) throws IllegalArgumentException{
        Inventario inventario = this.misInventarios.get(id);
        if (inventario==null){
            throw new IllegalArgumentException("Ese Inventario No existe");
        }
        return inventario;
    }

    private void validarQueExistanInventarios() throws IllegalArgumentException{
        if (tiendaNoTieneInventarios()){
            throw new IllegalArgumentException("No Hay Inventarios");
        }
    }

    //METODOS PARA MOSTRAR INFORMACION:

    public String mostrarInfoInventarios() throws IllegalArgumentException{
        validarQueExistanInventarios();
        StringBuilder informacion = new StringBuilder();
        informacion.append("---> INVENTARIOS:");
        informacion.append(System.lineSeparator());
        informacion.append("------------------------------------------------------------------------------------------------------------");
        informacion.append(System.lineSeparator());
        for (Inventario inventario :this.misInventarios.values()){
            informacion.append(inventario.informacionMinima());
        }
        informacion.append(System.lineSeparator());
        informacion.append("-------------------------------------------------------------------------------------------------------------");
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

    public String mostrarInventarioGeneral() throws IllegalArgumentException{
        validarQueExistanInventarios();
        StringBuilder inventarioGeneral = new StringBuilder();
        for (Inventario inventario: this.misInventarios.values()){
            inventarioGeneral.append(inventario.obtenerDetalle());
        }
        return inventarioGeneral.toString();
    }

    //METODOS PARA MODIFICAR TIENDA:

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

    public void eliminarInventarioVacio(int id) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        if (inventario.tieneProductos()){
            throw new IllegalArgumentException("El Inventario No Esta Vacio");
        }
        this.misInventarios.remove(id);
    }

    //METODOS PARA MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int id , String nombreNuevoInv) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        inventario.cambiarNombreInventario(nombreNuevoInv);
    }

    public Producto agregarProductoAUnInv(int id, Producto producto) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        return inventario.agregarUnProducto(producto);
    }

    public void eliminarProductoAUnInv(int id, int codigo) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        inventario.eliminarUnProducto(codigo);
    }

    public boolean buscarProductoAUnInv(int id, int codigo) throws IllegalArgumentException{
        Inventario inventario = obtenerInventario(id);
        return inventario.buscarProducto(codigo);
    }

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
        inventarioLlegada.agregarUnProducto(producto);
        inventarioSalida.eliminarUnProducto(codigo);
    }

    //METODOS SERVICIOS:

    public boolean tiendaNoTieneServicios() throws IllegalArgumentException{
        return this.serviciosOfrecidos.isEmpty();
    }

    public Servicio obtenerServicio(int codigo) throws IllegalArgumentException{
        Servicio servicio = this.serviciosOfrecidos.get(codigo);
        if (servicio==null){
            throw new IllegalArgumentException("Ese Servicio No existe");
        }
        return servicio;
    }

    public void registrarServicioAlCatalogo(Servicio servicio) throws IllegalArgumentException{
        this.serviciosOfrecidos.put(servicio.getCodigoServicio(), servicio);
    }

    public void eliminarServicioDelCatalogo(int codigoServicio) throws IllegalArgumentException{
        Servicio servicio = obtenerServicio(codigoServicio);
        this.serviciosOfrecidos.remove(servicio.getCodigoServicio());
    }

    public void cambiarNombreServicio(int codigoServicio, String nombreServicio) throws IllegalArgumentException{
        Servicio servicio = obtenerServicio(codigoServicio);
        servicio.cambiarNombreServicio(nombreServicio);
    }

    public void cambiarPrecioServicio(int codigoServicio, double precioNuevo) throws IllegalArgumentException{
        Servicio servicio = obtenerServicio(codigoServicio);
        servicio.cambiarPrecioBase(precioNuevo);
    }

    public String mostrarServiciosDeLaTienda() throws IllegalArgumentException{
        if (tiendaNoTieneServicios()){
            throw new IllegalArgumentException("No hay Servicios disponibles");
        }
        StringBuilder infoServicios = new StringBuilder();
        infoServicios.append("---------------------------------------------------------------");
        infoServicios.append(System.lineSeparator());
        infoServicios.append("SERVICIOS:");
        infoServicios.append(System.lineSeparator());
        for (Servicio servicio:this.serviciosOfrecidos.values()){
            infoServicios.append(servicio.obtenerInfoServicio());
            infoServicios.append(System.lineSeparator());
        }
        infoServicios.append("---------------------------------------------------------------");
        return infoServicios.toString();
    }

}

