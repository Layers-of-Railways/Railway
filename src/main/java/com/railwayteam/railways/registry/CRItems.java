package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.builders.ItemBuilder;
import com.simibubi.create.repack.registrate.util.LazySpawnEggItem;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.awt.*;
import java.util.function.Supplier;

public class CRItems {
    public static final CreativeModeTab itemGroup = new CreativeModeTab(Railways.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Items.MINECART);
        }
    };

    private static ItemBuilder<? extends Item, ?> makeMinecart (Registrate reg, String name, Supplier<EntityEntry<?>> entity, Color primary) {
        return reg.item(name, (props)-> new LazySpawnEggItem<>(entity.get(), primary.getRGB(), Color.BLACK.getRGB(), props))
        .model((ctx,prov)-> prov.withExistingParent(name, prov.mcLoc("item/minecart")).texture("layer0", prov.modLoc("item/" + name)));
    }

    //public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;
    public static ItemEntry<? extends Item> ITEM_BENCHCART;
    public static ItemEntry<? extends Item> ITEM_JUKEBOXCART;
    public static ItemEntry<? extends Item> ITEM_STEAMCART;

    public static void register(Registrate reg) {
        reg.creativeModeTab(() -> itemGroup, "Create Railways");

        ITEM_BENCHCART = makeMinecart(reg, "benchcart", ()->CREntities.CART_BLOCK, Color.YELLOW)
        .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.CRAFTING_TABLE)
          .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
        .lang("Minecart with Workbench")
        .register();

        ITEM_JUKEBOXCART = makeMinecart(reg, "jukeboxcart", ()->CREntities.CART_JUKEBOX, Color.RED)
        .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.JUKEBOX)
          .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
        .lang("Minecart with Jukebox")
        .register();

        ITEM_STEAMCART = makeMinecart(reg, "steamcart", ()->CREntities.CART_STEAM, Color.ORANGE)
          .recipe((ctx,prov)-> ShapedRecipeBuilder.shaped(ctx.get())
            .pattern("ctp")
            .pattern(" u ")
            .define('c', AllBlocks.COGWHEEL.get())
            .define('t', AllBlocks.FLUID_TANK.get())
            .define('p', AllItems.COPPER_SHEET.get())
            .define('u', Items.MINECART)
            .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov)
          )
          .lang("Steam-Powered Minecart").register();

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
