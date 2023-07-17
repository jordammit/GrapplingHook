package me.albus.grapplinghook.Commands.list;

import me.albus.grapplinghook.Commands.SubCommand;
import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.entity.Player;

import java.util.List;

public class reload extends SubCommand {


    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "gh.admin";
    }

    @Override
    public String getSyntax() {
        return "/grapplinghook reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        Notify notify = GrapplingHook.getInstance().getNotify();
        if(args.length != 1) {
            player.sendMessage(notify.chatMessage("syntax").replace("%this%", getSyntax()));
            return;
        }

        GrapplingHook.getInstance().config().reload();
        GrapplingHook.getInstance().getNotify().reload();
        player.sendMessage(notify.chatMessage("plugin_reloaded"));
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
