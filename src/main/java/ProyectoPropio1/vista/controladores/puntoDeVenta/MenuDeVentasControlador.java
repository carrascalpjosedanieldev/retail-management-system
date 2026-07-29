package ProyectoPropio1.vista.controladores.puntoDeVenta;

import ProyectoPropio1.aplicacion.dto.ItemCarritoDTO;
import ProyectoPropio1.aplicacion.orquestadores.OrquestadorVentas;
import ProyectoPropio1.vista.utilidades.CargadorVistas;
import ProyectoPropio1.vista.utilidades.FormateadorNumeros;
import ProyectoPropio1.vista.utilidades.RutasVista;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MenuDeVentasControlador {

    //ATRIBUTOS:

    @FXML private Button btnVolver;
    @FXML private TextField txtCodigo;
    @FXML private Button btnAgregar;
    @FXML private TableView<ItemCarritoDTO> tablaCarrito;
    @FXML private TableColumn<ItemCarritoDTO, String> colCodigo;
    @FXML private TableColumn<ItemCarritoDTO, String> colDescripcion;
    @FXML private TableColumn<ItemCarritoDTO, Integer> colCantidad;
    @FXML private TableColumn<ItemCarritoDTO, BigDecimal> colPrecio;
    @FXML private TableColumn<ItemCarritoDTO, BigDecimal> colSubtotal;
    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuestos;
    @FXML private Label lblTotalGeneral;
    @FXML private Button btnAumentarCant;
    @FXML private Button btnReducirCant;
    @FXML private Button btnEliminarItem;
    @FXML private Button btnCancelarVenta;
    @FXML private Button btnProcesarVenta;

    private final OrquestadorVentas orquestadorVentas;

    private ObservableList<ItemCarritoDTO> listaCarrito;

    //CONSTRUCTOR:

    public MenuDeVentasControlador(OrquestadorVentas orquestadorVentas) {
        this.orquestadorVentas = orquestadorVentas;
    }

    //MÉTODOS:

    private LocalDate obtenerFecha(){
        return LocalDate.now();
    }

    private void mostrarAlertaError(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            alert.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        alert.showAndWait();
    }


    @FXML
    public void initialize() {
        listaCarrito = FXCollections.observableArrayList();
        tablaCarrito.setItems(listaCarrito);
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().codigoArticulo()));
        colDescripcion.setCellValueFactory(cellData -> {
            ItemCarritoDTO item = cellData.getValue();
            String descripcionArmada = item.tipoItem() + ": " + item.nombreArticulo();
            return new SimpleStringProperty(descripcionArmada);
        });        colCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().cantidad()).asObject());
        colPrecio.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().precioUnitario()));
        colPrecio.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(precio));
                }
            }
        });
        colSubtotal.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().subtotal()));
        colSubtotal.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(FormateadorNumeros.formatoMoneda(subtotal));
                }
            }
        });
        btnAumentarCant.setDisable(true);
        btnReducirCant.setDisable(true);
        btnEliminarItem.setDisable(true);
        tablaCarrito.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = (newSelection != null);
            btnAumentarCant.setDisable(!haySeleccion);
            btnReducirCant.setDisable(!haySeleccion);
            btnEliminarItem.setDisable(!haySeleccion);
        });
        btnProcesarVenta.disableProperty().bind(Bindings.isEmpty(listaCarrito));
        btnCancelarVenta.disableProperty().bind(Bindings.isEmpty(listaCarrito));
        txtCodigo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                agregarItem(null);
            }
        });
        Platform.runLater(() -> txtCodigo.requestFocus());
        this.orquestadorVentas.abrirCarritoSesion();
        actualizarTotales();
    }

    private void actualizarTotales() {
        lblSubtotal.setText("$ 0.00");
        lblImpuestos.setText("$ 0.00");
        lblTotalGeneral.setText("$ 0.00");
    }


    @FXML
    public void volverAlMenu(ActionEvent event) {
        if (listaCarrito != null && !listaCarrito.isEmpty()) {
            Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            alertaConfirmacion.setTitle("Venta en Curso");
            alertaConfirmacion.setHeaderText("¿Está Seguro de que desea Salir?");
            alertaConfirmacion.setContentText("""
                    Tiene ítems en el Carrito. Si sale Ahora, se Cancelará la Venta y Perderá Todo el Progreso.
                    
                    ¿Desea Continuar y Salir?""");
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
            if (urlCss != null) {
                alertaConfirmacion.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
            }
            Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }
            try {
                orquestadorVentas.cancelarCompraTotal();
            } catch (Exception e) {
                System.out.println("No se pudo limpiar el carrito en el backend: " + e.getMessage());
            }
        }
        try {
            Stage stageActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            CargadorVistas.cambiarPantalla(stageActual, RutasVista.PANEL_DE_CONTROL_POS_VIEW);
        } catch (Exception e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de Navegación");
            alerta.setHeaderText("NO se pudo Cargar la Pantalla");
            alerta.setContentText("Ocurrió un Problema al Intentar Abrir la Vista.\n" +
                    "Ruta Solicitada: " + RutasVista.PANEL_DE_CONTROL_POS_VIEW + "\n" +
                    "Si el problema persiste, contacte al Administrador o al Creador Original 😎 Jose Daniel 😎.");
            DialogPane panelAlerta = alerta.getDialogPane();
            panelAlerta.setPrefSize(500, 280);
            URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_PANEL_DE_CONTROL_POS);
            if (urlCss != null) {
                panelAlerta.getStylesheets().add(urlCss.toExternalForm());
            } else {
                panelAlerta.setPrefSize(500, 180);
            }
            alerta.showAndWait();
        }
    }


    @FXML
    public void agregarItem(ActionEvent event) {
        agregarItem();
    }

    private void agregarItem(){
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            return;
        }
        try {
            this.orquestadorVentas.agregarItemAlCarrito(codigo, obtenerFecha());
            actualizarTablaYTotales();
            txtCodigo.clear();
        } catch (Exception e) {
            mostrarAlertaError(Alert.AlertType.WARNING, "Error al agregar", e.getMessage());
            txtCodigo.selectAll();
        } finally {
            txtCodigo.requestFocus();
        }
    }

    private void actualizarTablaYTotales() {
        List<ItemCarritoDTO> itemsDelCarrito = this.orquestadorVentas
                .obtenerVistaPreviaCarrito(obtenerFecha())
                .carritoItems();
        listaCarrito.setAll(itemsDelCarrito);
        BigDecimal subtotalVenta = BigDecimal.ZERO;
        BigDecimal impuestos = BigDecimal.ZERO;
        for (ItemCarritoDTO item : itemsDelCarrito) {
            subtotalVenta = subtotalVenta.add(item.subtotal());
            impuestos = impuestos.add(item.impuestos());
        }
        BigDecimal totalGeneral = subtotalVenta.add(impuestos);
        lblSubtotal.setText(FormateadorNumeros.formatoMoneda(subtotalVenta));
        lblImpuestos.setText(FormateadorNumeros.formatoMoneda(impuestos));
        lblTotalGeneral.setText(FormateadorNumeros.formatoMoneda(totalGeneral));
    }


    @FXML
    public void eliminarItemSeleccionado(ActionEvent event) {
        eliminarItemSeleccionado();
    }

    private void eliminarItemSeleccionado(){
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText("¿Eliminar ítem?");
        alertaConfirmacion.setContentText("¿Está seguro de que desea retirar -" + itemSeleccionado.nombreArticulo() +
                "- del carrito?");
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            alertaConfirmacion.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            txtCodigo.requestFocus();
            return;
        }
        try {
            this.orquestadorVentas.eliminarItemDelCarrito(itemSeleccionado.codigoArticulo());
            mostrarAlertaError(Alert.AlertType.INFORMATION, "Éxito",
                    "Item Eliminado del Carrito con Éxito ");
            actualizarTablaYTotales();
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Error al Intentar Eliminar el ítem.";
            mostrarAlertaError(Alert.AlertType.WARNING, "Error al Eliminar", mensaje);
        } finally {
            txtCodigo.requestFocus();
        }
    }


    @FXML
    public void cancelarVenta(ActionEvent event) {
        cancelarVenta();
    }

    private void cancelarVenta(){
        if (listaCarrito == null || listaCarrito.isEmpty()) {
            return;
        }
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Cancelar Venta");
        alertaConfirmacion.setHeaderText("¿Desea Cancelar Toda la Venta?");
        alertaConfirmacion.setContentText("Se Eliminarán Todos los Productos del Carrito. Esta Acción NO se puede Deshacer.");
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            alertaConfirmacion.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                this.orquestadorVentas.cancelarCompraTotal();
                actualizarTablaYTotales();
                mostrarAlertaError(Alert.AlertType.INFORMATION, "Venta Cancelada",
                        "Se ha Cancelado la Venta y vaciado el Carrito con Éxito.");

            } catch (Exception e) {
                String mensaje = e.getMessage() != null ? e.getMessage() : "Error al Intentar Cancelar la Venta.";
                mostrarAlertaError(Alert.AlertType.WARNING, "Error al Cancelar", mensaje);
            } finally {
                txtCodigo.clear();
                txtCodigo.requestFocus();
            }
        } else {
            txtCodigo.requestFocus();
        }
    }


    @FXML
    public void procesarVenta(ActionEvent event) {

    }


    @FXML
    public void aumentarCantidadSeleccionada(ActionEvent event) {
        aumentarCantidadSeleccionada();
    }

    private void aumentarCantidadSeleccionada(){
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        TextInputDialog dialogo = new TextInputDialog("1");
        dialogo.setTitle("Aumentar Cantidad");
        dialogo.setHeaderText("Aumentar unidades de: " + itemSeleccionado.nombreArticulo());
        dialogo.setContentText("Ingrese la cantidad adicional a agregar:");
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            dialogo.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isPresent()) {
            String entrada = resultado.get().trim();
            try {
                int cantidadAAumentar = Integer.parseInt(entrada);
                if (cantidadAAumentar <= 0) {
                    mostrarAlertaError(Alert.AlertType.WARNING, "Cantidad Inválida",
                            "La Cantidad a Aumentar debe ser Mayor a Cero (0).");
                    return;
                }
                this.orquestadorVentas.aumentarCantidadItem(
                        itemSeleccionado.codigoArticulo(), cantidadAAumentar, obtenerFecha()
                );
                actualizarTablaYTotales();
            } catch (NumberFormatException e) {
                mostrarAlertaError(Alert.AlertType.WARNING, "Entrada Inválida",
                        "Por favor, Ingrese un Número Entero Válido.");
            } catch (Exception e) {
                String mensaje = e.getMessage() != null ? e.getMessage() : "Error al Intentar Aumentar la Cantidad.";
                mostrarAlertaError(Alert.AlertType.WARNING, "Error al Aumentar", mensaje);
            } finally {
                txtCodigo.requestFocus();
            }
        } else {
            txtCodigo.requestFocus();
        }
    }


    @FXML
    public void reducirCantidadSeleccionada(ActionEvent event) {
        reducirCantidadSeleccionada();
    }

    private void reducirCantidadSeleccionada(){
        ItemCarritoDTO itemSeleccionado = tablaCarrito.getSelectionModel().getSelectedItem();
        if (itemSeleccionado == null) {
            return;
        }
        TextInputDialog dialogo = new TextInputDialog("1");
        dialogo.setTitle("Reducir Cantidad");
        dialogo.setHeaderText("Reducir unidades de: " + itemSeleccionado.nombreArticulo());
        dialogo.setContentText("Ingrese la cantidad a reducir:");
        URL urlCss = getClass().getResource(RutasVista.ESTILOS_CSS_MENU_DE_VENTAS);
        if (urlCss != null) {
            dialogo.getDialogPane().getStylesheets().add(urlCss.toExternalForm());
        }
        Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isPresent()) {
            String entrada = resultado.get().trim();
            try {
                int cantidadAReducir = Integer.parseInt(entrada);
                if (cantidadAReducir <= 0) {
                    mostrarAlertaError(Alert.AlertType.WARNING, "Cantidad Inválida",
                            "La cantidad a reducir debe ser mayor a cero (0).");
                    return;
                }
                this.orquestadorVentas.reducirCantidadItem(
                        itemSeleccionado.codigoArticulo(), cantidadAReducir
                );
                actualizarTablaYTotales();
            } catch (NumberFormatException e) {
                mostrarAlertaError(Alert.AlertType.WARNING, "Entrada Inválida",
                        "Por favor, ingrese un número entero válido.");
            } catch (Exception e) {
                String mensaje = e.getMessage() != null ? e.getMessage() : "Error al intentar reducir la cantidad.";
                mostrarAlertaError(Alert.AlertType.WARNING, "Error al Reducir", mensaje);
            } finally {
                txtCodigo.requestFocus();
            }
        } else {
            txtCodigo.requestFocus();
        }
    }


}//===================================================================================================================//

