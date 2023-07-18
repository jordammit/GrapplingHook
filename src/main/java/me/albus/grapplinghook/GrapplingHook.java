package me.albus.grapplinghook;

import me.albus.grapplinghook.Commands.CommandManager;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


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
        NamespacedKey key = new NamespacedKey(GrapplingHook.getInstance(), "hook");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "hook");

        meta.setDisplayName(notify.message("plugin_prefix"));
        item.setItemMeta(meta);
        return item;
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
