package obj;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;

import main.CristichiCasinoSigns;
import net.milkbowl.vault.economy.Economy;

public class CasinoSign {
	public static final String[] HEADER_WRITTEN_SIGN = new String[] { "[Casino Sign]", "[CS]", "[Casino]" };
	public static final String HEADER_SIGN_VISIBLE = CristichiCasinoSigns.mainColor + HEADER_WRITTEN_SIGN[0];
	public static final String HEADER_SIGN_ERROR = CristichiCasinoSigns.errorColor + HEADER_WRITTEN_SIGN[0];
	public static final String MONEY_COLOR = ChatColor.GREEN.toString();

	private Sign sign;
	private double cost;
	private Slot slot;
	private int threadRotate;

	public static CasinoSign place(SignChangeEvent e, String[] error) {
		Block block = e.getBlock();
		CasinoSign cdc = null;
		if (block != null) {
			BlockState bs = block.getState();
			if (bs instanceof Sign) {
				Sign s = (Sign) block.getState();
				for (String header : CasinoSign.HEADER_WRITTEN_SIGN) {
					if (e.getLine(0).equalsIgnoreCase(header)) {
						cdc = new CasinoSign();
						e.setLine(0, CasinoSign.HEADER_SIGN_VISIBLE);
						if (e.getLine(1).trim().isEmpty()) {
							e.setLine(0, CasinoSign.HEADER_SIGN_ERROR);
							e.setLine(1, "");
							e.setLine(2, "");
							e.setLine(3, "");
							e.getPlayer().sendMessage(error);
							cdc = null;
						} else {
							try {
								double d = Double.parseDouble(e.getLine(1));
								cdc.setCost(d);
								cdc.slot = new Slot();
								cdc.sign = s;
								e.setLine(1, MONEY_COLOR + Double.toString(d));
								e.setLine(2, ChatColor.WHITE + "Try your luck!");
								e.setLine(3, ChatColor.WHITE + "Right click");
							} catch (NumberFormatException ex) {
								e.setLine(0, CasinoSign.HEADER_SIGN_ERROR);
								e.setLine(1, "");
								e.setLine(2, "");
								e.setLine(3, "");
								cdc = null;
							}
						}
					}
				}
			}
		}
		return cdc;
	}

	public static CasinoSign check(Block bloque) {
		CasinoSign cdc = new CasinoSign();
		if (bloque != null) {
			BlockState bs = bloque.getState();
			if (bs instanceof Sign) {
				Sign s = (Sign) bloque.getState();
				String[] sLines = s.getLines();
				if (sLines[0].equalsIgnoreCase(HEADER_SIGN_VISIBLE)) {
					if (sLines[1].trim().isEmpty()) {
						s.setLine(0, HEADER_SIGN_ERROR);
						s.setLine(1, "");
						s.setLine(2, "");
						s.setLine(3, "");
						return null;
					} else {
						try {
							double d = Math.abs(Double.parseDouble(sLines[1].replace(MONEY_COLOR, "")));
							cdc.slot = new Slot();
							cdc.cost = d;
							cdc.sign = s;
							return cdc;
						} catch (NumberFormatException e) {
							s.setLine(0, HEADER_SIGN_ERROR);
							s.setLine(1, "");
							s.setLine(2, "");
							s.setLine(3, "");
							return null;
						}
					}
				}
			}
		}
		return null;
	}

	public void reset(int minRolls, int maxRolls, int diff) {
		slot.reset(minRolls, maxRolls, diff);
	}

	public boolean isFinished() {
		return slot.isFinished();
	}

	public void roll(Player p) {
		ItemSlot[][] items = slot.roll();
		sign.setLine(0, "- [" + items[0][0] + "][" + items[0][1] + "][" + items[0][2] + "] -");
		sign.setLine(1, "- [" + items[1][0] + "][" + items[1][1] + "][" + items[1][2] + "] -");
		sign.setLine(2, "- [" + items[2][0] + "][" + items[2][1] + "][" + items[2][2] + "] -");
		sign.setLine(3, p.getDisplayName());
		sign.update();
	}

	public void lastRoll(Player p) {
		ItemSlot[][] items = slot.actual();
		sign.setLine(0, ChatColor.GOLD + "-" + ChatColor.RESET + " [" + items[0][0] + "][" + items[0][1] + "]["
				+ items[0][2] + "] " + ChatColor.GOLD + "-");
		sign.setLine(1, ChatColor.GOLD + "-" + ChatColor.RESET + " [" + items[1][0] + "][" + items[1][1] + "]["
				+ items[1][2] + "] " + ChatColor.GOLD + "-");
		sign.setLine(2, ChatColor.GOLD + "-" + ChatColor.RESET + " [" + items[2][0] + "][" + items[2][1] + "]["
				+ items[2][2] + "] " + ChatColor.GOLD + "-");
		sign.setLine(3, p.getDisplayName());
		sign.update();
	}

	public void finishRolling(Player p) {
		sign.setLine(0, HEADER_SIGN_VISIBLE);
		sign.setLine(1, MONEY_COLOR + Double.toString(cost));
		sign.setLine(2, ChatColor.WHITE + "Try your luck!");
		sign.setLine(3, ChatColor.WHITE + "Right click");
		sign.update();
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Slot getSlot() {
		return slot;
	}

	@Override
	public String toString() {
		return "CasinoDeCartel [cost=" + cost + "]";
	}

	public void onPlayerRoll(Plugin plugin, Player p, Economy econ, String header, String textColor, String accentColor,
			String errorColor) {
		if (!econ.hasAccount(p)) {
			econ.createPlayerAccount(p);
		}
		if (econ.getBalance(p) < getCost()) {
			p.sendMessage(header + errorColor + "You can't afford this roll. You have " + accentColor
					+ econ.getBalance(p) + errorColor + " and you need " + accentColor + getCost() + errorColor + ".");
			return;
		}
		econ.withdrawPlayer(p, getCost());
		p.sendMessage(header + "You paid " + accentColor + getCost() + textColor + " to roll! Best of luck!");

		reset(20, 40, 10);
//		reset(5, 10, 0);
		Runnable run = new Runnable() {
			boolean finished = false;

			@Override
			public void run() {
				if (!finished)
					if (isFinished()) {
//						cancelTask();
						Bukkit.getScheduler().cancelTask(threadRotate);
						lastRoll(p);
						ItemSlot[][] items = getSlot().actual();
						Score score = getSlot().getScore();
						double winMoney = getCost() * score.getMultiplicator();
						econ.depositPlayer(p, winMoney);
						p.sendMessage(new String[] { header + "Your result:",
								".                           " + items[0][0] + " " + items[0][1] + " " + items[0][2],
								".                           " + items[1][0] + " " + items[1][1] + " " + items[1][2],
								".                           " + items[2][0] + " " + items[2][1] + " " + items[2][2],
								header + "You won " + MONEY_COLOR + winMoney + textColor + " for your " + accentColor
										+ score.getReason() + textColor + ". Your current balance: " + MONEY_COLOR
										+ econ.getBalance(p) + textColor + ".", });

						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								finishRolling(p);
							}
						}, 100);
//						}, 0);
						finished = true;
					} else {
						roll(p);
					}
			}
		};
		threadRotate = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run, 0, 6);
//		hiloGirar = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, run, 0, 1);
	}
}
