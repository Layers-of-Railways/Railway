package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JigsawTileEntity extends TileEntity {
   private ResourceLocation attachmentType = new ResourceLocation("empty");
   private ResourceLocation targetPool = new ResourceLocation("empty");
   private String finalState = "minecraft:air";

   public JigsawTileEntity(TileEntityType<?> p_i49960_1_) {
      super(p_i49960_1_);
   }

   public JigsawTileEntity() {
      this(TileEntityType.JIGSAW);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getAttachmentType() {
      return this.attachmentType;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTargetPool() {
      return this.targetPool;
   }

   @OnlyIn(Dist.CLIENT)
   public String getFinalState() {
      return this.finalState;
   }

   public void setAttachmentType(ResourceLocation p_214057_1_) {
      this.attachmentType = p_214057_1_;
   }

   public void setTargetPool(ResourceLocation p_214058_1_) {
      this.targetPool = p_214058_1_;
   }

   public void setFinalState(String p_214055_1_) {
      this.finalState = p_214055_1_;
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      compound.putString("attachement_type", this.attachmentType.toString());
      compound.putString("target_pool", this.targetPool.toString());
      compound.putString("final_state", this.finalState);
      return compound;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.attachmentType = new ResourceLocation(compound.getString("attachement_type"));
      this.targetPool = new ResourceLocation(compound.getString("target_pool"));
      this.finalState = compound.getString("final_state");
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 12, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }
}