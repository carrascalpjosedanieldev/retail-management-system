package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarConfiguraciones.politicasDeBloqueo;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorConfiguraciones;

import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class EditarPoliticasBloqueoControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Label lblDescripcionMaxErrores;
    @FXML private Label lblDescripcionTiempoBloqueo;
    @FXML private TextField txtIntentosMaximos;
    @FXML private TextField txtTiempoBloqueo;

    private final OrquestadorConfiguraciones orquestadorConfiguraciones;

    private UsuarioDTOCompleto usuarioActual;

    //CONSTRUCTOR:

    public EditarPoliticasBloqueoControlador(OrquestadorConfiguraciones orquestadorConfiguraciones) {
        this.orquestadorConfiguraciones = orquestadorConfiguraciones;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual
    ){
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.EDITAR_PERFIL_DE_TIENDA);
        this.usuarioActual = usuarioActual;
        cargarDatos();
    }

    private void cargarDatos(){
        CompletableFuture<ConfiguracionSistemaDTO> maxIntentos = CompletableFuture.supplyAsync(
                this.orquestadorConfiguraciones::obtenerMaxIntentosBloqueo
        );
        CompletableFuture<ConfiguracionSistemaDTO> maxMinutosBloqueo = CompletableFuture.supplyAsync(
                this.orquestadorConfiguraciones::obtenerMaxMinutosBloqueo
        );
        maxIntentos.thenCombine(
                maxMinutosBloqueo,
                (confMaxIntentos, confMaxMinutosBloqueo)->{
                    Platform.runLater(()->{
                        txtIntentosMaximos.setText(confMaxIntentos.valor());
                        lblDescripcionMaxErrores.setText(confMaxIntentos.descripcion());
                        txtTiempoBloqueo.setText(confMaxMinutosBloqueo.valor());
                        lblDescripcionTiempoBloqueo.setText(confMaxMinutosBloqueo.descripcion());
                        btnCancelar.requestFocus();
                    });
                    return null;
                }
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Crítico",
                        "NO se pudo Cargar los Datos de las Políticas de Bloqueo.",
                        "Se Cerrara la Ventana por Seguridad. Notifícale al Administrador este Error:\n" +
                                causa.getMessage()
                );
                cerrarPantalla();
            });
            return null;
        });
    }

    private Window getVentana(){
        return btnGuardar.getScene() != null ? btnGuardar.getScene().getWindow() : null;
    }


    @FXML
    private void initialize(){
        txtIntentosMaximos.setTextFormatter(
                new TextFormatter<>(change -> {
                    String texto = change.getControlNewText();
                    return texto.matches("([1-9][0-9]*)?") ? change : null;
                })
        );
        txtTiempoBloqueo.setTextFormatter(
                new TextFormatter<>(change -> {
                    String texto = change.getControlNewText();
                    return texto.matches("([1-9][0-9]*)?") ? change : null;
                })
        );
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        String intentosMax = txtIntentosMaximos.getText();
        String tiempoMax = txtTiempoBloqueo.getText();
        if (intentosMax.isBlank() || tiempoMax.isBlank()){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Campos Incompletos", null,
                    "Por favor, Completa Todos los Campos, NO pueden estar vacíos."
            );
            return;
        }
        int maxIntentos, maxTiempo;
        try {
            maxIntentos = Integer.parseInt(intentosMax);
            maxTiempo = Integer.parseInt(tiempoMax);
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(),
                    "Datos Inválidos", null,
                    "Escribe solo Números enteros Positivos."
            );
            return;
        }
        if (maxIntentos <= 0 || maxTiempo <=0){
            GestorAlertas.mostrarAlertaError(
                    getVentana(), "Datos Inválidos", null,
                    "Por favor, Escribe Números Positivos para los Intentos y los Minutos de Bloqueo"
            );
            return;
        }
        CompletableFuture<Void> guardarMaxIntentos = CompletableFuture.runAsync(()->
                this.orquestadorConfiguraciones.actualizarMaxIntentos(this.usuarioActual, maxIntentos)
        );
        CompletableFuture<Void> guardarMaxTiempo = CompletableFuture.runAsync(()->
                this.orquestadorConfiguraciones.actualizarMaxMinutosBloqueos(this.usuarioActual, maxTiempo)
        );
        guardarMaxIntentos.thenCombine(
                guardarMaxTiempo,
                (guardadoIntentos, guardadoTiempo)->{
                    Platform.runLater(()->{
                        GestorAlertas.mostrarAlertaInformacion(
                                getVentana(), "Éxito", null,
                                "Las Políticas de Bloqueo se han actualizado Exitosamente."
                        );
                        cerrarPantalla();
                    });
                    return null;
                }
        ).exceptionally(ex->{
            Platform.runLater(()->{
                Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                if (causa instanceof AccesoDenegadoException){
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Crítico",
                            "NO se pudieron Actualizar las Políticas de Bloqueo.",
                            "Se Cerrara la Ventana por Seguridad.\n" +
                                    causa.getMessage()
                    );
                    cerrarPantalla();
                } else {
                    GestorAlertas.mostrarAlertaError(
                            getVentana(), "Error Crítico",
                            "NO se pudo Completar la Acción.",
                            "Notifícale al Administrador este Error:\n" + causa.getMessage()
                    );
                }
            });
            return null;
        });
    }


    @FXML
    void cerrarVentana(ActionEvent event) {
        cerrarPantalla();
    }

    private void cerrarPantalla(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//

