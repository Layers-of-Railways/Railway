package net.minecraft.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderCrystalEntity extends Entity {
   private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.createKey(EnderCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
   private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.createKey(EnderCrystalEntity.class, DataSerializers.BOOLEAN);
   public int innerRotation;

   public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> p_i50231_1_, World p_i50231_2_) {
      super(p_i50231_1_, p_i50231_2_);
      this.preventEntitySpawning = true;
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EnderCrystalEntity(World worldIn, double x, double y, double z) {
      this(EntityType.END_CRYSTAL, worldIn);
      this.setPosition(x, y, z);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(BEAM_TARGET, Optional.empty());
      this.getDataManager().register(SHOW_BOTTOM, true);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      ++this.innerRotation;
      if (!this.world.isRemote) {
         BlockPos blockpos = new BlockPos(this);
         if (this.world.dimension instanceof EndDimension && this.world.getBlockState(blockpos).isAir()) {
            this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
         }
      }

   }

   protected void writeAdditional(CompoundNBT compound) {
      if (this.getBeamTarget() != null) {
         compound.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
      }

      compound.putBoolean("ShowBottom", this.shouldShowBottom());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      if (compound.contains("BeamTarget", 10)) {
         this.setBeamTarget(NBTUtil.readBlockPos(compound.getCompound("BeamTarget")));
      }

      if (compound.contains("ShowBottom", 1)) {
         this.setShowBottom(compound.getBoolean("ShowBottom"));
      }

   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return true;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (source.getTrueSource() instanceof EnderDragonEntity) {
         return false;
      } else {
         if (!this.removed && !this.world.isRemote) {
            this.remove();
            if (!source.isExplosion()) {
               this.world.createExplosion((Entity)null, this.getPosX(), this.getPosY(), this.getPosZ(), 6.0F, Explosion.Mode.DESTROY);
            }

            this.onCrystalDestroyed(source);
         }

         return true;
      }
   }

   /**
    * Called by the /kill command.
    */
   public void onKillCommand() {
      this.onCrystalDestroyed(DamageSource.GENERIC);
      super.onKillCommand();
   }

   private void onCrystalDestroyed(DamageSource source) {
      if (this.world.dimension instanceof EndDimension) {
         EndDimension enddimension = (EndDimension)this.world.dimension;
         DragonFightManager dragonfightmanager = enddimension.getDragonFightManager();
         if (dragonfightmanager != null) {
            dragonfightmanager.onCrystalDestroyed(this, source);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getDataManager().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return this.getDataManager().get(BEAM_TARGET).orElse((BlockPos)null);
   }

   public void setShowBottom(boolean showBottom) {
      this.getDataManager().set(SHOW_BOTTOM, showBottom);
   }

   public boolean shouldShowBottom() {
      return this.getDataManager().get(SHOW_BOTTOM);
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return super.isInRangeToRenderDist(distance) || this.getBeamTarget() != null;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}