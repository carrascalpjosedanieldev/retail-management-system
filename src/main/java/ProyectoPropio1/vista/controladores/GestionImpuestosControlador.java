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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GestionImpuestosControlador {

    @FXML
    private TableView<ImpuestoDTO> tablaImpuestos;

    @FXML
    private TableColumn<ImpuestoDTO, Integer> colId;

    @FXML
    private TableColumn<ImpuestoDTO, String > colNombre;

    @FXML
    private TableColumn<ImpuestoDTO, BigDecimal> colPorcentaje;

    @FXML
    private TableColumn<ImpuestoDTO, String> colEstado;

    @FXML
    private TextField txtBuscar;

    private final ServicioImpuestos servicioImpuestos = FabricaServicios.obtenerServicioImpuestos();

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto = FabricaEnsambladores.obtenerEnsambladorDTOImpuesto();

    private final ObservableList<ImpuestoDTO> listaObservableDescuentos = FXCollections.observableArrayList();

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.getDialogPane().getStylesheets().add(
                java.util.Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_IMPUESTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS en la ruta especificada."
                ).toExternalForm()
        );
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
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
        FilteredList<ImpuestoDTO> listaFiltrada = new FilteredList<>(listaObservableDescuentos, b -> true);
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
        List<ImpuestoDTO> todosLosDescuentos = new ArrayList<>();
        List<ImpuestoDTO> descuentosActivos = this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerImpuestosActivos()
        );
        List<ImpuestoDTO> descuentosInactivos = this.ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                this.servicioImpuestos.obtenerImpuestosInactivos()
        );
        todosLosDescuentos.addAll(descuentosActivos);
        todosLosDescuentos.addAll(descuentosInactivos);
        listaObservableDescuentos.setAll(todosLosDescuentos);
    }

    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        ImpuestoDTO seleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un impuesto de la tabla para modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modificar Impuesto");
        dialog.setHeaderText("Editando el impuesto: " + seleccionado.nombre());
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_IMPUESTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS para el formulario."
                ).toExternalForm()
        );
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
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El nombre del impuesto no puede estar vacío.");
                    return;
                }
                BigDecimal nuevoPorcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
                this.servicioImpuestos.actualizarImpuesto(seleccionado.idImpuesto(), nuevoNombre, nuevoPorcentaje);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El impuesto se ha actualizado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "Por favor, ingresa un porcentaje válido (ejemplo: 15 o 15.5).");
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Impuesto Duplicado", e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo actualizar el impuesto en la base de datos.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Nuevo Impuesto");
        dialog.setHeaderText("Ingresa los detalles del nuevo impuesto.");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                java.util.Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_IMPUESTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS para el formulario de impuestos."
                ).toExternalForm()
        );
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
        grid.add(new javafx.scene.control.Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new javafx.scene.control.Label("Porcentaje (%):"), 0, 1);
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
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El nombre del impuesto no puede estar vacío.");
                    return;
                }
                BigDecimal porcentaje = FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
                this.servicioImpuestos.registrarImpuesto(nombre, porcentaje, activo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El impuesto se ha guardado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "Por favor, ingresa un porcentaje válido (ejemplo: 15 o 15.5).");
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Impuesto Duplicado", e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo guardar el impuesto en la base de datos.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void cambiarEstadoImpuesto(ActionEvent event) {
        ImpuestoDTO impuestoSeleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (impuestoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un impuesto de la tabla para cambiar su estado.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas cambiar el estado del impuesto '" + impuestoSeleccionado.nombre() + "'?");
        confirmacion.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_IMPUESTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS."
                ).toExternalForm()
        );
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.servicioImpuestos.cambiarEstadoImpuesto(impuestoSeleccionado.idImpuesto());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El estado se ha actualizado correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cambiar el estado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void volverAlPanel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_TIENDA_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
