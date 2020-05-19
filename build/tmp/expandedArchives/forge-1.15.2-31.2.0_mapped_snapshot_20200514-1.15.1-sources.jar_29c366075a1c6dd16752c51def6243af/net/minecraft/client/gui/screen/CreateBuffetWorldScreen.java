package net.minecraft.client.gui.screen;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateBuffetWorldScreen extends Screen {
   private static final List<ResourceLocation> BUFFET_GENERATORS = Registry.CHUNK_GENERATOR_TYPE.keySet().stream().filter((p_205307_0_) -> {
      return Registry.CHUNK_GENERATOR_TYPE.getOrDefault(p_205307_0_).isOptionForBuffetWorld();
   }).collect(Collectors.toList());
   private final CreateWorldScreen parent;
   private final CompoundNBT field_213017_c;
   private CreateBuffetWorldScreen.BiomeList biomeList;
   private int field_205312_t;
   private Button field_205313_u;

   public CreateBuffetWorldScreen(CreateWorldScreen parentIn, CompoundNBT p_i49701_2_) {
      super(new TranslationTextComponent("createWorld.customize.buffet.title"));
      this.parent = parentIn;
      this.field_213017_c = p_i49701_2_;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.addButton(new Button((this.width - 200) / 2, 40, 200, 20, I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", BUFFET_GENERATORS.get(this.field_205312_t))), (p_213015_1_) -> {
         ++this.field_205312_t;
         if (this.field_205312_t >= BUFFET_GENERATORS.size()) {
            this.field_205312_t = 0;
         }

         p_213015_1_.setMessage(I18n.format("createWorld.customize.buffet.generatortype") + " " + I18n.format(Util.makeTranslationKey("generator", BUFFET_GENERATORS.get(this.field_205312_t))));
      }));
      this.biomeList = new CreateBuffetWorldScreen.BiomeList();
      this.children.add(this.biomeList);
      this.field_205313_u = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done"), (p_213014_1_) -> {
         this.parent.chunkProviderSettingsJson = this.serialize();
         this.minecraft.displayGuiScreen(this.parent);
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213012_1_) -> {
         this.minecraft.displayGuiScreen(this.parent);
      }));
      this.deserialize();
      this.func_205306_h();
   }

   private void deserialize() {
      if (this.field_213017_c.contains("chunk_generator", 10) && this.field_213017_c.getCompound("chunk_generator").contains("type", 8)) {
         ResourceLocation resourcelocation = new ResourceLocation(this.field_213017_c.getCompound("chunk_generator").getString("type"));

         for(int i = 0; i < BUFFET_GENERATORS.size(); ++i) {
            if (BUFFET_GENERATORS.get(i).equals(resourcelocation)) {
               this.field_205312_t = i;
               break;
            }
         }
      }

      if (this.field_213017_c.contains("biome_source", 10) && this.field_213017_c.getCompound("biome_source").contains("biomes", 9)) {
         ListNBT listnbt = this.field_213017_c.getCompound("biome_source").getList("biomes", 8);

         for(int j = 0; j < listnbt.size(); ++j) {
            ResourceLocation resourcelocation1 = new ResourceLocation(listnbt.getString(j));
            this.biomeList.setSelected(this.biomeList.children().stream().filter((p_213013_1_) -> {
               return Objects.equals(p_213013_1_.field_214394_b, resourcelocation1);
            }).findFirst().orElse((CreateBuffetWorldScreen.BiomeList.BiomeEntry)null));
         }
      }

      this.field_213017_c.remove("chunk_generator");
      this.field_213017_c.remove("biome_source");
   }

   private CompoundNBT serialize() {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt1.putString("type", Registry.BIOME_SOURCE_TYPE.getKey(BiomeProviderType.FIXED).toString());
      CompoundNBT compoundnbt2 = new CompoundNBT();
      ListNBT listnbt = new ListNBT();
      listnbt.add(StringNBT.valueOf((this.biomeList.getSelected()).field_214394_b.toString()));
      compoundnbt2.put("biomes", listnbt);
      compoundnbt1.put("options", compoundnbt2);
      CompoundNBT compoundnbt3 = new CompoundNBT();
      CompoundNBT compoundnbt4 = new CompoundNBT();
      compoundnbt3.putString("type", BUFFET_GENERATORS.get(this.field_205312_t).toString());
      compoundnbt4.putString("default_block", "minecraft:stone");
      compoundnbt4.putString("default_fluid", "minecraft:water");
      compoundnbt3.put("options", compoundnbt4);
      compoundnbt.put("biome_source", compoundnbt1);
      compoundnbt.put("chunk_generator", compoundnbt3);
      return compoundnbt;
   }

   public void func_205306_h() {
      this.field_205313_u.active = this.biomeList.getSelected() != null;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.biomeList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      this.drawCenteredString(this.font, I18n.format("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
      this.drawCenteredString(this.font, I18n.format("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class BiomeList extends ExtendedList<CreateBuffetWorldScreen.BiomeList.BiomeEntry> {
      private BiomeList() {
         super(CreateBuffetWorldScreen.this.minecraft, CreateBuffetWorldScreen.this.width, CreateBuffetWorldScreen.this.height, 80, CreateBuffetWorldScreen.this.height - 37, 16);
         Registry.BIOME.keySet().stream().sorted(Comparator.comparing((p_214347_0_) -> {
            return Registry.BIOME.getOrDefault(p_214347_0_).getDisplayName().getString();
         })).forEach((p_214348_1_) -> {
            this.addEntry(new CreateBuffetWorldScreen.BiomeList.BiomeEntry(p_214348_1_));
         });
      }

      protected boolean isFocused() {
         return CreateBuffetWorldScreen.this.getFocused() == this;
      }

      public void setSelected(@Nullable CreateBuffetWorldScreen.BiomeList.BiomeEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", Registry.BIOME.getOrDefault(p_setSelected_1_.field_214394_b).getDisplayName().getString())).getString());
         }

      }

      protected void moveSelection(int p_moveSelection_1_) {
         super.moveSelection(p_moveSelection_1_);
         CreateBuffetWorldScreen.this.func_205306_h();
      }

      @OnlyIn(Dist.CLIENT)
      class BiomeEntry extends ExtendedList.AbstractListEntry<CreateBuffetWorldScreen.BiomeList.BiomeEntry> {
         private final ResourceLocation field_214394_b;

         public BiomeEntry(ResourceLocation p_i50811_2_) {
            this.field_214394_b = p_i50811_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            BiomeList.this.drawString(CreateBuffetWorldScreen.this.font, Registry.BIOME.getOrDefault(this.field_214394_b).getDisplayName().getString(), p_render_3_ + 5, p_render_2_ + 2, 16777215);
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               BiomeList.this.setSelected(this);
               CreateBuffetWorldScreen.this.func_205306_h();
               return true;
            } else {
               return false;
            }
         }
      }
   }
}