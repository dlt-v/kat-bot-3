package com.katbot.utils;

import com.katbot.util.ColorUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ColorUtilsTest {

    @ParameterizedTest
    @CsvSource({
            // 6-digit hex formats
            "'#FF0000', 255, 0, 0",
            "'#00FF00', 0, 255, 0",
            "'#0000FF', 0, 0, 255",
            "'FF0000',  255, 0, 0",
            "'#ff5733', 255, 87, 51",

            // 3-digit shorthand formats
            "'#f00', 255, 0, 0",
            "'#0f0', 0, 255, 0",
            "'#00f', 0, 0, 255",
            "'f00',  255, 0, 0",
            "'#abc', 170, 187, 204"
    })
    void testValidHexColorParsing(String hexInput, int expectedR, int expectedG, int expectedB) {
        Color color = ColorUtils.parseColorFromHexCode(hexInput);
        assertEquals(expectedR, color.getRed());
        assertEquals(expectedG, color.getGreen());
        assertEquals(expectedB, color.getBlue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "#GGGGGG",
            "#1234567",
            "#12",
            " ",
            "#ZZZ"
    })
    void testInvalidHexColorParsingThrowsException(String invalidHexInput) {
        assertThrows(NumberFormatException.class, () -> ColorUtils.parseColorFromHexCode(invalidHexInput));
    }

    @Test
    void testNullInputThrowsException() {
        assertThrows(NumberFormatException.class, () -> ColorUtils.parseColorFromHexCode(null));
    }
}