package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoPerecederoDTO;
import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoRopaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
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
        return this.ensambladorDTOProducto.ensamblarDetalleProductosResumen(
                this.servicioProductos.obtenerProductosDeInventario(idInventario), fecha
        );
    }

    public ProductoResumenDTO reducirStockDeProductoDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, int cantidad, LocalDate fecha
    ){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_PRODUCTOS);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioProductos.reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidad), fecha
        );
    }

    public void cambiarEstadoProducto(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_PRODUCTOS);
        this.servicioProductos.cambiarEstadoProducto(idInventario, codigoProducto);
    }

    public List<DatosTotalesProductoRopaDTO> obtenerProductosRopaDeInventario(int idInventario, LocalDate fecha){
        return this.ensambladorDTOProducto.ensamblarDetalleProductosRopa(
                this.servicioProductos.obtenerProductosRopaDeInventario(idInventario), fecha
        );
    }

    public DatosTotalesProductoRopaDTO actualizarProductoRopaDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, String nombreNuevo,
            BigDecimal valorCompra, BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_PRODUCTOS);
        return this.ensambladorDTOProducto.ensamblarDatosProductoRopa(
                this.servicioProductos.actualizarProductoRopaDeInventario(
                        idInventario, codigoProducto, nombreNuevo, valorCompra, porcentajeGanancia,
                        idImpuesto, idDescuento
                ), LocalDate.now()
        );
    }

    public List<DatosTotalesProductoPerecederoDTO> obtenerProductosPerecederosDeInventario(int idInventario){
        return this.ensambladorDTOProducto.ensamblarDetalleProductosPerecedero(
                this.servicioProductos.obtenerProductosPerecederoDeInventario(idInventario)
        );
    }

    public DatosTotalesProductoPerecederoDTO actualizarProductoPerecederoDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, String nombreNuevo,
            BigDecimal valorCompra, BigDecimal porcentajeGanancia, int idImpuesto, int idDescuento,
            int idPoliticaVencimiento, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.ADMINISTRAR_PRODUCTOS);
        return this.ensambladorDTOProducto.ensamblarDatosProductoPerecedero(
                this.servicioProductos.actualizarProductoPerecederoDeInventario(
                        idInventario, codigoProducto, nombreNuevo, valorCompra, porcentajeGanancia,
                        idImpuesto, idDescuento, idPoliticaVencimiento
                ) , fecha
        );
    }

}//===================================================================================================================//

