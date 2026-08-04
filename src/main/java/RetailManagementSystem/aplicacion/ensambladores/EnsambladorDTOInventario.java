package RetailManagementSystem.aplicacion.ensambladores;

import RetailManagementSystem.dominio.entidades.comercial.Inventario;
import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.aplicacion.dto.comercial.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.comercial.DatosTotalesProductoDTO;
import RetailManagementSystem.aplicacion.dto.comercial.DetalleInventarioDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOInventario {

    private final EnsambladorDTOProducto ensambladorDTOProducto;

    public EnsambladorDTOInventario(EnsambladorDTOProducto ensambladorDTOProducto) {
        this.ensambladorDTOProducto = ensambladorDTOProducto;
    }

    public DetalleInventarioDTO ensamblarDetalleInventario(Inventario inventario, List<Producto> productos, LocalDate fechaReferencia){
        List<DatosTotalesProductoDTO> datosProductosDeInventario = new ArrayList<>();
        for (Producto producto:productos){
            DatosTotalesProductoDTO datosTotalesProducto = this.ensambladorDTOProducto.ensamblarDatosTotalesProducto(producto, fechaReferencia);
            datosProductosDeInventario.add(datosTotalesProducto);
        }
        return new DetalleInventarioDTO(inventario.getIdInventario(), inventario.getNombre(), inventario.getCapacidadMaxima(), inventario.getCapacidadOcupada(), datosProductosDeInventario);
    }

    public InventarioDTO ensamblarDatosInventario(Inventario inventario){
        return new InventarioDTO(inventario.getIdInventario(), inventario.getNombre(), inventario.getCapacidadMaxima(), inventario.getCapacidadOcupada(), inventario.calcularCapacidadLibre());
    }

    public List<InventarioDTO> ensamblarDetalleInventarioGeneral(List<Inventario> inventarios){
        List<InventarioDTO> inventarioGeneral = new ArrayList<>();
        for (Inventario inventario:inventarios){
            inventarioGeneral.add(this.ensamblarDatosInventario(inventario));
        }
        return inventarioGeneral;
    }

}//===================================================================================================================//

