package com.solarrabbit.largeraids.command;

import java.util.Map;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.VersionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VillageCentresCommand implements CommandExecutor {
    private final LargeRaids plugin;

    public VillageCentresCommand(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;
        if (args.length == 0) {
            this.list((Player) sender);
            return true;
        }

        switch (args[0]) {
        case "add":
            if (args.length < 2)
                return false;
            this.add((Player) sender, args[1]);
            return true;
        case "remove":
            if (args.length < 2)
                return false;
            this.remove((Player) sender, args[1]);
            return true;
        default:
            return false;
        }
    }

    private void add(Player player, String name) {
        // TODO async it
        Location centre = this.plugin.getDatabase().getCentre(name);
        if (centre != null) {
            player.sendMessage(ChatColor.RED + "There is already an artificial village center of the same name!");
            return;
        }

        if (player.isFlying() || player.isInWater()) {
            player.sendMessage(ChatColor.RED + "Stand on a safe platform when creating an artificial village center!");
            return;
        }

        this.plugin.getDatabase().addCentre(player.getLocation(), name);
        VersionUtil.getVillageManager().addVillage(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Successfully added a new artificial village center!");
    }

    private void remove(Player player, String name) {
        // TODO async it
        Location centre = this.plugin.getDatabase().getCentre(name);
        if (centre == null) {
            player.sendMessage(ChatColor.RED + "There are no existing artificial village centers with that name!");
            return;
        }
        this.plugin.getDatabase().removeCentre(name);
        VersionUtil.getVillageManager().removeVillage(centre);
        player.sendMessage(ChatColor.GREEN + "Successfully removed an artificial village center!");

    }

    private void list(Player player) {
        // TODO async it
        Map<String, Location> map = this.plugin.getDatabase().getCentres();
        if (map.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "There are no existing artificial village centers!");
            return;
        }
        map.forEach((str, loc) -> {
            player.sendMessage(ChatColor.GREEN + str + " " + getLocString(loc));
        });
    }

    private String getLocString(Location loc) {
        return "[" + loc.getWorld().getName() + ", " + getRoundedDouble(loc.getX()) + ", "
                + getRoundedDouble(loc.getY()) + ", " + getRoundedDouble(loc.getZ()) + "]";
    }

    private String getRoundedDouble(double d) {
        return String.format("%.3f", d);
    }

}
