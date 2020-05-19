package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements IPlayerFileData {
   private static final Logger LOGGER = LogManager.getLogger();
   private final File worldDirectory;
   private final File playersDirectory;
   private final long field_215776_e = Util.milliTime();
   private final String worldId;
   private final TemplateManager templateManager;
   protected final DataFixer fixerUpper;

   public SaveHandler(File p_i51278_1_, String p_i51278_2_, @Nullable MinecraftServer p_i51278_3_, DataFixer p_i51278_4_) {
      this.fixerUpper = p_i51278_4_;
      this.worldDirectory = new File(p_i51278_1_, p_i51278_2_);
      this.worldDirectory.mkdirs();
      this.playersDirectory = new File(this.worldDirectory, "playerdata");
      this.worldId = p_i51278_2_;
      if (p_i51278_3_ != null) {
         this.playersDirectory.mkdirs();
         this.templateManager = new TemplateManager(p_i51278_3_, this.worldDirectory, p_i51278_4_);
      } else {
         this.templateManager = null;
      }

      this.func_215770_h();
   }

   /**
    * Saves the given World Info with the given NBTTagCompound as the Player.
    */
   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable CompoundNBT tagCompound) {
      worldInformation.setSaveVersion(19133);
      CompoundNBT compoundnbt = worldInformation.cloneNBTCompound(tagCompound);
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt1.put("Data", compoundnbt);

      net.minecraftforge.fml.WorldPersistenceHooks.handleWorldDataSave(this, worldInformation, compoundnbt1);

      try {
         File file1 = new File(this.worldDirectory, "level.dat_new");
         File file2 = new File(this.worldDirectory, "level.dat_old");
         File file3 = new File(this.worldDirectory, "level.dat");
         CompressedStreamTools.writeCompressed(compoundnbt1, new FileOutputStream(file1));
         if (file2.exists()) {
            file2.delete();
         }

         file3.renameTo(file2);
         if (file3.exists()) {
            file3.delete();
         }

         file1.renameTo(file3);
         if (file1.exists()) {
            file1.delete();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

   }

   private void func_215770_h() {
      try {
         File file1 = new File(this.worldDirectory, "session.lock");
         DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));

         try {
            dataoutputstream.writeLong(this.field_215776_e);
         } finally {
            dataoutputstream.close();
         }

      } catch (IOException ioexception) {
         ioexception.printStackTrace();
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   /**
    * Gets the File object corresponding to the base directory of this world.
    */
   public File getWorldDirectory() {
      return this.worldDirectory;
   }

   /**
    * Checks the session lock to prevent save collisions
    */
   public void checkSessionLock() throws SessionLockException {
      try {
         File file1 = new File(this.worldDirectory, "session.lock");
         DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));

         try {
            if (datainputstream.readLong() != this.field_215776_e) {
               throw new SessionLockException("The save is being accessed from another location, aborting");
            }
         } finally {
            datainputstream.close();
         }

      } catch (IOException var7) {
         throw new SessionLockException("Failed to check session lock, aborting");
      }
   }

   /**
    * Loads and returns the world info
    */
   @Nullable
   public WorldInfo loadWorldInfo() {
      File file1 = new File(this.worldDirectory, "level.dat");
      if (file1.exists()) {
         WorldInfo worldinfo = SaveFormat.getWorldData(file1, this.fixerUpper, this);
         if (worldinfo != null) {
            return worldinfo;
         }
      }

      file1 = new File(this.worldDirectory, "level.dat_old");
      return file1.exists() ? SaveFormat.getWorldData(file1, this.fixerUpper, this) : null;
   }

   /**
    * used to update level.dat from old format to MCRegion format
    */
   public void saveWorldInfo(WorldInfo worldInformation) {
      this.saveWorldInfoWithPlayer(worldInformation, (CompoundNBT)null);
   }

   /**
    * Writes the player data to disk from the specified PlayerEntityMP.
    */
   public void writePlayerData(PlayerEntity player) {
      try {
         CompoundNBT compoundnbt = player.writeWithoutTypeId(new CompoundNBT());
         File file1 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat.tmp");
         File file2 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         CompressedStreamTools.writeCompressed(compoundnbt, new FileOutputStream(file1));
         if (file2.exists()) {
            file2.delete();
         }

         file1.renameTo(file2);
         net.minecraftforge.event.ForgeEventFactory.firePlayerSavingEvent(player, playersDirectory, player.getUniqueID().toString());
      } catch (Exception var5) {
         LOGGER.warn("Failed to save player data for {}", (Object)player.getName().getString());
      }

   }

   /**
    * Reads the player data from disk into the specified PlayerEntityMP.
    */
   @Nullable
   public CompoundNBT readPlayerData(PlayerEntity player) {
      CompoundNBT compoundnbt = null;

      try {
         File file1 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         if (file1.exists() && file1.isFile()) {
            compoundnbt = CompressedStreamTools.readCompressed(new FileInputStream(file1));
         }
      } catch (Exception var4) {
         LOGGER.warn("Failed to load player data for {}", (Object)player.getName().getString());
      }

      if (compoundnbt != null) {
         int i = compoundnbt.contains("DataVersion", 3) ? compoundnbt.getInt("DataVersion") : -1;
         player.read(NBTUtil.update(this.fixerUpper, DefaultTypeReferences.PLAYER, compoundnbt, i));
      }
      net.minecraftforge.event.ForgeEventFactory.firePlayerLoadingEvent(player, playersDirectory, player.getUniqueID().toString());

      return compoundnbt;
   }

   public String[] func_215771_d() {
      String[] astring = this.playersDirectory.list();
      if (astring == null) {
         astring = new String[0];
      }

      for(int i = 0; i < astring.length; ++i) {
         if (astring[i].endsWith(".dat")) {
            astring[i] = astring[i].substring(0, astring[i].length() - 4);
         }
      }

      return astring;
   }

   public TemplateManager getStructureTemplateManager() {
      return this.templateManager;
   }

   public DataFixer getFixer() {
      return this.fixerUpper;
   }

   public CompoundNBT getPlayerNBT(net.minecraft.entity.player.ServerPlayerEntity player) {
      try {
         File file1 = new File(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
         if (file1.exists() && file1.isFile()) {
            CompoundNBT nbt = CompressedStreamTools.readCompressed(new FileInputStream(file1));
            if (nbt != null) {
               nbt = NBTUtil.update(this.fixerUpper, DefaultTypeReferences.PLAYER, nbt, nbt.contains("DataVersion", 3) ? nbt.getInt("DataVersion") : -1);
            }
            return nbt;
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed to load player data for " + player.getName());
      }
      return null;
   }

   public File getPlayerFolder() {
      return playersDirectory;
   }
}