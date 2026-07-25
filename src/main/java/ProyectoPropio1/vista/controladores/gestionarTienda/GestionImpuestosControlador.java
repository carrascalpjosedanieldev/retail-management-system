package ProyectoPropio1.vista.controladores.gestionarTienda;

import ProyectoPropio1.dto.ImpuestoDTO;
import ProyectoPropio1.servicios.aplicacion.servicios.ServicioImpuestos;
import ProyectoPropio1.servicios.aplicacion.ensambladores.EnsambladorDTOImpuesto;
import ProyectoPropio1.utilidades.CargadorVistas;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GestionImpuestosControlador {

    //ATRIBUTOS:

    @FXML private TableView<ImpuestoDTO> tablaImpuestos;
    @FXML private TableColumn<ImpuestoDTO, Integer> colId;
    @FXML private TableColumn<ImpuestoDTO, String > colNombre;
    @FXML private TableColumn<ImpuestoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<ImpuestoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final ServicioImpuestos servicioImpuestos;

    private final EnsambladorDTOImpuesto ensambladorDTOImpuesto;

    private final ObservableList<ImpuestoDTO> listaObservableImpuestos = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionImpuestosControlador(ServicioImpuestos servicioImpuestos, EnsambladorDTOImpuesto ensambladorDTOImpuesto) {
        this.servicioImpuestos = servicioImpuestos;
        this.ensambladorDTOImpuesto = ensambladorDTOImpuesto;
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
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_IMPUESTOS);
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

    private void configurarColumnasTabla() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().idImpuesto()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().porcentaje()));
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().estado()));
        colEstado.setCellFactory(columna -> new TableCell<>() {
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

    private void configurarFiltroBusqueda() {
        FilteredList<ImpuestoDTO> listaFiltrada = new FilteredList<>(listaObservableImpuestos, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(impuesto -> {
                if (valorNuevo == null || valorNuevo.isBlank()) return true;
                String filtro = valorNuevo.toLowerCase();
                return String.valueOf(impuesto.idImpuesto()).contains(filtro) ||
                        impuesto.nombre().toLowerCase().contains(filtro);
            });
        });
        SortedList<ImpuestoDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaImpuestos.comparatorProperty());
        tablaImpuestos.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        List<ImpuestoDTO> activos = ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                servicioImpuestos.obtenerImpuestosActivos()
        );
        List<ImpuestoDTO> inactivos = ensambladorDTOImpuesto.ensamblarDetalleImpuestos(
                servicioImpuestos.obtenerImpuestosInactivos()
        );
        List<ImpuestoDTO> todosLosImpuestos = Stream.concat(
                activos != null ? activos.stream() : Stream.empty(),
                inactivos != null ? inactivos.stream() : Stream.empty()
        ).toList();

        listaObservableImpuestos.setAll(todosLosImpuestos);
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

    private GridPane crearGridPane(TextField campoNombre, TextField campoPorcentaje){
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(campoNombre, 1, 0);
        grid.add(new Label("Porcentaje (%):"), 0, 1);
        grid.add(campoPorcentaje, 1, 1);
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
                        "El Nombre del Impuesto NO puede estar Vacío.");
                event.consume();
                return;
            }
            if (porcentajeTexto.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "El Porcentaje del Impuesto NO puede estar Vacío");
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
        ImpuestoDTO seleccionado = tablaImpuestos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Impuesto de la Tabla para Modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = crearDialogo("Modificar Impuesto",
                "Editando el impuesto: " + seleccionado.nombre(), "Actualizar");
        TextField txtNombre = new TextField();
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtNombre.setText(seleccionado.nombre());
        txtPorcentaje.setText(seleccionado.porcentaje().toString());
        GridPane grid = crearGridPane(txtNombre, txtPorcentaje);
        dialog.getDialogPane().setContent(grid);
        validarCampos(dialog, txtNombre, txtPorcentaje);
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String nuevoNombre = txtNombre.getText().trim();
                String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
                try {
                    BigDecimal nuevoPorcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
                    this.servicioImpuestos.actualizarImpuesto(seleccionado.idImpuesto(), nuevoNombre, nuevoPorcentaje);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El Impuesto se ha Actualizado Correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Editar el Impuesto",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "NO se pudo Actualizar el Impuesto en la Base de Datos.\n" +
                                    "Error:  " + e.getMessage());
                }
            }
        });
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        abrirFormularioNuevo();
    }

    private void abrirFormularioNuevo(){
        Dialog<ButtonType> dialog = crearDialogo("Registrar Nuevo Impuesto",
                "Ingresa los detalles del nuevo impuesto.", "Guardar");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Ej. IVA 2024");
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("Ej. 15.5");
        CheckBox chkActivo = new CheckBox("¿Impuesto Activo?");
        chkActivo.setSelected(true);
        GridPane grid = crearGridPane(txtNombre, txtPorcentaje);
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
                    this.servicioImpuestos.registrarImpuesto(nombre, porcentaje, activo);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El Impuesto se ha Guardado Correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar el Impuesto",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "No se pudo Registrar el Impuesto en la Base de Datos.");
                }
            }
        });
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


}//===================================================================================================================//

