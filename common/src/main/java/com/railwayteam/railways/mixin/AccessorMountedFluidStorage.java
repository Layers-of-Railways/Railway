package com.railwayteam.railways.mixin;

import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MountedFluidStorage.class)
public interface AccessorMountedFluidStorage {
    @Accessor SmartFluidTank getTank();
}
