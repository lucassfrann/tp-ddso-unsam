package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.PieceSearchCriteria;
import java.time.LocalDate;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Profile({"local", "prod"})
public interface PieceRepositoryJpa extends JpaRepository<Piece, Long>, PieceRepository {

    @Override
    default List<Piece> search(PieceSearchCriteria criteria) {
        // An empty IN list is not portable across databases, so a name that no tag has stands in
        // for it. It is ignored anyway because tagCount is 0 in that case.
        List<String> tagNames =
                criteria.tagNames().isEmpty() ? List.of("") : List.copyOf(criteria.tagNames());
        return searchPage(
                likePattern(criteria.name()),
                likePattern(criteria.description()),
                criteria.from(),
                criteria.to(),
                tagNames,
                criteria.tagNames().size(),
                PageRequest.of(criteria.page(), criteria.size()));
    }

    // A @Query is required here: the filters are optional and the piece reaches its tags through
    // its digitalizations, which a method name cannot express. Subqueries (not joins) keep one row
    // per piece, so the pagination is done by the database and never in memory. DISTINCT is kept as
    // a guard so a piece can never repeat.
    @Query(
            """
            select distinct p from Piece p
            where (:namePattern is null or lower(p.name) like :namePattern)
              and (:descriptionPattern is null
                   or lower(p.description) like :descriptionPattern
                   or exists (select d.id from Digitalization d
                              where d.piece = p and lower(d.description) like :descriptionPattern))
              and (:from is null or p.loadDate >= :from)
              and (:to is null or p.loadDate <= :to)
              and (:tagCount = 0
                   or (select count(distinct t.id) from Digitalization dt join dt.tags t
                       where dt.piece = p and lower(t.name) in :tagNames) = :tagCount)
            order by p.loadDate desc, p.id desc
            """)
    List<Piece> searchPage(
            @Param("namePattern") String namePattern,
            @Param("descriptionPattern") String descriptionPattern,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("tagNames") List<String> tagNames,
            @Param("tagCount") int tagCount,
            Pageable pageable);

    private static String likePattern(String text) {
        return text == null ? null : "%" + text + "%";
    }
}
