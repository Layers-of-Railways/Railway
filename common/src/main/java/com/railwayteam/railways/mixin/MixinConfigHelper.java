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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.foundation.config.ui.ConfigHelper;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConfigHelper.class, remap = false)
public class MixinConfigHelper {
    @Inject(method = {
            "hasAnyConfig",
            "hasAnyForgeConfig"
    }, at = @At("HEAD"), cancellable = true)
    private static void markSNRConfig(String modID, CallbackInfoReturnable<Boolean> cir) {
        if (modID.equals(Railways.MODID))
            cir.setReturnValue(true);
    }

    @Inject(method = "findConfigSpecFor", at = @At("HEAD"), cancellable = true)
    private static void returnSNRConfig(ModConfig.Type type, String modID, CallbackInfoReturnable<IConfigSpec<?>> cir) {
        if (modID.equals(Railways.MODID)) {
            cir.setReturnValue(CRConfigs.byType(type).specification);
        }
    }
}
