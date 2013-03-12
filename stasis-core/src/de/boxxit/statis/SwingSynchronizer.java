package de.boxxit.statis;

import javax.swing.*;

/**
 * User: Christian Fruth
 */
public class SwingSynchronizer implements Synchronizer
{
	public SwingSynchronizer()
	{
	}

	@Override
	public void runLater(Runnable runnable)
	{
		SwingUtilities.invokeLater(runnable);
	}
}
