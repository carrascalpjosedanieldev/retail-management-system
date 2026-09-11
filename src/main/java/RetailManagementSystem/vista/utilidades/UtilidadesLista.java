package RetailManagementSystem.vista.utilidades;

import javafx.collections.ObservableList;

import java.util.function.Predicate;

public class UtilidadesLista {

    private UtilidadesLista() { }

    public static <T> void reemplazarPorIdentidad(ObservableList<T> lista, T elementoNuevo, Predicate<T> coincidenciaId) {
        for (int i = 0; i < lista.size(); i++) {
            if (coincidenciaId.test(lista.get(i))) {
                lista.set(i, elementoNuevo);
                return;
            }
        }
    }

}//===================================================================================================================//

