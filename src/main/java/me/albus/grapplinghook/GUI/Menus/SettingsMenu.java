package me.albus.grapplinghook.GUI.Menus;

import me.albus.grapplinghook.GUI.Menu;
import me.albus.grapplinghook.GUI.MenuUtilities;
import me.albus.grapplinghook.GrapplingHook;
import me.albus.grapplinghook.Utils.Config;
import me.albus.grapplinghook.Utils.Notify;
import me.albus.grapplinghook.Utils.Settings.SettingsHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class SettingsMenu extends Menu {

    private Notify notify;
    private YamlConfiguration configuration;

    private SettingsHandler settingsHandler;

    private int velocity;
    private int cooldown;

    private boolean breakSound;

    private boolean useSound;

    private Config config;

    public SettingsMenu(MenuUtilities menuUtilities) {
        super(menuUtilities);

        notify = GrapplingHook.getInstance().getNotify();

        config = GrapplingHook.getInstance().config();

        settingsHandler = GrapplingHook.getInstance().getSettingsHandler();

        configuration = config.get();
    }

    @Override
    public String getMenuName() {
        return notify.color("&#BE8CFFGrappling Hook &#EBC7FFSettings");
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();

        Player player = (Player) event.getWhoClicked();

        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        String click = dataContainer.get(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING);

        if(click == null || click.isEmpty()) {
            return;
        }

        if(cooldown == 0) {
            cooldown = 1;
        }

        if(click.equalsIgnoreCase("velocity")) {
            velocity = settingsHandler.getVelocity(player);
            velocity = velocity + 1;
            settingsHandler.updateVelocity(player, velocity);
            player.sendMessage(notify.chatMessage("set").replace("%this%", "velocity").replace("%that%", String.valueOf(velocity)));
            super.open();
        } else if(click.equalsIgnoreCase("cooldown")) {
            cooldown = settingsHandler.getCooldown(player);
            cooldown = cooldown + 1;
            settingsHandler.updateCooldown(player, cooldown);
            player.sendMessage(notify.chatMessage("set").replace("%this%", "cooldown").replace("%that%", String.valueOf(cooldown)));
            super.open();
        } else if (click.equalsIgnoreCase("break_sound")) {
            breakSound = settingsHandler.getToggleBreakSound(player);
            if(breakSound == false) {
                breakSound = true;
            } else {
                breakSound = false;
            }
            settingsHandler.updateToggleBreakSound(player, breakSound);
            player.sendMessage(notify.chatMessage("set").replace("%this%", "Break Sound").replace("%that%", String.valueOf(breakSound)));
            super.open();
        } else if (click.equalsIgnoreCase("use_sound")) {
            useSound = settingsHandler.getToggleSound(player);
            if(useSound == false) {
                useSound = true;
            } else {
                useSound = false;
            }
            settingsHandler.updateToggleSound(player, useSound);
            player.sendMessage(notify.chatMessage("set").replace("%this%", "Use Sound").replace("%that%", String.valueOf(useSound)));
            super.open();
        } else if(click.equalsIgnoreCase("save")) {
            
            velocity = settingsHandler.getVelocity(player);
            cooldown = settingsHandler.getCooldown(player);
            
            breakSound = settingsHandler.getToggleBreakSound(player);
            useSound = settingsHandler.getToggleSound(player);

            configuration.set("Settings.velocity", velocity);
            configuration.set("Settings.cooldown", cooldown);

            configuration.set("Settings.uses.sound.enabled", breakSound);
            configuration.set("Settings.sound.enabled", useSound);
            config.save();
            config.reload();
            player.closeInventory();
            player.sendMessage(notify.chatMessage("config_updated"));
            settingsHandler.purge(player);
        }

    }

    @Override
    public void setMenuItems() {
        Player player = menuUtilities.getOwner();

        inventory.setItem(0, setVelocityItem(player));
        inventory.setItem(1, setCooldownItem(player));
        inventory.setItem(2, toggleUseSound(player));
        inventory.setItem(3, toggleSoundBreak(player));
        inventory.setItem(8, saveButton());
    }

    private ItemStack setVelocityItem(Player player) {
        int cc = settingsHandler.getVelocity(player);

        if(cc == 0) {
            velocity = configuration.getInt("Settings.velocity");
        } else {
            velocity = cc;
        }

        if(velocity == 0 || velocity > 10) {
            velocity = 1;
        }

        settingsHandler.updateVelocity(player, velocity);

        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "velocity");
        meta.setDisplayName(notify.color("&eCurrent Velocity is &b" + this.velocity));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bClick to increase the velocity."));
        lore.add(notify.color("&7The max value is 10."));
        lore.add(notify.color("&7The velocity will be set to 1 after 10."));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setCooldownItem(Player player) {
        int cc = settingsHandler.getCooldown(player);

        if(cc == 0) {
            cooldown = configuration.getInt("Settings.cooldown");
        } else {
            cooldown = cc;
        }

        if(cooldown == 0 || cooldown > 300) {
            cooldown = 1;
        }

        settingsHandler.updateCooldown(player, cooldown);

        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "cooldown");
        meta.setDisplayName(notify.color("&eCurrent Cooldown is &b" + this.cooldown));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bClick to increase the cooldown."));
        lore.add(notify.color("&7The max value is 300."));
        lore.add(notify.color("&7The cooldown will be set to 1 after 300."));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack toggleSoundBreak(Player player) {
        boolean cc = settingsHandler.getToggleBreakSound(player);
        breakSound = configuration.getBoolean("Settings.uses.sound.enabled");
        String value = "false";
        if(cc == false) {
            if(breakSound == true) {
                breakSound = true;
                value = "true";
            }
        } else {
            breakSound = true;
            value = "true";
        }
        ItemStack item = new ItemStack(Material.MUSIC_DISC_5);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "break_sound");
        meta.setDisplayName(notify.color("&eCurrent Break Sound is &b" + value));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bToggle break sound."));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack toggleUseSound(Player player) {
        boolean cc = settingsHandler.getToggleSound(player);
        useSound = configuration.getBoolean("Settings.sound.enabled");
        String value = "false";
        if(cc == false) {
            if(useSound == true) {
                useSound = true;
                value = "true";
            }
        } else {
            useSound = true;
            value = "true";
        }

        ItemStack item = new ItemStack(Material.MUSIC_DISC_13);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "use_sound");
        meta.setDisplayName(notify.color("&eCurrent Use Sound is &b" + value));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bToggle use sound."));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack saveButton() {
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "save");
        meta.setDisplayName(notify.color("&a&lClick To Save"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bThis will save and reload the config"));
        lore.add(notify.color("&7With the updated values."));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
