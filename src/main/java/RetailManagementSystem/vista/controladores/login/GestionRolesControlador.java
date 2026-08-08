package RetailManagementSystem.vista.controladores.login;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.ensambladores.EnsambladorDTORol;
import RetailManagementSystem.vista.utilidades.GestorAlertas;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class GestionRolesControlador {

    //ATRIBUTOS:

    @FXML private TextField txtBuscar;
    @FXML private TableView<RolDTO> tablaRoles;
    @FXML private TableColumn<RolDTO, Integer> colId;
    @FXML private TableColumn<RolDTO, String> colNombre;
    @FXML private TableColumn<RolDTO, String> colPermisos;
    @FXML private TableColumn<RolDTO, String> colEstado;

    private final EnsambladorDTORol ensambladorDTORol;

    private final ObservableList<RolDTO> listaMaestraRoles = FXCollections.observableArrayList();

    private FilteredList<RolDTO> listaFiltrada;

    //CONSTRUCTOR:

    public GestionRolesControlador(EnsambladorDTORol ensambladorDTORol) {
        this.ensambladorDTORol = ensambladorDTORol;
    }

    //MÉTODOS:

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatosDesdeBD();
        configurarFiltroBusqueda();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPermisos.setCellValueFactory(new PropertyValueFactory<>("permisosAsignados"));
        colEstado.setCellValueFactory(cellData -> {
            boolean estaActivo = cellData.getValue().activo();
            return new SimpleStringProperty(estaActivo ? "Activo" : "Inactivo");
        });
    }

    private void cargarDatosDesdeBD() {
        try {
            List<RolDTO> datosBD = ;
            listaMaestraRoles.clear();
            if (datosBD != null && !datosBD.isEmpty()) {
                listaMaestraRoles.addAll(datosBD);
            }
        } catch (RuntimeException e) {
            GestorAlertas.mostrarError(
                    "Error de Carga",
                    "No se pudieron cargar los roles.",
                    "Detalle: " + e.getMessage()
            );
        }
    }

    private void configurarFiltroBusqueda() {
        listaFiltrada = new FilteredList<>(listaMaestraRoles, b -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(rol -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                String filtro = newValue.toLowerCase();
                boolean coincideId = String.valueOf(rol.idRol()).contains(filtro);
                boolean coincideNombre = rol.nombre().toLowerCase().contains(filtro);
                return coincideId || coincideNombre;
            });
        });
        tablaRoles.setItems(listaFiltrada);
    }


    @FXML
    private void abrirFormularioNuevo(ActionEvent event) {

    }


    @FXML
    private void abrirFormularioEdicion(ActionEvent event) {

    }


    @FXML
    private void cambiarEstadoRol(ActionEvent event) {

    }


    @FXML
    private void volverAlPanel(ActionEvent event) {

    }


}//===================================================================================================================//

