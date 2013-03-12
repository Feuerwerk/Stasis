package de.boxxit.statis;

import org.springframework.security.access.annotation.Secured;

/**
 * User: Christian Fruth
 */
public class TestService
{
	public TestService()
	{
	}

	@Secured("IS_AUTHENTICATED_REMEMBERED")
	public String mich(int i)
	{
		return "Hallo " + i;
	}

	public void wuff(int i)
	{
		System.out.println("Wuff " + i);
	}
}
