package se.yrgo.libraryapp.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class UtilsTest {
    @Test
    void onlyLettersAndWhitespace_mixedLettersNumbersAndSymbols_returnsOnlyLettersAndSpaces() {
        String input = ".,_-@Hejsan ! 666 #";
        String expected = "hejsan   ";
        String result = Utils.onlyLettersAndWhitespace(input);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @EmptySource
    void onlyLettersAndWhiteSpace_emptyString_returnsEmptyString(String string) {
        assertThat(Utils.onlyLettersAndWhitespace(string)).isEmpty();
    }

    @Test
    void onlyLettersAndWhitespace_alreadyCleanString_returnsSameString() {
        assertThat(Utils.onlyLettersAndWhitespace("hello world")).isEqualTo("hello world");
    }

    @Test
    void onlyLettersAndWhitespace_onlySpecialCharacters_returnsWhitespace() {
        assertThat(Utils.onlyLettersAndWhitespace(" !@#   ")).isEqualTo("    ");
    }

    @ParameterizedTest
    @NullSource
    void onlyLettersAndWhitespace_nullInput_throwsException(String input) {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Utils.onlyLettersAndWhitespace(input));
    }

    //

    @Test
    void cleanAndUnLeet_leetCharacters_areConvertedToLetters() {
        String input = "h3ll0";
        String expected = "hello";
        assertThat(Utils.cleanAndUnLeet(input)).isEqualTo(expected);
    }

    @Test
    void cleanAndUnLeet_mixedLeetAndSymbols_returnsOnlyLettersAndSpaces() {
        String input = "4w3s0m3!@# ";
        String expected = "awesome ";
        assertThat(Utils.cleanAndUnLeet(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @EmptySource
    void cleanAndUnLeet_emptyString_returnsEmpty(String string) {
        assertThat(Utils.cleanAndUnLeet(string)).isEmpty();
    }

    @Test
    void cleanAndUnLeet_noLeetCharacters_returnsSameString() {
        String input = "hello world";
        assertThat(Utils.cleanAndUnLeet(input)).isEqualTo("hello world");
    }

    @Test
    void cleanAndUnLeet_onlyLeetCharacters_convertedCorrectly() {
        String input = "1337";
        String expected = "ieet";
        assertThat(Utils.cleanAndUnLeet(input)).isEqualTo(expected);
    }

    @Test
    void cleanAndUnLeet_stringWithWhitespace_preservesWhitespace() {
        String input = "h3ll0 w0rld";
        String expected = "hello world";
        assertThat(Utils.cleanAndUnLeet(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullSource
    void cleanAndUnLeet_nullInput_throwsException(String input) {
        assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Utils.cleanAndUnLeet(input));
    }
}
