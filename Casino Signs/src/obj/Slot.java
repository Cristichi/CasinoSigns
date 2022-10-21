package obj;

import java.util.Random;

public class Slot {
	private Random rng;
	private SlotColumn rollo1, rollo2, rollo3;
	private int giros1, giros2, giros3;

	public Slot() {
		rng = new Random();
		this.rollo1 = new SlotColumn(rng);
		this.rollo2 = new SlotColumn(rng);
		this.rollo3 = new SlotColumn(rng);
	}

	public void reset(int minGiros1, int maxGiros1, int dif) {
		double x = Math.random() * (maxGiros1 - minGiros1) + minGiros1;
		double y = Math.random() * (maxGiros1 - minGiros1) + minGiros1;
		double z = Math.random() * (maxGiros1 - minGiros1) + minGiros1;
		double max = Math.max(x, Math.max(y, z));
		double min = Math.min(x, Math.min(y, z));
		double mid = x + y + z - max - min;
		giros1 = (int) min;
		giros2 = (int) mid + dif;
		giros3 = (int) max + dif * 2;
	}

	// TODO: En lugar de avanzar aleatoriamente, avanzan a la par pero con 3
	// cantidades de veces diferente
	public ItemSlot[][] roll() {
		ItemSlot[] i1 = rollo1.current();
		ItemSlot[] i2 = rollo2.current();
		ItemSlot[] i3 = rollo3.current();
		if (giros1 > 0) {
			i1 = rollo1.next();
			giros1--;
		}
		if (giros2 > 0) {
			i2 = rollo2.next();
			giros2--;
		}
		if (giros3 > 0) {
			i3 = rollo3.next();
			giros3--;
		}
		return new ItemSlot[][] { new ItemSlot[] { i1[0], i2[0], i3[0] }, new ItemSlot[] { i1[1], i2[1], i3[1] },
				new ItemSlot[] { i1[2], i2[2], i3[2] } };
	}

	public boolean isFinished() {
		return giros1 == giros2 && giros2 == giros3 && giros3 == 0;
	}

	public ItemSlot[][] actual() {
		ItemSlot[] i1 = rollo1.current();
		ItemSlot[] i2 = rollo2.current();
		ItemSlot[] i3 = rollo3.current();
		return new ItemSlot[][] { new ItemSlot[] { i1[0], i2[0], i3[0] }, new ItemSlot[] { i1[1], i2[1], i3[1] },
				new ItemSlot[] { i1[2], i2[2], i3[2] } };
	}

	public Score getScore() {
		ItemSlot[][] m = actual();
		if (m[1][0].equals(m[1][1]) && m[1][1].equals(m[1][2])) {
			return new Score("central line |─|" + (m[1][0].getValue() > 1 ? " of " + m[1][0].toString() : ""),
					5 * m[1][0].getValue());
		}
		if (m[0][0].equals(m[0][1]) && m[0][1].equals(m[0][2])) {
			return new Score("top line |‾|" + (m[0][0].getValue() > 1 ? " of " + m[0][0].toString() : ""),
					2 * m[0][0].getValue());
		}
		if (m[2][0].equals(m[2][1]) && m[2][1].equals(m[2][2])) {
			return new Score("bottom line |_|" + (m[2][0].getValue() > 1 ? " of " + m[2][0].toString() : ""),
					2 * m[2][0].getValue());
		}
		if (m[0][0].equals(m[1][1]) && m[1][1].equals(m[2][2])) {
			return new Score(
					"descending diagonal |\\|" + (m[0][0].getValue() > 1 ? " of " + m[0][0].toString() : ""),
					2 * m[0][0].getValue());
		}
		if (m[2][0].equals(m[1][1]) && m[1][1].equals(m[0][2])) {
			return new Score(
					"ascending diagonal |/|" + (m[2][0].getValue() > 1 ? " of " + m[2][0].toString() : ""),
					2 * m[2][0].getValue());
		}
		return new Score("bad luck", 0);
	}
}
