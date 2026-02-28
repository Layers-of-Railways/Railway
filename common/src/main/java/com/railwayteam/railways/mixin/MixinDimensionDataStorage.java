/*
 * Steam 'n' Rails
 * Copyright (c) 2026 The Railways Team
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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.railwayteam.railways.base.datafix.CRReferences;
import com.railwayteam.railways.base.datafixerapi.DataFixesInternals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Higher priority (applies later) for compatibility with [Plutonium](https://github.com/IThundxr/Plutonium)
@Mixin(value = DimensionDataStorage.class, priority = 1200)
public class MixinDimensionDataStorage {
    @WrapOperation(method = "readTagFromDisk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/datafix/DataFixTypes;update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag updateTracksData(DataFixTypes instance, DataFixer fixer, CompoundTag tag, int version,
                                         int newVersion, Operation<CompoundTag> original, String name) {
        CompoundTag vanillaFixed = original.call(instance, fixer, tag, version, newVersion);
        if (!"create_tracks".equals(name))
            return vanillaFixed;

        return (CompoundTag) DataFixesInternals.get().updateWithAllFixers(
            CRReferences.SAVED_DATA_CREATE_TRACKS,
            new Dynamic<>(NbtOps.INSTANCE, vanillaFixed)
        ).getValue();
    }
}
