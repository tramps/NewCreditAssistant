package com.rong360.creditassitant.model;


public class TelHelper {
	public static final String INTERNATION_PREFIX = "+86";
	
	private static final String phoneCharactersPattern = "[[+86]*#,()/; -"; // phones
																// characters;
	
	private static final String TAG = "TelHelper";
	
	public static String getPureTel(String tel) {
		if (null == tel) {
			return tel;
		}
		String pureTel = tel.trim();
		if (pureTel.startsWith(INTERNATION_PREFIX)) {
			pureTel = pureTel.substring(INTERNATION_PREFIX.length());
		}
		return pureTel;
	}
	
	public static String getTelNums(String tel) {
		return tel.trim().replaceAll(phoneCharactersPattern, "");
	}
}