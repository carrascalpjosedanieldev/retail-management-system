package ProyectoUniversidad1;

import java.util.Scanner;

import static ProyectoUniversidad1.MetodosTienda.*;

public class MenuModificarProducto {

    private static void menuModificarProducto(){
        System.out.print("""
                \n                           -MODIFICAR PRODUCTO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                          CAMBIAR NOMBRE PRODUCTO
                                   2                           ACTUALIZAR VALOR VENTA
                                   3                           ACTUALIZAR VALOR COMPRA
                                   4                               AUMENTAR STOCK
                                   5                                REDUCIR STOCK
                                   6                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void modificarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        if (!controladorTienda.inventarioTieneProductos(idInventario)){
            System.out.println("""
                    \nACCION DENEGADA
                    EL INVENTARIO ESTA VACIO
                    NO PUEDES MODIFICAR NADA
                    """);
            return;
        }
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR PRODUCTO-");
        int opcionModificarProducto;
        do {
            menuModificarProducto();
            opcionModificarProducto = pedirOpcion(sc,1,6);
            switch (opcionModificarProducto){
                case 1:
                    cambiarNombreProducto(sc, controladorTienda, idInventario);
                    break;
                case 2:
                    actualizarValorVentaProducto(sc, controladorTienda, idInventario);
                    break;
                case 3:
                    actualizarValorCompraProducto(sc, controladorTienda, idInventario);
                    break;
                case 4:
                    aumentarStockProducto(sc, controladorTienda, idInventario);
                    break;
                case 5:
                    reducirStockProducto(sc, controladorTienda, idInventario);
                    break;
                case 6:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionModificarProducto!=6);
    }

    private static void cambiarNombreProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR NOMBRE PRODUCTO-
                A que producto le cambiaras el Nombre, escribe el codigo
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        if (!controladorTienda.existeProductoEnInventario(idInventario, codigoProducto)) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
            return;
        }
        System.out.print("Escribe el nombre nuevo que le pondras:\n" +
                "---> ");
        String nombreNuevoProd = sc.nextLine();
        try {
            controladorTienda.cambiarNombreDeProductoDeInventario(idInventario, codigoProducto, nombreNuevoProd);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora se llama:  " + nombreNuevoProd);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " + e.getMessage());
        }
    }

    private static void actualizarValorVentaProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -ACTUALIZAR VALOR VENTA PRODUCTO-
                A que producto le cambiaras el Valor de Venta, Escribe el Codigo
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        if (!controladorTienda.existeProductoEnInventario(idInventario, codigoProducto)) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
            return;
        }
        System.out.print("""
                \nPor que modo cambiaras el valor de venta: PRECIO(1) PORCENTAJE(2)
                Escribe el numero segun el modo(1/2)
                """ + "---> ");
        int precioOProcentaje=leerEntero(sc);
        while (precioOProcentaje!=1 && precioOProcentaje!=2){
            System.out.print("""
                    \nEscribe el numero segun el modo(1/2)
                    """ + "---> ");
            precioOProcentaje = leerEntero(sc);
        }
        try {
            if (precioOProcentaje==1){
                System.out.print("""
                    \nEscribe el precio nuevo que le pondras:
                    """ + "---> ");
                double precio = leerDecimal(sc);
                controladorTienda.actualizarValorVentaPorPrecioDeProductoDeInventario(idInventario, codigoProducto, precio);
                System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora tendra el valor de venta:  $" + precio);
                return;
            }
            System.out.print("""
                \nEscribe el porcentaje de ganancia nuevo que le pondras:
                """ + "---> ");
            double porcentaje = leerDecimal(sc);
            controladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(idInventario, codigoProducto, porcentaje);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora valdra " + porcentaje + "% mas que el valor de compra");
        } catch (IllegalArgumentException e){
            System.out.println("ERRPR:  " + e.getMessage());
        }
    }

    private static void actualizarValorCompraProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -ACTUALIZAR VALOR COMPRA PRODUCTO-
                A que producto le cambiaras el Valor de Compra, Escribe el Codigo
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        if (!controladorTienda.existeProductoEnInventario(idInventario, codigoProducto)) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
            return;
        }
        System.out.print("""
                \nEscribe el valor nuevo que le pondras al producto:
                """ + "---> ");
        double valorNuevo = leerDecimal(sc);
        try {
            controladorTienda.actualizarValorCompraDeProductoDeInventario(idInventario, codigoProducto, valorNuevo);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora tendra el valor de compra:  $" + valorNuevo);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " + e.getMessage());
        }
    }

    private static void aumentarStockProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -AUMENTAR STOCK PRODUCTO-
                A que producto le cambiaras el Stock, Escribe el Codigo
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        if (!controladorTienda.existeProductoEnInventario(idInventario, codigoProducto)) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
            return;
        }
        System.out.print("""
                \nEscribe las unidades nuevas que llegaron:
                """ + "---> ");
        int cantidad = leerEntero(sc);
        try {
            controladorTienda.aumentarStockDeProductoDeInventario(idInventario, codigoProducto, cantidad);
            System.out.println("Al Producto de Codigo -" + codigoProducto + "- se le agregaran " + cantidad + " unidades al stock");
        } catch (IllegalArgumentException e){
            System.out.println("ERROR:  " + e.getMessage());
        }
    }

    private static void reducirStockProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -REDUCIR STOCK PRODUCTO-
                A que producto le cambiaras el Stock, Escribe el Codigo
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        if (!controladorTienda.existeProductoEnInventario(idInventario, codigoProducto)) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
            return;
        }
        System.out.print("""
                \nEscribe las unidades eliminadas:
                """ + "---> ");
        int cantidadQuitada = leerEntero(sc);
        try {
            controladorTienda.reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidadQuitada);
            System.out.println("Al Producto de Codigo -" + codigoProducto + "- se le reduciran " + cantidadQuitada + " unidades al stock");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR:  " + e.getMessage());
        }
    }

}
