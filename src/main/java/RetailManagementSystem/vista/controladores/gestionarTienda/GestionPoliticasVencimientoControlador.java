package RetailManagementSystem.vista.controladores.gestionarTienda;

import RetailManagementSystem.aplicacion.dto.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.servicios.ServicioPoliticaVencimiento;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTOPoliticaVencimiento;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
import RetailManagementSystem.vista.utilidades.FormateadorNumeros;
import RetailManagementSystem.vista.utilidades.RutasVista;

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

public class GestionPoliticasVencimientoControlador {

    //ATRIBUTOS:

    @FXML private TableView<PoliticaVencimientoDTO> tablaPoliticasVencimiento;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colId;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colNombre;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colDiasUmbral;
    @FXML private TableColumn<PoliticaVencimientoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colEstado;
    @FXML private TextField txtBuscar;

    private final ServicioPoliticaVencimiento servicioPoliticaVencimiento;

    private final EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento;

    private final ObservableList<PoliticaVencimientoDTO> listaObservablePoliticasVencimiento = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionPoliticasVencimientoControlador(ServicioPoliticaVencimiento servicioPoliticaVencimiento,
                                                  EnsambladorDTOPoliticaVencimiento ensambladorDTOPoliticaVencimiento) {
        this.servicioPoliticaVencimiento = servicioPoliticaVencimiento;
        this.ensambladorDTOPoliticaVencimiento = ensambladorDTOPoliticaVencimiento;
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
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_POLITICAS_V);
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
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().idPoliticaVencimiento()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombrePolitica()));
        colDiasUmbral.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().diasUmbral()));
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().porcentajeDescuento()));
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
        FilteredList<PoliticaVencimientoDTO> listaFiltrada = new FilteredList<>(listaObservablePoliticasVencimiento, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(politicaVencimiento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) return true;
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
        List<PoliticaVencimientoDTO> activas = ensambladorDTOPoliticaVencimiento.ensamblarDetallePoliticasVencimiento(
                servicioPoliticaVencimiento.obtenerPoliticasVencimientoActivas()
        );
        List<PoliticaVencimientoDTO> inactivas = ensambladorDTOPoliticaVencimiento.ensamblarDetallePoliticasVencimiento(
                servicioPoliticaVencimiento.obtenerPoliticasVencimientoInactivas()
        );
        List<PoliticaVencimientoDTO> todasLasPoliticasV = Stream.concat(
                activas != null ? activas.stream() : Stream.empty(),
                inactivas != null ? inactivas.stream() : Stream.empty()
        ).toList();
        listaObservablePoliticasVencimiento.setAll(todasLasPoliticasV);
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
        abrirFormularioEdicion();
    }

    private void abrirFormularioEdicion(){
        PoliticaVencimientoDTO seleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Modificarlo.");
            return;
        }
        Dialog<ButtonType> dialog = crearDialogo("Modificar Política Vencimiento",
                "Editando la Política de Vencimiento: " + seleccionado.nombrePolitica(), "Actualizar");
        TextField txtNombre = new TextField();
        txtNombre.setPrefWidth(250);
        TextField txtPorcentaje = new TextField();
        txtNombre.setText(seleccionado.nombrePolitica());
        txtPorcentaje.setText(seleccionado.porcentajeDescuento().toString());
        TextField txtDiasUmbral = new TextField();
        txtDiasUmbral.setText(String.valueOf(seleccionado.diasUmbral()));
        GridPane grid = crearGridPane(txtNombre, txtPorcentaje, txtDiasUmbral);
        dialog.getDialogPane().setContent(grid);
        validarCampos(dialog, txtNombre, txtPorcentaje, txtDiasUmbral);
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String nuevoNombre = txtNombre.getText().trim();
                String nuevoPorcentajeTexto = txtPorcentaje.getText().trim();
                String nuevoDiasUmbralTexto = txtDiasUmbral.getText().trim();
                try {
                    BigDecimal nuevoPorcentaje = FormateadorNumeros.stringAPorcentaje(nuevoPorcentajeTexto);
                    int nuevoDiasUmbral = Integer.parseInt(nuevoDiasUmbralTexto);
                    this.servicioPoliticaVencimiento.actualizarPoliticaVencimiento(
                            seleccionado.idPoliticaVencimiento(), nuevoNombre, nuevoDiasUmbral, nuevoPorcentaje
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "La Política de Vencimiento se ha Actualizado Correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Editar la Política de Vencimiento",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "NO se pudo Actualizar la Política de Vencimiento en la Base de Datos.\n" +
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
                    this.servicioPoliticaVencimiento.registrarPoliticaVencimiento(
                            nombre, diasUmbral, porcentaje, activo
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "La Política de Vencimiento se ha Guardado Correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar la Política de Vencimiento",
                            "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                            "No se pudo Registrar la Política de Vencimiento en la Base de Datos.");
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
        boolean esActivo = politicaSeleccionado.estado().equalsIgnoreCase("Activo");
        String accion = esActivo ? "Desactivar" : "Activar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas " + accion + " la Política -" +
                politicaSeleccionado.nombrePolitica() + "-?");
        DialogPane panelConfirmacion = confirmacion.getDialogPane();
        panelConfirmacion.setMinHeight(Region.USE_PREF_SIZE);
        aplicarCSS(panelConfirmacion);
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.servicioPoliticaVencimiento.cambiarEstadoPoliticaDeVencimiento(
                        politicaSeleccionado.idPoliticaVencimiento()
                );
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

