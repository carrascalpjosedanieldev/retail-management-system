package ProyectoUniversidad1;

import java.util.ArrayList;

public class Tienda { //213 LINEAS NETAS DE 263 LINEAS TOTALES

    //ATRIBUTOS:

    private String nombreTienda;

    private final ArrayList<Inventario> misInventarios;

    //GETTERS Y SETTERS:

    public String getNombreTienda() {
        return nombreTienda;
    }

    private void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    //CONSTRUCTOR:

    public Tienda(String nombreTienda) {
        if (nombreTienda.isBlank()){
            throw new IllegalArgumentException("Asignacion de Nombre de la Tienda Invalido");
        }
        this.nombreTienda = nombreTienda;
        this.misInventarios=new ArrayList<>();
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
        for ( Inventario miInventario : this.misInventarios){
            miInventario.informacionMinimaInventario();
        }
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

    public void agregarInventario(String nombre,int CAPACIDAD_MAXIMA){
        try {
            Inventario inventario = new Inventario(nombre,CAPACIDAD_MAXIMA);
            System.out.println("\nNUEVO INVENTARIO GENERADO CON EXITO:\n" +
                    "Ahora la Tienda: -" + getNombreTienda() + "- Tiene el Inventario: -" + nombre + "- Con una Capacidad de: " + CAPACIDAD_MAXIMA + " Unidades");
            this.misInventarios.add(inventario);
        } catch (IllegalArgumentException asignacionInvalida) {
            System.out.println("\nNO se puede generar este Inventario por un error de asignacion de datos:");
            System.out.println("ERROR: " + asignacionInvalida.getMessage());
        }
    }

    public void eliminarInventarioVacio(int indice){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("""
                    \nACCION DENEGADA
                    INVENTARIO INEXISTENTE
                    """);
        } else {
            if (!this.misInventarios.get(indice).tieneProductos()){
                this.misInventarios.remove(indice);
            } else {
                System.out.println("""
                    \nACCION DENEGADA
                    EL INVENTARIO NO ESTA VACIO
                    """);
            }
        }

    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public void moverProductoAOtroInventario(int indiceSalida,int indiceLlegada,int codigo){
        if (indiceSalida<0 || indiceSalida>=this.misInventarios.size() || indiceLlegada<0 || indiceLlegada>=this.misInventarios.size()){
            System.out.println("""
                    \nACCION DENEGADA
                    INVENTARIO INEXISTENTE
                    """);
        } else if (indiceSalida == indiceLlegada) {
            System.out.println("""
                    \nACCION DENEGADA:
                    EL INVENTARIO DE ORIGEN Y DESTINO SON EL MISMO
                    """);
        } else {
            Producto auxiliar = this.misInventarios.get(indiceSalida).asignarProductoParaCambiarInventario(codigo);
            this.misInventarios.get(indiceSalida).eliminarUnProducto(codigo);
            this.misInventarios.get(indiceLlegada).agregarProductoHecho(auxiliar);
        }
    }

    //METODOS UTILIZAR TIENDA:

    public void mostrarInventarioGeneral(){
        for (Inventario miInventario:this.misInventarios){
            miInventario.mostrarInventario();
        }
    }

    //METODOS VENDER PRODUCTO:

    public double venderProducto(int codigo,int cantidad){
        boolean productoEsta;
        double pagoProducto = 0;

        for (Inventario miInventario:this.misInventarios){
            productoEsta = miInventario.buscarProductoParaVender(codigo);
            if (productoEsta){
                pagoProducto = miInventario.venderProducto(codigo,cantidad);
                break;
            }
        }
        return pagoProducto;
    }

    //METODOS MODIFICAR INVENTARIO:

    public void cambiarNombreAUnInventario(int indice , String nombreNuevoInv){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).cambiarNombreInventario(nombreNuevoInv);
        }
    }

    public void agregarProductoAUnInv(int indice,String nombreProducto,double valorCompra,int stock){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).agregarUnProducto(nombreProducto,valorCompra,stock);
        }
    }

    public void agregarProductoAUnInv(int indice,String nombreProducto,double valorCompra){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).agregarUnProducto(nombreProducto,valorCompra);
        }
    }

    public void eliminarProductoAUnInv(int indice,int codigo){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).eliminarUnProducto(codigo);
        }
    }

    public void buscarProductoAUnInv(int indice,int codigo){
        if (this.misInventarios.isEmpty()){
            System.out.println("\nACCION DENEGADA\nNO HAY INVENTARIOS");
        }else if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).buscarProducto(codigo);
        }
    }

    //METODOS MODIFICAR PRODUCTO:

    public void actualizarNombreInventarioProducto(int indice, int codigo, String nombre){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).actualizarNombreProducto(codigo, nombre);
        }
    }

    public void actualizarValorVentaPorcentajeInventarioProducto(int indice, int codigo, double porcentaje){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).actualizarValorVentaPorPorcentaje(codigo, porcentaje);
        }
    }

    public void actualizarValorVentaPrecioInventarioProducto(int indice, int codigo, double precio){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).actualizarValorVentaPorPrecio(codigo, precio);
        }
    }

    public void actualizarValorCompraInventarioProducto(int indice, int codigo, double valorNuevo){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).actualizarValorCompra(codigo, valorNuevo);
        }
    }

    public void actualizarStockInventarioProducto(int indice, int codigo, int cantidad){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).actualizarStockProducto(codigo, cantidad);
        }
    }

    public void reducirStockInventarioProd(int indice, int codigo, int cantidad){
        if (indice<0 || indice>=this.misInventarios.size()){
            System.out.println("\nACCION DENEGADA\nESE INVENTARIO NO EXISTE");
        } else {
            this.misInventarios.get(indice).reducirStockProducto(codigo, cantidad);
        }
    }

}
