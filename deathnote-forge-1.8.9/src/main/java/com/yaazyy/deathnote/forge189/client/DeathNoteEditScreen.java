package com.yaazyy.deathnote.forge189.client;

import java.io.IOException;

import com.yaazyy.deathnote.forge189.network.DeathNoteNetwork;

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

    private final ItemStack heldStack;
    private GuiTextField nameField;
    private GuiButton doneButton;
    private GuiButton cancelButton;

    public DeathNoteEditScreen(ItemStack heldStack) {
        this.heldStack = heldStack;
    }

    @Override
    public void initGui() {
        int cx = this.width / 2;
        int cy = this.height / 2;
        this.nameField = new GuiTextField(0, this.fontRendererObj, cx - 100, cy - 10, 200, 20);
        this.nameField.setMaxStringLength(16);
        this.nameField.setFocused(true);
        this.buttonList.add(this.doneButton = new GuiButton(0, cx - 50, cy + 20, 100, 20, "Done"));
        this.buttonList.add(this.cancelButton = new GuiButton(1, cx - 50, cy + 50, 100, 20, "Cancel"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == this.doneButton) {
            submit();
        } else if (button == this.cancelButton) {
            this.mc.displayGuiScreen(null);
        }
    }

    private void submit() {
        String line = this.nameField.getText() == null ? "" : this.nameField.getText().trim();
        DeathNoteNetwork.CHANNEL.sendToServer(new DeathNoteNetwork.SubmitDeathNotePacket(line));
        this.mc.displayGuiScreen(null);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        this.nameField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Death Note", this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        drawCenteredString(this.fontRendererObj, "Write a player's name on the first line.",
                this.width / 2, this.height / 2 - 28, 0xA0A0A0);
        this.nameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
