package ar.edu.unsam.ddso.tp.controller;

import ar.edu.unsam.ddso.tp.dto.DigitalizationCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitalizationResponseDTO;
import ar.edu.unsam.ddso.tp.service.DigitalizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pieces/{pieceId}/digitalizations")
public class DigitalizationController {

    private final DigitalizationService digitalizationService;

    public DigitalizationController(DigitalizationService digitalizationService) {
        this.digitalizationService = digitalizationService;
    }

    @PostMapping
    public ResponseEntity<DigitalizationResponseDTO> create(
            @PathVariable("pieceId") Long pieceId,
            @RequestBody DigitalizationCreateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(digitalizationService.create(pieceId, request));
    }
}
