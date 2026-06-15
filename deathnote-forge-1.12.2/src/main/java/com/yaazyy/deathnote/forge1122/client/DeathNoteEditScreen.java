package com.yaazyy.deathnote.forge1122.client;

import java.io.IOException;

import com.yaazyy.deathnote.forge1122.network.DeathNoteNetwork;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Simple book-like editor: one text field for the first line, Done submits to server.
 */
@SideOnly(Side.CLIENT)
public final class DeathNoteEditScreen extends GuiScreen {

    private static final int BUTTON_DONE = 0;
    private static final int BUTTON_CANCEL = 1;

    private final ItemStack heldStack;
    private GuiTextField nameField;

    public DeathNoteEditScreen(ItemStack heldStack) {
        this.heldStack = heldStack;
    }

    @Override
    public void initGui() {
        int cx = this.width / 2;
        int cy = this.height / 2;
        nameField = new GuiTextField(0, this.fontRenderer, cx - 100, cy - 10, 200, 20);
        nameField.setMaxStringLength(16);
        nameField.setFocused(true);
        this.buttonList.add(new GuiButton(BUTTON_DONE, cx - 50, cy + 20, 100, 20, "Done"));
        this.buttonList.add(new GuiButton(BUTTON_CANCEL, cx - 50, cy + 50, 100, 20, "Cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == BUTTON_DONE) {
            submit();
        } else if (button.id == BUTTON_CANCEL) {
            mc.displayGuiScreen(null);
        }
    }

    private void submit() {
        String line = nameField.getText() == null ? "" : nameField.getText().trim();
        DeathNoteNetwork.CHANNEL.sendToServer(new DeathNoteNetwork.SubmitDeathNotePacket(line));
        mc.displayGuiScreen(null);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (nameField.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        nameField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int cx = this.width / 2;
        int cy = this.height / 2;
        drawCenteredString(this.fontRenderer, "Death Note", cx, cy - 40, 0xFFFFFF);
        drawCenteredString(this.fontRenderer, "Write a player's name on the first line.", cx, cy - 28, 0xA0A0A0);
        nameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
