package de.boxxit.stasis.spring;

import java.util.Map;

/**
 * User: Christian Fruth
 */
public interface LoginValidator
{
	public boolean validate(Map<String, Object> request, Map<String, Object> response);
}
