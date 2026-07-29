package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.dto.DescuentoDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioDescuentos;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTODescuento;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.RutasVista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.stream.Stream;

public class GestionDescuentosControlador {

    //ATRIBUTOS:

    @FXML private TableView<DescuentoDTO> tablaDescuentos;
    @FXML private TableColumn<DescuentoDTO, Integer> colId;
    @FXML private TableColumn<DescuentoDTO, String> colNombre;
    @FXML private TableColumn<DescuentoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<DescuentoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final ServicioDescuentos servicioDescuentos;

    private final EnsambladorDTODescuento ensambladorDTODescuento;

    private final ObservableList<DescuentoDTO> listaObservableDescuentos = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionDescuentosControlador(ServicioDescuentos servicioDescuentos,
                                        EnsambladorDTODescuento ensambladorDTODescuento) {
        this.servicioDescuentos = servicioDescuentos;
        this.ensambladorDTODescuento = ensambladorDTODescuento;
    }

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
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_DESCUENTOS);
        if (urlCss != null) {
            panel.getStylesheets().add(urlCss.toExternalForm());
        }
    }


    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla(){
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
                    String color = estado.equalsIgnoreCase("Activo") ? "#10b981" : "#ef4444";
                    setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
                }
            }
        });
    }

    private void configurarFiltroBusqueda(){
        FilteredList<DescuentoDTO> listaFiltrada = new FilteredList<>(listaObservableDescuentos, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(descuento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase().trim();
                return String.valueOf(descuento.idDescuento()).contains(filtro) ||
                        descuento.nombre().toLowerCase().contains(filtro);
            });
        });
        SortedList<DescuentoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaDescuentos.comparatorProperty());
        tablaDescuentos.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        List<DescuentoDTO> activos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosActivos()
        );
        List<DescuentoDTO> inactivos = this.ensambladorDTODescuento.ensamblarDetalleDescuentos(
                this.servicioDescuentos.obtenerDescuentosInactivos()
        );
        List<DescuentoDTO> todosLosDescuentos = Stream.concat(
                activos != null ? activos.stream() : Stream.empty(),
                inactivos != null ? inactivos.stream() : Stream.empty()
        ).toList();
        listaObservableDescuentos.setAll(todosLosDescuentos);
    }


    private Dialog<ButtonType> crearDialogo(String titulo, String cabecera, String textoBotonAccion) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);
        DialogPane dialogPane = dialog.getDialogPane();
        aplicarCSS(dialogPane);
        ButtonType btnAccion = new ButtonType(textoBotonAccion, ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnAccion, ButtonType.CANCEL);
        return dialog;
    }

    private GridPane crearGridPane(){
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        return grid;
    }

    private void validarCampos(Dialog<ButtonType> dialog, TextField campoNombre, TextField campoPorcentaje){
        ButtonType btnTipoGuardar = dialog.getDialogPane().getButtonTypes().stream()
                .filter(b -> b.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst().orElse(null);
        Button botonFisicoGuardar = (Button) dialog.getDialogPane().lookupButton(btnTipoGuardar);
        botonFisicoGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            String nombre = campoNombre.getText().trim();
            String porcentajeTexto = campoPorcentaje.getText().trim();
            if (nombre.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "El Nombre del Descuento NO puede estar Vacío.");
                event.consume();
                return;
            }
            if (porcentajeTexto.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "El Porcentaje del Descuento NO puede estar Vacío");
                event.consume();
                return;
            }
            try {
                FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Error al Ingresar el Porcentaje:\n" + e.getMessage());
                event.consume();
            }
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        abrirFormularioEdicion();
    }

    private void abrirFormularioEdicion(){
        DescuentoDTO seleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Descuento de la Tabla para Modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = crearDialogo("Modificar Descuento",
                "Editando el descuento: " + seleccionado.nombre(), "Actualizar");
        TextField txtNombre = new TextField();
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtNombre.setText(seleccionado.nombre());
        txtPorcentaje.setText(seleccionado.porcentaje().toString());
        GridPane grid = crearGridPane();
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Porcentaje (%):"), 0, 1);
        grid.add(txtPorcentaje, 1, 1);
        dialog.getDialogPane().setContent(grid);
        validarCampos(dialog, txtNombre, txtPorcentaje);
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String nuevoNombre = txtNombre.getText().trim();
                String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
                try {
                    BigDecimal nuevoPorcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
                    this.servicioDescuentos.actualizarDescuento(seleccionado.idDescuento(), nuevoNombre, nuevoPorcentaje);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El descuento se ha actualizado correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Editar el Descuento",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "NO se pudo Actualizar el Descuento en la Base de Datos.");
                }
            }
        });
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        abrirFormularioNuevo();
    }

    private void abrirFormularioNuevo(){
        Dialog<ButtonType> dialog = crearDialogo("Registrar Nuevo Descuento",
                "Ingresa los detalles del nuevo descuento.", "Guardar");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej. Navidad 2024");
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("Ej. 15.5");
        CheckBox chkActivo = new CheckBox("¿Descuento Activo?");
        chkActivo.setSelected(true);
        GridPane grid = crearGridPane();
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Porcentaje (%):"), 0, 1);
        grid.add(txtPorcentaje, 1, 1);
        grid.add(chkActivo, 1, 2);
        dialog.getDialogPane().setContent(grid);
        validarCampos(dialog, txtNombre, txtPorcentaje);
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String nombre = txtNombre.getText().trim();
                String porcentajeTexto = txtPorcentaje.getText().trim();
                boolean activo = chkActivo.isSelected();
                try {
                    BigDecimal porcentaje = FormateadorNumeros.stringAPorcentaje(porcentajeTexto);
                    this.servicioDescuentos.registrarDescuento(nombre, porcentaje, activo);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El descuento se ha guardado correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar el Descuento",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "No se pudo guardar el descuento en la base de datos\n" +
                                    "Error:  " + e.getMessage());
                }
            }
        });
    }


    @FXML
    void cambiarEstadoDescuento(ActionEvent event) {
        cambiarEstadoDescuento();
    }

    private void cambiarEstadoDescuento(){
        DescuentoDTO descuentoSeleccionado = tablaDescuentos.getSelectionModel().getSelectedItem();
        if (descuentoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, selecciona un Descuento de la Tabla para cambiar su Estado.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cambio de Estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás Seguro de que Deseas Cambiar el Estado del Descuento -" + descuentoSeleccionado.nombre() + "-?");
        DialogPane panelConfirmacion = confirmacion.getDialogPane();
        aplicarCSS(panelConfirmacion);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.servicioDescuentos.cambiarEstadoDescuento(descuentoSeleccionado.idDescuento());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Estado se ha Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "NO se pudo cambiar el Estado: " + e.getMessage());
            }
        }
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Error de Navegación",
                    "Ocurrió un problema al intentar volver al panel de Gestión.\n" +
                            "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎."
            );
        }
    }


} //==================================================================================================================//

