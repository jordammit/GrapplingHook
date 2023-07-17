package me.albus.grapplinghook.Commands.list;

import me.albus.grapplinghook.Commands.SubCommand;
import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.entity.Player;

import java.util.List;

public class set extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getPermission() {
        return "gh.admin";
    }

    @Override
    public String getSyntax() {
        return "/grapplinghook set velocity <number> | /grapplinghook set cooldown <number>";
    }

    @Override
    public void perform(Player player, String[] args) {
        Notify notify = GrapplingHook.getInstance().getNotify();
        if(args.length != 3) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        int value = 0;

        if (args[2] == null || args[2].isEmpty()) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        if(isInteger(args[2])) {
            value = Integer.parseInt(args[2]);
        }

        if(args[1].equalsIgnoreCase("velocity") && !args[2].isEmpty() && value > 0) {
            GrapplingHook.getInstance().config().get().set("Settings.velocity", value);
            GrapplingHook.getInstance().config().save();
            GrapplingHook.getInstance().config().reload();
            player.sendMessage(notify.chatMessage("set").replace("%this%", "velocity").replace("%that%", String.valueOf(value)));
            return;
        }

        if(args[1].equalsIgnoreCase("cooldown") && !args[2].isEmpty() && value > 0) {
            GrapplingHook.getInstance().config().get().set("Settings.cooldown", value);
            GrapplingHook.getInstance().config().save();
            GrapplingHook.getInstance().config().reload();
            player.sendMessage(notify.chatMessage("set").replace("%this%", "cooldown").replace("%that%", String.valueOf(value)));
            return;
        }

        player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
    }

    public boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
