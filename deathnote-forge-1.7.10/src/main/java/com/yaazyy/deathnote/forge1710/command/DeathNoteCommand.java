package com.yaazyy.deathnote.forge1710.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.yaazyy.deathnote.config.text.TextColors;
import com.yaazyy.deathnote.forge1710.Forge1710DNPlayer;
import com.yaazyy.deathnote.forge1710.ModContext;
import com.yaazyy.deathnote.forge1710.recipe.ForgeRecipeLayout;

public class DeathNoteCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "deathnote";
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
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            help(sender);
            return;
        }
        String sub = args[0].toLowerCase();
        if ("give".equals(sub)) {
            give(sender, args);
        } else if ("reload".equals(sub)) {
            reload(sender);
        } else if ("info".equals(sub)) {
            info(sender);
        } else if ("recipe".equals(sub)) {
            recipe(sender);
        } else if ("help".equals(sub)) {
            help(sender);
        } else {
            throw new net.minecraft.command.WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "give", "reload", "info", "recipe", "help");
        }
        if (args.length == 2 && "give".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        return null;
    }

    protected void give(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayerMP)) {
            if (!sender.canCommandSenderUseCommand(4, getCommandName())) {
                sender.addChatMessage(new ChatComponentText(msg("command.noPermission")));
                return;
            }
            if (args.length < 2) {
                throw new net.minecraft.command.WrongUsageException("/deathnote give <player>");
            }
            EntityPlayerMP target = getPlayer(sender, args[1]);
            giveTo(target, sender);
            return;
        }
        EntityPlayerMP actor = (EntityPlayerMP) sender;
        if (!canGive(actor)) {
            sender.addChatMessage(new ChatComponentText(msg("command.noPermission")));
            return;
        }
        if (args.length < 2) {
            throw new net.minecraft.command.WrongUsageException("/deathnote give <player>");
        }
        EntityPlayerMP target = getPlayer(sender, args[1]);
        giveTo(target, sender);
    }

    private void giveTo(EntityPlayerMP target, ICommandSender source) {
        ModContext mod = ModContext.get();
        ItemStack note = mod.itemBridge().createDeathNoteItem(target.getUniqueID());
        if (!target.inventory.addItemStackToInventory(note)) {
            target.dropPlayerItemWithRandomChoice(note, false);
        }
        target.addChatMessage(new ChatComponentText(msg("command.received")));
        source.addChatMessage(new ChatComponentText(
                msg("command.gaveItem", "target", target.getCommandSenderName())));
    }

    protected void reload(ICommandSender sender) {
        if (!canAdmin(sender)) {
            sender.addChatMessage(new ChatComponentText(msg("command.noPermission")));
            return;
        }
        ModContext.get().reload();
        sender.addChatMessage(new ChatComponentText(msg("command.reloaded")));
    }

    protected void info(ICommandSender sender) {
        if (!canUse(sender)) {
            sender.addChatMessage(new ChatComponentText(msg("command.noPermission")));
            return;
        }
        ModContext mod = ModContext.get();
        sender.addChatMessage(new ChatComponentText(TextColors.translate(mod.messages().raw("info.header"))));
        sender.addChatMessage(new ChatComponentText(" platform: " + mod.platform().platformName()
                + " " + mod.platform().platformVersion()));
        sender.addChatMessage(new ChatComponentText(" recipe: " + mod.config().recipe().difficultyPreset
                + " (enabled=" + mod.config().recipe().enabled + ")"));
        sender.addChatMessage(new ChatComponentText(" cooldown: " + mod.config().cooldown().seconds + "s (enabled="
                + mod.config().cooldown().enabled + ")"));
    }

    protected void recipe(ICommandSender sender) {
        if (!canUse(sender)) {
            sender.addChatMessage(new ChatComponentText(msg("command.noPermission")));
            return;
        }
        ModContext mod = ModContext.get();
        String preset = mod.config().recipe().difficultyPreset;
        String[] shape = ForgeRecipeLayout.shapeForPreset(preset);
        sender.addChatMessage(new ChatComponentText(TextColors.translate(mod.messages().raw("recipe.header"))));
        sender.addChatMessage(new ChatComponentText(" preset: " + preset));
        if (shape == null) {
            sender.addChatMessage(new ChatComponentText(" (recipe disabled)"));
        } else {
            for (String row : shape) {
                sender.addChatMessage(new ChatComponentText("  " + spaced(row)));
            }
            sender.addChatMessage(new ChatComponentText(" W=WitherSkeletonSkull N=NetherStar E=EyeOfEnder"));
            sender.addChatMessage(new ChatComponentText(" B=WritableBook I=InkSac T=NameTag"));
            sender.addChatMessage(new ChatComponentText(" G=GhastTear D=DiamondBlock"));
        }
    }

    protected void help(ICommandSender sender) {
        ModContext mod = ModContext.get();
        sender.addChatMessage(new ChatComponentText(TextColors.translate(mod.messages().raw("help.header"))));
        for (String line : new String[] {
                " /dn give <player>",
                " /dn reload",
                " /dn info",
                " /dn recipe",
                " /dn help"
        }) {
            sender.addChatMessage(new ChatComponentText(line));
        }
    }

    protected boolean canUse(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canCommandSenderUseCommand(2, getCommandName());
        }
        return ModContext.get().platform().hasPermission(new Forge1710DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().use);
    }

    protected boolean canGive(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canCommandSenderUseCommand(4, getCommandName());
        }
        return ModContext.get().platform().hasPermission(new Forge1710DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().give);
    }

    protected boolean canAdmin(ICommandSender sender) {
        if (!(sender instanceof EntityPlayerMP)) {
            return sender.canCommandSenderUseCommand(4, getCommandName());
        }
        return ModContext.get().platform().hasPermission(new Forge1710DNPlayer((EntityPlayerMP) sender),
                ModContext.get().config().permissions().admin);
    }

    protected String msg(String key, String... pairs) {
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
