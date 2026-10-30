package com.escaes.jobsy.application.dto.comun;

import java.util.List;

public record PaginaResponse<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
}
