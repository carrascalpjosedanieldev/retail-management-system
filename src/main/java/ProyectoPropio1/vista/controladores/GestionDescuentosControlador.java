package ProyectoPropio1.vista.controladores;


import ProyectoPropio1.dto.DescuentoDTO;

import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTODescuento;

import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
import ProyectoPropio1.utilidades.FormateadorNumeros;
import ProyectoPropio1.utilidades.RutasVista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;

import javafx.stage.Stage;

import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import java.io.IOException;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class GestionDescuentosControlador {

    @FXML private TableView<DescuentoDTO> tablaDescuentos;

    @FXML private TableColumn<DescuentoDTO, Integer> colId;

    @FXML private TableColumn<DescuentoDTO, String> colNombre;

    @FXML private TableColumn<DescuentoDTO, BigDecimal> colPorcentaje;

    @FXML private TableColumn<DescuentoDTO, String> colEstado;

    @FXML private TextField txtBuscar;

    private final ServicioDescuentos servicioDescuentos = FabricaServicios.obtenerServicioDescuentos();

    private final EnsambladorDTODescuento ensambladorDTODescuento = FabricaEnsambladores.obtenerEnsambladorDTODescuento();

    private final ObservableList<DescuentoDTO> listaObservableDescuentos = FXCollections.observableArrayList();


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        DescuentoDTO seleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un descuento de la tabla para modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modificar Descuento");
        dialog.setHeaderText("Editando el descuento: " + seleccionado.nombre());
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_DESCUENTOS),
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
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El nombre del descuento no puede estar vacío.");
                    return;
                }
                BigDecimal nuevoPorcentaje = FormateadorNumeros.stringABigDecimal(nuevoPorcentajeTexto);
                this.servicioDescuentos.actualizarDescuento(seleccionado.idDescuento(), nuevoNombre, nuevoPorcentaje);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El descuento se ha actualizado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "Por favor, ingresa un porcentaje válido (ejemplo: 15 o 15.5).");
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Descuento Duplicado", e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo actualizar el descuento en la base de datos.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().idDescuento()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().porcentaje()));
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().estado()));
        colEstado.setCellFactory(columna -> new TableCell<DescuentoDTO, String>() {
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
        FilteredList<DescuentoDTO> listaFiltrada = new FilteredList<>(listaObservableDescuentos, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(descuento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                if (String.valueOf(descuento.idDescuento()).contains(filtro)) {
                    return true;
                }
                else if (descuento.nombre().toLowerCase().contains(filtro)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<DescuentoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaDescuentos.comparatorProperty());
        tablaDescuentos.setItems(listaOrdenada);
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        List<DescuentoDTO> todosLosDescuentos = new ArrayList<>();
        List<DescuentoDTO> descuentosActivos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosActivos()
        );
        List<DescuentoDTO> descuentosInactivos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosInactivos()
        );
        todosLosDescuentos.addAll(descuentosActivos);
        todosLosDescuentos.addAll(descuentosInactivos);
        listaObservableDescuentos.setAll(todosLosDescuentos);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.getDialogPane().getStylesheets().add(
                java.util.Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_DESCUENTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS en la ruta especificada."
                ).toExternalForm()
        );
        alerta.showAndWait();
    }

    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Nuevo Descuento");
        dialog.setHeaderText("Ingresa los detalles del nuevo descuento.");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
                java.util.Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_DESCUENTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS para el formulario de descuentos."
                ).toExternalForm()
        );
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej. Navidad 2024");
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("Ej. 15.5");
        CheckBox chkActivo = new CheckBox("¿Descuento Activo?");
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
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El nombre del descuento no puede estar vacío.");
                    return;
                }
                BigDecimal porcentaje = FormateadorNumeros.stringABigDecimal(porcentajeTexto);
                this.servicioDescuentos.registrarDescuento(nombre, porcentaje, activo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El descuento se ha guardado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "Por favor, ingresa un porcentaje válido (ejemplo: 15 o 15.5).");
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Descuento Duplicado", e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo guardar el descuento en la base de datos.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    void cambiarEstadoDescuento(ActionEvent event) {
        DescuentoDTO descuentoSeleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (descuentoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un descuento de la tabla para cambiar su estado.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas cambiar el estado del descuento '" + descuentoSeleccionado.nombre() + "'?");
        confirmacion.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_DESCUENTOS),
                        "¡CRÍTICO! No se encontró el archivo CSS."
                ).toExternalForm()
        );
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.servicioDescuentos.cambiarEstadoDescuento(descuentoSeleccionado.idDescuento());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El estado se ha actualizado correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cambiar el estado: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void volverAlPanel(ActionEvent event) {
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

