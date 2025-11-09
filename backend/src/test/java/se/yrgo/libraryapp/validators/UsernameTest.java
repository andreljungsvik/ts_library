package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UsernameTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "bosse",
            "@ndre",
            "1234567678876",
            "abcdefghijklmnopqrstuvwxyz",
            "@.-_-.@"
    })
    void validate_validUsernames_shouldReturnTrue(String username) {
        assertThat(Username.validate(username)).isTrue();
    }

    @Test
    void validate_tooShortUsername_shouldReturnFalse() {
        assertThat(Username.validate("bos")).isFalse();
    }

    @Test
    void validate_forbiddenCharacter_shouldReturnFalse() {
        assertThat(Username.validate("b#sse")).isFalse();
    }

    @Test
    void validate_onlySpecialCharacters_shouldReturnFalse() {
        assertThat(Username.validate("!@#$%^&*")).isFalse();
    }

    @Test
    void validate_whitespace_shouldReturnFalse() {
        assertThat(Username.validate(" ")).isFalse();
    }

    @ParameterizedTest
    @EmptySource
    void validate_emptyString_shouldReturnFalse(String string) {
        assertThat(Username.validate(string)).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void validate_nullValue_shouldReturnFalse(String string) {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Username.validate(string));
    }
}
