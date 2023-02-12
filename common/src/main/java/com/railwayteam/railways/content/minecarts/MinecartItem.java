package com.railwayteam.railways.content.minecarts;

import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

public class MinecartItem extends Item {
  private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
    private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

    /**
     * Dispense the specified stack, play the dispense sound and spawn particles.
     */
    public ItemStack execute(BlockSource blockSource, ItemStack stack) {
      Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
      Level level = blockSource.getLevel();
      double dx = blockSource.x() + (double)direction.getStepX() * 1.125D;
      double dy = Math.floor(blockSource.y()) + (double)direction.getStepY();
      double dz = blockSource.z() + (double)direction.getStepZ() * 1.125D;
      BlockPos blockpos = blockSource.getPos().relative(direction);
      BlockState blockstate = level.getBlockState(blockpos);
      RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
      double dy_2;
      if (blockstate.is(BlockTags.RAILS)) {
        if (railshape.isAscending()) {
          dy_2 = 0.6D;
        } else {
          dy_2 = 0.1D;
        }
      } else {
        if (!blockstate.isAir() || !level.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
          return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
        }

        BlockState blockstate1 = level.getBlockState(blockpos.below());
        RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock) blockstate1.getBlock()).getRailDirection(blockstate1, level, blockpos.below(), null) : RailShape.NORTH_SOUTH;
        if (direction != Direction.DOWN && railshape1.isAscending()) {
          dy_2 = -0.4D;
        } else {
          dy_2 = -0.9D;
        }
      }

      Entity entity = ((MinecartItem) stack.getItem()).cartEntity.get().create(level);
      if (entity != null) {
        entity.setPos(dx, dy + dy_2, dz);
        entity.xo = dx;
        entity.yo = dy + dy_2;
        entity.zo = dz;
        if (stack.hasCustomHoverName()) {
          entity.setCustomName(stack.getHoverName());
        }

        level.addFreshEntity(entity);
        stack.shrink(1);
      }
      return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playSound(BlockSource blockSource) {
      blockSource.getLevel().levelEvent(1000, blockSource.getPos(), 0);
    }
  };

  public final EntityEntry<?> cartEntity;

  public MinecartItem(Properties pProperties, EntityEntry<?> cartEntity) {
    super(pProperties);
    this.cartEntity = cartEntity;
    DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
  }

  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos clickedPos = context.getClickedPos();
    BlockState clickedState = level.getBlockState(clickedPos);
    if (!clickedState.is(BlockTags.RAILS)) {
      return InteractionResult.FAIL;
    } else {
      ItemStack handStack = context.getItemInHand();
      if (!level.isClientSide) {
        RailShape railshape = clickedState.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)clickedState.getBlock()).getRailDirection(clickedState, level, clickedPos, null) : RailShape.NORTH_SOUTH;
        double y_offset = 0.0D;
        if (railshape.isAscending()) {
          y_offset = 0.5D;
        }

        Entity entity = this.cartEntity.get().create(level);
        if (entity == null)
          return InteractionResult.FAIL;
        entity.setPos((double)clickedPos.getX() + 0.5D, (double)clickedPos.getY() + 0.0625D + y_offset, (double)clickedPos.getZ() + 0.5D);
        entity.xo = (double)clickedPos.getX() + 0.5D;
        entity.yo = (double)clickedPos.getY() + 0.0625D + y_offset;
        entity.zo = (double)clickedPos.getZ() + 0.5D;
        if (handStack.hasCustomHoverName()) {
          entity.setCustomName(handStack.getHoverName());
        }

        level.addFreshEntity(entity);
        level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, clickedPos);
      }

      handStack.shrink(1);
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
  }
}
