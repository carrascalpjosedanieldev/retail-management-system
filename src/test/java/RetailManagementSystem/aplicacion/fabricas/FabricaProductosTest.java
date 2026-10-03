package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.dto.creacion.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetallePerecederoDTO;
import RetailManagementSystem.aplicacion.dto.creacion.DetalleRopaDTO;
import RetailManagementSystem.aplicacion.dto.creacion.FormularioProductoDTO;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioDescuentos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioImpuestos;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;

import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ImpuestoNoEncontradoException;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FabricaProductosTest {

    private final Impuesto impuestoPruebas = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuentoPruebas = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private final ProductoRopa productoPruebas = ProductoRopa.reconstruirDesdeBD(
            "Ropa01234567890",
            "Ropa",
            new BigDecimal("50000"),
            new BigDecimal("100"),
            25,
            impuestoPruebas,
            descuentoPruebas,
            true,
            Talla.M
    );

    @Mock
    private EstrategiaFabricarRopa estrategiaFabricarRopaFalsa;

    @Mock
    private ServicioImpuestos servicioImpuestosFalso;

    @Mock
    private ServicioDescuentos servicioDescuentosFalso;

    private FabricaProductos fabricaProductos;

    @BeforeEach
    void setUp(){
        Map<TipoProducto, EstrategiaFabricarProducto<?, ?>> estrategiasFabricacion = new HashMap<>();
        estrategiasFabricacion.put(TipoProducto.ROPA, estrategiaFabricarRopaFalsa);
        fabricaProductos = new FabricaProductos(
                estrategiasFabricacion, servicioImpuestosFalso, servicioDescuentosFalso
        );
    }

    //TESTS

    @Test
    void deberiaRetornarElProductoCreadoConLaEstrategiaExistenteAlFabricarProducto() {
        //ARRANGE
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.ROPA, "Ropa", new BigDecimal("50000"), new BigDecimal("100"),
                25, 1, 1
        );
        DetalleRopaDTO detalle = new DetalleRopaDTO(Talla.M);
        FormularioProductoDTO datosProducto = new FormularioProductoDTO(datosGenerales, detalle);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        when(servicioImpuestosFalso.obtenerImpuesto(1)).thenReturn(impuestoPruebas);
        when(servicioDescuentosFalso.obtenerDescuento(1)).thenReturn(descuentoPruebas);
        when(estrategiaFabricarRopaFalsa.fabricarProducto(
                datosGenerales, detalle, contextoEvaluacion, impuestoPruebas, descuentoPruebas
        )).thenReturn(productoPruebas);
        //ACT
        Producto resultado = fabricaProductos.fabricarProducto(datosProducto, contextoEvaluacion);
        //ASSERT
        assertEquals(productoPruebas, resultado);
        verify(servicioImpuestosFalso).obtenerImpuesto(1);
        verify(servicioDescuentosFalso).obtenerDescuento(1);
        verify(estrategiaFabricarRopaFalsa).fabricarProducto(
                datosGenerales, detalle, contextoEvaluacion, impuestoPruebas, descuentoPruebas
        );
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso, estrategiaFabricarRopaFalsa);
    }

    @Test
    void deberiaLanzarExcepcionCuandoLaEstrategiaNoExisteAlFabricarProducto() {
        //ARRANGE
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.PERECEDERO, "Perecedero", new BigDecimal("50000"), new BigDecimal("100"),
                25, 1, 1
        );
        LocalDate fecha = LocalDate.now();
        DetallePerecederoDTO detalle = new DetallePerecederoDTO(fecha, 1);
        FormularioProductoDTO datosProducto = new FormularioProductoDTO(datosGenerales, detalle);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        //ACT AND ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                ()-> fabricaProductos.fabricarProducto(datosProducto, contextoEvaluacion)
        );
        assertEquals(
                "NO Existe una Estrategia de Fabricación de Producto para el Tipo: " + TipoProducto.PERECEDERO,
                exception.getMessage()
        );
        verifyNoInteractions(estrategiaFabricarRopaFalsa, servicioImpuestosFalso, servicioDescuentosFalso);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImpuestoEstaInactivoAlFabricarProducto() {
        //ARRANGE
        int idImpuestoInactivo = 2;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.ROPA, "Ropa", new BigDecimal("50000"), new BigDecimal("100"),
                25, idImpuestoInactivo, 1
        );
        DetalleRopaDTO detalle = new DetalleRopaDTO(Talla.M);
        FormularioProductoDTO datosProducto = new FormularioProductoDTO(datosGenerales, detalle);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        Impuesto inactivo = Impuesto.reconstruirDesdeBD(
                idImpuestoInactivo, "Inactivo", new BigDecimal("5"), false
        );
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuestoInactivo)).thenReturn(inactivo);
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProducto(datosProducto, contextoEvaluacion)
        );
        assertEquals(
                "NO se puede Asignar el Impuesto -" + inactivo.getNombre() + "- Porque se Encuentra Inactivo.",
                exception.getMessage()
        );
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuestoInactivo);
        verifyNoMoreInteractions(servicioImpuestosFalso);
        verifyNoInteractions(servicioDescuentosFalso, estrategiaFabricarRopaFalsa);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElImpuestoNoExisteAlFabricarProducto() {
        //ARRANGE
        int idImpuestoInexistente = 99;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.ROPA, "Ropa", new BigDecimal("50000"), new BigDecimal("100"),
                25, idImpuestoInexistente, 1
        );
        DetalleRopaDTO detalle = new DetalleRopaDTO(Talla.M);
        FormularioProductoDTO datosProducto = new FormularioProductoDTO(datosGenerales, detalle);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        String mensajeEsperado = "No existe un Impuesto con el ID: " + idImpuestoInexistente;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuestoInexistente))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                ()-> fabricaProductos.fabricarProducto(datosProducto, contextoEvaluacion)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuestoInexistente);
        verifyNoMoreInteractions(servicioImpuestosFalso);
        verifyNoInteractions(servicioDescuentosFalso, estrategiaFabricarRopaFalsa);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElDescuentoEstaInactivoAlFabricarProducto() {
        //ARRANGE
        int idDescuentoInactivo = 2;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.ROPA, "Ropa", new BigDecimal("50000"), new BigDecimal("100"),
                25, 1, idDescuentoInactivo
        );
        DetalleRopaDTO detalle = new DetalleRopaDTO(Talla.M);
        FormularioProductoDTO datosProducto = new FormularioProductoDTO(datosGenerales, detalle);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        when(servicioImpuestosFalso.obtenerImpuesto(1)).thenReturn(impuestoPruebas);
        Descuento inactivo = Descuento.reconstruirDesdeBD(
                idDescuentoInactivo, "Inactivo", new BigDecimal("10"), false
        );
        when(servicioDescuentosFalso.obtenerDescuento(idDescuentoInactivo)).thenReturn(inactivo);
        //ACT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProducto(datosProducto, contextoEvaluacion)
        );
        assertEquals(
                "NO se puede Asignar el Descuento -" + inactivo.getNombre() + "- Porque se Encuentra Inactivo.",
                exception.getMessage()
        );
        verify(servicioImpuestosFalso).obtenerImpuesto(1);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuentoInactivo);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso);
        verifyNoInteractions(estrategiaFabricarRopaFalsa);
    }

    @Test
    void deberiaLanzarExcepcionCuandoElDescuentoNoExisteAlFabricarProducto() {
        //ARRANGE
        int idDescuentoInexistente = 99;
        DatosGeneralesCreacionProductoDTO datosGenerales = new DatosGeneralesCreacionProductoDTO(
                TipoProducto.ROPA, "Ropa", new BigDecimal("50000"), new BigDecimal("100"),
                25, 1, idDescuentoInexistente
        );
        DetalleRopaDTO detalle = new DetalleRopaDTO(Talla.M);
        FormularioProductoDTO datosProducto = new FormularioProductoDTO(datosGenerales, detalle);
        LocalDate fecha = LocalDate.now();
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        when(servicioImpuestosFalso.obtenerImpuesto(1)).thenReturn(impuestoPruebas);
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idDescuentoInexistente;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuentoInexistente))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        //ACT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                ()-> fabricaProductos.fabricarProducto(datosProducto, contextoEvaluacion)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(1);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuentoInexistente);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso);
        verifyNoInteractions(estrategiaFabricarRopaFalsa);
    }

}//===================================================================================================================//

