package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateFlatWorldScreen extends Screen {
   private final CreateWorldScreen createWorldGui;
   private FlatGenerationSettings generatorInfo = FlatGenerationSettings.getDefaultFlatGenerator();
   /** The text used to identify the material for a layer */
   private String materialText;
   /** The text used to identify the height of a layer */
   private String heightText;
   private CreateFlatWorldScreen.DetailsList createFlatWorldListSlotGui;
   /** The remove layer button */
   private Button removeLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen parent, CompoundNBT generatorOptions) {
      super(new TranslationTextComponent("createWorld.customize.flat.title"));
      this.createWorldGui = parent;
      this.setGeneratorOptions(generatorOptions);
   }

   /**
    * Gets the superflat preset string for the generator; see
    * https://minecraft.gamepedia.com/Superflat#Preset_code_format
    */
   public String getPreset() {
      return this.generatorInfo.toString();
   }

   /**
    * Gets the NBT data for the generator (which has the same use as the preset)
    */
   public CompoundNBT getGeneratorOptions() {
      return (CompoundNBT)this.generatorInfo.func_210834_a(NBTDynamicOps.INSTANCE).getValue();
   }

   /**
    * Sets the generator config based off of the given preset.
    *  
    * Implementation note: {@link GuiFlatPresets} calls this method and not {@link #getGeneratorOptions} when the done
    * button is used.
    */
   public void setPreset(String preset) {
      this.generatorInfo = FlatGenerationSettings.createFlatGeneratorFromString(preset);
   }

   /**
    * Sets the generator config based on the given NBT.
    */
   public void setGeneratorOptions(CompoundNBT nbt) {
      this.generatorInfo = FlatGenerationSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt));
   }

   protected void init() {
      this.materialText = I18n.format("createWorld.customize.flat.tile");
      this.heightText = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new CreateFlatWorldScreen.DetailsList();
      this.children.add(this.createFlatWorldListSlotGui);
      this.removeLayerButton = this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer"), (p_213007_1_) -> {
         if (this.hasSelectedLayer()) {
            List<FlatLayerInfo> list = this.generatorInfo.getFlatLayers();
            int i = this.createFlatWorldListSlotGui.children().indexOf(this.createFlatWorldListSlotGui.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.createFlatWorldListSlotGui.setSelected(list.isEmpty() ? null : this.createFlatWorldListSlotGui.children().get(Math.min(i, list.size() - 1)));
            this.generatorInfo.updateLayers();
            this.onLayersChanged();
         }
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets"), (p_213011_1_) -> {
         this.minecraft.displayGuiScreen(new FlatPresetsScreen(this));
         this.generatorInfo.updateLayers();
         this.onLayersChanged();
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done"), (p_213010_1_) -> {
         this.createWorldGui.chunkProviderSettingsJson = this.getGeneratorOptions();
         this.minecraft.displayGuiScreen(this.createWorldGui);
         this.generatorInfo.updateLayers();
         this.onLayersChanged();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_213009_1_) -> {
         this.minecraft.displayGuiScreen(this.createWorldGui);
         this.generatorInfo.updateLayers();
         this.onLayersChanged();
      }));
      this.generatorInfo.updateLayers();
      this.onLayersChanged();
   }

   /**
    * Would update whether or not the edit and remove buttons are enabled, but is currently disabled and always disables
    * the buttons (which are invisible anyways)
    */
   public void onLayersChanged() {
      this.removeLayerButton.active = this.hasSelectedLayer();
      this.createFlatWorldListSlotGui.func_214345_a();
   }

   /**
    * Returns whether there is a valid layer selection
    */
   private boolean hasSelectedLayer() {
      return this.createFlatWorldListSlotGui.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.displayGuiScreen(this.createWorldGui);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.createFlatWorldListSlotGui.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      this.drawString(this.font, this.materialText, i, 32, 16777215);
      this.drawString(this.font, this.heightText, i + 2 + 213 - this.font.getStringWidth(this.heightText), 32, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsList extends ExtendedList<CreateFlatWorldScreen.DetailsList.LayerEntry> {
      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int i = 0; i < CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size(); ++i) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.LayerEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().get(CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - this.children().indexOf(p_setSelected_1_) - 1);
            Item item = flatlayerinfo.getLayerMaterial().getBlock().asItem();
            if (item != Items.AIR) {
               NarratorChatListener.INSTANCE.say((new TranslationTextComponent("narrator.select", item.getDisplayName(new ItemStack(item)))).getString());
            }
         }

      }

      protected void moveSelection(int p_moveSelection_1_) {
         super.moveSelection(p_moveSelection_1_);
         CreateFlatWorldScreen.this.onLayersChanged();
      }

      protected boolean isFocused() {
         return CreateFlatWorldScreen.this.getFocused() == this;
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void func_214345_a() {
         int i = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int j = 0; j < CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size(); ++j) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

         List<CreateFlatWorldScreen.DetailsList.LayerEntry> list = this.children();
         if (i >= 0 && i < list.size()) {
            this.setSelected(list.get(i));
         }

      }

      @OnlyIn(Dist.CLIENT)
      class LayerEntry extends ExtendedList.AbstractListEntry<CreateFlatWorldScreen.DetailsList.LayerEntry> {
         private LayerEntry() {
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().get(CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - p_render_1_ - 1);
            BlockState blockstate = flatlayerinfo.getLayerMaterial();
            Block block = blockstate.getBlock();
            Item item = block.asItem();
            if (item == Items.AIR) {
               if (block == Blocks.WATER) {
                  item = Items.WATER_BUCKET;
               } else if (block == Blocks.LAVA) {
                  item = Items.LAVA_BUCKET;
               }
            }

            ItemStack itemstack = new ItemStack(item);
            String s = item.getDisplayName(itemstack).getFormattedText();
            this.func_214389_a(p_render_3_, p_render_2_, itemstack);
            CreateFlatWorldScreen.this.font.drawString(s, (float)(p_render_3_ + 18 + 5), (float)(p_render_2_ + 3), 16777215);
            String s1;
            if (p_render_1_ == 0) {
               s1 = I18n.format("createWorld.customize.flat.layer.top", flatlayerinfo.getLayerCount());
            } else if (p_render_1_ == CreateFlatWorldScreen.this.generatorInfo.getFlatLayers().size() - 1) {
               s1 = I18n.format("createWorld.customize.flat.layer.bottom", flatlayerinfo.getLayerCount());
            } else {
               s1 = I18n.format("createWorld.customize.flat.layer", flatlayerinfo.getLayerCount());
            }

            CreateFlatWorldScreen.this.font.drawString(s1, (float)(p_render_3_ + 2 + 213 - CreateFlatWorldScreen.this.font.getStringWidth(s1)), (float)(p_render_2_ + 3), 16777215);
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               DetailsList.this.setSelected(this);
               CreateFlatWorldScreen.this.onLayersChanged();
               return true;
            } else {
               return false;
            }
         }

         private void func_214389_a(int p_214389_1_, int p_214389_2_, ItemStack p_214389_3_) {
            this.func_214390_a(p_214389_1_ + 1, p_214389_2_ + 1);
            RenderSystem.enableRescaleNormal();
            if (!p_214389_3_.isEmpty()) {
               CreateFlatWorldScreen.this.itemRenderer.renderItemIntoGUI(p_214389_3_, p_214389_1_ + 2, p_214389_2_ + 2);
            }

            RenderSystem.disableRescaleNormal();
         }

         private void func_214390_a(int p_214390_1_, int p_214390_2_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DetailsList.this.minecraft.getTextureManager().bindTexture(AbstractGui.STATS_ICON_LOCATION);
            AbstractGui.blit(p_214390_1_, p_214390_2_, CreateFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}