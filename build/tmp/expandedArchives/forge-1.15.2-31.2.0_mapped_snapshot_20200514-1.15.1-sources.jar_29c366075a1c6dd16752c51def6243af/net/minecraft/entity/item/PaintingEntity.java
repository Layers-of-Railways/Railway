package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaintingEntity extends HangingEntity {
   public PaintingType art;

   public PaintingEntity(EntityType<? extends PaintingEntity> type, World worldIn) {
      super(type, worldIn);
   }

   public PaintingEntity(World worldIn, BlockPos pos, Direction facing) {
      super(EntityType.PAINTING, worldIn, pos);
      List<PaintingType> list = Lists.newArrayList();
      int i = 0;

      for(PaintingType paintingtype : Registry.MOTIVE) {
         this.art = paintingtype;
         this.updateFacingWithBoundingBox(facing);
         if (this.onValidSurface()) {
            list.add(paintingtype);
            int j = paintingtype.getWidth() * paintingtype.getHeight();
            if (j > i) {
               i = j;
            }
         }
      }

      if (!list.isEmpty()) {
         Iterator<PaintingType> iterator = list.iterator();

         while(iterator.hasNext()) {
            PaintingType paintingtype1 = iterator.next();
            if (paintingtype1.getWidth() * paintingtype1.getHeight() < i) {
               iterator.remove();
            }
         }

         this.art = list.get(this.rand.nextInt(list.size()));
      }

      this.updateFacingWithBoundingBox(facing);
   }

   @OnlyIn(Dist.CLIENT)
   public PaintingEntity(World worldIn, BlockPos pos, Direction facing, PaintingType artIn) {
      this(worldIn, pos, facing);
      this.art = artIn;
      this.updateFacingWithBoundingBox(facing);
   }

   public void writeAdditional(CompoundNBT compound) {
      compound.putString("Motive", Registry.MOTIVE.getKey(this.art).toString());
      super.writeAdditional(compound);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      this.art = Registry.MOTIVE.getOrDefault(ResourceLocation.tryCreate(compound.getString("Motive")));
      super.readAdditional(compound);
   }

   public int getWidthPixels() {
      return this.art == null ? 1 : this.art.getWidth();
   }

   public int getHeightPixels() {
      return this.art == null ? 1 : this.art.getHeight();
   }

   /**
    * Called when this entity is broken. Entity parameter may be null.
    */
   public void onBroken(@Nullable Entity brokenEntity) {
      if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
         this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);
         if (brokenEntity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)brokenEntity;
            if (playerentity.abilities.isCreativeMode) {
               return;
            }
         }

         this.entityDropItem(Items.PAINTING);
      }
   }

   public void playPlaceSound() {
      this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
   }

   /**
    * Sets the location and Yaw/Pitch of an entity in the world
    */
   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.setPosition(x, y, z);
   }

   /**
    * Sets a target for the client to interpolate towards over the next few ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      BlockPos blockpos = this.hangingPosition.add(x - this.getPosX(), y - this.getPosY(), z - this.getPosZ());
      this.setPosition((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnPaintingPacket(this);
   }
}