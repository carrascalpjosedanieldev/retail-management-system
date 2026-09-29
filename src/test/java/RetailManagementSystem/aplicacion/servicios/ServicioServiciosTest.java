package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ImpuestoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ServicioNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ServicioNoDisponibleException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioServicio;
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
public class ServicioServiciosTest {

    private final Impuesto impuesto = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuento = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private static final String CODIGO_POR_DEFECTO = "servicio01234567890";

    private Servicio servicioPruebas;

    @Mock
    private RepositorioImpuestos repositorioImpuestosFalso;

    @Mock
    private RepositorioDescuentos repositorioDescuentosFalso;

    @Mock
    private RepositorioServicio repositorioServicioFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioServicios servicioServicios;

    @BeforeEach
    void setUp(){
        servicioPruebas = Servicio.reconstruirDesdeBD(
                CODIGO_POR_DEFECTO,
                "Servicio",
                new BigDecimal("15000"),
                impuesto,
                descuento,
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

    //TESTS

    @Test
    void deberiaObtenerServicioActivoParaLaVentaCorrectamente(){
        //ARRANGE
        when(repositorioServicioFalso.obtenerServicioActivoSoloPorCodigo(CODIGO_POR_DEFECTO))
                .thenReturn(servicioPruebas);
        //ACT
        Servicio resultado = servicioServicios.obtenerServicioActivoParaLaVenta(CODIGO_POR_DEFECTO);
        //ASSERT
        assertTrue(resultado.isActivo());
        verify(repositorioServicioFalso).obtenerServicioActivoSoloPorCodigo(CODIGO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraServicioAlObtenerServicioActivoParaLaVentaCorrectamente(){
        //ARRANGE
        String mensajeEsperado = "El Servicio con Código -" + CODIGO_POR_DEFECTO + "- NO Existe";
        when(repositorioServicioFalso.obtenerServicioActivoSoloPorCodigo(CODIGO_POR_DEFECTO))
                .thenThrow(new ServicioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ServicioNoEncontradoException exception = assertThrows(
                ServicioNoEncontradoException.class,
                ()-> servicioServicios.obtenerServicioActivoParaLaVenta(CODIGO_POR_DEFECTO)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioServicioFalso).obtenerServicioActivoSoloPorCodigo(CODIGO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElServicioEstaInactivoAlObtenerServicioActivoParaLaVentaCorrectamente(){
        //ARRANGE
        String mensajeEsperado = "El Servicio con Código -" + CODIGO_POR_DEFECTO + "- NO esta Disponible";
        when(repositorioServicioFalso.obtenerServicioActivoSoloPorCodigo(CODIGO_POR_DEFECTO))
                .thenThrow(new ServicioNoDisponibleException(mensajeEsperado));
        //ACT AND ASSERT
        ServicioNoDisponibleException exception = assertThrows(
                ServicioNoDisponibleException.class,
                ()-> servicioServicios.obtenerServicioActivoParaLaVenta(CODIGO_POR_DEFECTO)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioServicioFalso).obtenerServicioActivoSoloPorCodigo(CODIGO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso);
    }

    @Test
    void deberiaRegistrarServicioNuevoCorrectamente(){
        //ARRANGE
        String nombre = "   Servicio   ";
        BigDecimal precioBase = new BigDecimal("15000");
        int idImpuesto = 1;
        int idDescuento = 1;
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuesto);
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuento);
        //ACT
        Servicio resultado = servicioServicios.registrarServicioNuevo(nombre, precioBase, idImpuesto, idDescuento);
        //ASSERT
        assertEquals("Servicio", resultado.getNombre());
        assertEquals(0, precioBase.compareTo(resultado.getPrecioBase()));
        assertEquals(impuesto, resultado.getImpuesto());
        assertEquals(descuento, resultado.getDescuento());
        assertTrue(resultado.isActivo());
        ArgumentCaptor<Servicio> servicioCapturador = ArgumentCaptor.forClass(Servicio.class);
        verify(repositorioServicioFalso).insertarServicio(servicioCapturador.capture());
        assertSame(servicioCapturador.getValue(), resultado);
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(
                repositorioServicioFalso, repositorioImpuestosFalso, repositorioDescuentosFalso,
                gestorTransaccionalFalso
        );
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoNoExisteAlRegistrarServicioNuevo(){
        //ARRANGE
        String nombre = "   Servicio   ";
        BigDecimal precioBase = new BigDecimal("15000");
        int idImpuesto = 1;
        int idDescuento = 1;
        String mensajeEsperado = "No existe un Impuesto con el ID: " + idImpuesto;
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                ()-> servicioServicios.registrarServicioNuevo(nombre, precioBase, idImpuesto, idDescuento)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioImpuestosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioServicioFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoNoExisteAlRegistrarServicioNuevo(){
        //ARRANGE
        String nombre = "   Servicio   ";
        BigDecimal precioBase = new BigDecimal("15000");
        int idImpuesto = 1;
        int idDescuento = 1;
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuesto);
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idDescuento;
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                ()-> servicioServicios.registrarServicioNuevo(nombre, precioBase, idImpuesto, idDescuento)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioImpuestosFalso, repositorioDescuentosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioServicioFalso);
    }

    @Test
    void deberiaActualizarServicioCorrectamente(){
        //ARRANGE
        String nombreNuevo = "   Nuevo   ";
        BigDecimal precioBaseNuevo = new BigDecimal("12000");
        int idImpuesto = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        int idDescuento = 2;
        Descuento descuentoNuevo = Descuento.reconstruirDesdeBD(
                idDescuento, "Nuevo", new BigDecimal("10"), true
        );
        boolean activo = servicioPruebas.isActivo();
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoNuevo);
        when(repositorioServicioFalso.obtenerServicio(CODIGO_POR_DEFECTO)).thenReturn(servicioPruebas);
        //ACT
        Servicio resultado = servicioServicios.actualizarServicio(
                CODIGO_POR_DEFECTO, nombreNuevo, precioBaseNuevo, idImpuesto, idDescuento
        );
        //ASSERT
        assertEquals("Nuevo", resultado.getNombre());
        assertEquals(0, precioBaseNuevo.compareTo(resultado.getPrecioBase()));
        assertEquals(impuestoNuevo, resultado.getImpuesto());
        assertEquals(descuentoNuevo, resultado.getDescuento());
        assertEquals(activo, resultado.isActivo());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(repositorioServicioFalso).obtenerServicio(CODIGO_POR_DEFECTO);
        ArgumentCaptor<Servicio> servicioCapturador = ArgumentCaptor.forClass(Servicio.class);
        verify(repositorioServicioFalso).actualizarServicio(servicioCapturador.capture());
        assertSame(servicioCapturador.getValue(), resultado);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(
                repositorioServicioFalso, repositorioDescuentosFalso, repositorioImpuestosFalso,
                gestorTransaccionalFalso
        );
    }

    @Test
    void deberiaActualizarServicioSinConsultarImpuestoYDescuentoSiLosIdSonIguales(){
        //ARRANGE
        String nombreNuevo = "   Nuevo   ";
        BigDecimal precioBaseNuevo = new BigDecimal("12000");
        int idImpuesto = 1;
        int idDescuento = 1;
        boolean activo = servicioPruebas.isActivo();
        when(repositorioServicioFalso.obtenerServicio(CODIGO_POR_DEFECTO)).thenReturn(servicioPruebas);
        //ACT
        Servicio resultado = servicioServicios.actualizarServicio(
                CODIGO_POR_DEFECTO, nombreNuevo, precioBaseNuevo, idImpuesto, idDescuento
        );
        //ASSERT
        assertEquals("Nuevo", resultado.getNombre());
        assertEquals(0, precioBaseNuevo.compareTo(resultado.getPrecioBase()));
        assertEquals(impuesto, resultado.getImpuesto());
        assertEquals(descuento, resultado.getDescuento());
        assertEquals(activo, resultado.isActivo());
        verify(repositorioServicioFalso).obtenerServicio(CODIGO_POR_DEFECTO);
        ArgumentCaptor<Servicio> servicioCapturador = ArgumentCaptor.forClass(Servicio.class);
        verify(repositorioServicioFalso).actualizarServicio(servicioCapturador.capture());
        assertSame(servicioCapturador.getValue(), resultado);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioImpuestosFalso, repositorioDescuentosFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraElImpuestoAlActualizarServicio(){
        //ARRANGE
        String nombreNuevo = "   Nuevo   ";
        BigDecimal precioBaseNuevo = new BigDecimal("12000");
        int idImpuesto = 2;
        String mensajeEsperado = "No existe un Impuesto con el ID: " + idImpuesto;
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        int idDescuento = 2;
        when(repositorioServicioFalso.obtenerServicio(CODIGO_POR_DEFECTO)).thenReturn(servicioPruebas);
        //ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                ()-> servicioServicios.actualizarServicio(
                        CODIGO_POR_DEFECTO, nombreNuevo, precioBaseNuevo ,idImpuesto, idDescuento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioServicioFalso).obtenerServicio(CODIGO_POR_DEFECTO);
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioServicioFalso, repositorioImpuestosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraElDescuentoAlActualizarServicio(){
        //ARRANGE
        String nombreNuevo = "   Nuevo   ";
        BigDecimal precioBaseNuevo = new BigDecimal("12000");
        int idImpuesto = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        int idDescuento = 2;
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idDescuento;
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        when(repositorioServicioFalso.obtenerServicio(CODIGO_POR_DEFECTO)).thenReturn(servicioPruebas);
        //ACT AND ASSERT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                ()-> servicioServicios.actualizarServicio(
                        CODIGO_POR_DEFECTO, nombreNuevo, precioBaseNuevo, idImpuesto, idDescuento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioServicioFalso).obtenerServicio(CODIGO_POR_DEFECTO);
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(
                repositorioServicioFalso, repositorioDescuentosFalso, repositorioImpuestosFalso,
                gestorTransaccionalFalso
        );
    }

    @Test
    void deberiaCambiarEstadoServicioCorrectamente(){
        //ARRANGE
        boolean activoAnterior = servicioPruebas.isActivo();
        when(repositorioServicioFalso.obtenerServicio(CODIGO_POR_DEFECTO)).thenReturn(servicioPruebas);
        //ACT
        servicioServicios.cambiarEstadoServicio(CODIGO_POR_DEFECTO);
        //ASSERT
        assertNotEquals(activoAnterior, servicioPruebas.isActivo());
        verify(repositorioServicioFalso).obtenerServicio(CODIGO_POR_DEFECTO);
        verify(repositorioServicioFalso).actualizarServicio(servicioPruebas);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraExcepcionAlCambiarEstadoServicio(){
        //ARRANGE
        String mensajeEsperado = "El Servicio con código -" + CODIGO_POR_DEFECTO + "- NO Existe";
        when(repositorioServicioFalso.obtenerServicio(CODIGO_POR_DEFECTO))
                .thenThrow(new ServicioNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ServicioNoEncontradoException exception = assertThrows(
                ServicioNoEncontradoException.class,
                ()-> servicioServicios.cambiarEstadoServicio(CODIGO_POR_DEFECTO)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioServicioFalso).obtenerServicio(CODIGO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso);
    }

    @Test
    void deberiaObtenerTodosLosServiciosCorrectamente(){
        //ARRANGE
        List<Servicio> listaEsperada = List.of(servicioPruebas);
        when(repositorioServicioFalso.obtenerTodosLosServicios()).thenReturn(listaEsperada);
        //ACT
        List<Servicio> resultado = servicioServicios.obtenerTodosLosServicios();
        //ASSERT
        assertEquals(listaEsperada.size(), resultado.size());
        assertEquals(listaEsperada, resultado);
        verify(repositorioServicioFalso).obtenerTodosLosServicios();
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioServicioFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso);

    }

}//===================================================================================================================//

