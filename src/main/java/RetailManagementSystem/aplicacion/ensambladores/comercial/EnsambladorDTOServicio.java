package RetailManagementSystem.aplicacion.ensambladores.comercial;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTODescuento;
import RetailManagementSystem.aplicacion.ensambladores.gestion.EnsambladorDTOImpuesto;
import RetailManagementSystem.dominio.financiero.calculos.CalculadoraPrecios;
import RetailManagementSystem.dominio.entidades.comercial.Servicio;
import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.dominio.financiero.calculos.ContextoEvaluacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EnsambladorDTOServicio {

    //ATRIBUTOS:

    private final CalculadoraPrecios calculadoraPrecios;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    //CONSTRUCTOR:

    public EnsambladorDTOServicio(
            CalculadoraPrecios calculadoraPrecios, EnsambladorDTOImpuesto ensambladorDTOImpuesto,
            EnsambladorDTODescuento ensambladorDTODescuento
    ) {
        this.calculadoraPrecios = calculadoraPrecios;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }

    //MÉTODOS:

    public ServicioDTO ensamblarServicio(Servicio servicio, ContextoEvaluacion contextoEvaluacion){
        ImpuestoDTO datosImpuesto = this.ensambladorDTOImpuesto.ensamblarDatosImpuesto(servicio.getImpuesto());
        DescuentoDTO datosDescuento = this.ensambladorDTODescuento.ensamblarDatosDescuento(servicio.getDescuento());
        BigDecimal valorVenta = this.calculadoraPrecios.calcularValorVenta(servicio, contextoEvaluacion);
        return new ServicioDTO(
                servicio.getCodigo(), servicio.getNombre(), servicio.getPrecioBase(), valorVenta,
                servicio.isActivo(), datosImpuesto, datosDescuento
        );
    }

    public List<ServicioDTO> ensamblarDatosCatalogoServicios(
            List<Servicio> servicios, ContextoEvaluacion contextoEvaluacion
    ) {
        List<ServicioDTO> listaServicios = new ArrayList<>();
        for (Servicio servicio: servicios){
            listaServicios.add(this.ensamblarServicio(servicio, contextoEvaluacion));
        }
        return listaServicios;
    }

}//===================================================================================================================//

