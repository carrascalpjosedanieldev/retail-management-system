package RetailManagementSystem.aplicacion.orquestadores;

import RetailManagementSystem.aplicacion.dto.ventas.ProductoResumenDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOProducto;
import RetailManagementSystem.aplicacion.servicios.ServicioProductos;

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
            int idInventario, String codigoProducto, int cantidad, LocalDate fecha
    ){
        return this.ensambladorDTOProducto.ensamblarProductoResumen(
                this.servicioProductos.reducirStockDeProductoDeInventario(idInventario, codigoProducto, cantidad), fecha
        );
    }

    public void cambiarEstadoProducto(int idInventario, String codigoProducto){
        this.servicioProductos.cambiarEstadoProducto(idInventario, codigoProducto);
    }

}//===================================================================================================================//

