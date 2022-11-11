package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorRenderer;
import com.railwayteam.railways.content.minecarts.MinecartBlockRenderer;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class CREntities {
  private static final CreateRegistrate REGISTRATE = Railways.registrate();

  public static final EntityEntry<MinecartWorkbench> CART_BLOCK = REGISTRATE.entity("benchcart", MinecartWorkbench::new, MobCategory.MISC)
      .renderer(()-> MinecartBlockRenderer::new)
      .properties(p -> p.sized(0.98F, 0.7F))
      .lang("Minecart with Workbench")
      .register();
  public static final EntityEntry<MinecartJukebox> CART_JUKEBOX = REGISTRATE.entity("jukeboxcart", MinecartJukebox::new, MobCategory.MISC)
      .renderer(()-> MinecartBlockRenderer::new)
      .properties(p -> p.sized(0.98F, 0.7F))
      .lang("Minecart with Jukebox")
      .register();

  public static final EntityEntry<ConductorEntity> CONDUCTOR = REGISTRATE.entity("conductor", ConductorEntity::new, MobCategory.CREATURE)
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

  @SuppressWarnings("EmptyMethod")
  public static void register() {}
}
