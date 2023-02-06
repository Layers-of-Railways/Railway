package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.curiosities.deco.SlidingDoorTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Components;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SlidingDoorTileEntity.class, remap = false)
public class MixinSlidingDoorTileEntity implements SlidingDoorMode.IHasDoorMode {
    private ScrollOptionBehaviour<SlidingDoorMode> doorModeScroll;

    @Inject(method = "addBehaviours", at = @At("RETURN"))
    private void addScrollBehaviour(List<TileEntityBehaviour> behaviours, CallbackInfo ci) {
        SlidingDoorTileEntity this_ = (SlidingDoorTileEntity) (Object) this;
        doorModeScroll = new ScrollOptionBehaviour<>(SlidingDoorMode.class, Components.translatable("create.sliding_door.mode"), this_, new SlidingDoorMode.SlidingDoorValueBoxTransform());
        doorModeScroll.requiresWrench();
//        doorModeScroll.value = doorModeScroll.scrollableValue = 1;
        behaviours.add(doorModeScroll);
    }

    @Override
    public SlidingDoorMode getSlidingDoorMode() {
        return doorModeScroll == null ? SlidingDoorMode.AUTO : doorModeScroll.get();
    }
}
