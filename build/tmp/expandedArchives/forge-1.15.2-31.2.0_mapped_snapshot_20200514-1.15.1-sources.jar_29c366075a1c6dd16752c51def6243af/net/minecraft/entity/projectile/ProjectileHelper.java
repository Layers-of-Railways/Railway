package net.minecraft.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class ProjectileHelper {
   public static RayTraceResult rayTrace(Entity projectile, boolean checkEntityCollision, boolean includeShooter, @Nullable Entity shooter, RayTraceContext.BlockMode blockModeIn) {
      return rayTrace(projectile, checkEntityCollision, includeShooter, shooter, blockModeIn, true, (p_221270_2_) -> {
         return !p_221270_2_.isSpectator() && p_221270_2_.canBeCollidedWith() && (includeShooter || !p_221270_2_.isEntityEqual(shooter)) && !p_221270_2_.noClip;
      }, projectile.getBoundingBox().expand(projectile.getMotion()).grow(1.0D));
   }

   public static RayTraceResult rayTrace(Entity projectile, AxisAlignedBB boundingBox, Predicate<Entity> filter, RayTraceContext.BlockMode blockModeIn, boolean checkEntityCollision) {
      return rayTrace(projectile, checkEntityCollision, false, (Entity)null, blockModeIn, false, filter, boundingBox);
   }

   /**
    * Gets the EntityRayTraceResult representing the entity hit
    */
   @Nullable
   public static EntityRayTraceResult rayTraceEntities(World worldIn, Entity projectile, Vec3d startVec, Vec3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter) {
      return rayTraceEntities(worldIn, projectile, startVec, endVec, boundingBox, filter, Double.MAX_VALUE);
   }

   private static RayTraceResult rayTrace(Entity projectile, boolean checkEntityCollision, boolean includeShooter, @Nullable Entity shooter, RayTraceContext.BlockMode blockModeIn, boolean p_221268_5_, Predicate<Entity> filter, AxisAlignedBB boundingBox) {
      Vec3d vec3d = projectile.getMotion();
      World world = projectile.world;
      Vec3d vec3d1 = projectile.getPositionVec();
      if (p_221268_5_ && !world.hasNoCollisions(projectile, projectile.getBoundingBox(), (Set<Entity>)(!includeShooter && shooter != null ? getEntityAndMount(shooter) : ImmutableSet.of()))) {
         return new BlockRayTraceResult(vec3d1, Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), new BlockPos(projectile), false);
      } else {
         Vec3d vec3d2 = vec3d1.add(vec3d);
         RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, blockModeIn, RayTraceContext.FluidMode.NONE, projectile));
         if (checkEntityCollision) {
            if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
               vec3d2 = raytraceresult.getHitVec();
            }

            RayTraceResult raytraceresult1 = rayTraceEntities(world, projectile, vec3d1, vec3d2, boundingBox, filter);
            if (raytraceresult1 != null) {
               raytraceresult = raytraceresult1;
            }
         }

         return raytraceresult;
      }
   }

   /**
    * Gets the EntityRayTraceResult representing the entity hit
    */
   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vec3d startVec, Vec3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
      World world = shooter.world;
      double d0 = distance;
      Entity entity = null;
      Vec3d vec3d = null;

      for(Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter)) {
         AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
         Optional<Vec3d> optional = axisalignedbb.rayTrace(startVec, endVec);
         if (axisalignedbb.contains(startVec)) {
            if (d0 >= 0.0D) {
               entity = entity1;
               vec3d = optional.orElse(startVec);
               d0 = 0.0D;
            }
         } else if (optional.isPresent()) {
            Vec3d vec3d1 = optional.get();
            double d1 = startVec.squareDistanceTo(vec3d1);
            if (d1 < d0 || d0 == 0.0D) {
               if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                  if (d0 == 0.0D) {
                     entity = entity1;
                     vec3d = vec3d1;
                  }
               } else {
                  entity = entity1;
                  vec3d = vec3d1;
                  d0 = d1;
               }
            }
         }
      }

      return entity == null ? null : new EntityRayTraceResult(entity, vec3d);
   }

   /**
    * Gets the EntityRayTraceResult representing the entity hit
    */
   @Nullable
   public static EntityRayTraceResult rayTraceEntities(World worldIn, Entity projectile, Vec3d startVec, Vec3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
      double d0 = distance;
      Entity entity = null;

      for(Entity entity1 : worldIn.getEntitiesInAABBexcluding(projectile, boundingBox, filter)) {
         AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)0.3F);
         Optional<Vec3d> optional = axisalignedbb.rayTrace(startVec, endVec);
         if (optional.isPresent()) {
            double d1 = startVec.squareDistanceTo(optional.get());
            if (d1 < d0) {
               entity = entity1;
               d0 = d1;
            }
         }
      }

      return entity == null ? null : new EntityRayTraceResult(entity);
   }

   private static Set<Entity> getEntityAndMount(Entity rider) {
      Entity entity = rider.getRidingEntity();
      return entity != null ? ImmutableSet.of(rider, entity) : ImmutableSet.of(rider);
   }

   public static final void rotateTowardsMovement(Entity projectile, float rotationSpeed) {
      Vec3d vec3d = projectile.getMotion();
      float f = MathHelper.sqrt(Entity.horizontalMag(vec3d));
      projectile.rotationYaw = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * (double)(180F / (float)Math.PI)) + 90.0F;

      for(projectile.rotationPitch = (float)(MathHelper.atan2((double)f, vec3d.y) * (double)(180F / (float)Math.PI)) - 90.0F; projectile.rotationPitch - projectile.prevRotationPitch < -180.0F; projectile.prevRotationPitch -= 360.0F) {
         ;
      }

      while(projectile.rotationPitch - projectile.prevRotationPitch >= 180.0F) {
         projectile.prevRotationPitch += 360.0F;
      }

      while(projectile.rotationYaw - projectile.prevRotationYaw < -180.0F) {
         projectile.prevRotationYaw -= 360.0F;
      }

      while(projectile.rotationYaw - projectile.prevRotationYaw >= 180.0F) {
         projectile.prevRotationYaw += 360.0F;
      }

      projectile.rotationPitch = MathHelper.lerp(rotationSpeed, projectile.prevRotationPitch, projectile.rotationPitch);
      projectile.rotationYaw = MathHelper.lerp(rotationSpeed, projectile.prevRotationYaw, projectile.rotationYaw);
   }

   public static Hand getHandWith(LivingEntity living, Item itemIn) {
      return living.getHeldItemMainhand().getItem() == itemIn ? Hand.MAIN_HAND : Hand.OFF_HAND;
   }

   public static AbstractArrowEntity fireArrow(LivingEntity shooter, ItemStack arrowStack, float distanceFactor) {
      ArrowItem arrowitem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
      AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(shooter.world, arrowStack, shooter);
      abstractarrowentity.setEnchantmentEffectsFromEntity(shooter, distanceFactor);
      if (arrowStack.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity) {
         ((ArrowEntity)abstractarrowentity).setPotionEffect(arrowStack);
      }

      return abstractarrowentity;
   }
}