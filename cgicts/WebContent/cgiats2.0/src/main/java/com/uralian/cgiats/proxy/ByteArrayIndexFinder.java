/*
 * StringIndexFinder.java Apr 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.proxy;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.web.UIBean;

/**
 * This class searches for a sequence of bytes in the channel buffer.
 * 
 * @author Vlad Orzhekhovskiy
 */ 
public class ByteArrayIndexFinder implements ChannelBufferIndexFinder 
{
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final byte[] data;

	/**
	 * @param str
	 */
	public ByteArrayIndexFinder(String str)
	{
		this(str.getBytes());
	}

	/**
	 * @param data
	 */
	public ByteArrayIndexFinder(byte[] data)
	{
		this.data = data;
	}

	/**
	 * @return
	 */
	public byte[] getData()
	{
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.netty.buffer.ChannelBufferIndexFinder#find(org.jboss.netty.buffer
	 * .ChannelBuffer, int)
	 */
	@Override
	public boolean find(ChannelBuffer buffer, int guessedIndex)
	{
		if (guessedIndex + data.length > buffer.capacity())
			return false;

		try
		{
			for (int i = 0; i < data.length; i++)
			{
				if (buffer.getByte(guessedIndex + i) != data[i])
					return false;
			}
		}
		catch (Exception e)
		{
			log.error("Error reading from channel", e);
			return false;
		}

		return true;
	}
}