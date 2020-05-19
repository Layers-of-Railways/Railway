package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldType implements net.minecraftforge.common.extensions.IForgeWorldType {
   public static WorldType[] WORLD_TYPES = new WorldType[16];
   public static final WorldType DEFAULT = (new WorldType(0, "default", 1)).setVersioned();
   public static final WorldType FLAT = (new WorldType(1, "flat")).setCustomOptions(true);
   public static final WorldType LARGE_BIOMES = new WorldType(2, "largeBiomes");
   /** amplified world type */
   public static final WorldType AMPLIFIED = (new WorldType(3, "amplified")).enableInfoNotice();
   public static final WorldType CUSTOMIZED = (new WorldType(4, "customized", "normal", 0)).setCustomOptions(true).setCanBeCreated(false);
   public static final WorldType BUFFET = (new WorldType(5, "buffet")).setCustomOptions(true);
   public static final WorldType DEBUG_ALL_BLOCK_STATES = new WorldType(6, "debug_all_block_states");
   public static final WorldType DEFAULT_1_1 = (new WorldType(8, "default_1_1", 0)).setCanBeCreated(false);
   private final int id;
   private final String name;
   private final String serializedId;
   private final int version;
   private boolean canBeCreated;
   private boolean versioned;
   private boolean hasInfoNotice;
   private boolean field_205395_p;

   public WorldType(String name) {
      this(getNextID(), name);
   }

   private WorldType(int id, String name) {
      this(id, name, name, 0);
   }

   private WorldType(int id, String name, int version) {
      this(id, name, name, version);
   }

   private WorldType(int idIn, String nameIn, String serialization, int versionIn) {
      if (nameIn.length() > 16 && DEBUG_ALL_BLOCK_STATES != null) throw new IllegalArgumentException("World type names must not be longer then 16: " + nameIn);
      this.name = nameIn;
      this.serializedId = serialization;
      this.version = versionIn;
      this.canBeCreated = true;
      this.id = idIn;
      WORLD_TYPES[idIn] = this;
   }

   private static int getNextID() {
      for (int x = 0; x < WORLD_TYPES.length; x++) {
         if (WORLD_TYPES[x] == null)
            return x;
      }
      int old = WORLD_TYPES.length;
      WORLD_TYPES = java.util.Arrays.copyOf(WORLD_TYPES, old + 16);
      return old;
   }

   public String getName() {
      return this.name;
   }

   public String getSerialization() {
      return this.serializedId;
   }

   /**
    * Gets the translation key for the name of this world type.
    */
   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "generator." + this.name;
   }

   /**
    * Gets the translation key for the info text for this world type.
    */
   @OnlyIn(Dist.CLIENT)
   public String getInfoTranslationKey() {
      return this.getTranslationKey() + ".info";
   }

   /**
    * Returns generatorVersion.
    */
   public int getVersion() {
      return this.version;
   }

   public WorldType getWorldTypeForGeneratorVersion(int version) {
      return this == DEFAULT && version == 0 ? DEFAULT_1_1 : this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomOptions() {
      return this.field_205395_p;
   }

   public WorldType setCustomOptions(boolean p_205392_1_) {
      this.field_205395_p = p_205392_1_;
      return this;
   }

   /**
    * Sets canBeCreated to the provided value, and returns this.
    */
   private WorldType setCanBeCreated(boolean enable) {
      this.canBeCreated = enable;
      return this;
   }

   /**
    * Gets whether this WorldType can be used to generate a new world.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean canBeCreated() {
      return this.canBeCreated;
   }

   /**
    * Flags this world type as having an associated version.
    */
   private WorldType setVersioned() {
      this.versioned = true;
      return this;
   }

   /**
    * Returns true if this world Type has a version associated with it.
    */
   public boolean isVersioned() {
      return this.versioned;
   }

   @Nullable
   public static WorldType byName(String type) {
      for(WorldType worldtype : WORLD_TYPES) {
         if (worldtype != null && worldtype.name.equalsIgnoreCase(type)) {
            return worldtype;
         }
      }

      return null;
   }

   public int getId() {
      return this.id;
   }

   /**
    * returns true if selecting this worldtype from the customize menu should display the generator.[worldtype].info
    * message
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasInfoNotice() {
      return this.hasInfoNotice;
   }

   /**
    * enables the display of generator.[worldtype].info message on the customize world menu
    */
   private WorldType enableInfoNotice() {
      this.hasInfoNotice = true;
      return this;
   }
}