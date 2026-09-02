package RetailManagementSystem.dominio.puertos.transacciones;

@FunctionalInterface
public interface OperacionTransaccionalConRetorno<T> {

    T ejecutar();

}

