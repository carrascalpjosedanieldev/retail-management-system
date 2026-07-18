package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dto.InventarioDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioInventario;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOInventario;
import ProyectoPropio1.utilidades.FabricaEnsambladores;
import ProyectoPropio1.utilidades.FabricaServicios;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GestionInventariosControlador {

    @FXML
    private TableColumn<InventarioDTO, Integer> colCapacidadLibre;

    @FXML
    private TableColumn<InventarioDTO, Integer> colCapacidadMax;

    @FXML
    private TableColumn<InventarioDTO, Integer> colCapacidadOcupada;

    @FXML
    private TableColumn<InventarioDTO, Integer> colId;

    @FXML
    private TableColumn<InventarioDTO, String> colNombre;

    @FXML
    private TableView<InventarioDTO> tablaInventarios;

    @FXML
    private TextField txtBuscar;

    private final ServicioInventario servicioInventario = FabricaServicios.obtenerServicioInventario();

    private final EnsambladorDTOInventario ensambladorDTOInventario = FabricaEnsambladores.obtenerEnsambladorDTOInventario();

    private final ObservableList<InventarioDTO> listaObservable = FXCollections.observableArrayList();

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(180);
        pane.setMinWidth(400);
        try {
            alerta.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource(RutasVista.ESTILOS_CSS_INVENTARIOS)).toExternalForm());
        } catch (Exception ignored) {}
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().idInventario()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colCapacidadMax.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().capacidadMaxima()));
        colCapacidadOcupada.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().capacidadOcupada()));
        colCapacidadLibre.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().capacidadLibre()));
        colCapacidadLibre.setCellFactory(columna -> new TableCell<InventarioDTO, Integer>() {
            @Override
            protected void updateItem(Integer libre, boolean empty) {
                super.updateItem(libre, empty);
                if (empty || libre == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(libre));
                    if (libre <= 0) {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    } else if (libre <= 10) {
                        setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    }
                }
            }
        });
        FilteredList<InventarioDTO> listaFiltrada = new FilteredList<>(listaObservable, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(inv -> {
                if (valorNuevo == null || valorNuevo.isBlank()){
                    return true;
                }
                String filtro = valorNuevo.toLowerCase().trim();
                String idComoTexto = String.valueOf(inv.idInventario());
                return inv.nombre().toLowerCase().contains(filtro) ||
                        idComoTexto.contains(filtro);
            });
        });
        SortedList<InventarioDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaInventarios.comparatorProperty());
        tablaInventarios.setItems(listaOrdenada);
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        List<InventarioDTO> inventarios = this.ensambladorDTOInventario.ensamblarDetalleInventarioGeneral(
                this.servicioInventario.obtenerTodosLosInventarios()
        );
        listaObservable.setAll(inventarios);
    }

    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un inventario de la tabla para modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modificar Inventario");
        dialog.setHeaderText("Editando inventario: " + seleccionado.nombre());
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(420);
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_INVENTARIOS),
                        "¡CRÍTICO! No se encontró el archivo CSS."
                ).toExternalForm()
        );
        ButtonType btnActualizar = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnActualizar, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        TextField txtNombre = new TextField(seleccionado.nombre());
        txtNombre.setPromptText("Ej: Bodega Principal");
        txtNombre.setPrefWidth(220);
        TextField txtCapacidad = new TextField(String.valueOf(seleccionado.capacidadMaxima()));
        txtCapacidad.setDisable(true);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Capacidad Máx:"), 0, 1);
        grid.add(txtCapacidad, 1, 1);
        Label notaVisual = new Label("(La capacidad no se puede modificar)");
        notaVisual.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        grid.add(notaVisual, 1, 2);
        dialogPane.setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnActualizar) {
            String nuevoNombre = txtNombre.getText().trim();
            try {
                if (nuevoNombre.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre del inventario no puede estar vacío.");
                    return;
                }
                if (nuevoNombre.equalsIgnoreCase(seleccionado.nombre())) {
                    return;
                }
                this.servicioInventario.actualizarInventario(seleccionado.idInventario(), nuevoNombre);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El nombre del inventario ha sido actualizado.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo actualizar el inventario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Inventario");
        dialog.setHeaderText("Ingresa los datos de la nueva bodega / inventario");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(500);
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_INVENTARIOS),
                        "¡CRÍTICO! No se encontró el archivo CSS."
                ).toExternalForm()
        );
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Bodega Central");
        txtNombre.setPrefWidth(220);
        TextField txtCapacidad = new TextField();
        txtCapacidad.setPromptText("Ej: 500");
        Label lblAdvertencia = new Label("⚠️ Importante: Verifica bien este número.\nUna vez creado el inventario, su capacidad\nmáxima NO podrá ser modificada.");
        lblAdvertencia.setStyle("-fx-text-fill: #ea580c; -fx-font-size: 12px; -fx-font-weight: bold;");
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Capacidad Máx:"), 0, 1);
        grid.add(txtCapacidad, 1, 1);
        grid.add(lblAdvertencia, 1, 2);
        dialogPane.setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnGuardar) {
            String nombre = txtNombre.getText().trim();
            String capacidadTexto = txtCapacidad.getText().trim();
            try {
                if (nombre.isEmpty() || capacidadTexto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre y la capacidad son obligatorios.");
                    return;
                }
                int capacidad = Integer.parseInt(capacidadTexto);
                if (capacidad <= 0) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Dato Inválido", "La capacidad debe ser un número mayor a cero.");
                    return;
                }
                this.servicioInventario.agregarInventario(nombre, capacidad);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El inventario ha sido creado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "La capacidad debe ser un número entero (ej. 500), sin letras ni decimales.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo crear el inventario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void editarProductosInventario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_PRODUCTOS_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void volverAlPanel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_TIENDA_VIEW));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
