package ProyectoPropio1.vistaConsola;

import java.math.BigDecimal;
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
                                   1                              MODIFICAR TIENDA
                                   2                               UTILIZAR TIENDA
                                   3                                   SALIR
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

    public static BigDecimal leerDecimal(Scanner sc){
        while (true){
            try {
                String entrada = sc.nextLine().trim();
                return new BigDecimal(entrada);
            } catch (NumberFormatException e) {
                System.out.print("\nDebes ingresar un número válido (Usa punto para decimales, Ej: 15.50)\n---> ");
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

    public static String peticionSiNo(Scanner sc){
        String peticion;
        do {
            System.out.print("(SI / NO):\n" +
                    "---> ");
            peticion = sc.nextLine();
        } while (!peticion.equalsIgnoreCase("SI") && !peticion.equalsIgnoreCase("NO"));
        return peticion;
    }

    public static void salirPrograma(){
        System.out.println("""
                        \nGUARDANDO ...
                        HAS SALIDO DEL SISTEMA CON EXITO
                        """);
    }

}

