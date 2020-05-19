package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BoatItem extends Item {
   private static final Predicate<Entity> field_219989_a = EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith);
   private final BoatEntity.Type type;

   public BoatItem(BoatEntity.Type typeIn, Item.Properties properties) {
      super(properties);
      this.type = typeIn;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
      if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return ActionResult.resultPass(itemstack);
      } else {
         Vec3d vec3d = playerIn.getLook(1.0F);
         double d0 = 5.0D;
         List<Entity> list = worldIn.getEntitiesInAABBexcluding(playerIn, playerIn.getBoundingBox().expand(vec3d.scale(5.0D)).grow(1.0D), field_219989_a);
         if (!list.isEmpty()) {
            Vec3d vec3d1 = playerIn.getEyePosition(1.0F);

            for(Entity entity : list) {
               AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow((double)entity.getCollisionBorderSize());
               if (axisalignedbb.contains(vec3d1)) {
                  return ActionResult.resultPass(itemstack);
               }
            }
         }

         if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            BoatEntity boatentity = new BoatEntity(worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
            boatentity.setBoatType(this.type);
            boatentity.rotationYaw = playerIn.rotationYaw;
            if (!worldIn.hasNoCollisions(boatentity, boatentity.getBoundingBox().grow(-0.1D))) {
               return ActionResult.resultFail(itemstack);
            } else {
               if (!worldIn.isRemote) {
                  worldIn.addEntity(boatentity);
                  if (!playerIn.abilities.isCreativeMode) {
                     itemstack.shrink(1);
                  }
               }

               playerIn.addStat(Stats.ITEM_USED.get(this));
               return ActionResult.resultSuccess(itemstack);
            }
         } else {
            return ActionResult.resultPass(itemstack);
         }
      }
   }
}