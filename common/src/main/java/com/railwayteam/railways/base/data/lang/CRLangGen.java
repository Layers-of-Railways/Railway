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

package com.railwayteam.railways.base.data.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRPalettes;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.createmod.ponder.foundation.PonderIndex;

import java.util.Map;
import java.util.function.BiConsumer;

public class CRLangGen {
    public static void generate(RegistrateLangProvider provider) {
        BiConsumer<String, String> langConsumer = provider::add;

        provideDefaultLang("interface", langConsumer);
        provideDefaultLang("tooltips", langConsumer);
        CRTags.provideLangEntries(langConsumer);
        CRPalettes.provideLangEntries(langConsumer);
        PonderIndex.getLangAccess().provideLang(Railways.MOD_ID, langConsumer);
    }

    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/railways/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }
}