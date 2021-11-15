package com.railwayteam.railways.registry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.items.ConductorItem;
import com.railwayteam.railways.content.items.HandcarItem;
import com.railwayteam.railways.content.items.StationEditorItem;
import com.railwayteam.railways.content.items.WayPointToolItem;
import com.railwayteam.railways.content.items.engineers_cap.EngineersCapItem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.providers.RegistrateRecipeProvider;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

import static com.railwayteam.railways.registry.CRBlocks.R_BLOCK_WHEEL;
import static com.simibubi.create.repack.registrate.providers.RegistrateLangProvider.toEnglishName;

public class CRItems {
    public static final ItemGroup itemGroup = new ItemGroup(Railways.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.MINECART);
        }
    };

    public static ItemEntry<WayPointToolItem> R_ITEM_WAYPOINT_TOOL;
    public static ItemEntry<StationEditorItem> R_ITEM_STATION_EDITOR_TOOL;
    public static ItemEntry<HandcarItem> R_ITEM_HANDCAR;
    public static ItemEntry<Item> R_ITEM_WHISTLE;

    public static HashMap<DyeColor, ItemEntry<EngineersCapItem>> ENGINEERS_CAPS = new HashMap<>();
    public static HashMap<DyeColor, ItemEntry<ConductorItem>> CONDUCTOR_ITEMS = new HashMap<>();

    public static void register(Registrate reg) {
        reg.itemGroup(() -> itemGroup, "Create Railways");

        R_ITEM_WAYPOINT_TOOL = reg.item(WayPointToolItem.name, WayPointToolItem::new)
                .lang("Waypoint Tool")
                .recipe((ctx, prov) -> ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                        .addIngredient(ItemTags.SIGNS)
                        .addIngredient(AllItems.ANDESITE_ALLOY.get())
                        .addCriterion("has_andesite_alloy", RegistrateRecipeProvider.hasItem(AllItems.ANDESITE_ALLOY.get()))
                        .build(prov))
                .register();

        ENGINEERS_CAPS = new HashMap<>();
        CONDUCTOR_ITEMS = new HashMap<>();

        for (DyeColor color : DyeColor.values()) {
            registerColored(reg, color);
        }

        R_ITEM_STATION_EDITOR_TOOL = reg.item(StationEditorItem.NAME, StationEditorItem::new)
                .lang("Station Editor")
                .register();

        R_ITEM_HANDCAR = reg.item("handcar", HandcarItem::new)
                .lang("Handcar")
//            .model((ctx, prov) -> {}) // TODO: handcar is invisible as an item even though it uses the same methods as others???
                .model((ctx, prov) -> {
                    prov.singleTexture(
                            ctx.getName(),
                            prov.mcLoc("item/generated"),
                            "layer0",
                            prov.modLoc("item/waypoint_manager"));
                })
                .properties(p -> p.maxStackSize(1))
                .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry())
                        .patternLine("W W")
                        .patternLine("CBC")
                        .patternLine("W W")
                        .key('W', R_BLOCK_WHEEL.get())
                        .key('C', AllBlocks.ANDESITE_CASING.get())
                        .key('B', AllBlocks.WOODEN_BRACKET.get())
                        .addCriterion("has_wheel", RegistrateRecipeProvider.hasItem(R_BLOCK_WHEEL.get()))
                        .build(prov))
                .register();

        R_ITEM_WHISTLE = reg.item("whistle", Item::new)
                .lang("Whistle")
                .properties(p -> p.maxStackSize(1))
                .model((ctx, prov) -> {
                })
                .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry())
                        .patternLine("B")
                        .patternLine("B")
                        .patternLine("A")
                        .key('B', AllItems.BRASS_INGOT.get())
                        .key('A', AllItems.ANDESITE_ALLOY.get())
                        .addCriterion("has_brass", RegistrateRecipeProvider.hasItem(AllItems.BRASS_INGOT.get()))
                        .build(prov))
                .register();
    }

    protected static void registerColored(Registrate reg, DyeColor color) {
        ENGINEERS_CAPS.put(color, reg.item(EngineersCapItem.name + "_" + color,
                (p) -> new EngineersCapItem(p, color))
                .lang(toEnglishName(color.getTranslationKey() + "_engineer's_cap"))
                .properties(p -> p.maxStackSize(1))
                .tag(CRTags.Items.EngineerCaps)
                .model((ctx, prov) -> {
                    prov.singleTexture(
                            ctx.getName(),
                            prov.mcLoc("item/generated"),
                            "layer0",
                            prov.modLoc("item/engineer_caps/" + color.getString() + "_engineers_cap"));
                })
                .recipe((ctx, prov) -> {
                    ShapedRecipeBuilder.shapedRecipe(ctx.get())
                            .patternLine("WWW")
                            .patternLine("W W")
                            .key('W', Ingredient.deserialize(new Gson().fromJson("{\"item\": \"minecraft:" + color.getString() + "_wool\"}", JsonObject.class))) // wow the fact that i have to do this is so stupid
                            .addCriterion("has_wool", RegistrateRecipeProvider.hasItem(ItemTags.WOOL))
                            .build(prov, new ResourceLocation("railways", "engineer_caps/" + color.getString()));
                    ShapelessRecipeBuilder.shapelessRecipe(ctx.get())
                            .addIngredient(CRTags.Items.EngineerCaps)
                            .addIngredient(color.getTag())
                            .addCriterion("has_wool", RegistrateRecipeProvider.hasItem(ItemTags.WOOL))
                            .build(prov, new ResourceLocation("railways", "engineer_caps/" + color.getString() + "_dye"));
                })
                .register());

        CONDUCTOR_ITEMS.put(color, reg.item("conductor" + "_" + color, p -> new ConductorItem(p, color))
                .lang(toEnglishName(color.getTranslationKey() + "_conductor"))
                .model((ctx, prov) -> {
                    prov.singleTexture(
                            ctx.getName(),
                            prov.mcLoc("item/generated"),
                            "layer0",
                            prov.modLoc("item/conductors/" + color.getTranslationKey() + "_conductor"));
                })
                .properties(p -> p.maxStackSize(1))
                .register()
        );
    }
}
