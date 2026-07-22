package ProyectoPropio1.vista.controladores;

import ProyectoPropio1.dominio.Descuento;
import ProyectoPropio1.dominio.Impuesto;
import ProyectoPropio1.dto.ServicioDTO;
import ProyectoPropio1.servicios.aplicacion.ServicioDescuentos;
import ProyectoPropio1.servicios.aplicacion.ServicioImpuestos;
import ProyectoPropio1.servicios.aplicacion.ServicioServicios;
import ProyectoPropio1.servicios.ensambladores.EnsambladorDTOServicio;
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
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final ServicioServicios servicioServicios = FabricaServicios.obtenerServicioServicios();

    private final ServicioImpuestos servicioImpuestos = FabricaServicios.obtenerServicioImpuestos();

    private final ServicioDescuentos servicioDescuentos = FabricaServicios.obtenerServicioDescuentos();

    private final EnsambladorDTOServicio ensambladorDTOServicio = FabricaEnsambladores.obtenerEnsambladorDTOServicio();

    private final ObservableList<ServicioDTO> listaObservableServicios = FXCollections.observableArrayList();

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

    private void configurarColumnaMoneda(TableColumn<ServicioDTO, BigDecimal> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$ %,.2f", precio));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");
                }
            }
        });
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

    private Dialog<ButtonType> crearDialogoBase(String titulo, String cabecera) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(cabecera);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(480);
        dialogPane.setPrefHeight(450);
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

    private void validarFormulario(String nombre, String precioBaseTexto, Impuesto impuesto, Descuento descuento) throws IllegalArgumentException {
        if (nombre == null || nombre.isEmpty() || precioBaseTexto == null || precioBaseTexto.isEmpty()) {
            throw new IllegalArgumentException("El Nombre y el Precio Base son Obligatorios.");
        }
        if (impuesto == null) {
            throw new IllegalArgumentException("Debes Seleccionar un Impuesto para el Servicio.");
        }
        if (descuento == null){
            throw new IllegalArgumentException("Debes Seleccionar un Descuento para el Servicio (De preferencia que sea -Sin Descuento-).");
        }
    }


    @FXML
    public void initialize() {
        inicializar();
    }

    private void inicializar(){
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().codigo()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colPrecioBase.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().precioBase())); // ¡Esta faltaba!
        colImpuesto.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombreImpuesto()));
        colDescuento.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombreDescuento()));
        colPrecioFinal.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().precioFinal())); // ¡Esta faltaba!
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().estado()));
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
        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        LocalDate fechaActual = LocalDate.now();
        List<ServicioDTO> todosLosServicios = new ArrayList<>();
        List<ServicioDTO> serviciosActivos = this.ensambladorDTOServicio.ensamblarDatosCatalogoServicios(
                this.servicioServicios.obtenerServiciosActivos(), fechaActual
        );
        List<ServicioDTO> serviciosInactivos = this.ensambladorDTOServicio.ensamblarDatosCatalogoServicios(
                this.servicioServicios.obtenerServiciosInactivos(), fechaActual
        );
        todosLosServicios.addAll(serviciosActivos);
        todosLosServicios.addAll(serviciosInactivos);
        listaObservableServicios.setAll(todosLosServicios);
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
        List<Impuesto> listaImpuestos = this.servicioImpuestos.obtenerImpuestosActivos();
        List<Descuento> listaDescuentos = this.servicioDescuentos.obtenerDescuentosActivos();
        Dialog<ButtonType> dialog = crearDialogoBase("Modificar Servicio",
                "Editando el servicio: " + seleccionado.codigo() + " \n " + seleccionado.nombre());
        ButtonType btnActualizar = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnActualizar, ButtonType.CANCEL);
        TextField txtNombre = new TextField(seleccionado.nombre());
        txtNombre.setPrefWidth(250);
        TextField txtPrecioBase = new TextField(seleccionado.precioBase().toString());
        ComboBox<Impuesto> cbImpuestos = new ComboBox<>();
        configurarComboBox(cbImpuestos, listaImpuestos, "Seleccione un Impuesto...",
                imp -> imp.getNombre() + " (" + imp.getPorcentaje() + "%)");
        ComboBox<Descuento> cbDescuentos = new ComboBox<>();
        configurarComboBox(cbDescuentos, listaDescuentos, "Seleccione un Descuento...",
                desc -> desc.getNombre() + " (" + desc.getPorcentaje() + "%)");
        listaImpuestos.stream().filter(imp -> imp.getId() == seleccionado.idImpuesto()).findFirst().ifPresent(cbImpuestos.getSelectionModel()::select);
        listaDescuentos.stream().filter(desc -> desc.getId() == seleccionado.idDescuento()).findFirst().ifPresent(cbDescuentos.getSelectionModel()::select);
        GridPane grid = crearGridPane();
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Precio Base:"), 0, 1);
        grid.add(txtPrecioBase, 1, 1);
        grid.add(new Label("Impuesto:"), 0, 2);
        grid.add(cbImpuestos, 1, 2);
        grid.add(new Label("Descuento:"), 0, 3);
        grid.add(cbDescuentos, 1, 3);
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnActualizar) {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoPrecioBaseTexto = txtPrecioBase.getText().trim();
            Impuesto impuestoSeleccionado = cbImpuestos.getValue();
            Descuento descuentoSeleccionado = cbDescuentos.getValue();
            try {
                validarFormulario(nuevoNombre, nuevoPrecioBaseTexto, impuestoSeleccionado, descuentoSeleccionado);
                BigDecimal nuevoPrecioBase = FormateadorNumeros.stringAPrecio(nuevoPrecioBaseTexto);
                this.servicioServicios.actualizarServicio(
                        seleccionado.codigo(), nuevoNombre, nuevoPrecioBase, impuestoSeleccionado.getId(),
                        descuentoSeleccionado.getId()
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Servicio ha sido Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Por favor, Ingresa un Precio Base numérico válido.");
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error al Editar el Servicio",
                        "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se pudo Actualizar el Servicio en la Base de Datos.\n" +
                                "Error:  " + e.getMessage());
            }
        }
    }


    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        abrirFormularioNuevo();
    }

    private void abrirFormularioNuevo(){
        List<Impuesto> listaImpuestos = this.servicioImpuestos.obtenerImpuestosActivos();
        List<Descuento> listaDescuentos = this.servicioDescuentos.obtenerDescuentosActivos();
        if (listaImpuestos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Configuración Requerida",
                    "NO puedes Crear un Servicio si no tienes al menos un Impuesto Activo en el Sistema.");
            return;
        }
        if (listaDescuentos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Configuración Requerida",
                    "NO puedes Crear un Servicio si no tienes al menos un Descuento Activo en el Sistema (De preferencia que sea -Sin Descuento-).");
            return;
        }
        Dialog<ButtonType> dialog = crearDialogoBase("Registrar Nuevo Servicio",
                "Ingresa los Datos del nuevo Servicio");
        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del Servicio...");
        txtNombre.setPrefWidth(250);
        TextField txtPrecioBase = new TextField();
        txtPrecioBase.setPromptText("Ej: 1500.00");
        ComboBox<Impuesto> cbImpuestos = new ComboBox<>();
        configurarComboBox(cbImpuestos, listaImpuestos, "Seleccione un Impuesto...",
                imp -> imp.getNombre() + " (" + imp.getPorcentaje() + "%)");
        ComboBox<Descuento> cbDescuentos = new ComboBox<>();
        configurarComboBox(cbDescuentos, listaDescuentos, "Seleccione un Descuento...",
                desc -> desc.getNombre() + " (" + desc.getPorcentaje() + "%)");
        GridPane grid = crearGridPane();
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Precio Base:"), 0, 1);
        grid.add(txtPrecioBase, 1, 1);
        grid.add(new Label("Impuesto:"), 0, 2);
        grid.add(cbImpuestos, 1, 2);
        grid.add(new Label("Descuento:"), 0, 3);
        grid.add(cbDescuentos, 1, 3);
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnGuardar) {
            String nombre = txtNombre.getText().trim();
            String precioBaseTexto = txtPrecioBase.getText().trim();
            Impuesto impuestoSeleccionado = cbImpuestos.getValue();
            Descuento descuentoSeleccionado = cbDescuentos.getValue();
            try {
                validarFormulario(nombre, precioBaseTexto, impuestoSeleccionado, descuentoSeleccionado);
                BigDecimal precioBase = FormateadorNumeros.stringAPrecio(precioBaseTexto);
                this.servicioServicios.registrarServicioNuevo(
                        nombre,
                        precioBase,
                        impuestoSeleccionado.getId(),
                        descuentoSeleccionado.getId()
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Servicio ha sido Registrado Correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido",
                        "Por favor, Ingresa un Precio Base numérico válido.");
            } catch (IllegalArgumentException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Error al Registrar el Servicio",
                        "Hay un Error en los Datos Ingresados:\n" + e.getMessage());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se pudo Registrar el Servicio en la Base de Datos:\n" +
                                "Error:  " + e.getMessage());
            }
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
                this.servicioServicios.cambiarEstadoServicio(seleccionado.codigo());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "El Estado del Servicio ha sido Actualizado Correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                        "NO se pudo Actualizar el Estado en la Base de Datos.");
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
                    Alert.AlertType.ERROR, "Error de Navegación",
                    "Ocurrió un problema al intentar volver al panel de Gestión.\n" +
                            "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎."
            );
        }
    }

}//===================================================================================================================//

