package com.railwayteam.railways.content.items;

import com.railwayteam.railways.content.blocks.BogieBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;

public class BogieItem extends BlockItem {
    public BogieItem(Block p_i48527_1_, Properties p_i48527_2_) {
        super(p_i48527_1_, p_i48527_2_);
    }

    @Override
    public ActionResultType tryPlace(BlockItemUseContext ctx) {
        World world = ctx.getWorld();
        if(BogieBlock.isValidPosition(world::isAirBlock, p -> world.getBlockState(p).isReplaceable(ctx), ctx.getPos())) {
            if (ctx.getPlayer() == null || !ctx.getPlayer().abilities.isCreativeMode) {
                ctx.getItem().shrink(1);
            }
            return super.tryPlace(ctx);
        }
        return ActionResultType.FAIL;
    }
}
