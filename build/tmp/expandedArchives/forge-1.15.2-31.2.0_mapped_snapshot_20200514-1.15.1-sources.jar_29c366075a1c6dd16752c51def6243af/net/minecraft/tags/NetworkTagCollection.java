package net.minecraft.tags;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagCollection<T> extends TagCollection<T> {
   private final Registry<T> registry;

   public NetworkTagCollection(Registry<T> p_i49817_1_, String p_i49817_2_, String p_i49817_3_) {
      super(p_i49817_1_::getValue, p_i49817_2_, false, p_i49817_3_);
      this.registry = p_i49817_1_;
   }

   public void write(PacketBuffer buf) {
      Map<ResourceLocation, Tag<T>> map = this.getTagMap();
      buf.writeVarInt(map.size());

      for(Entry<ResourceLocation, Tag<T>> entry : map.entrySet()) {
         buf.writeResourceLocation(entry.getKey());
         buf.writeVarInt(entry.getValue().getAllElements().size());

         for(T t : entry.getValue().getAllElements()) {
            buf.writeVarInt(this.registry.getId(t));
         }
      }

   }

   public void read(PacketBuffer buf) {
      Map<ResourceLocation, Tag<T>> map = Maps.newHashMap();
      int i = buf.readVarInt();

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = buf.readResourceLocation();
         int k = buf.readVarInt();
         Tag.Builder<T> builder = Tag.Builder.create();

         for(int l = 0; l < k; ++l) {
            builder.add(this.registry.getByValue(buf.readVarInt()));
         }

         map.put(resourcelocation, builder.build(resourcelocation));
      }

      this.toImmutable(map);
   }
}