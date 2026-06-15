package com.yaazyy.deathnote.forge189.network;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.forge189.Forge189DNPlayer;
import com.yaazyy.deathnote.forge189.ModContext;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class DeathNoteNetwork {

    public static final SimpleNetworkWrapper CHANNEL = new SimpleNetworkWrapper("deathnote");

    private DeathNoteNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                SubmitDeathNotePacket.Handler.class,
                SubmitDeathNotePacket.class,
                0,
                Side.SERVER);
    }

    public static final class SubmitDeathNotePacket implements IMessage {

        private String firstLine = "";

        public SubmitDeathNotePacket() {
        }

        public SubmitDeathNotePacket(String firstLine) {
            this.firstLine = firstLine == null ? "" : firstLine;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            firstLine = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeUTF8String(buf, firstLine);
        }

        public static final class Handler implements IMessageHandler<SubmitDeathNotePacket, IMessage> {

            @Override
            public IMessage onMessage(SubmitDeathNotePacket message, MessageContext ctx) {
                final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                final String line = message.firstLine;
                MinecraftServer server = MinecraftServer.getServer();
                if (server != null) {
                    server.addScheduledTask(new Runnable() {
                        @Override
                        public void run() {
                            handleServer(player, line);
                        }
                    });
                }
                return null;
            }

            private static void handleServer(EntityPlayerMP player, String firstLine) {
                if (player == null) {
                    return;
                }
                ModContext mod = ModContext.get();
                ItemStack inHand = player.getHeldItem();
                if (!mod.itemBridge().isDeathNoteItem(inHand)) {
                    player.addChatMessage(new ChatComponentText(mod.messages().get("result.invalidItem")));
                    return;
                }

                final DNPlayer actor = new Forge189DNPlayer(player);
                final String line = firstLine;
                mod.platform().runLater(new Runnable() {
                    @Override
                    public void run() {
                        DeathNoteResult result = mod.service().process(actor, line);
                        sendResult(player, result, line);
                        if (result == DeathNoteResult.SUCCESS && mod.config().input().consumeOnUse) {
                            consumeOne(player);
                        }
                    }
                }, mod.config().kill().delayTicks);
            }

            private static void sendResult(EntityPlayerMP player, DeathNoteResult result, String target) {
                ModContext mod = ModContext.get();
                String key;
                switch (result) {
                    case SUCCESS:
                        key = "result.success";
                        break;
                    case INVALID_ITEM:
                        key = "result.invalidItem";
                        break;
                    case INVALID_NAME:
                        key = "result.invalidName";
                        break;
                    case TARGET_NOT_FOUND:
                        key = "result.targetNotFound";
                        break;
                    case SELF_TARGET_BLOCKED:
                        key = "result.selfBlocked";
                        break;
                    case NO_PERMISSION:
                        key = "result.noPermission";
                        break;
                    case TARGET_IMMUNE:
                        key = "result.targetImmune";
                        break;
                    case COOLDOWN_ACTIVE:
                        key = "result.cooldown";
                        break;
                    case KILL_FAILED:
                        key = "result.killFailed";
                        break;
                    case CONFIG_ERROR:
                        key = "result.configError";
                        break;
                    default:
                        key = "result.killFailed";
                        break;
                }
                player.addChatMessage(new ChatComponentText(
                        mod.messages().get(key, "target", target == null ? "?" : target)));
            }

            private static void consumeOne(EntityPlayerMP player) {
                ItemStack inHand = player.getHeldItem();
                if (inHand == null) {
                    return;
                }
                int amount = inHand.stackSize;
                if (amount <= 1) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                } else {
                    inHand.stackSize = amount - 1;
                }
            }
        }
    }
}
