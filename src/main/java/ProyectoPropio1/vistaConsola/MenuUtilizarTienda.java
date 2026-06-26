package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.servicios.ControladorTienda;
import ProyectoPropio1.dto.SolicitudItemDTO;
import ProyectoPropio1.dto.*;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.leerEntero;
import static ProyectoPropio1.vistaConsola.MetodosTienda.pedirOpcion;

public class MenuUtilizarTienda {

    private static void menuUtilizarTienda(){
        System.out.print("""
                \n                        BIENVENIDO -MENU UTILIZAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                              VENDER PRODUCTOS
                                   2                           VER RECAUDO A CLIENTES
                                   3                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void utilizarTienda(Scanner sc, ControladorTienda controladorTienda){
        System.out.println("\nHAS SELECCIONADO: -UTILIZAR TIENDA-");
        int opcionUtilizarTienda;
        do {
            menuUtilizarTienda();
            opcionUtilizarTienda = pedirOpcion(sc,1,3);
            switch (opcionUtilizarTienda){
                case 1:
                    venderProductoDeInventario(sc, controladorTienda);
                    break;
                case 2:
                    verRecaudoClientes(controladorTienda);
                    break;
                case 3:
                    System.out.println("\nSALIENDO . . .");
                    break;
            }
        } while (opcionUtilizarTienda!=3);
    }

    private static void venderProductoDeInventario(Scanner sc, ControladorTienda controladorTienda){
        if (controladorTienda.ningunInventarioTieneProductos()){
            System.out.println("""
                    NO HAY PRODUCTOS DISPONIBLES PARA VENDER
                    """);
            return;
        }
        System.out.print("""
                \nHAS SELECCIONADO -VENDER PRODUCTOS-
                """);
        String atenderCliente , pedirProducto, pedirServicio;
        System.out.println("\nVAS A ATENDER UN CLIENTE");
        atenderCliente = peticionSiNo(sc);
        while (atenderCliente.equalsIgnoreCase("Si")){
            controladorTienda.abrirCarritoSesion();
            System.out.println("\nVAS A PEDIR UN PRODUCTO");
            pedirProducto = peticionSiNo(sc);
            while (pedirProducto.equalsIgnoreCase("SI")){
                try{
                    SolicitudItemDTO solicitudItem = capturarDatosDeCompra(sc);
                    controladorTienda.agregarItemASesion(solicitudItem.idInventario(), solicitudItem.codigoProducto(), solicitudItem.cantidad());
                    System.out.println("Producto Agregado Con Exito Al Carrito");
                    VistaPreviaCarritoDTO vistaPreviaCarrito = controladorTienda.obtenerVistaPreviaCarrito();
                    System.out.println("--------------------------------------------------------------------------------------------");
                    System.out.println("---> Carrito:");
                    for (ItemCarritoDTO itemCarrito:vistaPreviaCarrito.items()){
                        System.out.println("Producto:  " + itemCarrito.nombreArticulo() + "\n" +
                                "Cantidad:  " + itemCarrito.cantidad() + "\n" +
                                "Precio:  $" + itemCarrito.precioUnitario() + "\n" +
                                "Subtotal:  $" + itemCarrito.subtotal());
                        System.out.println("...................................................................");
                    }
                    System.out.println("\n---> Total a pagar:  $" + vistaPreviaCarrito.totalAproximado() + "\n");
                    System.out.println("--------------------------------------------------------------------------------------------");
                } catch (RuntimeException e){
                    System.out.println("NO se pudo completar la accion\nERROR:  " + e.getMessage());
                    System.out.println("No se agrego este Producto, Pero el Carrito Sigue Intacto");
                }
                System.out.println("VAS A PEDIR OTRO PRODUCTO");
                pedirProducto = peticionSiNo(sc);
            }
            if (controladorTienda.tiendaNoTieneServicios()){
                System.out.println("\nNO HAY SERVICIOS DISPONIBLES EN LA TIENDA. GENERAREMOS TU FACTURA");
            } else {
                System.out.println("\nVAS A PEDIR UN SERVICIO");
                pedirServicio = peticionSiNo(sc);
                while (pedirServicio.equalsIgnoreCase("SI")){
                    try {
                        DatosCatalogoServiciosDTO datosCatalogoServicios = controladorTienda.obtenerCatalogoServicios();
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("---> SERVICIOS:");
                        for (DatosServicioDTO datosServicio: datosCatalogoServicios.listaServicios()){
                            System.out.println("Codigo:  " + datosServicio.codigo() + "   Servicio:  " + datosServicio.nombre() + "   Precio:  $" + datosServicio.precioFinal());
                        }
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.print("Codigo del Servicio:\n" +
                                "---> ");
                        int codigoServicio = leerEntero(sc);
                        controladorTienda.agregarServicioAlCarrito(codigoServicio);
                        System.out.println("Servicio agregado con exito, estos son los servicios que tienes:");
                        VistaPreviaCarritoDTO vistaPreviaCarrito = controladorTienda.obtenerVistaPreviaCarrito();
                        System.out.println("--------------------------------------------------------------------------------------------");
                        System.out.println("---> Carrito:");
                        for (ItemCarritoDTO itemCarrito:vistaPreviaCarrito.items()){
                            System.out.println("Producto:  " + itemCarrito.nombreArticulo() + "\n" +
                                    "Cantidad:  " + itemCarrito.cantidad() + "\n" +
                                    "Precio:  $" + itemCarrito.precioUnitario() + "\n" +
                                    "Subtotal:  $" + itemCarrito.subtotal());
                            System.out.println("...................................................................");
                        }
                        for (DatosServicioDTO datosServicios:vistaPreviaCarrito.servicios()){
                            System.out.println("Servicio:  " + datosServicios.nombre() + "\n" +
                                    "Precio:  $" + datosServicios.precioFinal());
                            System.out.println("...................................................................");
                        }
                        System.out.println("\n---> Total a pagar:  $" + vistaPreviaCarrito.totalAproximado() + "\n");
                        System.out.println("--------------------------------------------------------------------------------------------");
                    } catch (RuntimeException e){
                        System.out.println("NO se pudo completar la accion\nERROR:  " + e.getMessage());
                        System.out.println("No se agrego este Servicio, Pero el Carrito Sigue Intacto");
                    }
                    System.out.println("VAS A PEDIR OTRO SERVICIO");
                    pedirServicio = peticionSiNo(sc);
                }
            }
            try {
                int idFactura = controladorTienda.confirmarYProcesarVentaActual();
                FacturaDTO datosFactura = controladorTienda.obtenerDatosFactura(idFactura);
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("---> FACTURA N`" + datosFactura.idFactura());
                for (DatosLineaFacturaDTO datosLineaFactura:datosFactura.listaItemsFinales()){
                    System.out.println("Tipo:  " + datosLineaFactura.tipoItem() + "\n" +
                        "Nombre:  " + datosLineaFactura.nombreItem() + "\n" +
                        "Cantidad Vendida:  " + datosLineaFactura.cantidad() + "\n" +
                        "Valor A Pagar:  $" + datosLineaFactura.valorTotal() );
                    System.out.println("......................................................................");
                }
                System.out.println("TOTAL A PAGAR:  $" + datosFactura.pagoTotal());
                System.out.println("--------------------------------------------------------------------------");
            } catch (RuntimeException e){
                System.out.println("NO se puede completar la accion\nERROR:  " + e.getMessage());
            }
            System.out.println("\nVAS A ATENDER OTRO CLIENTE");
            atenderCliente = peticionSiNo(sc);
        }
    }

    private static String peticionSiNo(Scanner sc){
        String peticion;
        do {
            System.out.println("(SI / NO):");
            peticion = sc.nextLine();
        } while (!peticion.equalsIgnoreCase("SI") && !peticion.equalsIgnoreCase("NO"));
        return peticion;
    }

    private static SolicitudItemDTO capturarDatosDeCompra(Scanner sc){
        System.out.println("Escribe los Datos del Producto a vender:");
        System.out.print("ID del Inventario del Producto:\n" +
                "---> ");
        int idInv = leerEntero(sc);
        System.out.print("Codigo del Producto:\n" +
                "---> ");
        int codigoProd = leerEntero(sc);
        System.out.print("Cantidad a vender:\n" +
                "---> ");
        int cantidadVender = leerEntero(sc);
        return new SolicitudItemDTO(idInv, codigoProd, cantidadVender);
    }

    private static void verRecaudoClientes(ControladorTienda controladorTienda){
        if (controladorTienda.registroVentasEstaVacio()){
            System.out.println("""
                    NO HAY VENTAS REGISTRADAS
                    NO HAY DINERO RECAUDADO
                    """);
            return;
        }
        System.out.print("""
                \nHAS SELECCIONADO -VER RECAUDO CLIENTES-
                """);
        try {
            HistorialVentasDTO historialVentas = controladorTienda.obtenerHistorialVentas();
            for (FacturaDTO datosFactura : historialVentas.facturasRegistradas()){
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("---> FACTURA N`" + datosFactura.idFactura());
                for (DatosLineaFacturaDTO datosLineaFactura:datosFactura.listaItemsFinales()){
                    System.out.println("Tipo:  " + datosLineaFactura.tipoItem() + "\n" +
                            "Nombre:  " + datosLineaFactura.nombreItem() + "\n" +
                            "Cantidad Vendida:  " + datosLineaFactura.cantidad() + "\n" +
                            "Valor A Pagar:  $" + datosLineaFactura.valorTotal() );
                    System.out.println("......................................................................");
                }
                System.out.println("TOTAL A PAGAR:  $" + datosFactura.pagoTotal());
                System.out.println("--------------------------------------------------------------------------");
            }
            System.out.println("\n---> TOTAL DE DINERO RECAUDADO A LOS CLIENTES:\n");
            System.out.println("  $" + historialVentas.recaudoTotal());
        } catch (RuntimeException e){
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

}

