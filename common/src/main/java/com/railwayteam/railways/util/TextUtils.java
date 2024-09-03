/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.util;

import com.simibubi.create.foundation.utility.Components;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;

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
