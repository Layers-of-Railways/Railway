package net.minecraft.advancements;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> MAP_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final MinecraftServer server;
   private final File progressFile;
   private final Map<Advancement, AdvancementProgress> progress = Maps.newLinkedHashMap();
   private final Set<Advancement> visible = Sets.newLinkedHashSet();
   private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
   private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
   private ServerPlayerEntity player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(MinecraftServer server, File progressFileIn, ServerPlayerEntity player) {
      this.server = server;
      this.progressFile = progressFileIn;
      this.player = player;
      this.load();
   }

   public void setPlayer(ServerPlayerEntity player) {
      this.player = player;
   }

   public void dispose() {
      for(ICriterionTrigger<?> icriteriontrigger : CriteriaTriggers.getAll()) {
         icriteriontrigger.removeAllListeners(this);
      }

   }

   public void reload() {
      this.dispose();
      this.progress.clear();
      this.visible.clear();
      this.visibilityChanged.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load();
   }

   private void registerListeners() {
      for(Advancement advancement : this.server.getAdvancementManager().getAllAdvancements()) {
         this.registerListeners(advancement);
      }

   }

   private void ensureAllVisible() {
      List<Advancement> list = Lists.newArrayList();

      for(Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
         if (entry.getValue().isDone()) {
            list.add(entry.getKey());
            this.progressChanged.add(entry.getKey());
         }
      }

      for(Advancement advancement : list) {
         this.ensureVisibility(advancement);
      }

   }

   private void checkForAutomaticTriggers() {
      for(Advancement advancement : this.server.getAdvancementManager().getAllAdvancements()) {
         if (advancement.getCriteria().isEmpty()) {
            this.grantCriterion(advancement, "");
            advancement.getRewards().apply(this.player);
         }
      }

   }

   private void load() {
      if (this.progressFile.isFile()) {
         try (JsonReader jsonreader = new JsonReader(new StringReader(Files.toString(this.progressFile, StandardCharsets.UTF_8)))) {
            jsonreader.setLenient(false);
            Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(jsonreader));
            if (!dynamic.get("DataVersion").asNumber().isPresent()) {
               dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
            }

            dynamic = this.server.getDataFixer().update(DefaultTypeReferences.ADVANCEMENTS.func_219816_a(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getVersion().getWorldVersion());
            dynamic = dynamic.remove("DataVersion");
            Map<ResourceLocation, AdvancementProgress> map = GSON.getAdapter(MAP_TOKEN).fromJsonTree(dynamic.getValue());
            if (map == null) {
               throw new JsonParseException("Found null for advancements");
            }

            Stream<Entry<ResourceLocation, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));

            for(Entry<ResourceLocation, AdvancementProgress> entry : stream.collect(Collectors.toList())) {
               Advancement advancement = this.server.getAdvancementManager().getAdvancement(entry.getKey());
               if (advancement == null) {
                  LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.progressFile);
               } else {
                  this.startProgress(advancement, entry.getValue());
               }
            }
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse player advancements in {}", this.progressFile, jsonparseexception);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't access player advancements in {}", this.progressFile, ioexception);
         }
      }

      this.checkForAutomaticTriggers();
      if (net.minecraftforge.common.ForgeConfig.SERVER.fixAdvancementLoading.get()) net.minecraftforge.common.AdvancementLoadFix.loadVisibility(this, this.visible, this.visibilityChanged, this.progress, this.progressChanged, this::shouldBeVisible); else
      this.ensureAllVisible();
      this.registerListeners();
   }

   public void save() {
      Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();

      for(Entry<Advancement, AdvancementProgress> entry : this.progress.entrySet()) {
         AdvancementProgress advancementprogress = entry.getValue();
         if (advancementprogress.hasProgress()) {
            map.put(entry.getKey().getId(), advancementprogress);
         }
      }

      if (this.progressFile.getParentFile() != null) {
         this.progressFile.getParentFile().mkdirs();
      }

      JsonElement jsonelement = GSON.toJsonTree(map);
      jsonelement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getVersion().getWorldVersion());

      try (
         OutputStream outputstream = new FileOutputStream(this.progressFile);
         Writer writer = new OutputStreamWriter(outputstream, Charsets.UTF_8.newEncoder());
      ) {
         GSON.toJson(jsonelement, writer);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save player advancements to {}", this.progressFile, ioexception);
      }

   }

   public boolean grantCriterion(Advancement advancementIn, String criterionKey) {
      // Forge: don't grant advancements for fake players
      if (this.player instanceof net.minecraftforge.common.util.FakePlayer) return false;
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getProgress(advancementIn);
      boolean flag1 = advancementprogress.isDone();
      if (advancementprogress.grantCriterion(criterionKey)) {
         this.unregisterListeners(advancementIn);
         this.progressChanged.add(advancementIn);
         flag = true;
         if (!flag1 && advancementprogress.isDone()) {
            advancementIn.getRewards().apply(this.player);
            if (advancementIn.getDisplay() != null && advancementIn.getDisplay().shouldAnnounceToChat() && this.player.world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
               this.server.getPlayerList().sendMessage(new TranslationTextComponent("chat.type.advancement." + advancementIn.getDisplay().getFrame().getName(), this.player.getDisplayName(), advancementIn.getDisplayText()));
            }
            net.minecraftforge.common.ForgeHooks.onAdvancement(this.player, advancementIn);
         }
      }

      if (advancementprogress.isDone()) {
         this.ensureVisibility(advancementIn);
      }

      return flag;
   }

   public boolean revokeCriterion(Advancement advancementIn, String criterionKey) {
      boolean flag = false;
      AdvancementProgress advancementprogress = this.getProgress(advancementIn);
      if (advancementprogress.revokeCriterion(criterionKey)) {
         this.registerListeners(advancementIn);
         this.progressChanged.add(advancementIn);
         flag = true;
      }

      if (!advancementprogress.hasProgress()) {
         this.ensureVisibility(advancementIn);
      }

      return flag;
   }

   private void registerListeners(Advancement advancementIn) {
      AdvancementProgress advancementprogress = this.getProgress(advancementIn);
      if (!advancementprogress.isDone()) {
         for(Entry<String, Criterion> entry : advancementIn.getCriteria().entrySet()) {
            CriterionProgress criterionprogress = advancementprogress.getCriterionProgress(entry.getKey());
            if (criterionprogress != null && !criterionprogress.isObtained()) {
               ICriterionInstance icriterioninstance = entry.getValue().getCriterionInstance();
               if (icriterioninstance != null) {
                  ICriterionTrigger<ICriterionInstance> icriteriontrigger = CriteriaTriggers.get(icriterioninstance.getId());
                  if (icriteriontrigger != null) {
                     icriteriontrigger.addListener(this, new ICriterionTrigger.Listener<>(icriterioninstance, advancementIn, entry.getKey()));
                  }
               }
            }
         }

      }
   }

   private void unregisterListeners(Advancement advancementIn) {
      AdvancementProgress advancementprogress = this.getProgress(advancementIn);

      for(Entry<String, Criterion> entry : advancementIn.getCriteria().entrySet()) {
         CriterionProgress criterionprogress = advancementprogress.getCriterionProgress(entry.getKey());
         if (criterionprogress != null && (criterionprogress.isObtained() || advancementprogress.isDone())) {
            ICriterionInstance icriterioninstance = entry.getValue().getCriterionInstance();
            if (icriterioninstance != null) {
               ICriterionTrigger<ICriterionInstance> icriteriontrigger = CriteriaTriggers.get(icriterioninstance.getId());
               if (icriteriontrigger != null) {
                  icriteriontrigger.removeListener(this, new ICriterionTrigger.Listener<>(icriterioninstance, advancementIn, entry.getKey()));
               }
            }
         }
      }

   }

   public void flushDirty(ServerPlayerEntity p_192741_1_) {
      if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
         Map<ResourceLocation, AdvancementProgress> map = Maps.newHashMap();
         Set<Advancement> set = Sets.newLinkedHashSet();
         Set<ResourceLocation> set1 = Sets.newLinkedHashSet();

         for(Advancement advancement : this.progressChanged) {
            if (this.visible.contains(advancement)) {
               map.put(advancement.getId(), this.progress.get(advancement));
            }
         }

         for(Advancement advancement1 : this.visibilityChanged) {
            if (this.visible.contains(advancement1)) {
               set.add(advancement1);
            } else {
               set1.add(advancement1.getId());
            }
         }

         if (this.isFirstPacket || !map.isEmpty() || !set.isEmpty() || !set1.isEmpty()) {
            p_192741_1_.connection.sendPacket(new SAdvancementInfoPacket(this.isFirstPacket, set, set1, map));
            this.visibilityChanged.clear();
            this.progressChanged.clear();
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable Advancement p_194220_1_) {
      Advancement advancement = this.lastSelectedTab;
      if (p_194220_1_ != null && p_194220_1_.getParent() == null && p_194220_1_.getDisplay() != null) {
         this.lastSelectedTab = p_194220_1_;
      } else {
         this.lastSelectedTab = null;
      }

      if (advancement != this.lastSelectedTab) {
         this.player.connection.sendPacket(new SSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
      }

   }

   public AdvancementProgress getProgress(Advancement advancementIn) {
      AdvancementProgress advancementprogress = this.progress.get(advancementIn);
      if (advancementprogress == null) {
         advancementprogress = new AdvancementProgress();
         this.startProgress(advancementIn, advancementprogress);
      }

      return advancementprogress;
   }

   private void startProgress(Advancement advancementIn, AdvancementProgress p_192743_2_) {
      p_192743_2_.update(advancementIn.getCriteria(), advancementIn.getRequirements());
      this.progress.put(advancementIn, p_192743_2_);
   }

   private void ensureVisibility(Advancement p_192742_1_) {
      boolean flag = this.shouldBeVisible(p_192742_1_);
      boolean flag1 = this.visible.contains(p_192742_1_);
      if (flag && !flag1) {
         this.visible.add(p_192742_1_);
         this.visibilityChanged.add(p_192742_1_);
         if (this.progress.containsKey(p_192742_1_)) {
            this.progressChanged.add(p_192742_1_);
         }
      } else if (!flag && flag1) {
         this.visible.remove(p_192742_1_);
         this.visibilityChanged.add(p_192742_1_);
      }

      if (flag != flag1 && p_192742_1_.getParent() != null) {
         this.ensureVisibility(p_192742_1_.getParent());
      }

      for(Advancement advancement : p_192742_1_.getChildren()) {
         this.ensureVisibility(advancement);
      }

   }

   private boolean shouldBeVisible(Advancement p_192738_1_) {
      for(int i = 0; p_192738_1_ != null && i <= 2; ++i) {
         if (i == 0 && this.hasCompletedChildrenOrSelf(p_192738_1_)) {
            return true;
         }

         if (p_192738_1_.getDisplay() == null) {
            return false;
         }

         AdvancementProgress advancementprogress = this.getProgress(p_192738_1_);
         if (advancementprogress.isDone()) {
            return true;
         }

         if (p_192738_1_.getDisplay().isHidden()) {
            return false;
         }

         p_192738_1_ = p_192738_1_.getParent();
      }

      return false;
   }

   private boolean hasCompletedChildrenOrSelf(Advancement p_192746_1_) {
      AdvancementProgress advancementprogress = this.getProgress(p_192746_1_);
      if (advancementprogress.isDone()) {
         return true;
      } else {
         for(Advancement advancement : p_192746_1_.getChildren()) {
            if (this.hasCompletedChildrenOrSelf(advancement)) {
               return true;
            }
         }

         return false;
      }
   }
}