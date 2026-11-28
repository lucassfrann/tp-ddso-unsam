package ar.edu.unsam.ddso.tp.model;

import ar.edu.unsam.ddso.tp.model.exceptions.InvalidSearchDateRangeException;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidSearchPageException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * What a consultant asks for when searching pieces. It normalizes its own filters and rejects
 * invalid ones, so every caller (REST API or Telegram bot) gets the same rules.
 *
 * <p>All filters are optional. {@code name} and {@code description} are lowercase text to look for
 * (null when not filtered). {@code from} and {@code to} are an inclusive range over the load date.
 * {@code tagNames} are lowercase tag names, all of which the piece must have (AND).
 */
public record PieceSearchCriteria(
        String name,
        String description,
        LocalDate from,
        LocalDate to,
        Set<String> tagNames,
        int page,
        int size) {

    public static final int MAX_PAGE_SIZE = 50;

    public PieceSearchCriteria {
        if (from != null && to != null && from.isAfter(to)) {
            throw new InvalidSearchDateRangeException();
        }
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidSearchPageException(MAX_PAGE_SIZE);
        }
        name = normalizeText(name);
        description = normalizeText(description);
        tagNames = normalizeTags(tagNames);
    }

    private static String normalizeText(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return text.trim().toLowerCase(Locale.ROOT);
    }

    private static Set<String> normalizeTags(Collection<String> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(tag -> tag.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }
}
