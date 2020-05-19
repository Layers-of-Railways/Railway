package net.minecraft.world.storage.loot;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class LootParameters {
   public static final LootParameter<Entity> THIS_ENTITY = register("this_entity");
   public static final LootParameter<PlayerEntity> LAST_DAMAGE_PLAYER = register("last_damage_player");
   public static final LootParameter<DamageSource> DAMAGE_SOURCE = register("damage_source");
   public static final LootParameter<Entity> KILLER_ENTITY = register("killer_entity");
   public static final LootParameter<Entity> DIRECT_KILLER_ENTITY = register("direct_killer_entity");
   public static final LootParameter<BlockPos> POSITION = register("position");
   public static final LootParameter<BlockState> BLOCK_STATE = register("block_state");
   public static final LootParameter<TileEntity> BLOCK_ENTITY = register("block_entity");
   public static final LootParameter<ItemStack> TOOL = register("tool");
   public static final LootParameter<Float> EXPLOSION_RADIUS = register("explosion_radius");

   private static <T> LootParameter<T> register(String p_216280_0_) {
      return new LootParameter<>(new ResourceLocation(p_216280_0_));
   }
}