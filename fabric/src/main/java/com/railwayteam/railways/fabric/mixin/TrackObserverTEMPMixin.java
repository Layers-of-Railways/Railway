package com.railwayteam.railways.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.observer.TrackObserver;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// FIXME: Remove once Create Fabric is fixed
@Mixin(TrackObserver.class)
public class TrackObserverTEMPMixin {
    @WrapOperation(method = "write(Lnet/minecraft/nbt/CompoundTag;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At(value = "INVOKE", target = "Lio/github/fabricators_of_create/porting_lib/util/NBTSerializer;serializeNBTCompound(Ljava/lang/Object;)Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag fixSerialization(Object c, Operation<CompoundTag> original) {
        return ((FilterItemStack) c).serializeNBT();
    }
}
