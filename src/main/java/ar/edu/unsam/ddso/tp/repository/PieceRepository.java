package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Piece;
import java.util.Optional;

public interface PieceRepository {

    Piece save(Piece piece);

    Optional<Piece> findById(Long id);
}
