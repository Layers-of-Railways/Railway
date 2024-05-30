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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.util.FluidUtils;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = MountedStorageManager.class, remap = false)
public abstract class MixinMountedStorageManager implements IFuelInventory {
    @Unique private CombinedTankWrapper railways$fluidFuelInventory;
    @Unique private Map<BlockPos, MountedFluidStorage> railways$fluidFuelStorage = new HashMap<>();

    @Inject(method = "entityTick", at = @At("TAIL"))
    private void entityTick(AbstractContraptionEntity entity, CallbackInfo ci) {
        railways$fluidFuelStorage.forEach((pos, mfs) -> mfs.tick(entity, pos, entity.level.isClientSide));
    }

    @SuppressWarnings({"ConstantConditions"})
    @Inject(method = "addBlock", at = @At("TAIL"))
    private void addBlock(BlockPos localPos, BlockEntity be, CallbackInfo ci) {
        if (be != null && FluidUtils.canUseAsFuelStorage(be))
            railways$fluidFuelStorage.put(localPos, new MountedFluidStorage(be));
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci) {
        railways$fluidFuelStorage.clear();
        NBTHelper.iterateCompoundList(nbt.getList("FluidFuelStorage", Tag.TAG_COMPOUND), c -> railways$fluidFuelStorage
                .put(NbtUtils.readBlockPos(c.getCompound("Pos")), MountedFluidStorage.deserialize(c.getCompound("Data"))));
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void write(CompoundTag nbt, boolean clientPacket, CallbackInfo ci) {
        ListTag fluidFuelStorageNBT = new ListTag();
        for (BlockPos pos : railways$fluidFuelStorage.keySet()) {
            CompoundTag c = new CompoundTag();
            MountedFluidStorage mountedStorage = railways$fluidFuelStorage.get(pos);
            if (!mountedStorage.isValid())
                continue;
            c.put("Pos", NbtUtils.writeBlockPos(pos));
            c.put("Data", mountedStorage.serialize());
            fluidFuelStorageNBT.add(c);
        }

        nbt.put("FluidFuelStorage", fluidFuelStorageNBT);
    }

    @Inject(method = "removeStorageFromWorld", at = @At("TAIL"))
    public void removeStorageFromWorld(CallbackInfo ci) {
        railways$fluidFuelStorage.values()
                .forEach(MountedFluidStorage::removeStorageFromWorld);
    }

    @Inject(method = "addStorageToWorld", at = @At("TAIL"))
    private void addStorageToWorld(StructureTemplate.StructureBlockInfo block, BlockEntity blockEntity, CallbackInfo ci) {
        if (railways$fluidFuelStorage.containsKey(block.pos())) {
            MountedFluidStorage mountedStorage = railways$fluidFuelStorage.get(block.pos());
            if (mountedStorage.isValid())
                mountedStorage.addStorageToWorld(blockEntity);
        }
    }

    @Override
    public void railways$setFuelFluids(CombinedTankWrapper combinedTankWrapper) {
        railways$fluidFuelInventory = combinedTankWrapper;
    }

    @Override
    public CombinedTankWrapper railways$getFuelFluids() {
        return railways$fluidFuelInventory;
    }

    @Override
    public void railways$setFluidFuelStorage(Map<BlockPos, MountedFluidStorage> storageMap) {
        railways$fluidFuelStorage = storageMap;
    }

    @Override
    public Map<BlockPos, MountedFluidStorage> railways$getFluidFuelStorage() {
        return railways$fluidFuelStorage;
    }
}
