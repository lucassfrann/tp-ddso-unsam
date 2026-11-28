package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.PieceSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface PieceRepository {

    Piece save(Piece piece);

    Optional<Piece> findById(Long id);

    /**
     * Pieces that match all the given criteria, without duplicates, newest load date first, only
     * the requested page. The filtering and the pagination are done here, not in memory.
     */
    List<Piece> search(PieceSearchCriteria criteria);
}
