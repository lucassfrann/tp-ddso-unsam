package ar.edu.unsam.ddso.tp.dto;

import java.util.List;

/** A digitalization as seen by the API client. Tags are exposed by name. */
public record DigitalizationResponseDTO(
        Long id, Long pieceId, String url, String description, List<String> tags) {}
