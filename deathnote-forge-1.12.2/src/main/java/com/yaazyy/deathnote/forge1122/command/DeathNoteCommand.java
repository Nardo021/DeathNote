package com.yaazyy.deathnote.forge1122.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.forge1122.Forge1122DNPlayer;
import com.yaazyy.deathnote.forge1122.ModContext;
import com.yaazyy.deathnote.forge1122.recipe.ForgeRecipeLayout;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public final class DeathNoteCommand extends CommandBase {

    private static final String COMMAND_NAME = "deathnote";

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("dn");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/deathnote <give|reload|info|recipe|help>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            help(sender);
            return;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "give":
                if (!canGive(sender)) {
                    sender.sendMessage(new TextComponentString(msg("command.noPermission")));
                    return;
                }
                if (args.length < 2) {
                    sender.sendMessage(new TextComponentString(msg("command.giveUsage")));
                    return;
                }
                EntityPlayerMP target = getPlayer(server, sender, args[1]);
                give(sender, target);
                break;
            case "reload":
                if (!canAdmin(sender)) {
                    sender.sendMessage(new TextComponentString(msg("command.noPermission")));
                    return;
                }
                reload(sender);
                break;
            case "info":
                if (!canUse(sender)) {
                    sender.sendMessage(new TextComponentString(msg("command.noPermission")));
                    return;
                }
                info(sender);
                break;
            case "recipe":
                if (!canUse(sender)) {
                    sender.sendMessage(new TextComponentString(msg("command.noPermission")));
                    return;
                }
                recipe(sender);
                break;
            case "help":
                help(sender);
                break;
            default:
                help(sender);
                break;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "give", "reload", "info", "recipe", "help");
        }
        if (args.length == 2 && "give".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }

    private static boolean canUse(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canUseCommand(2, COMMAND_NAME);
        }
        return ModContext.get().platform().hasPermission(
                new Forge1122DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().use);
    }

    private static boolean canGive(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canUseCommand(4, COMMAND_NAME);
        }
        return ModContext.get().platform().hasPermission(
                new Forge1122DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().give);
    }

    private static boolean canAdmin(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canUseCommand(4, COMMAND_NAME);
        }
        return ModContext.get().platform().hasPermission(
                new Forge1122DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().admin);
    }

    private static void give(ICommandSender sender, EntityPlayerMP target) {
        ModContext mod = ModContext.get();
        ItemStack note = mod.itemBridge().createDeathNoteItem(target.getUniqueID());
        if (!target.inventory.addItemStackToInventory(note)) {
            target.dropItem(note, false);
        }
        target.sendMessage(new TextComponentString(msg("command.received")));
        sender.sendMessage(new TextComponentString(
                msg("command.gaveItem", "target", target.getName())));
    }

    private static void reload(ICommandSender sender) {
        ModContext.get().reload();
        sender.sendMessage(new TextComponentString(msg("command.reloaded")));
    }

    private static void info(ICommandSender sender) {
        ModContext mod = ModContext.get();
        sender.sendMessage(new TextComponentString(TextColors.translate(mod.messages().raw("info.header"))));
        sender.sendMessage(new TextComponentString(" platform: " + mod.platform().platformName()
                + " " + mod.platform().platformVersion()));
        sender.sendMessage(new TextComponentString(" recipe: " + mod.config().recipe().difficultyPreset
                + " (enabled=" + mod.config().recipe().enabled + ")"));
        sender.sendMessage(new TextComponentString(" cooldown: " + mod.config().cooldown().seconds + "s (enabled="
                + mod.config().cooldown().enabled + ")"));
    }

    private static void recipe(ICommandSender sender) {
        ModContext mod = ModContext.get();
        String preset = mod.config().recipe().difficultyPreset;
        String[] shape = ForgeRecipeLayout.shapeForPreset(preset);
        sender.sendMessage(new TextComponentString(TextColors.translate(mod.messages().raw("recipe.header"))));
        sender.sendMessage(new TextComponentString(" preset: " + preset));
        if (shape == null) {
            sender.sendMessage(new TextComponentString(" (recipe disabled)"));
        } else {
            for (String row : shape) {
                sender.sendMessage(new TextComponentString("  " + spaced(row)));
            }
            sender.sendMessage(new TextComponentString(" W=WitherSkeletonSkull N=NetherStar E=EyeOfEnder"));
            sender.sendMessage(new TextComponentString(" B=WritableBook I=InkSac T=NameTag"));
            sender.sendMessage(new TextComponentString(" G=GhastTear D=DiamondBlock"));
        }
    }

    private static void help(ICommandSender sender) {
        ModContext mod = ModContext.get();
        sender.sendMessage(new TextComponentString(TextColors.translate(mod.messages().raw("help.header"))));
        for (String line : new String[] {
                " /dn give <player>",
                " /dn reload",
                " /dn info",
                " /dn recipe",
                " /dn help"
        }) {
            sender.sendMessage(new TextComponentString(line));
        }
    }

    private static String msg(String key, String... pairs) {
        return TextColors.translate(ModContext.get().messages().get(key, pairs));
    }

    private static String spaced(String row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(row.charAt(i));
        }
        return sb.toString();
    }
}
