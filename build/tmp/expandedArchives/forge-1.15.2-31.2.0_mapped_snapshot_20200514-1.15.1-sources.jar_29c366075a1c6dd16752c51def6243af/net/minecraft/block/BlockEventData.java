package net.minecraft.block;

import net.minecraft.util.math.BlockPos;

public class BlockEventData {
   private final BlockPos position;
   private final Block blockType;
   /** Different for each blockID */
   private final int eventID;
   private final int eventParameter;

   public BlockEventData(BlockPos pos, Block blockType, int eventId, int eventParameterIn) {
      this.position = pos;
      this.blockType = blockType;
      this.eventID = eventId;
      this.eventParameter = eventParameterIn;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public Block getBlock() {
      return this.blockType;
   }

   /**
    * Get the Event ID (different for each BlockID)
    */
   public int getEventID() {
      return this.eventID;
   }

   public int getEventParameter() {
      return this.eventParameter;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData blockeventdata = (BlockEventData)p_equals_1_;
         return this.position.equals(blockeventdata.position) && this.eventID == blockeventdata.eventID && this.eventParameter == blockeventdata.eventParameter && this.blockType == blockeventdata.blockType;
      }
   }

   public int hashCode() {
      int i = this.position.hashCode();
      i = 31 * i + this.blockType.hashCode();
      i = 31 * i + this.eventID;
      i = 31 * i + this.eventParameter;
      return i;
   }

   public String toString() {
      return "TE(" + this.position + ")," + this.eventID + "," + this.eventParameter + "," + this.blockType;
   }
}