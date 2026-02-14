/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_tracks.casing.CasingChecker;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.schematics.SchematicWorld;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.foundation.blockEntity.RemoveBlockEntityPacket;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = TrackBlockEntity.class, remap = false)
public abstract class MixinTrackBlockEntity extends SmartBlockEntity implements IHasTrackCasing {
    @Shadow
    Map<BlockPos, BezierConnection> connections;

    @Unique
    protected Block railways$trackCasing;
    @Unique
    protected boolean railways$isAlternateModel;

    protected MixinTrackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @Nullable Block railways$getTrackCasing() {
        return railways$trackCasing;
    }

    @Override
    public void railways$setTrackCasing(@Nullable Block trackCasing) {
        if (trackCasing != null && !CasingChecker.isValid(trackCasing)) //sanity check
            return;
        this.railways$trackCasing = trackCasing;
        notifyUpdate();
        if (this.level != null) {
            if (this.railways$trackCasing == null) { //Clean up the tile entity if it is no longer needed
                CasingCollisionUtils.manageTracks((TrackBlockEntity) (Object) this, true);
                if (!this.level.isClientSide) {
                    if (!this.connections.isEmpty() || getBlockState().getOptionalValue(TrackBlock.SHAPE)
                        .orElse(TrackShape.NONE)
                        .isPortal())
                        return;
                    BlockState blockState = this.level.getBlockState(worldPosition);
                    if (blockState.hasProperty(TrackBlock.HAS_BE))
                        level.setBlockAndUpdate(worldPosition, blockState.setValue(TrackBlock.HAS_BE, false));
                    if (!(this.level instanceof SchematicWorld))
                        CRPackets.PACKETS.sendTo(PlayerSelection.tracking(this), new RemoveBlockEntityPacket(worldPosition));
                }
            } else if (trackCasing != null && !railways$isAlternateModel) {
                CasingCollisionUtils.manageTracks((TrackBlockEntity) (Object) this, false);
            }
        }
    }

    @Override
    public boolean railways$isAlternate() {
        return railways$isAlternateModel;
    }

    @Override
    public void railways$setAlternate(boolean alternate) {
        if (getBlockState().getValue(TrackBlock.SHAPE).getModel().equals("ascending")) {
            alternate = false;
        }
        this.railways$isAlternateModel = alternate;
        if (railways$trackCasing != null) {
            CasingCollisionUtils.manageTracks((TrackBlockEntity) (Object) this, alternate);
        }
        notifyUpdate();
    }

    // Track casings require a TE to function, so prevent it from being removed.
    @Inject(
        method = "removeConnection",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
            remap = true
        ),
        cancellable = true
    )
    private void preventTileRemoval(BlockPos target, CallbackInfo ci) {
        if (railways$getTrackCasing() != null) {
            notifyUpdate();
            ci.cancel();
        }
    }

    @Inject(
        method = "removeInboundConnections",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/foundation/blockEntity/RemoveBlockEntityPacket;<init>(Lnet/minecraft/core/BlockPos;)V",
            remap = true
        ),
        cancellable = true
    )
    private void preventTileRemoval2(CallbackInfo ci) {
        if (railways$getTrackCasing() != null) {
            notifyUpdate();
            ci.cancel();
        }
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void writeCasing(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        Block casing = railways$getTrackCasing();
        if (casing != null) {
            tag.putString("TrackCasing", BuiltInRegistries.BLOCK.getKey(casing).toString());
        }
        tag.putBoolean("AlternateModel", this.railways$isAlternate());
    }

    @Inject(method = "read", at = @At("RETURN"))
    private void readCasing(CompoundTag tag, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("AlternateModel")) {
            this.railways$setAlternate(tag.getBoolean("AlternateModel"));
        } else {
            this.railways$setAlternate(false);
        }

        if (tag.contains("TrackCasing")) {
            ResourceLocation casingName = ResourceLocation.of(tag.getString("TrackCasing"), ':');
            if (BuiltInRegistries.BLOCK.containsKey(casingName)) {
                this.railways$setTrackCasing(BuiltInRegistries.BLOCK.get(casingName));
                return;
            }
        }
        this.railways$setTrackCasing(null);
    }

    @Inject(method = "lazyTick", at = @At("HEAD"))
    private void manageCasingCollisions(CallbackInfo ci) {
        if (railways$trackCasing == null || railways$isAlternateModel) return;
        CasingCollisionUtils.manageTracks((TrackBlockEntity) (Object) this, false);
    }
}
