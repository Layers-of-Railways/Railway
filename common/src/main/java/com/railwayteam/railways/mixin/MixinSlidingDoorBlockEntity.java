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

import com.railwayteam.railways.content.extended_sliding_doors.SlidingDoorMode;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SlidingDoorBlockEntity.class, remap = false)
public class MixinSlidingDoorBlockEntity implements SlidingDoorMode.IHasDoorMode {
    @Unique
    private ScrollOptionBehaviour<SlidingDoorMode> railways$doorModeScroll;

    @Inject(method = "addBehaviours", at = @At("RETURN"))
    private void addScrollBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        SlidingDoorBlockEntity this_ = (SlidingDoorBlockEntity) (Object) this;
        railways$doorModeScroll = new ScrollOptionBehaviour<>(SlidingDoorMode.class, Components.translatable("create.sliding_door.mode"), this_, new SlidingDoorMode.SlidingDoorValueBoxTransform()) {
            @Override
            public void read(CompoundTag nbt, boolean clientPacket) {
                super.read(nbt, clientPacket);
                setValue(value); // ensure that it is properly bounded
            }
        };
        railways$doorModeScroll.requiresWrench();
//        doorModeScroll.value = doorModeScroll.scrollableValue = 1;
        behaviours.add(railways$doorModeScroll);
    }

    @Override
    public SlidingDoorMode railways$getSlidingDoorMode() {
        return railways$doorModeScroll == null ? SlidingDoorMode.NORMAL : railways$doorModeScroll.get();
    }
}
