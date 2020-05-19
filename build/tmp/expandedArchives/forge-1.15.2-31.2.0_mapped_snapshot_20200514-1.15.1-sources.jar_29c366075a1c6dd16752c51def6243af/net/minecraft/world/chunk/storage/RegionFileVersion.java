package net.minecraft.world.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;

public class RegionFileVersion {
   private static final Int2ObjectMap<RegionFileVersion> field_227161_d_ = new Int2ObjectOpenHashMap<>();
   public static final RegionFileVersion field_227158_a_ = func_227167_a_(new RegionFileVersion(1, GZIPInputStream::new, GZIPOutputStream::new));
   public static final RegionFileVersion field_227159_b_ = func_227167_a_(new RegionFileVersion(2, InflaterInputStream::new, DeflaterOutputStream::new));
   public static final RegionFileVersion field_227160_c_ = func_227167_a_(new RegionFileVersion(3, (p_227171_0_) -> {
      return p_227171_0_;
   }, (p_227172_0_) -> {
      return p_227172_0_;
   }));
   private final int field_227162_e_;
   private final RegionFileVersion.IWrapper<InputStream> field_227163_f_;
   private final RegionFileVersion.IWrapper<OutputStream> field_227164_g_;

   private RegionFileVersion(int p_i225787_1_, RegionFileVersion.IWrapper<InputStream> p_i225787_2_, RegionFileVersion.IWrapper<OutputStream> p_i225787_3_) {
      this.field_227162_e_ = p_i225787_1_;
      this.field_227163_f_ = p_i225787_2_;
      this.field_227164_g_ = p_i225787_3_;
   }

   private static RegionFileVersion func_227167_a_(RegionFileVersion p_227167_0_) {
      field_227161_d_.put(p_227167_0_.field_227162_e_, p_227167_0_);
      return p_227167_0_;
   }

   @Nullable
   public static RegionFileVersion func_227166_a_(int p_227166_0_) {
      return field_227161_d_.get(p_227166_0_);
   }

   public static boolean func_227170_b_(int p_227170_0_) {
      return field_227161_d_.containsKey(p_227170_0_);
   }

   public int func_227165_a_() {
      return this.field_227162_e_;
   }

   public OutputStream func_227169_a_(OutputStream p_227169_1_) throws IOException {
      return this.field_227164_g_.wrap(p_227169_1_);
   }

   public InputStream func_227168_a_(InputStream p_227168_1_) throws IOException {
      return this.field_227163_f_.wrap(p_227168_1_);
   }

   @FunctionalInterface
   interface IWrapper<O> {
      O wrap(O p_wrap_1_) throws IOException;
   }
}