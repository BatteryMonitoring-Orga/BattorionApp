package com.battery_level_alarm.monitoring.estimation.algoritms;

/**
 * Exponential Moving Average
 */
public class EMA {
	private final double alpha;
	private Double value = null;
	
	public EMA(double alpha) {
		this.alpha = alpha;
	}
	
	public double update(double sample) {
		value = (value == null) ? sample : alpha * sample + (1 - alpha) * value;
		return value;
	}
	
	public double getValue() {
		return value == null ? Double.NaN : value;
	}
	
	public void reset(double init) {
		this.value = init;
	}
}