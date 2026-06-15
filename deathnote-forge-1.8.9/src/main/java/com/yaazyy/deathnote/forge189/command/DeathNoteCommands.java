package com.yaazyy.deathnote.forge189.command;

import java.util.Arrays;
import java.util.List;

import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.forge189.Forge189DNPlayer;
import com.yaazyy.deathnote.forge189.ModContext;
import com.yaazyy.deathnote.forge189.recipe.Forge189RecipeLayout;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public final class DeathNoteCommands extends CommandBase {

    @Override
    public String getCommandName() {
        return "deathnote";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("dn");
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/deathnote <give|reload|info|recipe|help> ...";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            help(sender);
            return;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "give":
                give(sender, args);
                break;
            case "reload":
                reload(sender);
                break;
            case "info":
                info(sender);
                break;
            case "recipe":
                recipe(sender);
                break;
            case "help":
            default:
                help(sender);
                break;
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "give", "reload", "info", "recipe", "help");
        }
        if (args.length == 2 && "give".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        return super.addTabCompletionOptions(sender, args, pos);
    }

    private void give(ICommandSender sender, String[] args) throws CommandException {
        if (!canGive(sender)) {
            throw new CommandException(ModContext.get().messages().get("command.noPermission"));
        }
        if (args.length < 2) {
            throw new CommandException(ModContext.get().messages().get("command.giveUsage"));
        }
        EntityPlayerMP target = getPlayer(sender, args[1]);
        ModContext mod = ModContext.get();
        ItemStack note = mod.itemBridge().createDeathNoteItem(target.getUniqueID());
        if (!target.inventory.addItemStackToInventory(note)) {
            target.entityDropItem(note, 0.0F);
        }
        target.addChatMessage(new net.minecraft.util.ChatComponentText(mod.messages().get("command.received")));
        if (sender instanceof EntityPlayerMP) {
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                    mod.messages().get("command.gaveItem", "target", target.getName())));
        } else {
            notifyOperators(sender, this, 2, mod.messages().get("command.gaveItem", "target", target.getName()),
                    new Object[]{target.getName()});
        }
    }

    private void reload(ICommandSender sender) throws CommandException {
        if (!canAdmin(sender)) {
            throw new CommandException(ModContext.get().messages().get("command.noPermission"));
        }
        ModContext.get().reload();
        String msg = ModContext.get().messages().get("command.reloaded");
        if (sender instanceof EntityPlayerMP) {
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(msg));
        } else {
            notifyOperators(sender, this, 2, msg, new Object[0]);
        }
    }

    private void info(ICommandSender sender) throws CommandException {
        if (!canUse(sender)) {
            throw new CommandException(ModContext.get().messages().get("command.noPermission"));
        }
        ModContext mod = ModContext.get();
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                TextColors.translate(mod.messages().raw("info.header"))));
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                " platform: " + mod.platform().platformName() + " " + mod.platform().platformVersion()));
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                " recipe: " + mod.config().recipe().difficultyPreset
                        + " (enabled=" + mod.config().recipe().enabled + ")"));
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                " cooldown: " + mod.config().cooldown().seconds + "s (enabled="
                        + mod.config().cooldown().enabled + ")"));
    }

    private void recipe(ICommandSender sender) throws CommandException {
        if (!canUse(sender)) {
            throw new CommandException(ModContext.get().messages().get("command.noPermission"));
        }
        ModContext mod = ModContext.get();
        String preset = mod.config().recipe().difficultyPreset;
        String[] shape = Forge189RecipeLayout.shapeForPreset(preset);
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                TextColors.translate(mod.messages().raw("recipe.header"))));
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(" preset: " + preset));
        if (shape == null) {
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(" (recipe disabled)"));
        } else {
            for (String row : shape) {
                sender.addChatMessage(new net.minecraft.util.ChatComponentText("  " + spaced(row)));
            }
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(" W=WitherSkeletonSkull N=NetherStar E=EyeOfEnder"));
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(" B=WritableBook I=InkSac T=NameTag"));
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(" G=GhastTear D=DiamondBlock"));
        }
    }

    private void help(ICommandSender sender) {
        ModContext mod = ModContext.get();
        sender.addChatMessage(new net.minecraft.util.ChatComponentText(
                TextColors.translate(mod.messages().raw("help.header"))));
        for (String line : new String[] {
                " /dn give <player>",
                " /dn reload",
                " /dn info",
                " /dn recipe",
                " /dn help"
        }) {
            sender.addChatMessage(new net.minecraft.util.ChatComponentText(line));
        }
    }

    private static boolean canUse(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canCommandSenderUseCommand(2, getCommandNameStatic());
        }
        return ModContext.get().platform().hasPermission(
                new Forge189DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().use);
    }

    private static boolean canGive(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canCommandSenderUseCommand(4, getCommandNameStatic());
        }
        return ModContext.get().platform().hasPermission(
                new Forge189DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().give);
    }

    private static boolean canAdmin(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canCommandSenderUseCommand(4, getCommandNameStatic());
        }
        return ModContext.get().platform().hasPermission(
                new Forge189DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().admin);
    }

    private static String getCommandNameStatic() {
        return "deathnote";
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
