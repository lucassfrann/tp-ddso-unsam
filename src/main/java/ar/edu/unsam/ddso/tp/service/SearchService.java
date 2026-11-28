package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.SearchRequestDTO;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.PieceSearchCriteria;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Orchestrates the search of pieces. It translates the request DTO to domain search criteria (which
 * validate themselves), asks the repository (which filters and paginates) and maps the pieces found
 * to DTOs.
 */
@Service
public class SearchService {

    private final PieceRepository pieceRepository;

    public SearchService(PieceRepository pieceRepository) {
        this.pieceRepository = pieceRepository;
    }

    // ---------- Consultas ----------

    public List<PieceResponseDTO> search(SearchRequestDTO request) {
        PieceSearchCriteria criteria = toCriteria(request);
        return pieceRepository.search(criteria).stream().map(this::toResponse).toList();
    }

    // ---------- Privados ----------

    private PieceSearchCriteria toCriteria(SearchRequestDTO request) {
        return new PieceSearchCriteria(
                request.name(),
                request.description(),
                request.from(),
                request.to(),
                request.tags() == null ? null : new HashSet<>(request.tags()),
                request.page(),
                request.size());
    }

    private PieceResponseDTO toResponse(Piece piece) {
        return new PieceResponseDTO(
                piece.getId(), piece.getName(), piece.getDescription(), piece.getLoadDate());
    }
}
