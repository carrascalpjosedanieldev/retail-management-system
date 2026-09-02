package RetailManagementSystem.infraestructura.persistencia.mysql.conexiones;

import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.infraestructura.persistencia.excepciones.PersistenciaException;

import java.sql.Connection;
import java.sql.SQLException;

public class GestorTransaccionalMySQL implements GestorTransaccional {

    @Override
    public void ejecutarEnTransaccion(OperacionTransaccional operacion) {
        Connection connection = null;
        try {
            connection = AdministradorConexion.obtenerConexion();
            connection.setAutoCommit(false);

            VinculadorTransaccion.vincular(connection);

            operacion.ejecutar();

            connection.commit();

        } catch (RuntimeException e) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {}
            }
            throw e;

        } catch (Exception e) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {}
            }
            throw new PersistenciaException("Fallo en la Infraestructura Transaccional", e);

        } finally {
            VinculadorTransaccion.desvincular();
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {}
            }
        }

    }

}

