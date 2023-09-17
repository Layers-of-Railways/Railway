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
import java.util.stream.Collectors;

@Mixin(value = MountedStorageManager.class, remap = false)
public class MixinMountedStorageManager implements IFuelInventory {
    @Unique private CombinedTankWrapper snr$fluidFuelInventory;
    @Unique private Map<BlockPos, MountedFluidStorage> snr$fluidFuelStorage = new HashMap<>();

    @Inject(method = "entityTick", at = @At("RETURN"))
    private void entityTick(AbstractContraptionEntity entity, CallbackInfo ci) {
        snr$fluidFuelStorage.forEach((pos, mfs) -> mfs.tick(entity, pos, entity.level.isClientSide));
    }

    @Inject(method = "createHandlers", at = @At("RETURN"))
    private void createHandler(CallbackInfo ci) {
        snr$fluidFuelInventory = ((AccessorMountedStorageManager) this).snr$wrapFluids(snr$fluidFuelStorage.values()
                .stream()
                .map(MountedFluidStorage::getFluidHandler)
                .collect(Collectors.toList()));
    }

    @Inject(method = "addBlock", at = @At("RETURN"))
    private void addBlock(BlockPos localPos, BlockEntity be, CallbackInfo ci) {
        if (FluidUtils.canUseAsFuelStorage(be))
            snr$fluidFuelStorage.put(localPos, new MountedFluidStorage(be));
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void read(CompoundTag nbt, Map<BlockPos, BlockEntity> presentBlockEntities, boolean clientPacket, CallbackInfo ci) {
        snr$fluidFuelStorage.clear();
        NBTHelper.iterateCompoundList(nbt.getList("FluidFuelStorage", Tag.TAG_COMPOUND), c -> snr$fluidFuelStorage
                .put(NbtUtils.readBlockPos(c.getCompound("Pos")), MountedFluidStorage.deserialize(c.getCompound("Data"))));
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void write(CompoundTag nbt, boolean clientPacket, CallbackInfo ci) {
        ListTag fluidFuelStorageNBT = new ListTag();
        for (BlockPos pos : snr$fluidFuelStorage.keySet()) {
            CompoundTag c = new CompoundTag();
            MountedFluidStorage mountedStorage = snr$fluidFuelStorage.get(pos);
            if (!mountedStorage.isValid())
                continue;
            c.put("Pos", NbtUtils.writeBlockPos(pos));
            c.put("Data", mountedStorage.serialize());
            fluidFuelStorageNBT.add(c);
        }

        nbt.put("FluidFuelStorage", fluidFuelStorageNBT);
    }

    //fixme
//    @Inject(method = "removeStorageFromWorld", at = @At("RETURN"))
//    public void removeStorageFromWorld(CallbackInfo ci) {
//        snr$fluidFuelStorage.values()
//                .forEach(MountedFluidStorage::removeStorageFromWorld);
//    }

    @Inject(method = "addStorageToWorld", at = @At("RETURN"))
    private void addStorageToWorld(StructureTemplate.StructureBlockInfo block, BlockEntity blockEntity, CallbackInfo ci) {
        if (snr$fluidFuelStorage.containsKey(block.pos)) {
            MountedFluidStorage mountedStorage = snr$fluidFuelStorage.get(block.pos);
            if (mountedStorage.isValid())
                mountedStorage.addStorageToWorld(blockEntity);
        }
    }

    @Override
    public void snr$setFuelFluids(CombinedTankWrapper combinedTankWrapper) {
        snr$fluidFuelInventory = combinedTankWrapper;
    }

    @Override
    public CombinedTankWrapper snr$getFuelFluids() {
        return snr$fluidFuelInventory;
    }

    @Override
    public void snr$setFluidFuelStorage(Map<BlockPos, MountedFluidStorage> storageMap) {
        snr$fluidFuelStorage = storageMap;
    }

    @Override
    public Map<BlockPos, MountedFluidStorage> snr$getFluidFuelStorage() {
        return snr$fluidFuelStorage;
    }
}
