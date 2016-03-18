package com.sephizor.actionreplayer.frames;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.sephizor.actionreplayer.IDisplayChosenListener;
import com.sephizor.actionreplayer.INotifier;
import com.sephizor.actionreplayer.ISubscriber;

public class DisplayChooser extends JFrame implements INotifier {
	private static final long serialVersionUID = 5793955102747254854L;

	private Map<JButton, GraphicsDevice> btnToDisplayMap;
	private ArrayList<IDisplayChosenListener> subscribers;

	public DisplayChooser() {
		btnToDisplayMap = new HashMap<JButton, GraphicsDevice>();
		subscribers = new ArrayList<IDisplayChosenListener>();

		GraphicsDevice[] displays = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		this.setLayout(new GridLayout(1, displays.length));
		this.setBounds(this.getX(), this.getY(), 100 * displays.length, 100);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		for (int i = 0; i < displays.length; i++) {
			JButton btn = new JButton("Display " + (i + 1));
			this.add(btn);
			btnToDisplayMap.put(btn, displays[i]);

			btn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					notifyListeners(btnToDisplayMap.get(btn));
					setVisible(false);
				}
			});
		}

		this.setVisible(true);
	}

	public void addSubscriber(ISubscriber subscriber) {
		subscribers.add((IDisplayChosenListener) subscriber);
	}

	private void notifyListeners(GraphicsDevice gd) {
		for (IDisplayChosenListener subscriber : subscribers) {
			subscriber.displayChosen(gd);
			this.dispose();
		}
	}
}
