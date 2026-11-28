package ar.edu.unsam.ddso.tp.service;

import ar.edu.unsam.ddso.tp.dto.TagCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.dto.TagUpdateRequestDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the tag use cases. It asks the repository (name taken? tag in use?) and passes the
 * answer to the entity, which decides.
 */
@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // ---------- Consultas ----------

    public List<TagResponseDTO> findAll() {
        return tagRepository.findAll().stream().map(this::toResponse).toList();
    }

    // ---------- Operaciones ----------

    @Transactional
    public TagResponseDTO create(TagCreateRequestDTO request) {
        String name = Tag.normalizeName(request.name());
        Tag tag = Tag.create(name, tagRepository.existsByNameIgnoreCase(name));
        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public TagResponseDTO rename(Long id, TagUpdateRequestDTO request) {
        Tag tag = findTag(id);
        String name = Tag.normalizeName(request.name());
        tag.rename(name, tagRepository.existsByNameIgnoreCaseAndIdNot(name, id));
        // Explicit save: JPA would persist the change by itself, but this also works with the
        // in-memory repository and lets the tests verify it.
        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public void delete(Long id) {
        Tag tag = findTag(id);
        tag.assertCanBeDeleted(tagRepository.isInUse(id));
        tagRepository.delete(tag);
    }

    // ---------- Privados ----------

    private Tag findTag(Long id) {
        return tagRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
    }

    private TagResponseDTO toResponse(Tag tag) {
        return new TagResponseDTO(tag.getId(), tag.getName());
    }
}
