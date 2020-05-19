package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NBTUtil {
   private static final Logger LOGGER = LogManager.getLogger();

   /**
    * Reads and returns a GameProfile that has been saved to the passed in NBTTagCompound
    */
   @Nullable
   public static GameProfile readGameProfile(CompoundNBT compound) {
      String s = null;
      String s1 = null;
      if (compound.contains("Name", 8)) {
         s = compound.getString("Name");
      }

      if (compound.contains("Id", 8)) {
         s1 = compound.getString("Id");
      }

      try {
         UUID uuid;
         try {
            uuid = UUID.fromString(s1);
         } catch (Throwable var12) {
            uuid = null;
         }

         GameProfile gameprofile = new GameProfile(uuid, s);
         if (compound.contains("Properties", 10)) {
            CompoundNBT compoundnbt = compound.getCompound("Properties");

            for(String s2 : compoundnbt.keySet()) {
               ListNBT listnbt = compoundnbt.getList(s2, 10);

               for(int i = 0; i < listnbt.size(); ++i) {
                  CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                  String s3 = compoundnbt1.getString("Value");
                  if (compoundnbt1.contains("Signature", 8)) {
                     gameprofile.getProperties().put(s2, new Property(s2, s3, compoundnbt1.getString("Signature")));
                  } else {
                     gameprofile.getProperties().put(s2, new Property(s2, s3));
                  }
               }
            }
         }

         return gameprofile;
      } catch (Throwable var13) {
         return null;
      }
   }

   /**
    * Writes a GameProfile to an NBTTagCompound.
    */
   public static CompoundNBT writeGameProfile(CompoundNBT tagCompound, GameProfile profile) {
      if (!StringUtils.isNullOrEmpty(profile.getName())) {
         tagCompound.putString("Name", profile.getName());
      }

      if (profile.getId() != null) {
         tagCompound.putString("Id", profile.getId().toString());
      }

      if (!profile.getProperties().isEmpty()) {
         CompoundNBT compoundnbt = new CompoundNBT();

         for(String s : profile.getProperties().keySet()) {
            ListNBT listnbt = new ListNBT();

            for(Property property : profile.getProperties().get(s)) {
               CompoundNBT compoundnbt1 = new CompoundNBT();
               compoundnbt1.putString("Value", property.getValue());
               if (property.hasSignature()) {
                  compoundnbt1.putString("Signature", property.getSignature());
               }

               listnbt.add(compoundnbt1);
            }

            compoundnbt.put(s, listnbt);
         }

         tagCompound.put("Properties", compoundnbt);
      }

      return tagCompound;
   }

   @VisibleForTesting
   public static boolean areNBTEquals(@Nullable INBT nbt1, @Nullable INBT nbt2, boolean compareTagList) {
      if (nbt1 == nbt2) {
         return true;
      } else if (nbt1 == null) {
         return true;
      } else if (nbt2 == null) {
         return false;
      } else if (!nbt1.getClass().equals(nbt2.getClass())) {
         return false;
      } else if (nbt1 instanceof CompoundNBT) {
         CompoundNBT compoundnbt = (CompoundNBT)nbt1;
         CompoundNBT compoundnbt1 = (CompoundNBT)nbt2;

         for(String s : compoundnbt.keySet()) {
            INBT inbt1 = compoundnbt.get(s);
            if (!areNBTEquals(inbt1, compoundnbt1.get(s), compareTagList)) {
               return false;
            }
         }

         return true;
      } else if (nbt1 instanceof ListNBT && compareTagList) {
         ListNBT listnbt = (ListNBT)nbt1;
         ListNBT listnbt1 = (ListNBT)nbt2;
         if (listnbt.isEmpty()) {
            return listnbt1.isEmpty();
         } else {
            for(int i = 0; i < listnbt.size(); ++i) {
               INBT inbt = listnbt.get(i);
               boolean flag = false;

               for(int j = 0; j < listnbt1.size(); ++j) {
                  if (areNBTEquals(inbt, listnbt1.get(j), compareTagList)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return nbt1.equals(nbt2);
      }
   }

   /**
    * Creates a new NBTTagCompound which stores a UUID.
    */
   public static CompoundNBT writeUniqueId(UUID uuid) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putLong("M", uuid.getMostSignificantBits());
      compoundnbt.putLong("L", uuid.getLeastSignificantBits());
      return compoundnbt;
   }

   /**
    * Reads a UUID from the passed NBTTagCompound.
    */
   public static UUID readUniqueId(CompoundNBT tag) {
      return new UUID(tag.getLong("M"), tag.getLong("L"));
   }

   /**
    * Creates a BlockPos object from the data stored in the passed NBTTagCompound.
    */
   public static BlockPos readBlockPos(CompoundNBT tag) {
      return new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
   }

   /**
    * Creates a new NBTTagCompound from a BlockPos.
    */
   public static CompoundNBT writeBlockPos(BlockPos pos) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putInt("X", pos.getX());
      compoundnbt.putInt("Y", pos.getY());
      compoundnbt.putInt("Z", pos.getZ());
      return compoundnbt;
   }

   /**
    * Reads a blockstate from the given tag.
    */
   public static BlockState readBlockState(CompoundNBT tag) {
      if (!tag.contains("Name", 8)) {
         return Blocks.AIR.getDefaultState();
      } else {
         Block block = Registry.BLOCK.getOrDefault(new ResourceLocation(tag.getString("Name")));
         BlockState blockstate = block.getDefaultState();
         if (tag.contains("Properties", 10)) {
            CompoundNBT compoundnbt = tag.getCompound("Properties");
            StateContainer<Block, BlockState> statecontainer = block.getStateContainer();

            for(String s : compoundnbt.keySet()) {
               IProperty<?> iproperty = statecontainer.getProperty(s);
               if (iproperty != null) {
                  blockstate = setValueHelper(blockstate, iproperty, s, compoundnbt, tag);
               }
            }
         }

         return blockstate;
      }
   }

   private static <S extends IStateHolder<S>, T extends Comparable<T>> S setValueHelper(S p_193590_0_, IProperty<T> p_193590_1_, String p_193590_2_, CompoundNBT p_193590_3_, CompoundNBT p_193590_4_) {
      Optional<T> optional = p_193590_1_.parseValue(p_193590_3_.getString(p_193590_2_));
      if (optional.isPresent()) {
         return (S)(p_193590_0_.with(p_193590_1_, (T)(optional.get())));
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", p_193590_2_, p_193590_3_.getString(p_193590_2_), p_193590_4_.toString());
         return p_193590_0_;
      }
   }

   /**
    * Writes the given blockstate to the given tag.
    */
   public static CompoundNBT writeBlockState(BlockState tag) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", Registry.BLOCK.getKey(tag.getBlock()).toString());
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = tag.getValues();
      if (!immutablemap.isEmpty()) {
         CompoundNBT compoundnbt1 = new CompoundNBT();

         for(Entry<IProperty<?>, Comparable<?>> entry : immutablemap.entrySet()) {
            IProperty<?> iproperty = entry.getKey();
            compoundnbt1.putString(iproperty.getName(), getName(iproperty, entry.getValue()));
         }

         compoundnbt.put("Properties", compoundnbt1);
      }

      return compoundnbt;
   }

   private static <T extends Comparable<T>> String getName(IProperty<T> p_190010_0_, Comparable<?> p_190010_1_) {
      return p_190010_0_.getName((T)p_190010_1_);
   }

   public static CompoundNBT update(DataFixer p_210822_0_, DefaultTypeReferences p_210822_1_, CompoundNBT p_210822_2_, int p_210822_3_) {
      return update(p_210822_0_, p_210822_1_, p_210822_2_, p_210822_3_, SharedConstants.getVersion().getWorldVersion());
   }

   public static CompoundNBT update(DataFixer dataFixer, DefaultTypeReferences type, CompoundNBT p_210821_2_, int version, int newVersion) {
      return (CompoundNBT)dataFixer.update(type.func_219816_a(), new Dynamic<>(NBTDynamicOps.INSTANCE, p_210821_2_), version, newVersion).getValue();
   }
}