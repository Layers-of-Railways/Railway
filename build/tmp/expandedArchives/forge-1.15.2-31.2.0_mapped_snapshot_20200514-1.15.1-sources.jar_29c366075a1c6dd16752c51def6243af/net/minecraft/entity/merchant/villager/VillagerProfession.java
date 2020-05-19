package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;

public class VillagerProfession extends net.minecraftforge.registries.ForgeRegistryEntry<VillagerProfession> {
   public static final VillagerProfession NONE = register("none", PointOfInterestType.UNEMPLOYED, (SoundEvent)null);
   public static final VillagerProfession ARMORER = register("armorer", PointOfInterestType.ARMORER, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
   public static final VillagerProfession BUTCHER = register("butcher", PointOfInterestType.BUTCHER, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
   public static final VillagerProfession CARTOGRAPHER = register("cartographer", PointOfInterestType.CARTOGRAPHER, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
   public static final VillagerProfession CLERIC = register("cleric", PointOfInterestType.CLERIC, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
   public static final VillagerProfession FARMER = register("farmer", PointOfInterestType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS), ImmutableSet.of(Blocks.FARMLAND), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
   public static final VillagerProfession FISHERMAN = register("fisherman", PointOfInterestType.FISHERMAN, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
   public static final VillagerProfession FLETCHER = register("fletcher", PointOfInterestType.FLETCHER, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
   public static final VillagerProfession LEATHERWORKER = register("leatherworker", PointOfInterestType.LEATHERWORKER, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
   public static final VillagerProfession LIBRARIAN = register("librarian", PointOfInterestType.LIBRARIAN, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
   public static final VillagerProfession MASON = register("mason", PointOfInterestType.MASON, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
   public static final VillagerProfession NITWIT = register("nitwit", PointOfInterestType.NITWIT, (SoundEvent)null);
   public static final VillagerProfession SHEPHERD = register("shepherd", PointOfInterestType.SHEPHERD, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
   public static final VillagerProfession TOOLSMITH = register("toolsmith", PointOfInterestType.TOOLSMITH, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
   public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PointOfInterestType.WEAPONSMITH, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
   private final String name;
   private final PointOfInterestType pointOfInterest;
   /** Defines items villagers of this profession can pick up and use. */
   private final ImmutableSet<Item> specificItems;
   /** World blocks this profession interracts with. */
   private final ImmutableSet<Block> relatedWorldBlocks;
   @Nullable
   private final SoundEvent sound;

   private VillagerProfession(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn, @Nullable SoundEvent soundIn) {
      this.name = nameIn;
      this.pointOfInterest = pointOfInterestIn;
      this.specificItems = specificItemsIn;
      this.relatedWorldBlocks = relatedWorldBlocksIn;
      this.sound = soundIn;
   }

   public PointOfInterestType getPointOfInterest() {
      return this.pointOfInterest;
   }

   /**
    * @return A shared static immutable set of the specific items this profession can handle.
    */
   public ImmutableSet<Item> getSpecificItems() {
      return this.specificItems;
   }

   /**
    * @return A shared static immutable set of the world blocks this profession interracts with beside job site block.
    */
   public ImmutableSet<Block> getRelatedWorldBlocks() {
      return this.relatedWorldBlocks;
   }

   @Nullable
   public SoundEvent getSound() {
      return this.sound;
   }

   public String toString() {
      return this.name;
   }

   static VillagerProfession register(String nameIn, PointOfInterestType pointOfInterestIn, @Nullable SoundEvent soundIn) {
      return register(nameIn, pointOfInterestIn, ImmutableSet.of(), ImmutableSet.of(), soundIn);
   }

   static VillagerProfession register(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn, @Nullable SoundEvent soundIn) {
      return Registry.register(Registry.VILLAGER_PROFESSION, new ResourceLocation(nameIn), new VillagerProfession(nameIn, pointOfInterestIn, specificItemsIn, relatedWorldBlocksIn, soundIn));
   }
}