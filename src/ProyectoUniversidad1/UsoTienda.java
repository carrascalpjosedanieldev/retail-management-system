package ProyectoUniversidad1;

import java.util.Scanner;

import static ProyectoUniversidad1.MenuModificarTienda.*;
import static ProyectoUniversidad1.MenuUtilizarTienda.utilizarTienda;
import static ProyectoUniversidad1.MetodosTienda.*;

public class UsoTienda {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ControladorTienda miControladorTienda = new ControladorTienda();

        //Comentable para pruebas
        miControladorTienda = getControladorTienda();

        int opcionMenuPrincipal;

        do {

            menuPrincipal();
            opcionMenuPrincipal = pedirOpcion(sc,1,4);

            switch (opcionMenuPrincipal){

                case 1:

                    crearTienda(sc,miControladorTienda);
                    break;

                case 2:

                    modificarTienda(sc, miControladorTienda);
                    break;

                case 3:

                    utilizarTienda(sc, miControladorTienda);
                    break;

                case 4:
                    System.out.println("""
                        \nGUARDANDO ...
                        HAS SALIDO DEL SISTEMA CON EXITO
                        """);
                    break;
            }

        } while (opcionMenuPrincipal!=4);

    }

    private static ControladorTienda getControladorTienda() {
        ControladorTienda miControladorTienda = new ControladorTienda();

        miControladorTienda.crearTienda("SLIM");

        miControladorTienda.agregarInventarioATienda("Principal", 400);

        miControladorTienda.agregarPorductoAInventario(1, "Camisa", 50000, 80);
        miControladorTienda.actualizarValorVentaPorPrecioDeProductoDeInventario(1, 1, 75000);

        miControladorTienda.agregarPorductoAInventario(1, "Pantalon", 75000, 75);
        miControladorTienda.actualizarValorVentaPorPrecioDeProductoDeInventario(1, 2, 105000);

        miControladorTienda.agregarPorductoAInventario(1, "Medias", 8000, 100);
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 3, 100);

        miControladorTienda.agregarPorductoAInventario(1, "Chaqueta", 120000, 50);
        miControladorTienda.actualizarValorVentaPorPrecioDeProductoDeInventario(1, 4, 165000);

        miControladorTienda.agregarPorductoAInventario(1, "Vestido", 65000, 50);
        miControladorTienda.actualizarValorVentaPorPrecioDeProductoDeInventario(1, 5, 100000);

        miControladorTienda.agregarInventarioATienda("Secundario", 250);

        miControladorTienda.agregarPorductoAInventario(2, "Pantaloneta", 35000, 50);
        miControladorTienda.actualizarValorVentaPorPrecioDeProductoDeInventario(2, 6, 55000);

        miControladorTienda.agregarPorductoAInventario(2, "Boxer", 25000, 100);
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 7, 100);

        return miControladorTienda;
    }

}

