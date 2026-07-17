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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GestionServiciosControlador {

    @FXML
    private TableColumn<ServicioDTO, String> colCodigo;

    @FXML
    private TableColumn<ServicioDTO, String> colDescuento;

    @FXML
    private TableColumn<ServicioDTO, String> colEstado;

    @FXML
    private TableColumn<ServicioDTO, String> colImpuesto;

    @FXML
    private TableColumn<ServicioDTO, String> colNombre;

    @FXML
    private TableColumn<ServicioDTO, BigDecimal> colPrecioBase;

    @FXML
    private TableColumn<ServicioDTO, BigDecimal> colPrecioFinal;

    @FXML
    private TableView<ServicioDTO> tablaServicios;

    @FXML
    private TextField txtBuscar;

    private final ServicioServicios servicioServicios = FabricaServicios.obtenerServicioServicios();

    private final ServicioImpuestos servicioImpuestos = FabricaServicios.obtenerServicioImpuestos();

    private final ServicioDescuentos servicioDescuentos = FabricaServicios.obtenerServicioDescuentos();

    private final EnsambladorDTOServicio ensambladorDTOServicio = FabricaEnsambladores.obtenerEnsambladorDTOServicio();

    private final ObservableList<ServicioDTO> listaObservableServicios = FXCollections.observableArrayList();

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        DialogPane pane = alerta.getDialogPane();
        pane.setMinHeight(180);
        pane.setMinWidth(400);
        alerta.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_SERVICIOS),
                        "¡CRÍTICO! No se encontró el archivo CSS."
                ).toExternalForm()
        );
        alerta.showAndWait();
    }

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().codigo()));
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombre()));
        colPrecioBase.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().precioBase())); // ¡Esta faltaba!
        colImpuesto.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombreImpuesto()));
        colDescuento.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().nombreDescuento()));
        colPrecioFinal.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().precioFinal())); // ¡Esta faltaba!
        colEstado.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().estado()));
        colPrecioBase.setCellFactory(columna -> new TableCell<ServicioDTO, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f", precio));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #0f172a;");
                }
            }
        });
        colPrecioFinal.setCellFactory(columna -> new TableCell<ServicioDTO, BigDecimal>() {
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
        LocalDate fechaActual = obtenerFecha();
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
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un servicio de la tabla para modificarlo.");
            return;
        }
        List<Impuesto> listaImpuestos = this.servicioImpuestos.obtenerImpuestosActivos();
        List<Descuento> listaDescuentos = this.servicioDescuentos.obtenerDescuentosActivos();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modificar Servicio");
        dialog.setHeaderText("Editando el servicio: " + seleccionado.codigo() + " \n " + seleccionado.nombre());
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(480);
        dialogPane.setPrefHeight(450);
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_SERVICIOS),
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
        txtNombre.setPrefWidth(250);
        TextField txtPrecioBase = new TextField(seleccionado.precioBase().toString());
        ComboBox<Impuesto> cbImpuestos = new ComboBox<>(FXCollections.observableArrayList(listaImpuestos));
        cbImpuestos.setMaxWidth(Double.MAX_VALUE);
        cbImpuestos.setConverter(new javafx.util.StringConverter<Impuesto>() {
            @Override
            public String toString(Impuesto imp) {
                return (imp == null) ? "" : imp.getNombre() + " (" + imp.getPorcentaje() + "%)";
            }
            @Override
            public Impuesto fromString(String string) { return null; }
        });
        ComboBox<Descuento> cbDescuentos = new ComboBox<>(FXCollections.observableArrayList(listaDescuentos));
        cbDescuentos.setMaxWidth(Double.MAX_VALUE);
        cbDescuentos.setConverter(new javafx.util.StringConverter<Descuento>() {
            @Override
            public String toString(Descuento desc) {
                return (desc == null) ? "" : desc.getNombre() + " (" + desc.getPorcentaje() + "%)";
            }
            @Override
            public Descuento fromString(String string) { return null; }
        });
        for (Impuesto imp : listaImpuestos) {
            if (imp.getId() == seleccionado.idImpuesto()) {
                cbImpuestos.getSelectionModel().select(imp);
                break;
            }
        }
        for (Descuento desc : listaDescuentos) {
            if (desc.getId() == seleccionado.idDescuento()) {
                cbDescuentos.getSelectionModel().select(desc);
                break;
            }
        }
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Precio Base:"), 0, 1);
        grid.add(txtPrecioBase, 1, 1);
        grid.add(new Label("Impuesto:"), 0, 2);
        grid.add(cbImpuestos, 1, 2);
        grid.add(new Label("Descuento:"), 0, 3);
        grid.add(cbDescuentos, 1, 3);
        dialogPane.setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnActualizar) {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoPrecioBaseTexto = txtPrecioBase.getText().trim();
            Impuesto impuestoSeleccionado = cbImpuestos.getValue();
            Descuento descuentoSeleccionado = cbDescuentos.getValue();
            try {
                if (nuevoNombre.isEmpty() || nuevoPrecioBaseTexto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre y el precio base no pueden estar vacíos.");
                    return;
                }
                if (impuestoSeleccionado == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Debes seleccionar un impuesto válido.");
                    return;
                }
                BigDecimal nuevoPrecioBase = FormateadorNumeros.stringABigDecimal(nuevoPrecioBaseTexto);
                int idDescuento = (descuentoSeleccionado != null) ? descuentoSeleccionado.getId() : 1;
                this.servicioServicios.actualizarServicio(
                        seleccionado.codigo(),
                        nuevoNombre,
                        nuevoPrecioBase,
                        impuestoSeleccionado.getId(),
                        idDescuento
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El servicio ha sido actualizado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "Por favor, ingresa un precio base numérico válido.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo actualizar el servicio: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void abrirFormularioNuevo(ActionEvent event) {
        List<Impuesto> listaImpuestos = this.servicioImpuestos.obtenerImpuestosActivos();
        List<Descuento> listaDescuentos = this.servicioDescuentos.obtenerDescuentosActivos();
        if (listaImpuestos.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Configuración Requerida",
                    "No puedes crear un servicio si no tienes al menos un Impuesto activo en el sistema.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registrar Nuevo Servicio");
        dialog.setHeaderText("Ingresa los datos del nuevo servicio");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(480);
        dialogPane.setPrefHeight(450);
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_SERVICIOS),
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
        txtNombre.setPromptText("Nombre del servicio...");
        txtNombre.setPrefWidth(250);
        TextField txtPrecioBase = new TextField();
        txtPrecioBase.setPromptText("Ej: 1500.00");
        ComboBox<Impuesto> cbImpuestos = new ComboBox<>();
        cbImpuestos.setItems(FXCollections.observableArrayList(listaImpuestos));
        cbImpuestos.setPromptText("Seleccione un impuesto...");
        cbImpuestos.setMaxWidth(Double.MAX_VALUE);
        cbImpuestos.setConverter(new javafx.util.StringConverter<Impuesto>() {
            @Override
            public String toString(Impuesto imp) {
                return (imp == null) ? "" : imp.getNombre() + " (" + imp.getPorcentaje() + "%)";
            }
            @Override
            public Impuesto fromString(String string) {
                return null;
            }
        });
        ComboBox<Descuento> cbDescuentos = new ComboBox<>();
        cbDescuentos.setItems(FXCollections.observableArrayList(listaDescuentos));
        cbDescuentos.setPromptText("Seleccione un descuento (Opcional)...");
        cbDescuentos.setMaxWidth(Double.MAX_VALUE);
        cbDescuentos.setConverter(new javafx.util.StringConverter<Descuento>() {
            @Override
            public String toString(Descuento desc) {
                return (desc == null) ? "" : desc.getNombre() + " (" + desc.getPorcentaje() + "%)";
            }
            @Override
            public Descuento fromString(String string) {
                return null;
            }
        });
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Precio Base:"), 0, 1);
        grid.add(txtPrecioBase, 1, 1);
        grid.add(new Label("Impuesto:"), 0, 2);
        grid.add(cbImpuestos, 1, 2);
        grid.add(new Label("Descuento:"), 0, 3);
        grid.add(cbDescuentos, 1, 3);
        dialogPane.setContent(grid);
        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnGuardar) {
            String nombre = txtNombre.getText().trim();
            String precioBaseTexto = txtPrecioBase.getText().trim();
            Impuesto impuestoSeleccionado = cbImpuestos.getValue();
            Descuento descuentoSeleccionado = cbDescuentos.getValue();
            try {
                if (nombre.isEmpty() || precioBaseTexto.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre y el precio base son obligatorios.");
                    return;
                }
                if (impuestoSeleccionado == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Debes seleccionar un impuesto para el servicio.");
                    return;
                }
                BigDecimal precioBase = FormateadorNumeros.stringABigDecimal(precioBaseTexto);
                int idDescuento = (descuentoSeleccionado != null) ? descuentoSeleccionado.getId() : 1;
                this.servicioServicios.registrarServicioNuevo(
                        nombre,
                        precioBase,
                        impuestoSeleccionado.getId(),
                        idDescuento
                );
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El servicio ha sido registrado correctamente.");
                cargarDatosTabla();
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Número Inválido", "Por favor, ingresa un precio base numérico válido.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo registrar el servicio: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void cambiarEstadoServicio(ActionEvent event) {
        ServicioDTO seleccionado = tablaServicios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Por favor, selecciona un servicio de la tabla para cambiar su estado.");
            return;
        }
        boolean esActivo = seleccionado.estado().equalsIgnoreCase("Activo");
        String accion = esActivo ? "inactivar" : "activar";
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro de que deseas " + accion + " el servicio:\n" +
                seleccionado.codigo() + " - " + seleccionado.nombre() + "?");
        confirmacion.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(RutasVista.ESTILOS_CSS_SERVICIOS),
                        "¡CRÍTICO! No se encontró el archivo CSS."
                ).toExternalForm()
        );
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                this.servicioServicios.cambiarEstadoServicio(seleccionado.codigo());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El estado del servicio ha sido actualizado correctamente.");
                cargarDatosTabla();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo actualizar el estado en la base de datos.");
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
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
