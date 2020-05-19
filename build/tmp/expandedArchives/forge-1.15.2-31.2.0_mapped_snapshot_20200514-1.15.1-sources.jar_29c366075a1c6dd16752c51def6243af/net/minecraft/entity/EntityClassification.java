package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EntityClassification implements net.minecraftforge.common.IExtensibleEnum {
   MONSTER("monster", 70, false, false),
   CREATURE("creature", 10, true, true),
   AMBIENT("ambient", 15, true, false),
   WATER_CREATURE("water_creature", 15, true, false),
   MISC("misc", 15, true, false);

   private static final Map<String, EntityClassification> VALUES_MAP = Arrays.stream(values()).collect(Collectors.toMap(EntityClassification::getName, (p_220362_0_) -> {
      return p_220362_0_;
   }));
   private final int maxNumberOfCreature;
   private final boolean isPeacefulCreature;
   private final boolean isAnimal;
   private final String name;

   private EntityClassification(String id, int maxNumberOfCreatureIn, boolean isPeacefulCreatureIn, boolean isAnimalIn) {
      this.name = id;
      this.maxNumberOfCreature = maxNumberOfCreatureIn;
      this.isPeacefulCreature = isPeacefulCreatureIn;
      this.isAnimal = isAnimalIn;
   }

   public String getName() {
      return this.name;
   }

   public int getMaxNumberOfCreature() {
      return this.maxNumberOfCreature;
   }

   /**
    * Gets whether or not this creature type is peaceful.
    */
   public boolean getPeacefulCreature() {
      return this.isPeacefulCreature;
   }

   /**
    * Return whether this creature type is an animal.
    */
   public boolean getAnimal() {
      return this.isAnimal;
   }

   public static EntityClassification create(String name, String id, int maxNumberOfCreatureIn, boolean isPeacefulCreatureIn, boolean isAnimalIn) {
      throw new IllegalStateException("Enum not extended");
   }
}