package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarInventarios;

import RetailManagementSystem.aplicacion.dto.gestion.InventarioDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorInventarios;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.configuracion.ConfiguradorExcepciones;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import RetailManagementSystem.vista.utilidades.UtilidadesLista;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;

import java.util.concurrent.CompletableFuture;

public class AumentarCapacidadControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Label lblIdInventario;
    @FXML private Label lblNombreInventario;
    @FXML private TextField txtCapacidadExtra;

    private final OrquestadorInventarios orquestadorInventarios;

    private UsuarioDTOCompleto usuarioActual;

    private int idInventario;

    private ObservableList<InventarioDTO> listaObservable;

    //CONSTRUCTOR:

    public AumentarCapacidadControlador(OrquestadorInventarios orquestadorInventarios) {
        this.orquestadorInventarios = orquestadorInventarios;
    }

    //MÉTODOS:

    public void cargarDatos(
            UsuarioDTOCompleto usuarioActual, InventarioDTO datosInventario, ObservableList<InventarioDTO> listaObservable
    ) {
        ValidadorSeguridad.exigirPermiso(usuarioActual, PermisosApp.AUMENTAR_CAPACIDAD_MAXIMA_INVENTARIO);
        this.usuarioActual = usuarioActual;
        if (datosInventario == null) {
            throw new IllegalArgumentException("NO puedes editar un Inventario Vacío.");
        }
        lblIdInventario.setText(String.valueOf(datosInventario.idInventario()));
        lblNombreInventario.setText(datosInventario.nombre());
        this.idInventario = datosInventario.idInventario();
        this.listaObservable = listaObservable;
        Platform.runLater(()->btnCancelar.requestFocus());
    }

    private Window getVentana(){
        return btnGuardar.getScene() != null ? btnGuardar.getScene().getWindow() : null;
    }


    @FXML
    void initialize(){
        TextFormatter<String> filtroPositivo = new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        });
        txtCapacidadExtra.setTextFormatter(filtroPositivo);
    }


    @FXML
    void guardarCambios(ActionEvent event) {
        String capacidadExtraSt = txtCapacidadExtra.getText().trim();
        if (capacidadExtraSt.isBlank()){
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Debes Ingresar la Capacidad Extra", null ,
                    "Por Favor, Ingresa una Cantidad Extra Valida"
            );
            return;
        }
        try {
            int capacidadExtra = Integer.parseInt(capacidadExtraSt);
            CompletableFuture.supplyAsync(()->
                    this.orquestadorInventarios.aumentarCapacidadMaxima(this.usuarioActual, this.idInventario, capacidadExtra)
            ).thenAccept(actualizado ->
                Platform.runLater(()->{
                    UtilidadesLista.reemplazarPorIdentidad(
                            listaObservable,
                            actualizado,
                            item -> item.idInventario().equals(actualizado.idInventario())
                    );
                    GestorAlertas.mostrarAlertaInformacion(
                            getVentana(), "Éxito", null,
                            "La Capacidad Maxima del Inventario se ha Actualizado con Éxito."
                    );
                    cerrarModal();
                })
            ).exceptionally(ex->{
               Platform.runLater(()->{
                   Throwable causa = ConfiguradorExcepciones.obtenerCausaRaiz(ex);
                   if (causa instanceof AccesoDenegadoException accesoDenegadoException){
                       GestorAlertas.mostrarAlertaAccesoDenegado(
                               getVentana(),
                               accesoDenegadoException
                       );
                   } else if (causa instanceof IllegalArgumentException){
                       GestorAlertas.mostrarAlertaError(
                               getVentana(), "Error en los Datos Ingresados", null,
                               "Error:  " + causa.getMessage()
                       );
                   } else {
                       GestorAlertas.mostrarAlertaError(
                               getVentana(), "Error Critico",
                               "NO se pudo Completar la Acción.",
                               "Notificale al Administrador este Error:\n" + causa.getMessage()
                       );
                   }
               });
               return null;
            });
        } catch (NumberFormatException e) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Capacidad Extra Invalida", null,
                    "Escribe la Capacidad Extra como un Numero Entero"
            );
        }
    }


    @FXML
    void cerrarVentana(ActionEvent event) {
        cerrarModal();
    }

    private void cerrarModal(){
        Window ventana = getVentana();
        if (ventana != null){
            ventana.hide();
        }
    }

}//===================================================================================================================//

