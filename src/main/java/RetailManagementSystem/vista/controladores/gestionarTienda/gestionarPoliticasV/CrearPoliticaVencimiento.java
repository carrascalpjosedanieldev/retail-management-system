package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

public class CrearPoliticaVencimiento {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtDiasUmbral;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;

    private ObservableList<PoliticaVencimientoDTO> listaObservable;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public CrearPoliticaVencimiento(OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento) {
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, ObservableList<PoliticaVencimientoDTO> listaObservable
    ) {
        if (usuarioActual == null){
            throw new IllegalArgumentException("Usuario Nulo, Error al Recibir el Usuario");
        }
        if (!usuarioActual.tienePermiso(PermisosApp.REGISTRAR_POLITICAS_V)){
            GestorAlertas.mostrarAlertaError(
                    getVentana(),
                    "Acceso Denegado", "Privilegios Insuficientes",
                    "NO tienes los Permisos Necesarios para Registrar Políticas de Vencimiento."
            );
            return;
        }
        this.usuarioActual = usuarioActual;
        this.listaObservable = listaObservable;
    }

    private Window getVentana(){
        return btnCancelar.getScene() != null ? btnCancelar.getScene().getWindow() : null;
    }


    @FXML
    private void guardarPoliticaV(ActionEvent event) {
        String nombre = txtNombre.getText().trim();
        String porcentajeTexto = txtPorcentaje.getText().trim();
        String diasUmbralTexto = txtDiasUmbral.getText().trim();
        boolean activo = chkActivo.isSelected();
        if (nombre.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Error de Validación", null,
                    "El Nombre de la Política NO puede estar Vacío."
            );
            return;
        }
        if (porcentajeTexto.isEmpty() || diasUmbralTexto.isEmpty()) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Error de Validación", null,
                    "El Porcentaje y los Dias Umbral NO pueden estar Vacíos."
            );
            return;
        }
        BigDecimal porcentaje;
        try {
            porcentaje = FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
        } catch (IllegalArgumentException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Número Inválido", null,
                    "Error al Ingresar el Porcentaje:\n" + e.getMessage()
            );
            return;
        }
        int diasUmbral;
        try {
            diasUmbral = Integer.parseInt(diasUmbralTexto);
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Número Inválido", null,
                    "Los Días Umbral deben ser un Número Entero válido."
            );
            return;
        }
        CompletableFuture.supplyAsync(()->
            this.orquestadorPoliticaVencimiento.registrarPoliticaVencimiento(
                    this.usuarioActual, nombre, diasUmbral, porcentaje, activo
            )
        ).thenAccept(politicaVRegistrada ->
            Platform.runLater(()->{
                listaObservable.add(politicaVRegistrada);
                cerrarPantalla();
            })
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
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

