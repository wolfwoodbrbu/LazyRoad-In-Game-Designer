package com.VeraLapsa.LRIGD.commands;

import com.VeraLapsa.LRIGD.CommandHandler;
import com.VeraLapsa.LRIGD.LazyRoadInGameDesigner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author VeraLapsa
 */
public class LRDHelpCommand extends CommandHandler {

    public LRDHelpCommand(LazyRoadInGameDesigner plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                if (showHelp(sender, args[0])) {
                    return true;
                }
                sender.sendMessage(ChatColor.DARK_RED + "That is not a viable argument.");
            case 0:
            default:
                return showHelp(sender, "all");
        }

    }

    private boolean showHelp(CommandSender sender, String kind) {
        if (kind.equalsIgnoreCase("all")) {
            plugin.getServer().dispatchCommand(sender, "help LazyRoadInGameDesigner");
            plugin.getServer().dispatchCommand(sender, "help LazyRoadInGameDesigner 2");
            return true;
        }
        if (kind.equalsIgnoreCase("lrdhelp")) {
            plugin.getServer().dispatchCommand(sender, "help lrdhelp");
            return true;
        }
        if (kind.equalsIgnoreCase("setroad")) {
            plugin.getServer().dispatchCommand(sender, "help setroad");
            return true;
        }
        if (kind.equalsIgnoreCase("setstairs")) {
            plugin.getServer().dispatchCommand(sender, "help setstairs");
            return true;
        }
        if (kind.equalsIgnoreCase("setgrade")) {
            plugin.getServer().dispatchCommand(sender, "help setgrade");
            return true;
        }
        if (kind.equalsIgnoreCase("setkeepid")) {
            plugin.getServer().dispatchCommand(sender, "help setkeepid");
            return true;
        }
        if (kind.equalsIgnoreCase("saveroad")) {
            plugin.getServer().dispatchCommand(sender, "help saveroad");
            return true;
        }
        if (kind.equalsIgnoreCase("testroad")) {
            plugin.getServer().dispatchCommand(sender, "help testroad");
            return true;
        }
        if (kind.equalsIgnoreCase("teststop")) {
            plugin.getServer().dispatchCommand(sender, "help teststop");
            return true;
        }
        if (kind.equalsIgnoreCase("roadinfo")) {
            plugin.getServer().dispatchCommand(sender, "help roadinfo");
            return true;
        }
        if (kind.equalsIgnoreCase("pillarinfo")) {
            plugin.getServer().dispatchCommand(sender, "help pillarinfo");
            return true;
        }
        if (kind.equalsIgnoreCase("setroad")) {
            plugin.getServer().dispatchCommand(sender, "help setpillar");
            return true;
        }
        if (kind.equalsIgnoreCase("saveroad")) {
            plugin.getServer().dispatchCommand(sender, "help savepillar");
            return true;
        }
        if (kind.equalsIgnoreCase("testroad")) {
            plugin.getServer().dispatchCommand(sender, "help testpillar");
            return true;
        }
        return false;
    }
}
