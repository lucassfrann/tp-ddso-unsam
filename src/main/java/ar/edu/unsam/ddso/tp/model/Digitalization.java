package ar.edu.unsam.ddso.tp.model;

import ar.edu.unsam.ddso.tp.model.exceptions.DigitalizationDescriptionRequiredException;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidDigitalizationUrlException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "digitalizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Digitalization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "piece_id", nullable = false)
    private Piece piece;

    @ManyToMany
    @JoinTable(
            name = "digitalization_tag",
            joinColumns = @JoinColumn(name = "digitalization_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    /**
     * Creates a digitalization of the given piece, with no tags. The URL must be a valid http or
     * https URL and the description is required.
     */
    public static Digitalization create(Piece piece, String url, String description) {
        if (!isValidUrl(url)) {
            throw new InvalidDigitalizationUrlException();
        }
        if (description == null || description.isBlank()) {
            throw new DigitalizationDescriptionRequiredException();
        }
        Digitalization digitalization = new Digitalization();
        digitalization.piece = piece;
        digitalization.url = url.trim();
        digitalization.description = description;
        return digitalization;
    }

    private static boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(url.trim());
            String scheme = uri.getScheme();
            boolean isHttp = "http".equalsIgnoreCase(scheme);
            boolean isHttps = "https".equalsIgnoreCase(scheme);
            return (isHttp || isHttps) && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
