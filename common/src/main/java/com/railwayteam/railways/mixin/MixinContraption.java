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

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.mixin_interfaces.IContraptionFuel;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.mixin_interfaces.IPreAssembleCallback;
import com.railwayteam.railways.util.AbstractionUtils;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Contraption.class)
public abstract class MixinContraption implements IContraptionFuel {
    @Shadow(remap = false) protected MountedStorageManager storage;

    @Shadow protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Inject(method = "getBlockEntityNBT", at = @At("RETURN"))
    private void getBlockEntityNBT(Level world, BlockPos pos, CallbackInfoReturnable<CompoundTag> cir) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null)
            return;
        // This is necessary because simply calling saveWithFullMetadata again will return a _different_ NBT tag
        CompoundTag nbt = cir.getReturnValue();
        if (nbt == null)
            return;

        if (AbstractionUtils.isInstanceOfFuelTankBlockEntity(blockEntity) && nbt.contains("Controller"))
            nbt.put("Controller", NbtUtils.writeBlockPos(toLocalPos(NbtUtils.readBlockPos(nbt.getCompound("Controller")))));
    }

    @Inject(method = "removeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"))
    private void applyPreTransformCallback(Level world, BlockPos offset, CallbackInfo ci, @Local(name="add") BlockPos add) {
        BlockEntity be = world.getBlockEntity(add);
        if (be instanceof IPreAssembleCallback preTransformCallback)
            preTransformCallback.railways$preAssemble();

        if (be instanceof SmartBlockEntity smartBE) {
            for (BlockEntityBehaviour behaviour : smartBE.getAllBehaviours()) {
                if (behaviour instanceof IPreAssembleCallback preTransformCallback)
                    preTransformCallback.railways$preAssemble();
            }
        }
    }

    @Override
    public CombinedTankWrapper railways$getSharedFuelTanks() {
        return ((IFuelInventory) storage).railways$getFuelFluids();
    }
}
