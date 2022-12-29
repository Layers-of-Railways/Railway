/*package com.railwayteam.railways.mixin;

import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;

//Ripped from LudoCrypt's Noteblock Expansion
// (https://github.com/LudoCrypt/Noteblock-Expansion-Forge/blob/main/src/main/java/net/ludocrypt/nbexpand/mixin/NoteblockInstrumentMixin.java)
@Mixin(TrackTargetingBehaviour.RenderedTrackOverlayType.class)
public abstract class MixinTrackTargetingBehavior_RenderedTrackOverlayType {
    @Shadow
    @Final
    @Mutable
    private static TrackTargetingBehaviour.RenderedTrackOverlayType[] $VALUES;

    private static final TrackTargetingBehaviour.RenderedTrackOverlayType
        COUPLER_COUPLE = snr$addVariant("COUPLER_COUPLE"),
        COUPLER_DECOUPLE = snr$addVariant("COUPLER_DECOUPLE"),
        COUPLER_BOTH = snr$addVariant("COUPLER_BOTH");


    @Invoker("<init>")
    public static TrackTargetingBehaviour.RenderedTrackOverlayType snr$invokeInit(String internalName, int internalId) {
        throw new AssertionError();
    }

    private static TrackTargetingBehaviour.RenderedTrackOverlayType snr$addVariant(String internalName) {
        ArrayList<TrackTargetingBehaviour.RenderedTrackOverlayType> variants = new ArrayList<TrackTargetingBehaviour.RenderedTrackOverlayType>(Arrays.asList(MixinTrackTargetingBehavior_RenderedTrackOverlayType.$VALUES));
        TrackTargetingBehaviour.RenderedTrackOverlayType variant = snr$invokeInit(internalName, variants.get(variants.size() - 1).ordinal() + 1);
        variants.add(variant);
        MixinTrackTargetingBehavior_RenderedTrackOverlayType.$VALUES = variants.toArray(new TrackTargetingBehaviour.RenderedTrackOverlayType[0]);
        return variant;
    }
}*/
