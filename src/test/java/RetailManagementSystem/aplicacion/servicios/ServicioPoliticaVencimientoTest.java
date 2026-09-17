package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.PoliticaVencimientoNoEncontradaException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPoliticaVencimiento;

import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioPoliticaVencimientoTest {

    @Mock
    private RepositorioPoliticaVencimiento repoPoliticaVFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    private PoliticaVencimiento politicaVPrueba;

    private static final String NOMBRE_POR_DEFECTO = "Política General";

    private static final Integer DIAS_UMBRAL_POR_DEFECTO = 3;

    private static final BigDecimal PORCENTAJE_POR_DEFECTO = new BigDecimal("20");

    @BeforeEach
    void setUp() {
        politicaVPrueba = PoliticaVencimiento.reconstruirDesdeBD(
                1,
                NOMBRE_POR_DEFECTO,
                DIAS_UMBRAL_POR_DEFECTO,
                PORCENTAJE_POR_DEFECTO,
                true
        );
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionConRetorno(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionDeLectura(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().doAnswer(invocation -> {
            OperacionTransaccional operacion = invocation.getArgument(0);
            operacion.ejecutar();
            return null;
        }).when(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    //TEST'S

    @Test
    void deberiaRegistrarUnaPoliticaVCorrectamente(){
        //ARRANGE
        when(repoPoliticaVFalso.insertarPoliticaVencimiento(any(PoliticaVencimiento.class))).thenReturn(politicaVPrueba);
        //ACT
        PoliticaVencimiento resultado = servicioPoliticaVencimiento.registrarPoliticaVencimiento(
                NOMBRE_POR_DEFECTO, DIAS_UMBRAL_POR_DEFECTO, PORCENTAJE_POR_DEFECTO, true
        );
        //ASSERT
        assertNotNull(resultado.getIdPolitica());
        ArgumentCaptor<PoliticaVencimiento> captor = ArgumentCaptor.forClass(PoliticaVencimiento.class);
        verify(repoPoliticaVFalso).insertarPoliticaVencimiento(captor.capture());
        PoliticaVencimiento politicaVCapturada = captor.getValue();
        assertNull(politicaVCapturada.getIdPolitica());
        assertEquals(NOMBRE_POR_DEFECTO, politicaVCapturada.getNombre());
        assertEquals(DIAS_UMBRAL_POR_DEFECTO, politicaVCapturada.getDiasUmbral());
        assertEquals(0, politicaVCapturada.getPorcentajeDescuento().compareTo(PORCENTAJE_POR_DEFECTO));
        assertTrue(politicaVCapturada.isActiva());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionCuandoObtenerPoliticaVNoExiste(){
        //ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "NO Existe una Política de Vencimiento con el ID: " + idInexistente;
        when(repoPoliticaVFalso.obtenerPoliticaVencimiento(idInexistente))
                .thenThrow(new PoliticaVencimientoNoEncontradaException(mensajeEsperado));
        //ACT AND ASSERT
        PoliticaVencimientoNoEncontradaException exception = assertThrows(
                PoliticaVencimientoNoEncontradaException.class,
                ()-> servicioPoliticaVencimiento.obtenerPoliticaVencimiento(99)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaActualizarUnaPoliticaVCorrectamente(){
        //ARRANGE
        String nombreNuevo = "Nombre Nuevo";
        int diasUmbralNuevo = 5;
        BigDecimal porcentajeNuevo = new BigDecimal("25");
        when(repoPoliticaVFalso.obtenerPoliticaVencimiento(1)).thenReturn(politicaVPrueba);
        //ACT
        servicioPoliticaVencimiento.actualizarPoliticaVencimiento(1, nombreNuevo, diasUmbralNuevo, porcentajeNuevo);
        //ASSERT
        ArgumentCaptor<PoliticaVencimiento> captor = ArgumentCaptor.forClass(PoliticaVencimiento.class);
        verify(repoPoliticaVFalso).actualizarPoliticaVencimiento(captor.capture());
        PoliticaVencimiento politicaVCapturada = captor.getValue();
        assertEquals(1, politicaVCapturada.getIdPolitica());
        assertEquals(nombreNuevo, politicaVCapturada.getNombre());
        assertEquals(diasUmbralNuevo, politicaVCapturada.getDiasUmbral());
        assertEquals(0, politicaVCapturada.getPorcentajeDescuento().compareTo(porcentajeNuevo));
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaCambiarEstadoCorrectamente(){
        // ARRANGE
        when(repoPoliticaVFalso.obtenerPoliticaVencimiento(1)).thenReturn(politicaVPrueba);
        // ACT
        servicioPoliticaVencimiento.cambiarEstadoPoliticaDeVencimiento(1);
        // ASSERT
        assertFalse(politicaVPrueba.isActiva());
        verify(repoPoliticaVFalso).actualizarPoliticaVencimiento(politicaVPrueba);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaLanzarExcepcionSiAlCambiarEstadoLaPoliticaVNoExiste(){
        //ARRANGE
        int idInexistente = 99;
        String mensajeEsperado = "NO Existe una Política de Vencimiento con el ID: " + idInexistente;
        when(repoPoliticaVFalso.obtenerPoliticaVencimiento(idInexistente))
                .thenThrow(new PoliticaVencimientoNoEncontradaException(mensajeEsperado));
        //ACT AND ASSERT
        PoliticaVencimientoNoEncontradaException exception = assertThrows(
                PoliticaVencimientoNoEncontradaException.class,
                ()-> servicioPoliticaVencimiento.cambiarEstadoPoliticaDeVencimiento(99)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repoPoliticaVFalso).obtenerPoliticaVencimiento(99);
        verifyNoMoreInteractions(repoPoliticaVFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaDevolverLaListaDePoliticasVActivasCorrectamente(){
        // ARRANGE
        List<PoliticaVencimiento> listaEsperada = List.of(politicaVPrueba);
        when(repoPoliticaVFalso.obtenerPoliticasVencimientoActivas()).thenReturn(listaEsperada);
        // ACT
        List<PoliticaVencimiento> listaRecibida = servicioPoliticaVencimiento.obtenerPoliticasVencimientoActivas();
        // ASSERT
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoPoliticaVFalso).obtenerPoliticasVencimientoActivas();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

    @Test
    void deberiaDevolverLaListaDeTodasLasPoliticasVCorrectamente(){
        // ARRANGE
        List<PoliticaVencimiento> listaEsperada = List.of(politicaVPrueba);
        when(repoPoliticaVFalso.obtenerTodasLasPoliticasDeVencimiento()).thenReturn(listaEsperada);
        // ACT
        List<PoliticaVencimiento> listaRecibida = servicioPoliticaVencimiento.obtenerTodasLasPoliticasDeVencimiento();
        // ASSERT
        assertEquals(listaEsperada.size(), listaRecibida.size());
        assertEquals(listaEsperada, listaRecibida);
        verify(repoPoliticaVFalso).obtenerTodasLasPoliticasDeVencimiento();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
    }

}//===================================================================================================================//

