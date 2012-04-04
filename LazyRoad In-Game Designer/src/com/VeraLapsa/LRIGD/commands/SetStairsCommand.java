package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.Direction;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import com.creadri.lazyroad.Road;
import com.creadri.lazyroad.RoadPart;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author VeraLapsa
 */
public class SetStairsCommand extends CommandHandler {

    private Road road = null;
    private int keepid = -1;

    public SetStairsCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        keepid = plugin.getConfig().getInt("users." + sender.getName() + ".keepid", -1);
        if (plugin.roadsetup.containsKey(sender.getName())) {
            return setStairs(sender);
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "There's no road set for you to add stairs to.");
            return true;
        }
    }

    private boolean setStairs(CommandSender sender) {
        road = plugin.roadsetup.get(sender.getName());

        Player player = (Player) sender;
        Location loc = player.getLocation();
        Direction dir = plugin.getDirection(loc);
        Region sel = null;

        WorldEditAPI WE = new WorldEditAPI((WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit"));

        try {
            sel = WE.getSession(player).getSelection(WE.getSession(player).getSelectionWorld());
        } catch (IncompleteRegionException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "World Edit selection doesn't contain any blocks.");
            Logger.getLogger(LRDHelpCommand.class.getName()).log(Level.WARNING, null, ex);
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
                // positive north is -x and L2R is -z
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
            case EAST:
                i = zmax;
                // go throuugh every z layer + dir so L2R

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
                part = new RoadPart(sel.getHeight(), sel.getWidth());
                break;
            case SOUTH:
                i = xmax;
                // go throuugh every z layer + dir so L2R

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
                part = new RoadPart(sel.getHeight(), sel.getLength());
                break;
            case WEST:
                i = zmin;
                // go throuugh every z layer - dir so L2R

                ids = new int[sel.getHeight()][sel.getWidth()];
                datas = new byte[sel.getHeight()][sel.getWidth()];

                y = 0;
                for (j = xmax; j >= xmin; j--) {
                    // iterate up the y part of the selection
                    x = 0;
                    for (k = ymin; k <= ymax; k++) {
                        Block worldblock = player.getWorld().getBlockAt(j, k, i);
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
                part = new RoadPart(sel.getHeight(), sel.getWidth());
                break;
            default:
                return false;
        }
        part.setIds(ids);
        part.setDatas(datas);
        part.setGroundLayer(groundLevel);
        road.setStairs(part);
        plugin.roadsetup.put(player.getName(), road);
        plugin.RoadInfo(sender, road, keepid);
        return true;
    }
}
