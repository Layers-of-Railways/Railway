/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.base.datafixers.CompatCherryTrackFix;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtUtils.class)
public class NbtUtilsMixin {
    // moved general updating to MixinDataFixTypes.java

    @Inject(method = "readBlockState", at = @At("HEAD"))
    private static void railways$upgradeBlocks(HolderGetter<Block> blockGetter, CompoundTag tag, CallbackInfoReturnable<BlockState> cir) {
        if (tag.contains("Name", Tag.TAG_STRING)) {
            /*
             * MonoBogey Fixer
             */
            if (tag.getString("Name").equals("railways:mono_bogey_upside_down")) {
                tag.putString("Name", "railways:mono_bogey");
                CompoundTag properties = tag.getCompound("Properties");
                properties.putString("upside_down", "true");
                tag.put("Properties", properties);
            }

            /*
             * Compat Cherry Track Fixer
             */
            if (CompatCherryTrackFix.standardCherryOld.contains(tag.getString("Name"))) {
                railways$fixCherryTrackData(tag, "railways:track_cherry");
            } else if (CompatCherryTrackFix.wideCherryOld.contains(tag.getString("Name"))) {
                railways$fixCherryTrackData(tag, "railways:track_cherry_wide");
            } else if (CompatCherryTrackFix.narrowCherryOld.contains(tag.getString("Name"))) {
                railways$fixCherryTrackData(tag, "railways:track_cherry_narrow");
            }
        }
    }

    @Unique
    private static void railways$fixCherryTrackData(CompoundTag tag, String name) {
        tag.putString("Name", name);
        CompoundTag properties = tag.getCompound("Properties");
        tag.put("Properties", properties);
    }
}