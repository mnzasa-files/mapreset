package de.mnzasa.mapreset;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import java.io.File;

public class Mapreset extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("MapReset plugin has been enabled!");

        // Create the "maps" folder if it doesn't exist
        File mapsFolder = new File(getDataFolder(), "maps");
        mapsFolder.mkdirs();

        // Register the command executor
        getCommand("mapreset").setExecutor(new MapResetCommandExecutor(this));

        // Register the join event listener
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("MapReset plugin has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            File mapsFolder = new File(getDataFolder(), "maps");
            File[] worldFolders = mapsFolder.listFiles();

            if (worldFolders == null || worldFolders.length == 0) {
                player.sendMessage("§0||§6Thank your for using the MapReset Plugin. \n§0||§cThe 'maps' folder is empty. To set up the MapReset plugin,\n§0||§cplace your world folder in the 'maps' folder\n§0||§clocated in the plugin's data folder.\n§0||§bThis plugin was created by Github: §8@mnzasa-files\n§0||§aYou can ask me for help on Discord: §8@mnzasa");
            }
        }
    }
}
