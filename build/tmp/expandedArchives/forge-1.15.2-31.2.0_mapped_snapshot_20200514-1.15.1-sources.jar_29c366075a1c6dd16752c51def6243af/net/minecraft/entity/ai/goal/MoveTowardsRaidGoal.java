package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsRaidGoal<T extends AbstractRaiderEntity> extends Goal {
   private final T field_220744_a;

   public MoveTowardsRaidGoal(T p_i50323_1_) {
      this.field_220744_a = p_i50323_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.field_220744_a.getAttackTarget() == null && !this.field_220744_a.isBeingRidden() && this.field_220744_a.isRaidActive() && !this.field_220744_a.getRaid().isOver() && !((ServerWorld)this.field_220744_a.world).isVillage(new BlockPos(this.field_220744_a));
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.field_220744_a.isRaidActive() && !this.field_220744_a.getRaid().isOver() && this.field_220744_a.world instanceof ServerWorld && !((ServerWorld)this.field_220744_a.world).isVillage(new BlockPos(this.field_220744_a));
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.field_220744_a.isRaidActive()) {
         Raid raid = this.field_220744_a.getRaid();
         if (this.field_220744_a.ticksExisted % 20 == 0) {
            this.func_220743_a(raid);
         }

         if (!this.field_220744_a.hasPath()) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.field_220744_a, 15, 4, new Vec3d(raid.getCenter()));
            if (vec3d != null) {
               this.field_220744_a.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            }
         }
      }

   }

   private void func_220743_a(Raid p_220743_1_) {
      if (p_220743_1_.isActive()) {
         Set<AbstractRaiderEntity> set = Sets.newHashSet();
         List<AbstractRaiderEntity> list = this.field_220744_a.world.getEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220744_a.getBoundingBox().grow(16.0D), (p_220742_1_) -> {
            return !p_220742_1_.isRaidActive() && RaidManager.canJoinRaid(p_220742_1_, p_220743_1_);
         });
         set.addAll(list);

         for(AbstractRaiderEntity abstractraiderentity : set) {
            p_220743_1_.joinRaid(p_220743_1_.getGroupsSpawned(), abstractraiderentity, (BlockPos)null, true);
         }
      }

   }
}