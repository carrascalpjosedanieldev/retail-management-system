package RetailManagementSystem.infraestructura.persistencia.mysql.conexiones;

import java.sql.Connection;

public class VinculadorTransaccion {

    public static boolean hayConexionVinculada() {
        return soporteConexion.get() != null;
    }

    private static final ThreadLocal<Connection> soporteConexion = new ThreadLocal<>();

    public static void vincular(Connection connection) {
        soporteConexion.set(connection);
    }

    public static Connection getConnection() {
        return soporteConexion.get();
    }

    public static void desvincular() {
        soporteConexion.remove();
    }

}//===================================================================================================================//

