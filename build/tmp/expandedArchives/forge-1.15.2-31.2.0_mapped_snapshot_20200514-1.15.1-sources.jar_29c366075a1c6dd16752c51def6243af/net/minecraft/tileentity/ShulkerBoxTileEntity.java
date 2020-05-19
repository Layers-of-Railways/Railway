package net.minecraft.tileentity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ShulkerBoxContainer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShulkerBoxTileEntity extends LockableLootTileEntity implements ISidedInventory, ITickableTileEntity {
   private static final int[] SLOTS = IntStream.range(0, 27).toArray();
   private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
   private int openCount;
   private ShulkerBoxTileEntity.AnimationStatus animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
   private float progress;
   private float progressOld;
   @Nullable
   private DyeColor color;
   private boolean needsColorFromWorld;

   public ShulkerBoxTileEntity(@Nullable DyeColor colorIn) {
      super(TileEntityType.SHULKER_BOX);
      this.color = colorIn;
   }

   public ShulkerBoxTileEntity() {
      this((DyeColor)null);
      this.needsColorFromWorld = true;
   }

   public void tick() {
      this.updateAnimation();
      if (this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.OPENING || this.animationStatus == ShulkerBoxTileEntity.AnimationStatus.CLOSING) {
         this.moveCollidedEntities();
      }

   }

   protected void updateAnimation() {
      this.progressOld = this.progress;
      switch(this.animationStatus) {
      case CLOSED:
         this.progress = 0.0F;
         break;
      case OPENING:
         this.progress += 0.1F;
         if (this.progress >= 1.0F) {
            this.moveCollidedEntities();
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENED;
            this.progress = 1.0F;
            this.func_213975_v();
         }
         break;
      case CLOSING:
         this.progress -= 0.1F;
         if (this.progress <= 0.0F) {
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSED;
            this.progress = 0.0F;
            this.func_213975_v();
         }
         break;
      case OPENED:
         this.progress = 1.0F;
      }

   }

   public ShulkerBoxTileEntity.AnimationStatus getAnimationStatus() {
      return this.animationStatus;
   }

   public AxisAlignedBB getBoundingBox(BlockState p_190584_1_) {
      return this.getBoundingBox(p_190584_1_.get(ShulkerBoxBlock.FACING));
   }

   public AxisAlignedBB getBoundingBox(Direction p_190587_1_) {
      float f = this.getProgress(1.0F);
      return VoxelShapes.fullCube().getBoundingBox().expand((double)(0.5F * f * (float)p_190587_1_.getXOffset()), (double)(0.5F * f * (float)p_190587_1_.getYOffset()), (double)(0.5F * f * (float)p_190587_1_.getZOffset()));
   }

   private AxisAlignedBB getTopBoundingBox(Direction p_190588_1_) {
      Direction direction = p_190588_1_.getOpposite();
      return this.getBoundingBox(p_190588_1_).contract((double)direction.getXOffset(), (double)direction.getYOffset(), (double)direction.getZOffset());
   }

   private void moveCollidedEntities() {
      BlockState blockstate = this.world.getBlockState(this.getPos());
      if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
         Direction direction = blockstate.get(ShulkerBoxBlock.FACING);
         AxisAlignedBB axisalignedbb = this.getTopBoundingBox(direction).offset(this.pos);
         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
         if (!list.isEmpty()) {
            for(int i = 0; i < list.size(); ++i) {
               Entity entity = list.get(i);
               if (entity.getPushReaction() != PushReaction.IGNORE) {
                  double d0 = 0.0D;
                  double d1 = 0.0D;
                  double d2 = 0.0D;
                  AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();
                  switch(direction.getAxis()) {
                  case X:
                     if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                     } else {
                        d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                     }

                     d0 = d0 + 0.01D;
                     break;
                  case Y:
                     if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                     } else {
                        d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                     }

                     d1 = d1 + 0.01D;
                     break;
                  case Z:
                     if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                     } else {
                        d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                     }

                     d2 = d2 + 0.01D;
                  }

                  entity.move(MoverType.SHULKER_BOX, new Vec3d(d0 * (double)direction.getXOffset(), d1 * (double)direction.getYOffset(), d2 * (double)direction.getZOffset()));
               }
            }

         }
      }
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.items.size();
   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.openCount = type;
         if (type == 0) {
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.CLOSING;
            this.func_213975_v();
         }

         if (type == 1) {
            this.animationStatus = ShulkerBoxTileEntity.AnimationStatus.OPENING;
            this.func_213975_v();
         }

         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   private void func_213975_v() {
      this.getBlockState().updateNeighbors(this.getWorld(), this.getPos(), 3);
   }

   public void openInventory(PlayerEntity player) {
      if (!player.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount == 1) {
            this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void closeInventory(PlayerEntity player) {
      if (!player.isSpectator()) {
         --this.openCount;
         this.world.addBlockEvent(this.pos, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount <= 0) {
            this.world.playSound((PlayerEntity)null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.shulkerBox");
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.loadFromNbt(compound);
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      return this.saveToNbt(compound);
   }

   public void loadFromNbt(CompoundNBT compound) {
      this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound) && compound.contains("Items", 9)) {
         ItemStackHelper.loadAllItems(compound, this.items);
      }

   }

   public CompoundNBT saveToNbt(CompoundNBT compound) {
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.items, false);
      }

      return compound;
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.items = itemsIn;
   }

   public int[] getSlotsForFace(Direction side) {
      return SLOTS;
   }

   /**
    * Returns true if automation can insert the given item in the given slot from the given side.
    */
   public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return !(Block.getBlockFromItem(itemStackIn.getItem()) instanceof ShulkerBoxBlock);
   }

   /**
    * Returns true if automation can extract the given item in the given slot from the given side.
    */
   public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
      return true;
   }

   public float getProgress(float p_190585_1_) {
      return MathHelper.lerp(p_190585_1_, this.progressOld, this.progress);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public DyeColor getColor() {
      if (this.needsColorFromWorld) {
         this.color = ShulkerBoxBlock.getColorFromBlock(this.getBlockState().getBlock());
         this.needsColorFromWorld = false;
      }

      return this.color;
   }

   protected Container createMenu(int id, PlayerInventory player) {
      return new ShulkerBoxContainer(id, player, this);
   }

   @Override
   protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
      return new net.minecraftforge.items.wrapper.SidedInvWrapper(this, Direction.UP);
   }

   public static enum AnimationStatus {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;
   }
}