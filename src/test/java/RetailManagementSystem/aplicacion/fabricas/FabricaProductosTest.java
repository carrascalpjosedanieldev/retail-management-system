package RetailManagementSystem.aplicacion.fabricas;

import RetailManagementSystem.aplicacion.servicios.gestion.ServicioDescuentos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioImpuestos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioPoliticaVencimiento;
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
public class FabricaProductosTest {

    private final Impuesto impuestoPruebas = Impuesto.reconstruirDesdeBD(
            1, "IVA", new BigDecimal("19"), true
    );

    private final Descuento descuentoPruebas = Descuento.reconstruirDesdeBD(
            1, "Descuento", new BigDecimal("10"), true
    );

    private final PoliticaVencimiento politicaVPruebas = PoliticaVencimiento.reconstruirDesdeBD(
            1, "Política", 3, new BigDecimal("15"), true
    );

    @Mock
    private ServicioImpuestos servicioImpuestosFalso;

    @Mock
    private ServicioDescuentos servicioDescuentosFalso;

    @Mock
    private ServicioPoliticaVencimiento servicioPoliticaVencimientoFalso;

    @InjectMocks
    private FabricaProductos fabricaProductos;

    //TESTS

    @Test
    void deberiaFabricarProductoRopaCorrectamente(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 1;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoPruebas);
        String nombre = "  Ropa  ";
        BigDecimal valorCompra = new BigDecimal("35000");
        BigDecimal porcentajeGanancia = new BigDecimal("85");
        int stock = 25;
        String tallaString = "m";
        //ACT
        ProductoRopa productoRopa = fabricaProductos.fabricarProductoRopa(
                nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, tallaString
        );
        //ASSERT
        assertNotNull(productoRopa.getCodigo());
        assertEquals(TipoProducto.ROPA, productoRopa.getTipoProducto());
        assertEquals("Ropa", productoRopa.getNombre());
        assertEquals(0, valorCompra.compareTo(productoRopa.getValorCompra()));
        assertEquals(0, porcentajeGanancia.compareTo(productoRopa.getPorcentajeGanancia()));
        assertEquals(stock, productoRopa.getStock());
        assertEquals(impuestoPruebas, productoRopa.getImpuesto());
        assertEquals(descuentoPruebas, productoRopa.getDescuento());
        assertEquals(Talla.M, productoRopa.getTalla());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso);
        verifyNoInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoExisteImpuestoAlFabricarProductoRopa(){
        //ARRANGE
        int idImpuesto = 1;
        String mensajeEsperado = "No existe un Impuesto con el ID: " + idImpuesto;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto))
                .thenThrow(new ImpuestoNoEncontradoException(mensajeEsperado));
        int idDescuento = 1;
        String nombre = "  Ropa  ";
        BigDecimal valorCompra = new BigDecimal("35000");
        BigDecimal porcentajeGanancia = new BigDecimal("85");
        int stock = 25;
        String tallaString = "m";
        //ACT AND ASSERT
        ImpuestoNoEncontradoException exception = assertThrows(
                ImpuestoNoEncontradoException.class,
                ()-> fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, tallaString
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verifyNoMoreInteractions(servicioImpuestosFalso);
        verifyNoInteractions(servicioDescuentosFalso, servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElImpuestoNoEstaActivoAlFabricarProductoRopa(){
        //ARRANGE
        int idImpuesto = 2;
        Impuesto impuestoInactivo = Impuesto.reconstruirDesdeBD(
                idImpuesto, "Inactivo", new BigDecimal("5"), false
        );
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoInactivo);
        int idDescuento = 1;
        String nombre = "  Ropa  ";
        BigDecimal valorCompra = new BigDecimal("35000");
        BigDecimal porcentajeGanancia = new BigDecimal("85");
        int stock = 25;
        String tallaString = "m";
        String mensajeEsperado = "NO se puede Asignar el Impuesto -" + impuestoInactivo.getNombre() +
                "- Porque se Encuentra Inactivo.";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, tallaString
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verifyNoMoreInteractions(servicioImpuestosFalso);
        verifyNoInteractions(servicioDescuentosFalso, servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiNoExisteDescuentoAlFabricarProductoRopa(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 1;
        String mensajeEsperado = "NO existe un Descuento con el ID: " + idDescuento;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento))
                .thenThrow(new DescuentoNoEncontradoException(mensajeEsperado));
        String nombre = "  Ropa  ";
        BigDecimal valorCompra = new BigDecimal("35000");
        BigDecimal porcentajeGanancia = new BigDecimal("85");
        int stock = 25;
        String tallaString = "m";
        //ACT AND ASSERT
        DescuentoNoEncontradoException exception = assertThrows(
                DescuentoNoEncontradoException.class,
                ()-> fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, tallaString
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso);
        verifyNoInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElDescuentoNoEstaActivoAlFabricarProductoRopa(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 2;
        Descuento descuentoInactivo = Descuento.reconstruirDesdeBD(
                idDescuento, "Inactivo", new BigDecimal("15"), false
        );
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoInactivo);
        String nombre = "  Ropa  ";
        BigDecimal valorCompra = new BigDecimal("35000");
        BigDecimal porcentajeGanancia = new BigDecimal("85");
        int stock = 25;
        String tallaString = "m";
        String mensajeEsperado = "NO se puede Asignar el Descuento -" + descuentoInactivo.getNombre() +
                "- Porque se Encuentra Inactivo.";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, tallaString
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso);
        verifyNoInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaTallaNoExisteAlFabricarProductoRopa(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 1;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoPruebas);
        String nombre = "  Ropa  ";
        BigDecimal valorCompra = new BigDecimal("35000");
        BigDecimal porcentajeGanancia = new BigDecimal("85");
        int stock = 25;
        String tallaStringInvalida = "no existe";
        String mensajeEsperado = "La Talla Ingresada NO está entre las Opciones (Usa S, M, L, XL etc).";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProductoRopa(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, tallaStringInvalida
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso);
        verifyNoInteractions(servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaFabricarProductoPerecederoCorrectamente(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 1;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoPruebas);
        int idPoliticaV = 1;
        when(servicioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPoliticaV)).thenReturn(politicaVPruebas);
        String nombre = "  Perecedero  ";
        BigDecimal valorCompra = new BigDecimal("3000");
        BigDecimal porcentajeGanancia = new BigDecimal("100");
        int stock = 25;
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaVencimiento = fechaActual.plusDays(5);
        //ACT
        ProductoPerecedero productoPerecedero = fabricaProductos.fabricarProductoPerecedero(
                nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, fechaVencimiento,
                idPoliticaV, fechaActual
        );
        //ASSERT
        assertNotNull(productoPerecedero.getCodigo());
        assertEquals(TipoProducto.PERECEDERO, productoPerecedero.getTipoProducto());
        assertEquals("Perecedero", productoPerecedero.getNombre());
        assertEquals(0, valorCompra.compareTo(productoPerecedero.getValorCompra()));
        assertEquals(0, porcentajeGanancia.compareTo(productoPerecedero.getPorcentajeGanancia()));
        assertEquals(stock, productoPerecedero.getStock());
        assertEquals(impuestoPruebas, productoPerecedero.getImpuesto());
        assertEquals(descuentoPruebas, productoPerecedero.getDescuento());
        assertEquals(fechaVencimiento, productoPerecedero.getFechaVencimiento());
        assertEquals(politicaVPruebas, productoPerecedero.getPoliticaVencimiento());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(servicioPoliticaVencimientoFalso).obtenerPoliticaVencimiento(idPoliticaV);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso, servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiElProductoYaEstaVencidoAlFabricarProductoPerecedero(){
        //ARRANGE
        int idImpuesto = 1;
        int idDescuento = 1;
        int idPoliticaV = 1;
        String nombre = "  Perecedero  ";
        BigDecimal valorCompra = new BigDecimal("3000");
        BigDecimal porcentajeGanancia = new BigDecimal("100");
        int stock = 25;
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaVencimiento = fechaActual.minusDays(1);
        String mensajeEsperado = "NO se puede Registrar el Producto porque ya está Vencido";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProductoPerecedero(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, fechaVencimiento,
                        idPoliticaV, fechaActual
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verifyNoInteractions(servicioImpuestosFalso, servicioDescuentosFalso, servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaPoliticaDeVencimientoNoExisteAlFabricarProductoPerecedero(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 1;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoPruebas);
        int idPoliticaV = 1;
        String mensajeEsperado = "NO Existe una Política de Vencimiento con el ID: " + idPoliticaV;
        when(servicioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPoliticaV))
                .thenThrow(new PoliticaVencimientoNoEncontradaException(mensajeEsperado));
        String nombre = "  Perecedero  ";
        BigDecimal valorCompra = new BigDecimal("3000");
        BigDecimal porcentajeGanancia = new BigDecimal("100");
        int stock = 25;
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaVencimiento = fechaActual.plusDays(5);
        //ACT AND ASSERT
        PoliticaVencimientoNoEncontradaException exception = assertThrows(
                PoliticaVencimientoNoEncontradaException.class,
                ()-> fabricaProductos.fabricarProductoPerecedero(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, fechaVencimiento,
                        idPoliticaV, fechaActual
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(servicioPoliticaVencimientoFalso).obtenerPoliticaVencimiento(idPoliticaV);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso, servicioPoliticaVencimientoFalso);
    }

    @Test
    void deberiaLanzarExcepcionSiLaPoliticaDeVencimientoEstaInactivaAlFabricarProductoPerecedero(){
        //ARRANGE
        int idImpuesto = 1;
        when(servicioImpuestosFalso.obtenerImpuesto(idImpuesto)).thenReturn(impuestoPruebas);
        int idDescuento = 1;
        when(servicioDescuentosFalso.obtenerDescuento(idDescuento)).thenReturn(descuentoPruebas);
        int idPoliticaV = 2;
        PoliticaVencimiento politicaVInactiva = PoliticaVencimiento.reconstruirDesdeBD(
                idPoliticaV, "Inactiva", 5, new BigDecimal("25"), false
        );
        when(servicioPoliticaVencimientoFalso.obtenerPoliticaVencimiento(idPoliticaV)).thenReturn(politicaVInactiva);
        String nombre = "  Perecedero  ";
        BigDecimal valorCompra = new BigDecimal("3000");
        BigDecimal porcentajeGanancia = new BigDecimal("100");
        int stock = 25;
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaVencimiento = fechaActual.plusDays(5);
        String mensajeEsperado = "NO se puede Asignar la Política de Vencimiento -" + politicaVInactiva.getNombre() +
                "- Porque se Encuentra Inactiva.";
        //ACT AND ASSERT
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> fabricaProductos.fabricarProductoPerecedero(
                        nombre, valorCompra, porcentajeGanancia, stock, idImpuesto, idDescuento, fechaVencimiento,
                        idPoliticaV, fechaActual
                )
        );
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(servicioImpuestosFalso).obtenerImpuesto(idImpuesto);
        verify(servicioDescuentosFalso).obtenerDescuento(idDescuento);
        verify(servicioPoliticaVencimientoFalso).obtenerPoliticaVencimiento(idPoliticaV);
        verifyNoMoreInteractions(servicioImpuestosFalso, servicioDescuentosFalso, servicioPoliticaVencimientoFalso);
    }

}//===================================================================================================================//

