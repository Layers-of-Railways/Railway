package com.railwayteam.railways;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRailwaysScreen <S extends Container> extends ContainerScreen<S> {
  protected List<Widget> widgets;

  public AbstractRailwaysScreen(S container, PlayerInventory inv, ITextComponent title) {
    super (container, inv, title);
    widgets = new ArrayList<>();
  }

  protected void setWindowSize (int width, int height) {
    this.xSize = width;
    this.ySize = height;
  }

//  @Override
//  public void render (int mouseX, int mouseY, float partialTicks) {
//    renderBackground();
//    renderWindow (mouseX, mouseY, partialTicks);
//
//    for (Widget w : widgets) {
//      w.render(mouseX, mouseY, partialTicks);
//    }
//    super.render(mouseX, mouseY, partialTicks);
//
//    RenderSystem.enableAlphaTest();
//    RenderSystem.enableBlend();
//    RenderSystem.disableRescaleNormal();
//    RenderSystem.disableLighting();
//    RenderSystem.disableDepthTest();
//    renderWindowForeground (mouseX, mouseY, partialTicks);
//    for (Widget w : widgets) {
//      w.renderToolTip(mouseX, mouseY);
//    }
//  }


  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    renderBackground(matrixStack);
    renderWindow (matrixStack, mouseX, mouseY, partialTicks);

    for (Widget w : widgets) {
      w.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    super.render(matrixStack, mouseX, mouseY, partialTicks);

    RenderSystem.enableAlphaTest();
    RenderSystem.enableBlend();
    RenderSystem.disableRescaleNormal();
    RenderSystem.disableLighting();
    RenderSystem.disableDepthTest();
    renderWindowForeground (matrixStack, mouseX, mouseY, partialTicks);
    for (Widget w : widgets) {
      w.renderToolTip(matrixStack, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked (double x, double y, int button) {
    boolean result = false;
    for (Widget w : widgets) {
      if (w.mouseClicked(x,y,button)) {
        result = true;
      }
    }
    return result || super.mouseClicked(x,y,button);
  }

  @Override
  public boolean keyPressed (int code, int p_keyPressed_2_, int p_keyPressed_3_) {
    for (Widget w : widgets) {
      if (w.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_)) {
        return true;
      }
    }
    return super.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_);
  }

  @Override
  public boolean charTyped (char c, int code) {
    for (Widget w : widgets) {
      if (w.charTyped(c, code)) {
        return true;
      }
    }
    return super.charTyped(c, code);
  }

  @Override
  public boolean mouseScrolled (double mouseX, double mouseY, double delta) {
    for (Widget w : widgets) {
      if (w.mouseScrolled(mouseX, mouseY, delta)) {
        return true;
      }
    }
    return super.mouseScrolled(mouseX, mouseY, delta);
  }

  @Override
  public boolean mouseReleased (double x, double y, int button) {
    boolean result = false;
    for (Widget w : widgets) {
      if (w.mouseReleased(x, y, button)) {
        result = true;
      }
    }
    return result | super.mouseReleased(x,y,button);
  }

  @Override
  public boolean shouldCloseOnEsc () {
    return true;
  }

  @Override
  public boolean isPauseScreen () {
    return false;
  }

  protected abstract void renderWindow (MatrixStack ms, int mouseX, int mouseY, float PartialTicks);

//  @Override
//  protected void drawGuiContainerBackgroundLayer (float partialTicks, int mouseX, int mouseY) {
//  }


//  @Override
//  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
//  }

  protected void renderWindowForeground (MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    drawMouseoverTooltip(matrixStack, mouseX, mouseY);
    for (Widget w : widgets) {
      if (!w.isHovered()) {
        continue;
      }
      // check for special widgetation here
    }
  }


}
