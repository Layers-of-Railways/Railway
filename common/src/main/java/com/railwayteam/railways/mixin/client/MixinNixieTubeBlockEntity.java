package com.railwayteam.railways.mixin.client;

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
    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/redstone/nixieTube/NixieTubeBlockEntity;signalState:Lcom/simibubi/create/content/trains/signal/SignalBlockEntity$SignalState;", ordinal = 0), cancellable = true)
    private void snr$handleOverride(CallbackInfo ci) {
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
    public void refresh(@Nullable SignalBlockEntity signalBE, SignalBlockEntity.SignalState state, int ticks, boolean distantSignal) {
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
    public Optional<SignalBlockEntity.SignalState> getOverriddenState() {
        if (overrideLastingTicks > 0 && signalState != null)
            return Optional.of(signalState);
        return Optional.empty();
    }
}
