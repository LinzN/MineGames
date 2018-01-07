package de.linzn.minegames.commands;

import de.linzn.minegames.Game;
import de.linzn.minegames.GameManager;
import de.linzn.minegames.MessageManager;
import de.linzn.minegames.MessageManager.PrefixType;
import de.linzn.minegames.SettingsManager;
import org.bukkit.entity.Player;


public class ForceStart implements SubCommand {

    MessageManager msgmgr = MessageManager.getInstance();

    public boolean onCommand(Player player, String[] args) {

        if (!player.hasPermission(permission()) && !player.isOp()) {
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.nopermission", player);
            return true;
        }
        int game = -1;
        int seconds = 10;
        if (args.length == 2) {
            seconds = Integer.parseInt(args[1]);
        }
        if (args.length >= 1) {
            game = Integer.parseInt(args[0]);

        } else
            game = GameManager.getInstance().getPlayerGameId(player);
        if (game == -1) {
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notingame", player);
            return true;
        }
        if (GameManager.getInstance().getGame(game).getActivePlayers() < 2) {
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.notenoughtplayers", player);
            return true;
        }


        Game g = GameManager.getInstance().getGame(game);
        if (g.getMode() != Game.GameMode.WAITING && !player.hasPermission("sg.arena.restart")) {
            MessageManager.getInstance().sendFMessage(PrefixType.ERROR, "error.alreadyingame", player);
            return true;
        }
        g.countdown(seconds);

        msgmgr.sendFMessage(PrefixType.INFO, "game.started", player, "arena-" + game);

        return true;
    }

    public String help(Player p) {
        return "/sg forcestart - " + SettingsManager.getInstance().getMessageConfig().getString("messages.help.forcestart", "Forces the game to start");
    }

    public String permission() {
        return "sg.arena.start";
    }
}