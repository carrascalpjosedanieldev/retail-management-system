package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.excepciones.ProductoNoEncontradoException;
import ProyectoPropio1.servicios.controlador.ControladorTienda;
import ProyectoPropio1.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuModificarProducto.modificarProductoAInventario;
import static ProyectoPropio1.vistaConsola.MenuGestionarImpuestos.impuestosActivosParaRegistro;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuModificarInventario {

    private static void menuModificarInventarioEspecifico(){
        System.out.print("""
                \n                      -MODIFICAR INVENTARIO ESPECIFICO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                         CAMBIAR NOMBRE INVENTARIO
                                   2                              AGREGAR PRODUCTO
                                   3                              ELIMINAR PRODUCTO
                                   4                               BUSCAR PRODUCTO
                                   5                       MOVER PRODUCTO A OTRO INVENTARIO
                                   6                             MODIFICAR PRODUCTO
                                   7                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }


    public static void modificarInventarioEspecifico(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -MODIFICAR INVENTARIO-");
        List<DatosInventarioDTO> detalleInventarioGeneral = controladorTienda.obtenerDatosInventarioGeneral();
        int opcionModificarInventario;
        System.out.println("""
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
                Elige el Inventario por su numero identificador:
                """ + "---> ");
        int idInventario = leerEntero(sc);
        do {
            menuModificarInventarioEspecifico();
            opcionModificarInventario = pedirOpcion(sc,1,7);
            switch (opcionModificarInventario){
                case 1:
                    cambiarNombreInventario(sc, controladorTienda, idInventario);
                    break;
                case 2:
                    agregarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 3:
                    eliminarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 4:
                    buscarProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 5:
                    moverProductoAInventario(sc, controladorTienda, idInventario);
                    break;
                case 6:
                    modificarProductoAInventario(sc, controladorTienda, idInventario);
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
            controladorTienda.cambiarNombreInventario(idInventario, nombreNuevoInv);
            System.out.println("El Inventario de ID -" + idInventario + "- ahora se llama: " + nombreNuevoInv);
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
        BigDecimal valorC = leerDecimal(sc);
        System.out.print("""
                \nEl Porcentaje de Ganancia es 20% por defecto, puedes cambiarlo en -Modificar Producto-
                """);
        BigDecimal porcentajeGanancia = BigDecimal.valueOf(20);
        System.out.print("""
                    \nEscribe el stock del producto nuevo:
                    """ + "---> ");
        int stock = leerEntero(sc);
        System.out.print("""
                \nEscribe el Tipo del producto nuevo:
                """ + "---> ");
        String tipoLeido = sc.nextLine().trim().toUpperCase();
        System.out.println();
        try {
            String impuestosActivosParaRegistro = impuestosActivosParaRegistro(controladorTienda);
            System.out.println(impuestosActivosParaRegistro);
            System.out.print("Escribe el ID del Impuesto que le corresponda: \n" +
                    "---> ");
            int idImpuesto = leerEntero(sc);
            System.out.print("""
                \nEl Descuento es -Sin Descuento- por defecto, puedes cambiarlo en -Modificar Producto-
                """);
            int idDescuento = 1;
            DatosTotalesProductoDTO datosProducto;
            String codigoProducto;
            switch (tipoLeido){
                case "ROPA":
                    System.out.print("""
                            \nIngrese la talla (S, M, L, XL):
                            """ + "---> ");
                    String tallaString = sc.nextLine().toUpperCase().trim();
                    codigoProducto = controladorTienda.registrarProductoRopa(idInventario, nombre, valorC, porcentajeGanancia,
                            stock,  idImpuesto, idDescuento, tallaString);
                    datosProducto = controladorTienda.obtenerDatosTotalesProducto(idInventario, codigoProducto);
                    System.out.println("\nNuevo Producto:");
                    DatosTotalesProductoRopaDTO productoRopa = (DatosTotalesProductoRopaDTO) datosProducto;
                    String descripcionRopa = String.format(
                        "Tipo de Producto: Ropa  Nombre del Producto: %-12s Talla: %-4s Codigo: %-40s " +
                        "Valor Compra: %-12.2f Ganancia: %3.0f%% Valor Venta: %-12.2f Stock: %-4d%n",
                        productoRopa.nombre(), productoRopa.talla(),productoRopa.codigo(),
                        productoRopa.valorCompra(), productoRopa.porcentajeGanancia(), productoRopa.valorVentaBase(),
                        productoRopa.stock()
                    );
                    System.out.println(descripcionRopa);
                    break;
                case "PERECEDERO":
                    System.out.print("""
                            \nIngrese la fecha de vencimiento (Formato DD/MM/AAAA):
                            """ + "---> ");
                    LocalDate fecha = leerFecha(sc);
                    codigoProducto = controladorTienda.registrarProductoPerecedero(idInventario, nombre, valorC,
                            porcentajeGanancia,  stock, idImpuesto, idDescuento, fecha);
                    datosProducto = controladorTienda.obtenerDatosTotalesProducto(idInventario, codigoProducto);
                    System.out.println("\nNuevo Producto:");
                    DatosTotalesProductoPerecederoDTO productoPerecedero = (DatosTotalesProductoPerecederoDTO) datosProducto;
                    String descripcionPerecedero = String.format(
                        "Tipo de Producto:  Comida    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-40s " +
                        "Valor Compra:  %-12.2f Ganancia:  %3.0f%% Valor Venta:  %-12.2f Stock:  %-4d Estado: %-15s %n",
                        productoPerecedero.nombre(),productoPerecedero.fechaVencimiento(),productoPerecedero.codigo(),
                        productoPerecedero.valorCompra(),productoPerecedero.porcentajeGanancia(),productoPerecedero.valorVentaBase(),
                        productoPerecedero.stock(),productoPerecedero.estaVencido());
                    System.out.println(descripcionPerecedero);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de Producto Invalido");
            }
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void eliminarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -ELIMINAR PRODUCTO-
                Que Producto vas a eliminar, escribe su codigo:
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            controladorTienda.eliminarProductoDeInventario(codigoProducto, idInventario);
            System.out.println("El Producto de codigo -" + codigoProducto + "- ha sido Desactivado con Exito");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void buscarProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario) {
        System.out.print("""
                \nHAS SELECCIONADO: -BUSCAR PRODUCTO-
                Que Producto vas a buscar, escribe su codigo:
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        try {
            DatosTotalesProductoDTO datosTotalesProducto = controladorTienda.obtenerDatosTotalesProducto(idInventario, codigoProducto);
            System.out.println("El Producto de codigo -" + codigoProducto + "- SI esta en ese inventario");
            if (datosTotalesProducto instanceof DatosTotalesProductoRopaDTO productoRopa) {
                String datosProducto = String.format(
                        "Tipo de Producto: Ropa  Nombre del Producto: %-12s Talla: %-4s Codigo: %-40s " +
                                "Valor Compra: %-12.2f Ganancia: %3.0f%% Valor Venta: %-12.2f Stock: %-4d%n",
                        productoRopa.nombre(), productoRopa.talla().toString(), productoRopa.codigo(),
                        productoRopa.valorCompra(), productoRopa.porcentajeGanancia(), productoRopa.valorVentaBase(),
                        productoRopa.stock()
                );
                System.out.println(datosProducto);
            } else if (datosTotalesProducto instanceof DatosTotalesProductoPerecederoDTO productoPerecedero) {
                String datosProducto = String.format(
                        "Tipo de Producto:  Perecedero    Nombre del Producto:  %-10s Fecha Vencimiento: %-12s Codigo:  %-40s " +
                                "Valor Compra:  %-12.2f Ganancia:  %3.0f%% Valor Venta:  %-12.2f Stock:  %-4d Estado: %-15s %n",
                        productoPerecedero.nombre(), productoPerecedero.fechaVencimiento().toString(), productoPerecedero.codigo(),
                        productoPerecedero.valorCompra(), productoPerecedero.porcentajeGanancia(), productoPerecedero.valorVentaBase(),
                        productoPerecedero.stock(), productoPerecedero.estaVencido()
                );
                System.out.println(datosProducto);
            } else {
                System.out.println("Tipo de Producto no se puede describir");
            }
        } catch (ProductoNoEncontradoException e) {
            System.out.println("El Producto de codigo -" + codigoProducto + "- NO esta en ese inventario");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


    private static void moverProductoAInventario(Scanner sc, ControladorTienda controladorTienda, int idInventario){
        System.out.print("""
                \nHAS SELECCIONADO: -MOVER PRODUCTO A OTRO INVENTARIO-
                Que Producto vas a mover, escribe su codigo:
                """ + "---> ");
        String codigoProducto = sc.nextLine();
        System.out.println("\nA que Inventario lo vas a mover, escribe su numero identificador:\n---> ");
        int numeroId2 = leerEntero(sc);
        try {
            controladorTienda.moverProductoAInventario(idInventario, numeroId2 , codigoProducto);
            System.out.println("El Producto de Codigo -" + codigoProducto + "- ha sido movido al Inventario de ID -" + numeroId2 + "- con exito");
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }


}

