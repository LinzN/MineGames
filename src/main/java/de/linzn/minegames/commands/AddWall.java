package de.linzn.minegames.commands;

import de.linzn.minegames.LobbyManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;


public class AddWall implements SubCommand {

    public boolean onCommand(Player player, String[] args) {
        if (!player.hasPermission(permission()) && !player.isOp()) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.nopermission", player);
            return true;
        } else if (args.length < 1) {
            MessageManager.getInstance().sendFMessage(MessageManager.PrefixType.ERROR, "error.notspecified", player, "input-Arena");
            return true;
        }
        LobbyManager.getInstance().setLobbySignsFromSelection(player, Integer.parseInt(args[0]));
        return true;
    }

    public String help(Player p) {
        return "/mg addwall <id> - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.addwall", "Add a lobby stats wall for Arena <id>");
    }

    public String permission() {
        return "mmg.admin.addwall";
    }

    //TODO: TAKE A W.E SELECTIONA AND SET THE LOBBY. ALSO SET LOBBY WALL
}