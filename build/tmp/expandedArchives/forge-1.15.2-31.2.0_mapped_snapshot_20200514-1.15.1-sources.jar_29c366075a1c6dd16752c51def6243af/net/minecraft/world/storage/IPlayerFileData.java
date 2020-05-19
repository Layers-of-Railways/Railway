package net.minecraft.world.storage;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IPlayerFileData {
   /**
    * Writes the player data to disk from the specified PlayerEntityMP.
    */
   void writePlayerData(PlayerEntity player);

   /**
    * Reads the player data from disk into the specified PlayerEntityMP.
    */
   @Nullable
   CompoundNBT readPlayerData(PlayerEntity player);
}