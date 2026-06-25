package ProyectoPropio1;

import ProyectoPropio1.dominio.Tienda;
import ProyectoPropio1.servicios.ControladorTienda;
import ProyectoPropio1.servicios.GestorVentas;

import java.time.LocalDate;
import java.util.Scanner;

import static ProyectoPropio1.vistaConsola.MenuModificarTienda.*;
import static ProyectoPropio1.vistaConsola.MenuUtilizarTienda.utilizarTienda;
import static ProyectoPropio1.vistaConsola.MetodosTienda.*;

public class Ejecutable {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Tienda miTienda = new Tienda("Nombre Por Defecto");
        GestorVentas miGestorVentas = new GestorVentas(miTienda);
        ControladorTienda miControladorTienda = new ControladorTienda(miTienda, miGestorVentas);
        //Comentable para pruebas
        miControladorTienda = paraPruebasTienda();
        int opcionMenuPrincipal;
        do {
            menuPrincipal();
            opcionMenuPrincipal = pedirOpcion(sc,1,3);
            switch (opcionMenuPrincipal){
                case 1:
                    modificarTienda(sc, miControladorTienda);
                    break;
                case 2:
                    utilizarTienda(sc, miControladorTienda);
                    break;
                case 3:
                    salirPrograma();
                    break;
            }
        } while (opcionMenuPrincipal!=3);
    }


    private static ControladorTienda paraPruebasTienda() {

        Tienda miTienda = new Tienda("SLIM");
        GestorVentas miGestorVentas = new GestorVentas(miTienda);
        ControladorTienda miControladorTienda = new ControladorTienda(miTienda, miGestorVentas);


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
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 4, 30);

        miControladorTienda.registrarProductoRopa(1, "Pantalon", 75000, 40, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 5, 30);


        miControladorTienda.registrarProductoRopa(1, "Medias", 8000, 50, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 6, 100);

        miControladorTienda.registrarProductoRopa(1, "Medias", 8000, 50, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(1, 7, 100);


        miControladorTienda.agregarInventarioATienda("Secundario", 250);//------------Inventario


        miControladorTienda.registrarProductoRopa(2, "Chaqueta", 120000, 20, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 1, 30);

        miControladorTienda.registrarProductoRopa(2, "Chaqueta", 120000, 30, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 2, 30);


        miControladorTienda.registrarProductoRopa(2, "Pantaloneta", 35000, 25, "M");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 3, 40);

        miControladorTienda.registrarProductoRopa(2, "Pantaloneta", 35000, 25, "L");
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(2, 4, 40);


        miControladorTienda.agregarInventarioATienda("Comida", 150);//------------Inventario

        miControladorTienda.registrarProductoPerecedero(3, "Papas", 1000, 20, LocalDate.of(2027,11,10));
        miControladorTienda.actualizarValorVentaPorPorcentajeDeProductoDeInventario(3, 1, 100);


        return miControladorTienda;
    }

}

