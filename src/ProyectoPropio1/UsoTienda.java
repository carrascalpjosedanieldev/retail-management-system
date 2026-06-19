package ProyectoPropio1;

import java.time.LocalDate;
import java.util.Scanner;

import static ProyectoPropio1.MenuModificarTienda.*;
import static ProyectoPropio1.MenuUtilizarTienda.utilizarTienda;
import static ProyectoPropio1.MetodosTienda.*;

public class UsoTienda {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ControladorTienda miControladorTienda = new ControladorTienda();

        //Comentable para pruebas
        miControladorTienda = paraPruebasTienda();

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
                    salirPrograma();
                    break;
            }
        } while (opcionMenuPrincipal!=4);

    }

    private static ControladorTienda paraPruebasTienda() {
        ControladorTienda miControladorTienda = new ControladorTienda();

        miControladorTienda.crearTienda("SLIM");

        miControladorTienda.registrarServicioNuevo("Domicilio", 15000);
        miControladorTienda.registrarServicioNuevo("Empaquetado General", 15000);
        miControladorTienda.registrarServicioNuevo("Empaquetado A Un Producto", 3000);


        miControladorTienda.agregarInventarioATienda("Principal", 450);//------------Inventario


        miControladorTienda.registrarProductoRopa(1, "Camisa", 50000, 30, "S");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 1, 40);

        miControladorTienda.registrarProductoRopa(1, "Camisa", 50000, 30, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 2, 40);

        miControladorTienda.registrarProductoRopa(1, "Camisa", 50000, 20, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 3, 45);


        miControladorTienda.registrarProductoRopa(1, "Pantalon", 75000, 40, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 5, 30);

        miControladorTienda.registrarProductoRopa(1, "Pantalon", 75000, 40, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 6, 30);


        miControladorTienda.registrarProductoRopa(1, "Medias", 8000, 50, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 7, 100);

        miControladorTienda.registrarProductoRopa(1, "Medias", 8000, 50, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 8, 100);


        miControladorTienda.agregarInventarioATienda("Secundario", 250);//------------Inventario


        miControladorTienda.registrarProductoRopa(2, "Chaqueta", 120000, 20, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 9, 30);

        miControladorTienda.registrarProductoRopa(2, "Chaqueta", 120000, 30, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 10, 30);


        miControladorTienda.registrarProductoRopa(2, "Pantaloneta", 35000, 25, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 11, 40);

        miControladorTienda.registrarProductoRopa(2, "Pantaloneta", 35000, 25, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 12, 40);


        miControladorTienda.agregarInventarioATienda("Comida", 150);//------------Inventario

        miControladorTienda.registrarProductoPerecedero(3, "Papas", 1000, 20, LocalDate.of(2027,11,10));
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(3, 13, 100);


        return miControladorTienda;
    }

}

