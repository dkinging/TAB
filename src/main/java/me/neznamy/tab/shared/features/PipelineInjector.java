package me.neznamy.tab.shared.features;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.cpu.TabFeature;
import me.neznamy.tab.shared.features.interfaces.JoinEventListener;
import me.neznamy.tab.shared.features.interfaces.Loadable;

/**
 * A large source of hate. Packet intercepting to secure proper functionality of some features:
 * Tablist names - anti-override (preventing other plugins from setting this value)
 * Nametags - anti-override
 * SpectatorFix - to change gamemode to something else than spectator
 * PetFix - to remove owner field from entity data
 * Unlimited nametags - replacement for bukkit events with much better accuracy and reliability
 */
public abstract class PipelineInjector implements JoinEventListener, Loadable {

	//name of the pipeline decoder injected in netty
	public final String DECODER_NAME = "TAB";
	
	protected TAB tab;
	
	public PipelineInjector(TAB tab) {
		this.tab = tab;
	}
	
	/**
	 * Injects custom channel duplex handler to prevent other plugins from overriding this one
	 * @param uuid - player's uuid
	 */
	public abstract void inject(TabPlayer player);
	
	public abstract void uninject(TabPlayer player);
	
	@Override
	public void load() {
		for (TabPlayer p : tab.getPlayers()) {
			inject(p);
		}
	}

	@Override
	public void unload() {
		for (TabPlayer p : tab.getPlayers()) {
			uninject(p);
		}
	}
	
	@Override
	public void onJoin(TabPlayer connectedPlayer) {
		inject(connectedPlayer);
	}
	
	@Override
	public TabFeature getFeatureType() {
		return TabFeature.PIPELINE_INJECTION;
	}
	
	protected void logTeamOverride(String team, String player) {
		tab.getErrorManager().printError("Something just tried to add player " + player + " into team " + team, null, false, tab.getErrorManager().antiOverrideLog);
	}
}