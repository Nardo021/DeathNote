package com.yaazyy.deathnote.forge1165.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yaazyy.deathnote.forge1165.network.DeathNoteNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class DeathNoteClient {

    private DeathNoteClient() {
    }

    public static void openEditScreen(ItemStack stack) {
        Minecraft.getInstance().displayGuiScreen(new DeathNoteEditScreen(stack));
    }

    @OnlyIn(Dist.CLIENT)
    static final class DeathNoteEditScreen extends Screen {

        private TextFieldWidget nameField;

        DeathNoteEditScreen(ItemStack heldStack) {
            super(new StringTextComponent("Death Note"));
        }

        @Override
        protected void init() {
            int cx = this.width / 2;
            int cy = this.height / 2;
            this.nameField = new TextFieldWidget(this.font, cx - 100, cy - 10, 200, 20, new StringTextComponent(""));
            this.nameField.setMaxStringLength(16);
            this.children.add(this.nameField);
            this.addButton(new Button(cx - 50, cy + 20, 100, 20, new StringTextComponent("Done"), b -> submit()));
            this.addButton(new Button(cx - 50, cy + 50, 100, 20, new StringTextComponent("Cancel"), b -> this.onClose()));
            this.setFocusedDefault(this.nameField);
        }

        private void submit() {
            String line = this.nameField.getText() == null ? "" : this.nameField.getText().trim();
            DeathNoteNetwork.CHANNEL.sendToServer(new DeathNoteNetwork.SubmitDeathNotePacket(line));
            this.onClose();
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            this.renderBackground(matrixStack);
            drawCenteredString(matrixStack, this.font, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
            drawCenteredString(matrixStack, this.font, "Write a player's name on the first line.",
                    this.width / 2, this.height / 2 - 28, 0xA0A0A0);
            this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }
}
