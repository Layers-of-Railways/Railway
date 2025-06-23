package com.railwayteam.railways.fabric.mixin.client;

import com.railwayteam.railways.annotation.mixin.DevEnvMixin;
import com.railwayteam.railways.util.Utils;
import io.github.fabricators_of_create.porting_lib.PortingLibObjLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.GeometryLoaderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PortingLibObjLoader.class)
public class PortingLibObjLoaderMixin {
    @DevEnvMixin
    @Inject(method = "onInitializeClient", at = @At("RETURN"), remap = false)
    private void registerOnInitializeClient(CallbackInfo ci) {
        if (Utils.isDevEnv())
            GeometryLoaderManager.init();
    }
}
