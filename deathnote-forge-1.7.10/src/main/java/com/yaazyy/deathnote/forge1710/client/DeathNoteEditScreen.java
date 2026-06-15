package com.yaazyy.deathnote.forge1710.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import com.yaazyy.deathnote.forge1710.network.DeathNoteNetwork;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Simple book-like editor: one text field for the first line, Done submits to server.
 */
@SideOnly(Side.CLIENT)
public final class DeathNoteEditScreen extends GuiScreen {

  private GuiTextField nameField;

  public DeathNoteEditScreen(net.minecraft.item.ItemStack heldStack) {
  }

  @Override
  public void initGui() {
    int cx = width / 2;
    int cy = height / 2;
    buttonList.clear();
    nameField = new GuiTextField(fontRendererObj, cx - 100, cy - 10, 200, 20);
    nameField.setMaxStringLength(16);
    nameField.setFocused(true);
    buttonList.add(new GuiButton(0, cx - 50, cy + 20, 100, 20, "Done"));
    buttonList.add(new GuiButton(1, cx - 50, cy + 50, 100, 20, "Cancel"));
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    if (button.id == 0) {
      submit();
    } else if (button.id == 1) {
      mc.displayGuiScreen(null);
    }
  }

  private void submit() {
    String line = nameField.getText() == null ? "" : nameField.getText().trim();
    DeathNoteNetwork.CHANNEL.sendToServer(new DeathNoteNetwork.SubmitDeathNoteMessage(line));
    mc.displayGuiScreen(null);
  }

  @Override
  public void updateScreen() {
    nameField.updateCursorCounter();
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) {
    if (nameField.textboxKeyTyped(typedChar, keyCode)) {
      return;
    }
    super.keyTyped(typedChar, keyCode);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    nameField.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    int cx = width / 2;
    int cy = height / 2;
    drawCenteredString(fontRendererObj, "Death Note", cx, cy - 40, 0xFFFFFF);
    drawCenteredString(fontRendererObj, "Write a player's name on the first line.", cx, cy - 28, 0xA0A0A0);
    nameField.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }
}
