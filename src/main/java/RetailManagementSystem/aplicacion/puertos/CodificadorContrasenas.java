package RetailManagementSystem.aplicacion.puertos;

public interface CodificadorContrasenas {

    String codificar(char[] contrasenaPlana);

    boolean verificar(char[] contrasenaPlana, String hashAlmacenado);

}
