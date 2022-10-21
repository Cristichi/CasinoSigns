package main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.plugins.Economy_Essentials;
import obj.CasinoSign;

public class CristichiCasinoSigns extends JavaPlugin implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;

//	private PluginDescriptionFile desc = getDescription();

	public static final ChatColor mainColor = ChatColor.DARK_AQUA;
	public static final ChatColor textColor = ChatColor.WHITE;
	public static final ChatColor accentColor = ChatColor.GOLD;
	public static final ChatColor errorColor = ChatColor.RED;
	public final String header = mainColor + "[Casino Signs] " + textColor;

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		getServer().getServicesManager().register(Economy_Essentials.class, new Economy_Essentials(this), this,
				ServicePriority.Normal);
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public void onEnable() {
		if (setupEconomy()) {
			getServer().getPluginManager().registerEvents(this, this);
			getLogger().info("Enabled");
		} else {
			log.severe(
					header + " This plugin needs Vault but it was not detected. We will proceed to disable ourselves.");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@EventHandler
	private void onSignChanged(SignChangeEvent e) {
		if (e.getPlayer().hasPermission("casinosigns.create")) {
			CasinoSign cdc = CasinoSign.place(e, new String[] { this.header + errorColor
					+ "To create a Casino Sign that costs, for example, 100, you need to write something like this:",
					textColor + "   [CS]", textColor + "   100" });
			if (cdc != null) {
				e.getPlayer().sendMessage(header + "You created a Casino Sign!");
			}
		} else {
			e.getPlayer().sendMessage(header + errorColor + "You don't have permission to create Casino Signs.");
		}
	}

	@EventHandler
	private void onSignClicked(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getPlayer().hasPermission("casinosigns.use")) {
				Block block = e.getClickedBlock();
				CasinoSign casinoSign = CasinoSign.check(block);
				if (casinoSign != null) {
					casinoSign.onPlayerRoll(this, e.getPlayer(), econ, header, textColor.toString(),
							accentColor.toString(), errorColor.toString());
				}
			} else {
				e.getPlayer()
						.sendMessage(header + errorColor + "You don't have permission to have fun with Casino Signs.");
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0)
			return false;
		switch (args[0]) {
		case "help":
			if (sender.hasPermission("casinosigns.create")) {
				sender.sendMessage(new String[] { this.header
						+ "To create a Casino Sign that costs, for example, 100, you need to write something like this:",
						textColor + "   [CS]", textColor + "   100" });
			}
			if (sender.hasPermission("casinosigns.use")) {
				sender.sendMessage(
						new String[] { this.header + "To use a Casino Sign that costs, right click on it." });
			}
			if (!sender.hasPermission("casinosigns.use") && !sender.hasPermission("casinosigns.create")) {
				sender.sendMessage(new String[] {
						this.header + errorColor + "It looks like you can neither create Casino Signs nor use them!" });
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> ret = new ArrayList<>(1);
		if (args.length <= 1) {
			ret.add("help");
		}
		return ret;
	}
}
