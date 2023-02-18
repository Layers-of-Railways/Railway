package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.monobogey.IPotentiallyUpsideDownBogeyBlock;
import com.railwayteam.railways.mixin_interfaces.IBogeyTypeAwareTravellingPoint;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.IBogeyBlock;
import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.entity.TravellingPoint;
import com.simibubi.create.foundation.utility.RegisteredObjects;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TravellingPoint.class, remap = false)
public class MixinTravellingPoint implements IBogeyTypeAwareTravellingPoint {

    private IBogeyBlock type;

    @Override
    public IBogeyBlock getType() {
        return type;
    }

    @Override
    public void setType(IBogeyBlock block) {
        type = block;
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeType(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        if (type != null)
            cir.getReturnValue().putString("Type", RegisteredObjects.getKeyOrThrow((Block) type)
                .toString());
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void readType(CompoundTag tag, TrackGraph graph, DimensionPalette dimensions, CallbackInfoReturnable<TravellingPoint> cir) {
        if (tag.contains("Type", Tag.TAG_STRING))
            ((IBogeyTypeAwareTravellingPoint) cir.getReturnValue())
                .setType((IBogeyBlock) Registry.BLOCK.get(
                    new ResourceLocation(tag.getString("Type"))
                ));
    }

    @Redirect(method = "getPositionWithOffset", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;", remap = true))
    private Vec3 changeNormalScale(Vec3 instance, double pFactor) { //scale normal properly for hanging bogeys
        if (type instanceof IPotentiallyUpsideDownBogeyBlock pudb && pudb.isUpsideDown())
            pFactor = -pFactor;
        return instance.scale(pFactor);
    }
}
