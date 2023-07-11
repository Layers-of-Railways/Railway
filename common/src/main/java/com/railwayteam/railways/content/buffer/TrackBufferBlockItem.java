package com.railwayteam.railways.content.buffer;


import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;

public class TrackBufferBlockItem extends TrackTargetingBlockItem {

    public static <T extends Block> NonNullBiFunction<? super T, Properties, TrackTargetingBlockItem> ofType(
        EdgePointType<?> type) {
        return (b, p) -> new TrackBufferBlockItem(b, p, type);
    }

    public TrackBufferBlockItem(Block pBlock, Properties pProperties, EdgePointType<?> type) {
        super(pBlock, pProperties, type);
    }

    @Override
    public boolean useOnCurve(TrackBlockOutline.BezierPointSelection selection, ItemStack stack) { // No.
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null)
            return false;
        player.displayClientMessage(Lang.translateDirect("track_target.invalid")
                .withStyle(ChatFormatting.RED), true);
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = pContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;

        if (state.getBlock() instanceof ITrackBlock) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;

            Direction[] directions = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP };
            Direction successDirection = null;
            for (Direction direction : directions) {
                BlockPos placePos = pos.relative(direction);
                Vec3 hitPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                        .add(direction.getStepX() * 0.5, direction.getStepY() * 0.5, direction.getStepZ() * 0.5);
                BlockPlaceContext ctx = new BlockPlaceContext(
                        player, pContext.getHand(), stack, new BlockHitResult(hitPos, direction.getOpposite(), placePos, false)
                );
                if (level.getBlockState(placePos).canBeReplaced(ctx)) {
                    successDirection = direction;
                    break;
                }
            }

            if (successDirection == null)
                return InteractionResult.FAIL;
            BlockPos placePos = pos.relative(successDirection);

            BlockState placeState = CRBlocks.TRACK_BUFFER.getDefaultState();
            level.setBlock(placePos, placeState, 11);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }
}
