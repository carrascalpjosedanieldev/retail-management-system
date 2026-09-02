package RetailManagementSystem.dominio.puertos.transacciones;

public interface GestorTransaccional {

    void ejecutarEnTransaccion(OperacionTransaccional operacion);

    <T> T ejecutarEnTransaccionConRetorno(OperacionTransaccionalConRetorno<T> operacion);

}

