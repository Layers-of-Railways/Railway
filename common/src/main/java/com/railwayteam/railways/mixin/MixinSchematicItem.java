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
import com.mojang.serialization.Dynamic;
import com.railwayteam.railways.base.datafixerapi.DataFixesInternals;
import com.simibubi.create.content.schematics.SchematicItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.DataInput;

@Mixin(SchematicItem.class)
public class MixinSchematicItem {
    @WrapOperation(method = "loadSchematic", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;read(Ljava/io/DataInput;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/CompoundTag;"))
    private static CompoundTag datafixSchematic(DataInput input, NbtAccounter accounter, Operation<CompoundTag> original) {
        CompoundTag tag = original.call(input, accounter);
        return (CompoundTag) DataFixesInternals.get().updateWithAllFixers(
            DataFixTypes.STRUCTURE,
            new Dynamic<>(NbtOps.INSTANCE, tag)
        ).getValue();
    }
}
