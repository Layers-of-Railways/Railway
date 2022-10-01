package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Conductor.ConductorEntity;
import com.railwayteam.railways.content.Conductor.ConductorRenderer;
import com.railwayteam.railways.content.Steamcart.SteamCartEntity;
import com.railwayteam.railways.content.Steamcart.SteamCartRenderer;
import com.railwayteam.railways.content.minecarts.MinecartBlock;
import com.railwayteam.railways.content.minecarts.MinecartBlockRenderer;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CREntities {
  public static EntityEntry<SteamCartEntity> CART_STEAM;
  public static EntityEntry<MinecartWorkbench> CART_BLOCK;
  public static EntityEntry<MinecartJukebox>   CART_JUKEBOX;

  public static EntityEntry<ConductorEntity> CONDUCTOR;

  private static class CRFactory implements EntityType.EntityFactory<MinecartBlock> {
    private final Block b;
    public CRFactory (Block contents) {
      b = contents;
    }
    @NotNull
    @Override
    public MinecartBlock create(@NotNull EntityType<MinecartBlock> type, @NotNull Level level) {
      return new MinecartBlock(type, level, b);
    }
  }

  public static void register(Registrate reg) {
    CART_STEAM = reg.entity("steamcart", SteamCartEntity::new, MobCategory.MISC)
    .renderer(()-> SteamCartRenderer::new)
    .properties(p -> p.sized(0.98F, 0.7F))
    .spawnEgg(Color.WHITE.getRGB(), Color.BLACK.getRGB())
      .lang("Minecart With Steam Engine")
      .tab(()-> CRItems.itemGroup)
      .model((ctx,prov)-> prov.withExistingParent("steamcart_spawn_egg", prov.mcLoc("item/minecart")).texture("layer0", prov.modLoc("item/steamcart")))
      .recipe((ctx,prov)-> ShapedRecipeBuilder.shaped(ctx.get())
        .pattern("ctp")
        .pattern(" u ")
        .define('c', AllBlocks.COGWHEEL.get())
        .define('t', AllBlocks.FLUID_TANK.get())
        .define('p', AllItems.COPPER_SHEET.get())
        .define('u', Items.MINECART)
        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov)
      )
      .build()
    .lang("Minecart With Steam Engine")
    .register();

    CART_BLOCK = reg.entity("benchcart", MinecartWorkbench::new, MobCategory.MISC)
    .renderer(()-> MinecartBlockRenderer::new)
    .properties(p -> p.sized(0.98F, 0.7F))
    .spawnEgg(Color.WHITE.getRGB(), Color.BLACK.getRGB())
      .lang("Minecart with Workbench")
      .tab(()-> CRItems.itemGroup)
      .model((ctx,prov)-> prov.withExistingParent("benchcart_spawn_egg", prov.mcLoc("item/minecart")).texture("layer0", prov.modLoc("item/benchcart")))
      .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.CRAFTING_TABLE)
        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
      .build()
    .lang("Minecart with Workbench")
    .register();

    CART_JUKEBOX = reg.entity("jukeboxcart", MinecartJukebox::new, MobCategory.MISC)
    .renderer(()-> MinecartBlockRenderer::new)
    .properties(p -> p.sized(0.98F, 0.7F))
    .spawnEgg(Color.WHITE.getRGB(), Color.BLACK.getRGB())
      .lang("Minecart with Jukebox")
      .tab(()-> CRItems.itemGroup)
      .model((ctx,prov)-> prov.withExistingParent("jukeboxcart_spawn_egg", prov.mcLoc("item/minecart")).texture("layer0", prov.modLoc("item/jukeboxcart")))
      .recipe((ctx,prov)-> ShapelessRecipeBuilder.shapeless(ctx.get()).requires(Items.MINECART).requires(Items.JUKEBOX)
        .unlockedBy("hasitem", InventoryChangeTrigger.TriggerInstance.hasItems(Items.MINECART)).save(prov))
      .build()
    .lang("Minecart with Jukebox")
    .register();

    CONDUCTOR = reg.entity("conductor", ConductorEntity::new, MobCategory.CREATURE)
      .renderer(()-> ConductorRenderer::new)
      .lang("Conductor")
      .properties(p -> p.sized(0.6f, 1.5f).fireImmune())
      .loot((table, type)-> table.add(type, new LootTable.Builder().withPool(
          LootPool.lootPool()
              .setRolls(ConstantValue.exactly(1.0f))
              .add(LootItem.lootTableItem(AllItems.ANDESITE_ALLOY.get())
                  .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0f, 1.0f)))
              )
      )))
      .attributes(ConductorEntity::createAttributes)
      .register();
  }
}
