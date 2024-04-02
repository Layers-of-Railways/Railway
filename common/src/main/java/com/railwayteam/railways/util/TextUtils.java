package com.railwayteam.railways.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;

import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public class TextUtils {
    public static String titleCaseConversion(String inputString) {
        if (StringUtils.isBlank(inputString)) {
            return "";
        }

        if (StringUtils.length(inputString) == 1) {
            return inputString.toUpperCase(Locale.ROOT);
        }

        StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());

        Stream.of(inputString.split(" ")).forEach(stringPart -> {
            if (stringPart.length() > 1)
                resultPlaceHolder.append(stringPart.substring(0, 1)
                        .toUpperCase(Locale.ROOT))
                    .append(stringPart.substring(1)
                        .toLowerCase(Locale.ROOT));
            else
                resultPlaceHolder.append(stringPart.toUpperCase(Locale.ROOT));

            resultPlaceHolder.append(" ");
        });
        return StringUtils.trim(resultPlaceHolder.toString());
    }

    public static Component translateWithFormatting(String key, Object... args) {
        MutableComponent base = Components.translatable(key, args);
        StringBuilder partsStringBuilder = new StringBuilder();
        base.visit((style, part) -> {
            partsStringBuilder.append(part);
            return Optional.empty();
        }, Style.EMPTY);
        return Components.literal(partsStringBuilder.toString());
    }

    public static String joinSpace(String... strings) {
        return join(" ", strings);
    }

    public static String joinUnderscore(String... strings) {
        return join("_", strings);
    }

    public static String join(String separator, final String... strings) {
        String[] filtered = Stream.of(strings).filter(s -> !s.isEmpty()).toArray(String[]::new);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < filtered.length; i++) {
            out.append(filtered[i]);
            if (i < filtered.length - 1) out.append(separator);
        }
        return out.toString();
    }
}
