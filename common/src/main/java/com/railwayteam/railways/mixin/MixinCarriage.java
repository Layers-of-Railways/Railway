package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.mixin_interfaces.CarriageBogeyUtils;
import com.railwayteam.railways.mixin_interfaces.ICarriageBufferDistanceTracker;
import com.railwayteam.railways.mixin_interfaces.ICarriageConductors;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.entity.*;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(value = Carriage.class, remap = false)
public abstract class MixinCarriage implements ICarriageConductors, ICarriageBufferDistanceTracker {

    @Shadow public Train train;

    @Shadow public abstract CarriageBogey leadingBogey();

    @Shadow public abstract CarriageBogey trailingBogey();

    @Shadow public abstract TravellingPoint getLeadingPoint();

    @Shadow public abstract TravellingPoint getTrailingPoint();

    private final List<UUID> controllingConductors = new ArrayList<>();

    @Override
    public List<UUID> getControllingConductors() {
        return controllingConductors;
    }

    @Unique
    private @Nullable Integer railways$leadingBufferDistance = null;
    @Unique
    private @Nullable Integer railways$trailingBufferDistance = null;

    @Override
    public @Nullable Integer railways$getLeadingDistance() {
        return railways$leadingBufferDistance;
    }

    @Override
    public @Nullable Integer railways$getTrailingDistance() {
        return railways$trailingBufferDistance;
    }

    @Override
    public void railways$setLeadingDistance(int distance) {
        railways$leadingBufferDistance = distance;
    }

    @Override
    public void railways$setTrailingDistance(int distance) {
        railways$trailingBufferDistance = distance;
    }

    @Redirect(method = "updateConductors", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;checkConductors()Lcom/simibubi/create/foundation/utility/Couple;"))
    private Couple<Boolean> addControllingConductors(CarriageContraptionEntity instance) {
        controllingConductors.clear();
        if (instance.getContraption() instanceof CarriageContraption cc) {
            for (Entity passenger : instance.getPassengers()) {
                if (!(passenger instanceof ConductorEntity))
                    continue;
                BlockPos seatOf = cc.getSeatOf(passenger.getUUID());
                if (seatOf == null)
                    continue;
                Couple<Boolean> validSides = cc.conductorSeats.get(seatOf);
                if (validSides == null)
                    continue;
                if (validSides.getFirst() || validSides.getSecond())
                    controllingConductors.add(passenger.getUUID());
            }
        }
        return instance.checkConductors();
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeControllingConductors(DimensionPalette dimensions, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = cir.getReturnValue();
        ListTag listTag = new ListTag();
        for (UUID uuid : controllingConductors) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putUUID("UUID", uuid);
            listTag.add(uuidTag);
        }
        tag.put("ControllingConductors", listTag);

        if (railways$leadingBufferDistance != null)
            tag.putInt("LeadingBufferDistance", railways$leadingBufferDistance);
        if (railways$trailingBufferDistance != null)
            tag.putInt("TrailingBufferDistance", railways$trailingBufferDistance);
    }

    @Inject(method = "read", at = @At("RETURN"))
    private static void readControllingConductors(CompoundTag tag, TrackGraph graph, DimensionPalette dimensions, CallbackInfoReturnable<Carriage> cir) {
        Carriage carriage = cir.getReturnValue();
        List<UUID> controllingConductors = ((ICarriageConductors) carriage).getControllingConductors();
        controllingConductors.clear();
        if (tag.contains("ControllingConductors", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("ControllingConductors", Tag.TAG_COMPOUND);
            for (Tag item : listTag) {
                if (item instanceof CompoundTag uuidTag && uuidTag.hasUUID("UUID")) {
                    controllingConductors.add(uuidTag.getUUID("UUID"));
                }
            }
        }

        if (tag.contains("LeadingBufferDistance", Tag.TAG_INT))
            ((ICarriageBufferDistanceTracker) carriage).railways$setLeadingDistance(tag.getInt("LeadingBufferDistance"));

        if (tag.contains("TrailingBufferDistance", Tag.TAG_INT))
            ((ICarriageBufferDistanceTracker) carriage).railways$setTrailingDistance(tag.getInt("TrailingBufferDistance"));
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void markTravelStart(Level level, TrackGraph graph, double distance, TravellingPoint toFollowForward,
                                 TravellingPoint toFollowBackward, int type, CallbackInfoReturnable<Double> cir) {
        if (train.navigation.isActive()) // only do automatic stuff when automatically operated
            Railways.trackEdgeCarriageTravelling = true;
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void markTravelEnd(Level level, TrackGraph graph, double distance, TravellingPoint toFollowForward,
                               TravellingPoint toFollowBackward, int type, CallbackInfoReturnable<Double> cir) {
        Railways.trackEdgeCarriageTravelling = false;
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "isOnIncompatibleTrack", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;isOnIncompatibleTrack(Lcom/simibubi/create/content/trains/entity/Carriage;Z)Z", ordinal = 0))
    private boolean allowUniversalTrackLeading(boolean original) {
        return railways$isIncompatible(original, true);
    }

    @SuppressWarnings("unused")
    @ModifyExpressionValue(method = "isOnIncompatibleTrack", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/bogey/AbstractBogeyBlock;isOnIncompatibleTrack(Lcom/simibubi/create/content/trains/entity/Carriage;Z)Z", ordinal = 1))
    private boolean allowUniversalTrackTrailing(boolean original) {
        return railways$isIncompatible(original, false);
    }

    @Unique
    private boolean railways$isIncompatible(boolean original, boolean leading) {
        CarriageBogey bogey = leading ? leadingBogey() : trailingBogey();
        TravellingPoint point = leading ? getLeadingPoint() : getTrailingPoint();
        if (point.edge == null)
            return false;
        if (point.edge.getTrackMaterial().trackType == CRTrackMaterials.CRTrackType.UNIVERSAL)
            return false;
        if (CarriageBogeyUtils.getType(bogey).getTrackType(bogey.getStyle()) == CRTrackMaterials.CRTrackType.UNIVERSAL)
            return false;
        return original;
    }

    @Inject(method = "manageEntities", at = @At("HEAD"), cancellable = true)
    private void allowTravellingWithoutLevel(Level level, CallbackInfo ci) {
        if (level == null) ci.cancel();
    }
}
