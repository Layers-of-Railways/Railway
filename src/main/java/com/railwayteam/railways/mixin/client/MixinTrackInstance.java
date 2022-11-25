package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.IGetBezierConnection;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TrackInstance.class, remap = false)
public abstract class MixinTrackInstance implements IGetBezierConnection {
  @Shadow
  public abstract void remove();

  @Nullable
  private BezierConnection bezierConnection = null;

  @Override
  public @Nullable BezierConnection getBezierConnection() {
    return bezierConnection;
  }

  @Inject(method = "createInstance", at = @At("HEAD"), remap = false)
  private void preCreateInstance(BezierConnection bc, CallbackInfoReturnable<?> cir) {
    this.bezierConnection = bc;
  }

  @Inject(method = "update", at = @At(value = "RETURN", ordinal = 0), remap = false)
  private void removeWithCasing(CallbackInfo ci) { //otherwise it visually stays when an encased track is broken
    this.remove();
  }
}
