package me.albus.grapplinghook.Commands;

import org.bukkit.entity.Player;

import java.util.List;
@SuppressWarnings("all")
public abstract class SubCommand {
    public abstract String getName();

    public abstract String getPermission();

    public abstract String getSyntax();

    //code for the subcommand
    public abstract void perform(Player player, String args[]);

    public abstract List<String> getSubcommandArguments(Player player, String args[]);

}