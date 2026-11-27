package ar.edu.unsam.ddso.tp.dto;

/** Body of POST /pieces/{id}/digitalizations. The piece comes from the path, not from the body. */
public record DigitalizationCreateRequestDTO(String url, String description) {}
