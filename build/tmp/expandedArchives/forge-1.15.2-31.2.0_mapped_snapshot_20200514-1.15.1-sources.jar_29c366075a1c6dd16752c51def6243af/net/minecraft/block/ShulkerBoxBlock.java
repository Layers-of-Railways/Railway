package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBoxBlock extends ContainerBlock {
   public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
   public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlock(@Nullable DyeColor color, Block.Properties properties) {
      super(properties);
      this.color = color;
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new ShulkerBoxTileEntity(this.color);
   }

   public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return true;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else if (player.isSpectator()) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            Direction direction = state.get(FACING);
            ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
            boolean flag;
            if (shulkerboxtileentity.getAnimationStatus() == ShulkerBoxTileEntity.AnimationStatus.CLOSED) {
               AxisAlignedBB axisalignedbb = VoxelShapes.fullCube().getBoundingBox().expand((double)(0.5F * (float)direction.getXOffset()), (double)(0.5F * (float)direction.getYOffset()), (double)(0.5F * (float)direction.getZOffset())).contract((double)direction.getXOffset(), (double)direction.getYOffset(), (double)direction.getZOffset());
               flag = worldIn.hasNoCollisions(axisalignedbb.offset(pos.offset(direction)));
            } else {
               flag = true;
            }

            if (flag) {
               player.openContainer(shulkerboxtileentity);
               player.addStat(Stats.OPEN_SHULKER_BOX);
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getFace());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof ShulkerBoxTileEntity) {
         ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
         if (!worldIn.isRemote && player.isCreative() && !shulkerboxtileentity.isEmpty()) {
            ItemStack itemstack = getColoredItemStack(this.getColor());
            CompoundNBT compoundnbt = shulkerboxtileentity.saveToNbt(new CompoundNBT());
            if (!compoundnbt.isEmpty()) {
               itemstack.setTagInfo("BlockEntityTag", compoundnbt);
            }

            if (shulkerboxtileentity.hasCustomName()) {
               itemstack.setDisplayName(shulkerboxtileentity.getCustomName());
            }

            ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), itemstack);
            itementity.setDefaultPickupDelay();
            worldIn.addEntity(itementity);
         } else {
            shulkerboxtileentity.fillWithLoot(player);
         }
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
      TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
      if (tileentity instanceof ShulkerBoxTileEntity) {
         ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
         builder = builder.withDynamicDrop(CONTENTS, (p_220168_1_, p_220168_2_) -> {
            for(int i = 0; i < shulkerboxtileentity.getSizeInventory(); ++i) {
               p_220168_2_.accept(shulkerboxtileentity.getStackInSlot(i));
            }

         });
      }

      return super.getDrops(state, builder);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            ((ShulkerBoxTileEntity)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof ShulkerBoxTileEntity) {
            worldIn.updateComparatorOutputLevel(pos, state.getBlock());
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
      if (compoundnbt != null) {
         if (compoundnbt.contains("LootTable", 8)) {
            tooltip.add(new StringTextComponent("???????"));
         }

         if (compoundnbt.contains("Items", 9)) {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
            ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
            int i = 0;
            int j = 0;

            for(ItemStack itemstack : nonnulllist) {
               if (!itemstack.isEmpty()) {
                  ++j;
                  if (i <= 4) {
                     ++i;
                     ITextComponent itextcomponent = itemstack.getDisplayName().deepCopy();
                     itextcomponent.appendText(" x").appendText(String.valueOf(itemstack.getCount()));
                     tooltip.add(itextcomponent);
                  }
               }
            }

            if (j - i > 0) {
               tooltip.add((new TranslationTextComponent("container.shulkerBox.more", j - i)).applyTextStyle(TextFormatting.ITALIC));
            }
         }
      }

   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public PushReaction getPushReaction(BlockState state) {
      return PushReaction.DESTROY;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof ShulkerBoxTileEntity ? VoxelShapes.create(((ShulkerBoxTileEntity)tileentity).getBoundingBox(state)) : VoxelShapes.fullCube();
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
      return Container.calcRedstoneFromInventory((IInventory)worldIn.getTileEntity(pos));
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      ItemStack itemstack = super.getItem(worldIn, pos, state);
      ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)worldIn.getTileEntity(pos);
      CompoundNBT compoundnbt = shulkerboxtileentity.saveToNbt(new CompoundNBT());
      if (!compoundnbt.isEmpty()) {
         itemstack.setTagInfo("BlockEntityTag", compoundnbt);
      }

      return itemstack;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor getColorFromItem(Item itemIn) {
      return getColorFromBlock(Block.getBlockFromItem(itemIn));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static DyeColor getColorFromBlock(Block blockIn) {
      return blockIn instanceof ShulkerBoxBlock ? ((ShulkerBoxBlock)blockIn).getColor() : null;
   }

   public static Block getBlockByColor(@Nullable DyeColor colorIn) {
      if (colorIn == null) {
         return Blocks.SHULKER_BOX;
      } else {
         switch(colorIn) {
         case WHITE:
            return Blocks.WHITE_SHULKER_BOX;
         case ORANGE:
            return Blocks.ORANGE_SHULKER_BOX;
         case MAGENTA:
            return Blocks.MAGENTA_SHULKER_BOX;
         case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;
         case YELLOW:
            return Blocks.YELLOW_SHULKER_BOX;
         case LIME:
            return Blocks.LIME_SHULKER_BOX;
         case PINK:
            return Blocks.PINK_SHULKER_BOX;
         case GRAY:
            return Blocks.GRAY_SHULKER_BOX;
         case LIGHT_GRAY:
            return Blocks.LIGHT_GRAY_SHULKER_BOX;
         case CYAN:
            return Blocks.CYAN_SHULKER_BOX;
         case PURPLE:
         default:
            return Blocks.PURPLE_SHULKER_BOX;
         case BLUE:
            return Blocks.BLUE_SHULKER_BOX;
         case BROWN:
            return Blocks.BROWN_SHULKER_BOX;
         case GREEN:
            return Blocks.GREEN_SHULKER_BOX;
         case RED:
            return Blocks.RED_SHULKER_BOX;
         case BLACK:
            return Blocks.BLACK_SHULKER_BOX;
         }
      }
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   public static ItemStack getColoredItemStack(@Nullable DyeColor colorIn) {
      return new ItemStack(getBlockByColor(colorIn));
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
}