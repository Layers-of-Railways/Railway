package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Components;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SlidingDoorBlockEntity.class, remap = false)
public class MixinSlidingDoorBlockEntity implements SlidingDoorMode.IHasDoorMode {
    private ScrollOptionBehaviour<SlidingDoorMode> doorModeScroll;

    @Inject(method = "addBehaviours", at = @At("RETURN"))
    private void addScrollBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        SlidingDoorBlockEntity this_ = (SlidingDoorBlockEntity) (Object) this;
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
