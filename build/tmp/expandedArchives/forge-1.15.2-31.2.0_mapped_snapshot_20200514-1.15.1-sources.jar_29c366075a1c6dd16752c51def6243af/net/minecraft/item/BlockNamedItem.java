package net.minecraft.item;

import net.minecraft.block.Block;

public class BlockNamedItem extends BlockItem {
   public BlockNamedItem(Block blockIn, Item.Properties properties) {
      super(blockIn, properties);
   }

   /**
    * Returns the unlocalized name of this item.
    */
   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }
}