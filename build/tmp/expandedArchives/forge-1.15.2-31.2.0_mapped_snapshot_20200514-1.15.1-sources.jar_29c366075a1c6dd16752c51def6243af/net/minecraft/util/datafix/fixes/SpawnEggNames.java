package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class SpawnEggNames extends DataFix {
   private static final String[] ENTITY_IDS = DataFixUtils.make(new String[256], (p_209278_0_) -> {
      p_209278_0_[1] = "Item";
      p_209278_0_[2] = "XPOrb";
      p_209278_0_[7] = "ThrownEgg";
      p_209278_0_[8] = "LeashKnot";
      p_209278_0_[9] = "Painting";
      p_209278_0_[10] = "Arrow";
      p_209278_0_[11] = "Snowball";
      p_209278_0_[12] = "Fireball";
      p_209278_0_[13] = "SmallFireball";
      p_209278_0_[14] = "ThrownEnderpearl";
      p_209278_0_[15] = "EyeOfEnderSignal";
      p_209278_0_[16] = "ThrownPotion";
      p_209278_0_[17] = "ThrownExpBottle";
      p_209278_0_[18] = "ItemFrame";
      p_209278_0_[19] = "WitherSkull";
      p_209278_0_[20] = "PrimedTnt";
      p_209278_0_[21] = "FallingSand";
      p_209278_0_[22] = "FireworksRocketEntity";
      p_209278_0_[23] = "TippedArrow";
      p_209278_0_[24] = "SpectralArrow";
      p_209278_0_[25] = "ShulkerBullet";
      p_209278_0_[26] = "DragonFireball";
      p_209278_0_[30] = "ArmorStand";
      p_209278_0_[41] = "Boat";
      p_209278_0_[42] = "MinecartRideable";
      p_209278_0_[43] = "MinecartChest";
      p_209278_0_[44] = "MinecartFurnace";
      p_209278_0_[45] = "MinecartTNT";
      p_209278_0_[46] = "MinecartHopper";
      p_209278_0_[47] = "MinecartSpawner";
      p_209278_0_[40] = "MinecartCommandBlock";
      p_209278_0_[48] = "Mob";
      p_209278_0_[49] = "Monster";
      p_209278_0_[50] = "Creeper";
      p_209278_0_[51] = "Skeleton";
      p_209278_0_[52] = "Spider";
      p_209278_0_[53] = "Giant";
      p_209278_0_[54] = "Zombie";
      p_209278_0_[55] = "Slime";
      p_209278_0_[56] = "Ghast";
      p_209278_0_[57] = "PigZombie";
      p_209278_0_[58] = "Enderman";
      p_209278_0_[59] = "CaveSpider";
      p_209278_0_[60] = "Silverfish";
      p_209278_0_[61] = "Blaze";
      p_209278_0_[62] = "LavaSlime";
      p_209278_0_[63] = "EnderDragon";
      p_209278_0_[64] = "WitherBoss";
      p_209278_0_[65] = "Bat";
      p_209278_0_[66] = "Witch";
      p_209278_0_[67] = "Endermite";
      p_209278_0_[68] = "Guardian";
      p_209278_0_[69] = "Shulker";
      p_209278_0_[90] = "Pig";
      p_209278_0_[91] = "Sheep";
      p_209278_0_[92] = "Cow";
      p_209278_0_[93] = "Chicken";
      p_209278_0_[94] = "Squid";
      p_209278_0_[95] = "Wolf";
      p_209278_0_[96] = "MushroomCow";
      p_209278_0_[97] = "SnowMan";
      p_209278_0_[98] = "Ozelot";
      p_209278_0_[99] = "VillagerGolem";
      p_209278_0_[100] = "EntityHorse";
      p_209278_0_[101] = "Rabbit";
      p_209278_0_[120] = "Villager";
      p_209278_0_[200] = "EnderCrystal";
   });

   public SpawnEggNames(Schema outputSchema, boolean changesType) {
      super(outputSchema, changesType);
   }

   public TypeRewriteRule makeRule() {
      Schema schema = this.getInputSchema();
      Type<?> type = schema.getType(TypeReferences.ITEM_STACK);
      OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<String> opticfinder1 = DSL.fieldFinder("id", DSL.string());
      OpticFinder<?> opticfinder2 = type.findField("tag");
      OpticFinder<?> opticfinder3 = opticfinder2.type().findField("EntityTag");
      OpticFinder<?> opticfinder4 = DSL.typeFinder(schema.getTypeRaw(TypeReferences.ENTITY));
      return this.fixTypeEverywhereTyped("ItemSpawnEggFix", type, (p_206359_6_) -> {
         Optional<Pair<String, String>> optional = p_206359_6_.getOptional(opticfinder);
         if (optional.isPresent() && Objects.equals(optional.get().getSecond(), "minecraft:spawn_egg")) {
            Dynamic<?> dynamic = p_206359_6_.get(DSL.remainderFinder());
            short short1 = dynamic.get("Damage").asShort((short)0);
            Optional<? extends Typed<?>> optional1 = p_206359_6_.getOptionalTyped(opticfinder2);
            Optional<? extends Typed<?>> optional2 = optional1.flatMap((p_207479_1_) -> {
               return p_207479_1_.getOptionalTyped(opticfinder3);
            });
            Optional<? extends Typed<?>> optional3 = optional2.flatMap((p_207482_1_) -> {
               return p_207482_1_.getOptionalTyped(opticfinder4);
            });
            Optional<String> optional4 = optional3.flatMap((p_207481_1_) -> {
               return p_207481_1_.getOptional(opticfinder1);
            });
            Typed<?> typed = p_206359_6_;
            String s = ENTITY_IDS[short1 & 255];
            if (s != null && (!optional4.isPresent() || !Objects.equals(optional4.get(), s))) {
               Typed<?> typed1 = p_206359_6_.getOrCreateTyped(opticfinder2);
               Typed<?> typed2 = typed1.getOrCreateTyped(opticfinder3);
               Typed<?> typed3 = typed2.getOrCreateTyped(opticfinder4);
               Dynamic<?> dynamic1 = typed3.write().set("id", dynamic.createString(s));
               Typed<?> typed4 = this.getOutputSchema().getTypeRaw(TypeReferences.ENTITY).readTyped(dynamic1).getSecond().orElseThrow(() -> {
                  return new IllegalStateException("Could not parse new entity");
               });
               typed = p_206359_6_.set(opticfinder2, typed1.set(opticfinder3, typed2.set(opticfinder4, typed4)));
            }

            if (short1 != 0) {
               dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
               typed = typed.set(DSL.remainderFinder(), dynamic);
            }

            return typed;
         } else {
            return p_206359_6_;
         }
      });
   }
}