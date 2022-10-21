package obj;

import org.bukkit.ChatColor;

public class ItemSlot {
	private static ItemSlot[] items;
	static {
		ItemSlot.items = new ItemSlot[] { new ItemSlot('A', 1, ChatColor.GOLD), new ItemSlot('B', 1, ChatColor.GOLD),
				new ItemSlot('C', 1, ChatColor.GOLD), new ItemSlot('X', 1.5, ChatColor.RED),
				new ItemSlot('7', 2, ChatColor.DARK_RED), };
	}

	public static ItemSlot[] values() {
		return items;
	}

	private char symbol;
	private double value;
	private String color;

	public ItemSlot(char symbol, double value, ChatColor color) {
		this(symbol, value, color.toString());
	}

	public ItemSlot(char symbol, double value, String color) {
		this.symbol = symbol;
		this.value = value;
		this.color = color;
	}

	public char getSymbol() {
		return symbol;
	}

	public double getValue() {
		return value;
	}

	public String getColor() {
		return color;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Character) {
			return (Character) obj == symbol;
		}
		if (obj instanceof ItemSlot) {
			return ((ItemSlot) obj).symbol == symbol;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return color + Character.toString(symbol) + ChatColor.RESET;
	}
}
