package RetailManagementSystem.infraestructura.configuracion;

public class ContextoAplicacion {

    //VERSION DE LA APLICACIÓN:

    private static InformacionAplicacion infoApp;

    public static void inicializar(String rutaProperties) {
        if (infoApp != null) {
            throw new IllegalStateException(
                    "El Contexto de la Aplicación YA fue Inicializado."
            );
        }
        infoApp = new InformacionAplicacion(rutaProperties);
    }

    public static InformacionAplicacion getInformacionAplicacion() {
        return infoApp;
    }

}//===================================================================================================================//

