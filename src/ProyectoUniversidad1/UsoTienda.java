package ProyectoUniversidad1;

import java.util.Scanner;

import static ProyectoUniversidad1.MetodosTienda.*;

public class UsoTienda {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Tienda miTienda = null;
        int leerOpcionPrincipal;
        boolean opcionPrincipalValida , opcionPrincipal=true;
        int contadorTiendas=0;
        int numeroId , codigo;
        int cantidad , atenderClientes , otroProducto , contadorClientes=0;
        double pagoProducto , pagoCliente , subtotalCliente=0 , totalClientes=0;

        //PARA PRUEBAS:

        ///*

        miTienda = new Tienda("SLIM");
        contadorTiendas++;

        miTienda.agregarInventario("Principal",400);

        miTienda.agregarProductoAUnInv(0,"Camisa",50000,80);
        miTienda.actValVenPreInvPro(0,1,75000);

        miTienda.agregarProductoAUnInv(0,"Pantalon",75000,75);
        miTienda.actValVenPreInvPro(0,2,105000);

        miTienda.agregarProductoAUnInv(0,"Medias",8000,100);
        miTienda.actValVenPorInvPro(0,3,100);

        miTienda.agregarProductoAUnInv(0,"Chaqueta",120000,50);
        miTienda.actValVenPreInvPro(0,4,165000);

        miTienda.agregarProductoAUnInv(0,"Vestido",65000,50);
        miTienda.actValVenPreInvPro(0,5,100000);

        miTienda.agregarInventario("Secundario",250);

        miTienda.agregarProductoAUnInv(1,"Pantaloneta",35000,50);
        miTienda.actValVenPreInvPro(1,6,55000);

        miTienda.agregarProductoAUnInv(1,"Boxer",25000,50);
        miTienda.actValVenPorInvPro(1,7,100);

        //*/

        do {


            menuPrincipal();
            do {
                opcionPrincipalValida =true;
                leerOpcionPrincipal = leerEntero(sc);
                if (leerOpcionPrincipal<1 || leerOpcionPrincipal>4){
                    System.out.print("""
                            \n---> ACCION DENEGADA:
                            Recuerda seleccionar una de las opciones disponibles
                            """ + "---> ");
                    opcionPrincipalValida =false;
                }
            } while (!opcionPrincipalValida);


            switch (leerOpcionPrincipal){

                case 1:


                    if (contadorTiendas==0){
                        System.out.print("""
                                \nHAS SELECCIONADO: -CREAR TIENDA-
                                Escribe el nombre que le pondras a tu tienda:
                                """ + "---> ");
                        String nombreTienda;
                        nombreTienda=sc.nextLine();
                        try {
                            miTienda = new Tienda(nombreTienda);
                            System.out.println("\nGENERACION DE TIENDA EXITOSA:\n" +
                                    "La Tienda: -" + miTienda.getNombreTienda() + "- esta lista para generar su Inventario");
                            contadorTiendas++;
                        } catch (IllegalArgumentException asignacionInvalida) {
                            System.out.println("\nNO se puede generar esta Tienda por un error de asignacion de datos:\n" +
                                    "ERROR: " + asignacionInvalida.getMessage());
                        }
                    } else {
                        System.out.print("""
                                \nACCION DENEGADA:
                                YA HAS CREADO UNA TIENDA Y EL PROGRAMA SOLO ADMITE EL MANEJO DE UNA SOLA
                                """);
                    }
                    break;


                case 2:


                    if (miTienda!=null){
                        System.out.println("\nHAS SELECCIONADO: -MODIFICAR TIENDA-");
                        int leerModificarTienda;
                        boolean opcionesModificarTienda;


                        do {
                            menuModificarTienda();
                            do {
                                opcionesModificarTienda =true;
                                leerModificarTienda = leerEntero(sc);
                                if (leerModificarTienda<1 || leerModificarTienda>7){
                                    System.out.print("""
                                            \n---> ACCION DENEGADA:
                                            Recuerda seleccionar una de las opciones disponibles
                                            """ + "---> ");
                                    opcionesModificarTienda =false;
                                }
                            } while (!opcionesModificarTienda);


                            switch (leerModificarTienda){
                                case 1:


                                    System.out.print("\nHAS SELECCIONADO: -CAMBIAR NOMBRE TIENDA-\n" +
                                            "La Tienda actualmente se llama -" + miTienda.getNombreTienda() + "\n" +
                                            "Escribe el nombre nuevo que le quieres colocar:\n" +
                                            "---> ");
                                    String nuevoNombreTienda = sc.nextLine();
                                    miTienda.cambiarNombreTienda(nuevoNombreTienda);
                                    break;


                                case 2:


                                    System.out.print("""
                                                \nHAS SELECCIONADO: -AGREGAR INVENTARIO-
                                                Escribe el Nombre del Inventario:
                                                """ + "---> ");
                                    String nombreInventario = sc.nextLine();
                                    System.out.print("""
                                                \nEscribe la Capacidad del inventario:
                                                """ + "---> ");
                                    int capacidadInventario = leerEntero(sc);
                                    miTienda.agregarInventario(nombreInventario,capacidadInventario);
                                    break;


                                case 3:


                                    if (!miTienda.tieneInventarios()){
                                        System.out.println("""
                                            \nHAS SELECCIONADO: -VER INVENTARIO-
                                            ---> NO HAY INVENTARIOS AUN
                                            """);
                                    } else {
                                        System.out.println("""
                                            \nHAS SELECCIONADO: -VER INVENTARIO-
                                            ---> INVENTARIOS:
                                            ------------------------------------------------------------------------------------
                                            """);
                                        miTienda.mostrarInventarios();
                                        System.out.print("""
                                            \n------------------------------------------------------------------------------------
                                            Elige el Inventario por su numero identificador:
                                            """ + "---> ");
                                        numeroId = leerEntero(sc);
                                        numeroId--;
                                        miTienda.mostrarUnInventario(numeroId);
                                    }
                                    break;


                                case 4:


                                    System.out.println("\nHAS SELECCIONADO: -MODIFICAR INVENTARIO-");

                                    if (!miTienda.tieneInventarios()){
                                        System.out.println("""
                                                \nACCION DENEGADA
                                                TODAVIA NO HAY INVENTARIO
                                                """);
                                    } else {

                                        int leerModificarInventario;
                                        boolean opcionesModificarInventario;
                                        System.out.println("""
                                                \n---> INVENTARIOS:
                                                ------------------------------------------------------------------------------------
                                                """);
                                        miTienda.mostrarInventarios();
                                        System.out.print("""
                                                \n------------------------------------------------------------------------------------
                                                Elige el Inventario por su numero identificador:
                                                """ + "---> ");
                                        numeroId = leerEntero(sc);
                                        numeroId--;

                                        do {

                                            menuModificarInventario();
                                            do {
                                                opcionesModificarInventario =true;
                                                leerModificarInventario = leerEntero(sc);
                                                if (leerModificarInventario<1 || leerModificarInventario>7){
                                                    System.out.print("""
                                                            \n---> ACCION DENEGADA:
                                                            Recuerda seleccionar una de las opciones disponibles
                                                            """ + "---> ");
                                                    opcionesModificarInventario =false;
                                                }
                                            } while (!opcionesModificarInventario);


                                            switch (leerModificarInventario){
                                                case 1:


                                                    System.out.print("""
                                                            \nHAS SELECCIONADO: -CAMBIAR NOMBRE INVENTARIO-
                                                            Escribe el nombre nuevo que le pondras:
                                                            """ + "---> ");
                                                    String nombreNuevoInv = sc.nextLine();
                                                    miTienda.cambiarNombreAUnInventario(numeroId,nombreNuevoInv);
                                                    break;


                                                case 2:


                                                    System.out.print("""
                                                            \nHAS SELECCIONADO: -AGREGAR PRODUCTO-
                                                            Escribe el nombre del producto nuevo:
                                                            """ + "---> ");
                                                    String nombre = sc.nextLine();
                                                    System.out.print("""
                                                            \nEscribe el valor de compra del producto nuevo:
                                                            """ + "---> ");
                                                    double valorC = leerDecimal(sc);
                                                    String tieneStock;
                                                    do {
                                                        System.out.print("""
                                                                \nTiene Stock? (SI / NO):
                                                                """ + "---> ");
                                                        tieneStock = sc.nextLine();
                                                    } while (!tieneStock.equalsIgnoreCase("si") && !tieneStock.equalsIgnoreCase("no"));

                                                    if (tieneStock.equalsIgnoreCase("si")){
                                                        System.out.print("""
                                                                \nEscribe el stock del producto nuevo:
                                                                """ + "--->");
                                                        int stock = leerEntero(sc);
                                                        miTienda.agregarProductoAUnInv(numeroId,nombre,valorC,stock);
                                                        System.out.println();
                                                    } else if (tieneStock.equalsIgnoreCase("no")){
                                                        System.out.println();
                                                        miTienda.agregarProductoAUnInv(numeroId,nombre,valorC);
                                                        System.out.println();
                                                    }
                                                    break;


                                                case 3:


                                                    if (miTienda.inventarioTieneProductos(numeroId)){
                                                        System.out.println("\nHAS SELECCIONADO: -MODIFICAR PRODUCTO-");
                                                        int leerModificarProducto;
                                                        boolean opcionesModificarProducto;

                                                        do {

                                                            menuModificarProducto();
                                                            do {
                                                                opcionesModificarProducto =true;
                                                                leerModificarProducto = leerEntero(sc);
                                                                if (leerModificarProducto<1 || leerModificarProducto>6){
                                                                    System.out.print("""
                                                                        \n---> ACCION DENEGADA:
                                                                        Recuerda seleccionar una de las opciones disponibles
                                                                        """ + "---> ");
                                                                    opcionesModificarProducto =false;
                                                                }
                                                            } while (!opcionesModificarProducto);

                                                            switch (leerModificarProducto){
                                                                case 1:


                                                                    System.out.print("""
                                                                        \nHAS SELECCIONADO: -CAMBIAR NOMBRE PRODUCTO-
                                                                        A que producto le cambiaras el Nombre, escribe el codigo
                                                                        """ + "---> ");
                                                                    codigo = leerEntero(sc);
                                                                    System.out.print("Escribe el nombre nuevo que le pondras:\n" +
                                                                            "---> ");
                                                                    String nombreNuevoProd = sc.nextLine();
                                                                    miTienda.actNomProdInv(numeroId,codigo,nombreNuevoProd);
                                                                    break;


                                                                case 2:


                                                                    System.out.print("""
                                                                        \nHAS SELECCIONADO: -ACTUALIZAR VALOR VENTA PRODUCTO-
                                                                        A que producto le cambiaras el Valor de Venta, Escribe el Codigo
                                                                        """ + "---> ");
                                                                    codigo = leerEntero(sc);
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
                                                                    if (precioOProcentaje==1){
                                                                        System.out.print("""
                                                                            \nEscribe el precio nuevo que le pondras:
                                                                            """ + "---> ");
                                                                        double precio = leerDecimal(sc);
                                                                        miTienda.actValVenPreInvPro(numeroId,codigo,precio);
                                                                    } else {
                                                                        System.out.print("""
                                                                            \nEscribe el porcentaje de ganancia nuevo que le pondras:
                                                                            """ + "---> ");
                                                                        double porcentaje = leerDecimal(sc);
                                                                        miTienda.actValVenPorInvPro(numeroId,codigo,porcentaje);
                                                                    }
                                                                    break;


                                                                case 3:


                                                                    System.out.print("""
                                                                        \nHAS SELECCIONADO: -ACTUALIZAR VALOR COMPRA PRODUCTO-
                                                                        A que producto le cambiaras el Valor de Compra, Escribe el Codigo
                                                                        """ + "---> ");
                                                                    codigo = leerEntero(sc);
                                                                    System.out.print("""
                                                                        \nEscribe el valor nuevo que le pondras al producto:
                                                                        """ + "---> ");
                                                                    double valorNuevo = leerDecimal(sc);
                                                                    miTienda.actValComProdInv(numeroId,codigo,valorNuevo);
                                                                    break;


                                                                case 4:


                                                                    System.out.print("""
                                                                        \nHAS SELECCIONADO: -ACTUALIZAR STOCK PRODUCTO-
                                                                        A que producto le cambiaras el Stock, Escribe el Codigo
                                                                        """ + "---> ");
                                                                    codigo = leerEntero(sc);
                                                                    System.out.print("""
                                                                        \nEscribe las unidades nuevas que llegaron:
                                                                        """ + "---> ");
                                                                    cantidad = leerEntero(sc);
                                                                    miTienda.actStockProdInv(numeroId,codigo,cantidad);
                                                                    break;


                                                                case 5:

                                                                    System.out.print("""
                                                                        \nHAS SELECCIONADO: -REDUCIR STOCK PRODUCTO-
                                                                        A que producto le cambiaras el Stock, Escribe el Codigo
                                                                        """ + "---> ");
                                                                    codigo = leerEntero(sc);
                                                                    System.out.print("""
                                                                        \nEscribe las unidades eliminadas:
                                                                        """ + "---> ");
                                                                    int cantidadQuitada = leerEntero(sc);
                                                                    miTienda.redStockProdInv(numeroId,codigo,cantidadQuitada);
                                                                    break;


                                                                case 6:
                                                                    break;
                                                            }


                                                        } while (leerModificarProducto!=6);

                                                    } else {
                                                        System.out.println("""
                                                                \nACCION DENEGADA
                                                                EL INVENTARIO ESTA VACIO
                                                                NO PUEDES MODIFICAR NADA
                                                                """);
                                                    }
                                                    break;


                                                case 4:


                                                    if (miTienda.inventarioTieneProductos(numeroId)){
                                                        System.out.print("""
                                                            \nHAS SELECCIONADO: -ELIMINAR PRODUCTO-
                                                            Que Producto vas a eliminar, escribe su codigo:
                                                            """ + "---> ");
                                                        codigo = leerEntero(sc);
                                                        miTienda.eliminarProductoAUnInv(numeroId,codigo);
                                                    } else {
                                                        System.out.println("""
                                                                \nACCION DENEGADA
                                                                EL INVENTARIO ESTA VACIO
                                                                NO PUEDES MODIFICAR NADA
                                                                """);
                                                    }
                                                    break;


                                                case 5:


                                                    if (miTienda.inventarioTieneProductos(numeroId)){
                                                        System.out.print("""
                                                            \nHAS SELECCIONADO: -BUSCAR PRODUCTO-
                                                            Que Producto vas a buscar, escribe su codigo:
                                                            """ + "---> ");
                                                        codigo = leerEntero(sc);
                                                        miTienda.buscarProductoAUnInv(numeroId,codigo);
                                                    } else {
                                                        System.out.println("""
                                                                \nACCION DENEGADA
                                                                EL INVENTARIO ESTA VACIO
                                                                NO PUEDES MODIFICAR NADA
                                                                """);
                                                    }
                                                    break;


                                                case 6:


                                                    if (miTienda.inventarioTieneProductos(numeroId)){
                                                        System.out.print("""
                                                            \nHAS SELECCIONADO: -MOVER PRODUCTO A OTRO INVENTARIO-
                                                            Que Producto vas a mover, escribe su codigo:
                                                            """ + "---> ");
                                                        codigo = leerEntero(sc);
                                                        System.out.println("\nA que Inventario lo vas a mover, escribe su numero identificador:\n---> ");
                                                        int numeroId2 = leerEntero(sc);
                                                        numeroId2--;
                                                        miTienda.moverProductoAOtroInventario(numeroId,numeroId2,codigo);
                                                    } else {
                                                        System.out.println("""
                                                                \nACCION DENEGADA
                                                                EL INVENTARIO ESTA VACIO
                                                                NO PUEDES MODIFICAR NADA
                                                                """);
                                                    }
                                                    break;


                                                case 7:
                                                    break;
                                            }

                                        }while (leerModificarInventario!=7);

                                    }
                                    break;


                                case 5:


                                    if (miTienda.tieneInventarios()){
                                        System.out.println("""
                                            \nHAS SELECCIONADO: -VER STOCK INVENTARIO-
                                            ---> INVENTARIOS:
                                            ------------------------------------------------------------------------------------
                                            """);
                                        miTienda.mostrarInventarios();
                                        System.out.print("""
                                            \n------------------------------------------------------------------------------------
                                            Elige el Inventario por su numero identificador:
                                            """ + "---> ");
                                        numeroId = leerEntero(sc);
                                        numeroId--;
                                        if (miTienda.inventarioTieneProductos(numeroId)){
                                            miTienda.mostrarStockUnInventario(numeroId);
                                        } else {
                                            System.out.println("""
                                                                \nACCION DENEGADA
                                                                EL INVENTARIO ESTA VACIO
                                                                """);
                                        }
                                    } else {
                                        System.out.println("""
                                                                \nACCION DENEGADA
                                                                NO HAY INVENTARIOS
                                                                """);
                                    }
                                    break;


                                case 6:


                                    if (!miTienda.tieneInventarios()){
                                        System.out.println("""
                                            \nHAS SELECCIONADO: -ELIMINAR INVENTARIO VACIO-
                                            ---> NO HAY INVENTARIOS AUN
                                            """);
                                    } else {
                                        System.out.println("""
                                            \nHAS SELECCIONADO: -ELIMINAR INVENTARIO VACIO-
                                            \n---> INVENTARIOS:
                                            ------------------------------------------------------------------------------------
                                            """);
                                        miTienda.mostrarInventarios();
                                        System.out.print("""
                                                \n------------------------------------------------------------------------------------
                                                Elige el Inventario que vas a Eliminar por su numero identificador:
                                                """ + "---> ");
                                        numeroId = leerEntero(sc);
                                        numeroId--;
                                        miTienda.eliminarInventarioVacio(numeroId);
                                    }
                                    break;


                                case 7:
                                    break;
                            }

                        } while (leerModificarTienda!=7);

                    } else {
                        System.out.println("""
                                \nACCION DENEGADA
                                TODAVIA NO HAY TIENDA
                                """);
                    }
                    break;


                case 3:


                    if (miTienda==null || !miTienda.tieneInventarios()){
                        System.out.println("""
                                        \nACCION DENEGADA
                                        NO PUEDES REALIZAR ESTA ACCION
                                        """);
                    } else {
                        System.out.println("\nHAS SELECCIONADO: -UTILIZAR TIENDA-");
                        int leerUtilizarTienda;
                        boolean opcionesUtilizarTienda;

                        do {

                            menuUtilizarTienda();
                            do {
                                opcionesUtilizarTienda =true;
                                leerUtilizarTienda = leerEntero(sc);
                                if (leerUtilizarTienda<1 || leerUtilizarTienda>4){
                                    System.out.print("""
                                        \n---> ACCION DENEGADA:
                                        Recuerda seleccionar una de las opciones disponibles
                                        """ + "---> ");
                                    opcionesUtilizarTienda =false;
                                }
                            } while (!opcionesUtilizarTienda);

                            switch (leerUtilizarTienda){
                                case 1:


                                    System.out.println("""
                                        \nHAS SELECCIONADO -VER TODOS LOS PRODUCTOS-
                                        """);
                                    miTienda.mostrarInventarioGeneral();
                                    break;


                                case 2:


                                    System.out.print("""
                                        \nHAS SELECCIONADO -VENDER PRODUCTOS-
                                        """);

                                    int salirVenderProductos,contVentas=0;

                                    do {

                                        System.out.print("""
                                            \nESCRIBE 1 PARA ATENDER UN CLIENTE
                                            """ + "---> ");
                                        atenderClientes = leerEntero(sc);

                                        while (atenderClientes==1){

                                            System.out.print("""
                                                \nESCRIBE 1 PARA VENDER UN PRODUCTO
                                                """ + "---> ");
                                            otroProducto = leerEntero(sc);

                                            pagoProducto=0;
                                            pagoCliente=0;
                                            contVentas=0;
                                            contadorClientes++;

                                            while (otroProducto == 1){

                                                miTienda.mostrarInventarioGeneral();
                                                System.out.print("""
                                                    \nQue Producto vas a Vender, Codigo:
                                                    """ + "---> ");
                                                codigo = leerEntero(sc);

                                                do {
                                                    System.out.print("""
                                                    \nCuantos vas a Vender, la Cantidad debe ser Positiva:
                                                    Escribe 0 para cancelar la Operacion
                                                    """ + "---> ");
                                                    cantidad = leerEntero(sc);
                                                } while (cantidad<0);

                                                if (cantidad > 0) {
                                                    pagoProducto = miTienda.venderProducto(codigo,cantidad);
                                                    pagoCliente+=pagoProducto;
                                                    subtotalCliente=pagoCliente;
                                                    contVentas++;
                                                } else {
                                                    System.out.println("""
                                                    \nHAS CANCELADO LA COMPRA DE ESTE PRODUCTO
                                                    PUEDES CONTINUAR
                                                    """);
                                                }

                                                if (pagoProducto==0 && cantidad!=0){
                                                    System.out.println("""
                                                    \nACCION DENEGADA
                                                    NO se encuentra ese Producto o NO Existe
                                                    """);
                                                }

                                                if (contVentas>0){
                                                    System.out.println("\n---> SUBTOTAL:\n" +
                                                            "---> $" + subtotalCliente );
                                                }

                                                System.out.print("""
                                                    \nESCRIBE 1 PARA VENDER OTRO PRODUCTO
                                                    """ + "---> ");
                                                otroProducto = leerEntero(sc);

                                            }

                                            if (contVentas>0){
                                                System.out.println("\n--------------------------------------" +
                                                        "\n---> TOTAL CLIENTE:\n" +
                                                        "---> $" + pagoCliente + "\n" +
                                                        "--------------------------------------");
                                                totalClientes+=pagoCliente;
                                            }

                                            System.out.print("""
                                                \nESCRIBE 1 PARA ATENDER OTRO CLIENTE
                                                """ + "---> ");
                                            atenderClientes = leerEntero(sc);

                                        }

                                        System.out.print("""
                                            \nESCRIBE 1 PARA SALIR
                                            """ + "---> ");
                                        salirVenderProductos = leerEntero(sc);

                                    } while (salirVenderProductos!=1);

                                    break;


                                case 3:

                                    System.out.println("\nHAS SELECCIONADO -VER RECAUDO A CLIENTES- \n" +
                                            "---> CLIENTES ATENDIDOS: \n" +
                                            "---> " + contadorClientes + "\n" +
                                            "---> TOTAL RECAUDADO: \n" +
                                            "---> $" + totalClientes);
                                    break;


                                case 4:
                                    break;
                            }

                        } while (leerUtilizarTienda!=4);

                    }
                    break;


                case 4:
                    break;
            }

            if (leerOpcionPrincipal==4){
                System.out.println("""
                        \nGUARDANDO ...
                        HAS SALIDO DEL SISTEMA CON EXITO
                        """);
                opcionPrincipal=false;
            }

        } while (opcionPrincipal);

    }

}
