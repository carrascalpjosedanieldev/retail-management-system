package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.servicios.ServicioGestionStock;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.time.LocalDate;

public class OrquestadorGestionStock {

    //ATRIBUTOS:

    private final ServicioProductos servicioProductos;
    private final ServicioGestionStock servicioGestionStock;

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    //CONSTRUCTOR:

    public OrquestadorGestionStock(
            ServicioProductos servicioProductos, ServicioGestionStock servicioGestionStock,
            EnsambladorDTOProducto ensambladorDTOProducto
    ) {
        this.servicioProductos = servicioProductos;
        this.servicioGestionStock = servicioGestionStock;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
    }

    //MÉTODOS:

    public ProductoResumenDTO validarEspacioInventarioYGuardarProducto(
            UsuarioDTOCompleto usuario, int idInventario, Producto producto, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_PRODUCTOS);
        this.servicioGestionStock.registrarProductoEnInventario(idInventario, producto);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(producto, fecha);
    }

    public ProductoResumenDTO validarEspacioInventarioYAumentarStockProducto(
            UsuarioDTOCompleto usuario, int idInventario, int cantidadAAumentarProducto, String codigoProducto,
            LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_PRODUCTOS);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioGestionStock.aumentarStockDeProductoDeInventario(
                        idInventario, codigoProducto, cantidadAAumentarProducto
                ) , fecha
        );
    }

    public ProductoResumenDTO reducirStockProductoDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, int cantidad, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.MANEJAR_STOCK_PRODUCTO);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioGestionStock.reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidad), fecha
        );
    }

    public void validarEspacioInventarioYMoverProducto(
            UsuarioDTOCompleto usuario, int idInventarioSalida, int idInventarioDestino, String codigoProducto,
            int stockProducto
    ){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.TRASLADAR_PRODUCTOS);
        this.servicioProductos.moverProductoAInventario(idInventarioSalida, idInventarioDestino, codigoProducto);
    }

}//===================================================================================================================//

