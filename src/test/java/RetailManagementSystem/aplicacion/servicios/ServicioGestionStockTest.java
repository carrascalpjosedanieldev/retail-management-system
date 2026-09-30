package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.servicios.gestion.ServicioGestionStock;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ProductoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ReferenciaNoEncontradaExcepcion;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.CapacidadInventarioExcedidaException;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.StockInsuficienteException;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioInventario;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioGestionStockTest {

    private static final int ID_INVENTARIO_POR_DEFECTO = 1;

    private static final String CODIGO_PRODUCTO_POR_DEFECTO = "producto01234567890";

    private static final int STOCK_POR_DEFECTO = 25;

    private final Impuesto impuesto = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuento = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private Producto productoPruebas;

    @Mock
    private RepositorioProducto repositorioProductoFalso;

    @Mock
    private RepositorioInventario repositorioInventarioFalso;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioGestionStock servicioGestionStock;

    @BeforeEach
    void setUp(){
        productoPruebas = ProductoRopa.reconstruirDesdeBD(
                CODIGO_PRODUCTO_POR_DEFECTO,
                "Producto",
                new BigDecimal("35000"),
                new BigDecimal("75"),
                STOCK_POR_DEFECTO,
                impuesto,
                descuento,
                true,
                Talla.M
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
    void deberiaRegistrarProductoEnInventarioCorrectamente(){
        //ACT
        servicioGestionStock.registrarProductoEnInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas);
        //ASSERT
        verify(repositorioInventarioFalso)
                .validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas.getStock());
        verify(repositorioProductoFalso).insertarProducto(productoPruebas, ID_INVENTARIO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaLanzarExcepcionSiELProductoEsNuloAlRegistrarProductoEnInventario(){
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioGestionStock.registrarProductoEnInventario(ID_INVENTARIO_POR_DEFECTO, null)
        );
        assertEquals("NO puedes Registrar un Producto Vacío", exception.getMessage());
        verifyNoInteractions(repositorioInventarioFalso, repositorioProductoFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiSeExcedeLaCapacidadDelInventarioAlRegistrarProductoEnInventario(){
        //ARRANGE
        String mensajeEsperado = "La Cantidad " + productoPruebas.getStock() + " Excede la capacidad del Inventario";
        doThrow(new CapacidadInventarioExcedidaException(mensajeEsperado))
                .when(repositorioInventarioFalso)
                .validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas.getStock());
        //ACT AND ASSERT
        CapacidadInventarioExcedidaException exception = assertThrows(
                CapacidadInventarioExcedidaException.class,
                ()-> servicioGestionStock.registrarProductoEnInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas)
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioInventarioFalso)
                .validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas.getStock());
        verifyNoInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraUnaReferenciaAlRegistrarProductoEnInventario(){
        //ARRANGE
        String mensajeEsperado =
                "NO se puede Guardar: Una Referencia (Inventario, Impuesto o Descuento) NO Existe en el Sistema.";
        doThrow(new ReferenciaNoEncontradaExcepcion(mensajeEsperado))
                .when(repositorioProductoFalso)
                .insertarProducto(productoPruebas, ID_INVENTARIO_POR_DEFECTO);
        //ACT AND ASSERT
        ReferenciaNoEncontradaExcepcion excepcion = assertThrows(
                ReferenciaNoEncontradaExcepcion.class,
                ()-> servicioGestionStock.registrarProductoEnInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas)
        );
        assertEquals(mensajeEsperado, excepcion.getMessage());
        verify(repositorioInventarioFalso)
                .validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, productoPruebas.getStock());
        verify(repositorioProductoFalso).insertarProducto(productoPruebas, ID_INVENTARIO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    @Test
    void deberiaAumentarStockDeProductoDeInventarioCorrectamente(){
        //ARRANGE
        int cantidadAAumentar = 5;
        when(repositorioProductoFalso.obtenerProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO
        )).thenReturn(productoPruebas);
        int stockEsperado = STOCK_POR_DEFECTO + cantidadAAumentar;
        //ACT
        Producto resultado = servicioGestionStock.aumentarStockDeProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO, cantidadAAumentar
        );
        //ASSERT
        assertEquals(stockEsperado, resultado.getStock());
        verify(repositorioProductoFalso)
                .obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO);
        verify(repositorioInventarioFalso)
                .validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, cantidadAAumentar);
        verify(repositorioProductoFalso).actualizarStockProducto(productoPruebas, ID_INVENTARIO_POR_DEFECTO);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraElProductoAlAumentarStockDeProductoDeInventario(){
        //ARRANGE
        int cantidadAAumentar = 5;
        String mensajeEsperado = "Error de negocio: El Producto con Código -" + CODIGO_PRODUCTO_POR_DEFECTO +
                "- NO existe en el Inventario con ID " + ID_INVENTARIO_POR_DEFECTO;
        when(repositorioProductoFalso.obtenerProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO
        )).thenThrow(new ProductoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                ()-> servicioGestionStock.aumentarStockDeProductoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO, cantidadAAumentar
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioInventarioFalso).validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, cantidadAAumentar);
        verify(repositorioProductoFalso)
                .obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionSiExcedeLaCapacidadALAumentarStockDeProductoDeInventario(){
        //ARRANGE
        int cantidadAAumentar = 5;
        String mensajeEsperado = "La Cantidad " + cantidadAAumentar + " Excede la capacidad del Inventario";
        doThrow(new CapacidadInventarioExcedidaException(mensajeEsperado))
                .when(repositorioInventarioFalso)
                .validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, cantidadAAumentar);
        //ACT AND ASSERT
        CapacidadInventarioExcedidaException exception = assertThrows(
                CapacidadInventarioExcedidaException.class,
                ()-> servicioGestionStock.aumentarStockDeProductoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO, cantidadAAumentar
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioInventarioFalso).validarCapacidadInventario(ID_INVENTARIO_POR_DEFECTO, cantidadAAumentar);
        verifyNoInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaReducirStockDeProductoDeInventarioCorrectamente(){
        //ARRANGE
        int cantidadAReducir = 5;
        when(repositorioProductoFalso.obtenerProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO
        )).thenReturn(productoPruebas);
        int cantidadEsperada = STOCK_POR_DEFECTO - cantidadAReducir;
        //ACT
        Producto resultado = servicioGestionStock.reducirStockDeProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO, cantidadAReducir
        );
        //ASSERT
        assertEquals(cantidadEsperada, resultado.getStock());
        verify(repositorioProductoFalso)
                .obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO);
        verify(repositorioProductoFalso).actualizarStockProducto(productoPruebas, ID_INVENTARIO_POR_DEFECTO);
        verifyNoInteractions(repositorioInventarioFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionSiNoEncuentraElProductoAlReducirStockDeProductoDeInventarioCorrectamente(){
        //ARRANGE
        int cantidadAReducir = 5;
        String mensajeEsperado = "Error de negocio: El Producto con Código -" + CODIGO_PRODUCTO_POR_DEFECTO +
                "- NO existe en el Inventario con ID " + ID_INVENTARIO_POR_DEFECTO;
        when(repositorioProductoFalso.obtenerProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO
        )).thenThrow(new ProductoNoEncontradoException(mensajeEsperado));
        //ACT AND ASSERT
        ProductoNoEncontradoException exception = assertThrows(
                ProductoNoEncontradoException.class,
                ()-> servicioGestionStock.reducirStockDeProductoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO, cantidadAReducir
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioProductoFalso)
                .obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioProductoFalso);
        verifyNoInteractions(repositorioInventarioFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaLanzarExcepcionSiElStockNoAlcanzaAlReducirStockDeProductoDeInventarioCorrectamente(){
        //ARRANGE
        int cantidadAReducirInvalida = STOCK_POR_DEFECTO + 1;
        when(repositorioProductoFalso.obtenerProductoDeInventario(
                ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO
        )).thenReturn(productoPruebas);
        //ACT AND ASSERT
        StockInsuficienteException exception = assertThrows(
                StockInsuficienteException.class,
                ()-> servicioGestionStock.reducirStockDeProductoDeInventario(
                        ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO, cantidadAReducirInvalida
                )
        );
        assertEquals(
                "La Cantidad de Producto a Reducir es Mayor a la Cantidad Existente",
                exception.getMessage()
        );
        verify(repositorioProductoFalso)
                .obtenerProductoDeInventario(ID_INVENTARIO_POR_DEFECTO, CODIGO_PRODUCTO_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioProductoFalso);
        verifyNoInteractions(repositorioInventarioFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccionConRetorno(any());
    }

    @Test
    void deberiaMoverProductoAInventarioCorrectamente() {
        // ARRANGE
        int idInventarioOrigen = 1;
        int idInventarioDestino = 2;
        // ACT
        servicioGestionStock.moverProductoAInventario(
                idInventarioOrigen, idInventarioDestino, CODIGO_PRODUCTO_POR_DEFECTO, STOCK_POR_DEFECTO
        );
        // ASSERT
        verify(repositorioInventarioFalso).validarCapacidadInventario(idInventarioDestino, STOCK_POR_DEFECTO);
        verify(repositorioProductoFalso)
                .cambiarInventarioProducto(CODIGO_PRODUCTO_POR_DEFECTO, idInventarioOrigen, idInventarioDestino);
        verifyNoMoreInteractions(repositorioInventarioFalso, repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiInventarioOrigenYDestinoSonIgualesAlMoverProducto() {
        // ARRANGE
        int idInventarioOrigen = 1;
        int idInventarioDestino = 1;
        // ACT & ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioGestionStock.moverProductoAInventario(
                        idInventarioOrigen, idInventarioDestino, CODIGO_PRODUCTO_POR_DEFECTO, STOCK_POR_DEFECTO
                )
        );
        assertEquals("El Inventario Destino y Origen son el mismo", exception.getMessage());
        verifyNoInteractions(repositorioInventarioFalso, repositorioProductoFalso, gestorTransaccionalFalso);
    }

    @Test
    void deberiaPropagarExcepcionYNoMoverProductoSiElDestinoNoTieneCapacidad() {
        // ARRANGE
        int idInventarioOrigen = 1;
        int idInventarioDestino = 2;
        String mensajeEsperado = "La Cantidad " + STOCK_POR_DEFECTO + " Excede la capacidad del Inventario";
        doThrow(new CapacidadInventarioExcedidaException(mensajeEsperado))
                .when(repositorioInventarioFalso).validarCapacidadInventario(idInventarioDestino, STOCK_POR_DEFECTO);
        // ACT & ASSERT
        CapacidadInventarioExcedidaException exception = assertThrows(
                CapacidadInventarioExcedidaException.class,
                () -> servicioGestionStock.moverProductoAInventario(
                        idInventarioOrigen, idInventarioDestino, CODIGO_PRODUCTO_POR_DEFECTO, STOCK_POR_DEFECTO
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(repositorioInventarioFalso).validarCapacidadInventario(idInventarioDestino, STOCK_POR_DEFECTO);
        verifyNoMoreInteractions(repositorioInventarioFalso);
        verifyNoInteractions(repositorioProductoFalso);
        verify(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
        verifyNoMoreInteractions(gestorTransaccionalFalso);
    }

}//===================================================================================================================//

