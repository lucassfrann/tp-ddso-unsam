package ar.edu.unsam.ddso.tp.model;

import ar.edu.unsam.ddso.tp.model.exceptions.TagInUseException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagNameAlreadyExistsException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagNameRequiredException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    // Only the id has a setter: TagRepositoryEnMemoria assigns it. The name changes only through
    // create and rename.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Normalizes a tag name (trims it). A null name becomes an empty one, which the entity then
     * rejects. The service uses this value to query the repository, and the entity stores the same
     * value, so "photo " cannot pass the uniqueness check and then fail on the unique column.
     */
    public static String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    /**
     * Creates a tag. The name is required and unique, ignoring case. The service tells the entity
     * whether the name is already taken.
     */
    public static Tag create(String name, boolean nameTaken) {
        Tag tag = new Tag();
        tag.name = validName(name, nameTaken);
        return tag;
    }

    /** Renames the tag. Same rules as create. The name is left untouched if a rule is broken. */
    public void rename(String name, boolean nameTaken) {
        this.name = validName(name, nameTaken);
    }

    /** A tag in use by some digitalization cannot be deleted. */
    public void assertCanBeDeleted(boolean inUse) {
        if (inUse) {
            throw new TagInUseException();
        }
    }

    private static String validName(String name, boolean nameTaken) {
        String normalized = normalizeName(name);
        if (normalized.isEmpty()) {
            throw new TagNameRequiredException();
        }
        if (nameTaken) {
            throw new TagNameAlreadyExistsException(normalized);
        }
        return normalized;
    }
}
