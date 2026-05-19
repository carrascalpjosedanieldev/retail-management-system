package ProyectoUniversidad1;

import java.util.InputMismatchException;
import java.util.Scanner;

public class MetodosTienda {

    //MENUS:

    public static void menuPrincipal(){
        System.out.print("""
                \n                          BIENVENIDO -MENU PRINCIPAL-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                              CREAR TIENDA (1)
                                   2                              MODIFICAR TIENDA
                                   3                               UTILIZAR TIENDA
                                   4                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void menuUtilizarTienda(){
        System.out.print("""
                \n                        BIENVENIDO -MENU UTILIZAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                          VER TODOS LOS PRODUCTOS
                                   2                              VENDER PRODUCTOS
                                   3                           VER RECAUDO A CLIENTES
                                   4                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void menuModificarTienda(){
        System.out.print("""
                \n                             -MODIFICAR TIENDA-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                           CAMBIAR NOMBRE TIENDA
                                   2                           AGREGAR INVENTARIO (1)
                                   3                               VER INVENTARIO
                                   4                            MODIFICAR INVENTARIO
                                   5                            VER STOCK INVENTARIO
                                   6                         ELIMINAR INVENTARIO VACIO
                                   7                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    public static void menuModificarInventario(){
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

    public static void menuModificarProducto(){
        System.out.print("""
                \n                           -MODIFICAR PRODUCTO-
                ---> SELECCIONA QUE QUIERES HACER:
                               * OPCION                              * ACCION
                                   1                          CAMBIAR NOMBRE PRODUCTO
                                   2                           ACTUALIZAR VALOR VENTA
                                   3                           ACTUALIZAR VALOR COMPRA
                                   4                              ACTUALIZAR STOCK
                                   5                                REDUCIR STOCK
                                   6                                   SALIR
                ---> Ingresa el numero segun tu eleccion:
                """ + "---> ");
    }

    //METODOS SUELTOS:

    public static int leerEntero(Scanner sc){
        int numero;
        while (true){
            try {
                numero = sc.nextInt();
                sc.nextLine();
                return numero;
            } catch (InputMismatchException enteroInvalido){
                System.out.print("\nDebes Ingresar un Numero Entero\n---> ");
                sc.nextLine();
            }
        }
    }

    public static double leerDecimal(Scanner sc){
        double numero;
        while (true){
            try {
                numero = sc.nextDouble();
                sc.nextLine();
                return numero;
            } catch (InputMismatchException enteroInvalido){
                System.out.print("\nDebes Ingresar un Numero Valido\n---> ");
                sc.nextLine();
            }
        }
    }

}
