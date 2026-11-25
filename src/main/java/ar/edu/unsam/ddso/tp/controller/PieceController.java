package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.PieceCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.service.PieceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pieces")
public class PieceController {

    private final PieceService pieceService;

    public PieceController(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    @PostMapping
    public ResponseEntity<PieceResponseDTO> create(@RequestBody PieceCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pieceService.create(request));
    }
}
