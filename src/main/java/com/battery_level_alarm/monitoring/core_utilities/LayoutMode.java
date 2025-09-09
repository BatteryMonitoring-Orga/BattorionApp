package com.battery_level_alarm.monitoring.core_utilities;

public enum LayoutMode {
	LEFT_MODE(0, 1, 0),          // ⇨ Center (id=1) | ⇦ Left (id=0)
	CENTER_MODE(1, 2, 0),        // ⇨ Right (id=2)  | ⇦ Left (id=0)
	RIGHT_MODE(2, 3, 1),         // ⇨ Popup (id=3)  | ⇦ Center (id=1)
	POPUP_MODE(3, 4, 2),         // ⇨ Toggle (id=4) | ⇦ Right (id=2)
	TOGGLE_MODE(4, 2, 3);        // ⇦ Right (id=2)  | ⇨ Popup (id=3)
	
	private final int id;
	private final int nextId;
	private final int previousId;
	
	LayoutMode(int id, int nextId, int previousId) {
		this.id = id;
		this.nextId = nextId;
		this.previousId = previousId;
	}
	
	public int getId() {
		return id;
	}
	
	public int getNextId() {
		return nextId;
	}
	
	public int getPreviousId() {
		return previousId;
	}
	
	public static LayoutMode getLayoutByID(int id) {
		return fromId(id);
	}
	
	public static LayoutMode fromId(int id) {
		for (LayoutMode mode : values()) {
			if (mode.id == id) {
				return mode;
			}
		}
		throw new IllegalArgumentException("Invalid LayoutMode id: " + id);
	}
}
