package com.railwayteam.railways.util;

import com.google.common.collect.ImmutableMap;
import com.railwayteam.railways.Translation;
import com.railwayteam.railways.entities.conductor.ConductorEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public abstract class ColorUtils {
    private static ImmutableMap<TextFormatting, DyeColor> colorToColorFormat;

    static {
        HashMap<TextFormatting, DyeColor> m = new HashMap<>();

        // pain
        m.put(TextFormatting.BLACK, DyeColor.BLACK);
        m.put(TextFormatting.DARK_BLUE, DyeColor.BLUE);
        m.put(TextFormatting.DARK_GREEN, DyeColor.GREEN);
        m.put(TextFormatting.DARK_AQUA, DyeColor.CYAN);
        m.put(TextFormatting.DARK_RED, DyeColor.RED);
        m.put(TextFormatting.DARK_PURPLE, DyeColor.PURPLE);
        m.put(TextFormatting.GOLD, DyeColor.ORANGE);
        m.put(TextFormatting.GRAY, DyeColor.LIGHT_GRAY);
        m.put(TextFormatting.DARK_GRAY, DyeColor.GRAY);
        m.put(TextFormatting.BLUE, DyeColor.LIGHT_BLUE);
        m.put(TextFormatting.GREEN, DyeColor.LIME);
        m.put(TextFormatting.AQUA, DyeColor.CYAN);
        m.put(TextFormatting.RED, DyeColor.PINK);
        m.put(TextFormatting.LIGHT_PURPLE, DyeColor.MAGENTA);
        m.put(TextFormatting.YELLOW, DyeColor.YELLOW);
        m.put(TextFormatting.WHITE, DyeColor.WHITE);

        ColorUtils.colorToColorFormat = ImmutableMap.copyOf(m);
        // for some reason intellij is saying it cant assign a final variable, AND THEN AT THE SAME TIME COMPLAINS THAT THE
        // VARIABLE ISNT ASSIGNED A VALUE AFTER MOVING THIS TO A NEW CLASS, SO THANKS INTELLIJ, NOW I HAVE TO MAKE A GETTER
    }

    public static ImmutableMap<TextFormatting, DyeColor> getColorToColorFormat() {
        return colorToColorFormat;
    }

    public static TextFormatting colorToFormat(DyeColor color) {
        return colorToColorFormat.keySet().stream().filter((k) -> colorToColorFormat.get(k).getId() == color.getId()).findFirst().orElse(TextFormatting.WHITE);
    }

    public static String colorToColoredText(DyeColor color, TextComponent text, boolean changeBlackToGray) {
        TextFormatting t = colorToFormat(color);
        return (t.equals(TextFormatting.BLACK) && changeBlackToGray ? TextFormatting.GRAY : t) + text.getString();
    }

    public static String colorToColoredText(DyeColor color) {
        return colorToColoredText(color, Translation.colorToText.get(color), true);
    }

    public static String colorToEnglish(String original) {
        return StringUtils.capitalize(original).replaceAll("_", " ");
    }

    public static String colorToEnglish(DyeColor color) {
        return colorToEnglish(color.getTranslationKey());
    }

    public static TranslationTextComponent colored(DyeColor color) {
        return Translation.Colored.getComponent(ColorUtils.colorToColoredText(color));
    }
}
