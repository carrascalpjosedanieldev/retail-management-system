package ProyectoUniversidad1;

import java.util.ArrayList;

public class Inventario { //302 LINEAS NETAS DE 352 LINEAS TOTALES

    //ATRIBUTOS:

    private String nombre;

    private final int NUMERO_IDENTIFICADOR;

    private static int numeroIdentificadorSiguiente = 1;

    private final int CAPACIDAD_MAXIMA;

    private int capacidadOcupada;

    private final ArrayList<Producto> misProductos;

    //GETTERS Y SETTERS:

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNUMERO_IDENTIFICADOR() {
        return NUMERO_IDENTIFICADOR;
    }

    public int getCAPACIDAD_MAXIMA() {
        return CAPACIDAD_MAXIMA;
    }

    public int getCapacidadOcupada() {
        return capacidadOcupada;
    }

    //CONSTRUCTORES:

    public Inventario(String nombre, int CAPACIDAD_MAXIMA) {
        if (nombre.isBlank()){
            throw new IllegalArgumentException("Nombre del Inventario Invalido");
        }
        if (CAPACIDAD_MAXIMA<=0){
            throw new IllegalArgumentException("Capacidad Maxima del Inventario Invalida");
        }
        this.nombre = nombre;
        this.NUMERO_IDENTIFICADOR = numeroIdentificadorSiguiente++;
        this.CAPACIDAD_MAXIMA = CAPACIDAD_MAXIMA;
        this.misProductos = new ArrayList<>();
        this.capacidadOcupada=0;
    }

    //METODOS:

    public void mostrarInventario(){
        if (this.misProductos.isEmpty()){
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------\n" +
                    "---> INVENTARIO:  -" + getNombre() + "-\n---> PRODUCTOS: ESTE INVENTARIO ESTA VACIO");
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        } else {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------\n" +
                    "---> INVENTARIO:  -" + getNombre() + "-\n---> PRODUCTOS:");
            for (Producto miProducto:this.misProductos) {
                miProducto.describirProducto();
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        }
    }

    public void informacionMinimaInventario(){
        System.out.printf("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d%n",this.getNombre(),this.getNUMERO_IDENTIFICADOR(),this.getCAPACIDAD_MAXIMA(),this.getCapacidadOcupada());
    }

    public void mostrarStockInventario(){
        int capacidadLibre = (this.CAPACIDAD_MAXIMA-this.capacidadOcupada);
        System.out.println("Inventario: " + getNombre() + "\nCapacidad Maxima:  " + this.CAPACIDAD_MAXIMA +
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

    public void agregarUnProducto(String nombreProducto,double valorCompra,int stock){
        int capacidadLibre = (this.CAPACIDAD_MAXIMA-this.capacidadOcupada);
        if (stock>capacidadLibre){
            System.out.println("NO Podemos agregar el Producto -" + nombreProducto + "- porque Exede la Capacidad " +
                    "Maxima del Inventario\nRecuerda que la Capacidad Maxima es de:  " + this.getCAPACIDAD_MAXIMA()
                    +"\nY la Capacidad Disponible Actualmente es de:  " + capacidadLibre);
            return;
        }
        try {
            Producto auxiliar = new Producto(nombreProducto, valorCompra, stock);
            this.misProductos.add(auxiliar);
            System.out.println("Nuevo Producto Agregado Exitosamente:");
            auxiliar.describirProducto();
            this.capacidadOcupada+=auxiliar.getStock();
        } catch (IllegalArgumentException asignacionInvalida) {
            System.out.println("NO se puede agregar ese Producto al Inventario por un error de datos:");
            System.out.println("ERROR: " + asignacionInvalida.getMessage());
        }
    }

    public void agregarUnProducto(String nombreProducto,double valorCompra){
        try {
            Producto auxiliar = new Producto(nombreProducto, valorCompra);
            this.misProductos.add(auxiliar);
            System.out.println("Nuevo Producto Agregado Exitosamente:");
            auxiliar.describirProducto();
            this.capacidadOcupada+=auxiliar.getStock();
        } catch (IllegalArgumentException asignacionInvalida) {
            System.out.println("NO se puede agregar ese Producto al Inventario por un error de datos:");
            System.out.println("ERROR: " + asignacionInvalida.getMessage());
        }
    }

    public void eliminarUnProducto(int codigo){
        int buscaCodigo;
        boolean codigoValido=false;
        for (int i = 0; i < this.misProductos.size(); i++) {
            buscaCodigo=this.misProductos.get(i).getCODIGO();
            if (buscaCodigo==codigo){
                System.out.println("El Producto: -" + this.misProductos.get(i).getNombre() + "-  De Codigo: -"
                        + this.misProductos.get(i).getCODIGO() + "-  Ha Sido Eliminado Exitosamente");
                this.capacidadOcupada-=this.misProductos.get(i).getStock();
                this.misProductos.remove(i);
                codigoValido=true;
                break;
            }
        }
        if (!codigoValido){
            System.out.println("El Producto de Codigo: -" + codigo + "- NO esta en el Inventario\n" +
                    "Verifica el Codigo, Ningun Producto ha sido Eliminado");
        }
    }

    public void buscarProducto(int codigo){
        int buscaCodigo;
        boolean productoEsta =false;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                System.out.println("El Producto: -" + miProducto.getNombre() + "-  De Codigo: -" + miProducto.getCODIGO()
                        + "- SI esta en el Inventario");
                productoEsta =true;
                break;
            }
        }
        if (!productoEsta){
            System.out.println("El Producto de Codigo: -" + codigo + "- NO esta en el Inventario");
        }
    }

    //METODOS VENDER PRODUCTO:

    public boolean buscarProductoParaVender(int codigo){
        int buscaCodigo;
        boolean productoEsta =false;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                productoEsta =true;
                break;
            }
        }
        return productoEsta;
    }

    public double venderProducto(int codigo,int cantidad){
        int buscaCodigo;
        double pagoProducto=0;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                if (cantidad > miProducto.getStock()) {
                    System.out.println("ACCION RECHAZADA:\n" +
                            "Stock insuficiente, solo hay " + miProducto.getStock() + " unidades");
                    return pagoProducto;
                }
                pagoProducto = (miProducto.getValorVenta())*cantidad;
                miProducto.reducirStock(cantidad);
                this.capacidadOcupada -= cantidad;
                System.out.println("\nHas Vendido " + cantidad + " Unidades de " + miProducto.getNombre());
                miProducto.describirProducto();
                break;
            }
        }
        return pagoProducto;
    }

    //METODOS MOVER PRODUCTO A OTRO INVENTARIO:

    public Producto asignarProductoParaCambiarInventario(int codigo){
        int buscaCodigo;
        boolean productoEsta =false;
        Producto auxiliar = null;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                productoEsta =true;
                auxiliar = miProducto;
                break;
            }
        }
        if (!productoEsta){
            System.out.println("El Producto de Codigo: -" + codigo + "- NO esta en el Inventario");
        }
        return auxiliar;
    }

    public void agregarProductoHecho(Producto producto){
        this.misProductos.add(producto);
        this.capacidadOcupada += producto.getStock();
    }

    //METODOS MODIFICAR PRODUCTO:

    public void actualizarValorVentaPorPorcentaje(int codigo, double porcentaje){
        int buscaCodigo;
        boolean productoEsta =false;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                miProducto.cambiarValorVentaPorPorcentaje(porcentaje);
                productoEsta =true;
                break;
            }
        }
        if (!productoEsta){
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarValorVentaPorPrecio(int codigo,double precio){
        int buscaCodigo;
        boolean productoEsta =false;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                miProducto.cambiarValorVentaPorPrecio(precio);
                productoEsta =true;
                break;
            }
        }
        if (!productoEsta){
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarNombreProducto(int codigo,String nombre){
        int buscaCodigo;
        boolean productoEsta =false;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                miProducto.cambiarNombreProducto(nombre);
                productoEsta =true;
                break;
            }
        }
        if (!productoEsta){
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarValorCompra(int codigo,double valorNuevo){
        int buscaCodigo;
        boolean productoEsta =false;
        for (Producto miProducto : this.misProductos) {
            buscaCodigo=miProducto.getCODIGO();
            if (buscaCodigo == codigo) {
                miProducto.cambiarValorCompra(valorNuevo);
                productoEsta =true;
                break;
            }
        }
        if (!productoEsta){
            System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
        }
    }

    public void actualizarStockProducto(int codigo,int cantidad){
        if (cantidad<=0){
            System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar NO es valida");
        } else {
            int buscaCodigo;
            boolean productoEsta =false;
            for (Producto miProducto : this.misProductos) {
                buscaCodigo=miProducto.getCODIGO();
                if (buscaCodigo == codigo) {
                    int capacidadLibre = (this.CAPACIDAD_MAXIMA-this.capacidadOcupada);
                    if (cantidad>capacidadLibre){
                        System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar Excede la Capacidad " +
                                "Disponible del Inventario:  " + capacidadLibre + " Espacios Disponibles");
                    } else {
                        miProducto.actualizarStock(cantidad);
                        this.capacidadOcupada += cantidad;
                        System.out.println("Stock del Producto -" + miProducto.getNombre() + "- Actualizado Con Exito:\n" +
                                "Ahora hay " + miProducto.getStock() + " Unidades del Producto " + miProducto.getNombre() +
                                " en el Inventario: -" + getNombre() + "-");
                    }
                    productoEsta =true;
                    break;
                }
            }
            if (!productoEsta){
                System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
            }
        }
    }

    public void reducirStockProducto(int codigo,int cantidad){
        if (cantidad<=0){
            System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar NO es valida");
        } else {
            int buscaCodigo;
            boolean productoEsta =false;
            for (Producto miProducto : this.misProductos) {
                buscaCodigo=miProducto.getCODIGO();
                if (buscaCodigo == codigo) {
                    if (cantidad>miProducto.getStock()){
                        System.out.println("ACCION RECHAZADA:\nLa Cantidad que deseas ingresar es mayor a la cantidad existente");
                    } else {
                        miProducto.reducirStock(cantidad);
                        this.capacidadOcupada -= cantidad;
                        System.out.println("Stock del Producto -" + miProducto.getNombre() + "- Actualizado Con Exito:\n" +
                                "Ahora hay " + miProducto.getStock() + " Unidades del Producto " + miProducto.getNombre() +
                                " en el Inventario: -" + getNombre() + "-");
                    }
                    productoEsta =true;
                    break;
                }
            }
            if (!productoEsta){
                System.out.println("El Producto de Codigo:  " + codigo + " NO esta en el Inventario");
            }
        }
    }

}
