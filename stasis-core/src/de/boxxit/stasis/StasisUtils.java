package de.boxxit.stasis;

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * User: Christian Fruth
 */
public class StasisUtils
{
	public static final String GZIP_ENCODING = "gzip";
	public static final String CONTENT_ENCODING_KEY = "Content-Encoding";
	public static final String ACCEPT_ENCODING_KEY = "Accept-Encoding";

	public static boolean isUsingGzipEncoding(Enumeration<String> values)
	{
		if (values != null)
		{
			while (values.hasMoreElements())
			{
				if (isUsingGzipEncoding(values.nextElement()))
				{
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isUsingGzipEncoding(String headerValue)
	{
		if (headerValue == null)
		{
			return false;
		}

		for (StringTokenizer tokenizer = new StringTokenizer(headerValue, ","); tokenizer.hasMoreTokens(); )
		{
			String encoding = tokenizer.nextToken();
			int offset = encoding.indexOf(';');

			if (offset >= 0)
			{
				encoding = encoding.substring(0, offset);
			}

			if (GZIP_ENCODING.equalsIgnoreCase(encoding.trim()))
			{
				return true;
			}
		}

		return false;
	}

	private StasisUtils()
	{
	}
}
