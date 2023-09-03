package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.foundation.config.ui.ConfigHelper;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConfigHelper.class, remap = false)
public class MixinConfigHelper {
    @Inject(method = {
        "hasAnyConfig",
        "hasAnyForgeConfig"
    }, at = @At("HEAD"), cancellable = true)
    private static void markSNRConfig(String modID, CallbackInfoReturnable<Boolean> cir) {
        if (modID.equals(Railways.MODID))
            cir.setReturnValue(true);
    }

    @Inject(method = "findConfigSpecFor", at = @At("HEAD"), cancellable = true)
    private static void returnSNRConfig(ModConfig.Type type, String modID, CallbackInfoReturnable<IConfigSpec<?>> cir) {
        if (modID.equals(Railways.MODID)) {
            cir.setReturnValue(CRConfigs.byType(type).specification);
        }
    }
}
