package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CarriageBogey.class, remap = false)
public class MixinCarriageBogey {
    /*
    ensure that railways:mono_bogey_upside_down is updated to railways:mono_bogey and upside_down is set to true
     */
    @Redirect(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getString(Ljava/lang/String;)Ljava/lang/String;", remap = true))
    private static String snr$updateMonoBogey(CompoundTag instance, String key) {
        String value = instance.getString(key);
        if (value.equals("railways:mono_bogey_upside_down")) {
            value = "railways:mono_bogey";
            instance.putString(key, value);
            instance.putBoolean("UpsideDown", true);
        }
        return value;
    }
}
