package com.escaes.jobsy.domain.model;

import java.util.List;

/*
 * Resultado paginado independiente de Spring Data.
 */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
}
