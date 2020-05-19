package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;

public class MagmaCubeEntity extends SlimeEntity {
   public MagmaCubeEntity(EntityType<? extends MagmaCubeEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
   }

   public static boolean func_223367_b(EntityType<MagmaCubeEntity> p_223367_0_, IWorld p_223367_1_, SpawnReason p_223367_2_, BlockPos p_223367_3_, Random p_223367_4_) {
      return p_223367_1_.getDifficulty() != Difficulty.PEACEFUL;
   }

   public boolean isNotColliding(IWorldReader worldIn) {
      return worldIn.checkNoEntityCollision(this) && !worldIn.containsAnyLiquid(this.getBoundingBox());
   }

   protected void setSlimeSize(int size, boolean resetHealth) {
      super.setSlimeSize(size, resetHealth);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue((double)(size * 3));
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   protected IParticleData getSquishParticle() {
      return ParticleTypes.FLAME;
   }

   protected ResourceLocation getLootTable() {
      return this.isSmallSlime() ? LootTables.EMPTY : this.getType().getLootTable();
   }

   /**
    * Returns true if the entity is on fire. Used by render to add the fire effect on rendering.
    */
   public boolean isBurning() {
      return false;
   }

   /**
    * Gets the amount of time the slime needs to wait between jumps.
    */
   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.9F;
   }

   /**
    * Causes this entity to do an upwards motion (jumping).
    */
   protected void jump() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x, (double)(this.getJumpUpwardsMotion() + (float)this.getSlimeSize() * 0.1F), vec3d.z);
      this.isAirBorne = true;
      net.minecraftforge.common.ForgeHooks.onLivingJump(this);
   }

   protected void handleFluidJump(Tag<Fluid> fluidTag) {
      if (fluidTag == FluidTags.LAVA) {
         Vec3d vec3d = this.getMotion();
         this.setMotion(vec3d.x, (double)(0.22F + (float)this.getSlimeSize() * 0.05F), vec3d.z);
         this.isAirBorne = true;
      } else {
         super.handleFluidJump(fluidTag);
      }

   }

   public boolean onLivingFall(float distance, float damageMultiplier) {
      return false;
   }

   /**
    * Indicates weather the slime is able to damage the player (based upon the slime's size)
    */
   protected boolean canDamagePlayer() {
      return this.isServerWorld();
   }

   protected float func_225512_er_() {
      return super.func_225512_er_() + 2.0F;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
   }
}