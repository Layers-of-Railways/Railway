/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.config.fabric;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;

public class CRConfigsImpl {
    public static void register() {
        CRConfigs.registerCommon();

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CRConfigs.CONFIGS.entrySet())
            ModLoadingContext.registerConfig(Railways.MODID, pair.getKey(), pair.getValue().specification);

        ModConfigEvents.loading(Railways.MODID).register(CRConfigs::onLoad);
        ModConfigEvents.reloading(Railways.MODID).register(CRConfigs::onReload);
    }
}
