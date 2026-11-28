package ar.edu.unsam.ddso.tp.model;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.unsam.ddso.tp.model.exceptions.TagInUseException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagNameAlreadyExistsException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagNameRequiredException;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void newTagKeepsItsName() {
        // When
        Tag tag = Tag.create("Photo", false);

        // Then
        assertEquals("Photo", tag.getName());
    }

    @Test
    void newTagStoresTheTrimmedName() {
        // When
        Tag tag = Tag.create("  photo  ", false);

        // Then
        assertEquals("photo", tag.getName());
    }

    @Test
    void nameIsRequiredOnCreate() {
        assertThrows(TagNameRequiredException.class, () -> Tag.create(null, false));
        assertThrows(TagNameRequiredException.class, () -> Tag.create("", false));
        assertThrows(TagNameRequiredException.class, () -> Tag.create("   ", false));
    }

    @Test
    void nameMustBeUniqueOnCreate() {
        assertThrows(TagNameAlreadyExistsException.class, () -> Tag.create("photo", true));
    }

    @Test
    void renameChangesTheName() {
        // Given
        Tag tag = Tag.create("photo", false);

        // When
        tag.rename(" film ", false);

        // Then
        assertEquals("film", tag.getName());
    }

    @Test
    void renameRejectsAnEmptyNameAndKeepsTheOldOne() {
        // Given
        Tag tag = Tag.create("photo", false);

        // When / Then
        assertThrows(TagNameRequiredException.class, () -> tag.rename("  ", false));
        assertThrows(TagNameRequiredException.class, () -> tag.rename(null, false));
        assertEquals("photo", tag.getName());
    }

    @Test
    void renameRejectsATakenNameAndKeepsTheOldOne() {
        // Given
        Tag tag = Tag.create("photo", false);

        // When / Then
        assertThrows(TagNameAlreadyExistsException.class, () -> tag.rename("film", true));
        assertEquals("photo", tag.getName());
    }

    @Test
    void aTagInUseCannotBeDeleted() {
        // Given
        Tag tag = Tag.create("photo", false);

        // When / Then
        assertThrows(TagInUseException.class, () -> tag.assertCanBeDeleted(true));
    }

    @Test
    void aTagNotInUseCanBeDeleted() {
        // Given
        Tag tag = Tag.create("photo", false);

        // When / Then
        assertDoesNotThrow(() -> tag.assertCanBeDeleted(false));
    }

    @Test
    void normalizeNameTrimsAndTurnsNullIntoEmpty() {
        assertEquals("photo", Tag.normalizeName("  photo "));
        assertEquals("", Tag.normalizeName(null));
    }
}
