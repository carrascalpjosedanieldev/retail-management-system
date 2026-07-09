package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.excepciones.ProductoNoEncontradoException;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.math.BigDecimal;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuGestionarDescuentos.descuentosActivosParaRegistro;
import static ProyectoPropio1.vistaConsola.MenuGestionarImpuestos.impuestosActivosParaRegistro;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuModificarProducto {

    private static void menuModificarProducto(){
        System.out.print("""
                \n                        -MODIFICAR PRODUCTO ESPECIFICO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                          CAMBIAR NOMBRE PRODUCTO
                                   2                           ACTUALIZAR VALOR COMPRA
                                   3                       ACTUALIZAR PORCENTAJE GANANCIA
                                   4                               AUMENTAR STOCK
                                   5                                REDUCIR STOCK
                                   6                              CAMBIAR IMPUESTO
                                   7                              CAMBIAR DESCUENTO
                                   8                              ACTIVAR PRODUCTO
                                   9                             DESACTIVAR PRODUCTO
                                   10                                  SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void modificarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR PRODUCTO ESPECIFICO-");
        int opcionModificarProducto;
        do {
            menuModificarProducto();
            opcionModificarProducto = pedirOpcion(sc,1,10);
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
                    cambiarImpuesto(sc, controladorTienda, idInventario);
                    break;
                case 7:
                    cambiarDescuento(sc, controladorTienda, idInventario);
                    break;
                case 8:
                    activarProducto(sc, controladorTienda, idInventario);
                    break;
                case 9:
                    desactivarProducto(sc, controladorTienda, idInventario);
                    break;
                case 10:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionModificarProducto!=10);
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

    private static void cambiarImpuesto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR IMPUESTO PRODUCTO-
                A que Producto le cambiaras el Impuesto, Escribe el Codigo
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            System.out.println(impuestosActivosParaRegistro(controladorTienda));
            System.out.print("""
                \nEscribe el ID del Impuesto que le pondras
                """ + "---> ");
            int idImpuesto = leerEntero(sc);
            controladorTienda.cambiarImpuestoAProducto(codigoProducto, idInventario, idImpuesto);
            System.out.println("\nImpuesto Cambiado con Exito");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void cambiarDescuento(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR DESCUENTO PRODUCTO-
                A que Producto le cambiaras el Descuento, Escribe el Codigo
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            System.out.println(descuentosActivosParaRegistro(controladorTienda));
            System.out.print("""
                \nEscribe el ID del Descuento que le pondras
                """ + "---> ");
            int idDescuento = leerEntero(sc);
            controladorTienda.cambiarDescuentoAProducto(codigoProducto, idInventario, idDescuento);
            System.out.println("\nDescuento Cambiado con Exito");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void activarProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -ACTIVAR PRODUCTO-
                Escribe el Codigo del Producto que vas a Activar
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.activarProducto(codigoProducto, idInventario);
            System.out.println("\nProducto Activado con Exito");
        } catch (ProductoNoEncontradoException e) {
            System.out.println("""
                \nACCION DENEGADA:
                EL CODIGO DE PRODUCTO INGRESADO NO EXISTE EN ESTE INVENTARIO
                """);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void desactivarProducto(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -DESACTIVAR PRODUCTO-
                Escribe el Codigo del Producto que vas a Desactivar
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.desactivarProducto(codigoProducto, idInventario);
            System.out.println("\nProducto Desactivado con Exito");
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

