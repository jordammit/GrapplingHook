package me.albus.grapplinghook.GUI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MenuUtilities {


    private Player owner;
    private UUID target;

    private ItemStack item;


    public MenuUtilities(Player player) {
        this.owner = player;
    }

    public Player getOwner() {
        return owner;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public UUID getTarget() {
        return target;
    }

    public void setPlayerToKill(UUID target) {
        this.target = target;
    }

}