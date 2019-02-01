package de.linzn.minegames;


import de.linzn.mineLib.title.MineTitle;
import de.linzn.mineProfile.core.PlayerDataAPI;
import de.linzn.mineProfile.modies.FlyMode;
import de.linzn.mineProfile.modies.VanishMode;
import de.linzn.minegames.api.PlayerJoinArenaEvent;
import de.linzn.minegames.api.PlayerKilledEvent;
import de.linzn.minegames.api.PlayerLeaveArenaEvent;
import de.linzn.minegames.hooks.HookManager;
import de.linzn.minegames.logging.QueueManager;
import de.linzn.minegames.stats.StatsManager;
import de.linzn.minegames.util.ItemReader;
import de.linzn.minegames.util.Kit;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


//Data container for a game

public class Game {

    HashMap<Player, Integer> nextspec = new HashMap<Player, Integer>();
    ArrayList<Player> voted = new ArrayList<Player>();
    int count = 20;
    int tid = 0;
    private GameMode mode = GameMode.DISABLED;
    private ArrayList<Player> activePlayers = new ArrayList<Player>();
    private ArrayList<Player> inactivePlayers = new ArrayList<Player>();
    private ArrayList<String> spectators = new ArrayList<String>();
    private ArrayList<Player> queue = new ArrayList<Player>();
    private HashMap<String, Object> flags = new HashMap<String, Object>();
    private ArrayList<Integer> tasks = new ArrayList<Integer>();
    private Arena arena;
    private int gameID;
    private int gcount = 0;
    private FileConfiguration config;
    private FileConfiguration system;
    private HashMap<Integer, Player> spawns = new HashMap<Integer, Player>();
    private int spawnCount = 0;
    private int vote = 0;
    private boolean disabled = false;
    private int endgameTaskID = 0;
    private boolean endgameRunning = false;
    private double rbpercent = 0;
    private String rbstatus = "";
    private long startTime = 0;
    private boolean countdownRunning;
    private StatsManager sm = StatsManager.getInstance();
    private HashMap<String, String> hookvars = new HashMap<String, String>();
    private MessageManager msgmgr = MessageManager.getInstance();

    public Game(int gameid) {
        gameID = gameid;
        reloadConfig();
        setup();
    }

    public void reloadConfig() {
        config = SettingsManager.getInstance().getConfig();
        system = SettingsManager.getInstance().getSystemConfig();
    }

    public void $(String msg) {
        MineGames.$(msg);
    }

    public void debug(String msg) {
        MineGames.debug(msg);
    }

    public void setup() {
        mode = GameMode.LOADING;
        int x = system.getInt("sg-system.arenas." + gameID + ".x1");
        int y = system.getInt("sg-system.arenas." + gameID + ".y1");
        int z = system.getInt("sg-system.arenas." + gameID + ".z1");
        $(x + " " + y + " " + z);
        int x1 = system.getInt("sg-system.arenas." + gameID + ".x2");
        int y1 = system.getInt("sg-system.arenas." + gameID + ".y2");
        int z1 = system.getInt("sg-system.arenas." + gameID + ".z2");
        $(x1 + " " + y1 + " " + z1);
        Location max = new Location(SettingsManager.getGameWorld(gameID), Math.max(x, x1), Math.max(y, y1), Math.max(z, z1));
        $(max.toString());
        Location min = new Location(SettingsManager.getGameWorld(gameID), Math.min(x, x1), Math.min(y, y1), Math.min(z, z1));
        $(min.toString());

        arena = new Arena(min, max);

        loadspawns();

        hookvars.put("arena", gameID + "");
        hookvars.put("maxplayers", spawnCount + "");
        hookvars.put("activeplayers", "0");

        mode = GameMode.WAITING;
    }

    public void reloadFlags() {
        flags = SettingsManager.getInstance().getGameFlags(gameID);
    }

    public void saveFlags() {
        SettingsManager.getInstance().saveGameFlags(flags, gameID);
    }

    public void loadspawns() {
        for (int a = 1; a <= SettingsManager.getInstance().getSpawnCount(gameID); a++) {
            spawns.put(a, null);
            spawnCount = a;
        }
    }

    public void addSpawn() {
        spawnCount++;
        spawns.put(spawnCount, null);
    }

    public GameMode getGameMode() {
        return mode;
    }

    public Arena getArena() {
        return arena;
    }


    /*
     *
     * ################################################
     *
     * 				ENABLE
     *
     * ################################################
     *
     *
     */

    public void Scoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("test", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Scoreboard");
        objective.getName();

        Score score = objective.getScore(ChatColor.GREEN + "Kills:"); //Get a fake offline player
        score.setScore(1);
    }


    /*
     *
     * ################################################
     *
     * 				ADD PLAYER
     *
     * ################################################
     *
     *
     */

    public void enable() {
        mode = GameMode.WAITING;
        if (disabled) {
            MessageManager.getInstance().broadcastFMessage(MessageManager.PrefixType.INFO, "broadcast.gameenabled", "arena-" + gameID);
        }
        disabled = false;
        int b = (SettingsManager.getInstance().getSpawnCount(gameID) > queue.size()) ? queue.size() : SettingsManager.getInstance().getSpawnCount(gameID);
        for (int a = 0; a < b; a++) {
            addPlayer(queue.remove(0));
        }
        int c = 1;
        for (Player p : queue) {
            msgmgr.sendMessage(MessageManager.PrefixType.INFO, "You are now #" + c + " in line for arena " + gameID, p);
            c++;
        }

        LobbyManager.getInstance().updateWall(gameID);

        MessageManager.getInstance().broadcastFMessage(MessageManager.PrefixType.INFO, "broadcast.gamewaiting", "arena-" + gameID);

    }

    @SuppressWarnings("deprecation")
    public boolean addPlayer(Player p) {
        if (SettingsManager.getInstance().getLobbySpawn() == null) {
            msgmgr.sendFMessage(MessageManager.PrefixType.WARNING, "error.nolobbyspawn", p);
            return false;
        }
        if (!p.hasPermission("mg.player.join." + gameID) && !p.hasPermission("mg.player.join.all")) {
            debug("permission needed to join arena: " + "mg.player.join." + gameID);
            msgmgr.sendFMessage(MessageManager.PrefixType.WARNING, "game.nopermission", p, "arena-" + gameID);
            return false;
        }
        HookManager.getInstance().runHook("GAME_PRE_ADDPLAYER", "arena-" + gameID, "player-" + p.getName(), "maxplayers-" + spawns.size(), "players-" + activePlayers.size());

        GameManager.getInstance().removeFromOtherQueues(p, gameID);

        if (GameManager.getInstance().getPlayerGameId(p) != -1) {
            if (GameManager.getInstance().isPlayerActive(p)) {
                msgmgr.sendMessage(MessageManager.PrefixType.ERROR, "Cannot join multiple games!", p);
                return false;
            }
        }
        if (p.isInsideVehicle()) {
            p.leaveVehicle();
        }
        if (spectators.contains(p)) removeSpectator(p, false);
        if (mode == GameMode.WAITING || mode == GameMode.STARTING) {
            if (activePlayers.size() < SettingsManager.getInstance().getSpawnCount(gameID)) {
                msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Joining Arena " + gameID, p);
                PlayerJoinArenaEvent joinarena = new PlayerJoinArenaEvent(p, GameManager.getInstance().getGame(gameID));
                Bukkit.getServer().getPluginManager().callEvent(joinarena);
                if (joinarena.isCancelled()) return false;
                boolean placed = false;
                int spawnCount = SettingsManager.getInstance().getSpawnCount(gameID);

                for (int a = 1; a <= spawnCount; a++) {
                    if (spawns.get(a) == null) {
                        placed = true;
                        spawns.put(a, p);
                        p.setGameMode(org.bukkit.GameMode.SURVIVAL);

                        p.teleport(SettingsManager.getInstance().getLobbySpawn());
                        //saveInv(p);
                        this.savePlayerData(p);
                        p.teleport(SettingsManager.getInstance().getSpawnPoint(gameID, a));

                        p.setHealth(p.getMaxHealth());
                        p.setFoodLevel(20);
                        clearInv(p);

                        activePlayers.add(p);
                        sm.addPlayer(p, gameID);

                        hookvars.put("activeplayers", activePlayers.size() + "");
                        LobbyManager.getInstance().updateWall(gameID);
                        showMenu(p);
                        HookManager.getInstance().runHook("GAME_POST_ADDPLAYER", "activePlayers-" + activePlayers.size());

                        if (spawnCount == activePlayers.size()) {
                            countdown(5);
                        }
                        break;
                    }
                }
                if (!placed) {
                    msgmgr.sendFMessage(MessageManager.PrefixType.ERROR, "error.gamefull", p, "arena-" + gameID);
                    return false;
                }

            } else if (SettingsManager.getInstance().getSpawnCount(gameID) == 0) {
                msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "No spawns set for Arena " + gameID + "!", p);
                return false;
            } else {
                msgmgr.sendFMessage(MessageManager.PrefixType.WARNING, "error.gamefull", p, "arena-" + gameID);
                return false;
            }
            msgFall(MessageManager.PrefixType.INFO, "game.playerjoingame", "player-" + p.getName(), "activeplayers-" + getActivePlayers(), "maxplayers-" + SettingsManager.getInstance().getSpawnCount(gameID));
            msgmgr.sendMessage(MessageManager.PrefixType.INFO, ChatColor.GOLD + "Vote für den Start des Spiels mit /mg vote", p);
            if (activePlayers.size() >= config.getInt("auto-start-players") && !countdownRunning)
                countdown(config.getInt("auto-start-time"));
            return true;
        } else {
            if (config.getBoolean("enable-player-queue")) {
                if (!queue.contains(p)) {
                    queue.add(p);
                    msgmgr.sendFMessage(MessageManager.PrefixType.INFO, "game.playerjoinqueue", p, "queuesize-" + queue.size());
                }
                int a = 1;
                for (Player qp : queue) {
                    if (qp == p) {
                        msgmgr.sendFMessage(MessageManager.PrefixType.INFO, "game.playercheckqueue", p, "queuepos-" + a);
                        break;
                    }
                    a++;
                }
            }
        }
        if (mode == GameMode.INGAME) msgmgr.sendFMessage(MessageManager.PrefixType.WARNING, "error.alreadyingame", p);
        else if (mode == GameMode.DISABLED)
            msgmgr.sendFMessage(MessageManager.PrefixType.WARNING, "error.gamedisabled", p, "arena-" + gameID);
        else if (mode == GameMode.RESETING)
            msgmgr.sendFMessage(MessageManager.PrefixType.WARNING, "error.gamereseting", p);
        else msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Spiel beitreten fehlgeschlagen!", p);
        LobbyManager.getInstance().updateWall(gameID);
        return false;
    }

    public void showMenu(Player p) {
        GameManager.getInstance().openKitMenu(p);
        Inventory i = Bukkit.getServer().createInventory(p, 90, ChatColor.RED + "" + ChatColor.BOLD + "Kit Selection");

        int a = 0;
        int b = 0;


        ArrayList<Kit> kits = GameManager.getInstance().getKits(p);
        MineGames.debug(kits + "");
        if (kits == null || kits.size() == 0 || !SettingsManager.getInstance().getKits().getBoolean("enabled")) {
            GameManager.getInstance().leaveKitMenu(p);
            return;
        }

        for (Kit k : kits) {
            ItemStack i1 = k.getIcon();
            ItemMeta im = i1.getItemMeta();

            debug(k.getName() + " " + i1 + " " + im);

            im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + k.getName());
            i1.setItemMeta(im);
            i.setItem((9 * a) + b, i1);
            a = 2;

            for (ItemStack s2 : k.getContents()) {
                if (s2 != null) {
                    i.setItem((9 * a) + b, s2);
                    a++;
                }
            }

            a = 0;
            b++;
        }
        p.openInventory(i);
        debug("Showing menu");
    }

    /*
     *
     * ################################################
     *
     * 				VOTE
     *
     * ################################################
     *
     *
     */

    public void removeFromQueue(Player p) {
        queue.remove(p);
    }

    public void vote(Player pl) {


        if (GameMode.STARTING == mode) {
            msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Spiel startet bereits!", pl);
            return;
        }
        if (GameMode.WAITING != mode) {
            msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Spiel hat bereits angefangen!", pl);
            return;
        }
        if (voted.contains(pl)) {
            msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Du hast bereits gevotet!", pl);
            return;
        }
        vote++;
        voted.add(pl);
        for (Player p : activePlayers) {
            msgmgr.sendFMessage(MessageManager.PrefixType.INFO, "game.playervote", p, "player-" + pl.getName());
        }
        HookManager.getInstance().runHook("PLAYER_VOTE", "player-" + pl.getName());
		/*for(Player p: activePlayers){
            p.sendMessage(ChatColor.AQUA+pl.getName()+" Voted to start the game! "+ Math.round((vote +0.0) / ((getActivePlayers() +0.0)*100)) +"/"+((c.getInt("auto-start-vote")+0.0))+"%");
        }*/
        // Bukkit.getServer().broadcastPrefixType((vote +0.0) / (getActivePlayers() +0.0) +"% voted, needs "+(c.getInt("auto-start-vote")+0.0)/100);
        if ((((vote + 0.0) / (getActivePlayers() + 0.0)) >= (config.getInt("auto-start-vote") + 0.0) / 100) && getActivePlayers() > 1) {
            countdown(config.getInt("auto-start-time"));
            // for (Player p : activePlayers) {
            //p.sendMessage(ChatColor.LIGHT_PURPLE + "Game Starting in " + c.getInt("auto-start-time"));
            // msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Spiel startet in " + config.getInt("auto-start-time") + "!", p);
            //}
        }
    }

    /*
     *
     * ################################################
     *
     * 				START GAME
     *
     * ################################################
     *
     *
     */
    @SuppressWarnings("deprecation")
    public void startGame() {
        if (mode == GameMode.INGAME) {
            return;
        }

        if (activePlayers.size() <= 0) {
            for (Player pl : activePlayers) {
                msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Nicht genug Spieler", pl);
                mode = GameMode.WAITING;
                LobbyManager.getInstance().updateWall(gameID);
            }
            return;
        } else {
            startTime = new Date().getTime();
            for (Player pl : activePlayers) {
                pl.setHealth(pl.getMaxHealth());
                pl.playSound(pl.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.MASTER, 15, 5);
                new MineTitle("" + ChatColor.YELLOW + ChatColor.BOLD + "Viel Glück!", ChatColor.YELLOW + "Du hast eine Schutzzeit von " + config.getInt("grace-period") + " Sekunden!", 10, 80, 20).send(pl);

            }
            if (config.getBoolean("restock-chest")) {
                SettingsManager.getGameWorld(gameID).setTime(0);
                gcount++;
                tasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(),
                        new NightChecker(),
                        14400));
            }
            if (config.getInt("grace-period") != 0) {
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(), () -> {
                    for (Player play : activePlayers) {
                        play.playSound(play.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 15, 1);
                        new MineTitle("" + ChatColor.RED + ChatColor.BOLD + "Schutzzeit Abgelaufen!", ChatColor.YELLOW + "Lass die MineGames beginnen!", 10, 60, 20).send(play);
                    }
                }, config.getInt("grace-period") * 20);
            }
            if (config.getBoolean("deathmatch.enabled")) {
                tasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(),
                        new DeathMatch(), config.getInt("deathmatch.time") * 20 * 60));
            }
        }

        mode = GameMode.INGAME;
        LobbyManager.getInstance().updateWall(gameID);
        MessageManager.getInstance().broadcastFMessage(MessageManager.PrefixType.INFO, "broadcast.gamestarted", "arena-" + gameID);

    }

    /*
     *
     * ################################################
     *
     * 				COUNTDOWN
     *
     * ################################################
     *
     *
     */
    public int getCountdownTime() {
        return count;
    }

    public void countdown(int time) {
        //Bukkit.broadcastMessage(""+time);
        MessageManager.getInstance().broadcastFMessage(MessageManager.PrefixType.INFO, "broadcast.gamestarting", "arena-" + gameID, "t-" + time);
        countdownRunning = true;
        count = time;
        Bukkit.getScheduler().cancelTask(tid);

        if (mode == GameMode.WAITING || mode == GameMode.STARTING) {
            mode = GameMode.STARTING;
            tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(GameManager.getInstance().getPlugin(), () -> {
                if (getActivePlayers() < 2) {
                    Bukkit.getScheduler().cancelTask(tid);
                    countdownRunning = false;
                    mode = GameMode.WAITING;
                    return;
                }

                if (count > 0) {
                    if (count == 20) {
                        for (Player p : activePlayers) {
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 15, 1);
                            new MineTitle("" + ChatColor.GREEN + ChatColor.BOLD + "Der Countdown beginnt!", "" + ChatColor.GREEN + ChatColor.BOLD + "Bereitet euch vor!", 5, 40, 20).send(p);
                        }
                    }
                    if (count < 17 && count > 10) {
                        for (Player p : activePlayers) {
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 15, 1);
                            new MineTitle(ChatColor.GREEN + "Spiel startet in", "" + ChatColor.RED + ChatColor.BOLD + count, 5, 20, 5).send(p);
                        }
                    }

                    if (count <= 10) {
                        //todo Tittle API
                        for (Player p : activePlayers) {
                            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 15, 1);
                            new MineTitle("" + ChatColor.RED + ChatColor.BOLD + count, ChatColor.YELLOW + "Spiel startet in", 5, 20, 5).send(p);
                        }
                        //msgFall(MessageManager.PrefixType.INFO, "game.countdown", "t-" + count);

                    }
                    count--;
                    LobbyManager.getInstance().updateWall(gameID);
                } else {
                    startGame();
                    Bukkit.getScheduler().cancelTask(tid);
                    countdownRunning = false;
                }
            }, 0, 20);

        }
    }

    public void removePlayer(Player p, boolean b, boolean onLeave) {
        p.teleport(SettingsManager.getInstance().getLobbySpawn());
        ///$("Teleporting to lobby");
        if (mode == GameMode.INGAME) {
            killPlayer(p, b, onLeave);
        } else {
            if (this.voted.contains(p)) {
                this.voted.remove(p);
                vote--;
            }
            sm.removePlayer(p, gameID);
            //	if (!b) p.teleport(SettingsManager.getInstance().getLobbySpawn());
            //restoreInv(p);
            this.loadPlayerData(p, onLeave);
            activePlayers.remove(p);
            inactivePlayers.remove(p);
            for (Object in : spawns.keySet().toArray()) {
                if (spawns.get(in) == p) spawns.remove(in);
            }
            LobbyManager.getInstance().clearSigns(gameID);
        }

        HookManager.getInstance().runHook("PLAYER_REMOVED", "player-" + p.getName());

        @SuppressWarnings("unused")
        PlayerLeaveArenaEvent pl = new PlayerLeaveArenaEvent(p, this, b);

        LobbyManager.getInstance().updateWall(gameID);
    }

    public void playerLeave(Player p) {

    }

    /*
     *
     * ################################################
     *
     * 				REMOVE PLAYER
     *
     * ################################################
     *
     *
     */

    /*
     *
     * ################################################
     *
     * 				KILL PLAYER
     *
     * ################################################
     *
     *
     */
    @SuppressWarnings("deprecation")
    public void killPlayer(Player p, boolean left, boolean onLeave) {
        try {
            clearInv(p);
            if (!left) {
                p.teleport(SettingsManager.getInstance().getLobbySpawn());
            }
            sm.playerDied(p, activePlayers.size(), gameID, new Date().getTime() - startTime);

            if (!activePlayers.contains(p)) return;
            else this.loadPlayerData(p, onLeave);

            activePlayers.remove(p);
            inactivePlayers.add(p);
            PlayerKilledEvent pk;
            if (left) {
                msgFall(MessageManager.PrefixType.INFO, "game.playerleavegame", "player-" + p.getName());
            } else {
                if (mode != GameMode.WAITING && p.getLastDamageCause() != null && p.getLastDamageCause().getCause() != null) {
                    System.out.println("Type: " + p.getLastDamageCause().getCause().name());
                    switch (p.getLastDamageCause().getCause()) {
                        case ENTITY_ATTACK:
                            if (p.getLastDamageCause().getEntityType() == EntityType.PLAYER && p.getKiller() != null) {
                                Player killer = p.getKiller();
                                msgRawFall(MessageManager.PrefixType.INFO, ChatColor.GREEN + "Spieler " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + " ist durch die Hand von " + ChatColor.GOLD + killer.getName() + ChatColor.GREEN + " gestorben!");
                                sm.addKill(killer, p, gameID);
                                pk = new PlayerKilledEvent(p, this, killer, p.getLastDamageCause().getCause());
                            } else {
                                msgRawFall(MessageManager.PrefixType.INFO, ChatColor.GREEN + "Spieler " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + " ist durch das Einwirken einer dritten partei (" + p.getLastDamageCause().getEntityType().name() + ") gestorben!");
                                pk = new PlayerKilledEvent(p, this, null, p.getLastDamageCause().getCause());
                            }
                            break;
                        default:
                            msgRawFall(MessageManager.PrefixType.INFO, ChatColor.GREEN + "Spieler " + ChatColor.GOLD + p.getName() + ChatColor.GREEN + " ist aufgrund von unvorhergesehenen umständen gestorben!");
                            pk = new PlayerKilledEvent(p, this, null, p.getLastDamageCause().getCause());

                            break;
                    }
                    Bukkit.getServer().getPluginManager().callEvent(pk);

                    if (getActivePlayers() > 1) {
                        for (Player pl : getAllPlayers()) {
                            msgmgr.sendMessage(MessageManager.PrefixType.INFO, ChatColor.DARK_AQUA + "Es verbleiben " + ChatColor.YELLOW + ""
                                    + getActivePlayers() + ChatColor.DARK_AQUA + " Spieler!", pl);
                        }
                    }
                }

            }

            for (Player pe : activePlayers) {
                Location l = pe.getLocation();
                l.setY(l.getWorld().getMaxHeight());
                l.getWorld().strikeLightningEffect(l);
            }

            if (getActivePlayers() <= config.getInt("endgame.players") && config.getBoolean("endgame.fire-lighting.enabled") && !endgameRunning) {

                tasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(GameManager.getInstance().getPlugin(),
                        new EndgameManager(),
                        0,
                        config.getInt("endgame.fire-lighting.interval") * 20));
            }

            if (activePlayers.size() < 2 && mode != GameMode.WAITING) {
                playerWin(p);
                endGame();
            }
            LobbyManager.getInstance().updateWall(gameID);

        } catch (Exception e) {
            MineGames.$("???????????????????????");
            e.printStackTrace();
            MineGames.$("ID" + gameID);
            MineGames.$(left + "");
            MineGames.$(activePlayers.size() + "");
            MineGames.$(activePlayers.toString());
            MineGames.$(p.getName());
            MineGames.$(p.getLastDamageCause().getCause().name());
        }
    }

    /*
     *
     * ################################################
     *
     * 				PLAYER WIN
     *
     * ################################################
     *
     *
     */
    @SuppressWarnings("deprecation")
    public void playerWin(Player p) {
        if (GameMode.DISABLED == mode) return;
        Player win = activePlayers.get(0);
        // clearInv(p);
        win.teleport(SettingsManager.getInstance().getLobbySpawn());
        //restoreInv(win);
        this.loadPlayerData(win, false);
        msgmgr.broadcastFMessage(MessageManager.PrefixType.INFO, "game.playerwin", "arena-" + gameID, "victim-" + p.getName(), "player-" + win.getName());
        LobbyManager.getInstance().display(new String[]{
                win.getName(), "", "Gewinnt die ", "MineGames!"
        }, gameID);

        mode = GameMode.FINISHING;
        if (config.getBoolean("reward.enabled", false)) {
            List<String> items = config.getStringList("reward.contents");
            for (int i = 0; i <= (items.size() - 1); i++) {
                ItemStack item = ItemReader.read(items.get(i));
                win.getInventory().addItem(item);
            }
        }

        clearSpecs();
        win.setHealth(p.getMaxHealth());
        win.setFoodLevel(20);
        win.setFireTicks(0);
        win.setFallDistance(0);

        sm.playerWin(win, gameID, new Date().getTime() - startTime);
        sm.saveGame(gameID, win, getActivePlayers() + getInactivePlayers(), new Date().getTime() - startTime);

        activePlayers.clear();
        inactivePlayers.clear();
        spawns.clear();

        loadspawns();
        LobbyManager.getInstance().updateWall(gameID);
        MessageManager.getInstance().broadcastFMessage(MessageManager.PrefixType.INFO, "broadcast.gameend", "arena-" + gameID);

    }

    public void endGame() {
        mode = GameMode.WAITING;
        resetArena();
        LobbyManager.getInstance().clearSigns(gameID);
        LobbyManager.getInstance().updateWall(gameID);

    }

    /*
     *
     * ################################################
     *
     * 				DISABLE
     *
     * ################################################
     *
     *
     */
    public void disable() {
        disabled = true;
        spawns.clear();

        for (int a = 0; a < activePlayers.size(); a = 0) {
            try {

                Player p = activePlayers.get(a);
                msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Game disabled!", p);
                removePlayer(p, false, false);
            } catch (Exception e) {
            }

        }

        for (int a = 0; a < inactivePlayers.size(); a = 0) {
            try {

                Player p = inactivePlayers.remove(a);
                msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Game disabled!", p);
            } catch (Exception e) {
            }

        }

        clearSpecs();
        queue.clear();

        endGame();
        LobbyManager.getInstance().updateWall(gameID);
        MessageManager.getInstance().broadcastFMessage(MessageManager.PrefixType.INFO, "broadcast.gamedisabled", "arena-" + gameID);

    }

    /*
     *
     * ################################################
     *
     * 				RESET
     *
     * ################################################
     *
     *
     */
    public void resetArena() {

        for (Integer i : tasks) {
            Bukkit.getScheduler().cancelTask(i);
        }

        tasks.clear();
        vote = 0;
        voted.clear();

        mode = GameMode.RESETING;
        endgameRunning = false;

        Bukkit.getScheduler().cancelTask(endgameTaskID);
        GameManager.getInstance().gameEndCallBack(gameID);
        QueueManager.getInstance().rollback(gameID, true);
        LobbyManager.getInstance().updateWall(gameID);

    }

    public void resetCallback() {
        if (!disabled) {
            enable();
        } else mode = GameMode.DISABLED;
        LobbyManager.getInstance().updateWall(gameID);
    }

    public void savePlayerData(Player p) {
        PlayerDataAPI.unloadProfile(p, true);
        new VanishMode(p, 0, false);
        new FlyMode(p, 0, false);
    }

    public void loadPlayerData(Player p, boolean onLogout) {
        if (!onLogout) {
            PlayerDataAPI.loadProfile(p);
        }
    }


    public void addSpectator(Player p) {
        if (mode != GameMode.INGAME) {
            msgmgr.sendMessage(MessageManager.PrefixType.WARNING, "Du kannst leider nur zuschauen!", p);
            return;
        }

        //saveInv(p);
        this.savePlayerData(p);
        clearInv(p);
        p.teleport(SettingsManager.getInstance().getSpawnPoint(gameID, 1).add(0, 10, 0));

        HookManager.getInstance().runHook("PLAYER_SPECTATE", "player-" + p.getName());

        for (Player pl : Bukkit.getOnlinePlayers()) {
            pl.hidePlayer(p);
        }

        p.setAllowFlight(true);
        p.setFlying(true);
        spectators.add(p.getName());
        msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Du schaust jetzt zu! Benutze /mg spectate nochmal, um wieder zur Lobby zu gelangen.", p);
        msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Right click while holding shift to teleport to the next ingame player, left click to go back.", p);
        nextspec.put(p, 0);
    }

    @SuppressWarnings("deprecation")
    public void removeSpectator(Player p, boolean onLogout) {
        ArrayList<Player> players = new ArrayList<Player>();
        players.addAll(activePlayers);
        players.addAll(inactivePlayers);

        if (p.isOnline()) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.showPlayer(p);
            }
        }
        this.loadPlayerData(p, onLogout);
        p.setFallDistance(0);
        p.teleport(SettingsManager.getInstance().getLobbySpawn());
        spectators.remove(p.getName());

        nextspec.remove(p);
    }


    /*
     *
     * ################################################
     *
     * 				SPECTATOR
     *
     * ################################################
     *
     *
     */

    public void clearSpecs() {

        for (int a = 0; a < spectators.size(); a = 0) {
            removeSpectator(Bukkit.getPlayerExact(spectators.get(0)), false);
        }
        spectators.clear();
        nextspec.clear();
    }

    public HashMap<Player, Integer> getNextSpec() {
        return nextspec;
    }


    public void clearInv(Player p) {
        ItemStack[] inv = p.getInventory().getContents();
        for (int i = 0; i < inv.length; i++) {
            inv[i] = null;
        }
        p.getInventory().setContents(inv);
        inv = p.getInventory().getArmorContents();
        for (int i = 0; i < inv.length; i++) {
            inv[i] = null;
        }
        p.getInventory().setArmorContents(inv);
        p.updateInventory();
        for (PotionEffect e : p.getActivePotionEffects()) {
            p.addPotionEffect(new PotionEffect(e.getType(), 0, 0), true);
        }

    }

    public boolean isBlockInArena(Location v) {
        return arena.containsBlock(v);
    }

    public boolean isProtectionOn() {
        long t = startTime / 1000;
        long l = config.getLong("grace-period");
        long d = new Date().getTime() / 1000;
        if ((d - t) < l) return true;
        return false;
    }

    public int getID() {
        return gameID;
    }

    public int getActivePlayers() {
        return activePlayers.size();
    }

    public int getInactivePlayers() {
        return inactivePlayers.size();
    }

    public Player[][] getPlayers() {
        return new Player[][]{
                activePlayers.toArray(new Player[0]), inactivePlayers.toArray(new Player[0])
        };
    }

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> all = new ArrayList<Player>();
        all.addAll(activePlayers);
        all.addAll(inactivePlayers);
        return all;
    }

    public boolean isSpectator(Player p) {
        return spectators.contains(p.getName());
    }

    public boolean isInQueue(Player p) {
        return queue.contains(p);
    }

    public boolean isPlayerActive(Player player) {
        return activePlayers.contains(player);
    }

    public boolean isPlayerInactive(Player player) {
        return inactivePlayers.contains(player);
    }

    public boolean hasPlayer(Player p) {
        return activePlayers.contains(p) || inactivePlayers.contains(p);
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode m) {
        mode = m;
    }

    public double getRBPercent() {
        return rbpercent;
    }

    public synchronized void setRBPercent(double d) {
        rbpercent = d;
    }

    public String getRBStatus() {
        return rbstatus;
    }

    public void setRBStatus(String s) {
        rbstatus = s;
    }

    public String getName() {
        return "Arena " + gameID;
    }

    public void msgFall(MessageManager.PrefixType type, String msg, String... vars) {
        for (Player p : getAllPlayers()) {
            msgmgr.sendFMessage(type, msg, p, vars);
        }
    }

    public void msgRawFall(MessageManager.PrefixType type, String msg) {
        for (Player p : getAllPlayers()) {
            msgmgr.sendMessage(type, msg, p);
        }
    }

    public static enum GameMode {
        DISABLED, LOADING, INACTIVE, WAITING,
        STARTING, INGAME, FINISHING, RESETING, ERROR
    }

    class NightChecker implements Runnable {
        boolean reset = false;
        int tgc = gcount;

        public void run() {
            if (SettingsManager.getGameWorld(gameID).getTime() > 14000) {
                for (Player pl : activePlayers) {
                    msgmgr.sendMessage(MessageManager.PrefixType.INFO, "Truhen wieder aufgefüllt!", pl);
                }
                QueueManager.getInstance().restockChests(gameID);
                //GameManager.openedChest.get(gameID).clear();
                reset = true;

                if (config.getBoolean("restock-chest-repeat")) {
                    tasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(),
                            new NightChecker(),
                            14400));
                }
            }

        }
    }

    class EndgameManager implements Runnable {
        public void run() {
            for (Player player : activePlayers.toArray(new Player[0])) {
                Location l = player.getLocation();
                l.add(0, 5, 0);
                player.getWorld().strikeLightningEffect(l);
            }

        }
    }

    class DeathMatch implements Runnable {
        public void run() {
            for (Player p : activePlayers) {
                for (int a = 0; a < spawns.size(); a++) {
                    if (spawns.get(a) == p) {
                        p.teleport(SettingsManager.getInstance().getSpawnPoint(gameID, a));
                        break;
                    }
                }
            }
            tasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask(GameManager.getInstance().getPlugin(), new Runnable() {
                public void run() {
                    for (Player p : activePlayers) {
                        p.getLocation().getWorld().strikeLightning(p.getLocation());
                    }
                }
            }, config.getInt("deathmatch.killtime") * 20 * 60));
        }
    }

	/*public void randomTrap() {
	 * 
        World world = SettingsManager.getGameWorld(gameID);

        double xcord;
        double zcord;
        double ycord = 80;
        Random rand = new Random();
        xcord = rand.nextInt(1000);
        zcord = rand.nextInt(1000);
        Location trap = new Location(world, xcord, ycord, zcord);
        boolean isAir = true;

        while(isAir == true) {
            ycord--;
            Byte blockData = trap.getBlock().getData();
            if(blockData != 0) {
                trap.getBlock().setType(Material.AIR);
                ycord--;
                trap.getBlock().setType(Material.AIR);
                ycord--;
                trap.getBlock().setType(Material.AIR);
                ycord--;
                trap.getBlock().setType(Material.LAVA);
                isAir = false;
            } else {
                isAir = true;
            }
        }

    }*/
}
