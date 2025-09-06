package com.battery_level_alarm.monitoring.estimation.algoritms;

public class KalmanFilter {
	private double x;
	private double p;
	private final double q;
	private final double r;
	
	public KalmanFilter(double initialX, double processVar, double measVar) {
		this.x = initialX;
		this.q = processVar;
		this.r = measVar;
		this.p = 1.0;
	}
	
	public synchronized double update(double measurement) {
		double xPrediction = x, pPrediction = p + q;
		double k = pPrediction / (pPrediction + r);
		x = xPrediction + k * (measurement - xPrediction);
		p = (1 - k) * pPrediction;
		return x;
	}
	
	public synchronized void reset(double newState) {
		this.x = newState;
		this.p = 1.0;
	}
}