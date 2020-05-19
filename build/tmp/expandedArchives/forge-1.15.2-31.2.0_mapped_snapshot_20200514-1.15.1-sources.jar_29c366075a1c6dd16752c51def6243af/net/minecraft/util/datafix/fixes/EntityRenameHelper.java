package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

public abstract class EntityRenameHelper extends EntityRename {
   public EntityRenameHelper(String name, Schema outputSchema, boolean changesType) {
      super(name, outputSchema, changesType);
   }

   protected Pair<String, Typed<?>> fix(String p_209149_1_, Typed<?> p_209149_2_) {
      Pair<String, Dynamic<?>> pair = this.getNewNameAndTag(p_209149_1_, p_209149_2_.getOrCreate(DSL.remainderFinder()));
      return Pair.of(pair.getFirst(), p_209149_2_.set(DSL.remainderFinder(), pair.getSecond()));
   }

   protected abstract Pair<String, Dynamic<?>> getNewNameAndTag(String name, Dynamic<?> tag);
}