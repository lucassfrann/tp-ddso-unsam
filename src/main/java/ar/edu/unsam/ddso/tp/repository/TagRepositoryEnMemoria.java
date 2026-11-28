package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class TagRepositoryEnMemoria implements TagRepository {

    private final Map<Long, Tag> data = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Tag save(Tag tag) {
        if (tag.getId() == null) {
            tag.setId(nextId.getAndIncrement());
        }
        data.put(tag.getId(), tag);
        return tag;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Tag> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(Tag tag) {
        data.remove(tag.getId());
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return data.values().stream().anyMatch(tag -> tag.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, Long id) {
        return data.values().stream()
                .anyMatch(tag -> tag.getName().equalsIgnoreCase(name) && !tag.getId().equals(id));
    }

    /** In memory there are no digitalization associations, so a tag is never in use here. */
    @Override
    public boolean isInUse(Long tagId) {
        return false;
    }
}
