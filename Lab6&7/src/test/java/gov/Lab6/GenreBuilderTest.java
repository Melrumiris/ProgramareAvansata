package gov.Lab6;

import gov.Lab6.data.GenreData;
import gov.Lab6.data.builder.GenreBuilder;
import gov.Lab6.exception.NullDataException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GenreBuilderTest {

    @Test
    void buildsGenreWhenIdAndNameAreProvided() throws Exception {
        GenreData genre = new GenreBuilder()
                .setID(7)
                .setName("Sci-Fi")
                .build();

        assertEquals(7, genre.getId());
        assertEquals("Sci-Fi", genre.getName());
    }

    @Test
    void failsToBuildWhenNameMissing() {
        GenreBuilder builder = new GenreBuilder().setID(3);
        assertThrows(NullDataException.class, builder::build);
    }

    @Test
    void equalityDependsOnlyOnId() throws Exception {
        GenreData first = new GenreBuilder().setID(1).setName("First").build();
        GenreData second = new GenreBuilder().setID(1).setName("Second").build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}

