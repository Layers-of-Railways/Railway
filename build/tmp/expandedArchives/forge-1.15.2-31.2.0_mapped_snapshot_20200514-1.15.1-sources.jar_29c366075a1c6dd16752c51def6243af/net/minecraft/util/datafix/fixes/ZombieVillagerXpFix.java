package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class ZombieVillagerXpFix extends NamedEntityFix {
   public ZombieVillagerXpFix(Schema p_i51507_1_, boolean p_i51507_2_) {
      super(p_i51507_1_, p_i51507_2_, "Zombie Villager XP rebuild", TypeReferences.ENTITY, "minecraft:zombie_villager");
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), (p_222993_0_) -> {
         Optional<Number> optional = p_222993_0_.get("Xp").asNumber();
         if (!optional.isPresent()) {
            int i = p_222993_0_.get("VillagerData").get("level").asNumber().orElse(1).intValue();
            return p_222993_0_.set("Xp", p_222993_0_.createInt(VillagerLevelAndXpFix.func_223001_a(i)));
         } else {
            return p_222993_0_;
         }
      });
   }
}