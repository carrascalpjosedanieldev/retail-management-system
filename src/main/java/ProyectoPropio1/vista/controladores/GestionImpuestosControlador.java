package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioImpuestos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOImpuesto;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
import ProyectoPropio1.utilidades.FormateadorNumeros;
import ProyectoPropio1.utilidades.RutasVista;

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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionImpuestosControlador {

    //ATRIBUTOS:

    @FXML private TableView<ImpuestoDTO> tablaImpuestos;
    @FXML private TableColumn<ImpuestoDTO, Integer> colId;
    @FXML private TableColumn<ImpuestoDTO, String > colNombre;
    @FXML private TableColumn<ImpuestoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<ImpuestoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final ServicioImpuestos servicioImpuestos = FabricaServicios.obtenerServicioImpuestos();

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto = FabricaEnsambladores.obtenerEnsambladorDTOImpuesto();

    private final ObservableList<ImpuestoDTO> listaObservableImpuestos = FXCollections.observableArrayList();

    //MÉTODOS:

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane panelAlerta = alerta.getDialogPane();
        panelAlerta.setMinHeight(Region.USE_PREF_SIZE);
        aplicarCSS(panelAlerta);
        alerta.showAndWait();
    }

    private void aplicarCSS(DialogPane panel) {
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_IMPUESTOS);
        if (urlCss != null) {
            panel.getStylesheets().add(urlCss.toExternalForm());
        }
    }


    @FXML
    public void initialize() {
        inicializar();
    }

    private void inicializar(){
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().idImpuesto()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().porcentaje()));
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().estado()));
        colEstado.setCellFactory(columna -> new TableCell<ImpuestoDTO, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(estado);
                    if (estado.equalsIgnoreCase("Activo")) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
        FilteredList<ImpuestoDTO> listaFiltrada = new FilteredList<>(listaObservableImpuestos, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(impuesto -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                if (String.valueOf(impuesto.idImpuesto()).contains(filtro)) {
                    return true;
                }
                else if (impuesto.nombre().toLowerCase().contains(filtro)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<ImpuestoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaImpuestos.comparatorProperty());
        tablaImpuestos.setItems(listaOrdenada);
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        List<ImpuestoDTO> todosLosImpuestos = new ArrayList<>();
        List<ImpuestoDTO> impuestosActivos = this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerImpuestosActivos()
        );
        List<ImpuestoDTO> impuestosInactivos = this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerImpuestosInactivos()
        );
        todosLosImpuestos.addAll(impuestosActivos);
        todosLosImpuestos.addAll(impuestosInactivos);
        listaObservableImpuestos.setAll(todosLosImpuestos);
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        abrirFormularioEdicion();
    }

    private void abrirFormularioEdicion(){
        ImpuestoDTO seleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Impuesto de la Tabla para Modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modificar Impuesto");
        dialog.setHeaderText("Editando el impuesto: " + seleccionado.nombre());
        DialogPane dialogPane = dialog.getDialogPane();
        aplicarCSS(dialogPane);
        ButtonType btnActualizar = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnActualizar, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        TextField txtNombre = new TextField();
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtNombre.setText(seleccionado.nombre());
        txtPorcentaje.setText(seleccionado.porcentaje().toString());
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Porcentaje (%):"), 0, 1);
        grid.add(txtPorcentaje, 1, 1);
        dialogPane.setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnActualizar) {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoPorcentajeTexto = txtPorcentaje.getText();
            try {
                if (nuevoNombre.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                            "El Nombre del Impuesto NO puede estar Vacío.");
                    return;
                }
                if (nuevoPorcentajeTexto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                            "El Porcentaje del Impuesto NO puede estar Vacío");
                    return;
                }
                BigDecimal nuevoPorcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
                this.servicioImpuestos.actualizarImpuesto(seleccionado.idImpuesto(), nuevoNombre, nuevoPorcentaje);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Impuesto se ha Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Error al Ingresar el Porcentaje:\n" + e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error al Editar el Impuesto",
                        "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se pudo Actualizar el Impuesto en la Base de Datos.\n" +
                                "Error:  " + e.getMessage());
            }
        }
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        abrirFormularioNuevo();
    }

    private void abrirFormularioNuevo(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Nuevo Impuesto");
        dialog.setHeaderText("Ingresa los detalles del nuevo impuesto.");
        DialogPane dialogPane = dialog.getDialogPane();
        aplicarCSS(dialogPane);
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej. IVA 2024");
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("Ej. 15.5");
        CheckBox chkActivo = new CheckBox("¿Impuesto Activo?");
        chkActivo.setSelected(true);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Porcentaje (%):"), 0, 1);
        grid.add(txtPorcentaje, 1, 1);
        grid.add(chkActivo, 1, 2);
        dialogPane.setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnGuardar) {
            String nombre = txtNombre.getText();
            String porcentajeTexto = txtPorcentaje.getText();
            boolean activo = chkActivo.isSelected();
            try {
                if (nombre.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                            "El nombre del impuesto no puede estar vacío.");
                    return;
                }
                if (porcentajeTexto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                            "El Porcentaje del Impuesto NO puede estar Vacío");
                    return;
                }
                BigDecimal porcentaje = FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
                this.servicioImpuestos.registrarImpuesto(nombre, porcentaje, activo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Impuesto se ha Guardado Correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Error al Ingresar el Porcentaje:\n" + e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar el Impuesto",
                        "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "No se pudo Registrar el Impuesto en la Base de Datos.");
            }
        }
    }


    @FXML
    void cambiarEstadoImpuesto(ActionEvent event) {
        cambiarEstadoImpuesto();
    }

    private void cambiarEstadoImpuesto(){
        ImpuestoDTO impuestoSeleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (impuestoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Impuesto de la Tabla para Cambiar su Estado.");
            return;
        }
        boolean esActivo = impuestoSeleccionado.estado().equalsIgnoreCase("Activo");
        String accion = esActivo ? "Desactivar" : "Activar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas " + accion + " el Impuesto -" + impuestoSeleccionado.nombre() + "-?");
        DialogPane panelConfirmacion = confirmacion.getDialogPane();
        panelConfirmacion.setMinHeight(Region.USE_PREF_SIZE);
        aplicarCSS(panelConfirmacion);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.servicioImpuestos.cambiarEstadoImpuesto(impuestoSeleccionado.idImpuesto());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Estado se ha Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "NO se Pudo Cambiar el Estado: " + e.getMessage());
            }
        }
    }


    @FXML
    void volverAlPanel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_TIENDA_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de Navegación",
                    "Ocurrió un problema al intentar volver al panel de Gestión.\n" +
                            "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎."
            );
        }
    }


}//===================================================================================================================//

