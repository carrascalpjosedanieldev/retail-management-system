package ProyectoPropio1.vista.controladores.puntoDeVenta;

import ProyectoPropio1.aplicacion.dto.ReporteRecaudoDTO;
import ProyectoPropio1.aplicacion.ensambladores.EnsambladorDTOFactura;
import ProyectoPropio1.aplicacion.servicios.ServicioFacturas;
import ProyectoPropio1.vista.utilidades.FormateadorNumeros;
import ProyectoPropio1.vista.utilidades.RutasVista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;

public class HistorialVentasControlador {

    //ATRIBUTOS:

    @FXML public DatePicker dpFechaInicio;
    @FXML public DatePicker dpFechaFin;
    @FXML public Button btnGenerar;
    @FXML public Button btnCerrar;
    @FXML public Label lblCantidad;
    @FXML public Label lblSubtotal;
    @FXML public Label lblImpuestos;
    @FXML public Label lblTotalGeneral;

    private final ServicioFacturas servicioFacturas;

    private final EnsambladorDTOFactura ensambladorDTOFactura;

    //CONSTRUCTOR:

    public HistorialVentasControlador(ServicioFacturas servicioFacturas, EnsambladorDTOFactura ensambladorDTOFactura) {
        this.servicioFacturas = servicioFacturas;
        this.ensambladorDTOFactura = ensambladorDTOFactura;
    }

    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText("");
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(Region.USE_PREF_SIZE);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_HISTORIAL_VENTAS);
        if (urlCss != null) {
            pane.getStylesheets().add(urlCss.toExternalForm());
        }
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        dpFechaInicio.setValue(LocalDate.now());
        dpFechaFin.setValue(LocalDate.now());
    }


    @FXML
    public void generarReporte(ActionEvent event) {
        generarReporte();
    }

    private void generarReporte(){
        LocalDate fechaInicio = dpFechaInicio.getValue();
        LocalDate fechaFin = dpFechaFin.getValue();
        if (fechaInicio == null || fechaFin == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos",
                    "Por favor, seleccione ambas fechas.");
            return;
        }
        if (fechaInicio.isAfter(fechaFin)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Rango inválido",
                    "La Fecha de Inicio NO puede ser Mayor a la Fecha de Fin.");
            return;
        }
        try {
            ReporteRecaudoDTO reporte = this.ensambladorDTOFactura.ensamblarReporteRecaudo(
                    servicioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin)
            );
            actualizarTarjetas(reporte);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR,"Error de Consulta",
                    "Hubo un Problema al Generar el Reporte\n" +
                            "Error:  " + e.getMessage());
        }
    }

    private void actualizarTarjetas(ReporteRecaudoDTO reporte) {
        lblCantidad.setText(String.valueOf(reporte.cantidadFacturasEmitidas()));
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(reporte.subTotal()));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(reporte.totalImpuestos()));
        lblTotalGeneral.setText(FormateadorNumeros.formatoMoneda(reporte.totalRecaudo()));
    }


    @FXML
    public void cerrarModal(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }


}//===================================================================================================================//

