package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.simibubi.create.repack.registrate.Registrate;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CRItems {
    public static final CreativeModeTab itemGroup = new CreativeModeTab(Railways.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.MINECART);
        }
    };

    //public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;

    public static void register(Registrate reg) {
        reg.creativeModeTab(() -> itemGroup, "Create Railways");
/*
        R_ITEM_WAYPOINT_TOOL = reg.item(WayPointToolItem.name, WayPointToolItem::new)
                .lang("Waypoint Tool")
                .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                        .addIngredient(ItemTags.SIGNS)
                        .addIngredient(AllItems.ANDESITE_ALLOY.get())
                        .addCriterion("has_andesite_alloy", RegistrateRecipeProvider.hasItem(AllItems.ANDESITE_ALLOY.get()))
                        .build(prov))
                .register();

 */
    }
}
