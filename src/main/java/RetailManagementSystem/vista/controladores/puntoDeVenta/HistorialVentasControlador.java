package RetailManagementSystem.vista.controladores.puntoDeVenta;

import RetailManagementSystem.aplicacion.dto.ventas.ReporteRecaudoDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOFactura;
import RetailManagementSystem.aplicacion.servicios.ServicioFacturas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

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
            GestorAlertas.mostrarAlertaWarning(
                    "Campos incompletos", null,
                    "Por favor, Seleccione ambas Fechas."
            );
            return;
        }
        if (fechaInicio.isAfter(fechaFin)) {
            GestorAlertas.mostrarAlertaWarning(
                    "Rango inválido", null,
                    "La Fecha de Inicio NO puede ser Mayor a la Fecha de Fin."
            );
            return;
        }
        lblCantidad.setText("...");
        lblTotalGeneral.setText("Calculando...");
        btnGenerar.setDisable(true);
        CompletableFuture.supplyAsync(()->{
            return this.ensambladorDTOFactura.ensamblarReporteRecaudo(
                    servicioFacturas.obtenerReporteRecaudo(fechaInicio, fechaFin)
            );
        }).thenAccept(reporteRecaudo ->
            Platform.runLater(()->{
                actualizarTarjetas(reporteRecaudo);
                btnGenerar.setDisable(false);
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error en los Datos Ingresados", null,
                        "Hubo un Problema al Generar el Reporte\n" + "Error:  " + causa.getMessage()
                );
                btnGenerar.setDisable(false);
            });
            return null;
        });
    }

    private void actualizarTarjetas(ReporteRecaudoDTO reporte) {
        lblCantidad.setText(String.valueOf(reporte.cantidadFacturasEmitidas()));
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(reporte.subTotal()));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(reporte.totalImpuestos()));
        lblTotalGeneral.setText(FormateadorNumeros.formatoMoneda(reporte.totalRecaudo()));
    }


    @FXML
    public void cerrarModal(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


}//===================================================================================================================//

