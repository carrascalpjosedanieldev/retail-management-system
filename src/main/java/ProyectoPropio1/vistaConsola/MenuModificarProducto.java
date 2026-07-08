package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.excepciones.ProductoNoEncontradoException;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.math.BigDecimal;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuModificarProducto {

    private static void menuModificarProducto(){
        System.out.print("""
                \n                           -MODIFICAR PRODUCTO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                          CAMBIAR NOMBRE PRODUCTO
                                   2                           ACTUALIZAR VALOR COMPRA
                                   3                       ACTUALIZAR PORCENTAJE GANANCIA
                                   4                               AUMENTAR STOCK
                                   5                                REDUCIR STOCK
                                   6                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void modificarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario){
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
                    actualizarValorCompraProducto(sc, controladorTienda, idInventario);
                    break;
                case 3:
                    actualizarPorcentajeGanancia(sc, controladorTienda, idInventario);
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
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.estaProductoEnInventario(idInventario, codigoProducto);
            System.out.print("Escribe el nombre nuevo que le pondras:\n" +
                    "---> ");
            String nombreNuevoProd = sc.nextLine();
            controladorTienda.actualizarNombreDeProductoDeInventario(idInventario, codigoProducto, nombreNuevoProd);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora se llama:  " + nombreNuevoProd);
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void actualizarValorCompraProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -ACTUALIZAR VALOR COMPRA PRODUCTO-
                A que producto le cambiaras el Valor de Compra, Escribe el Codigo
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.estaProductoEnInventario(idInventario, codigoProducto);
            System.out.print("""
                \nEscribe el valor nuevo que le pondras al producto:
                """ + "---> ");
            BigDecimal valorNuevo = leerDecimal(sc);
            controladorTienda.actualizarValorCompraDeProductoDeInventario(idInventario, codigoProducto, valorNuevo);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora tendra el valor de compra:  $" + valorNuevo);
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void actualizarPorcentajeGanancia(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -ACTUALIZAR PORCENTAJE GANANCIA PRODUCTO-
                A que producto le cambiaras el Porcentaje de Ganancia, Escribe el Codigo
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.estaProductoEnInventario(idInventario, codigoProducto);
            System.out.print("""
                \nEscribe el Porcentaje de Ganancia nuevo que le pondras al Producto (Maximo 100%):
                """ + "---> ");
            BigDecimal porcentajeNuevo = leerDecimal(sc);
            controladorTienda.actualizarPorcentajeGananciaDeProductoDeInventario(idInventario, codigoProducto, porcentajeNuevo);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ahora tendra el porcentaje de ganancia:  " + porcentajeNuevo + "%");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void aumentarStockProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -AUMENTAR STOCK PRODUCTO-
                A que producto le cambiaras el Stock, Escribe el Codigo
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.estaProductoEnInventario(idInventario, codigoProducto);
            System.out.print("""
                \nEscribe las unidades nuevas que llegaron:
                """ + "---> ");
            int cantidad = leerEntero(sc);
            controladorTienda.aumentarStockDeProductoDeInventario(idInventario, codigoProducto, cantidad);
            System.out.println("Al Producto de Codigo -" + codigoProducto + "- se le agregaran " + cantidad + " unidades al stock");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void reducirStockProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -REDUCIR STOCK PRODUCTO-
                A que producto le cambiaras el Stock, Escribe el Codigo
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.estaProductoEnInventario(idInventario, codigoProducto);
            System.out.print("""
                \nEscribe las unidades eliminadas:
                """ + "---> ");
            int cantidadQuitada = leerEntero(sc);
            controladorTienda.reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidadQuitada);
            System.out.println("Al Producto de Codigo -" + codigoProducto + "- se le reduciran " + cantidadQuitada + " unidades al stock");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}

