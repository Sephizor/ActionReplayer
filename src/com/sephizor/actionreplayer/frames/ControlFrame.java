package com.sephizor.actionreplayer.frames;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.sephizor.actionreplayer.INotifier;
import com.sephizor.actionreplayer.ISubscriber;

public class ControlFrame extends JFrame implements INotifier {

	private static final long serialVersionUID = -7068276222631721115L;
	private ArrayList<ISubscriber> subscribers;
	private ArrayList<Point> coordinates;
	private HashMap<Point, Integer> actionsAtLocations;
	private OverlayFrame parent;
	private Thread actionRunnerThread;
	private GraphicsDevice chosenDisplay;
	private JScrollPane actionListScrollPane;
	private JPanel leftPanelContainer;
	private JPanel leftTopPanel;
	private JPanel leftBottomPanel;
	private JPanel rightPanel;
	private JList<String> actionListDisplay;
	private JTextField delayField;
	private JButton runButton;
	private JButton stopButton;
	private JButton resetButton;
	private int delayTime;
	private boolean running;

	public ControlFrame(OverlayFrame parent, ArrayList<Point> coordinates, HashMap<Point, Integer> actionsAtLocations, GraphicsDevice chosenDisplay) {
		subscribers = new ArrayList<ISubscriber>();
		running = false;
		delayTime = 3000;
		this.parent = parent;
		this.coordinates = coordinates;
		this.actionsAtLocations = actionsAtLocations;
		this.chosenDisplay = chosenDisplay;

		this.setTitle("Replay Controls");
		this.setSize(400, 300);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setupPanels();
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resizeLeftPanels(getWidth(), getHeight());
			}
		});
		
		this.setLayout(new GridLayout(1, 2));
	}

	@Override
	public void addSubscriber(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	private void runActions() {
		actionRunnerThread = new Thread(new ActionRunner());
		running = true;
		actionRunnerThread.start();
	}

	private void stopRunningActions() {
		if (actionRunnerThread != null) {
			running = false;
		}
	}
	
	private void setupPanels() {
		leftPanelContainer = new JPanel();
		rightPanel = new JPanel();
		leftTopPanel = new JPanel();
		leftBottomPanel = new JPanel();
		
		runButton = new JButton("Run");
		stopButton = new JButton("Stop");
		resetButton = new JButton("Record");

		actionListDisplay = new JList<>(new DefaultListModel<String>());
		delayField = new JTextField();
		actionListScrollPane = new JScrollPane(actionListDisplay);
		
		stopButton.setEnabled(false);
		delayField.setText("" + delayTime);
		
		actionListDisplay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		actionListDisplay.setLayoutOrientation(JList.VERTICAL);
		
		leftPanelContainer.setLayout(null);
		rightPanel.setLayout(new GridLayout(3, 1));
		leftTopPanel.setLayout(new GridLayout(1, 1));
		leftBottomPanel.setLayout(new GridLayout(1, 1));
		
		leftTopPanel.add(delayField);
		leftBottomPanel.add(actionListScrollPane);
		
		leftPanelContainer.add(leftTopPanel);
		leftPanelContainer.add(leftBottomPanel);

		rightPanel.add(runButton);
		rightPanel.add(stopButton);
		rightPanel.add(resetButton);
		
		delayField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				int[] range = { KeyEvent.VK_0, KeyEvent.VK_9 };
				
				int currentKeyCode = e.getKeyCode();
				
				if(!e.isControlDown() && currentKeyCode != KeyEvent.VK_CONTROL && currentKeyCode != KeyEvent.VK_BACK_SPACE) {
					if((currentKeyCode >= range[0] && currentKeyCode <= range[1]) || currentKeyCode == KeyEvent.VK_ENTER) {
						if(e.getKeyCode() == KeyEvent.VK_ENTER) {
							delayTime = Integer.parseInt(delayField.getText());
						}
					}
					else {
						// If the character entered is not a key character in the range 0-9, delete it
						String text = delayField.getText();
						delayField.setText(text.substring(0, text.length() - 1));
					}
				}
			}
		});
		
		actionListDisplay.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_DELETE) {
					int index = actionListDisplay.getSelectedIndex();
					if(index >= 0) {
						coordinates.remove(index);
						((DefaultListModel<String>)actionListDisplay.getModel()).remove(index);
					}
				}
			}
		});

		runButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(runButton.isEnabled()) {
					runActions();
					runButton.setEnabled(false);
					resetButton.setEnabled(false);
					stopButton.setEnabled(true);
					actionListDisplay.setEnabled(false);
				}
			}
		});

		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(stopButton.isEnabled()) {
					stopRunningActions();
					runButton.setEnabled(true);
					stopButton.setEnabled(false);
					resetButton.setEnabled(true);
					actionListDisplay.setEnabled(true);
				}
			}
		});

		resetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(resetButton.isEnabled()) {
					parent.setVisible(true);
					setVisible(false);
				}
			}
		});
		
		this.add(leftPanelContainer);
		this.add(rightPanel);
	}
	
	private void resizeLeftPanels(int newWidth, int newHeight) {
		leftTopPanel.setSize(leftPanelContainer.getWidth(), (int)Math.round(newHeight * 0.1));
		leftBottomPanel.setLocation(0, (int)Math.round(newHeight * 0.1));
		leftBottomPanel.setSize(leftPanelContainer.getWidth(), getHeight() - (int)Math.round(newHeight * 0.1));
	}
	
	public void setData(ArrayList<Point> actions, HashMap<Point, Integer> actionsAtLocations) {
		this.coordinates.addAll(actions);
		this.actionsAtLocations.putAll(actionsAtLocations);
		for(Point p : actions) {
			((DefaultListModel<String>)actionListDisplay.getModel()).addElement("" + p.getX() + ", " + p.getY());
		}
		
	}

	private class ActionRunner implements Runnable {

		@Override
		public void run() {
			try {
				Robot robot = new Robot(chosenDisplay);
				while (true) {
					for (int i = 0; i < coordinates.size(); i++) {
						if(running) {
							Point currentPoint = coordinates.get(i);
							int actionType = actionsAtLocations.get(currentPoint);
							int randomTime = new Random().nextInt(120);

							robot.mouseMove(currentPoint.x, currentPoint.y);
							robot.mousePress(actionType);
							robot.mouseRelease(actionType);

							try {
								Thread.sleep(randomTime + delayTime);
							}
							catch (InterruptedException e) {
								e.printStackTrace();
								System.exit(-1);
							}
						}
						else {
							break;
						}
					}
				}
			} catch (AWTException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
