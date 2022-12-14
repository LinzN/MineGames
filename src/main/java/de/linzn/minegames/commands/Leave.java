package de.linzn.minegames.commands;

import de.linzn.minegames.GameManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;


public class Leave implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        if (GameManager.getInstance().getPlayerGameId(player) == -1) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notinarena", player);
        } else {
            GameManager.getInstance().removePlayer(player, false, false);
        }
        return true;
    }

    public String help(Player p) {
        return "/mg leave - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.leave", "Leaves the game");
    }

    public String permission() {
        return null;
    }
}
