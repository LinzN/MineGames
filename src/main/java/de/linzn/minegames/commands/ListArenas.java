package de.linzn.minegames.commands;

import de.linzn.minegames.Game;
import de.linzn.minegames.GameManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.MessageManager.PrefixType;
import de.linzn.minegames.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class ListArenas implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        StringBuilder arenas = new StringBuilder();
        try {
            if (args.length == 0) {
                arenas.append(SettingsManager.getInstance().getMessageConfig().getString("messages.error.needlobby", "Specify which lobby?")).append(": ");
                player.sendMessage(ChatColor.RED + arenas.toString());
            }
            if (Integer.parseInt(args[0]) < 0 || Integer.parseInt(args[0]) > GameManager.getInstance().getGameCount()) {
                MessageManager.getInstance().sendMessage(PrefixType.ERROR, "error.gamenoexist", player);
            }
            if (GameManager.getInstance().getGames().isEmpty()) {
                arenas.append(SettingsManager.getInstance().getMessageConfig().getString("messages.words.noarenas", "No arenas")).append(": ");
                player.sendMessage(ChatColor.RED + arenas.toString());
                return true;
            }
            arenas.append(SettingsManager.getInstance().getMessageConfig().getString("messages.words.arenas", "Arenas")).append(": ");
            for (Game g : GameManager.getInstance().getGames()) {
                arenas.append(g.getID()).append(", ");
            }
            player.sendMessage(ChatColor.GREEN + arenas.toString());
        } catch (Exception e) {
            MessageManager.getInstance().sendMessage(PrefixType.ERROR, "error.gamenoexist", player);
        }
        return false;
    }

    public String help(Player p) {
        return "/sg listarenas <lobby#> " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.listarenas", "List all available arenas");
    }

    public String permission() {
        return "";
    }
}