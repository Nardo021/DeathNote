package com.yaazyy.deathnote.bukkit.common.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.yaazyy.deathnote.bukkit.common.AbstractDeathNotePlugin;
import com.yaazyy.deathnote.bukkit.common.recipe.RecipeLayout;

/**
 * Handler for {@code /deathnote} (alias {@code /dn}) and its subcommands:
 * {@code give}, {@code reload}, {@code info}, {@code debug}, {@code recipe},
 * {@code help}.
 */
public final class DeathNoteCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS =
            Arrays.asList("give", "reload", "info", "debug", "recipe", "help");

    private final AbstractDeathNotePlugin plugin;

    public DeathNoteCommand(AbstractDeathNotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return help(sender);
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "give":   return give(sender, args);
            case "reload": return reload(sender);
            case "info":   return info(sender);
            case "debug":  return debug(sender, args);
            case "recipe": return recipe(sender);
            case "help":
            default:       return help(sender);
        }
    }

    private boolean give(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.dnConfig().permissions().give)) {
            sender.sendMessage(plugin.messages().get("command.noPermission"));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.messages().get("command.giveUsage"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.messages().get("command.playerNotFound", "target", args[1]));
            return true;
        }
        ItemStack note = plugin.itemBridge().createDeathNoteItem(target.getUniqueId());
        target.getInventory().addItem(note);
        target.sendMessage(plugin.messages().get("command.received"));
        sender.sendMessage(plugin.messages().get("command.gaveItem", "target", target.getName()));
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!sender.hasPermission(plugin.dnConfig().permissions().admin)) {
            sender.sendMessage(plugin.messages().get("command.noPermission"));
            return true;
        }
        plugin.reloadEverything();
        sender.sendMessage(plugin.messages().get("command.reloaded"));
        return true;
    }

    private boolean info(CommandSender sender) {
        if (!sender.hasPermission(plugin.dnConfig().permissions().use)) {
            sender.sendMessage(plugin.messages().get("command.noPermission"));
            return true;
        }
        sender.sendMessage(plugin.messages().raw("info.header"));
        sender.sendMessage(" platform: " + plugin.platform().platformName()
                + " " + plugin.platform().platformVersion());
        sender.sendMessage(" recipe: " + plugin.dnConfig().recipe().difficultyPreset
                + " (enabled=" + plugin.dnConfig().recipe().enabled + ")");
        sender.sendMessage(" cooldown: " + plugin.dnConfig().cooldown().seconds + "s (enabled="
                + plugin.dnConfig().cooldown().enabled + ")");
        sender.sendMessage(" customModelData: " + plugin.dnConfig().item().customModelData);
        return true;
    }

    private boolean debug(CommandSender sender, String[] args) {
        if (!sender.hasPermission(plugin.dnConfig().permissions().admin)) {
            sender.sendMessage(plugin.messages().get("command.noPermission"));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.messages().get("command.playersOnly"));
            return true;
        }
        Player player = (Player) sender;
        if (args.length >= 2 && args[1].equalsIgnoreCase("item")) {
            ItemStack note = plugin.itemBridge().createDeathNoteItem(player.getUniqueId());
            player.getInventory().addItem(note);
            String sig = plugin.itemBridge().readSignature(note).orElse("(none)");
            player.sendMessage(plugin.messages().raw("debug.itemGiven"));
            player.sendMessage(" signature: " + sig);
            player.sendMessage(" valid: " + plugin.itemBridge().isDeathNoteItem(note));
            return true;
        }
        sender.sendMessage(plugin.messages().get("command.debugUsage"));
        return true;
    }

    private boolean recipe(CommandSender sender) {
        if (!sender.hasPermission(plugin.dnConfig().permissions().use)) {
            sender.sendMessage(plugin.messages().get("command.noPermission"));
            return true;
        }
        String preset = plugin.dnConfig().recipe().difficultyPreset;
        String[] shape = RecipeLayout.shapeForPreset(preset);
        sender.sendMessage(plugin.messages().raw("recipe.header"));
        sender.sendMessage(" preset: " + preset);
        if (shape == null) {
            sender.sendMessage(" (recipe disabled)");
        } else {
            for (String row : shape) {
                sender.sendMessage("  " + spaced(row));
            }
            sender.sendMessage(" W=WitherSkeletonSkull N=NetherStar E=EyeOfEnder");
            sender.sendMessage(" B=WritableBook I=InkSac T=NameTag");
            sender.sendMessage(" G=GhastTear D=DiamondBlock");
        }
        return true;
    }

    private boolean help(CommandSender sender) {
        sender.sendMessage(plugin.messages().raw("help.header"));
        sender.sendMessage(" /dn give <player>");
        sender.sendMessage(" /dn reload");
        sender.sendMessage(" /dn info");
        sender.sendMessage(" /dn debug item");
        sender.sendMessage(" /dn recipe");
        sender.sendMessage(" /dn help");
        return true;
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> out = new ArrayList<>();
            for (String s : SUBCOMMANDS) {
                if (s.startsWith(args[0].toLowerCase())) {
                    out.add(s);
                }
            }
            return out;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // Bukkit.matchPlayer keeps a stable signature across 1.7 <-> 1.8,
            // unlike getOnlinePlayers() (array vs collection).
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.matchPlayer(args[1])) {
                names.add(p.getName());
            }
            return names;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return Arrays.asList("item");
        }
        return new ArrayList<>();
    }
}
