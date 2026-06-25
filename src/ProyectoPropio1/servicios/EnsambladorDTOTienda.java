package ProyectoPropio1.servicios;

import ProyectoPropio1.dominio.Inventario;
import ProyectoPropio1.dominio.Servicio;
import ProyectoPropio1.dominio.Tienda;
import ProyectoPropio1.dto.DatosCatalogoServiciosDTO;
import ProyectoPropio1.dto.DatosInventarioDTO;
import ProyectoPropio1.dto.DatosServicioDTO;
import ProyectoPropio1.dto.DetalleInventarioGeneralDTO;

import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOTienda {

    private final EnsambladorDTOServicio ensambladorDTOServicio;

    private final EnsambladorDTOInventario ensambladorDTOInventario;

    public EnsambladorDTOTienda(EnsambladorDTOServicio ensambladorDTOServicio, EnsambladorDTOInventario ensambladorDTOInventario) {
        this.ensambladorDTOServicio = ensambladorDTOServicio;
        this.ensambladorDTOInventario = ensambladorDTOInventario;
    }

    public DatosCatalogoServiciosDTO ensamblarDatosCatalogoServicios(Tienda tienda){
        List<DatosServicioDTO> listaServicios = new ArrayList<>();
        for (Servicio servicio: tienda.getServiciosOfrecidos()){
            listaServicios.add(this.ensambladorDTOServicio.ensamblarServicio(servicio));
        }
        return new DatosCatalogoServiciosDTO(listaServicios);
    }

    public DetalleInventarioGeneralDTO ensamblarDetalleInventarioGeneral(Tienda tienda){
        List<DatosInventarioDTO> inventarioGeneral = new ArrayList<>();
        for (Inventario inventario:tienda.getMisInventarios()){
            inventarioGeneral.add(this.ensambladorDTOInventario.ensamblarDatosInventario(inventario));
        }
        return new DetalleInventarioGeneralDTO(inventarioGeneral);
    }

}

