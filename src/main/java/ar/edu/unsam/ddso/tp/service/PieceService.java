package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PieceService {

    private final PieceRepository pieceRepository;

    public PieceService(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    // ---------- Operaciones ----------

    @Transactional
    public PieceResponseDTO create(PieceCreateRequestDTO request) {
        Piece piece = Piece.create(request.name(), request.description());
        return toResponse(pieceRepository.save(piece));
    }

    // ---------- Privados ----------

    private PieceResponseDTO toResponse(Piece piece) {
        return new PieceResponseDTO(
                piece.getId(), piece.getName(), piece.getDescription(), piece.getLoadDate());
    }
}
