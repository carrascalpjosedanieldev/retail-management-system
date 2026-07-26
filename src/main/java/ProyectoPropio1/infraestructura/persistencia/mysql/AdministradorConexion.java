package ProyectoPropio1.infraestructura.persistencia.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class AdministradorConexion {

    //ATRIBUTOS:

    private static final HikariDataSource dataSource;

    //CONSTRUCTORES:

    private AdministradorConexion() {
    }

    //METODOS

    static {
        Properties props = new Properties();
        try (InputStream input = AdministradorConexion.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error crítico al intentar leer application.properties", e);
        }

        String dbUrl = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : props.getProperty("db.url");
        String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : props.getProperty("db.user");
        String dbPass = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : props.getProperty("db.password");

        if (dbUrl == null || dbUser == null) {
            throw new IllegalStateException("ERROR CRÍTICO DE ARRANQUE: NO se encontraron Credenciales " +
                    "de Base de Datos. Verifique application.properties o las variables de entorno.");
        }

        HikariConfig config = new HikariConfig();

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

