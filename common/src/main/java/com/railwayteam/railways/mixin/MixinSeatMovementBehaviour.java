package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(value = SeatMovementBehaviour.class, remap = false)
public class MixinSeatMovementBehaviour {
    @Inject(method = "visitNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;stopRiding()V", remap = true),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void keepConductors(MovementContext context, BlockPos pos, CallbackInfo ci,
                                AbstractContraptionEntity contraptionEntity, int index, Map<?, ?> seatMapping,
                                BlockState blockState, boolean slab, boolean solid, Entity toDismount) {
        if (toDismount instanceof ConductorEntity)
            ci.cancel();
    }
}
