package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StatsScreen extends Screen implements IProgressMeter {
   protected final Screen parentScreen;
   private StatsScreen.CustomStatsList generalStats;
   private StatsScreen.StatsList itemStats;
   private StatsScreen.MobStatsList mobStats;
   private final StatisticsManager stats;
   @Nullable
   private ExtendedList<?> displaySlot;
   /** When true, the game will be paused when the gui is shown */
   private boolean doesGuiPauseGame = true;

   public StatsScreen(Screen parent, StatisticsManager manager) {
      super(new TranslationTextComponent("gui.stats"));
      this.parentScreen = parent;
      this.stats = manager;
   }

   protected void init() {
      this.doesGuiPauseGame = true;
      this.minecraft.getConnection().sendPacket(new CClientStatusPacket(CClientStatusPacket.State.REQUEST_STATS));
   }

   public void initLists() {
      this.generalStats = new StatsScreen.CustomStatsList(this.minecraft);
      this.itemStats = new StatsScreen.StatsList(this.minecraft);
      this.mobStats = new StatsScreen.MobStatsList(this.minecraft);
   }

   public void initButtons() {
      this.addButton(new Button(this.width / 2 - 120, this.height - 52, 80, 20, I18n.format("stat.generalButton"), (p_213109_1_) -> {
         this.func_213110_a(this.generalStats);
      }));
      Button button = this.addButton(new Button(this.width / 2 - 40, this.height - 52, 80, 20, I18n.format("stat.itemsButton"), (p_213115_1_) -> {
         this.func_213110_a(this.itemStats);
      }));
      Button button1 = this.addButton(new Button(this.width / 2 + 40, this.height - 52, 80, 20, I18n.format("stat.mobsButton"), (p_213114_1_) -> {
         this.func_213110_a(this.mobStats);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height - 28, 200, 20, I18n.format("gui.done"), (p_213113_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      if (this.itemStats.children().isEmpty()) {
         button.active = false;
      }

      if (this.mobStats.children().isEmpty()) {
         button1.active = false;
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.doesGuiPauseGame) {
         this.renderBackground();
         this.drawCenteredString(this.font, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.drawCenteredString(this.font, LOADING_STRINGS[(int)(Util.milliTime() / 150L % (long)LOADING_STRINGS.length)], this.width / 2, this.height / 2 + 9 * 2, 16777215);
      } else {
         this.func_213116_d().render(p_render_1_, p_render_2_, p_render_3_);
         this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
         super.render(p_render_1_, p_render_2_, p_render_3_);
      }

   }

   public void onStatsUpdated() {
      if (this.doesGuiPauseGame) {
         this.initLists();
         this.initButtons();
         this.func_213110_a(this.generalStats);
         this.doesGuiPauseGame = false;
      }

   }

   public boolean isPauseScreen() {
      return !this.doesGuiPauseGame;
   }

   @Nullable
   public ExtendedList<?> func_213116_d() {
      return this.displaySlot;
   }

   public void func_213110_a(@Nullable ExtendedList<?> p_213110_1_) {
      this.children.remove(this.generalStats);
      this.children.remove(this.itemStats);
      this.children.remove(this.mobStats);
      if (p_213110_1_ != null) {
         this.children.add(0, p_213110_1_);
         this.displaySlot = p_213110_1_;
      }

   }

   private int func_195224_b(int p_195224_1_) {
      return 115 + 40 * p_195224_1_;
   }

   private void drawStatsScreen(int x, int y, Item itemIn) {
      this.drawSprite(x + 1, y + 1, 0, 0);
      RenderSystem.enableRescaleNormal();
      this.itemRenderer.renderItemIntoGUI(itemIn.getDefaultInstance(), x + 2, y + 2);
      RenderSystem.disableRescaleNormal();
   }

   /**
    * Draws a sprite from assets/textures/gui/container/stats_icons.png
    */
   private void drawSprite(int x, int y, int u, int v) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(STATS_ICON_LOCATION);
      blit(x, y, this.getBlitOffset(), (float)u, (float)v, 18, 18, 128, 128);
   }

   @OnlyIn(Dist.CLIENT)
   class CustomStatsList extends ExtendedList<StatsScreen.CustomStatsList.Entry> {
      public CustomStatsList(Minecraft mcIn) {
         super(mcIn, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);

         for(Stat<ResourceLocation> stat : Stats.CUSTOM) {
            this.addEntry(new StatsScreen.CustomStatsList.Entry(stat));
         }

      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.CustomStatsList.Entry> {
         private final Stat<ResourceLocation> field_214405_b;

         private Entry(Stat<ResourceLocation> p_i50466_2_) {
            this.field_214405_b = p_i50466_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            ITextComponent itextcomponent = (new TranslationTextComponent("stat." + this.field_214405_b.getValue().toString().replace(':', '.'))).applyTextStyle(TextFormatting.GRAY);
            CustomStatsList.this.drawString(StatsScreen.this.font, itextcomponent.getString(), p_render_3_ + 2, p_render_2_ + 1, p_render_1_ % 2 == 0 ? 16777215 : 9474192);
            String s = this.field_214405_b.format(StatsScreen.this.stats.getValue(this.field_214405_b));
            CustomStatsList.this.drawString(StatsScreen.this.font, s, p_render_3_ + 2 + 213 - StatsScreen.this.font.getStringWidth(s), p_render_2_ + 1, p_render_1_ % 2 == 0 ? 16777215 : 9474192);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class MobStatsList extends ExtendedList<StatsScreen.MobStatsList.Entry> {
      public MobStatsList(Minecraft mcIn) {
         super(mcIn, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 9 * 4);

         for(EntityType<?> entitytype : Registry.ENTITY_TYPE) {
            if (StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(entitytype)) > 0 || StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(entitytype)) > 0) {
               this.addEntry(new StatsScreen.MobStatsList.Entry(entitytype));
            }
         }

      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.MobStatsList.Entry> {
         private final EntityType<?> field_214411_b;

         public Entry(EntityType<?> p_i50018_2_) {
            this.field_214411_b = p_i50018_2_;
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            String s = I18n.format(Util.makeTranslationKey("entity", EntityType.getKey(this.field_214411_b)));
            int i = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED.get(this.field_214411_b));
            int j = StatsScreen.this.stats.getValue(Stats.ENTITY_KILLED_BY.get(this.field_214411_b));
            MobStatsList.this.drawString(StatsScreen.this.font, s, p_render_3_ + 2, p_render_2_ + 1, 16777215);
            MobStatsList.this.drawString(StatsScreen.this.font, this.func_214409_a(s, i), p_render_3_ + 2 + 10, p_render_2_ + 1 + 9, i == 0 ? 6316128 : 9474192);
            MobStatsList.this.drawString(StatsScreen.this.font, this.func_214408_b(s, j), p_render_3_ + 2 + 10, p_render_2_ + 1 + 9 * 2, j == 0 ? 6316128 : 9474192);
         }

         private String func_214409_a(String p_214409_1_, int p_214409_2_) {
            String s = Stats.ENTITY_KILLED.getTranslationKey();
            return p_214409_2_ == 0 ? I18n.format(s + ".none", p_214409_1_) : I18n.format(s, p_214409_2_, p_214409_1_);
         }

         private String func_214408_b(String p_214408_1_, int p_214408_2_) {
            String s = Stats.ENTITY_KILLED_BY.getTranslationKey();
            return p_214408_2_ == 0 ? I18n.format(s + ".none", p_214408_1_) : I18n.format(s, p_214408_1_, p_214408_2_);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   class StatsList extends ExtendedList<StatsScreen.StatsList.Entry> {
      protected final List<StatType<Block>> field_195113_v;
      protected final List<StatType<Item>> field_195114_w;
      private final int[] field_195112_D = new int[]{3, 4, 1, 2, 5, 6};
      protected int field_195115_x = -1;
      protected final List<Item> field_195116_y;
      protected final java.util.Comparator<Item> field_195117_z = new StatsScreen.StatsList.Comparator();
      @Nullable
      protected StatType<?> field_195110_A;
      protected int field_195111_B;

      public StatsList(Minecraft mcIn) {
         super(mcIn, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
         this.field_195113_v = Lists.newArrayList();
         this.field_195113_v.add(Stats.BLOCK_MINED);
         this.field_195114_w = Lists.newArrayList(Stats.ITEM_BROKEN, Stats.ITEM_CRAFTED, Stats.ITEM_USED, Stats.ITEM_PICKED_UP, Stats.ITEM_DROPPED);
         this.setRenderHeader(true, 20);
         Set<Item> set = Sets.newIdentityHashSet();

         for(Item item : Registry.ITEM) {
            boolean flag = false;

            for(StatType<Item> stattype : this.field_195114_w) {
               if (stattype.contains(item) && StatsScreen.this.stats.getValue(stattype.get(item)) > 0) {
                  flag = true;
               }
            }

            if (flag) {
               set.add(item);
            }
         }

         for(Block block : Registry.BLOCK) {
            boolean flag1 = false;

            for(StatType<Block> stattype1 : this.field_195113_v) {
               if (stattype1.contains(block) && StatsScreen.this.stats.getValue(stattype1.get(block)) > 0) {
                  flag1 = true;
               }
            }

            if (flag1) {
               set.add(block.asItem());
            }
         }

         set.remove(Items.AIR);
         this.field_195116_y = Lists.newArrayList(set);

         for(int i = 0; i < this.field_195116_y.size(); ++i) {
            this.addEntry(new StatsScreen.StatsList.Entry());
         }

      }

      protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
         if (!this.minecraft.mouseHelper.isLeftDown()) {
            this.field_195115_x = -1;
         }

         for(int i = 0; i < this.field_195112_D.length; ++i) {
            StatsScreen.this.drawSprite(p_renderHeader_1_ + StatsScreen.this.func_195224_b(i) - 18, p_renderHeader_2_ + 1, 0, this.field_195115_x == i ? 0 : 18);
         }

         if (this.field_195110_A != null) {
            int k = StatsScreen.this.func_195224_b(this.func_195105_b(this.field_195110_A)) - 36;
            int j = this.field_195111_B == 1 ? 2 : 1;
            StatsScreen.this.drawSprite(p_renderHeader_1_ + k, p_renderHeader_2_ + 1, 18 * j, 0);
         }

         for(int l = 0; l < this.field_195112_D.length; ++l) {
            int i1 = this.field_195115_x == l ? 1 : 0;
            StatsScreen.this.drawSprite(p_renderHeader_1_ + StatsScreen.this.func_195224_b(l) - 18 + i1, p_renderHeader_2_ + 1 + i1, 18 * this.field_195112_D[l], 18);
         }

      }

      public int getRowWidth() {
         return 375;
      }

      protected int getScrollbarPosition() {
         return this.width / 2 + 140;
      }

      protected void renderBackground() {
         StatsScreen.this.renderBackground();
      }

      protected void clickedHeader(int p_clickedHeader_1_, int p_clickedHeader_2_) {
         this.field_195115_x = -1;

         for(int i = 0; i < this.field_195112_D.length; ++i) {
            int j = p_clickedHeader_1_ - StatsScreen.this.func_195224_b(i);
            if (j >= -36 && j <= 0) {
               this.field_195115_x = i;
               break;
            }
         }

         if (this.field_195115_x >= 0) {
            this.func_195107_a(this.func_195108_d(this.field_195115_x));
            this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

      }

      private StatType<?> func_195108_d(int p_195108_1_) {
         return p_195108_1_ < this.field_195113_v.size() ? this.field_195113_v.get(p_195108_1_) : this.field_195114_w.get(p_195108_1_ - this.field_195113_v.size());
      }

      private int func_195105_b(StatType<?> p_195105_1_) {
         int i = this.field_195113_v.indexOf(p_195105_1_);
         if (i >= 0) {
            return i;
         } else {
            int j = this.field_195114_w.indexOf(p_195105_1_);
            return j >= 0 ? j + this.field_195113_v.size() : -1;
         }
      }

      protected void renderDecorations(int p_renderDecorations_1_, int p_renderDecorations_2_) {
         if (p_renderDecorations_2_ >= this.y0 && p_renderDecorations_2_ <= this.y1) {
            StatsScreen.StatsList.Entry statsscreen$statslist$entry = this.getEntryAtPosition((double)p_renderDecorations_1_, (double)p_renderDecorations_2_);
            int i = (this.width - this.getRowWidth()) / 2;
            if (statsscreen$statslist$entry != null) {
               if (p_renderDecorations_1_ < i + 40 || p_renderDecorations_1_ > i + 40 + 20) {
                  return;
               }

               Item item = this.field_195116_y.get(this.children().indexOf(statsscreen$statslist$entry));
               this.func_200207_a(this.func_200208_a(item), p_renderDecorations_1_, p_renderDecorations_2_);
            } else {
               ITextComponent itextcomponent = null;
               int j = p_renderDecorations_1_ - i;

               for(int k = 0; k < this.field_195112_D.length; ++k) {
                  int l = StatsScreen.this.func_195224_b(k);
                  if (j >= l - 18 && j <= l) {
                     itextcomponent = new TranslationTextComponent(this.func_195108_d(k).getTranslationKey());
                     break;
                  }
               }

               this.func_200207_a(itextcomponent, p_renderDecorations_1_, p_renderDecorations_2_);
            }

         }
      }

      protected void func_200207_a(@Nullable ITextComponent p_200207_1_, int p_200207_2_, int p_200207_3_) {
         if (p_200207_1_ != null) {
            String s = p_200207_1_.getFormattedText();
            int i = p_200207_2_ + 12;
            int j = p_200207_3_ - 12;
            int k = StatsScreen.this.font.getStringWidth(s);
            this.fillGradient(i - 3, j - 3, i + k + 3, j + 8 + 3, -1073741824, -1073741824);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0F, 0.0F, 400.0F);
            StatsScreen.this.font.drawStringWithShadow(s, (float)i, (float)j, -1);
            RenderSystem.popMatrix();
         }
      }

      protected ITextComponent func_200208_a(Item p_200208_1_) {
         return p_200208_1_.getName();
      }

      protected void func_195107_a(StatType<?> p_195107_1_) {
         if (p_195107_1_ != this.field_195110_A) {
            this.field_195110_A = p_195107_1_;
            this.field_195111_B = -1;
         } else if (this.field_195111_B == -1) {
            this.field_195111_B = 1;
         } else {
            this.field_195110_A = null;
            this.field_195111_B = 0;
         }

         this.field_195116_y.sort(this.field_195117_z);
      }

      @OnlyIn(Dist.CLIENT)
      class Comparator implements java.util.Comparator<Item> {
         private Comparator() {
         }

         public int compare(Item p_compare_1_, Item p_compare_2_) {
            int i;
            int j;
            if (StatsList.this.field_195110_A == null) {
               i = 0;
               j = 0;
            } else if (StatsList.this.field_195113_v.contains(StatsList.this.field_195110_A)) {
               StatType<Block> stattype = (StatType<Block>)StatsList.this.field_195110_A;
               i = p_compare_1_ instanceof BlockItem ? StatsScreen.this.stats.getValue(stattype, ((BlockItem)p_compare_1_).getBlock()) : -1;
               j = p_compare_2_ instanceof BlockItem ? StatsScreen.this.stats.getValue(stattype, ((BlockItem)p_compare_2_).getBlock()) : -1;
            } else {
               StatType<Item> stattype1 = (StatType<Item>)StatsList.this.field_195110_A;
               i = StatsScreen.this.stats.getValue(stattype1, p_compare_1_);
               j = StatsScreen.this.stats.getValue(stattype1, p_compare_2_);
            }

            return i == j ? StatsList.this.field_195111_B * Integer.compare(Item.getIdFromItem(p_compare_1_), Item.getIdFromItem(p_compare_2_)) : StatsList.this.field_195111_B * Integer.compare(i, j);
         }
      }

      @OnlyIn(Dist.CLIENT)
      class Entry extends ExtendedList.AbstractListEntry<StatsScreen.StatsList.Entry> {
         private Entry() {
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            Item item = StatsScreen.this.itemStats.field_195116_y.get(p_render_1_);
            StatsScreen.this.drawStatsScreen(p_render_3_ + 40, p_render_2_, item);

            for(int i = 0; i < StatsScreen.this.itemStats.field_195113_v.size(); ++i) {
               Stat<Block> stat;
               if (item instanceof BlockItem) {
                  stat = StatsScreen.this.itemStats.field_195113_v.get(i).get(((BlockItem)item).getBlock());
               } else {
                  stat = null;
               }

               this.func_214406_a(stat, p_render_3_ + StatsScreen.this.func_195224_b(i), p_render_2_, p_render_1_ % 2 == 0);
            }

            for(int j = 0; j < StatsScreen.this.itemStats.field_195114_w.size(); ++j) {
               this.func_214406_a(StatsScreen.this.itemStats.field_195114_w.get(j).get(item), p_render_3_ + StatsScreen.this.func_195224_b(j + StatsScreen.this.itemStats.field_195113_v.size()), p_render_2_, p_render_1_ % 2 == 0);
            }

         }

         protected void func_214406_a(@Nullable Stat<?> p_214406_1_, int p_214406_2_, int p_214406_3_, boolean p_214406_4_) {
            String s = p_214406_1_ == null ? "-" : p_214406_1_.format(StatsScreen.this.stats.getValue(p_214406_1_));
            StatsList.this.drawString(StatsScreen.this.font, s, p_214406_2_ - StatsScreen.this.font.getStringWidth(s), p_214406_3_ + 5, p_214406_4_ ? 16777215 : 9474192);
         }
      }
   }
}