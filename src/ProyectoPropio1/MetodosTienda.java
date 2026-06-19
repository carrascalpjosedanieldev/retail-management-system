package ProyectoPropio1;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MetodosTienda {

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
            } catch (InputMismatchException decimalInvalido){
                System.out.print("\nDebes Ingresar un Numero Valido\n---> ");
                sc.nextLine();
            }
        }
    }

    public static LocalDate leerFecha(Scanner sc) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            String entrada = sc.nextLine().trim();
            if (entrada.isEmpty()) {
                continue;
            }
            try {
                return LocalDate.parse(entrada, formato);
            } catch (DateTimeParseException e) {
                System.out.println("ERROR: Formato de fecha incorrecto.");
                System.out.print("Por favor, use el formato DD/MM/AAAA (Ejemplo: 25/12/2024): ---> ");
            }
        }
    }

    public static int pedirOpcion(Scanner sc, int opcionMin, int opcionMax){
        boolean opcionValida;
        int opcion;
        do {
            opcionValida =true;
            opcion = leerEntero(sc);
            if (opcion <opcionMin || opcion >opcionMax){
                System.out.print("""
                            \n---> ACCION DENEGADA:
                            Recuerda seleccionar una de las opciones disponibles
                            """ + "---> ");
                opcionValida =false;
            }
        } while (!opcionValida);
        return opcion;
    }

    public static void crearTienda(Scanner sc, ControladorTienda controladorTienda){
        if (!controladorTienda.puedeCrearTienda()){
            System.out.print("""
                                \nACCION DENEGADA:
                                YA HAS CREADO UNA TIENDA Y EL PROGRAMA SOLO ADMITE EL MANEJO DE UNA SOLA
                                """);
            return;
        }
        System.out.println("\nHAS SELECCIONADO: -CREAR TIENDA-");
        System.out.print("""
                                Escribe el nombre que le pondras a tu tienda:
                                """ + "---> ");
        String nombreTienda;
        nombreTienda=sc.nextLine();
        try {
            controladorTienda.crearTienda(nombreTienda);
            System.out.println("\nGENERACION DE TIENDA EXITOSA:\n" +
                    "La Tienda: -" + controladorTienda.obtenerNombreTienda() + "- esta lista para generar su Inventario");
        } catch (IllegalArgumentException e) {
            System.out.println("\nNO se puede generar esta Tienda por un error de asignacion de datos:\n" +
                    "ERROR: " + e.getMessage());
        }
    }

    public static void salirPrograma(){
        System.out.println("""
                        \nGUARDANDO ...
                        HAS SALIDO DEL SISTEMA CON EXITO
                        """);
    }

}

