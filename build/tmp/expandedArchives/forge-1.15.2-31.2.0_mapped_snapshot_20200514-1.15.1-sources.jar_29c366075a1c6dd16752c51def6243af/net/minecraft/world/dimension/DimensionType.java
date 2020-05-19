package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;

public class DimensionType extends net.minecraftforge.registries.ForgeRegistryEntry<DimensionType> implements IDynamicSerializable {
   public static final DimensionType OVERWORLD = register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true, ColumnFuzzedBiomeMagnifier.INSTANCE));
   public static final DimensionType THE_NETHER = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false, FuzzedBiomeMagnifier.INSTANCE));
   public static final DimensionType THE_END = register("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new, false, FuzzedBiomeMagnifier.INSTANCE));
   private final int id;
   private final String suffix;
   private final String directory;
   private final BiFunction<World, DimensionType, ? extends Dimension> factory;
   private final boolean hasSkyLight;
   private final IBiomeMagnifier magnifier;
   private final boolean isVanilla;
   private final net.minecraftforge.common.ModDimension modType;
   private final net.minecraft.network.PacketBuffer data;

   private static DimensionType register(String key, DimensionType type) {
      return Registry.register(Registry.DIMENSION_TYPE, type.id, key, type);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   @Deprecated
   protected DimensionType(int p_i225789_1_, String p_i225789_2_, String p_i225789_3_, BiFunction<World, DimensionType, ? extends Dimension> p_i225789_4_, boolean p_i225789_5_, IBiomeMagnifier p_i225789_6_) {
      this(p_i225789_1_, p_i225789_2_, p_i225789_3_, p_i225789_4_, p_i225789_5_, p_i225789_6_, null, null);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   @Deprecated
   public DimensionType(int p_i225789_1_, String p_i225789_2_, String p_i225789_3_, BiFunction<World, DimensionType, ? extends Dimension> p_i225789_4_, boolean p_i225789_5_, IBiomeMagnifier p_i225789_6_, @Nullable net.minecraftforge.common.ModDimension modType, @Nullable net.minecraft.network.PacketBuffer data) {
      this.id = p_i225789_1_;
      this.suffix = p_i225789_2_;
      this.directory = p_i225789_3_;
      this.factory = p_i225789_4_;
      this.hasSkyLight = p_i225789_5_;
      this.magnifier = p_i225789_6_;
      this.isVanilla = this.id >= 0 && this.id <= 2;
      this.modType = modType;
      this.data = data;
   }

   public static DimensionType deserialize(Dynamic<?> p_218271_0_) {
      return Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(p_218271_0_.asString("")));
   }

   public static Iterable<DimensionType> getAll() {
      return Registry.DIMENSION_TYPE;
   }

   public int getId() {
      return this.id + -1;
   }

   @Deprecated //Forge Do not use, only used for villages backwards compatibility
   public String getSuffix() {
      return isVanilla ? this.suffix : "";
   }

   public File getDirectory(File p_212679_1_) {
      return this.directory.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.directory);
   }

   public Dimension create(World worldIn) {
      return this.factory.apply(worldIn, this);
   }

   public String toString() {
      return "DimensionType{" + getKey(this) + "}";
   }

   @Nullable
   public static DimensionType getById(int id) {
      return Registry.DIMENSION_TYPE.getByValue(id - -1);
   }

   public boolean isVanilla() {
      return this.isVanilla;
   }

   @Nullable
   public net.minecraftforge.common.ModDimension getModType() {
      return this.modType;
   }

   @Nullable
   public net.minecraft.network.PacketBuffer getData() {
      return this.data;
   }

   @Nullable
   public static DimensionType byName(ResourceLocation nameIn) {
      return Registry.DIMENSION_TYPE.getOrDefault(nameIn);
   }

   @Nullable
   public static ResourceLocation getKey(DimensionType dim) {
      return Registry.DIMENSION_TYPE.getKey(dim);
   }

   public boolean hasSkyLight() {
      return this.hasSkyLight;
   }

   public IBiomeMagnifier getMagnifier() {
      return this.magnifier;
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
   }
}