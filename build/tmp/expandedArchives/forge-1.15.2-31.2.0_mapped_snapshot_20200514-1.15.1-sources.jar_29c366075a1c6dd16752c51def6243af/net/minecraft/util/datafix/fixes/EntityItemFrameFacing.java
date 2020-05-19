package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.TypeReferences;

public class EntityItemFrameFacing extends NamedEntityFix {
   public EntityItemFrameFacing(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "EntityItemFrameDirectionFix", TypeReferences.ENTITY, "minecraft:item_frame");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209651_1_) {
      return p_209651_1_.set("Facing", p_209651_1_.createByte(direction2dTo3d(p_209651_1_.get("Facing").asByte((byte)0))));
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }

   private static byte direction2dTo3d(byte p_210567_0_) {
      switch(p_210567_0_) {
      case 0:
         return 3;
      case 1:
         return 4;
      case 2:
      default:
         return 2;
      case 3:
         return 5;
      }
   }
}