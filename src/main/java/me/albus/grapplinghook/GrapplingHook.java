package me.albus.grapplinghook;

import me.albus.grapplinghook.Commands.CommandManager;
import me.albus.grapplinghook.GUI.MenuListener;
import me.albus.grapplinghook.GUI.MenuUtilities;
import me.albus.grapplinghook.Listener.CraftEvent;
import me.albus.grapplinghook.Listener.FishEvent;
import me.albus.grapplinghook.Listener.JoinEvent;
import me.albus.grapplinghook.Utils.Config;
import me.albus.grapplinghook.Utils.CooldownManager;
import me.albus.grapplinghook.Utils.Notify;
import me.albus.grapplinghook.Utils.Settings.SettingsHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public final class GrapplingHook extends JavaPlugin {

    private static GrapplingHook instance;
    private Config config;
    private CooldownManager cooldownManager;

    private SettingsHandler settingsHandler;

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

        Objects.requireNonNull(getCommand("grapplinghook")).setExecutor(new CommandManager());

        getServer().getPluginManager().registerEvents(new FishEvent(), this);

        getServer().getPluginManager().registerEvents(new JoinEvent(), this);

        if(config.get().getBoolean("Settings.crafting.enabled")) {
            loadRecipe();
            getServer().getPluginManager().registerEvents(new CraftEvent(), this);
        }

        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        new Metrics(this, 18564);

        Bukkit.getLogger().info("[GrapplingHook] has been loaded correctly.");

        settingsHandler = new SettingsHandler();

    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("[GrapplingHook] has been unloaded correctly.");
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
        if(meta != null) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "hook");
            meta.setDisplayName(config.get().getString("Settings.itemname"));
            if(config.get().getBoolean("Settings.cmd.enabled")) {
            	meta.setCustomModelData(this.config.get().getInt("Settings.cmd.id"));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isInteger(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        int startIndex = 0;
        if (input.charAt(0) == '-' || input.charAt(0) == '+') {
            if (input.length() == 1) {
                return false;
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
        ItemMeta meta = item.getItemMeta();

        if(config().get().getBoolean("Settings.uses.enabled")) {
            int uses = config().get().getInt("Settings.uses.amount");

            if(uses < 1) {
                uses = 60;
            }

            NamespacedKey counter = new NamespacedKey(GrapplingHook.getInstance(), "uses");
            if(meta != null) {
                meta.getPersistentDataContainer().set(counter, PersistentDataType.INTEGER, uses);
                ArrayList<String> lore = new ArrayList<>();
                lore.add(notify.message("stats_uses").replace("%this%", String.valueOf(uses)));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "gh"), item);
        recipe.shape("S  ", "DS ", "D S");
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('D', Material.EMERALD);
        return recipe;
    }

    public int getRandom() {
        Random random = new Random();

        int min = config().get().getInt("Settings.uses.random.min");
        int max = config().get().getInt("Settings.uses.random.max");
        if (min < 1) {
            min = 1;
        }

        if (max < 1) {
            max = 99;
        }

        return random.nextInt(max - min + 1) + min;
    }

    public void loadRecipe() {
        Bukkit.addRecipe(getRecipe());
    }

    private static final HashMap<Player, MenuUtilities> menuUtilitiesMap = new HashMap<>();

    public static MenuUtilities menuUtilities(Player p) {
        MenuUtilities playerMenuUtility;
        if (!(menuUtilitiesMap.containsKey(p))) {
            playerMenuUtility = new MenuUtilities(p);
            menuUtilitiesMap.put(p, playerMenuUtility);
            return playerMenuUtility;
        } else {
            return menuUtilitiesMap.get(p);
        }
    }

    public SettingsHandler getSettingsHandler() {
        return settingsHandler;
    }
}
