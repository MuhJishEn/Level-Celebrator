package com.levelcelebrator;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LevelCelebratorPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LevelCelebratorPlugin.class);
		RuneLite.main(args);
	}
}