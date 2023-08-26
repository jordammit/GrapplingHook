package me.albus.grapplinghook.Utils.Settings;

public class SettingsData {

    private boolean toggleSound;

    private boolean toggleBreakSound;

    private boolean giveOnJoin;

    private int velocity;

    private int cooldown;

    public boolean getToggleGiveOnJoin() {
        return giveOnJoin;
    }

    public void setToggleGiveOnJoin(boolean value) {
        giveOnJoin = value;
    }

    public void setToggleSound(boolean b) {
        this.toggleSound = b;
    }

    public boolean getToggleSound() {
        return this.toggleSound;
    }

    public void setToggleBreakSound(boolean b) {
        this.toggleBreakSound = b;
    }

    public boolean getToggleBreakSound() {
        return  this.toggleBreakSound;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int i) {
        this.cooldown = i;
    }

    public int getVelocity() {
        return this.velocity;
    }

    public void setVelocity(int i) {
        this.velocity = i;
    }
}
