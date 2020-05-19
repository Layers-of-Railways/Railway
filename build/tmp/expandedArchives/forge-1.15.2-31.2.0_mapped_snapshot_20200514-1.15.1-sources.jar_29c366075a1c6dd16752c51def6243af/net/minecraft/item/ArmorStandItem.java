package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      Direction direction = context.getFace();
      if (direction == Direction.DOWN) {
         return ActionResultType.FAIL;
      } else {
         World world = context.getWorld();
         BlockItemUseContext blockitemusecontext = new BlockItemUseContext(context);
         BlockPos blockpos = blockitemusecontext.getPos();
         BlockPos blockpos1 = blockpos.up();
         if (blockitemusecontext.canPlace() && world.getBlockState(blockpos1).isReplaceable(blockitemusecontext)) {
            double d0 = (double)blockpos.getX();
            double d1 = (double)blockpos.getY();
            double d2 = (double)blockpos.getZ();
            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
            if (!list.isEmpty()) {
               return ActionResultType.FAIL;
            } else {
               ItemStack itemstack = context.getItem();
               if (!world.isRemote) {
                  world.removeBlock(blockpos, false);
                  world.removeBlock(blockpos1, false);
                  ArmorStandEntity armorstandentity = new ArmorStandEntity(world, d0 + 0.5D, d1, d2 + 0.5D);
                  float f = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlacementYaw() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                  armorstandentity.setLocationAndAngles(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                  this.applyRandomRotations(armorstandentity, world.rand);
                  EntityType.applyItemNBT(world, context.getPlayer(), armorstandentity, itemstack.getTag());
                  world.addEntity(armorstandentity);
                  world.playSound((PlayerEntity)null, armorstandentity.getPosX(), armorstandentity.getPosY(), armorstandentity.getPosZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
               }

               itemstack.shrink(1);
               return ActionResultType.SUCCESS;
            }
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   private void applyRandomRotations(ArmorStandEntity armorStand, Random rand) {
      Rotations rotations = armorStand.getHeadRotation();
      float f = rand.nextFloat() * 5.0F;
      float f1 = rand.nextFloat() * 20.0F - 10.0F;
      Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
      armorStand.setHeadRotation(rotations1);
      rotations = armorStand.getBodyRotation();
      f = rand.nextFloat() * 10.0F - 5.0F;
      rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      armorStand.setBodyRotation(rotations1);
   }
}