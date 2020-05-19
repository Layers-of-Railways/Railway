package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class FurnaceMinecartEntity extends AbstractMinecartEntity {
   private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(FurnaceMinecartEntity.class, DataSerializers.BOOLEAN);
   private int fuel;
   public double pushX;
   public double pushZ;
   private static final Ingredient field_195407_e = Ingredient.fromItems(Items.COAL, Items.CHARCOAL);

   public FurnaceMinecartEntity(EntityType<? extends FurnaceMinecartEntity> p_i50119_1_, World p_i50119_2_) {
      super(p_i50119_1_, p_i50119_2_);
   }

   public FurnaceMinecartEntity(World worldIn, double x, double y, double z) {
      super(EntityType.FURNACE_MINECART, worldIn, x, y, z);
   }

   public AbstractMinecartEntity.Type getMinecartType() {
      return AbstractMinecartEntity.Type.FURNACE;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(POWERED, false);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote()) {
         if (this.fuel > 0) {
            --this.fuel;
         }

         if (this.fuel <= 0) {
            this.pushX = 0.0D;
            this.pushZ = 0.0D;
         }

         this.setMinecartPowered(this.fuel > 0);
      }

      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.world.addParticle(ParticleTypes.LARGE_SMOKE, this.getPosX(), this.getPosY() + 0.8D, this.getPosZ(), 0.0D, 0.0D, 0.0D);
      }

   }

   /**
    * Get's the maximum speed for a minecart
    */
   protected double getMaximumSpeed() {
      return 0.2D;
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.entityDropItem(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos pos, BlockState state) {
      double d0 = 1.0E-4D;
      double d1 = 0.001D;
      super.moveAlongTrack(pos, state);
      Vec3d vec3d = this.getMotion();
      double d2 = horizontalMag(vec3d);
      double d3 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d3 > 1.0E-4D && d2 > 0.001D) {
         double d4 = (double)MathHelper.sqrt(d2);
         double d5 = (double)MathHelper.sqrt(d3);
         this.pushX = vec3d.x / d4 * d5;
         this.pushZ = vec3d.z / d4 * d5;
      }

   }

   protected void applyDrag() {
      double d0 = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d0 > 1.0E-7D) {
         d0 = (double)MathHelper.sqrt(d0);
         this.pushX /= d0;
         this.pushZ /= d0;
         this.setMotion(this.getMotion().mul(0.8D, 0.0D, 0.8D).add(this.pushX, 0.0D, this.pushZ));
      } else {
         this.setMotion(this.getMotion().mul(0.98D, 0.0D, 0.98D));
      }

      super.applyDrag();
   }

   public boolean processInitialInteract(PlayerEntity player, Hand hand) {
      if (super.processInitialInteract(player, hand)) return true;
      ItemStack itemstack = player.getHeldItem(hand);
      if (field_195407_e.test(itemstack) && this.fuel + 3600 <= 32000) {
         if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         this.fuel += 3600;
      }

      if (this.fuel > 0) {
         this.pushX = this.getPosX() - player.getPosX();
         this.pushZ = this.getPosZ() - player.getPosZ();
      }

      return true;
   }

   protected void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putDouble("PushX", this.pushX);
      compound.putDouble("PushZ", this.pushZ);
      compound.putShort("Fuel", (short)this.fuel);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.pushX = compound.getDouble("PushX");
      this.pushZ = compound.getDouble("PushZ");
      this.fuel = compound.getShort("Fuel");
   }

   protected boolean isMinecartPowered() {
      return this.dataManager.get(POWERED);
   }

   protected void setMinecartPowered(boolean p_94107_1_) {
      this.dataManager.set(POWERED, p_94107_1_);
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.FURNACE.getDefaultState().with(FurnaceBlock.FACING, Direction.NORTH).with(FurnaceBlock.LIT, Boolean.valueOf(this.isMinecartPowered()));
   }
}