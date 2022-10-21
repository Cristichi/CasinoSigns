package obj;

public class Score {
	private String reason;
	private double mult;

	public Score(String reason, double multiplicator) {
		this.reason = reason;
		this.mult = multiplicator;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public double getMultiplicator() {
		return mult;
	}

	public void setMultiplicator(float mult) {
		this.mult = mult;
	}
}
