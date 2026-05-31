package ProyectoUniversidad1;

import java.util.HashMap;
import java.util.Map;

public class Tienda { //x LINEAS NETAS DE 263 LINEAS TOTALES

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
        if (nombreTienda.isBlank()){
            throw new IllegalArgumentException("Asignacion de Nombre de la Tienda Invalido");
        }
        this.nombreTienda = nombreTienda;
        this.misInventarios = new HashMap<>();
    }

    //METODOS:

    public boolean tieneInventarios(){
        return !this.misInventarios.isEmpty();
    }

    public boolean inventarioTieneProductos(int indice){
        return this.misInventarios.get(indice).tieneProductos();
    }

    public void mostrarInventarios(){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
            return;
        }
        this.misInventarios.values().forEach(Inventario::informacionMinimaInventario);
    }

    public void mostrarUnInventario(int indice){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        } else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).mostrarInventario();
        }
    }

    public void mostrarStockUnInventario(int indice){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        } else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).mostrarStockInventario();
        }
    }

    //METODOS MODIFICAR TIENDA:

    public void cambiarNombreTienda(String nuevoNombre){
        if (nuevoNombre.isBlank()){
            System.out.println("\nACCION RECHAZADA:\nNombre para el Inventario invalido");
        } else {
            System.out.print("\nCAMBIO EXITOSO\nLa Tienda:  -" + getNombreTienda() + "- ");
            setNombreTienda(nuevoNombre);
            System.out.println(" Ahora tendra el nombre:  -" + getNombreTienda() + "-");
        }
    }

    public void agregarInventario(String nombre,int capacidadMaxima) {
        try {
            Inventario inventario = new Inventario(nombre, capacidadMaxima);
            System.out.println("\nNUEVO INVENTARIO GENERADO CON EXITO:\n" +
                    "Ahora la Tienda: -" + getNombreTienda() + "- Tiene el Inventario: -" + nombre + "- Con una Capacidad de: " +
                    capacidadMaxima + " Unidades");
            this.misInventarios.put(inventario.getNumeroId(),inventario);
        } catch (IllegalArgumentException asignacionInvalida) {
            System.out.println("\nNO se puede generar este Inventario por un error de asignacion de datos:");
            System.out.println("ERROR: " + asignacionInvalida.getMessage());
        }
    }

    public void eliminarInventarioVacio(int id){
        boolean inventarioEsta;
        inventarioEsta = this.misInventarios.containsKey(id);
        if (!inventarioEsta){
            System.out.println("""
                    \nACCION DENEGADA
                    INVENTARIO INEXISTENTE
                    """);
        } else {
            if (!this.misInventarios.get(id).tieneProductos()){
                this.misInventarios.remove(id);
            } else {
                System.out.println("""
                    \nACCION DENEGADA
                    EL INVENTARIO NO ESTA VACIO
                    """);
            }
        }
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public void moverProductoAOtroInventario(int idSalida, int idLlegada, int codigo){
        if (!this.misInventarios.containsKey(idSalida) || !this.misInventarios.containsKey(idLlegada)){
            System.out.println("""
                    \nACCION DENEGADA
                    INVENTARIO INEXISTENTE
                    """);
        } else if (idSalida == idLlegada) {
            System.out.println("""
                    \nACCION DENEGADA:
                    EL INVENTARIO DE ORIGEN Y DESTINO SON EL MISMO
                    """);
        } else {
            Producto auxiliar = this.misInventarios.get(idSalida).asignarProductoParaCambiarInventario(codigo);
            boolean eliminado = this.misInventarios.get(idSalida).eliminarProductoYSaber(codigo);
            if (eliminado) {
                this.misInventarios.get(idLlegada).agregarProductoHecho(auxiliar);
            } else {
                System.out.println("""
                        \nACCION DENEGADA
                        ESE PRODUCTO NO SE ENCUENTRA
                        """);
            }
        }
    }

    //METODOS UTILIZAR TIENDA:

    public void mostrarInventarioGeneral(){
        this.misInventarios.values().forEach(Inventario::mostrarInventario);
    }

    //METODOS VENDER PRODUCTO:

    public double venderProducto(int id, int codigo,int cantidad){
        boolean productoEsta;
        double pagoProducto = 0;
        productoEsta = this.misInventarios.get(id).buscarProductoParaVender(codigo);
        if (productoEsta){
            pagoProducto = this.misInventarios.get(id).venderProducto(codigo,cantidad);
        }
        return pagoProducto;
    }

    //METODOS MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int id , String nombreNuevoInv){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).cambiarNombreInventario(nombreNuevoInv);
        }
    }

    public void agregarProductoAUnInv(int id,String nombreProducto,double valorCompra,int stock){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).agregarUnProducto(nombreProducto,valorCompra,stock);
        }
    }

    public void agregarProductoAUnInv(int id, String nombreProducto, double valorCompra){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).agregarUnProducto(nombreProducto,valorCompra);
        }
    }

    public void eliminarProductoAUnInv(int id, int codigo){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).eliminarUnProducto(codigo);
        }
    }

    public void buscarProductoAUnInv(int id, int codigo){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).buscarProducto(codigo);
        }
    }

    //METODOS MODIFICAR PRODUCTO:

    public void actualizarNombreInventarioProducto(int id, int codigo, String nombre){
        if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).actualizarNombreProducto(codigo, nombre);
        }
    }

    public void actualizarValorVentaPorcentajeInventarioProducto(int id, int codigo, double porcentaje){
        if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).actualizarValorVentaPorPorcentaje(codigo, porcentaje);
        }
    }

    public void actualizarValorVentaPrecioInventarioProducto(int id, int codigo, double precio){
        if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).actualizarValorVentaPorPrecio(codigo, precio);
        }
    }

    public void actualizarValorCompraInventarioProducto(int id, int codigo, double valorNuevo){
        if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).actualizarValorCompra(codigo, valorNuevo);
        }
    }

    public void actualizarStockInventarioProducto(int id, int codigo, int cantidad){
        if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).agregarStockProducto(codigo, cantidad);
        }
    }

    public void reducirStockInventarioProd(int id, int codigo, int cantidad){
        if (!this.misInventarios.containsKey(id)){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(id).reducirStockProducto(codigo, cantidad);
        }
    }

}
