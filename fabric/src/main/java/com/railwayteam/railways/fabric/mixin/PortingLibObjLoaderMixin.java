package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.util.Utils;
import io.github.fabricators_of_create.porting_lib.PortingLibObjLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.GeometryLoaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// fixme remove this mixin when porting lib gets fixed
@Mixin(PortingLibObjLoader.class)
public class PortingLibObjLoaderMixin {
    @Inject(method = "onInitializeClient", at = @At("RETURN"), remap = false)
    private void registerOnInitializeClient(CallbackInfo ci) {
        if (Utils.isDevEnv())
            GeometryLoaderManager.init();
    }
}
