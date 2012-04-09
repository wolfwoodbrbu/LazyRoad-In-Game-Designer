package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import com.creadri.lazyroad.Pillar;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class PillarInfoCommand extends CommandHandler {

    private Pillar pillar = null;

    public PillarInfoCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        int keepid = plugin.getConfig().getInt("users." + sender.getName() + ".keepid", -1);
        if (plugin.pillarsetup.containsKey(sender.getName())) {
            pillar = plugin.pillarsetup.get(sender.getName());
            plugin.PillarInfo(sender, pillar, keepid);
            return true;
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "There's no pillar set for you yet.");
            return true;
        }
    }
}
