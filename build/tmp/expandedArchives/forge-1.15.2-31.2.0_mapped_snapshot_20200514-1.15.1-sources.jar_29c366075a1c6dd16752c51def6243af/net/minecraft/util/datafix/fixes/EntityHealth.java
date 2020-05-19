package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.TypeReferences;

public class EntityHealth extends DataFix {
   private static final Set<String> ENTITY_LIST = Sets.newHashSet("ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie");

   public EntityHealth(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public Dynamic<?> fixTag(Dynamic<?> p_209743_1_) {
      Optional<Number> optional = p_209743_1_.get("HealF").asNumber();
      Optional<Number> optional1 = p_209743_1_.get("Health").asNumber();
      float f;
      if (optional.isPresent()) {
         f = optional.get().floatValue();
         p_209743_1_ = p_209743_1_.remove("HealF");
      } else {
         if (!optional1.isPresent()) {
            return p_209743_1_;
         }

         f = optional1.get().floatValue();
      }

      return p_209743_1_.set("Health", p_209743_1_.createFloat(f));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(TypeReferences.ENTITY), (p_207449_1_) -> {
         return p_207449_1_.update(DSL.remainderFinder(), this::fixTag);
      });
   }
}