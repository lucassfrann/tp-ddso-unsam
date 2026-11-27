package ar.edu.unsam.ddso.tp.model;

import ar.edu.unsam.ddso.tp.model.exceptions.PieceNameRequiredException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pieces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Piece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Date the piece was loaded into the system (not the date of the physical material). */
    @Column(nullable = false)
    private LocalDate loadDate;

    private String description;

    @OneToMany(mappedBy = "piece", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Digitalization> digitalizations = new ArrayList<>();

    /**
     * Creates a new piece. The name is required and the load date is assigned by the system (today).
     */
    public static Piece create(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new PieceNameRequiredException();
        }
        Piece piece = new Piece();
        piece.name = name;
        piece.description = description;
        piece.loadDate = LocalDate.now();
        return piece;
    }
}
