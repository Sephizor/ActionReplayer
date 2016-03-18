package com.sephizor.actionreplayer;

import java.awt.GraphicsDevice;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import com.sephizor.actionreplayer.frames.ControlFrame;
import com.sephizor.actionreplayer.frames.DisplayChooser;
import com.sephizor.actionreplayer.frames.OverlayFrame;

public class ActionReplayer implements IDisplayChosenListener, IActionsRecordedListener {

	private GraphicsDevice chosenDisplay;
	private OverlayFrame overlay;
	private ControlFrame controls;

	public static void main(String[] args) {
		new ActionReplayer().run();
	}

	public void run() {
		new DisplayChooser().addSubscriber(this);
	}

	@Override
	public void displayChosen(GraphicsDevice gd) {
		chosenDisplay = gd;
		overlay = new OverlayFrame(gd);
		overlay.addSubscriber(this);
		controls = new ControlFrame(overlay, new ArrayList<Point>(), new HashMap<Point, Integer>(), chosenDisplay);
	}

	@Override
	public void actionsRecorded(ArrayList<Point> actions, HashMap<Point, Integer> actionsAtLocations) {
		controls.setData(actions, actionsAtLocations);
		controls.setVisible(true);
		overlay.reset();
	}

}
