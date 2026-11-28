package ar.edu.unsam.ddso.tp.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Filters of GET /pieces/search. Every filter is optional (null or empty means "no filter"). The
 * Telegram handler builds this same DTO, so the API and the bot search the same way.
 */
public record SearchRequestDTO(
        String name,
        String description,
        LocalDate from,
        LocalDate to,
        List<String> tags,
        int page,
        int size) {}
