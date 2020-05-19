package net.minecraft.tileentity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IChestLid.class
)
public class EnderChestTileEntity extends TileEntity implements IChestLid, ITickableTileEntity {
   public float lidAngle;
   /** The angle of the ender chest lid last tick */
   public float prevLidAngle;
   public int numPlayersUsing;
   /**
    * A counter that is incremented once each tick. Used to determine when to determine when to sync with the client;
    * this happens every 80 ticks. However, the number of players is not re-counted.
    */
   private int ticksSinceSync;

   public EnderChestTileEntity() {
      super(TileEntityType.ENDER_CHEST);
   }

   public void tick() {
      if (++this.ticksSinceSync % 20 * 4 == 0) {
         this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.numPlayersUsing);
      }

      this.prevLidAngle = this.lidAngle;
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      float f = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         double d0 = (double)i + 0.5D;
         double d1 = (double)k + 0.5D;
         this.world.playSound((PlayerEntity)null, d0, (double)j + 0.5D, d1, SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f2 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f1 = 0.5F;
         if (this.lidAngle < 0.5F && f2 >= 0.5F) {
            double d3 = (double)i + 0.5D;
            double d2 = (double)k + 0.5D;
            this.world.playSound((PlayerEntity)null, d3, (double)j + 0.5D, d2, SoundEvents.BLOCK_ENDER_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   /**
    * invalidates a tile entity
    */
   public void remove() {
      this.updateContainingBlockInfo();
      super.remove();
   }

   public void openChest() {
      ++this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.numPlayersUsing);
   }

   public void closeChest() {
      --this.numPlayersUsing;
      this.world.addBlockEvent(this.pos, Blocks.ENDER_CHEST, 1, this.numPlayersUsing);
   }

   public boolean canBeUsed(PlayerEntity player) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getLidAngle(float partialTicks) {
      return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
   }
}