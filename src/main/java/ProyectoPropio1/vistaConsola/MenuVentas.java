package ProyectoPropio1.vistaConsola;

import ProyectoPropio1.dto.*;
import ProyectoPropio1.servicios.controlador.ControladorTienda;

import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class MenuVentas {

    private static void menuVentas(){
        System.out.print("""
                ---> QUE DESEA EL CLIENTE:
                        * OPCION                 * ACCION
                            1.                PEDIR PRODUCTO
                            2.                PEDIR SERVICIO
                            3.           REDUCIR CANTIDAD PRODUCTO
                            4.           REDUCIR CANTIDAD SERVICIO
                            5.               ELIMINAR PRODUCTO
                            6.               ELIMINAR SERVICIO
                            7.                FINALIZAR VENTA
                            8.                CANCELAR COMPRA
                """ + "---> ");
    }


    public static void vender(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("""
                \nHAS SELECCIONADO -VENDER PRODUCTOS-
                """);
        String atenderCliente;
        boolean finalizar;
        int peticionMenuVentas;
        System.out.println("\nVAS A ATENDER UN CLIENTE");
        atenderCliente = peticionSiNo(sc);
        while (atenderCliente.equalsIgnoreCase("Si")){
            controladorTienda.abrirCarritoSesion();
            finalizar = false;
            while (!finalizar){
                menuVentas();
                peticionMenuVentas = pedirOpcion(sc, 1, 8);
                switch (peticionMenuVentas){
                    case 1:
                        pedirProducto(sc, controladorTienda);
                        break;
                    case 2:
                        pedirServicio(sc, controladorTienda);
                        break;
                    case 3:
                        reducirCantidadProducto(sc, controladorTienda);
                        break;
                    case 4:
                        reducirCantidadServicio(sc, controladorTienda);
                        break;
                    case 5:
                        eliminarProducto(sc, controladorTienda);
                        break;
                    case 6:
                        eliminarServicio(sc, controladorTienda);
                        break;
                    case 7:
                        finalizar = true;
                        finalizarVenta(controladorTienda);
                        break;
                    case 8:
                        finalizar = true;
                        cancelarCompra(sc, controladorTienda);
                        break;
                }

            }
            System.out.println("\nVAS A ATENDER OTRO CLIENTE");
            atenderCliente = peticionSiNo(sc);
        }
    }

    private static void mostrarVistaPreviaCarrito(ControladorTienda controladorTienda){
        VistaPreviaCarritoDTO vistaPreviaCarrito = controladorTienda.obtenerVistaPreviaCarrito();
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println("---> Carrito:");
        for (ItemCarritoDTO itemCarrito:vistaPreviaCarrito.carritoItems()){
            System.out.println("Item:  " + itemCarrito.nombreArticulo() + "\n" +
                    "Cantidad:  " + itemCarrito.cantidad() + "\n" +
                    "Precio:  $" + itemCarrito.precioUnitario() + "\n" +
                    "Subtotal:  $" + itemCarrito.subtotal());
            System.out.println("...................................................................");
        }
        System.out.println("\n---> Total a pagar:  $" + vistaPreviaCarrito.totalAproximado() + "\n");
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    private static void pedirProducto(Scanner sc, ControladorTienda controladorTienda){
        try{
            SolicitudItemDTO solicitudItem = capturarDatosDeCompraProducto(sc);
            controladorTienda.agregarProductoACarritoSesion(solicitudItem.idInventario(), solicitudItem.codigo(), solicitudItem.cantidad());
            System.out.println("Producto Agregado Con Exito Al Carrito");
            mostrarVistaPreviaCarrito(controladorTienda);
        } catch (RuntimeException e){
            System.out.println("NO se pudo completar la accion\nERROR:  " + e.getMessage());
            System.out.println("No se agrego este Producto, Pero el Carrito Sigue Intacto");
        }
    }

    private static SolicitudItemDTO capturarDatosDeCompraProducto(Scanner sc){
        System.out.println("Escribe los Datos del Producto a vender:");
        System.out.print("ID del Inventario del Producto:\n" +
                "---> ");
        int idInv = leerEntero(sc);
        System.out.print("Codigo del Producto:\n" +
                "---> ");
        String codigoProd = sc.nextLine();
        System.out.print("Cantidad a Vender:\n" +
                "---> ");
        int cantidadVender = leerEntero(sc);
        return new SolicitudItemDTO(idInv, codigoProd, cantidadVender);
    }


    private static void pedirServicio(Scanner sc, ControladorTienda controladorTienda){
        try {
            CatalogoServiciosDTO datosCatalogoServicios = controladorTienda.obtenerCatalogoServicios();
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.println("---> SERVICIOS:");
            for (ServicioDTO datosServicio: datosCatalogoServicios.listaServicios()){
                System.out.println("Codigo:  " + datosServicio.codigo() + "   Servicio:  " + datosServicio.nombre() + "   Precio:  $" + datosServicio.precioFinal());
            }
            System.out.println("--------------------------------------------------------------------------------------------");
            System.out.print("Codigo del Servicio:\n" +
                    "---> ");
            String codigoServicio = sc.nextLine();
            controladorTienda.agregarServicioACarritoSesion(codigoServicio);
            System.out.println("Servicio Agregado Con Exito Al Carrito");
            mostrarVistaPreviaCarrito(controladorTienda);
        } catch (RuntimeException e) {
            System.out.println("NO se pudo completar la accion\nERROR:  " + e.getMessage());
            System.out.println("No se agrego este Producto, Pero el Carrito Sigue Intacto");
        }
    }


    private static void reducirCantidadProducto(Scanner sc, ControladorTienda controladorTienda){
        try {
            System.out.print("""
                \nA que Producto le vas a Reducir la Cantidad
                Escribe el Codigo:
                """ + "---> ");
            String codigoProducto = sc.nextLine();
            System.out.print("""
                \nCuanta Cantidad le vas a Reducir
                Escribe la Cantidad:
                """ + "---> ");
            int cantidadAReducir = leerEntero(sc);
            controladorTienda.reducirCantidadProductoACarritoSesion(codigoProducto, cantidadAReducir);
            System.out.println("Se redujo la Cantidad con Exito");
            mostrarVistaPreviaCarrito(controladorTienda);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
            System.out.println("El Carrito sigue intacto");
        }
    }


    private static void reducirCantidadServicio(Scanner sc, ControladorTienda controladorTienda){
        try {
            System.out.print("""
                \nA que Servicio le vas a Reducir la Cantidad
                Escribe el Codigo:
                """ + "---> ");
            String codigoServicio = sc.nextLine();
            System.out.print("""
                \nCuanta Cantidad le vas a Reducir
                Escribe la Cantidad:
                """ + "---> ");
            int cantidadAReducir = leerEntero(sc);
            controladorTienda.reducirCantidadServicioACarritoSesion(codigoServicio, cantidadAReducir);
            System.out.println("Se redujo la Cantidad con Exito");
            mostrarVistaPreviaCarrito(controladorTienda);
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
            System.out.println("El Carrito sigue intacto");
        }
    }


    private static void eliminarProducto(Scanner sc, ControladorTienda controladorTienda){
        try {
            System.out.print("""
                \nQue Producto vas a Eliminar
                Escribe el Codigo:
                """ + "---> ");
            String codigoProducto = sc.nextLine();
            controladorTienda.eliminarProductoACarritoSesion(codigoProducto);
            System.out.println("Producto Eliminado del Carrito con Exito");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void eliminarServicio(Scanner sc, ControladorTienda controladorTienda){
        try {
            System.out.print("""
                \nQue Servicio vas a Eliminar
                Escribe el Codigo:
                """ + "---> ");
            String codigoServicio = sc.nextLine();
            controladorTienda.eliminarServicioACarritoSesion(codigoServicio);
            System.out.println("Servicio Eliminado del Carrito con Exito");
        } catch (RuntimeException e) {
            System.out.println("No se pudo completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void finalizarVenta(ControladorTienda controladorTienda){
        try {
            FacturaDTO datosFactura = controladorTienda.confirmarProcesarVentaActualYGenerarFactura();
            System.out.println("\nFactura Generada Exitosamente");
            mostrarFactura(datosFactura);
        } catch (RuntimeException e) {
            System.out.println("\nNo se pudo completar la accion\nERROR:  " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarFactura(FacturaDTO datosFactura){
        try {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("---> FACTURA -" + datosFactura.numeroFactura() + "-  FECHA Y HORA: " + datosFactura.fechaEmision());
            for (DatosItemVendidoFacturaDTO datosItemVendido:datosFactura.listaItemsFinales()){
                System.out.println("Tipo:  " + datosItemVendido.tipoItem() + "\n" +
                        "Codigo: " + datosItemVendido.codigoReferencia() + "\n" +
                        "Nombre:  " + datosItemVendido.nombreItem() + "\n" +
                        "Cantidad Vendida:  " + datosItemVendido.cantidad() + "\n" +
                        "Precio Por Unidad:  $" + datosItemVendido.precioUnitario() + "\n" +
                        "SubTotal Neto:  $" + datosItemVendido.subTotalNeto() + "\n" +
                        "Porcentaje Impuestos:  " + datosItemVendido.porcentajeImpuestos() + "%" + "\n" +
                        "Monto Impuestos:  $" + datosItemVendido.montoImpuestos() + "\n" +
                        "Valor A Pagar:  $" + datosItemVendido.totalLinea() );
                System.out.println("...........................................................................");
            }
            System.out.println("SUBTOTAL:  $" + datosFactura.subTotal());
            System.out.println("TOTAL IMPUESTOS:  $" + datosFactura.totalImpuestos());
            System.out.println("TOTAL A PAGAR:  $" + datosFactura.totalGeneral());
            System.out.println("-------------------------------------------------------------------------------------");
        } catch (RuntimeException e){
            System.out.println("NO se puede completar la accion\nERROR:  " + e.getMessage());
        }
    }

    private static void cancelarCompra(Scanner sc, ControladorTienda controladorTienda){
        System.out.print("\nEstas Seguro de que Quieres Cancelar la Compra");
        String peticion = peticionSiNo(sc);
        if (peticion.equalsIgnoreCase("NO")){
            System.out.println("Puedes seguir comprando");
            return;
        }
        controladorTienda.cancelarCompraTotal();
    }


}

