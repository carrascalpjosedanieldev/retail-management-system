package RetailManagementSystem.vista.controladores.gestionarTienda.gestionarPoliticasV;

import RetailManagementSystem.aplicacion.dto.gestion.PoliticaVencimientoDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorPoliticaVencimiento;
import RetailManagementSystem.dominio.excepciones.autenticacionYSeguridad.AccesoDenegadoException;
import RetailManagementSystem.infraestructura.seguridad.PermisosApp;
import RetailManagementSystem.infraestructura.seguridad.ValidadorSeguridad;
import RetailManagementSystem.vista.controladores.gestionarTienda.GestionarTiendaControlador;
import RetailManagementSystem.vista.utilidades.CargadorVistas;
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
import javafx.scene.control.*;
import javafx.stage.Window;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GestionPoliticasVencimientoControlador {

    //ATRIBUTOS:

    @FXML private TableView<PoliticaVencimientoDTO> tablaPoliticasVencimiento;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colId;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colNombre;
    @FXML private TableColumn<PoliticaVencimientoDTO, Integer> colDiasUmbral;
    @FXML private TableColumn<PoliticaVencimientoDTO, BigDecimal> colPorcentaje;
    @FXML private TableColumn<PoliticaVencimientoDTO, String> colEstado;
    @FXML private TextField txtBuscar;
    @FXML private Button btnCambiarEstado;
    @FXML private Button btnModificar;
    @FXML private Button btnNuevo;

    private UsuarioDTOCompleto usuarioActual;

    private final OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento;

    private final ObservableList<PoliticaVencimientoDTO> listaObservablePoliticasVencimiento = FXCollections.observableArrayList();

    //CONSTRUCTOR:

    public GestionPoliticasVencimientoControlador(OrquestadorPoliticaVencimiento orquestadorPoliticaVencimiento) {
        this.orquestadorPoliticaVencimiento = orquestadorPoliticaVencimiento;
    }

    //MÉTODOS:

    public void cargarDatos(UsuarioDTOCompleto usuarioActual){
        ValidadorSeguridad.exigirAlgunPermiso(usuarioActual, List.of(
                PermisosApp.VER_POLITICAS_V,
                PermisosApp.REGISTRAR_POLITICAS_V,
                PermisosApp.MODIFICAR_POLITICAS_V,
                PermisosApp.CAMBIAR_ESTADO_POLITICAS_V
        ));
        this.usuarioActual = usuarioActual;
        protegerBoton(btnNuevo, PermisosApp.REGISTRAR_POLITICAS_V);
        protegerBoton(btnModificar, PermisosApp.MODIFICAR_POLITICAS_V);
        protegerBoton(btnCambiarEstado, PermisosApp.CAMBIAR_ESTADO_POLITICAS_V);
    }

    private void protegerBoton(Button boton, String permisoRequerido) {
        boolean tieneAcceso = this.usuarioActual.tienePermiso(permisoRequerido);
        boton.setVisible(tieneAcceso);
        boton.setManaged(tieneAcceso);
    }

    private Window getVentana(){
        return tablaPoliticasVencimiento.getScene() != null ? tablaPoliticasVencimiento.getScene().getWindow() : null;
    }

    @FXML
    public void initialize() {
        configurarColumnasTabla();
        configurarFiltroBusqueda();
        cargarDatosTabla();
    }

    private void configurarColumnasTabla() {
        colId.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().idPoliticaVencimiento())
        );
        colNombre.setCellValueFactory(celda -> new SimpleStringProperty(
                celda.getValue().nombrePolitica())
        );
        colDiasUmbral.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().diasUmbral())
        );
        colPorcentaje.setCellValueFactory(celda -> new SimpleObjectProperty<>(
                celda.getValue().porcentajeDescuento())
        );
        colEstado.setCellValueFactory(celda -> {
            boolean esActiva = celda.getValue().activo();
            String textoEstado = esActiva ? "Activa" : "Inactiva";
            return new SimpleStringProperty(textoEstado);
        });
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);
                getStyleClass().removeAll("estado-activo", "estado-inactivo");
                if (empty || estado == null) {
                    setText(null);
                } else {
                    setText(estado);
                    String estiloCss = estado.equalsIgnoreCase("Activa") ? "estado-activo" : "estado-inactivo";
                    getStyleClass().add(estiloCss);
                }
            }
        });
    }

    private void configurarFiltroBusqueda() {
        FilteredList<PoliticaVencimientoDTO> listaFiltrada = new FilteredList<>(
                listaObservablePoliticasVencimiento, b -> true
        );
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            listaFiltrada.setPredicate(politicaVencimiento -> {
                if (valorNuevo == null || valorNuevo.isBlank()) {
                    return true;
                }
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
        CompletableFuture.supplyAsync(
                this.orquestadorPoliticaVencimiento::obtenerTodasLasPoliticasV
        ).thenAccept(listaPoliticasV ->
            Platform.runLater(()->
                listaObservablePoliticasVencimiento.setAll(listaPoliticasV)
            )
        ).exceptionally(ex ->{
            Platform.runLater(()->{
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
                volverAGestionTienda();
            });
            return null;
        });
    }


    @FXML
    private void abrirFormularioEdicion(ActionEvent event) {
        PoliticaVencimientoDTO seleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Modificarlo."
            );
            return;
        }
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.EDITAR_POLITICA_V_VIEW,
                    "Editando Política de Vencimiento", getVentana(),
                    (EditarPoliticaVencimientoControlador c)->{
                        c.cargarDatos(this.usuarioActual, seleccionado, listaObservablePoliticasVencimiento);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void abrirFormularioNuevo(ActionEvent event) {
        try {
            CargadorVistas.abrirModalConInyeccion(
                    RutasVista.CREAR_POLITiCA_V_VIEW,
                    "Creando Política de Vencimiento", getVentana(),
                    (CrearPoliticaVencimiento c)->{
                        c.cargarDatos(this.usuarioActual, listaObservablePoliticasVencimiento);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


    @FXML
    private void cambiarEstadoPoliticaV(ActionEvent event) {
        cambiarEstadoPoliticaV();
    }

    private void cambiarEstadoPoliticaV(){
        PoliticaVencimientoDTO politicaSeleccionado = tablaPoliticasVencimiento.getSelectionModel().getSelectedItem();
        if (politicaSeleccionado == null) {
            GestorAlertas.mostrarAlertaWarning(
                    getVentana(), "Atención", null,
                    "Por favor, Selecciona una Política de Vencimiento de la Tabla para Cambiar su Estado."
            );
            return;
        }
        if (!GestorAlertas.mostrarConfirmacion(getVentana(), "Confirmar", null,
                "¿Estás Seguro de Cambiar el Estado de la Política de Vencimiento?")) {
            return;
        }
        CompletableFuture.runAsync(()->
            this.orquestadorPoliticaVencimiento.cambiarEstadoPoliticaV(
                    this.usuarioActual, politicaSeleccionado.idPoliticaVencimiento()
            )
        ).thenRun(()->
            Platform.runLater(()->{
                GestorAlertas.mostrarAlertaInformacion(
                        getVentana(), "Éxito", null,
                        "El Estado se ha Actualizado Correctamente."
                );
                PoliticaVencimientoDTO actualizado = new PoliticaVencimientoDTO(
                        politicaSeleccionado.idPoliticaVencimiento(),
                        politicaSeleccionado.nombrePolitica(),
                        politicaSeleccionado.diasUmbral(),
                        politicaSeleccionado.porcentajeDescuento(),
                        !politicaSeleccionado.activo()
                );
                int indice = listaObservablePoliticasVencimiento.indexOf(politicaSeleccionado);
                listaObservablePoliticasVencimiento.set(indice, actualizado);
            })
        ).exceptionally(ex -> {
            Platform.runLater(() -> {
                Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                GestorAlertas.mostrarAlertaError(
                        getVentana(), "Error Critico",
                        "NO se pudo Completar la Acción.",
                        "Notificale al Administrador este Error:\n" + causa.getMessage()
                );
            });
            return null;
        });
    }


    @FXML
    private void volverAlPanel(ActionEvent event) {
        volverAGestionTienda();
    }

    private void volverAGestionTienda(){
        try {
            CargadorVistas.cambiarPantallaInyectada(
                    getVentana(),
                    RutasVista.GESTIONAR_TIENDA_VIEW,
                    (GestionarTiendaControlador c) -> {
                        c.cargarUsuario(this.usuarioActual);
                    }
            );
        } catch (AccesoDenegadoException ex){
            GestorAlertas.mostrarAlertaAccesoDenegado(getVentana(), ex);
        }
    }


}//===================================================================================================================//

