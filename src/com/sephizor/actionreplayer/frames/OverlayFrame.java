package com.sephizor.actionreplayer.frames;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sephizor.actionreplayer.IActionsRecordedListener;
import com.sephizor.actionreplayer.INotifier;
import com.sephizor.actionreplayer.ISubscriber;

public class OverlayFrame extends JFrame implements INotifier {

	private static final long serialVersionUID = 9016034427297719977L;
	public ArrayList<Point> actionsList;
	private HashMap<Point, Integer> actionsAtLocations;
	private ArrayList<IActionsRecordedListener> subscribers;

	public OverlayFrame(GraphicsDevice activeScreen) {
		actionsList = new ArrayList<Point>();
		actionsAtLocations = new HashMap<Point, Integer>();
		subscribers = new ArrayList<IActionsRecordedListener>();

		DisplayMode activeDisplayMode = activeScreen.getDisplayMode();
		JLabel escapeToCloseInfo = new JLabel("Press escape to close this window when you are done");
		int screenWidth = activeDisplayMode.getWidth();
		int screenHeight = activeDisplayMode.getHeight();

		this.setUndecorated(true);
		this.setLayout(null);
		this.setBackground(new Color(180, 180, 180, 95));
		this.setAlwaysOnTop(true);
		this.setSize(screenWidth, screenHeight);
		this.setLocation(activeScreen.getDefaultConfiguration().getBounds().x,
				activeScreen.getDefaultConfiguration().getBounds().y);

		escapeToCloseInfo.setBounds(10, 50, 500, 25);

		this.add(escapeToCloseInfo);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
					notifySubscribers();
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point mouseLocation = e.getLocationOnScreen();
				actionsList.add(mouseLocation);
				if (e.getButton() == MouseEvent.BUTTON1) {
					actionsAtLocations.put(mouseLocation, InputEvent.BUTTON1_DOWN_MASK);
				}
				else if(e.getButton() == MouseEvent.BUTTON2) {
					actionsAtLocations.put(mouseLocation, InputEvent.BUTTON2_DOWN_MASK);
				}
				else if(e.getButton() == MouseEvent.BUTTON3) {
					actionsAtLocations.put(mouseLocation, InputEvent.BUTTON3_DOWN_MASK);
				}
			}
		});

		this.setVisible(true);

		this.setFocusable(true);
		this.requestFocus();
	}
	
	public void reset() {
		actionsList.clear();
		actionsAtLocations.clear();
	}

	@Override
	public void addSubscriber(ISubscriber subscriber) {
		subscribers.add((IActionsRecordedListener) subscriber);
	}

	private void notifySubscribers() {
		for (IActionsRecordedListener subscriber : subscribers) {
			subscriber.actionsRecorded(actionsList, actionsAtLocations);
			this.setVisible(false);
		}
	}
}
