package com.levelcelebrator;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import java.util.*;

@Slf4j
@PluginDescriptor(name = "Level Celebrator", description = "Celebrate every new skill level like you deserve.")
public class LevelCelebratorPlugin extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private LevelCelebratorConfig config;

	private Player localPlayer;
	private final Map<Skill, Integer> previousSkillExpTable = new EnumMap<>(Skill.class);
	private Integer tickCounter = 0;
	private Boolean localAnimIsActive = false;

	final private Integer JUMP_FOR_JOY_ANIMATION = 2109;
	final private Integer HEADBANG_ANIMATION = 2108;
	final private Integer SPIN_ANIMATION = 2107;
	final private Integer JIG_ANIMATION = 2106;
	final private Integer FIREWORKS_ANIMATION = 1388;
	final private Integer CHEER_ANIMATION = 862;
	final private Integer CLAP_ANIMATION = 865;
	final private Integer DANCE_ANIMATION = 866;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			localPlayer = client.getLocalPlayer();
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		final Skill skill = statChanged.getSkill();
		Integer previous = previousSkillExpTable.put(skill, statChanged.getXp());
		if (previous != null)
		{
			startAnimationsIfLeveledUp(skill.getName(), previous, statChanged.getLevel());
		}
	}

	private void startAnimationsIfLeveledUp(String skillName, int previousXp, int currentLevel) {
		int levelBeforeExpGained = Experience.getLevelForXp(previousXp);
		boolean leveledUp = levelBeforeExpGained < currentLevel;

		if (leveledUp)
		{
			setLocalPlayerAnimations(skillName, currentLevel);
			if (config.showOtherPlayerAnims())
			{
				setPublicCelebrationAnimations();
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (localAnimIsActive)
		{
			tickCounter += 1;
			if (tickCounter >= 10)
			{
				tickCounter = 0;
				localAnimIsActive = false;
				localPlayer.removeSpotAnim(0);
			}
		}
	}

	private void setLocalPlayerAnimations(String skillName, Integer level)
	{
		Integer localPlayerAnimation = getSelectedPlayerAnimation();

		if (localPlayerAnimation != null)
		{
			localPlayer.setAnimation(localPlayerAnimation);
		}

		String overheadText = config.localOverheadText()
				.replace("{skill.level}", String.valueOf(level))
				.replace("{skill.name}", skillName);
		if (!overheadText.isBlank()) {
			localPlayer.setOverheadText(overheadText);
			localPlayer.setOverheadCycle(175);
		}

		if (config.showFireworkAnim())
		{
			localPlayer.createSpotAnim(0, FIREWORKS_ANIMATION, 0, 0);
			localAnimIsActive = true;
		}
	}

	private Integer getSelectedPlayerAnimation() {
		switch (config.localPlayerEmote().toString())
		{
			case "DANCE":
				return DANCE_ANIMATION;
			case "CHEER":
				return CHEER_ANIMATION;
			case "HEADBANG":
				return HEADBANG_ANIMATION;
			case "SPIN":
				return SPIN_ANIMATION;
			case "JIG":
				return JIG_ANIMATION;
			case "CLAP":
				return CLAP_ANIMATION;
			case "NONE":
				return null;
			default:
				return JUMP_FOR_JOY_ANIMATION;
		}
	}

	private void setPublicCelebrationAnimations()
	{
		List<Integer> celebrationEmotes = Arrays.asList(
				HEADBANG_ANIMATION, JIG_ANIMATION, CHEER_ANIMATION, CLAP_ANIMATION, DANCE_ANIMATION, SPIN_ANIMATION);

		for (Player player : client.getPlayers())
		{
			if (player != localPlayer)
			{
				Integer animationId = celebrationEmotes.get(new Random().nextInt(celebrationEmotes.size()));
				player.setAnimation(animationId);
			}
		}
	}

	@Provides
	LevelCelebratorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LevelCelebratorConfig.class);
	}

}
