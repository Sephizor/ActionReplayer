package com.sephizor.actionreplayer;

public interface IDisplayChosenEventGenerator extends ISubscriber {
	public void subscribe(IDisplayChosenListener listener);
}
