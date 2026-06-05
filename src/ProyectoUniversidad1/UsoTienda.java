package ProyectoUniversidad1;

import java.util.Scanner;

import static ProyectoUniversidad1.MenuModificarTienda.*;
import static ProyectoUniversidad1.MenuUtilizarTienda.utilizarTienda;
import static ProyectoUniversidad1.MetodosTienda.*;

public class UsoTienda { //x LINEAS NETAS DE CODIGO DE 733 LINEAS TOTALES

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ControladorTienda miControladorTienda = new ControladorTienda();
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

}
