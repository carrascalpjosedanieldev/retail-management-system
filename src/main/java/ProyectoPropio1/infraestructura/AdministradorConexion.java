package ProyectoPropio1.infraestructura;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class AdministradorConexion {

    private static final HikariDataSource dataSource;

    private AdministradorConexion() {
    }

    static {
        HikariConfig config = new HikariConfig();

        String dbUrl = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/mi_tienda";
        String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "root";
        String dbPass = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "";

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPass);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection obtenerConexion() throws SQLException {
        return dataSource.getConnection();
    }

}
