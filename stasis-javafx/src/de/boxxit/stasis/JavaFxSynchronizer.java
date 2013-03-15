package de.boxxit.stasis;

import javafx.application.Platform;

/**
 * User: Christian Fruth
 */
public class JavaFxSynchronizer implements Synchronizer
{
	public JavaFxSynchronizer()
	{
	}

	@Override
	public void runLater(Runnable runnable)
	{
		Platform.runLater(runnable);
	}
}
