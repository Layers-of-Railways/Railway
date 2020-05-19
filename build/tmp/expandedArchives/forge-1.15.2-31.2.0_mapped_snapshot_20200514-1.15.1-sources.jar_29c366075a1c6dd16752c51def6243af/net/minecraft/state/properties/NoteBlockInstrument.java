package net.minecraft.state.properties;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public enum NoteBlockInstrument implements IStringSerializable {
   HARP("harp", SoundEvents.BLOCK_NOTE_BLOCK_HARP),
   BASEDRUM("basedrum", SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
   SNARE("snare", SoundEvents.BLOCK_NOTE_BLOCK_SNARE),
   HAT("hat", SoundEvents.BLOCK_NOTE_BLOCK_HAT),
   BASS("bass", SoundEvents.BLOCK_NOTE_BLOCK_BASS),
   FLUTE("flute", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE),
   BELL("bell", SoundEvents.BLOCK_NOTE_BLOCK_BELL),
   GUITAR("guitar", SoundEvents.BLOCK_NOTE_BLOCK_GUITAR),
   CHIME("chime", SoundEvents.BLOCK_NOTE_BLOCK_CHIME),
   XYLOPHONE("xylophone", SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE),
   IRON_XYLOPHONE("iron_xylophone", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE),
   COW_BELL("cow_bell", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL),
   DIDGERIDOO("didgeridoo", SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO),
   BIT("bit", SoundEvents.BLOCK_NOTE_BLOCK_BIT),
   BANJO("banjo", SoundEvents.BLOCK_NOTE_BLOCK_BANJO),
   PLING("pling", SoundEvents.BLOCK_NOTE_BLOCK_PLING);

   private final String name;
   private final SoundEvent sound;

   private NoteBlockInstrument(String name, SoundEvent sound) {
      this.name = name;
      this.sound = sound;
   }

   public String getName() {
      return this.name;
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public static NoteBlockInstrument byState(BlockState p_208087_0_) {
      Block block = p_208087_0_.getBlock();
      if (block == Blocks.CLAY) {
         return FLUTE;
      } else if (block == Blocks.GOLD_BLOCK) {
         return BELL;
      } else if (block.isIn(BlockTags.WOOL)) {
         return GUITAR;
      } else if (block == Blocks.PACKED_ICE) {
         return CHIME;
      } else if (block == Blocks.BONE_BLOCK) {
         return XYLOPHONE;
      } else if (block == Blocks.IRON_BLOCK) {
         return IRON_XYLOPHONE;
      } else if (block == Blocks.SOUL_SAND) {
         return COW_BELL;
      } else if (block == Blocks.PUMPKIN) {
         return DIDGERIDOO;
      } else if (block == Blocks.EMERALD_BLOCK) {
         return BIT;
      } else if (block == Blocks.HAY_BLOCK) {
         return BANJO;
      } else if (block == Blocks.GLOWSTONE) {
         return PLING;
      } else {
         Material material = p_208087_0_.getMaterial();
         if (material == Material.ROCK) {
            return BASEDRUM;
         } else if (material == Material.SAND) {
            return SNARE;
         } else if (material == Material.GLASS) {
            return HAT;
         } else {
            return material == Material.WOOD ? BASS : HARP;
         }
      }
   }
}