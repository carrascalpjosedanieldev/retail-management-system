package RetailManagementSystem.dominio.entidades.gestion;

import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class InventarioTest {

    private static final Integer ID_POR_DEFECTO = 1;
    private static final String NOMBRE_POR_DEFECTO = "Inventario Estándar";
    private static final int CAPACIDAD_MAXIMA_POR_DEFECTO = 500;
    private static final int CAPACIDAD_OCUPADA_POR_DEFECTO = 250;

    private Inventario inventarioMutador;

    @BeforeEach
    void setUp() {
        inventarioMutador = Inventario.reconstruirDesdeBD(
                ID_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                CAPACIDAD_MAXIMA_POR_DEFECTO,
                CAPACIDAD_OCUPADA_POR_DEFECTO
        );
    }

    @Test
    void deberiaCrearInventarioCorrectamente(){
        //ACT
        Inventario inventario = Inventario.crearNuevo(NOMBRE_POR_DEFECTO, CAPACIDAD_MAXIMA_POR_DEFECTO);
        //ASSERT
        assertNull(inventario.getIdInventario());
        assertEquals(NOMBRE_POR_DEFECTO, inventario.getNombre());
        assertEquals(CAPACIDAD_MAXIMA_POR_DEFECTO, inventario.getCapacidadMaxima());
        assertEquals(0, inventario.getCapacidadOcupada());
    }

    @Test
    void deberiaRecuperarInventarioDeBDCorrectamente(){
        //ACT
        Inventario inventario = Inventario.reconstruirDesdeBD(
                ID_POR_DEFECTO,
                NOMBRE_POR_DEFECTO,
                CAPACIDAD_MAXIMA_POR_DEFECTO,
                CAPACIDAD_OCUPADA_POR_DEFECTO
        );
        //ASSERT
        assertEquals(ID_POR_DEFECTO, inventario.getIdInventario());
        assertEquals(NOMBRE_POR_DEFECTO, inventario.getNombre());
        assertEquals(CAPACIDAD_MAXIMA_POR_DEFECTO, inventario.getCapacidadMaxima());
        assertEquals(CAPACIDAD_OCUPADA_POR_DEFECTO, inventario.getCapacidadOcupada());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si el nombre es nulo o vacío al crear nuevo")
    @CsvSource( value = {"null", "''", "'   '"} , nullValues = "null")
    void deberiaLanzarExcepcionSiElNombreEsInvalidoAlCrearNuevo(String nombre){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Inventario.crearNuevo(nombre, CAPACIDAD_MAXIMA_POR_DEFECTO)
        );
        assertEquals("El Nombre del Inventario NO puede estar Vacío", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si la capacidad maxima es nula o invalida al crear nuevo")
    @CsvSource( value = {"null", "-1", "0"} , nullValues = "null")
    void deberiaLanzarExcepcionSiLaCapacidadMaximaEsInvalidaAlCrearNuevo(Integer capacidadMaxima){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Inventario.crearNuevo(NOMBRE_POR_DEFECTO, capacidadMaxima)
        );
        assertEquals("La Capacidad Maxima del Inventario es Invalida", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si la capacidad ocupada es nula o invalida al recuperar de BD")
    @CsvSource( value = {"null", "-1"} , nullValues = "null")
    void deberiaLanzarExcepcionSiLaCapacidadOcupadaEsInvalidaAlReconstruirDeBD(Integer capacidadOcupada){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> Inventario.reconstruirDesdeBD(ID_POR_DEFECTO, NOMBRE_POR_DEFECTO, CAPACIDAD_MAXIMA_POR_DEFECTO, capacidadOcupada)
        );
        assertEquals("La Capacidad Ocupada del Inventario es Invalida", exception.getMessage());
    }


    @Test
    void deberiaLanzarExcepcionSiLaCapacidadOcupadaEsMayorALaCapacidadMaxima(){
        //ARRANGE
        int capacidadOcupada = 501;
        //ACT AND ASSERT
        CapacidadInventarioExcedidaException exception = assertThrows(
                CapacidadInventarioExcedidaException.class,
                ()-> Inventario.reconstruirDesdeBD(ID_POR_DEFECTO, NOMBRE_POR_DEFECTO, CAPACIDAD_MAXIMA_POR_DEFECTO, capacidadOcupada)
        );
        assertEquals("La Capacidad Ocupada es Mayor a la Capacidad Maxima", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"   Modificado   ", "Modificado"})
    void deberiaCambiarNombreCorrectamente(String nombreNuevo){
        //ACT
        inventarioMutador.cambiarNombreInventario(nombreNuevo);
        //ASSERT
        assertEquals("Modificado", inventarioMutador.getNombre());
    }

    @ParameterizedTest
    @DisplayName("Debería lanzar excepción si el nuevo nombre es nulo o vacío al cambiar nombre")
    @CsvSource(value = {"null", "''", "'   '"}, nullValues = "null")
    void deberiaLanzarExcepcionSiElNuevoNombreEsInvalidoAlCambiarNombre(String nombreInvalido) {
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioMutador.cambiarNombreInventario(nombreInvalido)
        );
        assertEquals("El Nombre del Inventario NO puede estar Vacío", exception.getMessage());
    }

    @Test
    void deberiaAumentarLaCapacidadMaximaCorrectamente(){
        //ACT
        inventarioMutador.aumentarCapacidadMaxima(50);
        //ASSERT
        assertEquals(550, inventarioMutador.getCapacidadMaxima());
    }

    @ParameterizedTest
    @CsvSource( value = {"null", "-1", "0"}, nullValues = "null")
    void deberiaLanzarExcepcionSiAlAumentarCapacidadMaximaLaCantidadExtraEsInvalida(Integer capacidadExtra){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> inventarioMutador.aumentarCapacidadMaxima(capacidadExtra)
        );
        assertEquals(
                "La Capacidad Extra para expandir la Capacidad Maxima del Inventario debe ser Mayor a 0",
                exception.getMessage()
        );
    }

    @Test
    void deberiaCalcularCapacidadLibreCorrectamente() {
        // ACT
        int capacidadLibre = inventarioMutador.calcularCapacidadLibre();
        // ASSERT
        assertEquals(250, capacidadLibre);
    }

    @Test
    void deberiaConsiderarIgualesDosInventariosConElMismoId() {
        // ARRANGE
        Inventario inventario1 = Inventario.reconstruirDesdeBD(1, "A", 100, 10);
        Inventario inventario2 = Inventario.reconstruirDesdeBD(1, "B", 200, 20);
        // ACT & ASSERT
        assertEquals(inventario1, inventario2);
        assertEquals(inventario1.hashCode(), inventario2.hashCode());
    }

}//===================================================================================================================//

