package com.railwayteam.railways.util;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColorUtils {
    public static MapColor mapColorFromDye(@Nullable DyeColor dyeColor, @NotNull MapColor defaultColor) {
        if (dyeColor == null)
            return defaultColor;
        return mapColorFromDye(dyeColor);
    }

    public static MapColor mapColorFromDye(@NotNull DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE -> MapColor.TERRACOTTA_WHITE;
            case ORANGE -> MapColor.COLOR_ORANGE;
            case MAGENTA -> MapColor.COLOR_MAGENTA;
            case LIGHT_BLUE -> MapColor.COLOR_LIGHT_BLUE;
            case YELLOW -> MapColor.COLOR_YELLOW;
            case LIME -> MapColor.COLOR_LIGHT_GREEN;
            case PINK -> MapColor.COLOR_PINK;
            case GRAY -> MapColor.COLOR_GRAY;
            case LIGHT_GRAY -> MapColor.COLOR_LIGHT_GRAY;
            case CYAN -> MapColor.COLOR_CYAN;
            case PURPLE -> MapColor.COLOR_PURPLE;
            case BLUE -> MapColor.COLOR_BLUE;
            case BROWN -> MapColor.COLOR_BROWN;
            case GREEN -> MapColor.COLOR_GREEN;
            case RED -> MapColor.COLOR_RED;
            case BLACK -> MapColor.COLOR_BLACK;
        };
    }

    public static String coloredName(String string) {
        return switch (string) {
            case "white" -> "White";
            case "orange" -> "Orange";
            case "magenta" -> "Magenta";
            case "light_blue" -> "Light Blue";
            case "yellow" -> "Yellow";
            case "lime" -> "Lime";
            case "pink" -> "Pink";
            case "gray" -> "Gray";
            case "light_gray" -> "Light Gray";
            case "cyan" -> "Cyan";
            case "purple" -> "Purple";
            case "blue" -> "Blue";
            case "brown" -> "Brown";
            case "green" -> "Green";
            case "red" -> "Red";
            case "black" -> "Black";
            default -> "Unknown Color";
        };
    }

    public static Item getDyeColorDyeItem(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_DYE;
            case ORANGE -> Items.ORANGE_DYE;
            case MAGENTA -> Items.MAGENTA_DYE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
            case YELLOW -> Items.YELLOW_DYE;
            case LIME -> Items.LIME_DYE;
            case PINK -> Items.PINK_DYE;
            case GRAY -> Items.GRAY_DYE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
            case CYAN -> Items.CYAN_DYE;
            case PURPLE -> Items.PURPLE_DYE;
            case BLUE -> Items.BLUE_DYE;
            case BROWN -> Items.BROWN_DYE;
            case GREEN -> Items.GREEN_DYE;
            case RED -> Items.RED_DYE;
            case BLACK -> Items.BLACK_DYE;
        };
    }
}
