package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.consultas.DatosGeneralesCreacionProductoDTO;
import RetailManagementSystem.aplicacion.dto.consultas.DetallePerecederoDTO;
import RetailManagementSystem.aplicacion.dto.consultas.DetalleRopaDTO;
import RetailManagementSystem.aplicacion.dto.consultas.FormularioProductoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.comercial.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.fabricas.FabricaProductos;
import RetailManagementSystem.aplicacion.servicios.gestion.ServicioGestionStock;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.dominio.enums.TipoProducto;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;

import java.time.LocalDate;

public class OrquestadorGestionStock {

    //ATRIBUTOS:

    private final ServicioGestionStock servicioGestionStock;

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    private final FabricaProductos fabricaProductos;

    //CONSTRUCTOR:

    public OrquestadorGestionStock(
            ServicioGestionStock servicioGestionStock, EnsambladorDTOProducto ensambladorDTOProducto,
            FabricaProductos fabricaProductos
    ) {
        this.servicioGestionStock = servicioGestionStock;
        this.ensambladorDTOProducto = ensambladorDTOProducto;
        this.fabricaProductos = fabricaProductos;
    }

    //MÉTODOS:

    public ProductoResumenDTO validarEspacioInventarioYGuardarProducto(
            UsuarioDTOCompleto usuario, int idInventario, FormularioProductoDTO datosProducto, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_PRODUCTOS);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        Producto producto = ensamblarProductoSegunElTipo(datosProducto, contextoEvaluacion);
        this.servicioGestionStock.registrarProductoEnInventario(idInventario, producto);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(producto, contextoEvaluacion);
    }

    private Producto ensamblarProductoSegunElTipo(
            FormularioProductoDTO datosProducto, ContextoEvaluacion contextoEvaluacion
    ) {
        DatosGeneralesCreacionProductoDTO general = datosProducto.datosGenerales();
        switch (general.tipoProducto()){
            case TipoProducto.ROPA -> {
                DetalleRopaDTO dto = (DetalleRopaDTO) datosProducto.detalle();
                return this.fabricaProductos.fabricarProductoRopa(
                        general.nombre(), general.valorCompra(), general.ganancia(), general.stock(),
                        general.idImpuesto(), general.idDescuento(), dto.talla()
                );
            }
            case TipoProducto.PERECEDERO -> {
                DetallePerecederoDTO dto = (DetallePerecederoDTO) datosProducto.detalle();
                return this.fabricaProductos.fabricarProductoPerecedero(
                        general.nombre(), general.valorCompra(), general.ganancia(), general.stock(),
                        general.idImpuesto(), general.idDescuento(), dto.fechaVencimiento(),
                        dto.idPoliticaVencimiento(), contextoEvaluacion
                );
            }
            default -> throw new IllegalArgumentException("Tipo de Producto NO Soportado");
        }
    }

    public ProductoResumenDTO validarEspacioInventarioYAumentarStockProducto(
            UsuarioDTOCompleto usuario, int idInventario, int cantidadAAumentarProducto, String codigoProducto,
            LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.REGISTRAR_PRODUCTOS);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioGestionStock.aumentarStockDeProductoDeInventario(
                        idInventario, codigoProducto, cantidadAAumentarProducto
                ) , contextoEvaluacion
        );
    }

    public ProductoResumenDTO reducirStockProductoDeInventario(
            UsuarioDTOCompleto usuario, int idInventario, String codigoProducto, int cantidad, LocalDate fecha
    ) {
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.MANEJAR_STOCK_PRODUCTO);
        ContextoEvaluacion contextoEvaluacion = ContextoEvaluacion.crearNuevo(fecha);
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioGestionStock.reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidad),
                contextoEvaluacion
        );
    }

    public void validarEspacioInventarioYMoverProducto(
            UsuarioDTOCompleto usuario, int idInventarioSalida, int idInventarioDestino, String codigoProducto,
            int stockAMover
    ){
        ValidadorSeguridad.exigirPermiso(usuario, PermisosApp.TRASLADAR_PRODUCTOS);
        this.servicioGestionStock.moverProductoAInventario(
                idInventarioSalida, idInventarioDestino, codigoProducto, stockAMover
        );
    }

}//===================================================================================================================//

