package com.yaazyy.deathnote.forge1710.command;

public final class DeathNoteDnCommand extends DeathNoteCommand {

    @Override
    public String getCommandName() {
        return "dn";
    }

    @Override
    public String getCommandUsage(net.minecraft.command.ICommandSender sender) {
        return "/dn <give|reload|info|recipe|help> ...";
    }
}
