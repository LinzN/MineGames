package de.linzn.minegames.commands;

import de.linzn.minegames.GameManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;


public class Spectate implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        if (!player.hasPermission(permission()) && !player.isOp()) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.nopermission", player);
            return true;
        }

        if (args.length == 0) {
            if (GameManager.getInstance().isSpectator(player)) {
                GameManager.getInstance().removeSpectator(player);
                return true;
            } else {
                MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notspecified", player, "input-Game ID");
                return true;
            }
        }
        if (SettingsManager.getInstance().getSpawnCount(Integer.parseInt(args[0])) == 0) {
            MessageManager.getInstance().sendMessage(MessageManager.PrefixType.ERROR, "error.nospawns", player);
            return true;
        }
        if (GameManager.getInstance().isPlayerActive(player)) {
            MessageManager.getInstance().sendMessage(MessageManager.PrefixType.ERROR, "error.specingame", player);
            return true;
        }
        GameManager.getInstance().getGame(Integer.parseInt(args[0])).addSpectator(player);
        return true;
    }

    public String help(Player p) {
        return "/mg spectate <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.spectate", "Spectate a running arena");
    }

    public String permission() {
        return "mg.arena.spectate";
    }

}
