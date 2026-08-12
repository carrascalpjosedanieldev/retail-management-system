package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV;

import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
import RetailManagementSystem.dominio.excepciones.PoliticaVencimientoNoEncontradaException;
import RetailManagementSystem.vista.controladores.gestionarTienda.gestionarImpuestos.EditarImpuestoControlador;
import RetailManagementSystem.vista.excepciones.CargarVistaException;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.GestorAlertas;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GestionPoliticasVencimientoControlador {

    //ATRIBUTOS:

    @FXML private TableView<PoliticaVencimientoDTO> tablaPoliticasVencimiento;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colId;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colNombre;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colDiasUmbral;
    @FXML private TableColumn<PoliticaVencimientoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    private final ObservableList<PoliticaVencimientoDTO> listaObservablePoliticasVencimiento = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionPoliticasVencimientoControlador(OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento) {
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
    }

    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        alerta.showAndWait();
    }


    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idPoliticaVencimiento())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombrePolitica())
        );
        colDiasUmbral.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().diasUmbral())
        );
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().porcentajeDescuento())
        );
        colEstado.setCellValueFactory(celda -> {
            boolean esActiva = celda.getValue().activo();
            String textoEstado = esActiva ? "Activa" : "Inactiva";
            return new SimpleStringProperty(textoEstado);
        });
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                getStyleClass().removeAll("estado-activo", "estado-inactivo");
                if (empty || estado == null) {
                    setText(null);
                } else {
                    setText(estado);
                    String estiloCss = estado.equalsIgnoreCase("Activa") ? "estado-activo" : "estado-inactivo";
                    getStyleClass().add(estiloCss);
                }
            }
        });
    }

    private void configurarFiltroBusqueda() {
        FilteredList<PoliticaVencimientoDTO> listaFiltrada = new FilteredList<>(
                listaObservablePoliticasVencimiento, b -> true
        );
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(politicaVencimiento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                return String.valueOf(politicaVencimiento.idPoliticaVencimiento()).contains(filtro) ||
                        politicaVencimiento.nombrePolitica().toLowerCase().contains(filtro);
            });
        });
        SortedList<PoliticaVencimientoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaPoliticasVencimiento.comparatorProperty());
        tablaPoliticasVencimiento.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        CompletableFuture.supplyAsync(
                this.orquestadorPoliticaVencimiento::obtenerTodasLasPoliticasV
        ).thenAccept(listaPoliticasV -> {
            Platform.runLater(()->{
                listaObservablePoliticasVencimiento.setAll(listaPoliticasV);
            });
        }).exceptionally(ex ->{
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + ex.getMessage()
                );
                Stage stageActual = (Stage) tablaPoliticasVencimiento.getScene().getWindow();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
            });
            return null;
        });
    }


    private Dialog<ButtonType> crearDialogo(String titulo, String cabecera, String textoBotonAccion) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);
        DialogPane dialogPane = dialog.getDialogPane();
        ButtonType btnAccion = new ButtonType(textoBotonAccion, ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnAccion, ButtonType.CANCEL);
        return dialog;
    }

    private GridPane crearGridPane(TextField campoNombre, TextField campoPorcentaje, TextField campoDiasUmbral){
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(campoNombre, 1, 0);
        grid.add(new Label("Porcentaje (%):"), 0, 1);
        grid.add(campoPorcentaje, 1, 1);
        grid.add(new Label("Dias Umbral"), 0 , 2);
        grid.add(campoDiasUmbral, 1, 2);
        return grid;
    }

    private void validarCampos(Dialog<ButtonType> dialog, TextField campoNombre, TextField campoPorcentaje, TextField campoDiasUmbral){
        ButtonType btnTipoGuardar = dialog.getDialogPane().getButtonTypes().stream()
                .filter(b -> b.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst().orElse(null);
        Button botonFisicoGuardar = (Button) dialog.getDialogPane().lookupButton(btnTipoGuardar);
        botonFisicoGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            String nombre = campoNombre.getText().trim();
            String porcentajeTexto = campoPorcentaje.getText().trim();
            String diasUmbralTexto = campoDiasUmbral.getText().trim();
            if (nombre.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "El Nombre de la Política NO puede estar Vacío.");
                event.consume();
                return;
            }
            if (porcentajeTexto.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "El Porcentaje NO puede estar Vacío.");
                event.consume();
                return;
            }
            if (diasUmbralTexto.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "Los Días Umbral NO pueden estar Vacíos.");
                event.consume();
                return;
            }
            try {
                FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Error al Ingresar el Porcentaje:\n" + e.getMessage());
                event.consume();
                return;
            }
            try {
                Integer.parseInt(diasUmbralTexto);
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Los Días Umbral deben ser un número entero válido.");
                event.consume();
            }
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        PoliticaVencimientoDTO seleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    "Atención", null,
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Modificarlo."
            );
            return;
        }
        String rutaFxml = RutasVista.EDITAR_POLITICA_V_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            EditarPoliticaVencimientoControlador controlador = loader.getController();
            controlador.cargarDatos(seleccionado, listaObservablePoliticasVencimiento);
            Stage stageEdicion = new Stage();
            stageEdicion.setTitle("Editando Política de Vencimiento");
            stageEdicion.initModality(Modality.APPLICATION_MODAL);
            stageEdicion.setResizable(false);
            Scene escenaEdicion = new Scene(root);
            stageEdicion.setScene(escenaEdicion);
            stageEdicion.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        abrirFormularioNuevo();
    }

    private void abrirFormularioNuevo(){
        Dialog<ButtonType> dialog = crearDialogo("Registrar Nueva Política de Vencimiento",
                "Ingresa los detalles de la Nueva Política de Vencimiento.", "Guardar");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej. Política General 2024");
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("Ej. 15.5");
        TextField txtDiasUmbral = new TextField();
        txtDiasUmbral.setPromptText("Ej. 3");
        CheckBox chkActivo = new CheckBox("¿Política V Activa?");
        chkActivo.setSelected(true);
        GridPane grid = crearGridPane(txtNombre, txtPorcentaje, txtDiasUmbral);
        grid.add(chkActivo, 1, 3);
        dialog.getDialogPane().setContent(grid);
        validarCampos(dialog, txtNombre, txtPorcentaje, txtDiasUmbral);
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String nombre = txtNombre.getText().trim();
                String porcentajeTexto = txtPorcentaje.getText().trim();
                String diasUmbralTexto = txtDiasUmbral.getText().trim();
                boolean activo = chkActivo.isSelected();
                try {
                    BigDecimal porcentaje = FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
                    int diasUmbral = Integer.parseInt(diasUmbralTexto);
                    this.orquestadorPoliticaVencimiento.registrarPoliticaVencimiento(
                            nombre, diasUmbral, porcentaje, activo
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "La Política de Vencimiento se ha Guardado Correctamente.");
                    cargarDatosTabla();
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Datos Numéricos Inválidos",
                            "Error:  " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar la Política de Vencimiento",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                }
            }
        });
    }


    @FXML
    void cambiarEstadoPoliticaV(ActionEvent event) {
        cambiarEstadoPoliticaV();
    }

    private void cambiarEstadoPoliticaV(){
        PoliticaVencimientoDTO politicaSeleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (politicaSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Cambiar su Estado.");
            return;
        }
        //boolean esActivo = politicaSeleccionado.estado().equalsIgnoreCase("Activo");
        //String accion = esActivo ? "Desactivar" : "Activar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de activo");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas " + " la Política -" +
                politicaSeleccionado.nombrePolitica() + "-?");
        DialogPane panelConfirmacion = confirmacion.getDialogPane();
        panelConfirmacion.setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.orquestadorPoliticaVencimiento.cambiarEstadoPoliticaV(
                        politicaSeleccionado.idPoliticaVencimiento()
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Estado se ha Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "NO se pudo Completar la Acción",
                        "Error:  " + e.getMessage());
            } catch (PoliticaVencimientoNoEncontradaException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Política de Vencimiento NO Encontrada",
                        "Error:  " + e.getMessage());
            }
        }
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
    }


}//===================================================================================================================//

