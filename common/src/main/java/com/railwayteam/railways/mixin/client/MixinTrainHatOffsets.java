package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.simibubi.create.content.trains.schedule.TrainHatOffsets;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrainHatOffsets.class, remap = false)
public class MixinTrainHatOffsets {
  @Inject(at = @At("HEAD"), method = "getOffset", cancellable = true)
  private static void inj$getOffset(EntityModel<?> model, CallbackInfoReturnable<Vec3> cir) {
    if (model instanceof ConductorEntityModel) {
      cir.setReturnValue(new Vec3(0f, -1f, 0f));
    }
  }
}
