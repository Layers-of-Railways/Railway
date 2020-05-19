package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;
import net.minecraft.util.datafix.TypeReferences;

public class ZombieProfToType extends NamedEntityFix {
   private static final Random RANDOM = new Random();

   public ZombieProfToType(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType, "EntityZombieVillagerTypeFix", TypeReferences.ENTITY, "Zombie");
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209656_1_) {
      if (p_209656_1_.get("IsVillager").asBoolean(false)) {
         if (!p_209656_1_.get("ZombieType").get().isPresent()) {
            int i = this.getVillagerProfession(p_209656_1_.get("VillagerProfession").asInt(-1));
            if (i == -1) {
               i = this.getVillagerProfession(RANDOM.nextInt(6));
            }

            p_209656_1_ = p_209656_1_.set("ZombieType", p_209656_1_.createInt(i));
         }

         p_209656_1_ = p_209656_1_.remove("IsVillager");
      }

      return p_209656_1_;
   }

   private int getVillagerProfession(int p_191277_1_) {
      return p_191277_1_ >= 0 && p_191277_1_ < 6 ? p_191277_1_ : -1;
   }

   protected Typed<?> fix(Typed<?> p_207419_1_) {
      return p_207419_1_.update(DSL.remainderFinder(), this::fixTag);
   }
}