package me.albus.grapplinghook.Listener;

import me.albus.grapplinghook.GrapplingHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import org.bukkit.inventory.Recipe;

public class CraftEvent implements Listener {

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();

        Recipe craftedRecipe = event.getRecipe();
        Recipe targetRecipe = GrapplingHook.getInstance().getRecipe();

        if (craftedRecipe != null) {
            if (craftedRecipe.getResult().isSimilar(targetRecipe.getResult())) {
                if (!(player.hasPermission("gh.craft") || player.hasPermission("gh.admin") || player.isOp())) {
                    event.getInventory().setResult(null);
                }
            }
        }
    }
    
}
