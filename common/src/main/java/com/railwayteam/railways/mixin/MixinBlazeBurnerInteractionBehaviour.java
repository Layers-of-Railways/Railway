package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerInteractionBehaviour;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlazeBurnerInteractionBehaviour.class, remap = false)
public abstract class MixinBlazeBurnerInteractionBehaviour {

    @Inject(method = "handlePlayerInteraction", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;setSchedule(Lcom/simibubi/create/content/trains/schedule/Schedule;Z)V"))
    private void storeIndex(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity, CallbackInfoReturnable<Boolean> cir) {
        if (contraptionEntity instanceof CarriageContraptionEntity cce) {
            Carriage carriage = cce.getCarriage();
            Train train = carriage.train;
            ((IIndexedSchedule) train).snr$setIndex(train.carriages.indexOf(carriage));
        }
    }
}
