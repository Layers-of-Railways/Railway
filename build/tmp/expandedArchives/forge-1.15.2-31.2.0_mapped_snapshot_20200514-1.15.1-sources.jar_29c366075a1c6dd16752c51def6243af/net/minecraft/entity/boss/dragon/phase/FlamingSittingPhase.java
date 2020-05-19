package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FlamingSittingPhase extends SittingPhase {
   private int flameTicks;
   private int flameCount;
   private AreaEffectCloudEntity areaEffectCloud;

   public FlamingSittingPhase(EnderDragonEntity dragonIn) {
      super(dragonIn);
   }

   /**
    * Generates particle effects appropriate to the phase (or sometimes sounds).
    * Called by dragon's onLivingUpdate. Only used when worldObj.isRemote.
    */
   public void clientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vec3d vec3d = this.dragon.getHeadLookVec(1.0F).normalize();
         vec3d.rotateYaw((-(float)Math.PI / 4F));
         double d0 = this.dragon.dragonPartHead.getPosX();
         double d1 = this.dragon.dragonPartHead.getPosYHeight(0.5D);
         double d2 = this.dragon.dragonPartHead.getPosZ();

         for(int i = 0; i < 8; ++i) {
            double d3 = d0 + this.dragon.getRNG().nextGaussian() / 2.0D;
            double d4 = d1 + this.dragon.getRNG().nextGaussian() / 2.0D;
            double d5 = d2 + this.dragon.getRNG().nextGaussian() / 2.0D;

            for(int j = 0; j < 6; ++j) {
               this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, d3, d4, d5, -vec3d.x * (double)0.08F * (double)j, -vec3d.y * (double)0.6F, -vec3d.z * (double)0.08F * (double)j);
            }

            vec3d.rotateYaw(0.19634955F);
         }
      }

   }

   /**
    * Gives the phase a chance to update its status.
    * Called by dragon's onLivingUpdate. Only used when !worldObj.isRemote.
    */
   public void serverTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vec3d vec3d = (new Vec3d(this.dragon.dragonPartHead.getPosX() - this.dragon.getPosX(), 0.0D, this.dragon.dragonPartHead.getPosZ() - this.dragon.getPosZ())).normalize();
         float f = 5.0F;
         double d0 = this.dragon.dragonPartHead.getPosX() + vec3d.x * 5.0D / 2.0D;
         double d1 = this.dragon.dragonPartHead.getPosZ() + vec3d.z * 5.0D / 2.0D;
         double d2 = this.dragon.dragonPartHead.getPosYHeight(0.5D);
         double d3 = d2;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(d0, d2, d1);

         while(this.dragon.world.isAirBlock(blockpos$mutable)) {
            --d3;
            if (d3 < 0.0D) {
               d3 = d2;
               break;
            }

            blockpos$mutable.setPos(d0, d3, d1);
         }

         d3 = (double)(MathHelper.floor(d3) + 1);
         this.areaEffectCloud = new AreaEffectCloudEntity(this.dragon.world, d0, d3, d1);
         this.areaEffectCloud.setOwner(this.dragon);
         this.areaEffectCloud.setRadius(5.0F);
         this.areaEffectCloud.setDuration(200);
         this.areaEffectCloud.setParticleData(ParticleTypes.DRAGON_BREATH);
         this.areaEffectCloud.addEffect(new EffectInstance(Effects.INSTANT_DAMAGE));
         this.dragon.world.addEntity(this.areaEffectCloud);
      }

   }

   /**
    * Called when this phase is set to active
    */
   public void initPhase() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public void removeAreaEffect() {
      if (this.areaEffectCloud != null) {
         this.areaEffectCloud.remove();
         this.areaEffectCloud = null;
      }

   }

   public PhaseType<FlamingSittingPhase> getType() {
      return PhaseType.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}