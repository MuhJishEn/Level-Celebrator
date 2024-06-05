package com.levelcelebrator;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("levelcelebrator")
public interface LevelCelebratorConfig extends Config
{

	@ConfigItem(
			keyName = "showFireworkAnim",
			name = "Show Firework Animation",
			description = "Hides the firework animation if checked when a new level is celebrated.",
			position = 0
	)
	default boolean showFireworkAnim(){ return true; }

	@ConfigItem(
			keyName = "localOverheadText",
			name = "Player Overhead Text",
			description = "Edit your player's overhead text when a new level is celebrated.\n\n" +
					"Use {skill.level} and {skill.name} to dynamically reference the skill level and name.",
			position = 1
	)
	default String localOverheadText(){ return "I did it! Finally level {skill.level} {skill.name}!!!"; }

	enum EmoteOption
	{
		JUMP_FOR_JOY,
		DANCE,
		CHEER,
		HEADBANG,
		SPIN,
		JIG,
		CLAP,
		NONE;

		@Override
		public String toString()
		{
			if (this == JUMP_FOR_JOY)
			{
				return "Jump for Joy";
			}
			return name();
		}
	}
	@ConfigItem(
			keyName = "localPlayerEmote",
			name = "Player Emote",
			description = "Select the emote your player performs when celebrating.",
			position = 2
	)
	default EmoteOption localPlayerEmote(){ return EmoteOption.JUMP_FOR_JOY; }

	@ConfigItem(
			keyName = "showOtherPlayerAnims",
			name = "Show Other Player Animations",
			description = "Enables public player emote animations if checked.",
			position = 3
	)
	default boolean showOtherPlayerAnims(){ return true; }
	
}
