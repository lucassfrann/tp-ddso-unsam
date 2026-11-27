package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.DigitalizationCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitalizationResponseDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Digitalization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.DigitalizationRepository;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DigitalizationService {

    private final PieceRepository pieceRepository;
    private final DigitalizationRepository digitalizationRepository;

    public DigitalizationService(
            PieceRepository pieceRepository, DigitalizationRepository digitalizationRepository) {
        this.pieceRepository = pieceRepository;
        this.digitalizationRepository = digitalizationRepository;
    }

    // ---------- Operaciones ----------

    /**
     * Associates a new digitalization to an existing piece. A missing piece is a 404; the URL and
     * description rules are enforced by the domain.
     */
    @Transactional
    public DigitalizationResponseDTO create(Long pieceId, DigitalizationCreateRequestDTO request) {
        Piece piece =
                pieceRepository
                        .findById(pieceId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Piece not found: " + pieceId));
        Digitalization digitalization =
                piece.addDigitalization(request.url(), request.description());
        return toResponse(digitalizationRepository.save(digitalization));
    }

    // ---------- Privados ----------

    private DigitalizationResponseDTO toResponse(Digitalization digitalization) {
        List<String> tagNames =
                digitalization.getTags().stream().map(Tag::getName).sorted().toList();
        return new DigitalizationResponseDTO(
                digitalization.getId(),
                digitalization.getPiece().getId(),
                digitalization.getUrl(),
                digitalization.getDescription(),
                tagNames);
    }
}
