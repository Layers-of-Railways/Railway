package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IContraptionFuel;
import com.railwayteam.railways.mixin_interfaces.IFuelInventory;
import com.railwayteam.railways.util.AbstractionUtils;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Contraption.class)
public abstract class MixinContraption implements IContraptionFuel {
    @Shadow protected MountedStorageManager storage;

    @Shadow protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Inject(method = "getBlockEntityNBT", at = @At("TAIL"))
    private void getBlockEntityNBT(Level world, BlockPos pos, CallbackInfoReturnable<CompoundTag> cir) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null)
            return;
        CompoundTag nbt = blockEntity.saveWithFullMetadata();

        if (AbstractionUtils.isInstanceOfFuelTankBlockEntity(blockEntity) && nbt.contains("Controller"))
            nbt.put("Controller", NbtUtils.writeBlockPos(toLocalPos(NbtUtils.readBlockPos(nbt.getCompound("Controller")))));
    }

    @Override
    public CombinedTankWrapper snr$getSharedFuelTanks() {
        return ((IFuelInventory) storage).snr$getFuelFluids();
    }
}
