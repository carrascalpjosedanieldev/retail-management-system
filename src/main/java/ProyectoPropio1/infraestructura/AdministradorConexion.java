package ProyectoPropio1.infraestructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdministradorConexion {

    private static final String URL = "jdbc:mysql://localhost:3306/mi_tienda";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private AdministradorConexion() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

}
