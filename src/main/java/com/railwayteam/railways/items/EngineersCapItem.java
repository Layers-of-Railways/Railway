package com.railwayteam.railways.items;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.entities.engineer.EngineerGolemEntity;
import com.simibubi.create.AllBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.command.impl.SummonCommand;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Arrays;

public class EngineersCapItem extends Item {
    public static final String name = "engineers_cap";

    public EngineersCapItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    static Block AndesiteCasing = AllBlocks.ANDESITE_CASING.get();

    static boolean isCasing(Block block) {
        return block.equals(AndesiteCasing);
    }

    static boolean isCasing(BlockState block) {
        return isCasing(block.getBlock());
    }

    static boolean isCasing(World world, BlockPos pos) {
        return isCasing(world.getBlockState(pos));
    }

    public static BlockPos[] getBlocksToRemove(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(!isCasing(state)) return new BlockPos[0];
        BlockPos otherBlock = pos.up();
        if(!isCasing(world, otherBlock)) otherBlock = pos.down();
        if(!isCasing(world, otherBlock)) return new BlockPos[0];
        return new BlockPos[]{pos, otherBlock};
    }

    public static BlockPos getLowest(BlockPos[] pos) {
        BlockPos lowest = pos[0];
        for(BlockPos pos1 : pos) {
            if(pos1.getY() < lowest.getY()) lowest = pos1;
        }
        return lowest;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        World world = ctx.getWorld();
        if(!world.isRemote) {
            PlayerEntity player = ctx.getPlayer();
            Hand hand = ctx.getHand();
            ItemStack stack = ctx.getItem();

            BlockPos pos = ctx.getPos();
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            BlockPos[] blocksToRemove = getBlocksToRemove(world, pos);
            if(blocksToRemove.length > 0) {
                for(BlockPos pos1 : blocksToRemove) world.breakBlock(pos1, false, player);
//            EngineerGolemEntity golem = new EngineerGolemEntity(ModSetup.R_ENTITY_ENGINEER.get(), world);
//            golem.setPos(pos.getX(), pos.getY(), pos.getZ());
//            world.addEntity(golem);
                ModSetup.R_ENTITY_ENGINEER.get().spawn(
                        world, stack, player, getLowest(blocksToRemove), SpawnReason.STRUCTURE, false, false
                );
                if(!player.isCreative()) {
                    stack.setCount(stack.getCount() - 1);
                    return ActionResultType.CONSUME;
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onItemUse(ctx);
    }

    //    @Override
//    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
//        Vec3d from = player.getEyePosition(0);
//
//        BlockRayTraceResult result = world.rayTraceBlocks(
//                new RayTraceContext(
//                        from,
//                        from.add(player.getLookVec().scale(4)),
//                        RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, null
//                )
//        );
//
//        if (result.getType() == RayTraceResult.Type.BLOCK){
//            BlockPos pos = result.getPos();
//            BlockState blockState = world.getBlockState(pos);
//            Block block = blockState.getBlock();
//            if(block.equals(AndesiteCasing)) {
//                world.breakBlock(pos, false, null);
//                EngineerGolemEntity golem = new EngineerGolemEntity(ModSetup.R_ENTITY_ENGINEER.get(), world);
//                golem.setPos(pos.getX(), pos.getY(), pos.getZ());
//                world.addEntity(golem);
//                SpawnEggItem
//                return ActionResult.success(player.getHeldItem(hand));
//            }
//        }
//        return super.onItemRightClick(world, player, hand);
//    }
}
