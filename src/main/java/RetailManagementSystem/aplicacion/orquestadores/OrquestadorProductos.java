package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioProductos;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OrquestadorProductos {

    //ATRIBUTOS:

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    private final ServicioProductos servicioProductos;

    //CONSTRUCTOR:

    public OrquestadorProductos(EnsambladorDTOProducto ensambladorDTOProducto, ServicioProductos servicioProductos) {
        this.ensambladorDTOProducto = ensambladorDTOProducto;
        this.servicioProductos = servicioProductos;
    }

    //MÉTODOS:

    public List<ProductoResumenDTO> obtenerResumenProductosDeInventario(int idInventario, LocalDate fecha){
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarDetalleProductosResumen(
                this.servicioProductos.obtenerTodosLosProductosDeInventario(idInventario), contextoEvaluacion
        );
    }

    public void cambiarEstadoProducto(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.CAMBIAR_ESTADO_PRODUCTO);
        this.servicioProductos.cambiarEstadoProducto(idInventario, codigoProducto);
    }

    public List<DatosTotalesProductoRopaDTO> obtenerProductosRopaDeInventario(int idInventario, LocalDate fecha){
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarDetalleProductosRopa(
                this.servicioProductos.obtenerTodosLosProductosRopaDeInventario(idInventario), contextoEvaluacion
        );
    }

    public DatosTotalesProductoRopaDTO actualizarProductoRopaDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, String nombreNuevo,
            BigDecimal valorCompra, BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_PRODUCTO);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarDatosProductoRopa(
                this.servicioProductos.actualizarProductoRopaDeInventario(
                        idInventario, codigoProducto, nombreNuevo, valorCompra, porcentajeGanancia,
                        idImpuesto, idDescuento
                ), contextoEvaluacion
        );
    }

    public List<DatosTotalesProductoPerecederoDTO> obtenerProductosPerecederosDeInventario(
            int idInventario, LocalDate fecha
    ) {
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarDetalleProductosPerecedero(
                this.servicioProductos.obtenerTodosLosProductosPerecederoDeInventario(idInventario), contextoEvaluacion
        );
    }

    public DatosTotalesProductoPerecederoDTO actualizarProductoPerecederoDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, String nombreNuevo,
            BigDecimal valorCompra, BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento,
            int idPoliticaVencimiento, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.EDITAR_PRODUCTO);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarDatosProductoPerecedero(
                this.servicioProductos.actualizarProductoPerecederoDeInventario(
                        idInventario, codigoProducto, nombreNuevo, valorCompra, porcentajeGanancia,
                        idImpuesto, idDescuento, idPoliticaVencimiento
                ) , contextoEvaluacion
        );
    }

}//===================================================================================================================//

