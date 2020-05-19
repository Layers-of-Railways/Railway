package net.minecraft.block.material;

public final class Material {
   public static final Material AIR = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().replaceable().build();
   public static final Material STRUCTURE_VOID = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().replaceable().build();
   public static final Material PORTAL = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().pushBlocks().build();
   public static final Material CARPET = (new Material.Builder(MaterialColor.WOOL)).doesNotBlockMovement().notOpaque().notSolid().flammable().build();
   public static final Material PLANTS = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().build();
   public static final Material OCEAN_PLANT = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().build();
   public static final Material TALL_PLANTS = (new Material.Builder(MaterialColor.FOLIAGE)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().flammable().build();
   public static final Material SEA_GRASS = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().build();
   public static final Material WATER = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
   public static final Material BUBBLE_COLUMN = (new Material.Builder(MaterialColor.WATER)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
   public static final Material LAVA = (new Material.Builder(MaterialColor.TNT)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().liquid().build();
   public static final Material SNOW = (new Material.Builder(MaterialColor.SNOW)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().requiresTool().build();
   public static final Material FIRE = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().build();
   public static final Material MISCELLANEOUS = (new Material.Builder(MaterialColor.AIR)).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().build();
   public static final Material WEB = (new Material.Builder(MaterialColor.WOOL)).doesNotBlockMovement().notOpaque().pushDestroys().requiresTool().build();
   public static final Material REDSTONE_LIGHT = (new Material.Builder(MaterialColor.AIR)).build();
   public static final Material CLAY = (new Material.Builder(MaterialColor.CLAY)).build();
   public static final Material EARTH = (new Material.Builder(MaterialColor.DIRT)).build();
   public static final Material ORGANIC = (new Material.Builder(MaterialColor.GRASS)).build();
   public static final Material PACKED_ICE = (new Material.Builder(MaterialColor.ICE)).build();
   public static final Material SAND = (new Material.Builder(MaterialColor.SAND)).build();
   public static final Material SPONGE = (new Material.Builder(MaterialColor.YELLOW)).build();
   public static final Material SHULKER = (new Material.Builder(MaterialColor.PURPLE)).build();
   public static final Material WOOD = (new Material.Builder(MaterialColor.WOOD)).flammable().build();
   public static final Material BAMBOO_SAPLING = (new Material.Builder(MaterialColor.WOOD)).flammable().pushDestroys().doesNotBlockMovement().build();
   public static final Material BAMBOO = (new Material.Builder(MaterialColor.WOOD)).flammable().pushDestroys().build();
   public static final Material WOOL = (new Material.Builder(MaterialColor.WOOL)).flammable().build();
   public static final Material TNT = (new Material.Builder(MaterialColor.TNT)).flammable().notOpaque().build();
   public static final Material LEAVES = (new Material.Builder(MaterialColor.FOLIAGE)).flammable().notOpaque().pushDestroys().build();
   public static final Material GLASS = (new Material.Builder(MaterialColor.AIR)).notOpaque().build();
   public static final Material ICE = (new Material.Builder(MaterialColor.ICE)).notOpaque().build();
   public static final Material CACTUS = (new Material.Builder(MaterialColor.FOLIAGE)).notOpaque().pushDestroys().build();
   public static final Material ROCK = (new Material.Builder(MaterialColor.STONE)).requiresTool().build();
   public static final Material IRON = (new Material.Builder(MaterialColor.IRON)).requiresTool().build();
   /** The material for crafted snow. */
   public static final Material SNOW_BLOCK = (new Material.Builder(MaterialColor.SNOW)).requiresTool().build();
   public static final Material ANVIL = (new Material.Builder(MaterialColor.IRON)).requiresTool().pushBlocks().build();
   public static final Material BARRIER = (new Material.Builder(MaterialColor.AIR)).requiresTool().pushBlocks().build();
   public static final Material PISTON = (new Material.Builder(MaterialColor.STONE)).pushBlocks().build();
   public static final Material CORAL = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
   public static final Material GOURD = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
   public static final Material DRAGON_EGG = (new Material.Builder(MaterialColor.FOLIAGE)).pushDestroys().build();
   public static final Material CAKE = (new Material.Builder(MaterialColor.AIR)).pushDestroys().build();
   /** The color index used to draw the blocks of this material on maps. */
   private final MaterialColor color;
   /**
    * Mobility information flag. 0 indicates that this block is normal, 1 indicates that it can't push other blocks, 2
    * indicates that it can't be pushed.
    */
   private final PushReaction pushReaction;
   private final boolean blocksMovement;
   private final boolean flammable;
   private final boolean requiresNoTool;
   private final boolean isLiquid;
   private final boolean isOpaque;
   private final boolean replaceable;
   private final boolean isSolid;

   public Material(MaterialColor materialMapColorIn, boolean liquid, boolean solid, boolean doesBlockMovement, boolean opaque, boolean requiresNoToolIn, boolean canBurnIn, boolean replaceableIn, PushReaction mobilityFlag) {
      this.color = materialMapColorIn;
      this.isLiquid = liquid;
      this.isSolid = solid;
      this.blocksMovement = doesBlockMovement;
      this.isOpaque = opaque;
      this.requiresNoTool = requiresNoToolIn;
      this.flammable = canBurnIn;
      this.replaceable = replaceableIn;
      this.pushReaction = mobilityFlag;
   }

   /**
    * Returns if blocks of these materials are liquids.
    */
   public boolean isLiquid() {
      return this.isLiquid;
   }

   /**
    * Returns true if the block is a considered solid. This is true by default.
    */
   public boolean isSolid() {
      return this.isSolid;
   }

   /**
    * Returns if this material is considered solid or not
    */
   public boolean blocksMovement() {
      return this.blocksMovement;
   }

   /**
    * Returns if the block can burn or not.
    */
   public boolean isFlammable() {
      return this.flammable;
   }

   /**
    * Returns whether the material can be replaced by other blocks when placed - eg snow, vines and tall grass.
    */
   public boolean isReplaceable() {
      return this.replaceable;
   }

   /**
    * Indicate if the material is opaque
    */
   public boolean isOpaque() {
      return this.isOpaque;
   }

   /**
    * Returns true if the material can be harvested without a tool (or with the wrong tool)
    */
   public boolean isToolNotRequired() {
      return this.requiresNoTool;
   }

   public PushReaction getPushReaction() {
      return this.pushReaction;
   }

   /**
    * Retrieves the color index of the block. This is is the same color used by vanilla maps to represent this block.
    */
   public MaterialColor getColor() {
      return this.color;
   }

   public static class Builder {
      private PushReaction pushReaction = PushReaction.NORMAL;
      private boolean blocksMovement = true;
      private boolean canBurn;
      private boolean requiresNoTool = true;
      private boolean isLiquid;
      private boolean isReplaceable;
      private boolean isSolid = true;
      private final MaterialColor color;
      private boolean isOpaque = true;

      public Builder(MaterialColor color) {
         this.color = color;
      }

      public Material.Builder liquid() {
         this.isLiquid = true;
         return this;
      }

      public Material.Builder notSolid() {
         this.isSolid = false;
         return this;
      }

      public Material.Builder doesNotBlockMovement() {
         this.blocksMovement = false;
         return this;
      }

      private Material.Builder notOpaque() {
         this.isOpaque = false;
         return this;
      }

      protected Material.Builder requiresTool() {
         this.requiresNoTool = false;
         return this;
      }

      protected Material.Builder flammable() {
         this.canBurn = true;
         return this;
      }

      public Material.Builder replaceable() {
         this.isReplaceable = true;
         return this;
      }

      protected Material.Builder pushDestroys() {
         this.pushReaction = PushReaction.DESTROY;
         return this;
      }

      protected Material.Builder pushBlocks() {
         this.pushReaction = PushReaction.BLOCK;
         return this;
      }

      public Material build() {
         return new Material(this.color, this.isLiquid, this.isSolid, this.blocksMovement, this.isOpaque, this.requiresNoTool, this.canBurn, this.isReplaceable, this.pushReaction);
      }
   }
}