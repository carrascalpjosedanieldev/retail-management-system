package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.servicios.ControladorTienda;
import ProyectoPropio1.dto.*;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuModificarInventario.modificarInventario;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;
import static ProyectoPropio1.vistaConsola.MenuGestionarServicios.gestionarServicios;

public class MenuModificarTienda {

    private static void menuModificarTienda(){
        System.out.print("""
                \n                             -MODIFICAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                           CAMBIAR NOMBRE TIENDA
                                   2                             AGREGAR INVENTARIO
                                   3                               VER INVENTARIOS
                                   4                           VER DETALLE INVENTARIO
                                   5                            MODIFICAR INVENTARIO
                                   6                          ELIMINAR INVENTARIO VACIO
                                   7                            GESTIONAR SERVICIOS
                                   8                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }


    public static void modificarTienda(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR TIENDA-");
        int opcionModificarTienda;
        do {
            menuModificarTienda();
            opcionModificarTienda = pedirOpcion(sc,1,8);
            switch (opcionModificarTienda){
                case 1:
                    cambiarNombreTienda(sc,controladorTienda);
                    break;
                case 2:
                    agregarInventario(sc, controladorTienda);
                    break;
                case 3:
                    verInventarios(controladorTienda);
                    break;
                case 4:
                    verDetalleInventario(sc,controladorTienda);
                    break;
                case 5:
                    modificarInventario(sc, controladorTienda);
                    break;
                case 6:
                    eliminarInventarioVacio(sc, controladorTienda);
                    break;
                case 7:
                    gestionarServicios(sc, controladorTienda);
                    break;
                case 8:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionModificarTienda!=8);
    }


    private static void cambiarNombreTienda(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -CAMBIAR NOMBRE TIENDA-");
        try {
            System.out.print("La Tienda actualmente se llama -" + controladorTienda.obtenerNombreTienda() + "\n" +
                    "Escribe el nombre nuevo que le quieres colocar:\n" +
                    "---> ");
            String nuevoNombreTienda = sc.nextLine();
            controladorTienda.cambiarNombreTienda(nuevoNombreTienda);
            System.out.println("Nombre cambiado con Exito\n" +
                    "La Tienda ahora se llama -" + nuevoNombreTienda + "-");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void agregarInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -AGREGAR INVENTARIO-");
        try {
            System.out.print("""
                    Escribe el Nombre del Inventario:
                    """ + "---> ");
            String nombreInventario = sc.nextLine();
            System.out.print("""
                    \nEscribe la Capacidad del inventario:
                    """ + "---> ");
            int capacidadInventario = leerEntero(sc);
            controladorTienda.agregarInventarioATienda(nombreInventario,capacidadInventario);
            System.out.println("Nuevo Inventario generado con Exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void verDetalleInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -VER DETALLE INVENTARIO-");
        try {
            DetalleInventarioGeneralDTO detalleInventarioGeneral = controladorTienda.obtenerDetalleInventarioGeneral();
            System.out.println("---> INVENTARIOS:");
            System.out.println("------------------------------------------------------------------------------------------------------------");
            for (DatosInventarioDTO datosInventario:detalleInventarioGeneral.inventarioGeneral()){
                String informacionMinima;
                informacionMinima = String.format("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d CAPACIDAD LIBRE: %-10d%n",
                        datosInventario.nombre(),datosInventario.idInventario(),datosInventario.capacidadMaxima(),datosInventario.capacidadOcupada(),datosInventario.capacidadLibre());
                System.out.println(informacionMinima);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
            System.out.print("""
                    \nElige el Inventario por su numero identificador:
                    """ + "---> ");
            int id = leerEntero(sc);
            int numeroId = leerEntero(sc);
            if (controladorTienda.inventarioNoTieneProductos(numeroId)){
                System.out.println("""
                \nACCION DENEGADA
                EL INVENTARIO ESTA VACIO
                """);
            }
            DetalleInventarioDTO detalleInventario = controladorTienda.obtenerDetalleInventario(numeroId);
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("---> INVENTARIO:  -" + detalleInventario.nombre() + "-  NUMERO ID:  -" + detalleInventario.id() + "-");
            if (detalleInventario.productos().isEmpty()){
                System.out.println("---> ESTE INVENTARIO NO TIENE PRODUCTOS");
            } else {
                for (DatosTotalesProductoDTO datosTotalesProducto:detalleInventario.productos()){
                    if (datosTotalesProducto instanceof DatosTotalesProductoRopaDTO productoRopa) {
                        String datosProducto = String.format(
                            "Tipo de Producto: Ropa  Nombre del Producto: %-12s Talla: %-4s Codigo: %-4d " +
                            "Valor Compra: %-12.2f Ganancia: %3.0f%% Valor Venta: %-12.2f Stock: %-4d%n",
                            productoRopa.nombre(), productoRopa.talla(), productoRopa.codigo(),
                            productoRopa.valorCompra(), productoRopa.porcentajeGanancia(), productoRopa.valorVentaBase(),
                            productoRopa.stock()
                        );
                        System.out.println(datosProducto);
                    } else if (datosTotalesProducto instanceof DatosTotalesProductoPerecederoDTO productoPerecedero) {
                        String datosProducto = String.format(
                            "Tipo de Producto:  Comida    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-4d " +
                            "Valor Compra:  %-12.2f Ganancia:  %3.0f%% Valor Venta:  %-12.2f Stock:  %-4d Estado: %-15s %n",
                            productoPerecedero.nombre(), productoPerecedero.fechaVencimiento(), productoPerecedero.codigo(),
                            productoPerecedero.valorCompra(), productoPerecedero.porcentajeGanancia(), productoPerecedero.valorVentaBase(),
                            productoPerecedero.stock(), productoPerecedero.estaVencido()
                        );
                        System.out.println(datosProducto);
                    } else {
                        System.out.println("Tipo de Producto no se puede describir");
                    }
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void verInventarios(ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
            return;
        }
        try {
            System.out.println("""
                \nHAS SELECCIONADO: -VER INVENTARIOS-
                ---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
            DetalleInventarioGeneralDTO detalleInventarioGeneral = controladorTienda.obtenerDetalleInventarioGeneral();
            for (DatosInventarioDTO datosInventario:detalleInventarioGeneral.inventarioGeneral()){
                String informacionMinima;
                informacionMinima = String.format("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d CAPACIDAD LIBRE: %-10d%n",
                        datosInventario.nombre(),datosInventario.idInventario(),datosInventario.capacidadMaxima(),datosInventario.capacidadOcupada(),datosInventario.capacidadLibre());
                System.out.println(informacionMinima);
            }
            System.out.println("------------------------------------------------------------------------------------");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void eliminarInventarioVacio(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
            return;
        }
        try {
            System.out.println("""
                \nHAS SELECCIONADO: -ELIMINAR INVENTARIO VACIO-
                \n---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
            DetalleInventarioGeneralDTO detalleInventarioGeneral = controladorTienda.obtenerDetalleInventarioGeneral();
            for (DatosInventarioDTO datosInventario:detalleInventarioGeneral.inventarioGeneral()){
                String informacionMinima;
                informacionMinima = String.format("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d CAPACIDAD LIBRE: %-10d%n",
                        datosInventario.nombre(),datosInventario.idInventario(),datosInventario.capacidadMaxima(),datosInventario.capacidadOcupada(),datosInventario.capacidadLibre());
                System.out.println(informacionMinima);
            }
            System.out.print("""
                \n------------------------------------------------------------------------------------
                Elige el Inventario que vas a Eliminar por su numero identificador:
                """ + "---> ");
            int numeroId = leerEntero(sc);
            controladorTienda.eliminarInventarioVacio(numeroId);
            System.out.println("Inventario eliminado con exito");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}

