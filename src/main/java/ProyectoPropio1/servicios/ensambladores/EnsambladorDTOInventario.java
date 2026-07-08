package ProyectoPropio1.servicios.ensambladores;

import ProyectoPropio1.dominio.Inventario;
import ProyectoPropio1.dominio.Producto;
import ProyectoPropio1.dto.DatosInventarioDTO;
import ProyectoPropio1.dto.DatosTotalesProductoDTO;
import ProyectoPropio1.dto.DetalleInventarioDTO;

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

    public DatosInventarioDTO ensamblarDatosInventario(Inventario inventario){
        return new DatosInventarioDTO(inventario.getIdInventario(), inventario.getNombre(), inventario.getCapacidadMaxima(), inventario.getCapacidadOcupada(), inventario.calcularCapacidadLibre());
    }

    public List<DatosInventarioDTO> ensamblarDetalleInventarioGeneral(List<Inventario> inventarios){
        List<DatosInventarioDTO> inventarioGeneral = new ArrayList<>();
        for (Inventario inventario:inventarios){
            inventarioGeneral.add(this.ensamblarDatosInventario(inventario));
        }
        return inventarioGeneral;
    }

}

