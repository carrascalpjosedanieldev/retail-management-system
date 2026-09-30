package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.servicios.gestion.ServicioProductos;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.DescuentoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ImpuestoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.PoliticaVencimientoNoEncontradaException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ProductoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ProductoNoDisponibleException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioProducto;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioProductosTest {

    private static final int ID_INVENTARIO_POR_DEFECTO = 1;

    private final Impuesto impuesto = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuento = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private final PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
            1, "Política", 3, new BigDecimal("10"), true
    );

    private Producto productoRopaPruebas;

    private Producto productoPerecederoPruebas;

    @Mock
    private RepositorioProducto repositorioProductoFalso;

    @Mock
    private RepositorioImpuestos repositorioImpuestosFalso;

    @Mock
    private RepositorioDescuentos repositorioDescuentosFalso;

    @Mock
    private RepositorioPoliticaVencimiento repositorioPoliticaVencimientoFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioProductos servicioProductos;

    @BeforeEach
    void setUp(){
        productoRopaPruebas = ProductoRopa.reconstruirDesdeBD(
                "productoRopa01234567890",
                "Ropa",
                new BigDecimal("35000"),
                new BigDecimal("85"),
                25,
                impuesto,
                descuento,
                true,
                Talla.M
        );
        productoPerecederoPruebas = ProductoPerecedero.reconstruirDesdeBD(
                "productoPerecedero01234567890",
                "Perecedero",
                new BigDecimal("3000"),
                new BigDecimal("100"),
                25,
                impuesto,
                descuento,
                true,
                LocalDate.now().plusDays(10),
                politicaVencimiento
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
    void deberiaObtenerProductoActivoParaLaVentaCorrectamente(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        when(repositorioProductoFalso.obtenerProductoActivoSoloPorCodigo(codigoProducto))
                .thenReturn(productoRopaPruebas);
        //ACT
        Producto resultado = servicioProductos.obtenerProductoActivoParaLaVenta(codigoProducto);
        //ASSERT
        assertEquals(resultado, productoRopaPruebas);
        verify(repositorioProductoFalso).obtenerProductoActivoSoloPorCodigo(codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraProductoAlObtenerProductoActivoParaLaVenta(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String mensajeEsperado = "El Producto de Código -" + codigoProducto + "- NO existe";
        when(repositorioProductoFalso.obtenerProductoActivoSoloPorCodigo(codigoProducto))
                .thenThrow(new ProductoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                ()-> servicioProductos.obtenerProductoActivoParaLaVenta(codigoProducto)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoActivoSoloPorCodigo(codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElProductoNoEstaActivoAlObtenerProductoActivoParaLaVenta(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String mensajeEsperado = "Error de negocio: El Producto con Código -" + codigoProducto + "- NO esta en Venta";
        when(repositorioProductoFalso.obtenerProductoActivoSoloPorCodigo(codigoProducto))
                .thenThrow(new ProductoNoDisponibleException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoDisponibleException exception = assertThrows(
                ProductoNoDisponibleException.class,
                ()-> servicioProductos.obtenerProductoActivoParaLaVenta(codigoProducto)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoActivoSoloPorCodigo(codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaCambiarEstadoProductoCorrectamente(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        boolean estadoAnterior = productoRopaPruebas.isActivo();
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoRopaPruebas);
        //ACT
        servicioProductos.cambiarEstadoProducto(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        //ASSERT
        assertNotEquals(estadoAnterior, productoRopaPruebas.isActivo());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(repositorioProductoFalso).actualizarProducto(productoRopaPruebas, ID_INVENTARIO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraProductoAlCambiarEstadoProducto(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String mensajeEsperado = "El Producto con Código -" + codigoProducto + "- NO existe en el Inventario con ID " +
                ID_INVENTARIO_POR_DEFECTO;
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenThrow(new ProductoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                ()-> servicioProductos.cambiarEstadoProducto(ID_INVENTARIO_POR_DEFECTO, codigoProducto)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaActualizarProductoRopaDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        Descuento descuentoNuevo = Descuento.reconstruirDesdeBD(
                idDescuento, "Nuevo", new BigDecimal("15"), true
        );
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoNuevo);
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoRopaPruebas);
        //ACT
        Producto resultado = servicioProductos.actualizarProductoRopaDeInventario(
                ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                porcentajeGananciaNuevo, idImpuesto, idDescuento
        );
        //ASSERT
        assertEquals("Nombre Nuevo", productoRopaPruebas.getNombre());
        assertEquals(0, valorCompraNuevo.compareTo(productoRopaPruebas.getValorCompra()));
        assertEquals(0, porcentajeGananciaNuevo.compareTo(productoRopaPruebas.getPorcentajeGanancia()));
        assertEquals(impuestoNuevo, productoRopaPruebas.getImpuesto());
        assertEquals(descuentoNuevo, productoRopaPruebas.getDescuento());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(repositorioProductoFalso).actualizarProducto(productoRopaPruebas, ID_INVENTARIO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verifyNoMoreInteractions(
                repositorioProductoFalso, repositorioDescuentosFalso, repositorioImpuestosFalso,
                gestorTransaccionalFalso
        );
        verifyNoInteractions(repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraProductoAlActualizarProductoRopaDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        String mensajeEsperado = "El Producto con Código -" + codigoProducto + "- NO existe en el Inventario con ID " +
                ID_INVENTARIO_POR_DEFECTO;
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenThrow(new ProductoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                ()-> servicioProductos.actualizarProductoRopaDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                        porcentajeGananciaNuevo, idImpuesto, idDescuento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraElDescuentoAlActualizarProductoRopaDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idDescuento;
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoRopaPruebas);
        //ACT AND ASSERT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                ()-> servicioProductos.actualizarProductoRopaDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                        porcentajeGananciaNuevo, idImpuesto, idDescuento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verifyNoMoreInteractions(repositorioProductoFalso, repositorioImpuestosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraImpuestoAlActualizarProductoRopaDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoRopaPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        String mensajeEsperado = "No existe un Impuesto con el ID: " + idImpuesto;
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoRopaPruebas);
        //ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                ()-> servicioProductos.actualizarProductoRopaDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                        porcentajeGananciaNuevo, idImpuesto, idDescuento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verifyNoMoreInteractions(repositorioProductoFalso, repositorioImpuestosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaActualizarProductoPerecederoDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoPerecederoPruebas.getCodigo();
        String nombreNuevo = "  Nuevo Nombre  ";
        BigDecimal valorCompraNuevo = new BigDecimal("2500");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("85");
        int idImpuesto = 2;
        int idDescuento = 2;
        int idPoliticaVencimiento = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        Descuento descuentoNuevo = Descuento.reconstruirDesdeBD(
                idDescuento, "Nuevo", new BigDecimal("15"), true
        );
        PoliticaVencimiento politicaVNueva = PoliticaVencimiento.reconstruirDesdeBD(
                idPoliticaVencimiento, "Nuevo", 5, new BigDecimal("15"), true
        );
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoNuevo);
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        when(repositorioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPoliticaVencimiento))
                .thenReturn(politicaVNueva);
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoPerecederoPruebas);
        //ACT
        Producto resultado = servicioProductos.actualizarProductoPerecederoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo, porcentajeGananciaNuevo,
                idImpuesto, idDescuento, idPoliticaVencimiento
        );
        //ASSERT
        assertEquals("Nuevo Nombre", resultado.getNombre());
        assertEquals(0, valorCompraNuevo.compareTo(resultado.getValorCompra()));
        assertEquals(0, porcentajeGananciaNuevo.compareTo(resultado.getPorcentajeGanancia()));
        assertEquals(impuestoNuevo, resultado.getImpuesto());
        assertEquals(descuentoNuevo, resultado.getDescuento());
        ProductoPerecedero perecedero = (ProductoPerecedero) productoPerecederoPruebas;
        assertEquals(politicaVNueva, perecedero.getPoliticaVencimiento());
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraProductoAlActualizarProductoPerecederoDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoPerecederoPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        int idPoliticaVencimiento = 2;
        String mensajeEsperado = "El Producto con Código -" + codigoProducto + "- NO existe en el Inventario con ID " +
                ID_INVENTARIO_POR_DEFECTO;
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenThrow(new ProductoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                ()-> servicioProductos.actualizarProductoPerecederoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                        porcentajeGananciaNuevo, idImpuesto, idDescuento, idPoliticaVencimiento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verifyNoMoreInteractions(repositorioProductoFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso,repositorioImpuestosFalso,repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraImpuestoAlActualizarProductoPerecederoDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoPerecederoPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        int idPoliticaVencimiento = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idDescuento;
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoPerecederoPruebas);
        //ACT AND ASSERT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                ()-> servicioProductos.actualizarProductoPerecederoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                        porcentajeGananciaNuevo, idImpuesto, idDescuento, idPoliticaVencimiento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verifyNoMoreInteractions(repositorioProductoFalso, repositorioImpuestosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraDescuentoAlActualizarProductoPerecederoDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoPerecederoPruebas.getCodigo();
        String nombreNuevo = "  Nombre Nuevo  ";
        BigDecimal valorCompraNuevo = new BigDecimal("45000");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("75");
        int idImpuesto = 2;
        int idDescuento = 2;
        int idPoliticaVencimiento = 2;
        String mensajeEsperado = "No existe un Impuesto con el ID: " + idImpuesto;
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoPerecederoPruebas);
        //ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                ()-> servicioProductos.actualizarProductoPerecederoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo,
                        porcentajeGananciaNuevo, idImpuesto, idDescuento, idPoliticaVencimiento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verifyNoMoreInteractions(repositorioProductoFalso, repositorioImpuestosFalso, gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraPoliticaVAlActualizarProductoPerecederoDeInventarioCorrectamente(){
        //ARRANGE
        String codigoProducto = productoPerecederoPruebas.getCodigo();
        String nombreNuevo = "  Nuevo Nombre  ";
        BigDecimal valorCompraNuevo = new BigDecimal("2500");
        BigDecimal porcentajeGananciaNuevo = new BigDecimal("85");
        int idImpuesto = 2;
        int idDescuento = 2;
        int idPoliticaVencimiento = 2;
        Impuesto impuestoNuevo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Nuevo", new BigDecimal("5"), true
        );
        Descuento descuentoNuevo = Descuento.reconstruirDesdeBD(
                idDescuento, "Nuevo", new BigDecimal("15"), true
        );
        PoliticaVencimiento politicaVNueva = PoliticaVencimiento.reconstruirDesdeBD(
                idPoliticaVencimiento, "Nuevo", 5, new BigDecimal("15"), true
        );
        when(repositorioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoNuevo);
        when(repositorioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoNuevo);
        String mensajeEsperado = "NO Existe una Política de Vencimiento con el ID: " + idPoliticaVencimiento;
        when(repositorioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPoliticaVencimiento))
                .thenThrow(new PoliticaVencimientoNoEncontradaException(mensajeEsperado));
        when(repositorioProductoFalso.obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto))
                .thenReturn(productoPerecederoPruebas);
        //ACT ASSERT
        PoliticaVencimientoNoEncontradaException exception = assertThrows(
                PoliticaVencimientoNoEncontradaException.class,
                ()-> servicioProductos.actualizarProductoPerecederoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, codigoProducto, nombreNuevo, valorCompraNuevo, porcentajeGananciaNuevo,
                        idImpuesto, idDescuento, idPoliticaVencimiento
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso).obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, codigoProducto);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
        verify(repositorioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(repositorioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(repositorioPoliticaVencimientoFalso).obtenerPoliticaVencimiento(idPoliticaVencimiento);
        verifyNoMoreInteractions(
                repositorioProductoFalso, repositorioImpuestosFalso, repositorioDescuentosFalso,
                gestorTransaccionalFalso, repositorioPoliticaVencimientoFalso
        );
    }

    @Test
    void deberiaObtenerTodosLosProductosDeInventarioCorrectamente(){
        //ARRANGE
        int idInventario = 1;
        List<Producto> listaEsperada = List.of(productoRopaPruebas, productoPerecederoPruebas);
        when(repositorioProductoFalso.obtenerProductosPorInventario(idInventario))
                .thenReturn(listaEsperada);
        //ACT
        List<Producto> resultado = servicioProductos.obtenerTodosLosProductosDeInventario(idInventario);
        //ASSERT
        assertEquals(listaEsperada, resultado);
        assertEquals(listaEsperada.size(), resultado.size());
        verify(repositorioProductoFalso).obtenerProductosPorInventario(idInventario);
        verifyNoMoreInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso, repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaObtenerTodosLosProductosRopaDeInventario(){
        //ARRANGE
        int idInventario = 1;
        List<Producto> listaEsperada = List.of(productoRopaPruebas);
        when(repositorioProductoFalso.obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.ROPA))
                .thenReturn(listaEsperada);
        //ACT
        List<Producto> resultado = servicioProductos.obtenerTodosLosProductosRopaDeInventario(idInventario);
        //ASSERT
        assertEquals(listaEsperada, resultado);
        assertEquals(listaEsperada.size(), resultado.size());
        verify(repositorioProductoFalso).obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.ROPA);
        verifyNoMoreInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso, repositorioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaObtenerTodosLosProductosPerecederoDeInventarioCorrectamente(){
        //ARRANGE
        int idInventario = 1;
        List<Producto> listaEsperada = List.of(productoRopaPruebas);
        when(repositorioProductoFalso.obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.PERECEDERO))
                .thenReturn(listaEsperada);
        //ACT
        List<Producto> resultado = servicioProductos.obtenerTodosLosProductosPerecederoDeInventario(idInventario);
        //ASSERT
        assertEquals(listaEsperada, resultado);
        assertEquals(listaEsperada.size(), resultado.size());
        verify(repositorioProductoFalso).obtenerProductosDeTipoDeInventario(idInventario, TipoProducto.PERECEDERO);
        verifyNoMoreInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionDeLectura(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
        verifyNoInteractions(repositorioDescuentosFalso, repositorioImpuestosFalso, repositorioPoliticaVencimientoFalso);
    }

}//===================================================================================================================//

