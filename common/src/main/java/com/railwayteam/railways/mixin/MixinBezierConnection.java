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

import com.llamalad7.mixinextras.sugar.Local;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.casing.CasingChecker;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BezierConnection.class, remap = false)
public abstract class MixinBezierConnection implements IHasTrackCasing {
    @Shadow public Couple<BlockPos> tePositions;

    @Shadow public abstract Vec3 getPosition(double t);

    @Unique
    protected Block railways$trackCasing;
    @Unique
    protected boolean railways$isShiftedDown;

    @Override
    public @Nullable Block railways$getTrackCasing() {
        return railways$trackCasing;
    }

    @Override
    public void railways$setTrackCasing(@Nullable Block trackCasing) {
        if (trackCasing != null && !CasingChecker.isValid(trackCasing)) //sanity check
            return;
        this.railways$trackCasing = trackCasing;
    }

    @Override
    public boolean railways$isAlternate() {
        return railways$isShiftedDown;
    }

    @Override
    public void railways$setAlternate(boolean alternate) {
        this.railways$isShiftedDown = alternate;
    }


    @Inject(
        method = "write(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag;",
        at = @At("RETURN"),
        cancellable = true,
        remap = true
    )
    private void write(BlockPos localTo, CallbackInfoReturnable<CompoundTag> cir,
                       @Local(name = "tePositions") Couple<BlockPos> tePositions,
                       @Local(name = "starts") Couple<Vec3> starts,
                       @Local(name = "compound") CompoundTag compound
    ) {
        Block casing = railways$getTrackCasing();
        if (casing != null) {
            if (BuiltInRegistries.BLOCK.getKey(casing).toString().equals("minecraft:block")) {
                Railways.LOGGER.error("NBTwrite trackCasing was minecraft:block!!! for BezierConnection: starts=" + starts + ", primary=" + tePositions.getFirst() + ", secondary=" + tePositions.getSecond() + ", casing: " + casing);
            } else {
                compound.putString("Casing", BuiltInRegistries.BLOCK.getKey(casing).toString());
            }
        }
        compound.putBoolean("ShiftDown", railways$isAlternate());
        cir.setReturnValue(compound);
    }

    @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = true)
    private void netWrite(FriendlyByteBuf buffer, CallbackInfo ci) {
        Block casing = railways$getTrackCasing();
        buffer.writeBoolean(casing != null);
        if (casing != null) {
            buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(casing));
            buffer.writeBoolean(railways$isAlternate());
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = true)
    private void nbtConstructor(CompoundTag compound, BlockPos localTo, CallbackInfo ci) {
        if (compound.contains("Casing", Tag.TAG_STRING)) {
            if (compound.getString("Casing").equals("minecraft:block")) {
                Railways.LOGGER.error("NBTCtor trackCasing was minecraft:block!!! for BezierConnection: primary="+tePositions.getFirst()+", secondary="+tePositions.getSecond());
            }
            //Railways.LOGGER.warn("NBTCtor: Casing="+compound.getString("Casing"));
            railways$setTrackCasing(BuiltInRegistries.BLOCK.get(ResourceLocation.of(compound.getString("Casing"), ':')));
        }
        if (compound.contains("ShiftDown", Tag.TAG_BYTE)) {
            railways$setAlternate(compound.getBoolean("ShiftDown"));
        } else {
            railways$setAlternate(false);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = true)
    private void byteBufConstructor(FriendlyByteBuf buffer, CallbackInfo ci) {
        if (buffer.readBoolean()) {
            railways$setTrackCasing(BuiltInRegistries.BLOCK.get(buffer.readResourceLocation()));
            railways$setAlternate(buffer.readBoolean());
        } else {
            railways$setTrackCasing(null);
        }
    }

    @Inject(method = "spawnItems", at = @At("TAIL"))
    private void spawnCasing(Level level, CallbackInfo ci) {
        Block casing = this.railways$getTrackCasing();
        if (casing != null) {
            Vec3 origin = Vec3.atLowerCornerOf(tePositions.getFirst());
            Vec3 spawnPos = this.getPosition(0.5);
            ItemEntity entity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, new ItemStack(casing));
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }
    }

    @Inject(method = "addItemsToPlayer", at = @At("TAIL"))
    private void addCasingItem(Player player, CallbackInfo ci) {
        Block casing = this.railways$getTrackCasing();
        if (casing != null) {
            Inventory inv = player.getInventory();
            inv.placeItemBackInInventory(new ItemStack(casing));
        }
    }
}
