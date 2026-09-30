package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.aplicacion.servicios.gestion.ServicioProductos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioServicios;
import RetailManagementSystem.aplicacion.servicios.ventas.ServicioCarrito;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.comercial.ProductoRopa;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.dominio.entidades.gestion.Descuento;
import RetailManagementSystem.dominio.entidades.gestion.Impuesto;
import RetailManagementSystem.dominio.entidades.gestion.PoliticaVencimiento;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.enums.Talla;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ProductoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ServicioNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.reglasDeNegocio.ProductoVencidoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServicioCarritoTest {

    private final Impuesto impuesto = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuento = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    @Mock
    private ServicioProductos servicioProductosFalso;

    @Mock
    private ServicioServicios servicioServiciosFalso;

    private ProductoRopa crearProductoRopaBase(String codigo, int stock) {
        return ProductoRopa.reconstruirDesdeBD(
                codigo,
                "Camiseta",
                new BigDecimal("35000"),
                new BigDecimal("65"),
                stock,
                impuesto,
                descuento,
                true,
                Talla.M
        );
    }

    private ProductoPerecedero crearProductoPerecederoBase(
            String codigo, LocalDate fechaVencimiento, int diasUmbral
    ) {
        PoliticaVencimiento politicaVencimiento = PoliticaVencimiento.reconstruirDesdeBD(
                1, "Política General", diasUmbral, new BigDecimal("20"), true
        );
        return ProductoPerecedero.reconstruirDesdeBD(
                codigo,
                "Leche",
                new BigDecimal("35000"),
                new BigDecimal("65"),
                25,
                impuesto,
                descuento,
                true,
                fechaVencimiento,
                politicaVencimiento
        );
    }

    private Servicio crearServicioBase(String codigo) {
        return Servicio.reconstruirDesdeBD(
                codigo,
                "Mantenimiento",
                new BigDecimal("15000"),
                impuesto,
                descuento,
                true
        );
    }

    private Carrito carrito;

    @InjectMocks
    private ServicioCarrito servicioCarrito;

    @BeforeEach
    void setUp(){
        carrito = Carrito.crearNuevo();
    }

    //TESTS

//    @Test
//    void deberiaAgregarItemNuevoCorrectamente(){
//        //ARRANGE
//        String codigoProducto = "producto1234567890";
//        Producto producto = crearProductoRopaBase(codigoProducto, 20);
//        int cantidad = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
//        //ACT
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidad, fecha);
//        //ASSERT
//        assertTrue(carrito.getItems().containsKey(codigoProducto));
//        verify(servicioProductosFalso).obtenerProductoActivoParaLaVenta(codigoProducto);
//        verifyNoInteractions(servicioServiciosFalso);
//    }
//
//    @Test
//    void deberiaHacerLaValidacionDePerecederoAlAgregarNuevoCorrectamente(){
//        //ARRANGE
//        String codigoProducto = "producto1234567890";
//        LocalDate fechaVencimiento = LocalDate.now().plusDays(5);
//        Producto producto = crearProductoPerecederoBase(codigoProducto, fechaVencimiento, 3);
//        int cantidad = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
//        //ACT
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidad, fecha);
//        //ASSERT
//        assertTrue(carrito.getItems().containsKey(codigoProducto));
//        verify(servicioProductosFalso).obtenerProductoActivoParaLaVenta(codigoProducto);
//        verifyNoInteractions(servicioServiciosFalso);
//    }

    @ParameterizedTest
    @CsvSource({
          //diasExtra, diasUmbral
            "1,        3",
            "1,        0",
            "5,        3"
    })
    void deberiaLanzarExcepcionSiElItemPerecederoEstaVencido(int diasExtra, int diasUmbral){
        //ARRANGE
        String codigoProducto = "producto1234567890";
        LocalDate fechaVencimiento = LocalDate.now().minusDays(diasExtra);
        Producto producto = crearProductoPerecederoBase(codigoProducto, fechaVencimiento, diasUmbral);
        int cantidad = 5;
        LocalDate fecha = LocalDate.now();
        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
        //ACT AND ASSERT
        ProductoVencidoException exception = assertThrows(
                ProductoVencidoException.class,
                ()-> servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidad, fecha)
        );
        assertEquals("El Producto -" + producto.getNombre() + "- está vencido.", exception.getMessage());
    }

//    @Test
//    void deberiaAgregarItemServicioCorrectamente(){
//        String codigoServicio = "servicio1234567890";
//        Servicio servicio = Servicio.reconstruirDesdeBD(
//                codigoServicio,
//                "Servicio",
//                new BigDecimal("15000"),
//                impuesto,
//                descuento,
//                true
//        );
//        int cantidad = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoServicio))
//                .thenThrow(new ProductoNoEncontradoException("NO Encontrado"));
//        when(servicioServiciosFalso.obtenerServicioActivoParaLaVenta(codigoServicio)).thenReturn(servicio);
//        //ACT
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoServicio, cantidad, fecha);
//        //ASSERT
//        assertTrue(carrito.getItems().containsKey(codigoServicio));
//        verify(servicioProductosFalso).obtenerProductoActivoParaLaVenta(codigoServicio);
//        verify(servicioServiciosFalso).obtenerServicioActivoParaLaVenta(codigoServicio);
//    }

    @Test
    void deberiaLanzarExcepcionSiElCodigoNoPerteneceAUnProductoOServicio(){
        //ARRANGE
        String codigoInvalido = "noExiste1234567890";
        int cantidad = 5;
        LocalDate fecha = LocalDate.now();
        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoInvalido))
                .thenThrow(new ProductoNoEncontradoException("NO Encontrado"));
        when(servicioServiciosFalso.obtenerServicioActivoParaLaVenta(codigoInvalido))
                .thenThrow(new ServicioNoEncontradoException("NO Encontrado"));
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoInvalido, cantidad, fecha)
        );
        assertEquals(
                "El Código -" + codigoInvalido + "- NO pertenece a un Producto ni a un Servicio Activo.",
                exception.getMessage()
        );
    }

//    @Test
//    void deberiaAumentarLaCantidadDelItemProductoCorrectamente(){
//        //ARRANGE
//        String codigoProducto = "producto1234567890";
//        Producto producto = crearProductoRopaBase(codigoProducto, 25);
//        int cantidadInicial = 5;
//        int cantidadAAumentar = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidadInicial, fecha);
//        int cantidadEsperada = cantidadInicial + cantidadAAumentar;
//        //ACT
//        servicioCarrito.aumentarCantidadItemDeCarrito(
//                carrito, codigoProducto, cantidadAAumentar, producto.getTipoItem(), fecha
//        );
//        //ASSERT
//        assertTrue(carrito.getItems().containsKey(codigoProducto));
//        assertEquals(cantidadEsperada, carrito.getItems().get(codigoProducto).getCantidad());
//        verify(servicioProductosFalso, times(2))
//                .obtenerProductoActivoParaLaVenta(codigoProducto);
//        verifyNoInteractions(servicioServiciosFalso);
//    }
//
//    @Test
//    void deberiaAumentarLaCantidadDelItemServicioCorrectamente(){
//        String codigoServicio = "servicio1234567890";
//        Servicio servicio = crearServicioBase(codigoServicio);
//        int cantidadInicial = 5;
//        int cantidadAAumentar = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoServicio))
//                .thenThrow(new ProductoNoEncontradoException("NO Encontrado"));
//        when(servicioServiciosFalso.obtenerServicioActivoParaLaVenta(codigoServicio)).thenReturn(servicio);
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoServicio, cantidadInicial, fecha);
//        int cantidadEsperada = cantidadInicial + cantidadAAumentar;
//        //ACT
//        servicioCarrito.aumentarCantidadItemDeCarrito(
//                carrito, codigoServicio, cantidadAAumentar, servicio.getTipoItem(), fecha
//        );
//        //ASSERT
//        assertTrue(carrito.getItems().containsKey(codigoServicio));
//        assertEquals(cantidadEsperada, carrito.getItems().get(codigoServicio).getCantidad());
//        verify(servicioProductosFalso, times(1))
//                .obtenerProductoActivoParaLaVenta(codigoServicio);
//        verify(servicioServiciosFalso, times(2))
//                .obtenerServicioActivoParaLaVenta(codigoServicio);
//    }
//
//    @Test
//    void deberiaReducirCantidadItemCorrectamente(){
//        //ARRANGE
//        String codigoProducto = "producto1234567890";
//        Producto producto = crearProductoRopaBase(codigoProducto, 15);
//        int cantidadInicial = 5;
//        int cantidadAReducir = 4;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidadInicial, fecha);
//        int cantidadEsperada = cantidadInicial - cantidadAReducir;
//        //ACT
//        servicioCarrito.reducirCantidadItem(carrito, codigoProducto, cantidadAReducir);
//        //ASSERT
//        assertTrue(carrito.getItems().containsKey(codigoProducto));
//        assertEquals(cantidadEsperada, carrito.getItems().get(codigoProducto).getCantidad());
//        verify(servicioProductosFalso, times(1))
//                .obtenerProductoActivoParaLaVenta(codigoProducto);
//        verifyNoInteractions(servicioServiciosFalso);
//    }
//
//    @Test
//    void deberiaEliminarItemCorrectamente(){
//        //ARRANGE
//        String codigoProducto = "producto1234567890";
//        Producto producto = crearProductoRopaBase(codigoProducto, 20);
//        int cantidadInicial = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidadInicial, fecha);
//        //ACT
//        servicioCarrito.eliminarItem(carrito, codigoProducto);
//        //ASSERT
//        assertFalse(carrito.getItems().containsKey(codigoProducto));
//        verify(servicioProductosFalso, times(1))
//                .obtenerProductoActivoParaLaVenta(codigoProducto);
//        verifyNoInteractions(servicioServiciosFalso);
//    }
//
//    @Test
//    void deberiaCancelarCompraTotalCorrectamente(){
//        //ARRANGE
//        String codigoProducto = "producto1234567890";
//        Producto producto = crearProductoRopaBase(codigoProducto, 25);
//        int cantidadProducto = 5;
//        LocalDate fecha = LocalDate.now();
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoProducto)).thenReturn(producto);
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoProducto, cantidadProducto, fecha);
//        String codigoServicio = "servicio1234567890";
//        Servicio servicio = Servicio.reconstruirDesdeBD(
//                codigoServicio,
//                "Servicio",
//                new BigDecimal("15000"),
//                impuesto,
//                descuento,
//                true
//        );
//        int cantidadServicio = 5;
//        when(servicioProductosFalso.obtenerProductoActivoParaLaVenta(codigoServicio))
//                .thenThrow(new ProductoNoEncontradoException("NO Encontrado"));
//        when(servicioServiciosFalso.obtenerServicioActivoParaLaVenta(codigoServicio)).thenReturn(servicio);
//        servicioCarrito.agregarItemNuevoAlCarrito(carrito, codigoServicio, cantidadServicio, fecha);
//        //ACT
//        servicioCarrito.cancelarCompraTotal(carrito);
//        //ASSERT
//        assertTrue(carrito.getItems().isEmpty());
//        assertFalse(carrito.getItems().containsKey(codigoProducto));
//        assertFalse(carrito.getItems().containsKey(codigoServicio));
//    }

}//===================================================================================================================//

