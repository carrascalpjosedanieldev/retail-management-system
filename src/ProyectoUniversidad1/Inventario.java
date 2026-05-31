package ProyectoUniversidad1;

import java.util.HashMap;
import java.util.Map;

public class Inventario { //x LINEAS NETAS DE 304 LINEAS TOTALES

    //ATRIBUTOS:

    private String nombre;

    private final int numeroId;

    private static int numeroIdSiguiente = 1;

    private final int capacidadMaxima;

    private int capacidadOcupada;

    private final Map<Integer,Producto> misProductos;

    //GETTERS Y SETTERS:

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumeroId() {
        return numeroId;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public int getCapacidadOcupada() {
        return capacidadOcupada;
    }

    //CONSTRUCTORES:

    public Inventario(String nombre, int capacidadMaxima) throws IllegalArgumentException{
        if (nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Inventario Invalido");
        }
        if (capacidadMaxima<=0){
            throw new IllegalArgumentException("Capacidad Maxima del Inventario Invalida");
        }
        this.nombre = nombre;
        this.numeroId = numeroIdSiguiente;
        this.capacidadMaxima = capacidadMaxima;
        this.misProductos = new HashMap<>();
        this.capacidadOcupada=0;
        numeroIdSiguiente++;
    }

    //METODOS:

    public void mostrarInventario(){
        if (this.misProductos.isEmpty()){
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------\n" +
                    "---> INVENTARIO:  -" + getNombre() + "-\n---> PRODUCTOS: ESTE INVENTARIO ESTA VACIO");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        } else {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------\n" +
                    "---> INVENTARIO:  -" + getNombre() + "- NUMERO ID:  - " + getNumeroId() + " -\n---> PRODUCTOS:");
            this.misProductos.values().forEach(Producto::describirProducto);
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    public void informacionMinimaInventario(){
        System.out.printf("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d%n",
                this.getNombre(),this.getNumeroId(),this.getCapacidadMaxima(),this.getCapacidadOcupada());
    }

    public void mostrarStockInventario(){
        int capacidadLibre = (this.capacidadMaxima-this.capacidadOcupada);
        System.out.println("Inventario: " + getNombre() + "\nCapacidad Maxima:  " + this.capacidadMaxima +
                "  Capacidad Libre:  " + capacidadLibre + "  Capacidad Ocupada:  " + this.capacidadOcupada);
    }

    public boolean tieneProductos(){
        return !this.misProductos.isEmpty();
    }

    //METODOS MODIFICAR INVENTARIO:

    public void cambiarNombreInventario(String nuevoNombre){
        if (nuevoNombre.isBlank()){
            System.out.println("ACCION RECHAZADA:\nNombre para el Inventario invalido");
        } else {
            System.out.print("El inventario:  -" + getNombre() + "-");
            setNombre(nuevoNombre);
            System.out.println("  Ahora tendra el nombre:  " + getNombre());
        }
    }

    public void agregarUnProducto(String nombreProducto,double valorCompra,int stock) {
        int capacidadLibre = (this.capacidadMaxima-this.capacidadOcupada);
        if (stock>capacidadLibre){
            System.out.println("NO Podemos agregar el Producto -" + nombreProducto + "- porque Exede la Capacidad " +
                    "Maxima del Inventario\nRecuerda que la Capacidad Maxima es de:  " + this.getCapacidadMaxima()
                    +"\nY la Capacidad Disponible Actualmente es de:  " + capacidadLibre);
            return;
        }
        try {
            Producto auxiliar = new Producto(nombreProducto, valorCompra, stock);
            this.misProductos.put(auxiliar.getCodigo(),auxiliar);
            System.out.println("Nuevo Producto Agregado Exitosamente:");
            auxiliar.describirProducto();
            this.capacidadOcupada+=auxiliar.getStock();
        } catch (IllegalArgumentException asignacionInvalida) {
            System.out.println("NO se puede agregar ese Producto al Inventario por un error de datos:");
            System.out.println("ERROR: " + asignacionInvalida.getMessage());
        }
    }

    public void agregarUnProducto(String nombreProducto,double valorCompra) {
        try {
            Producto auxiliar = new Producto(nombreProducto, valorCompra);
            this.misProductos.put(auxiliar.getCodigo(),auxiliar);
            System.out.println("Nuevo Producto Agregado Exitosamente:");
            auxiliar.describirProducto();
            this.capacidadOcupada+=auxiliar.getStock();
        } catch (IllegalArgumentException asignacionInvalida) {
            System.out.println("NO se puede agregar ese Producto al Inventario por un error de datos:");
            System.out.println("ERROR: " + asignacionInvalida.getMessage());
        }
    }

    public void eliminarUnProducto(int codigo){
        boolean productoEsta;
        productoEsta =this.misProductos.containsKey(codigo);
        if (productoEsta){
            System.out.println("El Producto: -" + this.misProductos.get(codigo).getNombre() + "-  De Codigo: -"
                    + this.misProductos.get(codigo).getCodigo() + "-  Ha Sido Eliminado Exitosamente");
            this.capacidadOcupada-=this.misProductos.get(codigo).getStock();
            this.misProductos.remove(codigo);
        } else {
            System.out.println("El Producto de Codigo: -" + codigo + "- NO esta en el Inventario\n" +
                    "Verifica el Codigo, Ningun Producto ha sido Eliminado");
        }
    }

    public boolean eliminarProductoYSaber(int codigo){
        boolean productoEsta;
        productoEsta =this.misProductos.containsKey(codigo);
        if (productoEsta){
            this.capacidadOcupada-=this.misProductos.get(codigo).getStock();
            this.misProductos.remove(codigo);
        }
        return productoEsta;
    }

    public void buscarProducto(int codigo){
        boolean productoEsta;
        productoEsta=this.misProductos.containsKey(codigo);
        if (productoEsta) {
            System.out.println("El Producto: -" + this.misProductos.get(codigo).getNombre() + "-  De Codigo: -" +
                    this.misProductos.get(codigo).getCodigo() + "- SI esta en el Inventario");
        } else {
            System.out.println("El Producto de Codigo: -" + codigo + "- NO esta en el Inventario");
        }
    }

    //METODOS VENDER PRODUCTO:

    public boolean buscarProductoParaVender(int codigo){
        boolean productoEsta;
        productoEsta = this.misProductos.containsKey(codigo);
        return productoEsta;
    }

    public double venderProducto(int codigo,int cantidad){
        boolean productoEsta;
        double pagoProducto=0;
        productoEsta=this.misProductos.containsKey(codigo);
        if (productoEsta) {
            if (cantidad > this.misProductos.get(codigo).getStock()) {
                System.out.println("ACCION RECHAZADA:\n" +
                        "Stock insuficiente, solo hay " + this.misProductos.get(codigo).getStock() + " unidades");
                return pagoProducto;
            }
            pagoProducto = (this.misProductos.get(codigo).getValorVenta())*cantidad;
            this.misProductos.get(codigo).reducirStock(cantidad);
            this.capacidadOcupada -= cantidad;
            System.out.println("\nHas Vendido " + cantidad + " Unidades de " + this.misProductos.get(codigo).getNombre());
            this.misProductos.get(codigo).describirProducto();
        }
        return pagoProducto;
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public Producto asignarProductoParaCambiarInventario(int codigo){
        boolean productoEsta;
        productoEsta = this.misProductos.containsKey(codigo);
        Producto auxiliar = null;
        if (productoEsta){
            auxiliar = this.misProductos.get(codigo);
        } else {
            System.out.println("El Producto de Codigo: -" + codigo + "- NO esta en el Inventario");
        }
        return auxiliar;
    }

    public void agregarProductoHecho(Producto producto){
        this.misProductos.put(producto.getCodigo(),producto);
        this.capacidadOcupada += producto.getStock();
    }

    //METODOS MODIFICAR PRODUCTO:

    public void actualizarValorVentaPorPorcentaje(int codigo, double porcentaje){
        boolean productoEsta;
        productoEsta = this.misProductos.containsKey(codigo);
        if (productoEsta) {
            this.misProductos.get(codigo).cambiarValorVentaPorPorcentaje(porcentaje);
        } else {
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarValorVentaPorPrecio(int codigo,double precio){
        boolean productoEsta;
        productoEsta = this.misProductos.containsKey(codigo);
        if (productoEsta) {
            this.misProductos.get(codigo).cambiarValorVentaPorPrecio(precio);
        } else {
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarNombreProducto(int codigo,String nombre){
        boolean productoEsta;
        productoEsta = this.misProductos.containsKey(codigo);
        if (productoEsta) {
            this.misProductos.get(codigo).cambiarNombreProducto(nombre);
        } else {
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarValorCompra(int codigo,double valorNuevo){
        boolean productoEsta;
        productoEsta = this.misProductos.containsKey(codigo);
        if (productoEsta) {
            this.misProductos.get(codigo).cambiarValorCompra(valorNuevo);
        } else {
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void agregarStockProducto(int codigo, int cantidad){
        if (cantidad<=0){
            System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar NO es valida");
        } else {
            boolean productoEsta;
            productoEsta = this.misProductos.containsKey(codigo);
            if (productoEsta) {
                int capacidadLibre = (this.capacidadMaxima-this.capacidadOcupada);
                if (cantidad>capacidadLibre){
                    System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar Excede la Capacidad " +
                            "Disponible del Inventario:  " + capacidadLibre + " Espacios Disponibles");
                } else {
                    this.misProductos.get(codigo).actualizarStock(cantidad);
                    this.capacidadOcupada += cantidad;
                    System.out.println("Stock del Producto -" + this.misProductos.get(codigo).getNombre() + "- Actualizado Con Exito:\n" +
                            "Ahora hay " + this.misProductos.get(codigo).getStock() + " Unidades del Producto " +
                            this.misProductos.get(codigo).getNombre() + " en el Inventario: -" + getNombre() + "-");
                }
            } else {
                System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
            }
        }
    }

    public void reducirStockProducto(int codigo,int cantidad){
        if (cantidad<=0){
            System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar NO es valida");
        } else {
            boolean productoEsta;
            productoEsta = this.misProductos.containsKey(codigo);
            if (productoEsta) {
                if (cantidad>this.misProductos.get(codigo).getStock()){
                    System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas reducir es mayor a la cantidad existente");
                } else {
                    this.misProductos.get(codigo).reducirStock(cantidad);
                    this.capacidadOcupada -= cantidad;
                    System.out.println("Stock del Producto -" + this.misProductos.get(codigo).getNombre() + "- Actualizado Con Exito:\n" +
                            "Ahora hay " + this.misProductos.get(codigo).getStock() + " Unidades del Producto " + this.misProductos.get(codigo).getNombre() +
                            " en el Inventario: -" + getNombre() + "-");
                }
            } else {
                System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
            }
        }
    }

}
