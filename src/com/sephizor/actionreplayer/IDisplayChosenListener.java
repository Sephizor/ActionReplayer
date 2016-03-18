package com.sephizor.actionreplayer;

import java.awt.GraphicsDevice;

public interface IDisplayChosenListener extends ISubscriber {
	public void displayChosen(GraphicsDevice gd);
}
