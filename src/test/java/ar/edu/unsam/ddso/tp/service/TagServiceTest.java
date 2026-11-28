package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.unsam.ddso.tp.dto.TagCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.TagResponseDTO;
import ar.edu.unsam.ddso.tp.dto.TagUpdateRequestDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.model.exceptions.TagInUseException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagNameAlreadyExistsException;
import ar.edu.unsam.ddso.tp.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock private TagRepository tagRepository;

    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagService(tagRepository);
    }

    @Test
    void createsAValidTag() {
        // Given
        TagCreateRequestDTO request = new TagCreateRequestDTO("  photo ");
        when(tagRepository.existsByNameIgnoreCase("photo")).thenReturn(false);
        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(
                        invocation -> {
                            Tag saved = invocation.getArgument(0);
                            saved.setId(1L);
                            return saved;
                        });

        // When
        TagResponseDTO response = tagService.create(request);

        // Then
        assertEquals(1L, response.id());
        assertEquals("photo", response.name());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void rejectsARepeatedNameAndDoesNotSaveIt() {
        // Given
        TagCreateRequestDTO request = new TagCreateRequestDTO("Photo");
        when(tagRepository.existsByNameIgnoreCase("Photo")).thenReturn(true);

        // When / Then
        assertThrows(TagNameAlreadyExistsException.class, () -> tagService.create(request));
        verify(tagRepository, never()).save(any());
    }

    @Test
    void renamesAnExistingTag() {
        // Given
        Tag tag = existingTag(1L, "photo");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameIgnoreCaseAndIdNot("film", 1L)).thenReturn(false);
        when(tagRepository.save(tag)).thenReturn(tag);

        // When
        TagResponseDTO response = tagService.rename(1L, new TagUpdateRequestDTO(" film "));

        // Then
        assertEquals(1L, response.id());
        assertEquals("film", response.name());
        verify(tagRepository).save(tag);
    }

    @Test
    void rejectsRenamingToAnExistingNameAndDoesNotSaveIt() {
        // Given
        Tag tag = existingTag(1L, "photo");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByNameIgnoreCaseAndIdNot("film", 1L)).thenReturn(true);

        // When / Then
        assertThrows(
                TagNameAlreadyExistsException.class,
                () -> tagService.rename(1L, new TagUpdateRequestDTO("film")));
        assertEquals("photo", tag.getName());
        verify(tagRepository, never()).save(any());
    }

    @Test
    void failsToRenameAMissingTag() {
        // Given
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class,
                () -> tagService.rename(99L, new TagUpdateRequestDTO("film")));
        verify(tagRepository, never()).save(any());
    }

    @Test
    void rejectsDeletingATagInUse() {
        // Given
        Tag tag = existingTag(1L, "photo");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.isInUse(1L)).thenReturn(true);

        // When / Then
        assertThrows(TagInUseException.class, () -> tagService.delete(1L));
        verify(tagRepository, never()).delete(any());
    }

    @Test
    void deletesAFreeTag() {
        // Given
        Tag tag = existingTag(1L, "photo");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.isInUse(1L)).thenReturn(false);

        // When
        tagService.delete(1L);

        // Then
        verify(tagRepository).delete(tag);
    }

    @Test
    void failsToDeleteAMissingTag() {
        // Given
        when(tagRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> tagService.delete(99L));
        verify(tagRepository, never()).delete(any());
    }

    @Test
    void listsTheTagsAsDtos() {
        // Given
        when(tagRepository.findAll())
                .thenReturn(List.of(existingTag(1L, "photo"), existingTag(2L, "letter")));

        // When
        List<TagResponseDTO> response = tagService.findAll();

        // Then
        assertEquals(
                List.of(new TagResponseDTO(1L, "photo"), new TagResponseDTO(2L, "letter")),
                response);
    }

    private Tag existingTag(Long id, String name) {
        Tag tag = Tag.create(name, false);
        tag.setId(id);
        return tag;
    }
}
