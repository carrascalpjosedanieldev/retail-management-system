package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarDescuentos;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.UtilidadesLista;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class EditarDescuentoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Label lblNombreDescuento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private DescuentoDTO datosDescuento;

    private ObservableList<DescuentoDTO> listaObservable;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EditarDescuentoControlador(OrquestadorDescuentos orquestadorDescuentos) {
        this.orquestadorDescuentos = orquestadorDescuentos;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, DescuentoDTO datosDescuento, ObservableList<DescuentoDTO> listaObservable
    ) {
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.MODIFICAR_DESCUENTOS);
        if (datosDescuento == null) {
            throw new IllegalArgumentException("No puedes editar un Descuento Vacío.");
        }
        this.usuarioActual = usuarioActual;
        this.datosDescuento = datosDescuento;
        this.listaObservable = listaObservable;
        lblNombreDescuento.setText(this.datosDescuento.nombre());
        txtNombre.setText(this.datosDescuento.nombre());
        txtPorcentaje.setText(this.datosDescuento.porcentaje().toString());
        Platform.runLater(() -> btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    private void actualizarDescuento(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
        if (nombre.isEmpty() || nuevoPorcentajeTexto.isEmpty()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Datos Incompletos",
                    "El Nombre y el Porcentaje son Obligatorios.",
                    "Por favor escribe un Nombre y un Porcentaje Validos."
            );
            return;
        }
        BigDecimal porcentaje;
        try {
            porcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Número Inválido", null,
                    "Error al Ingresar el Porcentaje:\n" + e.getMessage()
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorDescuentos.actualizarDescuento(
                    this.usuarioActual, this.datosDescuento.idDescuento(), nombre, porcentaje
            )
        ).thenAccept(descuentoActualizado ->
            Platform.runLater(() -> {
                UtilidadesLista.reemplazarPorIdentidad(
                        listaObservable,
                        descuentoActualizado,
                        item-> item.idDescuento() == descuentoActualizado.idDescuento()
                );
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Descuento se ha Actualizado con Éxito."
                );
                cerrarPantalla();
            })
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                cerrarPantalla();
            });
            return null;
        });
    }

    private void cerrarPantalla(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }


    @FXML
    private void cancelar(ActionEvent event) {
        cerrarPantalla();
    }

}//===================================================================================================================//

