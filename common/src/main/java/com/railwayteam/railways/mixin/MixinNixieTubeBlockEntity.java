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

import com.railwayteam.railways.content.distant_signals.IOverridableSignal;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.OverridableSignalPacket;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.Optional;

@Mixin(value = NixieTubeBlockEntity.class, remap = false)
public abstract class MixinNixieTubeBlockEntity extends SmartBlockEntity implements IOverridableSignal {
    @Shadow private WeakReference<SignalBlockEntity> cachedSignalTE;

    @Shadow public SignalBlockEntity.SignalState signalState;

    private int overrideLastingTicks = -1;

    private MixinNixieTubeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new UnsupportedOperationException("Cannot instantiate mixin class");
    }

    /*
    Inject right before signalState is set to null
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void railways$handleOverride(CallbackInfo ci) { // needs to decrement override lasting on server and client
        if (overrideLastingTicks > 0) {
            overrideLastingTicks--;
            ci.cancel();
            SignalBlockEntity signalBE = cachedSignalTE.get();
            if (signalBE != null && !signalBE.isRemoved()) {
                signalState = signalBE.getState();
            }
        } else if (overrideLastingTicks == 0) {
            overrideLastingTicks--;
            cachedSignalTE.clear();
            signalState = null;
        }
    }

    @Override
    public void railways$refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks, boolean distantSignal) {
        if (level == null) return;
        cachedSignalTE = new WeakReference<>(signalBE);
        signalState = state;
        overrideLastingTicks = ticks;
        if (!level.isClientSide) {
            CRPackets.PACKETS.sendTo(PlayerSelection.tracking(this),
                new OverridableSignalPacket(getBlockPos(),signalBE == null ? null : signalBE.getBlockPos(),
                    state, ticks, distantSignal));
        }
    }

    @Override
    public Optional<SignalBlockEntity.SignalState> railways$getOverriddenState() {
        if (overrideLastingTicks > 0 && signalState != null)
            return Optional.of(signalState);
        return Optional.empty();
    }
}
