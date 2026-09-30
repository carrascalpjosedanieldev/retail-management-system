package RetailManagementSystem.aplicacion.servicios.ventas;

import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioProductos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioServicios;
import RetailManagementSystem.dominio.entidades.comercial.ItemFacturable;
import RetailManagementSystem.dominio.entidades.comercial.ProductoPerecedero;
import RetailManagementSystem.dominio.entidades.ventas.Carrito;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.enums.TipoItem;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ProductoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.recursosNoEncontrados.ServicioNoEncontradoException;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ServicioCarrito {

    //ATRIBUTOS:

    private final CalculadoraPrecios calculadoraPrecios;

    private final ServicioProductos servicioProductos;

    private final ServicioServicios servicioServicios;

    //CONSTRUCTOR:

    public ServicioCarrito(
            CalculadoraPrecios calculadoraPrecios, ServicioProductos servicioProductos,
            ServicioServicios servicioServicios
    ) {
        this.calculadoraPrecios = calculadoraPrecios;
        this.servicioProductos = servicioProductos;
        this.servicioServicios = servicioServicios;
    }

    //MÉTODOS:

    public void agregarItemNuevoAlCarrito(
            Carrito carrito, String codigo, int cantidad, ContextoEvaluacion contextoEvaluacion
    ) {
        if (contextoEvaluacion.getFechaEvaluacion().isEmpty()){
            throw new IllegalStateException("Se Requiere una Fecha para Calcular el Valor del Producto");
        }
        ItemFacturable item = resolverItemPorCodigo(codigo, contextoEvaluacion.getFechaEvaluacion().get());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(item, contextoEvaluacion);
        carrito.agregarItem(item, valorVenta, cantidad);
    }

    private ItemFacturable resolverItemPorCodigo(String codigo, LocalDate fecha) {
        try {
            return obtenerYValidarProducto(codigo, fecha);
        } catch (ProductoNoEncontradoException e) {
            try {
                return this.servicioServicios.obtenerServicioActivoParaLaVenta(codigo);
            } catch (ServicioNoEncontradoException ex) {
                throw new IllegalArgumentException(
                        "El Código -" + codigo + "- NO pertenece a un Producto ni a un Servicio Activo."
                );
            }
        }
    }

    private Producto obtenerYValidarProducto(String codigo, LocalDate fecha) {
        Producto producto = this.servicioProductos.obtenerProductoActivoParaLaVenta(codigo);
        if (producto instanceof ProductoPerecedero perecedero) {
            perecedero.validarEstadoParaVenta(fecha);
        }
        return producto;
    }

    public void aumentarCantidadItemDeCarrito(
            Carrito carrito, String codigo, int cantidad, TipoItem tipoItem, ContextoEvaluacion contextoEvaluacion
    ){
        if (contextoEvaluacion.getFechaEvaluacion().isEmpty()){
            throw new IllegalStateException("Se Requiere una Fecha para Calcular el Valor del Producto");
        }
        ItemFacturable item = obtenerItemValido(codigo, tipoItem, contextoEvaluacion.getFechaEvaluacion().get());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(item, contextoEvaluacion);
        carrito.agregarItem(item, valorVenta, cantidad);
    }

    private ItemFacturable obtenerItemValido(String codigo, TipoItem tipo, LocalDate fecha) {
        return switch (tipo) {
            case PRODUCTO -> obtenerYValidarProducto(codigo, fecha);
            case SERVICIO -> this.servicioServicios.obtenerServicioActivoParaLaVenta(codigo);
        };
    }

    public void reducirCantidadItem(Carrito carrito, String codigo, int cantidadAReducir){
        carrito.reducirCantidadItem(codigo, cantidadAReducir);
    }

    public void eliminarItem(Carrito carrito, String codigo){
        carrito.eliminarItem(codigo);
    }

    public void cancelarCompraTotal(Carrito carrito){
        carrito.vaciarCarrito();
    }

}//===================================================================================================================//

