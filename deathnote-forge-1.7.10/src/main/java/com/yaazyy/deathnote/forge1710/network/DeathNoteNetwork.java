package com.yaazyy.deathnote.forge1710.network;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.forge1710.Forge1710DNPlayer;
import com.yaazyy.deathnote.forge1710.ModContext;

import com.yaazyy.deathnote.forge1710.ServerTasks;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

public final class DeathNoteNetwork {

    public static final SimpleNetworkWrapper CHANNEL = new SimpleNetworkWrapper("deathnote");

    private DeathNoteNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(SubmitDeathNoteHandler.class, SubmitDeathNoteMessage.class, 0, Side.SERVER);
    }

    public static final class SubmitDeathNoteMessage implements IMessage {

        private String firstLine = "";

        public SubmitDeathNoteMessage() {
        }

        public SubmitDeathNoteMessage(String firstLine) {
            this.firstLine = firstLine == null ? "" : firstLine;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            firstLine = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            ByteBufUtils.writeUTF8String(buf, firstLine == null ? "" : firstLine);
        }

        public String firstLine() {
            return firstLine;
        }
    }

    public static final class SubmitDeathNoteHandler implements IMessageHandler<SubmitDeathNoteMessage, IMessage> {

        @Override
        public IMessage onMessage(final SubmitDeathNoteMessage message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ServerTasks.runSync(FMLCommonHandler.instance().getMinecraftServerInstance(), new Runnable() {
                @Override
                public void run() {
                    handleServer(player, message.firstLine());
                }
            });
            return null;
        }

        private static void handleServer(EntityPlayerMP player, String firstLine) {
            if (player == null) {
                return;
            }
            ModContext mod = ModContext.get();
            ItemStack inHand = player.getCurrentEquippedItem();
            if (!mod.itemBridge().isDeathNoteItem(inHand)) {
                player.addChatMessage(new ChatComponentText(mod.messages().get("result.invalidItem")));
                return;
            }

            final DNPlayer actor = new Forge1710DNPlayer(player);
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
            ItemStack inHand = player.getCurrentEquippedItem();
            if (inHand == null) {
                return;
            }
            int slot = player.inventory.currentItem;
            if (inHand.stackSize <= 1) {
                player.inventory.setInventorySlotContents(slot, null);
            } else {
                inHand.stackSize--;
            }
        }
    }
}
