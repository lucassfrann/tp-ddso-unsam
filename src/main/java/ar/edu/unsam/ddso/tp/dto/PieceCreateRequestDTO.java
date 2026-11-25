package ar.edu.unsam.ddso.tp.dto;

/** Body of POST /pieces. The load date is not here: the system assigns it. */
public record PieceCreateRequestDTO(String name, String description) {}
