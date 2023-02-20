package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ILimitedGlobalStation;
import com.railwayteam.railways.mixin_interfaces.ISidedStation;
import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = GlobalStation.class, remap = false)
public abstract class MixinGlobalStation implements ILimitedGlobalStation, ISidedStation {
    @Shadow @Nullable public abstract Train getNearestTrain();

    @Shadow @Nullable public abstract Train getImminentTrain();

    private boolean limitEnabled;
    private boolean openRight = true;
    private boolean openLeft = true;

    @Override
    public boolean isStationEnabled() {
        return !limitEnabled || (getNearestTrain()) == null;
    }

    @Override
    public Train getDisablingTrain() {
        if (!limitEnabled)
            return null;
        return (getNearestTrain());
    }

    @Override
    public Train orDisablingTrain(Train before, Train except) {
        if (before == null || before == except)
            before = getDisablingTrain();
        return before;
    }

    @Override
    public boolean opensRight() {
        return openRight;
    }

    @Override
    public boolean opensLeft() {
        return openLeft;
    }

    @Override
    public void setOpensRight(boolean opensRight) {
        this.openRight = opensRight;
    }

    @Override
    public void setOpensLeft(boolean opensLeft) {
        this.openLeft = opensLeft;
    }

    @Override
    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
    }

    @Override
    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    @Inject(method = "read(Lnet/minecraft/nbt/CompoundTag;ZLcom/simibubi/create/content/logistics/trains/DimensionPalette;)V", at = @At("TAIL"), remap = true)
    private void readLimit(CompoundTag nbt, boolean migration, DimensionPalette dimensions, CallbackInfo ci) {
        limitEnabled = nbt.getBoolean("LimitEnabled");
        openRight = !nbt.contains("OpenRight") || nbt.getBoolean("OpenRight");
        openLeft = !nbt.contains("OpenLeft") || nbt.getBoolean("OpenLeft");
    }

    @Inject(method = "read(Lnet/minecraft/network/FriendlyByteBuf;Lcom/simibubi/create/content/logistics/trains/DimensionPalette;)V", at = @At("TAIL"), remap = true)
    private void readNetLimit(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        limitEnabled = buffer.readBoolean();
        openRight = buffer.readBoolean();
        openLeft = buffer.readBoolean();
    }

    @Inject(method = "write(Lnet/minecraft/nbt/CompoundTag;Lcom/simibubi/create/content/logistics/trains/DimensionPalette;)V", at = @At("TAIL"), remap = true)
    private void writeLimit(CompoundTag nbt, DimensionPalette dimensions, CallbackInfo ci) {
        nbt.putBoolean("LimitEnabled", limitEnabled);
        nbt.putBoolean("OpenRight", openRight);
        nbt.putBoolean("OpenLeft", openLeft);
    }

    @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;Lcom/simibubi/create/content/logistics/trains/DimensionPalette;)V", at = @At("TAIL"), remap = true)
    private void writeNetLimit(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        buffer.writeBoolean(limitEnabled);
        buffer.writeBoolean(openRight);
        buffer.writeBoolean(openLeft);
    }
}
