package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Piece;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class PieceRepositoryEnMemoria implements PieceRepository {

    private final Map<Long, Piece> data = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Piece save(Piece piece) {
        if (piece.getId() == null) {
            piece.setId(nextId.getAndIncrement());
        }
        data.put(piece.getId(), piece);
        return piece;
    }

    @Override
    public Optional<Piece> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }
}
