package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Set;
import java.util.stream.Stream;

public class WoodType {
   private static final Set<WoodType> VALUES = new ObjectArraySet<>();
   public static final WoodType OAK = register(new WoodType("oak"));
   public static final WoodType SPRUCE = register(new WoodType("spruce"));
   public static final WoodType BIRCH = register(new WoodType("birch"));
   public static final WoodType ACACIA = register(new WoodType("acacia"));
   public static final WoodType JUNGLE = register(new WoodType("jungle"));
   public static final WoodType DARK_OAK = register(new WoodType("dark_oak"));
   private final String name;

   protected WoodType(String nameIn) {
      this.name = nameIn;
   }

   private static WoodType register(WoodType woodTypeIn) {
      VALUES.add(woodTypeIn);
      return woodTypeIn;
   }

   public static Stream<WoodType> getValues() {
      return VALUES.stream();
   }

   public String getName() {
      return this.name;
   }
}