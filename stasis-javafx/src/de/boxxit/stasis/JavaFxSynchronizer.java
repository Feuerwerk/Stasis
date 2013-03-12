package de.boxxit.stasis;

import de.boxxit.statis.Synchronizer;
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
