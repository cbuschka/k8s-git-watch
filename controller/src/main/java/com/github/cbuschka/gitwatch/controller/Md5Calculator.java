package com.github.cbuschka.gitwatch.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Calculator
{
	public BigInteger hashCode(byte[] bytes)
	{

		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			return new BigInteger(md5.digest(bytes));
		}
		catch (SecurityException | NoSuchAlgorithmException ex)
		{
			throw new RuntimeException(ex);
		}
	}

}
