package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class TagLootEntry extends StandaloneLootEntry {
   private final Tag<Item> field_216180_c;
   private final boolean field_216181_h;

   private TagLootEntry(Tag<Item> p_i51248_1_, boolean p_i51248_2_, int p_i51248_3_, int p_i51248_4_, ILootCondition[] p_i51248_5_, ILootFunction[] p_i51248_6_) {
      super(p_i51248_3_, p_i51248_4_, p_i51248_5_, p_i51248_6_);
      this.field_216180_c = p_i51248_1_;
      this.field_216181_h = p_i51248_2_;
   }

   public void func_216154_a(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      this.field_216180_c.getAllElements().forEach((p_216174_1_) -> {
         p_216154_1_.accept(new ItemStack(p_216174_1_));
      });
   }

   private boolean func_216179_a(LootContext p_216179_1_, Consumer<ILootGenerator> p_216179_2_) {
      if (!this.test(p_216179_1_)) {
         return false;
      } else {
         for(final Item item : this.field_216180_c.getAllElements()) {
            p_216179_2_.accept(new StandaloneLootEntry.Generator() {
               public void func_216188_a(Consumer<ItemStack> p_216188_1_, LootContext p_216188_2_) {
                  p_216188_1_.accept(new ItemStack(item));
               }
            });
         }

         return true;
      }
   }

   public boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      return this.field_216181_h ? this.func_216179_a(p_expand_1_, p_expand_2_) : super.expand(p_expand_1_, p_expand_2_);
   }

   public static StandaloneLootEntry.Builder<?> func_216176_b(Tag<Item> p_216176_0_) {
      return builder((p_216177_1_, p_216177_2_, p_216177_3_, p_216177_4_) -> {
         return new TagLootEntry(p_216176_0_, true, p_216177_1_, p_216177_2_, p_216177_3_, p_216177_4_);
      });
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<TagLootEntry> {
      public Serializer() {
         super(new ResourceLocation("tag"), TagLootEntry.class);
      }

      public void serialize(JsonObject json, TagLootEntry entryIn, JsonSerializationContext context) {
         super.serialize(json, entryIn, context);
         json.addProperty("name", entryIn.field_216180_c.getId().toString());
         json.addProperty("expand", entryIn.field_216181_h);
      }

      protected TagLootEntry func_212829_b_(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_212829_1_, "name"));
         Tag<Item> tag = ItemTags.getCollection().get(resourcelocation);
         if (tag == null) {
            throw new JsonParseException("Can't find tag: " + resourcelocation);
         } else {
            boolean flag = JSONUtils.getBoolean(p_212829_1_, "expand");
            return new TagLootEntry(tag, flag, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
         }
      }
   }
}