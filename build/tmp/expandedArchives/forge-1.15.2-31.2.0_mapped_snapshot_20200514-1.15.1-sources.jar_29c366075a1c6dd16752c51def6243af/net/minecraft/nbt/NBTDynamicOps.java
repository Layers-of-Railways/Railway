package net.minecraft.nbt;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class NBTDynamicOps implements DynamicOps<INBT> {
   public static final NBTDynamicOps INSTANCE = new NBTDynamicOps();

   protected NBTDynamicOps() {
   }

   public INBT empty() {
      return EndNBT.INSTANCE;
   }

   public Type<?> getType(INBT p_getType_1_) {
      switch(p_getType_1_.getId()) {
      case 0:
         return DSL.nilType();
      case 1:
         return DSL.byteType();
      case 2:
         return DSL.shortType();
      case 3:
         return DSL.intType();
      case 4:
         return DSL.longType();
      case 5:
         return DSL.floatType();
      case 6:
         return DSL.doubleType();
      case 7:
         return DSL.list(DSL.byteType());
      case 8:
         return DSL.string();
      case 9:
         return DSL.list(DSL.remainderType());
      case 10:
         return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
      case 11:
         return DSL.list(DSL.intType());
      case 12:
         return DSL.list(DSL.longType());
      default:
         return DSL.remainderType();
      }
   }

   public Optional<Number> getNumberValue(INBT p_getNumberValue_1_) {
      return p_getNumberValue_1_ instanceof NumberNBT ? Optional.of(((NumberNBT)p_getNumberValue_1_).getAsNumber()) : Optional.empty();
   }

   public INBT createNumeric(Number p_createNumeric_1_) {
      return DoubleNBT.valueOf(p_createNumeric_1_.doubleValue());
   }

   public INBT createByte(byte p_createByte_1_) {
      return ByteNBT.valueOf(p_createByte_1_);
   }

   public INBT createShort(short p_createShort_1_) {
      return ShortNBT.valueOf(p_createShort_1_);
   }

   public INBT createInt(int p_createInt_1_) {
      return IntNBT.valueOf(p_createInt_1_);
   }

   public INBT createLong(long p_createLong_1_) {
      return LongNBT.valueOf(p_createLong_1_);
   }

   public INBT createFloat(float p_createFloat_1_) {
      return FloatNBT.valueOf(p_createFloat_1_);
   }

   public INBT createDouble(double p_createDouble_1_) {
      return DoubleNBT.valueOf(p_createDouble_1_);
   }

   public INBT createBoolean(boolean p_createBoolean_1_) {
      return ByteNBT.valueOf(p_createBoolean_1_);
   }

   public Optional<String> getStringValue(INBT p_getStringValue_1_) {
      return p_getStringValue_1_ instanceof StringNBT ? Optional.of(p_getStringValue_1_.getString()) : Optional.empty();
   }

   public INBT createString(String p_createString_1_) {
      return StringNBT.valueOf(p_createString_1_);
   }

   public INBT mergeInto(INBT p_mergeInto_1_, INBT p_mergeInto_2_) {
      if (p_mergeInto_2_ instanceof EndNBT) {
         return p_mergeInto_1_;
      } else if (!(p_mergeInto_1_ instanceof CompoundNBT)) {
         if (p_mergeInto_1_ instanceof EndNBT) {
            throw new IllegalArgumentException("mergeInto called with a null input.");
         } else if (p_mergeInto_1_ instanceof CollectionNBT) {
            CollectionNBT<INBT> collectionnbt = new ListNBT();
            CollectionNBT<?> collectionnbt1 = (CollectionNBT)p_mergeInto_1_;
            collectionnbt.addAll(collectionnbt1);
            collectionnbt.add(p_mergeInto_2_);
            return collectionnbt;
         } else {
            return p_mergeInto_1_;
         }
      } else if (!(p_mergeInto_2_ instanceof CompoundNBT)) {
         return p_mergeInto_1_;
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         CompoundNBT compoundnbt1 = (CompoundNBT)p_mergeInto_1_;

         for(String s : compoundnbt1.keySet()) {
            compoundnbt.put(s, compoundnbt1.get(s));
         }

         CompoundNBT compoundnbt2 = (CompoundNBT)p_mergeInto_2_;

         for(String s1 : compoundnbt2.keySet()) {
            compoundnbt.put(s1, compoundnbt2.get(s1));
         }

         return compoundnbt;
      }
   }

   public INBT mergeInto(INBT p_mergeInto_1_, INBT p_mergeInto_2_, INBT p_mergeInto_3_) {
      CompoundNBT compoundnbt;
      if (p_mergeInto_1_ instanceof EndNBT) {
         compoundnbt = new CompoundNBT();
      } else {
         if (!(p_mergeInto_1_ instanceof CompoundNBT)) {
            return p_mergeInto_1_;
         }

         CompoundNBT compoundnbt1 = (CompoundNBT)p_mergeInto_1_;
         compoundnbt = new CompoundNBT();
         compoundnbt1.keySet().forEach((p_212014_2_) -> {
            compoundnbt.put(p_212014_2_, compoundnbt1.get(p_212014_2_));
         });
      }

      compoundnbt.put(p_mergeInto_2_.getString(), p_mergeInto_3_);
      return compoundnbt;
   }

   public INBT merge(INBT p_merge_1_, INBT p_merge_2_) {
      if (p_merge_1_ instanceof EndNBT) {
         return p_merge_2_;
      } else if (p_merge_2_ instanceof EndNBT) {
         return p_merge_1_;
      } else {
         if (p_merge_1_ instanceof CompoundNBT && p_merge_2_ instanceof CompoundNBT) {
            CompoundNBT compoundnbt = (CompoundNBT)p_merge_1_;
            CompoundNBT compoundnbt1 = (CompoundNBT)p_merge_2_;
            CompoundNBT compoundnbt2 = new CompoundNBT();
            compoundnbt.keySet().forEach((p_211384_2_) -> {
               compoundnbt2.put(p_211384_2_, compoundnbt.get(p_211384_2_));
            });
            compoundnbt1.keySet().forEach((p_212012_2_) -> {
               compoundnbt2.put(p_212012_2_, compoundnbt1.get(p_212012_2_));
            });
         }

         if (p_merge_1_ instanceof CollectionNBT && p_merge_2_ instanceof CollectionNBT) {
            ListNBT listnbt = new ListNBT();
            listnbt.addAll((CollectionNBT)p_merge_1_);
            listnbt.addAll((CollectionNBT)p_merge_2_);
            return listnbt;
         } else {
            throw new IllegalArgumentException("Could not merge " + p_merge_1_ + " and " + p_merge_2_);
         }
      }
   }

   public Optional<Map<INBT, INBT>> getMapValues(INBT p_getMapValues_1_) {
      if (p_getMapValues_1_ instanceof CompoundNBT) {
         CompoundNBT compoundnbt = (CompoundNBT)p_getMapValues_1_;
         return Optional.of(compoundnbt.keySet().stream().map((p_210819_2_) -> {
            return Pair.of(this.createString(p_210819_2_), compoundnbt.get(p_210819_2_));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      } else {
         return Optional.empty();
      }
   }

   public INBT createMap(Map<INBT, INBT> p_createMap_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();

      for(Entry<INBT, INBT> entry : p_createMap_1_.entrySet()) {
         compoundnbt.put(entry.getKey().getString(), entry.getValue());
      }

      return compoundnbt;
   }

   public Optional<Stream<INBT>> getStream(INBT p_getStream_1_) {
      return p_getStream_1_ instanceof CollectionNBT ? Optional.of(((CollectionNBT)p_getStream_1_).stream().map((p_210817_0_) -> {
         return p_210817_0_;
      })) : Optional.empty();
   }

   public Optional<ByteBuffer> getByteBuffer(INBT p_getByteBuffer_1_) {
      return p_getByteBuffer_1_ instanceof ByteArrayNBT ? Optional.of(ByteBuffer.wrap(((ByteArrayNBT)p_getByteBuffer_1_).getByteArray())) : DynamicOps.super.getByteBuffer(p_getByteBuffer_1_);
   }

   public INBT createByteList(ByteBuffer p_createByteList_1_) {
      return new ByteArrayNBT(DataFixUtils.toArray(p_createByteList_1_));
   }

   public Optional<IntStream> getIntStream(INBT p_getIntStream_1_) {
      return p_getIntStream_1_ instanceof IntArrayNBT ? Optional.of(Arrays.stream(((IntArrayNBT)p_getIntStream_1_).getIntArray())) : DynamicOps.super.getIntStream(p_getIntStream_1_);
   }

   public INBT createIntList(IntStream p_createIntList_1_) {
      return new IntArrayNBT(p_createIntList_1_.toArray());
   }

   public Optional<LongStream> getLongStream(INBT p_getLongStream_1_) {
      return p_getLongStream_1_ instanceof LongArrayNBT ? Optional.of(Arrays.stream(((LongArrayNBT)p_getLongStream_1_).getAsLongArray())) : DynamicOps.super.getLongStream(p_getLongStream_1_);
   }

   public INBT createLongList(LongStream p_createLongList_1_) {
      return new LongArrayNBT(p_createLongList_1_.toArray());
   }

   public INBT createList(Stream<INBT> p_createList_1_) {
      PeekingIterator<INBT> peekingiterator = Iterators.peekingIterator(p_createList_1_.iterator());
      if (!peekingiterator.hasNext()) {
         return new ListNBT();
      } else {
         INBT inbt = peekingiterator.peek();
         if (inbt instanceof ByteNBT) {
            List<Byte> list2 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210815_0_) -> {
               return ((ByteNBT)p_210815_0_).getByte();
            }));
            return new ByteArrayNBT(list2);
         } else if (inbt instanceof IntNBT) {
            List<Integer> list1 = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210818_0_) -> {
               return ((IntNBT)p_210818_0_).getInt();
            }));
            return new IntArrayNBT(list1);
         } else if (inbt instanceof LongNBT) {
            List<Long> list = Lists.newArrayList(Iterators.transform(peekingiterator, (p_210816_0_) -> {
               return ((LongNBT)p_210816_0_).getLong();
            }));
            return new LongArrayNBT(list);
         } else {
            ListNBT listnbt = new ListNBT();

            while(peekingiterator.hasNext()) {
               INBT inbt1 = peekingiterator.next();
               if (!(inbt1 instanceof EndNBT)) {
                  listnbt.add(inbt1);
               }
            }

            return listnbt;
         }
      }
   }

   public INBT remove(INBT p_remove_1_, String p_remove_2_) {
      if (p_remove_1_ instanceof CompoundNBT) {
         CompoundNBT compoundnbt = (CompoundNBT)p_remove_1_;
         CompoundNBT compoundnbt1 = new CompoundNBT();
         compoundnbt.keySet().stream().filter((p_212019_1_) -> {
            return !Objects.equals(p_212019_1_, p_remove_2_);
         }).forEach((p_212010_2_) -> {
            compoundnbt1.put(p_212010_2_, compoundnbt.get(p_212010_2_));
         });
         return compoundnbt1;
      } else {
         return p_remove_1_;
      }
   }

   public String toString() {
      return "NBT";
   }
}