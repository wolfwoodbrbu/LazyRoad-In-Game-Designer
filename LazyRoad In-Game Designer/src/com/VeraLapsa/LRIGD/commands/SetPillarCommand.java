package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.Direction;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import com.creadri.lazyroad.Pillar;
import com.creadri.lazyroad.PillarPart;
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
public class SetPillarCommand extends CommandHandler {

    private Pillar pillar = null;
    private int keepid = -1;

    public SetPillarCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        keepid = plugin.getConfig().getInt("users." + sender.getName() + ".keepid", -1);
        if (keepid < 0) {
            plugin.getConfig().set("users." + sender.getName() + ".keepid", -1);
            plugin.saveConfig();
        }
        return setPillar(sender);
    }

    private boolean setPillar(CommandSender sender) {
        Player player = (Player) sender;
        Location loc = player.getLocation();
        Direction dir = plugin.getDirection(loc);
        Region sel = null;
        WorldEditAPI WE = new WorldEditAPI((WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit"));
        pillar = new Pillar();
        try {
            sel = WE.getSession(player).getSelection(WE.getSession(player).getSelectionWorld());
        } catch (IncompleteRegionException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "World Edit selection doesn't contain any blocks.");
            Logger.getLogger(LRDHelpCommand.class.getName()).log(Level.WARNING, null, ex);
        }
        final int xmin = sel.getMinimumPoint().getBlockX();
        final int xmax = sel.getMaximumPoint().getBlockX();
        final int ymin = sel.getMinimumPoint().getBlockY();
        final int ymax = sel.getMaximumPoint().getBlockY();
        final int zmin = sel.getMinimumPoint().getBlockZ();
        final int zmax = sel.getMaximumPoint().getBlockZ();
        int r;
        switch (dir) {
            case NORTH:
                // positive north is -x and L2R is -z
                r = 1;
                for (int i = xmax; i >= xmin; i--) {
                    int[][] ids = new int[sel.getHeight()][sel.getLength()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getLength()];
                    int y = 0;
                    for (int j = zmax; j >= zmin; j--) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymax; k >= ymin; k--) {
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
                    PillarPart part = new PillarPart(sel.getHeight(), sel.getLength());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setRepeatEvery(r);
                    pillar.addPillarPart(part);
                    r++;
                }
                break;
            case SOUTH:
                // positive south is +x and L2R is +z
                r = 1;
                for (int i = xmin; i <= xmax; i++) {
                    // go throuugh every z layer + dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getLength()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getLength()];

                    int y = 0;
                    for (int j = zmin; j <= zmax; j++) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymax; k >= ymin; k--) {
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
                    PillarPart part = new PillarPart(sel.getHeight(), sel.getLength());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setRepeatEvery(r);
                    pillar.addPillarPart(part);
                    r++;
                }
                break;
            case EAST:
                // positive east is -z and L2R is +x
                r = 1;
                for (int i = zmax; i >= zmin; i--) {
                    // go throuugh every z layer + dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getWidth()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getWidth()];

                    int y = 0;
                    for (int j = xmin; j <= xmax; j++) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymax; k >= ymin; k--) {
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
                    PillarPart part = new PillarPart(sel.getHeight(), sel.getWidth());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setRepeatEvery(r);
                    pillar.addPillarPart(part);
                    r++;
                }
                break;
            case WEST:
                // positive west is +z and L2R is -x
                // Go through every x layer - dir so west to east
                r = 1;
                for (int i = zmin; i <= zmax; i++) {
                    // go throuugh every z layer - dir so L2R

                    int[][] ids = new int[sel.getHeight()][sel.getWidth()];
                    byte[][] datas = new byte[sel.getHeight()][sel.getWidth()];

                    int y = 0;
                    for (int j = xmax; j >= xmin; j--) {
                        // iterate up the y part of the selection
                        int x = 0;
                        for (int k = ymax; k >= ymin; k--) {
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
                    PillarPart part = new PillarPart(sel.getHeight(), sel.getWidth());
                    part.setIds(ids);
                    part.setDatas(datas);
                    part.setRepeatEvery(r);
                    pillar.addPillarPart(part);
                    r++;
                }
                break;
            default:
                return false;
        }
        plugin.pillarsetup.put(player.getName(), pillar);
        plugin.PillarInfo(sender, pillar, keepid);
        return true;
    }
}
