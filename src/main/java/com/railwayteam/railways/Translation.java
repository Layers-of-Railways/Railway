package com.railwayteam.railways;

import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashMap;

public enum Translation {
    Colored("tooltip", "colored", "Color: %s"),
    Named("tooltip", "named", "Name: %s"),
    EngineersCap("item", "engineers_cap", "Engineer's cap");

    public static HashMap<DyeColor, TranslationTextComponent> colorToText = new HashMap<>();

    public final String type;
    public final String english;
    public final ResourceLocation id;

    Translation(String type, ResourceLocation id, String english) {
        this.type = type;
        this.english = english;
        this.id = id;
    }

    Translation(String type, String name, String english) {
        this(type, new ResourceLocation("railways", name), english);
    }

    public String translate(String... args) {
        return getComponent(args).getString();
    }

    public String t(String... args) {
        return translate(args);
    }

    public TranslationTextComponent getComponent(String... args) {
        return new TranslationTextComponent(getKey(), args);
    }

    public String getKey() {
        return Util.makeTranslationKey(type, id);
    }
}
