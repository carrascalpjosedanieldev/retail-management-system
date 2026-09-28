package RetailManagementSystem.aplicacion.servicios;

import RetailManagementSystem.dominio.puertos.repositorios.RepositorioDescuentos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioImpuestos;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioPoliticaVencimiento;
import RetailManagementSystem.dominio.puertos.repositorios.RepositorioProducto;
import RetailManagementSystem.dominio.puertos.transacciones.GestorTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccional;
import RetailManagementSystem.dominio.puertos.transacciones.OperacionTransaccionalConRetorno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ServicioProductosTest {

    @Mock
    private RepositorioProducto repositorioProductoFalso;

    @Mock
    private RepositorioImpuestos repositorioImpuestosFalso;

    @Mock
    private RepositorioDescuentos repositorioDescuentosFalso;

    @Mock
    private RepositorioPoliticaVencimiento repositorioPoliticaVencimiento;

    @Mock
    private GestorTransaccional gestorTransaccionalFalso;

    @InjectMocks
    private ServicioProductos servicioProductos;

    @BeforeEach
    void setUp(){
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionConRetorno(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().when(gestorTransaccionalFalso.ejecutarEnTransaccionDeLectura(any()))
                .thenAnswer(invocation -> {
                    OperacionTransaccionalConRetorno<?> operacion = invocation.getArgument(0);
                    return operacion.ejecutar();
                });
        lenient().doAnswer(invocation -> {
            OperacionTransaccional operacion = invocation.getArgument(0);
            operacion.ejecutar();
            return null;
        }).when(gestorTransaccionalFalso).ejecutarEnTransaccion(any());
    }

    //TESTS

}//===================================================================================================================//

