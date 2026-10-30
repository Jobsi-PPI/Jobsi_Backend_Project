package com.escaes.jobsy.infraestructure.mapper;

import com.escaes.jobsy.application.dto.comun.PaginaResponse;
import com.escaes.jobsy.domain.model.Pagina;

import java.util.function.Function;

public class PaginaMapper {

    public static <D, R> PaginaResponse<R> toResponse(Pagina<D> pagina, Function<D, R> mapper) {
        if (pagina == null) {
            return null;
        }
        return new PaginaResponse<>(
                pagina.contenido().stream().map(mapper).toList(),
                pagina.pagina(),
                pagina.tamano(),
                pagina.totalElementos(),
                pagina.totalPaginas());
    }
}
