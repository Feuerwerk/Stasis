package de.boxxit.stasis;

import android.os.Handler;

/**
 * User: Christian Fruth
 */
public class AndroidSynchronizer implements Synchronizer
{
	private Handler syncHandler;

	public AndroidSynchronizer()
	{
		syncHandler = new Handler();
	}

	@Override
	public void runLater(Runnable runnable)
	{
		syncHandler.post(runnable);
	}
}
