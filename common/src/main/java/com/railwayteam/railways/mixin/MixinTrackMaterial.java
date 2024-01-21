package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = TrackMaterial.class, remap = false)
public class MixinTrackMaterial {
    /**
     * DataFixer for Modded Compat Cherry Tracks.
     * If it finds a modded cherry track material it'll replace it {@link CRTrackMaterials#CHERRY }
     * Which is from 1.20 Vanilla.
     */
    @Inject(method = "deserialize", at = @At("HEAD"), cancellable = true)
    private static void snr$updateCherryCompatTracks(String serializedName, CallbackInfoReturnable<TrackMaterial> cir) {
        switch (serializedName) {
            case "biomesoplenty:cherry", "byg:cherry", "blue_skies:cherry"
                    -> cir.setReturnValue(CRTrackMaterials.CHERRY);
            case "biomesoplenty:cherry_wide", "byg:cherry_wide", "blue_skies:cherry_wide"
                    -> cir.setReturnValue(CRTrackMaterials.getWide(CRTrackMaterials.CHERRY));
            case "biomesoplenty:cherry_narrow", "byg:cherry_narrow", "blue_skies:cherry_narrow"
                    -> cir.setReturnValue(CRTrackMaterials.getNarrow(CRTrackMaterials.CHERRY));
        }
    }

    /*
     * Properly deserialize pre-resourcelocation tracks
     */
    @Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;", remap = true))
    private static ResourceLocation railways$deserializeLegacyTracks(String location) {
        ResourceLocation parsed = ResourceLocation.tryParse(location);
        if (parsed.getNamespace().equals("minecraft")) {
            ResourceLocation alternate = Railways.asResource(parsed.getPath());
            if (TrackMaterial.ALL.containsKey(alternate))
                return alternate;
        }
        return parsed;
    }
}
