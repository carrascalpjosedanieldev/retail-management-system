package RetailManagementSystem.aplicacion.dto.seguridad;

import RetailManagementSystem.aplicacion.dto.consultas.ConfiguracionSistemaDTO;

public record PoliticaDeBloqueoDTO(ConfiguracionSistemaDTO maxIntentos, ConfiguracionSistemaDTO minutosBloqueo) { }

