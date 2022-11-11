package com.railwayteam.railways.content.semaphore;

import com.simibubi.create.foundation.utility.placement.IPlacementHelper;
import com.simibubi.create.foundation.utility.placement.PlacementHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SemaphoreItem extends BlockItem {
    public SemaphoreItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {

        //return super.place(pContext);
        IPlacementHelper placementHelper = PlacementHelpers.get(SemaphoreBlock.placementHelperId);

        Level world = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos pos = pContext.getClickedPos();
        if(!pContext.replacingClickedOnBlock())
        {
            pos = pos.offset(pContext.getClickedFace().getOpposite().getNormal());
        }
        BlockState state = world.getBlockState(pos);
        //BlockHitResult ray = pContext.getLevel().clip(new ClipContext(player.getEyePosition(),pContext.getClickLocation(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,null));
        BlockHitResult ray = (BlockHitResult)Minecraft.getInstance().hitResult;
        if(!placementHelper.matchesState(state))
            return super.place(pContext);

        InteractionResult result = placementHelper.getOffset(player, world, state, pos, ray)
                .placeInWorld(world, this, player, pContext.getHand(), ray);

        if(result.consumesAction())
            return result;
        else
            return super.place(pContext);
    }
}
