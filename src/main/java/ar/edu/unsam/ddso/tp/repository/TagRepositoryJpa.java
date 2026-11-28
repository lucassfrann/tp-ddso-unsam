package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Profile({"local", "prod"})
public interface TagRepositoryJpa extends JpaRepository<Tag, Long>, TagRepository {

    @Override
    boolean existsByNameIgnoreCase(String name);

    @Override
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    // A @Query is required here: the relation lives in Digitalization (unidirectional), so the
    // method name alone cannot express it.
    @Override
    @Query("select count(d) > 0 from Digitalization d join d.tags t where t.id = :tagId")
    boolean isInUse(@Param("tagId") Long tagId);
}
