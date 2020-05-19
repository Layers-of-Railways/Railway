package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionSavedDataManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, WorldSavedData> savedDatum = Maps.newHashMap();
   private final DataFixer dataFixer;
   private final File folder;

   public DimensionSavedDataManager(File dataFolder, DataFixer dataFixerIn) {
      this.dataFixer = dataFixerIn;
      this.folder = dataFolder;
   }

   private File getDataFile(String name) {
      return new File(this.folder, name + ".dat");
   }

   public <T extends WorldSavedData> T getOrCreate(Supplier<T> defaultSupplier, String name) {
      T t = this.get(defaultSupplier, name);
      if (t != null) {
         return t;
      } else {
         T t1 = defaultSupplier.get();
         this.set(t1);
         return t1;
      }
   }

   @Nullable
   public <T extends WorldSavedData> T get(Supplier<T> defaultSupplier, String name) {
      WorldSavedData worldsaveddata = this.savedDatum.get(name);
      if (worldsaveddata == net.minecraftforge.common.util.DummyWorldSaveData.DUMMY) return null;
      if (worldsaveddata == null && !this.savedDatum.containsKey(name)) {
         worldsaveddata = this.<T>func_223409_c(defaultSupplier, name);
         this.savedDatum.put(name, worldsaveddata);
      } else if (worldsaveddata == null) {
         this.savedDatum.put(name, net.minecraftforge.common.util.DummyWorldSaveData.DUMMY);
         return null;
      }

      return (T)worldsaveddata;
   }

   @Nullable
   private <T extends WorldSavedData> T func_223409_c(Supplier<T> p_223409_1_, String p_223409_2_) {
      try {
         File file1 = this.getDataFile(p_223409_2_);
         if (file1.exists()) {
            T t = p_223409_1_.get();
            CompoundNBT compoundnbt = this.load(p_223409_2_, SharedConstants.getVersion().getWorldVersion());
            t.read(compoundnbt.getCompound("data"));
            return t;
         }
      } catch (Exception exception) {
         LOGGER.error("Error loading saved data: {}", p_223409_2_, exception);
      }

      return (T)null;
   }

   public void set(WorldSavedData data) {
      this.savedDatum.put(data.getName(), data);
   }

   public CompoundNBT load(String name, int worldVersion) throws IOException {
      File file1 = this.getDataFile(name);

      CompoundNBT compoundnbt1;
      try (PushbackInputStream pushbackinputstream = new PushbackInputStream(new FileInputStream(file1), 2)) {
         CompoundNBT compoundnbt;
         if (this.isCompressed(pushbackinputstream)) {
            compoundnbt = CompressedStreamTools.readCompressed(pushbackinputstream);
         } else {
            try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
               compoundnbt = CompressedStreamTools.read(datainputstream);
            }
         }

         int i = compoundnbt.contains("DataVersion", 99) ? compoundnbt.getInt("DataVersion") : 1343;
         compoundnbt1 = NBTUtil.update(this.dataFixer, DefaultTypeReferences.SAVED_DATA, compoundnbt, i, worldVersion);
      }

      return compoundnbt1;
   }

   private boolean isCompressed(PushbackInputStream inputStream) throws IOException {
      byte[] abyte = new byte[2];
      boolean flag = false;
      int i = inputStream.read(abyte, 0, 2);
      if (i == 2) {
         int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
         if (j == 35615) {
            flag = true;
         }
      }

      if (i != 0) {
         inputStream.unread(abyte, 0, i);
      }

      return flag;
   }

   public void save() {
      for(WorldSavedData worldsaveddata : this.savedDatum.values()) {
         if (worldsaveddata != null) {
            worldsaveddata.save(this.getDataFile(worldsaveddata.getName()));
         }
      }

   }
}