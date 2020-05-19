package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class CommandStorage {
   private final Map<String, CommandStorage.Container> field_227482_a_ = Maps.newHashMap();
   private final DimensionSavedDataManager field_227483_b_;

   public CommandStorage(DimensionSavedDataManager p_i225883_1_) {
      this.field_227483_b_ = p_i225883_1_;
   }

   private CommandStorage.Container func_227486_a_(String p_227486_1_, String p_227486_2_) {
      CommandStorage.Container commandstorage$container = new CommandStorage.Container(p_227486_2_);
      this.field_227482_a_.put(p_227486_1_, commandstorage$container);
      return commandstorage$container;
   }

   public CompoundNBT func_227488_a_(ResourceLocation p_227488_1_) {
      String s = p_227488_1_.getNamespace();
      String s1 = func_227485_a_(s);
      CommandStorage.Container commandstorage$container = this.field_227483_b_.get(() -> {
         return this.func_227486_a_(s, s1);
      }, s1);
      return commandstorage$container != null ? commandstorage$container.func_227493_a_(p_227488_1_.getPath()) : new CompoundNBT();
   }

   public void func_227489_a_(ResourceLocation p_227489_1_, CompoundNBT p_227489_2_) {
      String s = p_227489_1_.getNamespace();
      String s1 = func_227485_a_(s);
      this.field_227483_b_.getOrCreate(() -> {
         return this.func_227486_a_(s, s1);
      }, s1).func_227495_a_(p_227489_1_.getPath(), p_227489_2_);
   }

   public Stream<ResourceLocation> func_227484_a_() {
      return this.field_227482_a_.entrySet().stream().flatMap((p_227487_0_) -> {
         return p_227487_0_.getValue().func_227497_b_(p_227487_0_.getKey());
      });
   }

   private static String func_227485_a_(String p_227485_0_) {
      return "command_storage_" + p_227485_0_;
   }

   static class Container extends WorldSavedData {
      private final Map<String, CompoundNBT> field_227492_a_ = Maps.newHashMap();

      public Container(String p_i225884_1_) {
         super(p_i225884_1_);
      }

      /**
       * reads in data from the NBTTagCompound into this MapDataBase
       */
      public void read(CompoundNBT nbt) {
         CompoundNBT compoundnbt = nbt.getCompound("contents");

         for(String s : compoundnbt.keySet()) {
            this.field_227492_a_.put(s, compoundnbt.getCompound(s));
         }

      }

      public CompoundNBT write(CompoundNBT compound) {
         CompoundNBT compoundnbt = new CompoundNBT();
         this.field_227492_a_.forEach((p_227496_1_, p_227496_2_) -> {
            compoundnbt.put(p_227496_1_, p_227496_2_.copy());
         });
         compound.put("contents", compoundnbt);
         return compound;
      }

      public CompoundNBT func_227493_a_(String p_227493_1_) {
         CompoundNBT compoundnbt = this.field_227492_a_.get(p_227493_1_);
         return compoundnbt != null ? compoundnbt : new CompoundNBT();
      }

      public void func_227495_a_(String p_227495_1_, CompoundNBT p_227495_2_) {
         if (p_227495_2_.isEmpty()) {
            this.field_227492_a_.remove(p_227495_1_);
         } else {
            this.field_227492_a_.put(p_227495_1_, p_227495_2_);
         }

         this.markDirty();
      }

      public Stream<ResourceLocation> func_227497_b_(String p_227497_1_) {
         return this.field_227492_a_.keySet().stream().map((p_227494_1_) -> {
            return new ResourceLocation(p_227497_1_, p_227494_1_);
         });
      }
   }
}