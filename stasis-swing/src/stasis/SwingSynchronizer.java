package stasis;

import javax.swing.*;
import de.boxxit.stasis.Synchronizer;

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
