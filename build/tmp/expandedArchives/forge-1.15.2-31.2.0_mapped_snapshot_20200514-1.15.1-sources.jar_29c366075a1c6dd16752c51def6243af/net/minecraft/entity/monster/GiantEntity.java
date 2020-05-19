package net.minecraft.entity.monster;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GiantEntity extends MonsterEntity {
   public GiantEntity(EntityType<? extends GiantEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return 10.440001F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(50.0D);
   }

   public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
      return worldIn.getBrightness(pos) - 0.5F;
   }
}