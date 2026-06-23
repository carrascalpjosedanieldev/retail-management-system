package ProyectoPropio1.dominio;

import ProyectoPropio1.dto.DatosLineaFacturaDTO;

public interface ItemFacturable {

    double getValorCobrado();

    DatosLineaFacturaDTO obtenerDatosLinea();

}

