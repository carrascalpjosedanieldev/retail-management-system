package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.servicios.ControladorTienda;
import ProyectoPropio1.dominio.TipoProducto;
import ProyectoPropio1.excepciones.CapacidadExcedidaException;
import ProyectoPropio1.excepciones.InventarioNoEncontradoException;
import ProyectoPropio1.excepciones.ProductoNoEncontradoException;
import ProyectoPropio1.dto.*;

import java.time.LocalDate;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuModificarProducto.modificarProductoAInventario;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuModificarInventario {

    private static void menuModificarInventario(){
        System.out.print("""
                \n                           -MODIFICAR INVENTARIO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                         CAMBIAR NOMBRE INVENTARIO
                                   2                              AGREGAR PRODUCTO
                                   3                             MODIFICAR PRODUCTO
                                   4                              ELIMINAR PRODUCTO
                                   5                               BUSCAR PRODUCTO
                                   6                      MOVER PRODUCTO A OTRO INVENTARIO
                                   7                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }


    public static void modificarInventario(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.tiendaNoTieneInventarios()){
            System.out.println("""
                    \nACCION DENEGADA
                    TODAVIA NO HAY INVENTARIO
                    """);
            return;
        }
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR INVENTARIO-");
        DetalleInventarioGeneralDTO detalleInventarioGeneral = controladorTienda.mostrarInfoInventariosDeTienda();
        int opcionModificarInventario;
        System.out.println("""
                \n---> INVENTARIOS:
                ------------------------------------------------------------------------------------
                """);
        for (DatosInventarioDTO datosInventario:detalleInventarioGeneral.inventarioGeneral()){
            String informacionMinima;
            informacionMinima = String.format("NOMBRE: %-10s NUMERO IDENTIFICADOR: %-5d CAPACIDAD MAXIMA: %-10d CAPACIDAD OCUPADA: %-10d CAPACIDAD LIBRE: %-10d%n",
                    datosInventario.nombre(),datosInventario.idInventario(),datosInventario.capacidadMaxima(),datosInventario.capacidadOcupada(),datosInventario.capacidadLibre());
            System.out.println(informacionMinima);
        }
        System.out.print("""
                \n------------------------------------------------------------------------------------
                Elige el Inventario por su numero identificador:
                """ + "---> ");
        int idInventario = leerEntero(sc);
        do {
            menuModificarInventario();
            opcionModificarInventario = pedirOpcion(sc,1,7);
            switch (opcionModificarInventario){
                case 1:
                    cambiarNombreInventario(sc, controladorTienda, idInventario);
                    break;
                case 2:
                    agregarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 3:
                    modificarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 4:
                    eliminarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 5:
                    buscarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 6:
                    moverProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 7:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionModificarInventario !=7);
    }


    private static void cambiarNombreInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -CAMBIAR NOMBRE INVENTARIO-
                Escribe el nombre nuevo que le pondras:
                """ + "---> ");
        String nombreNuevoInv = sc.nextLine();
        try {
            controladorTienda.cambiarNombreAUnInventario(idInventario, nombreNuevoInv);
            System.out.println("El Inventario de ID -" + idInventario + "- ahora se llama: " + nombreNuevoInv);
        } catch (InventarioNoEncontradoException e) {
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void agregarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -AGREGAR PRODUCTO-
                Escribe el nombre del producto nuevo:
                """ + "---> ");
        String nombre = sc.nextLine();
        System.out.print("""
                \nEscribe el valor de compra del producto nuevo:
                """ + "---> ");
        double valorC = leerDecimal(sc);
        System.out.print("""
                    \nEscribe el stock del producto nuevo:
                    """ + "--->");
        int stock = leerEntero(sc);
        System.out.print("""
                \nEscribe el Tipo del producto nuevo:
                """ + "---> ");
        String tipoLeido = sc.nextLine().trim().toUpperCase();
        TipoProducto tipoProducto;
        DatosTotalesProductoDTO datosProducto;
        try {
            try {
                tipoProducto = TipoProducto.valueOf(tipoLeido);
            } catch (IllegalArgumentException e){
                System.out.println("Tipo de Producto Invalido");
                return;
            }
            switch (tipoProducto){
                case ROPA:
                    System.out.println("Ingrese la talla (S, M, L, XL):");
                    String tallaString = sc.nextLine();
                    datosProducto = controladorTienda.registrarProductoRopa(idInventario, nombre, valorC, stock, tallaString);
                    System.out.println("Nuevo Producto:");
                    DatosTotalesProductoRopaDTO productoRopa = (DatosTotalesProductoRopaDTO) datosProducto;
                    String descripcionRopa = String.format(
                        "Tipo de Producto: Ropa  Nombre del Producto: %-12s Talla: %-4s Codigo: %-4d " +
                        "Valor Compra: %-12.2f Ganancia: %3.0f%% Valor Venta: %-12.2f Stock: %-4d%n",
                        productoRopa.nombre(), productoRopa.talla(),productoRopa.codigo(),
                        productoRopa.valorCompra(), productoRopa.porcentajeGanancia(), productoRopa.valorVentaBase(),
                        productoRopa.stock()
                    );
                    System.out.println(descripcionRopa);
                    break;
                case PERECEDERO:
                    System.out.println("Ingrese la fecha de vencimiento (Formato DD/MM/AAAA):");
                    LocalDate fecha = leerFecha(sc);
                    datosProducto = controladorTienda.registrarProductoPerecedero(idInventario, nombre, valorC, stock, fecha);
                    System.out.println("Nuevo Producto:");
                    DatosTotalesProductoPerecederoDTO productoPerecedero = (DatosTotalesProductoPerecederoDTO) datosProducto;
                    String descripcionPerecedero = String.format(
                        "Tipo de Producto:  Comida    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-4d " +
                        "Valor Compra:  %-12.2f Ganancia:  %3.0f%% Valor Venta:  %-12.2f Stock:  %-4d Estado: %-15s %n",
                        productoPerecedero.nombre(),productoPerecedero.fechaVencimiento(),productoPerecedero.codigo(),
                        productoPerecedero.valorCompra(),productoPerecedero.porcentajeGanancia(),productoPerecedero.valorVentaBase(),
                        productoPerecedero.stock(),productoPerecedero.estaVencido());
                    System.out.println(descripcionPerecedero);
                    break;
            }
        } catch (CapacidadExcedidaException | InventarioNoEncontradoException e) {
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void eliminarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -ELIMINAR PRODUCTO-
                Que Producto vas a eliminar, escribe su codigo:
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        try {
            controladorTienda.eliminarProductoAInventario(idInventario, codigoProducto);
            System.out.println("El Producto de codigo -" + codigoProducto + "- ha sido eliminado con exito");
        } catch (ProductoNoEncontradoException | InventarioNoEncontradoException e) {
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void buscarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -BUSCAR PRODUCTO-
                Que Producto vas a buscar, escribe su codigo:
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        try {
            boolean esta = controladorTienda.buscarProductoAInventario(idInventario, codigoProducto);
            if (!esta){
                System.out.println("El Producto de codigo -" + codigoProducto + "- NO esta en ese inventario");
                return;
            }
            System.out.println("El Producto de codigo -" + codigoProducto + "- SI esta en ese inventario");
        } catch (InventarioNoEncontradoException e) {
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void moverProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -MOVER PRODUCTO A OTRO INVENTARIO-
                Que Producto vas a mover, escribe su codigo:
                """ + "---> ");
        int codigoProducto = leerEntero(sc);
        System.out.println("\nA que Inventario lo vas a mover, escribe su numero identificador:\n---> ");
        int numeroId2 = leerEntero(sc);
        try {
            controladorTienda.moverProductoAInventario(idInventario, numeroId2 , codigoProducto);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ha sido movido al Inventario de ID -" + numeroId2 + "- con exito");
        } catch (CapacidadExcedidaException | InventarioNoEncontradoException | ProductoNoEncontradoException e) {
            System.out.println("Proceso Interrumpido\nERROR:  " + e.getMessage());
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


}

