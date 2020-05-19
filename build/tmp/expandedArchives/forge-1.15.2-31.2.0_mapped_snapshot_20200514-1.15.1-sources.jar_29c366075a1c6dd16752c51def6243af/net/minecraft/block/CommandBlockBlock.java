package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandBlockBlock extends ContainerBlock {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty CONDITIONAL = BlockStateProperties.CONDITIONAL;

   public CommandBlockBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(CONDITIONAL, Boolean.valueOf(false)));
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      CommandBlockTileEntity commandblocktileentity = new CommandBlockTileEntity();
      commandblocktileentity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
      return commandblocktileentity;
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.isRemote) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof CommandBlockTileEntity) {
            CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
            boolean flag = worldIn.isBlockPowered(pos);
            boolean flag1 = commandblocktileentity.isPowered();
            commandblocktileentity.setPowered(flag);
            if (!flag1 && !commandblocktileentity.isAuto() && commandblocktileentity.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
               if (flag) {
                  commandblocktileentity.setConditionMet();
                  worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
               }

            }
         }
      }
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof CommandBlockTileEntity) {
         CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
         CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlockLogic();
         boolean flag = !StringUtils.isNullOrEmpty(commandblocklogic.getCommand());
         CommandBlockTileEntity.Mode commandblocktileentity$mode = commandblocktileentity.getMode();
         boolean flag1 = commandblocktileentity.isConditionMet();
         if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.AUTO) {
            commandblocktileentity.setConditionMet();
            if (flag1) {
               this.execute(state, worldIn, pos, commandblocklogic, flag);
            } else if (commandblocktileentity.isConditional()) {
               commandblocklogic.setSuccessCount(0);
            }

            if (commandblocktileentity.isPowered() || commandblocktileentity.isAuto()) {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
            }
         } else if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.REDSTONE) {
            if (flag1) {
               this.execute(state, worldIn, pos, commandblocklogic, flag);
            } else if (commandblocktileentity.isConditional()) {
               commandblocklogic.setSuccessCount(0);
            }
         }

         worldIn.updateComparatorOutputLevel(pos, this);
      }

   }

   private void execute(BlockState p_193387_1_, World p_193387_2_, BlockPos p_193387_3_, CommandBlockLogic p_193387_4_, boolean p_193387_5_) {
      if (p_193387_5_) {
         p_193387_4_.trigger(p_193387_2_);
      } else {
         p_193387_4_.setSuccessCount(0);
      }

      executeChain(p_193387_2_, p_193387_3_, p_193387_1_.get(FACING));
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 1;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof CommandBlockTileEntity && player.canUseCommandBlock()) {
         player.openCommandBlock((CommandBlockTileEntity)tileentity);
         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
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
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof CommandBlockTileEntity ? ((CommandBlockTileEntity)tileentity).getCommandBlockLogic().getSuccessCount() : 0;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof CommandBlockTileEntity) {
         CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
         CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlockLogic();
         if (stack.hasDisplayName()) {
            commandblocklogic.setName(stack.getDisplayName());
         }

         if (!worldIn.isRemote) {
            if (stack.getChildTag("BlockEntityTag") == null) {
               commandblocklogic.setTrackOutput(worldIn.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK));
               commandblocktileentity.setAuto(this == Blocks.CHAIN_COMMAND_BLOCK);
            }

            if (commandblocktileentity.getMode() == CommandBlockTileEntity.Mode.SEQUENCE) {
               boolean flag = worldIn.isBlockPowered(pos);
               commandblocktileentity.setPowered(flag);
            }
         }

      }
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
      builder.add(FACING, CONDITIONAL);
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite());
   }

   private static void executeChain(World p_193386_0_, BlockPos p_193386_1_, Direction p_193386_2_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_193386_1_);
      GameRules gamerules = p_193386_0_.getGameRules();

      int i;
      BlockState blockstate;
      for(i = gamerules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH); i-- > 0; p_193386_2_ = blockstate.get(FACING)) {
         blockpos$mutable.move(p_193386_2_);
         blockstate = p_193386_0_.getBlockState(blockpos$mutable);
         Block block = blockstate.getBlock();
         if (block != Blocks.CHAIN_COMMAND_BLOCK) {
            break;
         }

         TileEntity tileentity = p_193386_0_.getTileEntity(blockpos$mutable);
         if (!(tileentity instanceof CommandBlockTileEntity)) {
            break;
         }

         CommandBlockTileEntity commandblocktileentity = (CommandBlockTileEntity)tileentity;
         if (commandblocktileentity.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
            break;
         }

         if (commandblocktileentity.isPowered() || commandblocktileentity.isAuto()) {
            CommandBlockLogic commandblocklogic = commandblocktileentity.getCommandBlockLogic();
            if (commandblocktileentity.setConditionMet()) {
               if (!commandblocklogic.trigger(p_193386_0_)) {
                  break;
               }

               p_193386_0_.updateComparatorOutputLevel(blockpos$mutable, block);
            } else if (commandblocktileentity.isConditional()) {
               commandblocklogic.setSuccessCount(0);
            }
         }
      }

      if (i <= 0) {
         int j = Math.max(gamerules.getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH), 0);
         LOGGER.warn("Command Block chain tried to execute more than {} steps!", (int)j);
      }

   }
}