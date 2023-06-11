package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TrackMaterial.class, remap = false)
public class MixinTrackMaterial {
    /*
    Properly deserialize pre-resourcelocation tracks
     */
    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;", remap = true))
    private static ResourceLocation snr$deserializeLegacyTracks(String location) {
        ResourceLocation parsed = ResourceLocation.tryParse(location);
        if (parsed.getNamespace().equals("minecraft")) {
            ResourceLocation alternate = Railways.asResource(parsed.getPath());
            if (TrackMaterial.ALL.containsKey(alternate))
                return alternate;
        }
        return parsed;
    }
}
