package com.railwayteam.railways.content.minecarts;

import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinecartWorkbench extends MinecartBlock implements MenuProvider {
  public static final Type TYPE = Type.valueOf("RAILWAY_WORKBENCH");

  private final double VALID_RANGE = 32d;
  private static final EntityTypeTest<Entity, MinecartWorkbench> test = EntityTypeTest.forClass(MinecartWorkbench.class);

  public MinecartWorkbench(EntityType<?> type, Level level) {
    super(type, level, Blocks.CRAFTING_TABLE);
  }

  protected MinecartWorkbench(Level level, double x, double y, double z) {
    super(CREntities.CART_BLOCK.get(), level, x, y, z, Blocks.CRAFTING_TABLE);
  }

  // need to detour through this or generics explode somehow
  public static MinecartWorkbench create(Level level, double x, double y, double z) {
    return new MinecartWorkbench(level, x, y, z);
  }

  @Override
  public Type getMinecartType() {
    return TYPE;
  }

  @NotNull
  @Override
  public InteractionResult interact (@NotNull Player player, @NotNull InteractionHand hand) {
    InteractionResult ret = super.interact(player, hand);
    if (ret.consumesAction()) return ret;
    player.openMenu(this);
    if (!player.level.isClientSide) {
      this.gameEvent(GameEvent.CONTAINER_OPEN, player);
      PiglinAi.angerNearbyPiglins(player, true);
      return InteractionResult.CONSUME;
    } else {
      return InteractionResult.SUCCESS;
    }
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu (int p_39954_, @NotNull Inventory inv, @NotNull Player player) {
    return new CraftingMenu(p_39954_, inv, ContainerLevelAccess.create(level, blockPosition())) {
      @Override
      public boolean stillValid(@NotNull Player player) {
        return player.level.getEntities(
        test, player.getBoundingBox().inflate(VALID_RANGE), Entity::isAlive
        ).stream().anyMatch((e) -> player.distanceToSqr(e) < VALID_RANGE);
      }
    };
  }

  @Override
  public ItemStack getPickResult() {
    return CRItems.ITEM_BENCHCART.asStack();
  }
}
