/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.Direction;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import com.creadri.lazyroad.Pillar;
import com.creadri.lazyroad.Road;
import com.creadri.lazyroad.RoadPart;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Wolfwood
 */
public class LRDCommand extends CommandHandler {

    private Road road = null;
    private Pillar pillar = null;
    private int keepid = -1;

    public LRDCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        if (plugin.keepid.containsKey(sender.getName())) {
            keepid = plugin.keepid.get(sender.getName());
        }
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("help")) {
                    showHelp(sender, "all");
                    return true;
                }
                if (args[0].equalsIgnoreCase("setroad")) {
                    return makeroad(sender);
                }
                if (args[0].equalsIgnoreCase("setstairs")) {
                    if (plugin.roadsetup.containsKey(sender.getName())) {
                        return setStairs(sender);
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you to add stairs to.");
                        return true;
                    }

                }
                if (args[0].equalsIgnoreCase("roadinfo")) {
                    if (plugin.roadsetup.containsKey(sender.getName())) {
                        road = plugin.roadsetup.get(sender.getName());
                        RoadInfo(sender);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you yet.");
                        return true;
                    }

                }
                if (args[0].equalsIgnoreCase("testroad")) {
                    if (plugin.roadsetup.containsKey(sender.getName())) {
                        saveRoad(sender, "temp", true);
                        sender.sendMessage(ChatColor.DARK_AQUA + "Testing currently set road. " + ChatColor.WHITE + "/lrc stoptest" + ChatColor.DARK_AQUA + " to stop and undo.");
                        plugin.getServer().dispatchCommand(sender, "road temp");
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you yet.");
                        return true;
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("stoptest")) {
                    sender.sendMessage(ChatColor.DARK_AQUA + "Stopping and undoing the test.");
                    plugin.getServer().dispatchCommand(sender, "road stop");
                    plugin.getServer().dispatchCommand(sender, "road undo");
                    return true;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("setgrade")) {
                    if (plugin.roadsetup.containsKey(sender.getName())) {
                        road = plugin.roadsetup.get(sender.getName());
                        try {
                            setGradiant(Integer.parseInt(args[1]));
                            plugin.roadsetup.put(sender.getName(), road);
                            RoadInfo(sender);
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(ChatColor.DARK_RED + "Enter a number as the gradiant next time.");
                            return true;
                        }
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you to add a gradiant to.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("setkeepid")) {
                    try {
                        keepid = Integer.parseInt(args[1]);
                        plugin.keepid.put(sender.getName(), keepid);
                        sender.sendMessage(ChatColor.DARK_AQUA + "Value Keeper Id set to " + ChatColor.WHITE + keepid);
                    } catch (NumberFormatException ex) {
                        sender.sendMessage(ChatColor.DARK_RED + "Enter a number as the Keep Id or a negitive number to disable.");
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("help")) {
                    showHelp(sender, args[1]);
                    return true;
                }
                if (args[0].equalsIgnoreCase("saveroad")) {
                    if (!isValidName(args[1])) {
                        sender.sendMessage(ChatColor.DARK_RED + "sorry that is not a valid name for a file");
                        return true;
                    }
                    if (plugin.roadsetup.containsKey(sender.getName())) {
                        if (saveRoad(sender, args[1], false)) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "No road has been set up yet to save.");
                        return true;
                    }
                }

            case 0:
            default:
                showHelp(sender, "all");
                return true;
        }
        sender.sendMessage(ChatColor.DARK_RED + "That is not a viable argument.");
        return false;
    }

    private void RoadInfo(CommandSender sender) {
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
    }

    private boolean saveRoad(CommandSender sender, String name, boolean temp) {
        road = plugin.roadsetup.get(sender.getName());
        if (road.size() == 0) {
            sender.sendMessage(ChatColor.DARK_RED + "You must have at least one part in order to save the road");
            return true;
        }
        if (road.getStairs() == null) {
            road.setStairs(new RoadPart(0, 0));
        }
        File folder = new File(plugin.getServer().getPluginManager().getPlugin("LazyRoad").getDataFolder(), "roads");
        if (folder == null || !folder.isDirectory()) {
            folder = plugin.getDataFolder();
            log.Severe("Error getting the LazyRoad's roads folder. Defaulting to the Designer's folder");
            sender.sendMessage(ChatColor.DARK_RED + "Error getting the LazyRoad's roads folder.");
            sender.sendMessage(ChatColor.DARK_RED + "Defaulting to the Designer's folder");
        }
        File saveFile = new File(folder, name.concat(".ser"));
        try {

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));

            oos.writeObject(road);

            oos.close();

        } catch (Exception ex) {
            sender.sendMessage(ChatColor.DARK_RED + "An error occured when trying to save " + saveFile.getName());
            log.Severe(ChatColor.DARK_RED + "An error occured when trying to save " + saveFile.getName());
            log.Severe(ex.toString());
        }
        if (!temp) {
            sender.sendMessage(ChatColor.DARK_GREEN + "The road " + ChatColor.WHITE + ChatColor.BOLD + saveFile.getName() + ChatColor.DARK_GREEN + " was saved.");
        }
        plugin.getServer().dispatchCommand(sender, "road reload");
        return true;
    }

    private void showHelp(CommandSender sender, String kind) {
        if (kind.equalsIgnoreCase("all")) {
            plugin.getServer().getHelpMap().getHelpTopic("lrd");
            return;
        }
        if (kind.equalsIgnoreCase("save")) {
            sender.sendMessage(ChatColor.BOLD + "Save Usage: [] " + ChatColor.RESET + "denotes required");
            sender.sendMessage(ChatColor.RED + "/lrd save [road|pillar] [filename] " + ChatColor.RESET + "- filename cannot contain spaces.");
            return;
        }
        sender.sendMessage(plugin.getServer().getHelpMap().getHelpTopic("lrd").getFullText(sender));
    }

    public static boolean isValidName(String text) {
        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n"
                + "^                                # Anchor to start of string.        \n"
                + "(?!                              # Assert filename is not: CON, PRN, \n"
                + "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
                + "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
                + "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
                + "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
                + "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
                + "  $                              # and end of string                 \n"
                + ")                                # End negative lookahead assertion. \n"
                + "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
                + "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
                + "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        boolean isMatch = matcher.matches();
        return isMatch;
    }

    private boolean makeroad(CommandSender sender) {
        Player player = (Player) sender;
        Location loc = player.getLocation();
        Direction dir = getDirection(loc);
        Region sel = null;

        WorldEditAPI WE = new WorldEditAPI((WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit"));

        road = new Road();
        try {
            sel = WE.getSession(player).getSelection(WE.getSession(player).getSelectionWorld());
        } catch (IncompleteRegionException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "World Edit selection doesn't contain any blocks.");
            Logger.getLogger(LRDCommand.class.getName()).log(Level.WARNING, null, ex);
        }
        final int groundLevel = player.getLocation().getBlockY() - sel.getMinimumPoint().getBlockY() - 1;
        final int xmin = sel.getMinimumPoint().getBlockX();
        final int xmax = sel.getMaximumPoint().getBlockX();
        final int ymin = sel.getMinimumPoint().getBlockY();
        final int ymax = sel.getMaximumPoint().getBlockY();
        final int zmin = sel.getMinimumPoint().getBlockZ();
        final int zmax = sel.getMaximumPoint().getBlockZ();
        int r = 1;
        // positive north is -z and L2R is +x

        // positive east is +x and L2R is +z

        // positive south is +z and L2R is -x

        // positive west is -x and L2R is -z

        switch (dir) {
            case NORTH:
                // Go through every z layer - dir so south to north
                r = 1;
                for (int i = zmax; i >= zmin; i--) {
                    // go throuugh every x layer + dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getWidth()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getWidth()];

                    int y = 0;
                    for (int j = xmin; j <= xmax; j++) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymin; k <= ymax; k++) {
                            Block worldblock = player.getWorld().getBlockAt(j, k, i);
                            BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                            if (block.getType() != keepid) {
                                ids[x][y] = block.getType();
                                datas[x][y] = (byte) block.getData();
                            } else {
                                ids[x][y] = -1;
                                datas[x][y] = 0;
                            }
                            x++;
                        }
                        y++;
                    }
                    RoadPart part = new RoadPart(sel.getHeight(), sel.getWidth());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setGroundLayer(groundLevel);
                    part.setRepeatEvery(r);
                    road.addRoadPart(part);
                    part = null;
                    r++;
                }
                break;
            case EAST:
                // Go through every x layer + dir so west to east
                r = 1;
                for (int i = xmin; i <= xmax; i++) {
                    // go throuugh every z layer + dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getLength()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getLength()];

                    int y = 0;
                    for (int j = zmin; j <= zmax; j++) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymin; k <= ymax; k++) {
                            Block worldblock = player.getWorld().getBlockAt(i, k, j);
                            BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                            //rotate to face north.
                            block.rotate90Reverse();
                            if (block.getType() != keepid) {
                                ids[x][y] = block.getType();
                                datas[x][y] = (byte) block.getData();
                            } else {
                                ids[x][y] = -1;
                                datas[x][y] = 0;
                            }
                            x++;
                        }
                        y++;
                    }
                    RoadPart part = new RoadPart(sel.getHeight(), sel.getLength());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setGroundLayer(groundLevel);
                    part.setRepeatEvery(r);
                    road.addRoadPart(part);
                    part = null;
                    r++;
                }
                break;
            case SOUTH:
                // Go through every z layer + dir so north to south
                r = 1;
                for (int i = zmax; i >= zmin; i--) {
                    // go throuugh every x layer - dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getWidth()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getWidth()];

                    int y = 0;
                    for (int j = xmin; j <= xmax; j++) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymin; k <= ymax; k++) {
                            Block worldblock = player.getWorld().getBlockAt(j, k, i);
                            BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                            //rotate to face north.
                            block.rotate90();
                            block.rotate90();
                            if (block.getType() != keepid) {
                                ids[x][y] = block.getType();
                                datas[x][y] = (byte) block.getData();
                            } else {
                                ids[x][y] = -1;
                                datas[x][y] = 0;
                            }
                            x++;
                        }
                        y++;
                    }
                    RoadPart part = new RoadPart(sel.getHeight(), sel.getWidth());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setGroundLayer(groundLevel);
                    part.setRepeatEvery(r);
                    road.addRoadPart(part);
                    part = null;
                    r++;
                }
                break;
            case WEST:
                // Go through every x layer - dir so west to east
                r = 1;
                for (int i = xmax; i >= xmin; i--) {
                    // go throuugh every z layer - dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getLength()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getLength()];

                    int y = 0;
                    for (int j = zmax; j >= zmin; j--) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymin; k <= ymax; k++) {
                            Block worldblock = player.getWorld().getBlockAt(i, k, j);
                            BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                            //rotate to face north.
                            block.rotate90();
                            if (block.getType() != keepid) {
                                ids[x][y] = block.getType();
                                datas[x][y] = (byte) block.getData();
                            } else {
                                ids[x][y] = -1;
                                datas[x][y] = 0;
                            }
                            x++;
                        }
                        y++;
                    }
                    RoadPart part = new RoadPart(sel.getHeight(), sel.getLength());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setGroundLayer(groundLevel);
                    part.setRepeatEvery(r);
                    road.addRoadPart(part);
                    part = null;
                    r++;
                }
                break;
            default:
                return false;
        }
        plugin.roadsetup.put(player.getName(), road);
        RoadInfo(sender);
        sender.sendMessage(ChatColor.GOLD + "Use " + ChatColor.WHITE + ChatColor.BOLD + "/lrd testroad" + ChatColor.RESET + ChatColor.GOLD + " to test.");
        return true;

    }

    private Direction getDirection(Location loc) {
        // get the direction of the player N, S, W, E
        float rot = loc.getYaw() % 360;
        if (rot < 0) {
            rot += 360;
        }

        if ((rot >= 0 && rot < 45) || (rot >= 315 && rot <= 360)) {
            // WEST
            return Direction.SOUTH;
        } else if (rot >= 45 && rot < 135) {
            // NORTH
            return Direction.WEST;
        } else if (rot >= 135 && rot < 225) {
            // EAST
            return Direction.NORTH;
        } else if (rot >= 225 && rot < 315) {
            // SOUTH
            return Direction.EAST;
        }
        return null;
    }

    private boolean setGradiant(int grade) {
        road.setMaxGradient(grade);
        return true;
    }

    private boolean setStairs(CommandSender sender) {
        road = plugin.roadsetup.get(sender.getName());

        Player player = (Player) sender;
        Location loc = player.getLocation();
        Direction dir = getDirection(loc);
        Region sel = null;

        WorldEditAPI WE = new WorldEditAPI((WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit"));

        try {
            sel = WE.getSession(player).getSelection(WE.getSession(player).getSelectionWorld());
        } catch (IncompleteRegionException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "World Edit selection doesn't contain any blocks.");
            Logger.getLogger(LRDCommand.class.getName()).log(Level.WARNING, null, ex);
        }
        final int groundLevel = player.getLocation().getBlockY() - sel.getMinimumPoint().getBlockY() - 1;
        final int xmin = sel.getMinimumPoint().getBlockX();
        final int xmax = sel.getMaximumPoint().getBlockX();
        final int ymin = sel.getMinimumPoint().getBlockY();
        final int ymax = sel.getMaximumPoint().getBlockY();
        final int zmin = sel.getMinimumPoint().getBlockZ();
        final int zmax = sel.getMaximumPoint().getBlockZ();
        int i = 0, j = 0, k = 0, x = 0, y = 0;
        RoadPart part = null;
        int[][] ids = null;
        byte[][] datas = null;
        switch (dir) {
            case NORTH:
                i = zmax;
                // go throuugh every x layer + dir so L2R

                ids = new int[sel.getHeight()][sel.getWidth()];
                datas = new byte[sel.getHeight()][sel.getWidth()];

                y = 0;
                for (j = xmin; j <= xmax; j++) {
                    // iterate up the y part of the selection
                    x = 0;
                    for (k = ymin; k <= ymax; k++) {
                        Block worldblock = player.getWorld().getBlockAt(j, k, i);
                        BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                        if (block.getType() != keepid) {
                            ids[x][y] = block.getType();
                            datas[x][y] = (byte) block.getData();
                        } else {
                            ids[x][y] = -1;
                            datas[x][y] = 0;
                        }
                        x++;
                    }
                    y++;
                }
                part = new RoadPart(sel.getHeight(), sel.getWidth());
                break;
            case EAST:
                i = xmin;
                ids = new int[sel.getHeight()][sel.getLength()];
                datas = new byte[sel.getHeight()][sel.getLength()];

                y = 0;
                for (j = zmin; j <= zmax; j++) {
                    // iterate up the y part of the selection
                    x = 0;
                    for (k = ymin; k <= ymax; k++) {
                        Block worldblock = player.getWorld().getBlockAt(i, k, j);
                        BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                        //rotate to face north.
                        block.rotate90Reverse();
                        if (block.getType() != keepid) {
                            ids[x][y] = block.getType();
                            datas[x][y] = (byte) block.getData();
                        } else {
                            ids[x][y] = -1;
                            datas[x][y] = 0;
                        }
                        x++;
                    }
                    y++;
                }
                part = new RoadPart(sel.getHeight(), sel.getLength());
                break;
            case SOUTH:
                i = zmin;
                ids = new int[sel.getHeight()][sel.getWidth()];
                datas = new byte[sel.getHeight()][sel.getWidth()];

                y = 0;
                for (j = xmin; j <= xmax; j++) {
                    // iterate up the y part of the selection
                    x = 0;
                    for (k = ymin; k <= ymax; k++) {
                        Block worldblock = player.getWorld().getBlockAt(j, k, i);
                        BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                        //rotate to face north.
                        block.rotate90();
                        block.rotate90();
                        if (block.getType() != keepid) {
                            ids[x][y] = block.getType();
                            datas[x][y] = (byte) block.getData();
                        } else {
                            ids[x][y] = -1;
                            datas[x][y] = 0;
                        }
                        x++;
                    }
                    y++;
                }
                part = new RoadPart(sel.getHeight(), sel.getWidth());
                break;
            case WEST:
                i = xmax;
                ids = new int[sel.getHeight()][sel.getLength()];
                datas = new byte[sel.getHeight()][sel.getLength()];

                y = 0;
                for (j = zmax; j >= zmin; j--) {
                    // iterate up the y part of the selection
                    x = 0;
                    for (k = ymin; k <= ymax; k++) {
                        Block worldblock = player.getWorld().getBlockAt(i, k, j);
                        BaseBlock block = new BaseBlock(worldblock.getTypeId(), worldblock.getData());
                        //rotate to face north.
                        block.rotate90();
                        if (block.getType() != keepid) {
                            ids[x][y] = block.getType();
                            datas[x][y] = (byte) block.getData();
                        } else {
                            ids[x][y] = -1;
                            datas[x][y] = 0;
                        }
                        x++;
                    }
                    y++;
                }
                part = new RoadPart(sel.getHeight(), sel.getLength());
                break;
            default:
                return false;
        }
        part.setIds(ids);
        part.setDatas(datas);
        part.setGroundLayer(groundLevel);
        road.setStairs(part);
        plugin.roadsetup.put(player.getName(), road);
        RoadInfo(sender);
        return true;
    }
}
