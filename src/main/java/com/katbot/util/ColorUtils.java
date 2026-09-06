package com.katbot.util;

import java.awt.*;

public class ColorUtils {

    public static Color parseColorFromHexCode(String hexCode) {
        if (hexCode == null || !hexCode.matches("^#?([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$")) {
            throw new NumberFormatException("Invalid hex color format: " + hexCode);
        }

        if (hexCode.matches("^#?[0-9a-fA-F]{3}$")) {
            String hex = hexCode.startsWith("#") ? hexCode.substring(1) : hexCode;
            hexCode = "#" + hex.charAt(0) + hex.charAt(0)
                    + hex.charAt(1) + hex.charAt(1)
                    + hex.charAt(2) + hex.charAt(2);
        }

        return Color.decode(hexCode.startsWith("#") ? hexCode : "#" + hexCode);
    }
}
