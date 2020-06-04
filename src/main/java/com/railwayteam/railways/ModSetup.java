package com.railwayteam.railways;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModSetup {
    public static ItemGroup itemGroup = new ItemGroup(Railways.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.MINECART);
        }
    };

    public void init() {

    }
}
