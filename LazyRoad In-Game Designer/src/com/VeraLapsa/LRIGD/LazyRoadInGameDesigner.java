package com.VeraLapsa.LRIGD;

import com.VeraLapsa.LRIGD.commands.LRDHelpCommand;
import com.VeraLapsa.LRIGD.commands.RoadInfoCommand;
import com.VeraLapsa.LRIGD.commands.SaveRoadCommand;
import com.VeraLapsa.LRIGD.commands.SetGradeCommand;
import com.VeraLapsa.LRIGD.commands.SetKeepIdCommand;
import com.VeraLapsa.LRIGD.commands.SetRoadCommand;
import com.VeraLapsa.LRIGD.commands.SetStairsCommand;
import com.VeraLapsa.LRIGD.commands.TestRoadCommand;
import com.VeraLapsa.LRIGD.commands.TestStopCommand;
import com.creadri.lazyroad.Pillar;
import com.creadri.lazyroad.Road;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author VeraLapsa
 */
public class LazyRoadInGameDesigner extends JavaPlugin {

    public Log log = null;
    private static PluginDescriptionFile pdfFile = null;
    public static String B_PluginName = null;
    private Map<String, CommandHandler> commands = new HashMap<String, CommandHandler>();
    public Map<String, Road> roadsetup = new HashMap<String, Road>();
    public Map<String, Pillar> pillarsetup = new HashMap<String, Pillar>();
    public Map<String, Integer> keepid = new HashMap<String, Integer>();

    @Override
    public void onDisable() {
        log.Info("has been disabled!");
    }

    @Override
    public void onEnable() {
        pdfFile = this.getDescription();
        log = new Log(this);
        Plugin p = this.getServer().getPluginManager().getPlugin("LazyRoad");
        if (p != null) {
            getDataFolder().mkdirs();

            this.getConfig().options().copyDefaults(true);

            if (!getConfig().contains("version")) {
                this.saveDefaultConfig();
                this.reloadConfig();
                this.getConfig().set("version", "'" + getDescription().getVersion() + "'");
            } else if (!getConfig().getString("version").equalsIgnoreCase(getDescription().getVersion())) {
                this.saveDefaultConfig();
                this.reloadConfig();
                this.getConfig().set("version", "'" + getDescription().getVersion() + "'");
            }

            this.saveConfig();;

            commands.put("lrdhelp", new LRDHelpCommand(this));
            commands.put("roadinfo", new RoadInfoCommand(this));
            commands.put("saveroad", new SaveRoadCommand(this));
            commands.put("setgrade", new SetGradeCommand(this));
            commands.put("setkeepid", new SetKeepIdCommand(this));
            commands.put("setroad", new SetRoadCommand(this));
            commands.put("setstairs", new SetStairsCommand(this));
            commands.put("testroad", new TestRoadCommand(this));
            commands.put("teststop", new TestStopCommand(this));

            log.Info("has been enabled!");
        } else {
            log.Severe("The server does not seem to have the LazyRoad plugin, it is required.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        CommandHandler handler = commands.get(command.getName().toLowerCase());

        if (getConfig().getBoolean("debug.commandcalls")) {
            String dispargs = "";
            for (String string : args) {
                dispargs = dispargs.concat(string + " ");
            }
            log.Debug(sender.getName() + " ran the /" + label + " command. Args: " + dispargs);
        }

        if (handler != null) {
            return handler.perform(sender, args);
        } else {
            return false;
        }
    }

    public Direction getDirection(Location loc) {
        // get the direction of the player N, S, W, E
        float rot = loc.getYaw() % 360;
        if (rot < 0) {
            rot += 360;
        }

        if (getConfig().getBoolean("debug.gotheres")) {
            log.Debug("Rotation: " + rot);
        }

        if ((rot >= 0 && rot < 45) || (rot >= 315 && rot <= 360)) {
            // WEST
            if (getConfig().getBoolean("debug.gotheres")) {
                log.Debug("Direction: WEST");
            }
            return Direction.WEST;
        } else if (rot >= 45 && rot < 135) {
            // NORTH
            if (getConfig().getBoolean("debug.gotheres")) {
                log.Debug("Direction: NORTH");
            }
            return Direction.NORTH;
        } else if (rot >= 135 && rot < 225) {
            // EAST
            if (getConfig().getBoolean("debug.gotheres")) {
                log.Debug("Direction: EAST");
            }
            return Direction.EAST;
        } else if (rot >= 225 && rot < 315) {
            // SOUTH
            if (getConfig().getBoolean("debug.gotheres")) {
                log.Debug("Direction: SOUTH");
            }
            return Direction.SOUTH;
        }
        return null;
    }

    public void RoadInfo(CommandSender sender, Road road, int keepid) {
        sender.sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "Road Info:");
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "Segments: " + ChatColor.WHITE + road.getParts().size());
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "GroundHeight: " + ChatColor.WHITE + road.getRoadPart(0).getGroundLayer());
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "PartSize:");
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "    Height: " + ChatColor.WHITE + road.getRoadPart(0).getHeight());
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "    Width: " + ChatColor.WHITE + road.getRoadPart(0).getWidth());
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "Stairs:");
        sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "    Set: " + ChatColor.WHITE + (road.getStairs() == null ? "No" : "Yes"));
        if (road.getStairs() != null) {
            sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "    Grade: " + ChatColor.WHITE + road.getMaxGradient());
        }
        if (keepid > 0) {
            sender.sendMessage("" + ChatColor.LIGHT_PURPLE + "Value Keeper Id: " + ChatColor.WHITE + keepid);
        }
        sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.WHITE + ChatColor.BOLD + "/testroad" + ChatColor.RESET + ChatColor.GOLD + " to test.");
    }
}
