package me.albus.grapplinghook;

import me.albus.grapplinghook.Commands.CommandManager;
import me.albus.grapplinghook.Listener.CraftEvent;
import me.albus.grapplinghook.Listener.FishEvent;
import me.albus.grapplinghook.Listener.JoinEvent;
import me.albus.grapplinghook.Utils.Config;
import me.albus.grapplinghook.Utils.CooldownManager;
import me.albus.grapplinghook.Utils.Notify;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;


public final class GrapplingHook extends JavaPlugin {

    private static GrapplingHook instance;
    private Config config;
    private CooldownManager cooldownManager;

    private Notify notify;
    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        config = new Config();
        config.setup();

        notify = new Notify();

        notify.setup();
        notify.initialize();
        notify.get().options().copyDefaults(true);
        notify.save();

        cooldownManager = new CooldownManager();

        getCommand("grapplinghook").setExecutor(new CommandManager());

        getServer().getPluginManager().registerEvents(new FishEvent(), this);

        if(config.get().getBoolean("Settings.give_on_join")) {
            getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        }

        if(config.get().getBoolean("Settings.crafting.enabled")) {
            loadRecipe();
            getServer().getPluginManager().registerEvents(new CraftEvent(), this);
        }

        new Metrics(this, 18564);
    }

    public Config config() {
        return config;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }


    public static GrapplingHook getInstance() {
        return instance;
    }

    public Notify getNotify() {
        return notify;
    }

    public ItemStack getHook() {
        ItemStack item = new ItemStack(Material.FISHING_ROD, 1);
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(getInstance(), "hook");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "hook");

        if(config().get().getBoolean("Settings.uses.enabled")) {
            int uses = config().get().getInt("Settings.uses.amount");
            NamespacedKey counter = new NamespacedKey(GrapplingHook.getInstance(), "uses");
            meta.getPersistentDataContainer().set(counter, PersistentDataType.INTEGER, uses);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(notify.message("stats_uses").replace("%this%", String.valueOf(uses)));
            meta.setLore(lore);
        }
        meta.setDisplayName(notify.message("plugin_prefix"));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isInteger(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        int startIndex = 0;
        if (input.charAt(0) == '-' || input.charAt(0) == '+') {
            if (input.length() == 1) {
                return false; // Single '+' or '-' is not a valid integer
            }
            startIndex = 1;
        }

        for (int i = startIndex; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (!Character.isDigit(ch)) {
                return false;
            }
        }

        return true;
    }

    public Recipe getRecipe() {
        ItemStack item = getHook();

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "gh"), item);
        recipe.shape("S  ", "DS ", "D S");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('D', Material.EMERALD);
        return recipe;
    }

    public void loadRecipe() {
        Bukkit.addRecipe(getRecipe());
    }
}
