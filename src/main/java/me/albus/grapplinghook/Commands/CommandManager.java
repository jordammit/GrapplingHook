package me.albus.grapplinghook.Commands;

import me.albus.grapplinghook.Commands.list.give;
import me.albus.grapplinghook.Commands.list.reload;
import me.albus.grapplinghook.Commands.list.set;
import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Notify;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {
    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    public CommandManager(){
        subcommands.add(new reload());
        subcommands.add(new set());
        subcommands.add(new give());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Notify notify = GrapplingHook.getInstance().getNotify();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                for (int i = 0; i < getSubcommands().size(); i++) {
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                        if (player.hasPermission(getSubcommands().get(i).getPermission()) || player.hasPermission("gh.admin") || player.isOp()) {
                            getSubcommands().get(i).perform(player, args);
                        } else {
                            sender.sendMessage(notify.chatMessage("permission"));
                        }
                    }
                }
            } else {
                if(player.hasPermission("gh.admin") || player.isOp()) {
                    player.sendMessage(notify.color("&c~ &e&lGrappling Hook Plugin &c~"));
                    player.sendMessage(notify.color("&e/grapplinghook give &d<player>"));
                    player.sendMessage(notify.color("&e/grapplinghook set &d<velocity>&e | &d<cooldown> <number>"));
                    player.sendMessage(notify.color("&e/grapplinghook reload"));
                } else {
                    sender.sendMessage(notify.chatMessage("permission"));
                }
            }
        }

        if(sender instanceof ConsoleCommandSender) {
            if(args.length > 0) {
                if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    GrapplingHook.getInstance().config().reload();
                    notify.reload();
                    sender.sendMessage("[GrapplingHook] Config and messages has been reloaded.");
                } else if(args.length == 2 && args[0].equalsIgnoreCase("give")) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target == null && !target.isOnline()) {
                        sender.sendMessage("[GrapplingHook] That player is not online.");
                        return true;
                    }

                    boolean isInventoryFull = target.getInventory().firstEmpty() == -1;
                    if(isInventoryFull) {
                        sender.sendMessage("[GrapplingHook] " + target.getName() + "'s inventory is full.");
                        return true;
                    }

                    ItemStack item = GrapplingHook.getInstance().getHook();

                    target.getInventory().addItem(item);
                    target.sendMessage(notify.chatMessage("received").replace("%this%", "Console").replace("%plugin%", notify.message("plugin_prefix")));
                    sender.sendMessage("[GrapplingHook] you gave " + target.getName() + " a grappling hook.");
                    return true;
                }
            } else {
                sender.sendMessage(notify.color("Grappling Hook"));
                sender.sendMessage("/grapplinghook give <player>");
                sender.sendMessage("/grapplinghook set <velocity | cooldown> <number> || can't do as console");
                sender.sendMessage("/grapplinghook reload");
                return true;
            }
        }


        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1){
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            for (int i = 0; i < getSubcommands().size(); i++){
                if(sender.hasPermission(getSubcommands().get(i).getPermission()) || sender.hasPermission("gh.admin") || sender.isOp()) {
                    subcommandsArguments.add(getSubcommands().get(i).getName());
                }
            }

            return subcommandsArguments;
        }
        return null;
    }

}