package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class RealNameTest {

    @Test
    void validate_nameContainingBadWord_returnsFalse() {
        assertThat(RealName.validate("Bosse Bredsladd")).isFalse();
    }

    @Test
    void validate_cleanName_returnsTrue() {
        assertThat(RealName.validate("Sean Banan")).isTrue();
    }

    @Test
    void validate_leetSpeakBadWord_returnsFalse() {
        assertThat(RealName.validate("John idiot Doe")).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void validate_nullInput_throwsException(String name) {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> RealName.validate(name));
    }
}
