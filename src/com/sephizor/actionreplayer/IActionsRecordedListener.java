package com.sephizor.actionreplayer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public interface IActionsRecordedListener {
	public void actionsRecorded(ArrayList<Point> actions, HashMap<Point, Integer> actionsAtLocations);
}
