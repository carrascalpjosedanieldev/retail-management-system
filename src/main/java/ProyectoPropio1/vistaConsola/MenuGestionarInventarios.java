package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.*;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.util.List;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuModificarInventario.modificarInventarioEspecifico;
import static ProyectoPropio1.vistaConsola.MetodosTienda.leerEntero;
import static ProyectoPropio1.vistaConsola.MetodosTienda.pedirOpcion;

public class MenuGestionarInventarios {

    private static void menuGestionarInventarios(){
        System.out.print("""
                \n                             -MODIFICAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                             AGREGAR INVENTARIO
                                   2                        VER INFO MINIMA INVENTARIOS
                                   3                           VER DETALLE INVENTARIO
                                   4                          ELIMINAR INVENTARIO VACIO
                                   5                       MODIFICAR INVENTARIO ESPECIFICO
                                   6                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void gestionarInventarios(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -GESTIONAR INVENTARIOS-");
        int opcionGestionarInventarios;
        do {
            menuGestionarInventarios();
            opcionGestionarInventarios = pedirOpcion(sc,1,6);
            switch (opcionGestionarInventarios){
                case 1:
                    agregarInventario(sc, controladorTienda);
                    break;
                case 2:
                    verInfoMinimaInventarios(controladorTienda);
                    break;
                case 3:
                    verDetalleInventario(sc,controladorTienda);
                    break;
                case 4:
                    eliminarInventarioVacio(sc, controladorTienda);
                    break;
                case 5:
                    modificarInventarioEspecifico(sc, controladorTienda);
                    break;
                case 6:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionGestionarInventarios !=6);
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
            int idInventario = controladorTienda.agregarInventarioATienda(nombreInventario,capacidadInventario);
            System.out.println("Se generó el inventario con éxito. Su ID asignado es: " + idInventario);
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void verDetalleInventario(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -VER DETALLE INVENTARIO-");
        try {
            List<DatosInventarioDTO> detalleInventarioGeneral = controladorTienda.obtenerDetalleInventarioGeneral();
            System.out.println("---> INVENTARIOS:");
            System.out.println("------------------------------------------------------------------------------------------------------------");
            for (DatosInventarioDTO datosInventario:detalleInventarioGeneral){
                String informacionMinima;
                informacionMinima = String.format("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d CAPACIDAD LIBRE: %-10d%n",
                        datosInventario.nombre(),datosInventario.idInventario(),datosInventario.capacidadMaxima(),datosInventario.capacidadOcupada(),datosInventario.capacidadLibre());
                System.out.println(informacionMinima);
            }
            System.out.println("------------------------------------------------------------------------------------------------------------");
            System.out.print("""
                    \nElige el Inventario al que quieres ver el Detalle por su numero identificador:
                    """ + "---> ");
            int numeroId = leerEntero(sc);
            if (detalleInventarioGeneral.isEmpty()){
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
                                "Tipo de Producto: Ropa  Nombre del Producto: %-12s Talla: %-4s Codigo: %-40s " +
                                        "Valor Compra: %-12.2f Ganancia: %3.2f%% Valor Venta: %-12.2f Stock: %-4d%n",
                                productoRopa.nombre(), productoRopa.talla().toString(), productoRopa.codigo(),
                                productoRopa.valorCompra(), productoRopa.porcentajeGanancia(), productoRopa.valorVentaBase(),
                                productoRopa.stock()
                        );
                        System.out.println(datosProducto);
                    } else if (datosTotalesProducto instanceof DatosTotalesProductoPerecederoDTO productoPerecedero) {
                        String datosProducto = String.format(
                                "Tipo de Producto:  Perecedero    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-40s " +
                                        "Valor Compra:  %-12.2f Ganancia:  %3.2f%% Valor Venta:  %-12.2f Stock:  %-4d Estado: %-15s %n",
                                productoPerecedero.nombre(), productoPerecedero.fechaVencimiento().toString(), productoPerecedero.codigo(),
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

    private static void verInfoMinimaInventarios(ControladorTienda controladorTienda){
        try {
            List<DatosInventarioDTO> detalleInventarioGeneral = controladorTienda.obtenerDetalleInventarioGeneral();
            if (detalleInventarioGeneral.isEmpty()){
                System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
                return;
            }
            System.out.println("""
                \nHAS SELECCIONADO: -VER INVENTARIOS-
                ---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
            for (DatosInventarioDTO datosInventario:detalleInventarioGeneral){
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
        try {
            List<DatosInventarioDTO> detalleInventarioGeneral = controladorTienda.obtenerDetalleInventarioGeneral();
            if (detalleInventarioGeneral.isEmpty()){
                System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIOS
                    """);
                return;
            }
            System.out.println("""
                \nHAS SELECCIONADO: -ELIMINAR INVENTARIO VACIO-
                \n---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
            for (DatosInventarioDTO datosInventario:detalleInventarioGeneral){
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
