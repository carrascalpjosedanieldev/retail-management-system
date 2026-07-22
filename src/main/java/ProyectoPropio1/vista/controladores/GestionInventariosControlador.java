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
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class GestionInventariosControlador {

    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadLibre;
    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadMax;
    @FXML private TableColumn<InventarioDTO, Integer> colCapacidadOcupada;
    @FXML private TableColumn<InventarioDTO, Integer> colId;
    @FXML private TableColumn<InventarioDTO, String> colNombre;
    @FXML private TableView<InventarioDTO> tablaInventarios;
    @FXML private TextField txtBuscar;

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
        aplicarCSS(pane);
        alerta.showAndWait();
    }

    private void aplicarCSS(DialogPane panel) {
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_INVENTARIOS);
        if (urlCss != null) {
            panel.getStylesheets().add(urlCss.toExternalForm());
        }
    }

    private Dialog<ButtonType> crearDialogoBase(String titulo, String cabecera, double ancho) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(ancho);
        aplicarCSS(dialogPane);
        return dialog;
    }

    private GridPane crearGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        return grid;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().idInventario()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colCapacidadMax.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().capacidadMaxima()));
        colCapacidadOcupada.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().capacidadOcupada()));
        colCapacidadLibre.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().capacidadLibre()));
        colCapacidadLibre.setCellFactory(columna -> new TableCell<>() {
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
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Inventario de la Tabla para Modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = crearDialogoBase("Modificar Inventario", "Editando inventario: " + seleccionado.nombre(), 420);
        ButtonType btnActualizar = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnActualizar, ButtonType.CANCEL);
        TextField txtNombre = new TextField(seleccionado.nombre());
        txtNombre.setPromptText("Ej: Bodega Principal");
        txtNombre.setPrefWidth(220);
        TextField txtCapacidad = new TextField(String.valueOf(seleccionado.capacidadMaxima()));
        txtCapacidad.setDisable(true);
        GridPane grid = crearGridPane();
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Capacidad Máx:"), 0, 1);
        grid.add(txtCapacidad, 1, 1);
        Label notaVisual = new Label("(La capacidad no se puede modificar)");
        notaVisual.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");
        grid.add(notaVisual, 1, 2);
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnActualizar) {
            String nuevoNombre = txtNombre.getText().trim();
            try {
                if (nuevoNombre.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos",
                            "El Nombre del Inventario NO puede estar Vacío.");
                    return;
                }
                if (nuevoNombre.equalsIgnoreCase(seleccionado.nombre())) {
                    return;
                }
                this.servicioInventario.actualizarInventario(seleccionado.idInventario(), nuevoNombre);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Inventario ha sido Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se pudo Actualizar el Inventario:\n" +
                                "Error:  " + e.getMessage());
            }
        }
    }

    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        Dialog<ButtonType> dialog = crearDialogoBase("Nuevo Inventario", "Ingresa los datos de la nueva bodega / inventario", 500);
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej: Bodega Central");
        txtNombre.setPrefWidth(220);
        TextField txtCapacidad = new TextField();
        txtCapacidad.setPromptText("Ej: 500");
        Label lblAdvertencia = new Label("""
                ⚠️ Importante: Verifica bien este número.
                Una vez creado el inventario, su capacidad
                máxima NO podrá ser modificada.""");
        lblAdvertencia.setStyle("-fx-text-fill: #ea580c; -fx-font-size: 12px; -fx-font-weight: bold;");
        GridPane grid = crearGridPane();
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Capacidad Máx:"), 0, 1);
        grid.add(txtCapacidad, 1, 1);
        grid.add(lblAdvertencia, 1, 2);
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnGuardar) {
            String nombre = txtNombre.getText().trim();
            String capacidadTexto = txtCapacidad.getText().trim();
            try {
                if (nombre.isEmpty() || capacidadTexto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos",
                            "El Nombre y la Capacidad son Obligatorios.");
                    return;
                }
                int capacidad = Integer.parseInt(capacidadTexto);
                if (capacidad <= 0) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Dato Inválido",
                            "La Capacidad debe ser un Número Positivo.");
                    return;
                }
                this.servicioInventario.agregarInventario(nombre, capacidad);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Inventario ha sido Creado Correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "La Capacidad debe ser un Número Entero (ej. 500), Sin Letras ni Decimales.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se pudo Registrar el Inventario en la Base de Datos:\n" +
                                "Error:  " + e.getMessage());
            }
        }
    }

    @FXML
    void editarProductosInventario(ActionEvent event) {
        InventarioDTO seleccionado = tablaInventarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Selecciona un Inventario para ver sus Productos.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(RutasVista.GESTIONAR_PRODUCTOS_VIEW));
            Parent root = loader.load();
            GestionProductosControlador controlador = loader.getController();
            controlador.inicializarConInventario(seleccionado.idInventario());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación",
                    "NO se pudo Cargar la Vista de Productos.\n" +
                            "Detalle: " + e.getMessage());
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

