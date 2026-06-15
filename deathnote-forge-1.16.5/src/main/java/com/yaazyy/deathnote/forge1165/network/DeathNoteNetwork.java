package com.yaazyy.deathnote.forge1165.network;

import java.util.function.Supplier;

import com.yaazyy.deathnote.api.DNPlayer;
import com.yaazyy.deathnote.core.DeathNoteResult;
import com.yaazyy.deathnote.forge1165.Forge1165DNPlayer;
import com.yaazyy.deathnote.forge1165.ModContext;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class DeathNoteNetwork {

    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("deathnote", "main"),
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

        public static void encode(SubmitDeathNotePacket msg, PacketBuffer buf) {
            buf.writeString(msg.firstLine, 256);
        }

        public static SubmitDeathNotePacket decode(PacketBuffer buf) {
            return new SubmitDeathNotePacket(buf.readString(256));
        }

        public static void handle(SubmitDeathNotePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> handleServer(ctx.getSender(), msg.firstLine));
            ctx.setPacketHandled(true);
        }

        private static void handleServer(ServerPlayerEntity player, String firstLine) {
            if (player == null) {
                return;
            }
            ModContext mod = ModContext.get();
            ItemStack inHand = player.getHeldItemMainhand();
            if (!mod.itemBridge().isDeathNoteItem(inHand)) {
                player.sendMessage(new StringTextComponent(mod.messages().get("result.invalidItem")),
                        player.getUniqueID());
                return;
            }

            final DNPlayer actor = new Forge1165DNPlayer(player);
            final String line = firstLine;
            mod.platform().runLater(() -> {
                DeathNoteResult result = mod.service().process(actor, line);
                sendResult(player, result, line);
                if (result == DeathNoteResult.SUCCESS && mod.config().input().consumeOnUse) {
                    consumeOne(player);
                }
            }, mod.config().kill().delayTicks);
        }

        private static void sendResult(ServerPlayerEntity player, DeathNoteResult result, String target) {
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
            player.sendMessage(new StringTextComponent(mod.messages().get(key, "target",
                    target == null ? "?" : target)), player.getUniqueID());
        }

        private static void consumeOne(ServerPlayerEntity player) {
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
