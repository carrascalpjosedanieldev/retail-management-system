package RetailManagementSystem.dominio.financiero.calculos;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContextoEvaluacionTest {

    @Test
    void deberiaRetornarFechaCuandoSeInstanciaConFechaValida() {
        // ARRANGE
        LocalDate fechaEsperada = LocalDate.now();
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(fechaEsperada);
        // ACT
        Optional<LocalDate> resultado = contexto.getFechaEvaluacion();
        // ASSERT
        assertTrue(resultado.isPresent());
        assertEquals(fechaEsperada, resultado.get());
    }

    @Test
    void deberiaRetornarOptionalVacioCuandoSeInstanciaConNull() {
        // ARRANGE
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(null);
        // ACT
        Optional<LocalDate> resultado = contexto.getFechaEvaluacion();
        // ASSERT
        assertTrue(resultado.isEmpty());
    }

}//===================================================================================================================//

