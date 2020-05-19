package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagManager implements IFutureReloadListener {
   private final NetworkTagCollection<Block> blocks = new NetworkTagCollection<>(Registry.BLOCK, "tags/blocks", "block");
   private final NetworkTagCollection<Item> items = new NetworkTagCollection<>(Registry.ITEM, "tags/items", "item");
   private final NetworkTagCollection<Fluid> fluids = new NetworkTagCollection<>(Registry.FLUID, "tags/fluids", "fluid");
   private final NetworkTagCollection<EntityType<?>> entityTypes = new NetworkTagCollection<>(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");

   public NetworkTagCollection<Block> getBlocks() {
      return this.blocks;
   }

   public NetworkTagCollection<Item> getItems() {
      return this.items;
   }

   public NetworkTagCollection<Fluid> getFluids() {
      return this.fluids;
   }

   public NetworkTagCollection<EntityType<?>> getEntityTypes() {
      return this.entityTypes;
   }

   public void write(PacketBuffer buffer) {
      this.blocks.write(buffer);
      this.items.write(buffer);
      this.fluids.write(buffer);
      this.entityTypes.write(buffer);
   }

   public static NetworkTagManager read(PacketBuffer buffer) {
      NetworkTagManager networktagmanager = new NetworkTagManager();
      networktagmanager.getBlocks().read(buffer);
      networktagmanager.getItems().read(buffer);
      networktagmanager.getFluids().read(buffer);
      networktagmanager.getEntityTypes().read(buffer);
      return networktagmanager;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Block>>> completablefuture = this.blocks.reload(resourceManager, backgroundExecutor);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Item>>> completablefuture1 = this.items.reload(resourceManager, backgroundExecutor);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Fluid>>> completablefuture2 = this.fluids.reload(resourceManager, backgroundExecutor);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<EntityType<?>>>> completablefuture3 = this.entityTypes.reload(resourceManager, backgroundExecutor);
      return completablefuture.thenCombine(completablefuture1, Pair::of).thenCombine(completablefuture2.thenCombine(completablefuture3, Pair::of), (p_215296_0_, p_215296_1_) -> {
         return new NetworkTagManager.ReloadResults(p_215296_0_.getFirst(), p_215296_0_.getSecond(), p_215296_1_.getFirst(), p_215296_1_.getSecond());
      }).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_215298_1_) -> {
         this.blocks.registerAll(p_215298_1_.blocks);
         this.items.registerAll(p_215298_1_.items);
         this.fluids.registerAll(p_215298_1_.fluids);
         this.entityTypes.registerAll(p_215298_1_.entityTypes);
         BlockTags.setCollection(this.blocks);
         ItemTags.setCollection(this.items);
         FluidTags.setCollection(this.fluids);
         EntityTypeTags.setCollection(this.entityTypes);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent(this));
      }, gameExecutor);
   }

   public static class ReloadResults {
      final Map<ResourceLocation, Tag.Builder<Block>> blocks;
      final Map<ResourceLocation, Tag.Builder<Item>> items;
      final Map<ResourceLocation, Tag.Builder<Fluid>> fluids;
      final Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes;

      public ReloadResults(Map<ResourceLocation, Tag.Builder<Block>> blocks, Map<ResourceLocation, Tag.Builder<Item>> items, Map<ResourceLocation, Tag.Builder<Fluid>> fluids, Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes) {
         this.blocks = blocks;
         this.items = items;
         this.fluids = fluids;
         this.entityTypes = entityTypes;
      }
   }
}