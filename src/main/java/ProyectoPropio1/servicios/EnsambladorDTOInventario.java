package ProyectoPropio1.servicios;

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

    public DetalleInventarioDTO ensamblarDetalleInventario(Inventario inventario, LocalDate fechaReferencia){
        List<DatosTotalesProductoDTO> datosProductosDeInventario = new ArrayList<>();
        for (Producto producto:inventario.getMisProductos().values()){
            DatosTotalesProductoDTO datosTotalesProducto = this.ensambladorDTOProducto.ensamblarDatosTotalesProducto(producto, fechaReferencia);
            datosProductosDeInventario.add(datosTotalesProducto);
        }
        return new DetalleInventarioDTO(inventario.getNumeroId(), inventario.getNombre(), inventario.getCapacidadMaxima(), inventario.getCapacidadOcupada(), datosProductosDeInventario);
    }

    public DatosInventarioDTO ensamblarDatosInventario(Inventario inventario){
        return new DatosInventarioDTO(inventario.getNumeroId(), inventario.getNombre(), inventario.getCapacidadMaxima(), inventario.getCapacidadOcupada(), inventario.calcularCapacidadLibre());
    }

}

