package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemGroup {
   public static ItemGroup[] GROUPS = new ItemGroup[12];
   public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.BRICKS);
      }
   }).setTabPath("building_blocks");
   public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.PEONY);
      }
   };
   public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.REDSTONE);
      }
   };
   public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.POWERED_RAIL);
      }
   };
   public static final ItemGroup MISC = new ItemGroup(6, "misc") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.LAVA_BUCKET);
      }
   };
   public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.COMPASS);
      }
   }).setBackgroundImageName("item_search.png");
   public static final ItemGroup FOOD = new ItemGroup(7, "food") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.APPLE);
      }
   };
   public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.IRON_AXE);
      }
   }).setRelevantEnchantmentTypes(new EnchantmentType[]{EnchantmentType.ALL, EnchantmentType.DIGGER, EnchantmentType.FISHING_ROD, EnchantmentType.BREAKABLE});
   public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.GOLDEN_SWORD);
      }
   }).setRelevantEnchantmentTypes(new EnchantmentType[]{EnchantmentType.ALL, EnchantmentType.ARMOR, EnchantmentType.ARMOR_FEET, EnchantmentType.ARMOR_HEAD, EnchantmentType.ARMOR_LEGS, EnchantmentType.ARMOR_CHEST, EnchantmentType.BOW, EnchantmentType.WEAPON, EnchantmentType.WEARABLE, EnchantmentType.BREAKABLE, EnchantmentType.TRIDENT, EnchantmentType.CROSSBOW});
   public static final ItemGroup BREWING = new ItemGroup(10, "brewing") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), Potions.WATER);
      }
   };
   public static final ItemGroup MATERIALS = MISC;
   public static final ItemGroup HOTBAR = new ItemGroup(4, "hotbar") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.BOOKSHELF);
      }

      /**
       * Fills {@code items} with all items that are in this group.
       */
      @OnlyIn(Dist.CLIENT)
      public void fill(NonNullList<ItemStack> items) {
         throw new RuntimeException("Implement exception client-side.");
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isAlignedRight() {
         return true;
      }
   };
   public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.CHEST);
      }
   }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
   private final int index;
   private final String tabLabel;
   private String tabPath;
   private String backgroundTexture = "items.png";
   private boolean hasScrollbar = true;
   private boolean drawTitle = true;
   private EnchantmentType[] enchantmentTypes = new EnchantmentType[0];
   private ItemStack icon;

   public ItemGroup(String label) {
       this(-1, label);
   }

   public ItemGroup(int index, String label) {
      this.tabLabel = label;
      this.icon = ItemStack.EMPTY;
      this.index = addGroupSafe(index, this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getIndex() {
      return this.index;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTabLabel() {
      return this.tabLabel;
   }

   /**
    * Gets the name that's valid for use in a ResourceLocation's path. This should be set if the tabLabel contains
    * illegal characters.
    */
   public String getPath() {
      return this.tabPath == null ? this.tabLabel : this.tabPath;
   }

   /**
    * Gets the translated Label.
    */
   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "itemGroup." + this.getTabLabel();
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getIcon() {
      if (this.icon.isEmpty()) {
         this.icon = this.createIcon();
      }

      return this.icon;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract ItemStack createIcon();

   @OnlyIn(Dist.CLIENT)
   public String getBackgroundImageName() {
      return this.backgroundTexture;
   }

   public ItemGroup setBackgroundImageName(String texture) {
      this.backgroundTexture = texture;
      return this;
   }

   public ItemGroup setTabPath(String pathIn) {
      this.tabPath = pathIn;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public ItemGroup setNoTitle() {
      this.drawTitle = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasScrollbar() {
      return this.hasScrollbar;
   }

   public ItemGroup setNoScrollbar() {
      this.hasScrollbar = false;
      return this;
   }

   /**
    * returns index % 6
    */
   @OnlyIn(Dist.CLIENT)
   public int getColumn() {
      if (index > 11) return ((index - 12) % 10) % 5;
      return this.index % 6;
   }

   /**
    * returns tabIndex < 6
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isOnTopRow() {
      if (index > 11) return ((index - 12) % 10) < 5;
      return this.index < 6;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAlignedRight() {
      return this.getColumn() == 5;
   }

   /**
    * Returns the enchantment types relevant to this tab
    */
   public EnchantmentType[] getRelevantEnchantmentTypes() {
      return this.enchantmentTypes;
   }

   /**
    * Sets the enchantment types for populating this tab with enchanting books
    */
   public ItemGroup setRelevantEnchantmentTypes(EnchantmentType... types) {
      this.enchantmentTypes = types;
      return this;
   }

   public boolean hasRelevantEnchantmentType(@Nullable EnchantmentType enchantmentType) {
      if (enchantmentType != null) {
         for(EnchantmentType enchantmenttype : this.enchantmentTypes) {
            if (enchantmenttype == enchantmentType) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * Fills {@code items} with all items that are in this group.
    */
   @OnlyIn(Dist.CLIENT)
   public void fill(NonNullList<ItemStack> items) {
      for(Item item : Registry.ITEM) {
         item.fillItemGroup(this, items);
      }

   }

   public int getTabPage() {
      return index < 12 ? 0 : ((index - 12) / 10) + 1;
   }

   public boolean hasSearchBar() {
      return index == SEARCH.index;
   }

   /**
    * Gets the width of the search bar of the creative tab, use this if your
    * creative tab name overflows together with a custom texture.
    *
    * @return The width of the search bar, 89 by default
    */
   public int getSearchbarWidth() {
      return 89;
   }

   @OnlyIn(Dist.CLIENT)
   public net.minecraft.util.ResourceLocation getBackgroundImage() {
      return new net.minecraft.util.ResourceLocation("textures/gui/container/creative_inventory/tab_" + this.getBackgroundImageName());
   }

   private static final net.minecraft.util.ResourceLocation CREATIVE_INVENTORY_TABS = new net.minecraft.util.ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   @OnlyIn(Dist.CLIENT)
   public net.minecraft.util.ResourceLocation getTabsImage() {
      return CREATIVE_INVENTORY_TABS;
   }

   public int getLabelColor() {
      return 4210752;
   }

   public int getSlotColor() {
      return -2130706433;
   }
   
   public static synchronized int getGroupCountSafe() {
      return ItemGroup.GROUPS.length;
   }
   
   private static synchronized int addGroupSafe(int index, ItemGroup newGroup) {
      if(index == -1) {
         index = GROUPS.length;
      }
      if (index >= GROUPS.length) {
         ItemGroup[] tmp = new ItemGroup[index + 1];
         System.arraycopy(GROUPS, 0, tmp, 0, GROUPS.length);
         GROUPS = tmp;
      }
      GROUPS[index] = newGroup;
      return index;
   }
}