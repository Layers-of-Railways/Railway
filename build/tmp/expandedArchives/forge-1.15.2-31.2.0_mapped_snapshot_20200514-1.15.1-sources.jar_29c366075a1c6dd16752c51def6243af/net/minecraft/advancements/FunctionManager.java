package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation TICK_TAG_ID = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_TAG_ID = new ResourceLocation("load");
   public static final int PATH_PREFIX_LENGTH = "functions/".length();
   public static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
   private final MinecraftServer server;
   private final Map<ResourceLocation, FunctionObject> functions = Maps.newHashMap();
   private boolean isExecuting;
   private final ArrayDeque<FunctionManager.QueuedCommand> commandQueue = new ArrayDeque<>();
   private final List<FunctionManager.QueuedCommand> commandChain = Lists.newArrayList();
   private final TagCollection<FunctionObject> tagCollection = new TagCollection<>(this::get, "tags/functions", true, "function");
   private final List<FunctionObject> tickFunctions = Lists.newArrayList();
   private boolean loadFunctionsRun;

   public FunctionManager(MinecraftServer p_i47920_1_) {
      this.server = p_i47920_1_;
   }

   public Optional<FunctionObject> get(ResourceLocation p_215361_1_) {
      return Optional.ofNullable(this.functions.get(p_215361_1_));
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public int getMaxCommandChainLength() {
      return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
   }

   public Map<ResourceLocation, FunctionObject> getFunctions() {
      return this.functions;
   }

   public CommandDispatcher<CommandSource> getCommandDispatcher() {
      return this.server.getCommandManager().getDispatcher();
   }

   public void tick() {
      this.server.getProfiler().startSection(TICK_TAG_ID::toString);

      for(FunctionObject functionobject : this.tickFunctions) {
         this.execute(functionobject, this.getCommandSource());
      }

      this.server.getProfiler().endSection();
      if (this.loadFunctionsRun) {
         this.loadFunctionsRun = false;
         Collection<FunctionObject> collection = this.getTagCollection().getOrCreate(LOAD_TAG_ID).getAllElements();
         this.server.getProfiler().startSection(LOAD_TAG_ID::toString);

         for(FunctionObject functionobject1 : collection) {
            this.execute(functionobject1, this.getCommandSource());
         }

         this.server.getProfiler().endSection();
      }

   }

   public int execute(FunctionObject p_195447_1_, CommandSource p_195447_2_) {
      int i = this.getMaxCommandChainLength();
      if (this.isExecuting) {
         if (this.commandQueue.size() + this.commandChain.size() < i) {
            this.commandChain.add(new FunctionManager.QueuedCommand(this, p_195447_2_, new FunctionObject.FunctionEntry(p_195447_1_)));
         }

         return 0;
      } else {
         try {
            this.isExecuting = true;
            int j = 0;
            FunctionObject.IEntry[] afunctionobject$ientry = p_195447_1_.getEntries();

            for(int k = afunctionobject$ientry.length - 1; k >= 0; --k) {
               this.commandQueue.push(new FunctionManager.QueuedCommand(this, p_195447_2_, afunctionobject$ientry[k]));
            }

            while(!this.commandQueue.isEmpty()) {
               try {
                  FunctionManager.QueuedCommand functionmanager$queuedcommand = this.commandQueue.removeFirst();
                  this.server.getProfiler().startSection(functionmanager$queuedcommand::toString);
                  functionmanager$queuedcommand.execute(this.commandQueue, i);
                  if (!this.commandChain.isEmpty()) {
                     Lists.reverse(this.commandChain).forEach(this.commandQueue::addFirst);
                     this.commandChain.clear();
                  }
               } finally {
                  this.server.getProfiler().endSection();
               }

               ++j;
               if (j >= i) {
                  return j;
               }
            }

            return j;
         } finally {
            this.commandQueue.clear();
            this.commandChain.clear();
            this.isExecuting = false;
         }
      }
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.functions.clear();
      this.tickFunctions.clear();
      Collection<ResourceLocation> collection = resourceManager.getAllResourceLocations("functions", (p_215364_0_) -> {
         return p_215364_0_.endsWith(".mcfunction");
      });
      List<CompletableFuture<FunctionObject>> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : collection) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(PATH_PREFIX_LENGTH, s.length() - PATH_SUFFIX_LENGTH));
         list.add(CompletableFuture.supplyAsync(() -> {
            return readLines(resourceManager, resourcelocation);
         }, SimpleResource.RESOURCE_IO_EXECUTOR).thenApplyAsync((p_215365_2_) -> {
            return FunctionObject.create(resourcelocation1, this, p_215365_2_);
         }, this.server.getBackgroundExecutor()).handle((p_215362_2_, p_215362_3_) -> {
            return this.load(p_215362_2_, p_215362_3_, resourcelocation);
         }));
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      if (!this.functions.isEmpty()) {
         LOGGER.info("Loaded {} custom command functions", (int)this.functions.size());
      }

      this.tagCollection.registerAll(this.tagCollection.reload(resourceManager, this.server.getBackgroundExecutor()).join());
      this.tickFunctions.addAll(this.tagCollection.getOrCreate(TICK_TAG_ID).getAllElements());
      this.loadFunctionsRun = true;
   }

   @Nullable
   private FunctionObject load(FunctionObject function, @Nullable Throwable error, ResourceLocation id) {
      if (error != null) {
         LOGGER.error("Couldn't load function at {}", id, error);
         return null;
      } else {
         synchronized(this.functions) {
            this.functions.put(function.getId(), function);
            return function;
         }
      }
   }

   private static List<String> readLines(IResourceManager p_195449_0_, ResourceLocation p_195449_1_) {
      try (IResource iresource = p_195449_0_.getResource(p_195449_1_)) {
         List list = IOUtils.readLines(iresource.getInputStream(), StandardCharsets.UTF_8);
         return list;
      } catch (IOException ioexception) {
         throw new CompletionException(ioexception);
      }
   }

   public CommandSource getCommandSource() {
      return this.server.getCommandSource().withPermissionLevel(2).withFeedbackDisabled();
   }

   public CommandSource func_223402_g() {
      return new CommandSource(ICommandSource.DUMMY, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld)null, this.server.getFunctionLevel(), "", new StringTextComponent(""), this.server, (Entity)null);
   }

   public TagCollection<FunctionObject> getTagCollection() {
      return this.tagCollection;
   }

   public static class QueuedCommand {
      private final FunctionManager functionManager;
      private final CommandSource sender;
      private final FunctionObject.IEntry entry;

      public QueuedCommand(FunctionManager p_i48018_1_, CommandSource p_i48018_2_, FunctionObject.IEntry p_i48018_3_) {
         this.functionManager = p_i48018_1_;
         this.sender = p_i48018_2_;
         this.entry = p_i48018_3_;
      }

      public void execute(ArrayDeque<FunctionManager.QueuedCommand> commandQueue, int maxCommandChainLength) {
         try {
            this.entry.execute(this.functionManager, this.sender, commandQueue, maxCommandChainLength);
         } catch (Throwable var4) {
            ;
         }

      }

      public String toString() {
         return this.entry.toString();
      }
   }
}