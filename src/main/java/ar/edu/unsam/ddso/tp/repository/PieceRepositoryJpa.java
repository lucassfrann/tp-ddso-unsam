package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Piece;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile({"local", "prod"})
public interface PieceRepositoryJpa extends JpaRepository<Piece, Long>, PieceRepository {}
