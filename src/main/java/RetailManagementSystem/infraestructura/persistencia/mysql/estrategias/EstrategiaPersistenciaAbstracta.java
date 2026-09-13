package RetailManagementSystem.infraestructura.persistencia.mysql.estrategias;

import RetailManagementSystem.dominio.entidades.comercial.Producto;
import RetailManagementSystem.infraestructura.persistencia.mysql.mappers.DatosProductoBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class EstrategiaPersistenciaAbstracta<T extends Producto> implements EstrategiaPersistenciaProducto<T> {

    @Override
    public List<Producto> obtenerDetallesYConstruirEnLote(Connection conn, List<DatosProductoBase> loteBase) throws SQLException {
        if (loteBase == null || loteBase.isEmpty()){
            return Collections.emptyList();
        }

        Map<String, DatosProductoBase> mapaLoteBase = loteBase.stream()
                .collect(Collectors.toMap(DatosProductoBase::codigo, Function.identity()));

        String sql = generarSqlLote(mapaLoteBase.size());

        List<Producto> listaFinal = new ArrayList<>(mapaLoteBase.size());

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;
            for (String codigo : mapaLoteBase.keySet()) {
                pstmt.setString(index++, codigo);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("codigo_producto");
                    DatosProductoBase datosBase = mapaLoteBase.get(codigo);

                    if (datosBase != null) {
                        listaFinal.add(mapearDetalleYConstruirProducto(rs, datosBase));
                    }
                }
            }
        }
        return listaFinal;
    }

    protected abstract String generarSqlLote(int cantidadParametros);

}//===================================================================================================================//

