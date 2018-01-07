package de.linzn.minegames.commands;


import de.linzn.minegames.GameManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.MessageManager.PrefixType;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;


public class Join implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        if (args.length == 1) {
            if (player.hasPermission(permission())) {
                try {
                    int a = Integer.parseInt(args[0]);
                    GameManager.getInstance().addPlayer(player, a);
                } catch (NumberFormatException e) {
                    MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notanumber", player, "input-" + args[0]);
                }
            } else {
                MessageManager.getInstance().sendFMessage(PrefixType.WARNING, "error.nopermission", player);
            }
        } else {
            if (player.hasPermission("sg.lobby.join")) {
                if (GameManager.getInstance().getPlayerGameId(player) != -1) {
                    MessageManager.getInstance().sendMessage(PrefixType.ERROR, "error.alreadyingame", player);
                    return true;
                }
                player.teleport(SettingsManager.getInstance().getLobbySpawn());
                return true;
            } else {
                MessageManager.getInstance().sendFMessage(PrefixType.WARNING, "error.nopermission", player);
            }
        }
        return true;
    }

    public String help(Player p) {
        return "/sg join - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.join", "Join the lobby");
    }

    public String permission() {
        return "sg.arena.join";
    }
}

