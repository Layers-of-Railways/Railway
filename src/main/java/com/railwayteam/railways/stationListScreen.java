package com.railwayteam.railways;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.railwayteam.railways.items.StationLocation;
import com.railwayteam.railways.packets.CustomPacketUpdateOrders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.Iterator;

public class stationListScreen extends AbstractRailwaysScreen<StationListContainer> {

  private ResourceLocation BG = new ResourceLocation(Railways.MODID, "textures/gui/schedule_bg.png");
  private ResourceLocation FG = new ResourceLocation(Railways.MODID, "textures/gui/schedule_fg.png");
  private boolean pressed = false;
  private TextListWidget listWidget;
  Minecraft minecraft = Minecraft.getInstance();

  private static final int buttonWidth  = 80;
  private static final int buttonHeight = 20;
  private static final int buttonX = 20;
  private static final int buttonY = 20;

  protected stationListScreen(StationListContainer container, PlayerInventory inv, ITextComponent title) {
    super (container, inv, title);
  }

  @Override
  protected void init () {
    super.init();
    if (Config.HIBYE.get()) {
      widgets.add(new Button(buttonX, buttonY, buttonWidth, buttonHeight, new StringTextComponent("Press me!"), button -> {
        this.pressed = !pressed;
        button.setMessage(new StringTextComponent("Press again!"));
      }));
    }
    Iterator<StationLocation> list = container.stationList.iterator();
    listWidget = new TextListWidget(buttonX, buttonY + buttonHeight*2, 200, 200);
    listWidget.setAddable(false);
    while (list.hasNext()) listWidget.addItem(list.next().name);
    widgets.add(listWidget);
  }

  @Override
  protected void drawBackground(MatrixStack matrix, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    if (Config.HIBYE.get()) {
      RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
      minecraft.getTextureManager().bindTexture(pressed ? FG : BG);
      RenderSystem.scaled(0.7d, 0.7d, 1d);
      int relX = (width - getXSize()) / 2 + 200;
      int relY = (height - getYSize()) / 2;
      this.drawTexture(matrix, relX, relY, 0, 0, (int) Math.floor(getXSize() * 1.5), (int) Math.floor(getYSize() * 1.54));
    }
  }

  @Override
  protected void renderWindow (MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
    drawCenteredString(ms, Minecraft.getInstance().fontRenderer, "Test GUI: " + container.target, 10,10, 0xffffff);
  }

//  @Override
//  protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {
//    if (Config.HIBYE.get()) {
//      RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
//      minecraft.getTextureManager().bindTexture(pressed ? FG : BG);
//      RenderSystem.scaled(0.7d, 0.7d, 1d);
//      int relX = (width - getXSize()) / 2 + 200;
//      int relY = (height - getYSize()) / 2;
//      blit(relX, relY, 0, 0, (int) Math.floor(getXSize() * 1.5), (int) Math.floor(getYSize() * 1.54));
//    }
//  }


//  @Override
//  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
//    if (Config.HIBYE.get()) {
//      RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
//      minecraft.getTextureManager().bindTexture(pressed ? FG : BG);
//      RenderSystem.scaled(0.7d, 0.7d, 1d);
//      int relX = (width - getXSize()) / 2 + 200;
//      int relY = (height - getYSize()) / 2;
//      blit(matrixStack, relX, relY, 0, 0, (int) Math.floor(getXSize() * 1.5), (int) Math.floor(getYSize() * 1.54));
//    }
//  }

  @Override
  public void tick () {
    super.tick();
  }

  @Override
  public boolean mouseClicked (double x, double y, int button) {
    boolean mouseClicked = super.mouseClicked(x,y,button);
  //  if (button == 0) {
  //    minecraft.player.closeScreen();
  //    mouseClicked = true;
  //  }
    return mouseClicked;
  }

  @Override
  public void onClose() {
    super.onClose();
    RailwaysPacketHandler.channel.sendToServer(new CustomPacketUpdateOrders(container.target, listWidget.getActiveValues()));
  //  playerInventory.player.sendMessage(new StringTextComponent("updated list with " + container.stationList.size() + " values"));
  }
}
