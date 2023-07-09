package de.mnzasa.mapreset;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MapResetCommandExecutor implements CommandExecutor {

    private final Mapreset plugin;
    private static final long COOLDOWN_SECONDS = 3;
    private boolean resetInProgress;

    public MapResetCommandExecutor(Mapreset plugin) {
        this.plugin = plugin;
        this.resetInProgress = false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("mapreset.reset")) {
                player.sendMessage("You don't have permission to use this command!");
                return true;
            }

            if (resetInProgress) {
                player.sendMessage("A map reset is already in progress. Please wait.");
                return true;
            }

            File mapsFolder = new File(plugin.getDataFolder(), "maps");
            File[] worldFolders = mapsFolder.listFiles();

            if (worldFolders == null || worldFolders.length == 0) {
                player.sendMessage(ChatColor.RED + "No world folder found in the 'maps' directory. Please add your world folder to the 'maps' directory located in the plugin's data folder.");
                return true;
            }

            player.sendMessage(ChatColor.YELLOW + "Resetting the map...");

            new BukkitRunnable() {
                private int secondsLeft = (int) COOLDOWN_SECONDS;

                @Override
                public void run() {
                    if (secondsLeft > 0) {
                        player.sendMessage(ChatColor.GREEN + "Map reset in " + secondsLeft + " seconds...");
                        secondsLeft--;
                    } else {
                        resetMap(player);
                        resetInProgress = false;
                        restartServer();
                        cancel(); // Cancel the task after the cooldown is finished
                    }
                }
            }.runTaskTimer(plugin, 0, 20); // Run the task every second (20 ticks)

            resetInProgress = true;

            return true;
        } else {
            sender.sendMessage("This command can only be executed by a player!");
            return true;
        }
    }

    private void resetMap(Player player) {
        File worldFolder = new File(plugin.getServer().getWorldContainer(), "world");
        File mapsFolder = new File(plugin.getDataFolder(), "maps");
        File[] worldFolders = mapsFolder.listFiles();

        if (worldFolders != null && worldFolders.length > 0) {
            File userWorldFolder = worldFolders[0];

            // Delete the existing "world" folder
            deleteDirectory(worldFolder);

            // Copy the user's world folder to replace the "world" folder
            try {
                copyDirectory(userWorldFolder, worldFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void restartServer() {
        plugin.getServer().shutdown();
    }

    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    private void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        Files.walk(sourceDirectory.toPath())
                .forEach(source -> {
                    try {
                        Files.copy(source, destinationDirectory.toPath().resolve(sourceDirectory.toPath().relativize(source)),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
