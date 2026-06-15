package com.yaazyy.deathnote.forge1122.network;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.forge1122.Forge1122DNPlayer;
import com.yaazyy.deathnote.forge1122.ModContext;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class DeathNoteNetwork {

    public static final SimpleNetworkWrapper CHANNEL =
            net.minecraftforge.fml.common.network.NetworkRegistry.INSTANCE.newSimpleChannel("deathnote");

    private DeathNoteNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(Handler.class, SubmitDeathNotePacket.class, 0, Side.SERVER);
    }

    public static final class SubmitDeathNotePacket implements IMessage {

        private String firstLine;

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
    }

    public static final class Handler implements IMessageHandler<SubmitDeathNotePacket, IMessage> {

        @Override
        public IMessage onMessage(SubmitDeathNotePacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            final String line = message.firstLine == null ? "" : message.firstLine.trim();
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    handleServer(player, line);
                }
            });
            return null;
        }

        private static void handleServer(EntityPlayerMP player, String firstLine) {
            if (player == null) {
                return;
            }
            ModContext mod = ModContext.get();
            ItemStack inHand = player.getHeldItemMainhand();
            if (!mod.itemBridge().isDeathNoteItem(inHand)) {
                player.sendMessage(new TextComponentString(mod.messages().get("result.invalidItem")));
                return;
            }

            final DNPlayer actor = new Forge1122DNPlayer(player);
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
            player.sendMessage(new TextComponentString(
                    mod.messages().get(key, "target", target == null ? "?" : target)));
        }

        private static void consumeOne(EntityPlayerMP player) {
            ItemStack inHand = player.getHeldItemMainhand();
            if (inHand.isEmpty()) {
                return;
            }
            int amount = inHand.getCount();
            if (amount <= 1) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
            } else {
                inHand.shrink(1);
            }
        }
    }
}
