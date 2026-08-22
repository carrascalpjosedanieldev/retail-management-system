package RetailManagementSystem.vista.controladores.gestionarUsuarios;

import RetailManagementSystem.aplicacion.dto.seguridad.RolDTO;
import RetailManagementSystem.aplicacion.dto.seguridad.UsuarioDTOCompleto;
import RetailManagementSystem.aplicacion.orquestadores.OrquestadorUsuarios;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.concurrent.CompletableFuture;

public class GestionarRolesDeUsuarioControlador {

    //ATRIBUTOS:

    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private TableColumn<RolDTO, Integer> colIdActual;
    @FXML private TableColumn<RolDTO, Integer> colIdDisponible;
    @FXML private TableColumn<RolDTO, String> colNombreActual;
    @FXML private TableColumn<RolDTO, String> colNombreDisponible;
    @FXML private TableColumn<RolDTO, String> colActivoActual;
    @FXML private TableColumn<RolDTO, String> colActivoDisponible;
    @FXML private Label lblNombreUsuario;
    @FXML private TableView<RolDTO> tablaRolesActuales;
    @FXML private TableView<RolDTO> tablaRolesDisponibles;

    private Long idUsuario;

    private UsuarioDTOCompleto datosUsuario;

    private final OrquestadorUsuarios orquestadorUsuarios;

    //CONSTRUCTOR:

    public GestionarRolesDeUsuarioControlador(OrquestadorUsuarios orquestadorUsuarios) {
        this.orquestadorUsuarios = orquestadorUsuarios;
    }

    //MÉTODOS:

    public void cargarDatos(Long idUsuario){
        this.idUsuario = idUsuario;
        CompletableFuture.supplyAsync(()->
                this.orquestadorUsuarios.obtenerDatosTotalesUsuario(idUsuario)
        ).thenAccept(datosUsuario->
                Platform.runLater(()->{
                    this.datosUsuario = datosUsuario;

                })
        ).exceptionally(ex->{
            Platform.runLater(()->{

            });
            return null;
        });
    }


    @FXML
    void initialize(){

    }

    private void configurarColumnas(){
        colIdActual.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idRol()
        ));
        colIdDisponible.setCellValueFactory(cellData-> new SimpleObjectProperty<>(
                cellData.getValue().idRol()
        ));
        colNombreActual.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colNombreDisponible.setCellValueFactory(cellData-> new SimpleStringProperty(
                cellData.getValue().nombre()
        ));
        colActivoActual.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String activo = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(activo);
        });
        colActivoDisponible.setCellValueFactory(cellData-> {
            boolean esActivo = cellData.getValue().activo();
            String activo = esActivo ? "Activo" : "Inactivo";
            return new SimpleStringProperty(activo);
        });

    }


    @FXML
    void anadirRol(ActionEvent event) {

    }

    @FXML
    void cerrarVentana(ActionEvent event) {

    }

    @FXML
    void guardarCambios(ActionEvent event) {

    }

    @FXML
    void quitarRol(ActionEvent event) {

    }

}//===================================================================================================================//

