package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DispenserBlock extends ContainerBlock {
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   /** Registry for all dispense behaviors. */
   private static final Map<Item, IDispenseItemBehavior> DISPENSE_BEHAVIOR_REGISTRY = Util.make(new Object2ObjectOpenHashMap<>(), (p_212564_0_) -> {
      p_212564_0_.defaultReturnValue(new DefaultDispenseItemBehavior());
   });

   public static void registerDispenseBehavior(IItemProvider itemIn, IDispenseItemBehavior behavior) {
      DISPENSE_BEHAVIOR_REGISTRY.put(itemIn.asItem(), behavior);
   }

   protected DispenserBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TRIGGERED, Boolean.valueOf(false)));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 4;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof DispenserTileEntity) {
            player.openContainer((DispenserTileEntity)tileentity);
            if (tileentity instanceof DropperTileEntity) {
               player.addStat(Stats.INSPECT_DROPPER);
            } else {
               player.addStat(Stats.INSPECT_DISPENSER);
            }
         }

         return ActionResultType.SUCCESS;
      }
   }

   protected void dispense(World worldIn, BlockPos pos) {
      ProxyBlockSource proxyblocksource = new ProxyBlockSource(worldIn, pos);
      DispenserTileEntity dispensertileentity = proxyblocksource.getBlockTileEntity();
      int i = dispensertileentity.getDispenseSlot();
      if (i < 0) {
         worldIn.playEvent(1001, pos, 0);
      } else {
         ItemStack itemstack = dispensertileentity.getStackInSlot(i);
         IDispenseItemBehavior idispenseitembehavior = this.getBehavior(itemstack);
         if (idispenseitembehavior != IDispenseItemBehavior.NOOP) {
            dispensertileentity.setInventorySlotContents(i, idispenseitembehavior.dispense(proxyblocksource, itemstack));
         }

      }
   }

   protected IDispenseItemBehavior getBehavior(ItemStack stack) {
      return DISPENSE_BEHAVIOR_REGISTRY.get(stack.getItem());
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
      boolean flag1 = state.get(TRIGGERED);
      if (flag && !flag1) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
         worldIn.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(true)), 4);
      } else if (!flag && flag1) {
         worldIn.setBlockState(pos, state.with(TRIGGERED, Boolean.valueOf(false)), 4);
      }

   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      this.dispense(worldIn, pos);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new DispenserTileEntity();
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof DispenserTileEntity) {
            ((DispenserTileEntity)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof DispenserTileEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (DispenserTileEntity)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   /**
    * Get the position where the dispenser at the given Coordinates should dispense to.
    */
   public static IPosition getDispensePosition(IBlockSource coords) {
      Direction direction = coords.getBlockState().get(FACING);
      double d0 = coords.getX() + 0.7D * (double)direction.getXOffset();
      double d1 = coords.getY() + 0.7D * (double)direction.getYOffset();
      double d2 = coords.getZ() + 0.7D * (double)direction.getZOffset();
      return new Position(d0, d1, d2);
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, TRIGGERED);
   }
}