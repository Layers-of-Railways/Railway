package net.minecraft.client.settings;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class CreativeSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File dataFile;
   private final DataFixer dataFixer;
   private final HotbarSnapshot[] hotbarSnapshots = new HotbarSnapshot[9];
   private boolean loaded;

   public CreativeSettings(File dataPath, DataFixer dataFixerIn) {
      this.dataFile = new File(dataPath, "hotbar.nbt");
      this.dataFixer = dataFixerIn;

      for(int i = 0; i < 9; ++i) {
         this.hotbarSnapshots[i] = new HotbarSnapshot();
      }

   }

   private void load() {
      try {
         CompoundNBT compoundnbt = CompressedStreamTools.read(this.dataFile);
         if (compoundnbt == null) {
            return;
         }

         if (!compoundnbt.contains("DataVersion", 99)) {
            compoundnbt.putInt("DataVersion", 1343);
         }

         compoundnbt = NBTUtil.update(this.dataFixer, DefaultTypeReferences.HOTBAR, compoundnbt, compoundnbt.getInt("DataVersion"));

         for(int i = 0; i < 9; ++i) {
            this.hotbarSnapshots[i].fromTag(compoundnbt.getList(String.valueOf(i), 10));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to load creative mode options", (Throwable)exception);
      }

   }

   public void save() {
      try {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());

         for(int i = 0; i < 9; ++i) {
            compoundnbt.put(String.valueOf(i), this.getHotbarSnapshot(i).createTag());
         }

         CompressedStreamTools.write(compoundnbt, this.dataFile);
      } catch (Exception exception) {
         LOGGER.error("Failed to save creative mode options", (Throwable)exception);
      }

   }

   public HotbarSnapshot getHotbarSnapshot(int index) {
      if (!this.loaded) {
         this.load();
         this.loaded = true;
      }

      return this.hotbarSnapshots[index];
   }
}