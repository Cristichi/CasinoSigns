package obj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SlotColumn extends ArrayList<ItemSlot> {
	private static final long serialVersionUID = 2465039072523553802L;
	
	private int index = 0;

	private SlotColumn(ItemSlot[] array, Random rng) {
		super(array.length);
		for (ItemSlot itemRuleta : array) {
			add(itemRuleta);
		}
		Collections.shuffle(this);
		index = rng.nextInt(array.length);
	}
	
	public SlotColumn(Random rng) {
		this(ItemSlot.values(), rng);
	}
	
	public ItemSlot[] next() {
			index++;
		return new ItemSlot[] {get(index+2), get(index+1), get(index)};
	}
	
	public ItemSlot[] current() {
		return new ItemSlot[] {get(index+2), get(index+1), get(index)};
	}
	
	@Override
	public ItemSlot get(int index) {
		return super.get(index % size());
	}
}
