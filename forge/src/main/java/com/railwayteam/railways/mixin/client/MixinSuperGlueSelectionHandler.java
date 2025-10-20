/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.mixin.client;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = SuperGlueSelectionHandler.class, remap = false)
public class MixinSuperGlueSelectionHandler {

    @Shadow private BlockPos hoveredPos;
    @Shadow private Object clusterOutlineSlot;
    @Shadow private int clusterCooldown;
    private static final int CONTROL_HIGHLIGHT = 0x41bdc6;

    @Inject(method = "tick", at = @At(value = "RETURN", ordinal = 4))
    private void controlHighlightsGlueReturn(CallbackInfo ci) {
        controlHighlightsGlue();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void controlHighlightsGlueTail(CallbackInfo ci) {
        controlHighlightsGlue();
    }

    private void controlHighlightsGlue() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keySprint.isDown()) {
            Set<BlockPos> cluster = SuperGlueSelectionHelper.searchGlueGroup(mc.level, hoveredPos, hoveredPos, true);
            if (cluster != null) {
                Outliner.getInstance().showCluster(clusterOutlineSlot, cluster)
                    .colored(CONTROL_HIGHLIGHT)
                    .withFaceTextures(AllSpecialTextures.GLUE, AllSpecialTextures.HIGHLIGHT_CHECKERED)
                    .disableLineNormals()
                    .lineWidth(1 / 24f);

                clusterCooldown = 10;
            }
        }
    }
}
