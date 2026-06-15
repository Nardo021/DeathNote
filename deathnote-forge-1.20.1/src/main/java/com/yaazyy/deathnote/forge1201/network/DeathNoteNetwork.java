package com.yaazyy.deathnote.forge1201.network;

import java.util.function.Supplier;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.forge1201.Forge1201DNPlayer;
import com.yaazyy.deathnote.forge1201.ModContext;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class DeathNoteNetwork {

    private static final String PROTOCOL = "1";
  public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new net.minecraft.resources.ResourceLocation("deathnote", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private DeathNoteNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SubmitDeathNotePacket.class,
                SubmitDeathNotePacket::encode,
                SubmitDeathNotePacket::decode,
                SubmitDeathNotePacket::handle);
    }

    public static final class SubmitDeathNotePacket {

        private final String firstLine;

        public SubmitDeathNotePacket(String firstLine) {
            this.firstLine = firstLine == null ? "" : firstLine;
        }

        public static void encode(SubmitDeathNotePacket msg, FriendlyByteBuf buf) {
            buf.writeUtf(msg.firstLine, 256);
        }

        public static SubmitDeathNotePacket decode(FriendlyByteBuf buf) {
            return new SubmitDeathNotePacket(buf.readUtf(256));
        }

        public static void handle(SubmitDeathNotePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> handleServer(ctx.getSender(), msg.firstLine));
            ctx.setPacketHandled(true);
        }

        private static void handleServer(ServerPlayer player, String firstLine) {
            if (player == null) {
                return;
            }
            ModContext mod = ModContext.get();
            ItemStack inHand = player.getMainHandItem();
            if (!mod.itemBridge().isDeathNoteItem(inHand)) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        mod.messages().get("result.invalidItem")));
                return;
            }

            final DNPlayer actor = new Forge1201DNPlayer(player);
            final String line = firstLine;
            mod.platform().runLater(() -> {
                DeathNoteResult result = mod.service().process(actor, line);
                sendResult(player, result, line);
                if (result == DeathNoteResult.SUCCESS && mod.config().input().consumeOnUse) {
                    consumeOne(player);
                }
            }, mod.config().kill().delayTicks);
        }

        private static void sendResult(ServerPlayer player, DeathNoteResult result, String target) {
            ModContext mod = ModContext.get();
            String key;
            switch (result) {
                case SUCCESS:             key = "result.success"; break;
                case INVALID_ITEM:        key = "result.invalidItem"; break;
                case INVALID_NAME:        key = "result.invalidName"; break;
                case TARGET_NOT_FOUND:    key = "result.targetNotFound"; break;
                case SELF_TARGET_BLOCKED: key = "result.selfBlocked"; break;
                case NO_PERMISSION:       key = "result.noPermission"; break;
                case TARGET_IMMUNE:       key = "result.targetImmune"; break;
                case COOLDOWN_ACTIVE:     key = "result.cooldown"; break;
                case KILL_FAILED:         key = "result.killFailed"; break;
                case CONFIG_ERROR:        key = "result.configError"; break;
                default:                  key = "result.killFailed"; break;
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    mod.messages().get(key, "target", target == null ? "?" : target)));
        }

        private static void consumeOne(ServerPlayer player) {
            ItemStack inHand = player.getMainHandItem();
            if (inHand.isEmpty()) {
                return;
            }
            int amount = inHand.getCount();
            if (amount <= 1) {
                player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
            } else {
                inHand.shrink(1);
            }
        }
    }
}
