package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetallePerecederoDTO;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioPoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstrategiaFabricarPerecederoTest {

    @Mock
    private ServicioPoliticaVencimiento servicioPoliticaVencimientoFalso;

    private final Impuesto impuestoPruebas = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuentoPruebas = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private final PoliticaVencimiento politicaPruebas = PoliticaVencimiento.reconstruirDesdeBD(
            1, "Politica General", 3, new BigDecimal("15"), true
    );

    @InjectMocks
    private EstrategiaFabricarPerecedero estrategia;

    //TESTS

    @Test
    void deberiaRetornarProductoPerecederoCreadoConLosDatosProporcionadosAlFabricarProducto() {
        //ARRANGE
        LocalDate fecha = LocalDate.now();
        LocalDate vencimiento = fecha.plusDays(10);
        int idPolitica = 1;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.PERECEDERO,
                "Queso",
                new BigDecimal("15000"),
                new BigDecimal("30"),
                50,
                1,
                1
        );
        DetallePerecederoDTO detalle = new DetallePerecederoDTO(vencimiento, idPolitica);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(fecha);
        when(servicioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPolitica)).thenReturn(politicaPruebas);
        //ACT
        ProductoPerecedero resultado = estrategia.fabricarProducto(
                datosGenerales, detalle, contexto, impuestoPruebas, descuentoPruebas
        );
        //ASSERT
        assertNotNull(resultado, "El producto fabricado NO debe ser nulo");
        assertEquals("Queso", resultado.getNombre());
        assertEquals(new BigDecimal("15000.000000"), resultado.getValorCompra());
        assertEquals(new BigDecimal("30.000000"), resultado.getPorcentajeGanancia());
        assertEquals(50, resultado.getStock());
        assertEquals(impuestoPruebas, resultado.getImpuesto());
        assertEquals(descuentoPruebas, resultado.getDescuento());
        assertEquals(vencimiento, resultado.getFechaVencimiento());
        assertEquals(politicaPruebas, resultado.getPoliticaVencimiento());
        verify(servicioPoliticaVencimientoFalso).obtenerPoliticaVencimiento(idPolitica);
        verifyNoMoreInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElContextoNoTieneFechaAlFabricarProducto() {
        //ARRANGE
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.PERECEDERO,
                "Queso",
                new BigDecimal("15000"),
                new BigDecimal("30"),
                50,
                1,
                1
        );
        LocalDate fecha = LocalDate.now();
        DetallePerecederoDTO detalle = new DetallePerecederoDTO(fecha, 1);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(null);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estrategia.fabricarProducto(
                        datosGenerales, detalle, contextoEvaluacion, impuestoPruebas, descuentoPruebas
                )
        );
        assertEquals("Se Requiere la Fecha Actual para Fabricar el Producto", exception.getMessage());
        verifyNoInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElProductoEstaVencidoAlFabricarProducto() {
        //ARRANGE
        LocalDate fecha = LocalDate.now();
        LocalDate vencimiento = fecha.minusDays(1);
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.PERECEDERO,
                "Queso",
                new BigDecimal("15000"),
                new BigDecimal("30"),
                50,
                1,
                1
        );
        DetallePerecederoDTO detalle = new DetallePerecederoDTO(vencimiento, 1);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(fecha);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estrategia.fabricarProducto(
                        datosGenerales, detalle, contexto, impuestoPruebas, descuentoPruebas
                )
        );
        assertEquals("NO se puede Registrar el Producto porque ya está Vencido", exception.getMessage());
        verifyNoInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaPoliticaEstaInactivaAlFabricarProducto() {
        //ARRANGE
        LocalDate fecha = LocalDate.now();
        LocalDate vencimiento = fecha.plusDays(10);
        int idPoliticaInactiva = 2;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.PERECEDERO, "Queso", new BigDecimal("15000"), new BigDecimal("30"),
                50, 1, 1
        );
        DetallePerecederoDTO detalle = new DetallePerecederoDTO(vencimiento, idPoliticaInactiva);
        ContextoEvaluacion contexto = ContextoEvaluacion.crearNuevo(fecha);
        PoliticaVencimiento inactiva = PoliticaVencimiento.reconstruirDesdeBD(
                idPoliticaInactiva, "Inactiva", 3, new BigDecimal("5"), false
        );
        when(servicioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPoliticaInactiva))
                .thenReturn(inactiva);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estrategia.fabricarProducto(
                        datosGenerales, detalle, contexto, impuestoPruebas, descuentoPruebas
                )
        );
        assertEquals(
                "NO se puede Asignar la Política de Vencimiento -" + inactiva.getNombre() +
                        "- Porque se Encuentra Inactiva.",
                exception.getMessage()
        );
        verify(servicioPoliticaVencimientoFalso).obtenerPoliticaVencimiento(idPoliticaInactiva);
        verifyNoMoreInteractions(servicioPoliticaVencimientoFalso);
    }

}//===================================================================================================================//

