package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarServicios;

import RetailManagementSystem.aplicacion.dto.gestion.DescuentoDTO;
import RetailManagementSystem.aplicacion.dto.gestion.ImpuestoDTO;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorDescuentos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorImpuestos;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorServicios;
import RetailManagementSystem.aplicacion.dto.comercial.ServicioDTO;
import RetailManagementSystem.dominio.excepciones.DescuentoNoEncontradoExeption;
import RetailManagementSystem.dominio.excepciones.ImpuestoNoEncontradoException;
import RetailManagementSystem.dominio.excepciones.ServicioNoEncontradoException;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GestionServiciosControlador {

    //ATRIBUTOS:

    @FXML private TableColumn<ServicioDTO, String> colCodigo;
    @FXML private TableColumn<ServicioDTO, String> colDescuento;
    @FXML private TableColumn<ServicioDTO, String> colEstado;
    @FXML private TableColumn<ServicioDTO, String> colImpuesto;
    @FXML private TableColumn<ServicioDTO, String> colNombre;
    @FXML private TableColumn<ServicioDTO, BigDecimal> colPrecioBase;
    @FXML private TableColumn<ServicioDTO, BigDecimal> colPrecioFinal;
    @FXML private TableView<ServicioDTO> tablaServicios;
    @FXML private TextField txtBuscar;

    private final OrquestadorServicios orquestadorServicios;

    private final OrquestadorDescuentos orquestadorDescuentos;

    private final OrquestadorImpuestos orquestadorImpuestos;

    private final ObservableList<ServicioDTO> listaObservableServicios = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionServiciosControlador(
            OrquestadorServicios orquestadorServicios, OrquestadorDescuentos orquestadorDescuentos,
            OrquestadorImpuestos orquestadorImpuestos
    ) {
        this.orquestadorServicios = orquestadorServicios;
        this.orquestadorDescuentos = orquestadorDescuentos;
        this.orquestadorImpuestos = orquestadorImpuestos;
    }

    //MÉTODOS:

    private Optional<ButtonType> mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(180);
        pane.setMinWidth(400);
        aplicarCSS(pane);
        return alerta.showAndWait();
    }

    private void aplicarCSS(DialogPane panel) {
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_SERVICIOS);
        if (urlCss != null) {
            panel.getStylesheets().add(urlCss.toExternalForm());
        }
    }



    private <T> void configurarComboBox(ComboBox<T> comboBox, List<T> items, String prompt, Function<T, String> extractorTexto) {
        comboBox.setItems(FXCollections.observableArrayList(items));
        comboBox.setPromptText(prompt);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(T item) { return (item == null) ? "" : extractorTexto.apply(item); }
            @Override
            public T fromString(String string) { return null; }
        });
    }

    private Dialog<ButtonType> crearDialogoBase(String titulo, String cabecera, String textoBoton) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(480);
        dialogPane.setPrefHeight(450);
        aplicarCSS(dialogPane);
        ButtonType btnAccion = new ButtonType(textoBoton, ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnAccion, ButtonType.CANCEL);
        return dialog;
    }

    private GridPane crearGridPane(
            TextField txtNombre, TextField txtPrecioBase,
            ComboBox<ImpuestoDTO> cbImpuestos, ComboBox<DescuentoDTO> cbDescuentos
    ) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Precio Base:"), 0, 1);
        grid.add(txtPrecioBase, 1, 1);
        grid.add(new Label("Impuesto:"), 0, 2);
        grid.add(cbImpuestos, 1, 2);
        grid.add(new Label("Descuento:"), 0, 3);
        grid.add(cbDescuentos, 1, 3);
        return grid;
    }

    private void validarCampos(Dialog<ButtonType> dialog, TextField txtNombre, TextField txtPrecioBase,
                               ComboBox<ImpuestoDTO> cbImpuestos, ComboBox<DescuentoDTO> cbDescuentos) {
        ButtonType btnTipoAccion = dialog.getDialogPane().getButtonTypes().stream()
                .filter(b -> b.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst().orElse(null);
        Button botonFisico = (Button) dialog.getDialogPane().lookupButton(btnTipoAccion);
        botonFisico.addEventFilter(ActionEvent.ACTION, event -> {
            String nombre = txtNombre.getText().trim();
            String precioTexto = txtPrecioBase.getText().trim();
            if (nombre.isEmpty() || precioTexto.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "El Nombre y el Precio Base son Obligatorios.");
                event.consume();
                return;
            }
            if (cbImpuestos.getValue() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "Debes Seleccionar un Impuesto para el Servicio.");
                event.consume();
                return;
            }
            if (cbDescuentos.getValue() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación",
                        "Debes Seleccionar un Descuento para el Servicio (De preferencia que sea -Sin Descuento-).");
                event.consume();
                return;
            }
            try {
                FormateadorNumeros.stringAPrecio(precioTexto);
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Por favor, Ingresa un Precio Base Numérico Válido.");
                event.consume();
            }
        });
    }


    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnas(){
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().codigo())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombre())
        );
        colPrecioBase.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().precioBase())
        );
        colImpuesto.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().datosImpuesto().nombre())
        );
        colDescuento.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().datosDescuento().nombre())
        );
        colPrecioFinal.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().precioFinal())
        );
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().estado())
        );
        configurarColumnaMoneda(colPrecioBase);
        configurarColumnaMoneda(colPrecioFinal);
        colEstado.setCellFactory(columna -> new TableCell<ServicioDTO, String>() {
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
    }

    private void configurarColumnaMoneda(TableColumn<ServicioDTO, BigDecimal> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                getStyleClass().removeAll("columna-moneda");
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(precio));
                    getStyleClass().add("columna-moneda");
                }
            }
        });
    }

    private void configurarFiltroBusqueda(){
        FilteredList<ServicioDTO> listaFiltrada = new FilteredList<>(listaObservableServicios, b -> true);
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(servicio -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
                String filtro = valorNuevo.toLowerCase();
                return servicio.codigo().toLowerCase().contains(filtro) ||
                        servicio.nombre().toLowerCase().contains(filtro);
            });
        });
        SortedList<ServicioDTO> listaOrdenada = new SortedList<>(listaFiltrada);
        listaOrdenada.comparatorProperty().bind(tablaServicios.comparatorProperty());
        tablaServicios.setItems(listaOrdenada);
    }

    private void cargarDatosTabla() {
        LocalDate fechaActual = LocalDate.now();
        CompletableFuture.supplyAsync(()->
                this.orquestadorServicios.obtenerTodosLosServicios(fechaActual)
        ).thenAccept(listaServicios->
            Platform.runLater(()->
                listaObservableServicios.setAll(listaServicios)
            )
        ).exceptionally(ex->{
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                Stage stageActual = (Stage) tablaServicios.getScene().getWindow();
                CargadorVistas.cambiarPantalla(stageActual, RutasVista.GESTIONAR_TIENDA_VIEW);
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioEdicion(ActionEvent event) {
        abrirFormularioEdicion();
    }

    private void abrirFormularioEdicion(){
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Servicio de la Tabla para Modificarlo.");
            return;
        }
        List<ImpuestoDTO> listaImpuestos = this.orquestadorImpuestos.obtenerImpuestosActivos();
        List<DescuentoDTO> listaDescuentos = this.orquestadorDescuentos.obtenerDescuentosActivos();
        Dialog<ButtonType> dialog = crearDialogoBase("Modificar Servicio",
                "Editando el servicio: " + seleccionado.codigo() + " \n " + seleccionado.nombre(),
                "Actualizar");
        TextField txtNombre = new TextField(seleccionado.nombre());
        txtNombre.setPrefWidth(250);
        TextField txtPrecioBase = new TextField(seleccionado.precioBase().toString());
        ComboBox<ImpuestoDTO> cbImpuestos = new ComboBox<>();
        configurarComboBox(cbImpuestos, listaImpuestos, "Seleccione un Impuesto...",
                imp -> imp.nombre() + " (" + imp.porcentaje() + "%)");
        ComboBox<DescuentoDTO> cbDescuentos = new ComboBox<>();
        configurarComboBox(cbDescuentos, listaDescuentos, "Seleccione un Descuento...",
                desc -> desc.nombre() + " (" + desc.porcentaje() + "%)");
        listaImpuestos.stream().filter(imp ->
                imp.idImpuesto() == seleccionado.datosImpuesto().idImpuesto()).findFirst().ifPresent(cbImpuestos.getSelectionModel()::select);
        listaDescuentos.stream().filter(desc ->
                desc.idDescuento() == seleccionado.datosDescuento().idDescuento()).findFirst().ifPresent(cbDescuentos.getSelectionModel()::select);
        GridPane grid = crearGridPane(txtNombre, txtPrecioBase, cbImpuestos, cbDescuentos);
        dialog.getDialogPane().setContent(grid);
        validarCampos(dialog, txtNombre, txtPrecioBase, cbImpuestos, cbDescuentos);
        dialog.showAndWait().ifPresent(resultado -> {
            if (resultado.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    String nuevoNombre = txtNombre.getText().trim();
                    BigDecimal nuevoPrecioBase = FormateadorNumeros.stringAPrecio(txtPrecioBase.getText().trim());
                    ImpuestoDTO impuestoSeleccionado = cbImpuestos.getValue();
                    DescuentoDTO descuentoSeleccionado = cbDescuentos.getValue();
                    this.orquestadorServicios.actualizarServicio(
                            seleccionado.codigo(), nuevoNombre, nuevoPrecioBase,
                            impuestoSeleccionado.idImpuesto(), descuentoSeleccionado.idDescuento(), LocalDate.now()
                    );
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "El Servicio ha sido Actualizado Correctamente.");
                    cargarDatosTabla();
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error en los Datos Ingresados",
                            "Error:  " + e.getMessage());
                } catch (ServicioNoEncontradoException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Servicio NO Encontrado",
                            "Error:  " + e.getMessage());
                } catch (ImpuestoNoEncontradoException | DescuentoNoEncontradoExeption e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al Editar el Servicio",
                            "Error:  " + e.getMessage());
                }
            }
        });
    }

    private void ejecutarConCatalogosListos(BiConsumer<List<ImpuestoDTO>, List<DescuentoDTO>> accionVisual) {
        CompletableFuture<List<ImpuestoDTO>> futuroImpuestos =
                CompletableFuture.supplyAsync(this.orquestadorImpuestos::obtenerImpuestosActivos);
        CompletableFuture<List<DescuentoDTO>> futuroDescuentos =
                CompletableFuture.supplyAsync(this.orquestadorDescuentos::obtenerDescuentosActivos);
        futuroImpuestos.thenCombine(futuroDescuentos, (impuestos, descuentos) -> {
            if (impuestos.isEmpty()) {
                throw new IllegalStateException("NO puedes realizar esta Acción sin al menos un Impuesto Activo.");
            }
            if (descuentos.isEmpty()) {
                throw new IllegalStateException("NO puedes realizar esta Acción sin al menos un Descuento Activo.");
            }
            Platform.runLater(() -> accionVisual.accept(impuestos, descuentos));
            return null;
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaWarning("Configuración Requerida", null, causa.getMessage());
            });
            return null;
        });
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        ejecutarConCatalogosListos(this::abrirModalCrear);
    }

    private void abrirModalCrear(List<ImpuestoDTO> listaImpuestos, List<DescuentoDTO> listaDescuentos){
        String rutaFxml = RutasVista.CREAR_SERVICIO_VIEW;
        try {
            FXMLLoader loader = CargadorVistas.obtenerLoaderConfigurado(rutaFxml);
            Parent root = loader.load();
            CrearServicioControlador controlador = loader.getController();
            controlador.cargarDatos(listaObservableServicios, listaImpuestos, listaDescuentos);
            Stage stageCrear = new Stage();
            stageCrear.setTitle("Creando Servicio");
            stageCrear.initModality(Modality.APPLICATION_MODAL);
            stageCrear.setResizable(false);
            Scene escenaCrear = new Scene(root);
            stageCrear.setScene(escenaCrear);
            stageCrear.showAndWait();
        } catch (IOException e) {
            throw new CargarVistaException(rutaFxml, "NO se pudo Cargar el Archivo FXML.", e);
        }
    }


    @FXML
    void cambiarEstadoServicio(ActionEvent event) {
        cambiarEstadoServicio();
    }

    private void cambiarEstadoServicio(){
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención",
                    "Por favor, Selecciona un Servicio de la Tabla para Cambiar su Estado.");
            return;
        }
        boolean esActivo = seleccionado.estado().equalsIgnoreCase("Activo");
        String accion = esActivo ? "Desactivar" : "Activar";
        Optional<ButtonType> respuesta = mostrarAlerta(Alert.AlertType.CONFIRMATION, "Confirmar Cambio de Estado",
                "¿Estás Seguro de que Deseas " + accion + " el Servicio:\n" +
                        seleccionado.codigo() + " - " + seleccionado.nombre() + "?");
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.orquestadorServicios.cambiarEstadoServicio(seleccionado.codigo());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Estado del Servicio ha sido Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "NO se pudo Completar la Acción",
                        "Error:  " + e.getMessage());
            } catch (ServicioNoEncontradoException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Servicio NO Encontrado",
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

