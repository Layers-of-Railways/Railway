package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerServer;
import com.simibubi.create.AllBogeyStyles;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;

@Mixin(value = StationBlockEntity.class, remap = false)
public abstract class MixinStationBlockEntity extends SmartBlockEntity {
    @Shadow @Nullable public abstract GlobalStation getStation();

    private MixinStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "trackClicked", at = @At("HEAD"))
    private void storePlayer(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BogeyCategoryHandlerServer.currentPlayer = player.getUUID();
    }

    @Inject(method = "trackClicked", at = @At("RETURN"))
    private void clearPlayer(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BogeyCategoryHandlerServer.currentPlayer = null;
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "trackClicked",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V", remap = true),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = true, require = 0
    )
    private void snr$setBogeyData(Player player, InteractionHand hand, ITrackBlock track, BlockState state, BlockPos pos,
                              CallbackInfoReturnable<Boolean> cir, BoundingBox bb, BlockPos up, BlockPos down,
                              int bogeyOffset, ItemStack handItem, boolean upsideDown, BlockPos targetPos) {
        BogeyStyle style = BogeyCategoryHandlerServer.getStyle(player.getUUID());
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

    @Redirect(method = "dropSchedule", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/GlobalStation;getPresentTrain()Lcom/simibubi/create/content/trains/entity/Train;"), require = 0)
    private Train returnOverridenTrain(GlobalStation instance) {
        Train train = instance.getPresentTrain();
        if (train == null)
            train = dropScheduleTrain;
        dropScheduleTrain = null;
        return train;
    }
}
