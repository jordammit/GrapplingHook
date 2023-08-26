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

    private boolean giveOnJoin;

    private Config config;

    private Player player;

    public SettingsMenu(MenuUtilities menuUtilities) {
        super(menuUtilities);

        notify = GrapplingHook.getInstance().getNotify();

        config = GrapplingHook.getInstance().config();

        settingsHandler = GrapplingHook.getInstance().getSettingsHandler();

        configuration = config.get();
    }

    @Override
    public String getMenuName() {
        return notify.color("&#BE8CFFGrappling Hook &8Settings");
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();

        player = (Player) event.getWhoClicked();

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
        } else if(click.equalsIgnoreCase("give_on_join")) {
            giveOnJoin = settingsHandler.getGiveOnJoin(player);
            if(giveOnJoin == false) {
                giveOnJoin = true;
            } else {
                giveOnJoin = false;
            }
            settingsHandler.updateGiveOnJoin(player, giveOnJoin);
            player.sendMessage(notify.chatMessage("set").replace("%this%", "Give On Join").replace("%that%", String.valueOf(giveOnJoin)));
            super.open();
        } else if(click.equalsIgnoreCase("save")) {
            
            velocity = settingsHandler.getVelocity(player);
            cooldown = settingsHandler.getCooldown(player);
            
            breakSound = settingsHandler.getToggleBreakSound(player);
            useSound = settingsHandler.getToggleSound(player);
            giveOnJoin = settingsHandler.getGiveOnJoin(player);

            configuration.set("Settings.velocity", velocity);
            configuration.set("Settings.cooldown", cooldown);

            configuration.set("Settings.uses.sound.enabled", breakSound);
            configuration.set("Settings.sound.enabled", useSound);
            configuration.set("Settings.give_on_join", giveOnJoin);

            config.save();
            config.reload();
            player.closeInventory();
            player.sendMessage(notify.chatMessage("config_updated"));
            settingsHandler.purge(player);
        }

    }

    @Override
    public void setMenuItems() {
        player = menuUtilities.getOwner();

        inventory.setItem(0, filler());
        inventory.setItem(1, filler());
        inventory.setItem(2, filler());
        inventory.setItem(3, filler());
        inventory.setItem(4, filler());
        inventory.setItem(5, filler());
        inventory.setItem(6, filler());
        inventory.setItem(7, filler());
        inventory.setItem(8, filler());
        inventory.setItem(9, filler());
        inventory.setItem(17, filler());
        inventory.setItem(18, filler());
        inventory.setItem(26, filler());
        inventory.setItem(27, filler());
        inventory.setItem(28, filler());
        inventory.setItem(29, filler());
        inventory.setItem(30, filler());
        inventory.setItem(31, saveButton());
        inventory.setItem(32, filler());
        inventory.setItem(33, filler());
        inventory.setItem(34, filler());
        inventory.setItem(35, filler());



        inventory.setItem(10, setVelocityItem());
        inventory.setItem(11, setCooldownItem());
        inventory.setItem(12, toggleUseSound());
        inventory.setItem(13, toggleSoundBreak());
        inventory.setItem(14, giveOnJoinButton());
    }

    private ItemStack setVelocityItem() {
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
        meta.setDisplayName(notify.color("&eVelocity: &b" + this.velocity));
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

    private ItemStack setCooldownItem() {
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
        meta.setDisplayName(notify.color("&eCooldown: &b" + this.cooldown));
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

    private ItemStack toggleSoundBreak() {
        boolean cc = settingsHandler.getToggleBreakSound(player);
        breakSound = configuration.getBoolean("Settings.uses.sound.enabled");

        Material material = Material.GREEN_STAINED_GLASS_PANE;
        if(cc == false) {
            material = Material.RED_STAINED_GLASS_PANE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "break_sound");
        meta.setDisplayName(notify.color("&eBreak Sound: &b" + cc));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bToggle sound when grappling hook breaks."));
        lore.add(notify.color("&bCurrent config value:&6 " + breakSound));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack toggleUseSound() {
        boolean cc = settingsHandler.getToggleSound(player);
        useSound = configuration.getBoolean("Settings.sound.enabled");
        Material material = Material.GREEN_STAINED_GLASS_PANE;
        if(cc == false) {
            material = Material.RED_STAINED_GLASS_PANE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "use_sound");
        meta.setDisplayName(notify.color("&eSound: &b" + cc));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bToggle sound when using grappling hook."));
        lore.add(notify.color("&bCurrent config value:&6 " + useSound));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack giveOnJoinButton() {
        boolean cc = settingsHandler.getGiveOnJoin(player);
        giveOnJoin = configuration.getBoolean("Settings.give_on_join");

        Material material = Material.GREEN_STAINED_GLASS_PANE;
        if(cc == false) {
            material = Material.RED_STAINED_GLASS_PANE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "give_on_join");
        meta.setDisplayName(notify.color("&eJoin: &b" + cc));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bToggle if you want to give players"));
        lore.add(notify.color("&bthe grappling hook when they join."));
        lore.add(notify.color("&bCurrent config value:&6 " + giveOnJoin));
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack saveButton() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(GrapplingHook.getInstance(), "CLICK"), PersistentDataType.STRING, "save");
        meta.setDisplayName(notify.color("&a&lClick To Save"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(notify.color("&bThis will save and reload"));
        lore.add(notify.color("&bthe config with"));
        lore.add(notify.color("&bthe updated values."));
        lore.add(" ");
        lore.add(notify.color("&8--------------------"));
        lore.add(" ");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack filler() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        meta.removeItemFlags();
        item.setItemMeta(meta);
        return item;
    }
}
