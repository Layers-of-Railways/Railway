package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Conductor.ConductorEntity;
import com.railwayteam.railways.content.Conductor.ConductorRenderer;
import com.railwayteam.railways.content.Steamcart.SteamCartEntity;
import com.railwayteam.railways.content.Steamcart.SteamCartRenderer;
import com.railwayteam.railways.content.minecarts.MinecartBlock;
import com.railwayteam.railways.content.minecarts.MinecartBlockRenderer;
import com.railwayteam.railways.content.minecarts.MinecartJukebox;
import com.railwayteam.railways.content.minecarts.MinecartWorkbench;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class CREntities {
  public static EntityEntry<SteamCartEntity>   CART_STEAM;
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
      .lang("Steam Engine Cart")
      .register();

    CART_BLOCK = reg.entity("benchcart", MinecartWorkbench::new, MobCategory.MISC)
      .renderer(()-> MinecartBlockRenderer::new)
      .lang("Bench Minecart")
      .register();

    CART_JUKEBOX = reg.entity("jukeboxcart", MinecartJukebox::new, MobCategory.MISC)
      .renderer(()-> MinecartBlockRenderer::new)
      .lang("Jukebox Minecart")
      .register();

    CONDUCTOR = reg.entity("conductor", ConductorEntity::new, MobCategory.CREATURE)
      .renderer(()-> ConductorRenderer::new)
      .lang("Conductor")
      .properties(p -> p.sized(2, 1.7f).fireImmune())
      .loot((table, type)-> table.add(type, new LootTable.Builder()))
      .attributes(ConductorEntity::createAttributes)
      .register();
  }
}
