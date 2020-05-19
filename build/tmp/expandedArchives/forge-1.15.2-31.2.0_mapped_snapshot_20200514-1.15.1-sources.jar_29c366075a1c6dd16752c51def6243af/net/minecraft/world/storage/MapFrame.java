package net.minecraft.world.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class MapFrame {
   private final BlockPos pos;
   private final int rotation;
   private final int entityId;

   public MapFrame(BlockPos p_i49855_1_, int p_i49855_2_, int p_i49855_3_) {
      this.pos = p_i49855_1_;
      this.rotation = p_i49855_2_;
      this.entityId = p_i49855_3_;
   }

   public static MapFrame read(CompoundNBT p_212765_0_) {
      BlockPos blockpos = NBTUtil.readBlockPos(p_212765_0_.getCompound("Pos"));
      int i = p_212765_0_.getInt("Rotation");
      int j = p_212765_0_.getInt("EntityId");
      return new MapFrame(blockpos, i, j);
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("Pos", NBTUtil.writeBlockPos(this.pos));
      compoundnbt.putInt("Rotation", this.rotation);
      compoundnbt.putInt("EntityId", this.entityId);
      return compoundnbt;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getRotation() {
      return this.rotation;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public String func_212767_e() {
      return func_212766_a(this.pos);
   }

   public static String func_212766_a(BlockPos p_212766_0_) {
      return "frame-" + p_212766_0_.getX() + "," + p_212766_0_.getY() + "," + p_212766_0_.getZ();
   }
}