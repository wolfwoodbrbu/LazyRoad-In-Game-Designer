package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class SetKeepIdCommand extends CommandHandler {

    public SetKeepIdCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        if (anonymousCheck(sender)) {
            return true;
        }
        try {
            int keepid = Integer.parseInt(args[0]);
            plugin.getConfig().set("users." + sender.getName() + ".keepid", keepid);
            plugin.saveConfig();
            sender.sendMessage(ChatColor.DARK_AQUA + "Value Keeper Id set to " + ChatColor.WHITE + keepid);
            return true;
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.DARK_RED + "Enter a number as the Keep Id or a negitive number to disable.");
            return true;
        }
    }
}
