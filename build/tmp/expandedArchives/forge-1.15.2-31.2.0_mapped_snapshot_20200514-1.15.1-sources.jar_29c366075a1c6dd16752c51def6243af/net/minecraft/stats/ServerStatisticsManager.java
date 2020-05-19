package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatisticsManager extends StatisticsManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File statsFile;
   private final Set<Stat<?>> dirty = Sets.newHashSet();
   private int lastStatRequest = -300;

   public ServerStatisticsManager(MinecraftServer serverIn, File statsFileIn) {
      this.server = serverIn;
      this.statsFile = statsFileIn;
      if (statsFileIn.isFile()) {
         try {
            this.parseLocal(serverIn.getDataFixer(), FileUtils.readFileToString(statsFileIn));
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't read statistics file {}", statsFileIn, ioexception);
         } catch (JsonParseException jsonparseexception) {
            LOGGER.error("Couldn't parse statistics file {}", statsFileIn, jsonparseexception);
         }
      }

   }

   public void saveStatFile() {
      try {
         FileUtils.writeStringToFile(this.statsFile, this.func_199061_b());
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save stats", (Throwable)ioexception);
      }

   }

   /**
    * Triggers the logging of an achievement and attempts to announce to server
    */
   public void setValue(PlayerEntity playerIn, Stat<?> statIn, int p_150873_3_) {
      super.setValue(playerIn, statIn, p_150873_3_);
      this.dirty.add(statIn);
   }

   private Set<Stat<?>> getDirty() {
      Set<Stat<?>> set = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return set;
   }

   public void parseLocal(DataFixer p_199062_1_, String p_199062_2_) {
      try (JsonReader jsonreader = new JsonReader(new StringReader(p_199062_2_))) {
         jsonreader.setLenient(false);
         JsonElement jsonelement = Streams.parse(jsonreader);
         if (jsonelement.isJsonNull()) {
            LOGGER.error("Unable to parse Stat data from {}", (Object)this.statsFile);
            return;
         }

         CompoundNBT compoundnbt = func_199065_a(jsonelement.getAsJsonObject());
         if (!compoundnbt.contains("DataVersion", 99)) {
            compoundnbt.putInt("DataVersion", 1343);
         }

         compoundnbt = NBTUtil.update(p_199062_1_, DefaultTypeReferences.STATS, compoundnbt, compoundnbt.getInt("DataVersion"));
         if (compoundnbt.contains("stats", 10)) {
            CompoundNBT compoundnbt1 = compoundnbt.getCompound("stats");

            for(String s : compoundnbt1.keySet()) {
               if (compoundnbt1.contains(s, 10)) {
                  Util.acceptOrElse(Registry.STATS.getValue(new ResourceLocation(s)), (p_219731_3_) -> {
                     CompoundNBT compoundnbt2 = compoundnbt1.getCompound(s);

                     for(String s1 : compoundnbt2.keySet()) {
                        if (compoundnbt2.contains(s1, 99)) {
                           Util.acceptOrElse(this.func_219728_a(p_219731_3_, s1), (p_219730_3_) -> {
                              this.statsData.put(p_219730_3_, compoundnbt2.getInt(s1));
                           }, () -> {
                              LOGGER.warn("Invalid statistic in {}: Don't know what {} is", this.statsFile, s1);
                           });
                        } else {
                           LOGGER.warn("Invalid statistic value in {}: Don't know what {} is for key {}", this.statsFile, compoundnbt2.get(s1), s1);
                        }
                     }

                  }, () -> {
                     LOGGER.warn("Invalid statistic type in {}: Don't know what {} is", this.statsFile, s);
                  });
               }
            }
         }
      } catch (IOException | JsonParseException jsonparseexception) {
         LOGGER.error("Unable to parse Stat data from {}", this.statsFile, jsonparseexception);
      }

   }

   private <T> Optional<Stat<T>> func_219728_a(StatType<T> p_219728_1_, String p_219728_2_) {
      return Optional.ofNullable(ResourceLocation.tryCreate(p_219728_2_)).flatMap(p_219728_1_.getRegistry()::getValue).map(p_219728_1_::get);
   }

   private static CompoundNBT func_199065_a(JsonObject p_199065_0_) {
      CompoundNBT compoundnbt = new CompoundNBT();

      for(Entry<String, JsonElement> entry : p_199065_0_.entrySet()) {
         JsonElement jsonelement = entry.getValue();
         if (jsonelement.isJsonObject()) {
            compoundnbt.put(entry.getKey(), func_199065_a(jsonelement.getAsJsonObject()));
         } else if (jsonelement.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = jsonelement.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               compoundnbt.putInt(entry.getKey(), jsonprimitive.getAsInt());
            }
         }
      }

      return compoundnbt;
   }

   protected String func_199061_b() {
      Map<StatType<?>, JsonObject> map = Maps.newHashMap();

      for(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> entry : this.statsData.object2IntEntrySet()) {
         Stat<?> stat = entry.getKey();
         map.computeIfAbsent(stat.getType(), (p_199064_0_) -> {
            return new JsonObject();
         }).addProperty(func_199066_b(stat).toString(), entry.getIntValue());
      }

      JsonObject jsonobject = new JsonObject();

      for(Entry<StatType<?>, JsonObject> entry1 : map.entrySet()) {
         jsonobject.add(Registry.STATS.getKey(entry1.getKey()).toString(), entry1.getValue());
      }

      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.add("stats", jsonobject);
      jsonobject1.addProperty("DataVersion", SharedConstants.getVersion().getWorldVersion());
      return jsonobject1.toString();
   }

   private static <T> ResourceLocation func_199066_b(Stat<T> p_199066_0_) {
      return p_199066_0_.getType().getRegistry().getKey(p_199066_0_.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.statsData.keySet());
   }

   public void sendStats(ServerPlayerEntity player) {
      int i = this.server.getTickCounter();
      Object2IntMap<Stat<?>> object2intmap = new Object2IntOpenHashMap<>();
      if (i - this.lastStatRequest > 300) {
         this.lastStatRequest = i;

         for(Stat<?> stat : this.getDirty()) {
            object2intmap.put(stat, this.getValue(stat));
         }
      }

      player.connection.sendPacket(new SStatisticsPacket(object2intmap));
   }
}