package RetailManagementSystem.infraestructura.seguridad;

import RetailManagementSystem.aplicacion.puertos.CodificadorContrasenas;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.Arrays;

public class Argon2CodificadorAdapter implements CodificadorContrasenas {

    //ATRIBUTOS:

    private final Argon2 argon2;

    private static final int ITERACIONES = 3;

    private static final int MEMORIA = 65536;

    private static final int PARALELISMO = 1;

    //CONSTRUCTOR:

    public Argon2CodificadorAdapter() {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    //MÉTODOS:

    @Override
    public String codificar(char[] contrasenaPlana) {
        try {
            return argon2.hash(ITERACIONES, MEMORIA, PARALELISMO, contrasenaPlana);
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
    }

    @Override
    public boolean verificar(char[] contrasenaPlana, String hashAlmacenado) {
        try {
            return argon2.verify(hashAlmacenado, contrasenaPlana);
        } finally {
            Arrays.fill(contrasenaPlana, '\0');
        }
    }

}//===================================================================================================================//

