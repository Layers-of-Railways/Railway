package net.minecraft.network.datasync;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class DataSerializers {
   private static final IntIdentityHashBiMap<IDataSerializer<?>> REGISTRY = new IntIdentityHashBiMap<>(16);
   public static final IDataSerializer<Byte> BYTE = new IDataSerializer<Byte>() {
      public void write(PacketBuffer buf, Byte value) {
         buf.writeByte(value);
      }

      public Byte read(PacketBuffer buf) {
         return buf.readByte();
      }

      public Byte copyValue(Byte value) {
         return value;
      }
   };
   public static final IDataSerializer<Integer> VARINT = new IDataSerializer<Integer>() {
      public void write(PacketBuffer buf, Integer value) {
         buf.writeVarInt(value);
      }

      public Integer read(PacketBuffer buf) {
         return buf.readVarInt();
      }

      public Integer copyValue(Integer value) {
         return value;
      }
   };
   public static final IDataSerializer<Float> FLOAT = new IDataSerializer<Float>() {
      public void write(PacketBuffer buf, Float value) {
         buf.writeFloat(value);
      }

      public Float read(PacketBuffer buf) {
         return buf.readFloat();
      }

      public Float copyValue(Float value) {
         return value;
      }
   };
   public static final IDataSerializer<String> STRING = new IDataSerializer<String>() {
      public void write(PacketBuffer buf, String value) {
         buf.writeString(value);
      }

      public String read(PacketBuffer buf) {
         return buf.readString(32767);
      }

      public String copyValue(String value) {
         return value;
      }
   };
   public static final IDataSerializer<ITextComponent> TEXT_COMPONENT = new IDataSerializer<ITextComponent>() {
      public void write(PacketBuffer buf, ITextComponent value) {
         buf.writeTextComponent(value);
      }

      public ITextComponent read(PacketBuffer buf) {
         return buf.readTextComponent();
      }

      public ITextComponent copyValue(ITextComponent value) {
         return value.deepCopy();
      }
   };
   public static final IDataSerializer<Optional<ITextComponent>> OPTIONAL_TEXT_COMPONENT = new IDataSerializer<Optional<ITextComponent>>() {
      public void write(PacketBuffer buf, Optional<ITextComponent> value) {
         if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeTextComponent(value.get());
         } else {
            buf.writeBoolean(false);
         }

      }

      public Optional<ITextComponent> read(PacketBuffer buf) {
         return buf.readBoolean() ? Optional.of(buf.readTextComponent()) : Optional.empty();
      }

      public Optional<ITextComponent> copyValue(Optional<ITextComponent> value) {
         return value.isPresent() ? Optional.of(value.get().deepCopy()) : Optional.empty();
      }
   };
   public static final IDataSerializer<ItemStack> ITEMSTACK = new IDataSerializer<ItemStack>() {
      public void write(PacketBuffer buf, ItemStack value) {
         buf.writeItemStack(value);
      }

      public ItemStack read(PacketBuffer buf) {
         return buf.readItemStack();
      }

      public ItemStack copyValue(ItemStack value) {
         return value.copy();
      }
   };
   public static final IDataSerializer<Optional<BlockState>> OPTIONAL_BLOCK_STATE = new IDataSerializer<Optional<BlockState>>() {
      public void write(PacketBuffer buf, Optional<BlockState> value) {
         if (value.isPresent()) {
            buf.writeVarInt(Block.getStateId(value.get()));
         } else {
            buf.writeVarInt(0);
         }

      }

      public Optional<BlockState> read(PacketBuffer buf) {
         int i = buf.readVarInt();
         return i == 0 ? Optional.empty() : Optional.of(Block.getStateById(i));
      }

      public Optional<BlockState> copyValue(Optional<BlockState> value) {
         return value;
      }
   };
   public static final IDataSerializer<Boolean> BOOLEAN = new IDataSerializer<Boolean>() {
      public void write(PacketBuffer buf, Boolean value) {
         buf.writeBoolean(value);
      }

      public Boolean read(PacketBuffer buf) {
         return buf.readBoolean();
      }

      public Boolean copyValue(Boolean value) {
         return value;
      }
   };
   public static final IDataSerializer<IParticleData> PARTICLE_DATA = new IDataSerializer<IParticleData>() {
      public void write(PacketBuffer buf, IParticleData value) {
         buf.writeVarInt(Registry.PARTICLE_TYPE.getId(value.getType()));
         value.write(buf);
      }

      public IParticleData read(PacketBuffer buf) {
         return this.read(buf, Registry.PARTICLE_TYPE.getByValue(buf.readVarInt()));
      }

      private <T extends IParticleData> T read(PacketBuffer p_200543_1_, ParticleType<T> p_200543_2_) {
         return p_200543_2_.getDeserializer().read(p_200543_2_, p_200543_1_);
      }

      public IParticleData copyValue(IParticleData value) {
         return value;
      }
   };
   public static final IDataSerializer<Rotations> ROTATIONS = new IDataSerializer<Rotations>() {
      public void write(PacketBuffer buf, Rotations value) {
         buf.writeFloat(value.getX());
         buf.writeFloat(value.getY());
         buf.writeFloat(value.getZ());
      }

      public Rotations read(PacketBuffer buf) {
         return new Rotations(buf.readFloat(), buf.readFloat(), buf.readFloat());
      }

      public Rotations copyValue(Rotations value) {
         return value;
      }
   };
   public static final IDataSerializer<BlockPos> BLOCK_POS = new IDataSerializer<BlockPos>() {
      public void write(PacketBuffer buf, BlockPos value) {
         buf.writeBlockPos(value);
      }

      public BlockPos read(PacketBuffer buf) {
         return buf.readBlockPos();
      }

      public BlockPos copyValue(BlockPos value) {
         return value;
      }
   };
   public static final IDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new IDataSerializer<Optional<BlockPos>>() {
      public void write(PacketBuffer buf, Optional<BlockPos> value) {
         buf.writeBoolean(value.isPresent());
         if (value.isPresent()) {
            buf.writeBlockPos(value.get());
         }

      }

      public Optional<BlockPos> read(PacketBuffer buf) {
         return !buf.readBoolean() ? Optional.empty() : Optional.of(buf.readBlockPos());
      }

      public Optional<BlockPos> copyValue(Optional<BlockPos> value) {
         return value;
      }
   };
   public static final IDataSerializer<Direction> DIRECTION = new IDataSerializer<Direction>() {
      public void write(PacketBuffer buf, Direction value) {
         buf.writeEnumValue(value);
      }

      public Direction read(PacketBuffer buf) {
         return buf.readEnumValue(Direction.class);
      }

      public Direction copyValue(Direction value) {
         return value;
      }
   };
   public static final IDataSerializer<Optional<UUID>> OPTIONAL_UNIQUE_ID = new IDataSerializer<Optional<UUID>>() {
      public void write(PacketBuffer buf, Optional<UUID> value) {
         buf.writeBoolean(value.isPresent());
         if (value.isPresent()) {
            buf.writeUniqueId(value.get());
         }

      }

      public Optional<UUID> read(PacketBuffer buf) {
         return !buf.readBoolean() ? Optional.empty() : Optional.of(buf.readUniqueId());
      }

      public Optional<UUID> copyValue(Optional<UUID> value) {
         return value;
      }
   };
   public static final IDataSerializer<CompoundNBT> COMPOUND_NBT = new IDataSerializer<CompoundNBT>() {
      public void write(PacketBuffer buf, CompoundNBT value) {
         buf.writeCompoundTag(value);
      }

      public CompoundNBT read(PacketBuffer buf) {
         return buf.readCompoundTag();
      }

      public CompoundNBT copyValue(CompoundNBT value) {
         return value.copy();
      }
   };
   public static final IDataSerializer<VillagerData> VILLAGER_DATA = new IDataSerializer<VillagerData>() {
      public void write(PacketBuffer buf, VillagerData value) {
         buf.writeVarInt(Registry.VILLAGER_TYPE.getId(value.getType()));
         buf.writeVarInt(Registry.VILLAGER_PROFESSION.getId(value.getProfession()));
         buf.writeVarInt(value.getLevel());
      }

      public VillagerData read(PacketBuffer buf) {
         return new VillagerData(Registry.VILLAGER_TYPE.getByValue(buf.readVarInt()), Registry.VILLAGER_PROFESSION.getByValue(buf.readVarInt()), buf.readVarInt());
      }

      public VillagerData copyValue(VillagerData value) {
         return value;
      }
   };
   public static final IDataSerializer<OptionalInt> OPTIONAL_VARINT = new IDataSerializer<OptionalInt>() {
      public void write(PacketBuffer buf, OptionalInt value) {
         buf.writeVarInt(value.orElse(-1) + 1);
      }

      public OptionalInt read(PacketBuffer buf) {
         int i = buf.readVarInt();
         return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
      }

      public OptionalInt copyValue(OptionalInt value) {
         return value;
      }
   };
   public static final IDataSerializer<Pose> POSE = new IDataSerializer<Pose>() {
      public void write(PacketBuffer buf, Pose value) {
         buf.writeEnumValue(value);
      }

      public Pose read(PacketBuffer buf) {
         return buf.readEnumValue(Pose.class);
      }

      public Pose copyValue(Pose value) {
         return value;
      }
   };

   public static void registerSerializer(IDataSerializer<?> serializer) {
      if (REGISTRY.add(serializer) >= 256) throw new RuntimeException("Vanilla DataSerializer ID limit exceeded");
   }

   @Nullable
   public static IDataSerializer<?> getSerializer(int id) {
      return net.minecraftforge.common.ForgeHooks.getSerializer(id, REGISTRY);
   }

   public static int getSerializerId(IDataSerializer<?> serializer) {
      return net.minecraftforge.common.ForgeHooks.getSerializerId(serializer, REGISTRY);
   }

   static {
      registerSerializer(BYTE);
      registerSerializer(VARINT);
      registerSerializer(FLOAT);
      registerSerializer(STRING);
      registerSerializer(TEXT_COMPONENT);
      registerSerializer(OPTIONAL_TEXT_COMPONENT);
      registerSerializer(ITEMSTACK);
      registerSerializer(BOOLEAN);
      registerSerializer(ROTATIONS);
      registerSerializer(BLOCK_POS);
      registerSerializer(OPTIONAL_BLOCK_POS);
      registerSerializer(DIRECTION);
      registerSerializer(OPTIONAL_UNIQUE_ID);
      registerSerializer(OPTIONAL_BLOCK_STATE);
      registerSerializer(COMPOUND_NBT);
      registerSerializer(PARTICLE_DATA);
      registerSerializer(VILLAGER_DATA);
      registerSerializer(OPTIONAL_VARINT);
      registerSerializer(POSE);
   }
}