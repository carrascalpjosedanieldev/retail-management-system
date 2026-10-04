package RetailManagementSystem.infraestructura.seguridad;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Argon2CodificadorAdapterTest {

    private Argon2CodificadorAdapter codificador;

    @BeforeEach
    void setUp() {
        codificador = new Argon2CodificadorAdapter();
    }

    //TESTS

    @Test
    void deberiaGenerarHashValidoYLimpiarArregloOriginalAlCodificar() {
        //ARRANGE
        char[] contrasena = {'M', 'i', 'C', 'l', 'a', 'v', 'e', '1', '2', '3'};
        char[] arregloEsperadoLimpio = {'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        //ACT
        String hashGenerado = codificador.codificar(contrasena);
        //ASSERT
        assertNotNull(hashGenerado, "El hash generado no debe ser nulo");
        assertTrue(hashGenerado.startsWith("$argon2id$"), "El hash debe tener el formato de Argon2id");
        assertArrayEquals(arregloEsperadoLimpio, contrasena);
    }

    @Test
    void deberiaVerificarConContrasenaCorrectaRetornarTrueYLimpiarArreglo() {
        //ARRANGE
        char[] contrasenaOriginal = {'S', 'e', 'c', 'r', 'e', 't', 'o'};
        String hashAlmacenado = codificador.codificar(contrasenaOriginal);
        char[] contrasenaIntento = {'S', 'e', 'c', 'r', 'e', 't', 'o'};
        char[] arregloEsperadoLimpio = {'\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        //ACT
        boolean esValida = codificador.verificar(contrasenaIntento, hashAlmacenado);
        //ASSERT
        assertTrue(esValida, "La verificación debe ser exitosa con la contraseña correcta");
        assertArrayEquals(arregloEsperadoLimpio, contrasenaIntento);
    }

    @Test
    void deberiaVerificarConContrasenaIncorrectaRetornarFalseYLimpiarArreglo() {
        //ARRANGE
        char[] contrasenaOriginal = {'S', 'e', 'c', 'r', 'e', 't', 'o'};
        String hashAlmacenado = codificador.codificar(contrasenaOriginal);
        char[] contrasenaIncorrecta = {'F', 'a', 'l', 's', 'o', '1', '2'};
        char[] arregloEsperadoLimpio = {'\0', '\0', '\0', '\0', '\0', '\0', '\0'};
        //ACT
        boolean esValida = codificador.verificar(contrasenaIncorrecta, hashAlmacenado);
        //ASSERT
        assertFalse(esValida, "La verificación debe fallar con una contraseña incorrecta");
        assertArrayEquals(arregloEsperadoLimpio, contrasenaIncorrecta);
    }

}//===================================================================================================================//

