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

import com.mojang.datafixers.DataFixer;
import com.railwayteam.railways.base.datafixerapi.DataFixesInternals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtUtils.class)
public abstract class NbtUtilsMixin {
    @Inject(
            method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/util/datafix/DataFixTypes;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void updateDataWithFixers(DataFixer fixer, DataFixTypes fixTypes, CompoundTag compound,
                                             int oldVersion, int targetVersion, CallbackInfoReturnable<CompoundTag> cir) {
        cir.setReturnValue(DataFixesInternals.get().updateWithAllFixers(fixTypes, cir.getReturnValue()));
    }

    @Inject(method = "readBlockState", at = @At("HEAD"))
    private static void railways$upgradeMonoBogey(CompoundTag tag, CallbackInfoReturnable<BlockState> cir) {
        if (tag.contains("Name", Tag.TAG_STRING) && tag.getString("Name").equals("railways:mono_bogey_upside_down")) {
            tag.putString("Name", "railways:mono_bogey");
            CompoundTag properties = tag.getCompound("Properties");
            properties.putString("upside_down", "true");
            tag.put("Properties", properties);
        }
    }
}