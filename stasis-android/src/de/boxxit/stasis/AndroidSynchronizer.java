package de.boxxit.stasis;

import android.app.Activity;

/**
 * User: Christian Fruth
 */
public class AndroidSynchronizer implements Synchronizer
{
	private Activity activity;

	public AndroidSynchronizer()
	{
	}

	public AndroidSynchronizer(Activity activity)
	{
		this.activity = activity;
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}

	@Override
	public void runLater(Runnable runnable)
	{
		activity.runOnUiThread(runnable);
	}
}
