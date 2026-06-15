package com.yaazyy.deathnote.forge1201.client;

import com.yaazyy.deathnote.forge1201.network.DeathNoteNetwork;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Simple book-like editor: one text field for the first line, Done submits to server.
 */
@OnlyIn(Dist.CLIENT)
public final class DeathNoteEditScreen extends Screen {

    private final ItemStack heldStack;
    private EditBox nameField;

    public DeathNoteEditScreen(ItemStack heldStack) {
        super(Component.literal("Death Note"));
        this.heldStack = heldStack;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;
        nameField = new EditBox(this.font, cx - 100, cy - 10, 200, 20, Component.literal("Name"));
        nameField.setMaxLength(16);
        addRenderableWidget(nameField);
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> submit())
                .bounds(cx - 50, cy + 20, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> onClose())
                .bounds(cx - 50, cy + 50, 100, 20).build());
        setInitialFocus(nameField);
    }

    private void submit() {
        String line = nameField.getValue() == null ? "" : nameField.getValue().trim();
        DeathNoteNetwork.CHANNEL.sendToServer(new DeathNoteNetwork.SubmitDeathNotePacket(line));
        onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Write a player's name on the first line.", this.width / 2,
                this.height / 2 - 28, 0xA0A0A0);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
