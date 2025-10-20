/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuHandlerServer;
import com.railwayteam.railways.content.handcar.HandcarBlock;
import com.railwayteam.railways.mixin_interfaces.IIndexedSchedule;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class MixinStationBlockEntity extends SmartBlockEntity {
    @Shadow @Nullable public abstract GlobalStation getStation();

    private MixinStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "trackClicked", at = @At("HEAD"))
    private void storePlayer(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BogeyMenuHandlerServer.setCurrentPlayer(player.getUUID());
    }

    @Inject(method = "trackClicked", at = @At("RETURN"))
    private void clearPlayer(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BogeyMenuHandlerServer.setCurrentPlayer(null);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "trackClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", remap = true),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = true, require = 0
    )
    private void railways$setBogeyData(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos,
                              CallbackInfoReturnable<Boolean> cir, BoundingBox bb, BlockPos up, BlockPos down,
                              int bogeyOffset, ItemStack handItem, boolean upsideDown, BlockPos targetPos) {
        if (track.getMaterial().trackType == CRTrackMaterials.CRTrackType.MONORAIL)
            return;
        Pair<BogeyStyle, BogeySize> styleData = BogeyMenuHandlerServer.getStyle(player.getUUID());
        BogeyStyle style = styleData.getFirst();

        TrackMaterial.TrackType trackType = track.getMaterial().trackType;

        Optional<BogeyStyle> mappedStyleOptional = CRBogeyStyles.getMapped(style, trackType, true);
        if (mappedStyleOptional.isPresent())
            style = mappedStyleOptional.get();

        if (style == AllBogeyStyles.STANDARD)
            return;

        //noinspection DataFlowIssue - we know that level isn't null
        if (level.getBlockEntity(targetPos) instanceof AbstractBogeyBlockEntity bogeyBE) {
            bogeyBE.setBogeyStyle(style);
        }
    }

    private Train dropScheduleTrain;

    @Inject(method = "tryDisassembleTrain", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/GlobalStation;getPresentTrain()Lcom/simibubi/create/content/trains/entity/Train;"))
    private void storeTrainForDropping(ServerPlayer sender, CallbackInfoReturnable<Boolean> cir) {
        dropScheduleTrain = getStation() == null ? null : getStation().getPresentTrain();
    }

    @Inject(method = "tryDisassembleTrain", at = @At("RETURN"))
    private void clearDropTrain(ServerPlayer sender, CallbackInfoReturnable<Boolean> cir) {
        dropScheduleTrain = null;
    }
    
    // TODO - Checkover
    @ModifyReceiver(method = "dropSchedule", at = @At(value = "FIELD", target = "Lcom/simibubi/create/content/trains/entity/Train;runtime:Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;"), require = 0)
    private Train returnOverridenTrain(Train instance) {
        Train train = instance != null ? instance : dropScheduleTrain;
        dropScheduleTrain = null;
        return train;
    }

    @Inject(method = "assemble", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;", ordinal = 0), require = 0)
    private void allowAssemblingWithoutControls(UUID playerUUID, CallbackInfo ci, @Local(name="typeOfFirstBogey") AbstractBogeyBlock<?> typeOfFirstBogey, @Local(name="atLeastOneForwardControls") LocalBooleanRef atLeastOneForwardControls) {
        if (typeOfFirstBogey instanceof HandcarBlock) {
            atLeastOneForwardControls.set(true);
        }
    }

    @Inject(method = "applyAutoSchedule", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/schedule/ScheduleRuntime;setSchedule(Lcom/simibubi/create/content/trains/schedule/Schedule;Z)V"))
    private void setScheduleIndexOnAutoSchedule(CallbackInfo ci, @Local Train imminentTrain) {
        int idx = 0;
        ((IIndexedSchedule) imminentTrain).railways$setIndex(0); // backup
        for (Carriage carriage : imminentTrain.carriages) {
            if (carriage.presentConductors.either(b -> b)) {
                ((IIndexedSchedule) imminentTrain).railways$setIndex(idx);
                if (Utils.isDevEnv())
                    Railways.LOGGER.info("[SET_INDEX {}] on train {} called in MixinStationBlockEntity#setScheduleIndexOnAutoSchedule", idx, imminentTrain.name.getString());
                break;
            }
            idx++;
        }
    }
}
