/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2016 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.manager;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaPlayer;
import co.marcin.novaguilds.api.basic.NovaRegion;
import co.marcin.novaguilds.api.storage.ResourceManager;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Dependency;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.RegionValidity;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.event.PlayerEnterRegionEvent;
import co.marcin.novaguilds.event.PlayerExitRegionEvent;
import co.marcin.novaguilds.impl.storage.managers.database.AbstractDatabaseResourceManager;
import co.marcin.novaguilds.runnable.RunnableRaid;
import co.marcin.novaguilds.util.LoggerUtils;
import co.marcin.novaguilds.util.NumberUtils;
import co.marcin.novaguilds.util.RegionUtils;
import co.marcin.novaguilds.util.StringUtils;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.kitteh.vanish.VanishPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegionManager {
	private static final NovaGuilds plugin = NovaGuilds.getInstance();

	/**
	 * Gets the region at a location
	 *
	 * @param location the location
	 * @return region
	 */
	public static NovaRegion get(Location location) {
		int x = location.getBlockX();
		int z = location.getBlockZ();

		for(NovaRegion region : plugin.getRegionManager().getRegions()) {
			if(!location.getWorld().equals(region.getWorld())) {
				continue;
			}

			Location c1 = region.getCorner(0);
			Location c2 = region.getCorner(1);

			if((x >= c1.getBlockX() && x <= c2.getBlockX()) || (x <= c1.getBlockX() && x >= c2.getBlockX())) {
				if((z >= c1.getBlockZ() && z <= c2.getBlockZ()) || (z <= c1.getBlockZ() && z >= c2.getBlockZ())) {
					return region;
				}
			}
		}

		return null;
	}

	/**
	 * Gets the region a block lays in
	 *
	 * @param block the block
	 * @return region
	 */
	public static NovaRegion get(Block block) {
		return get(block.getLocation());
	}

	/**
	 * Gets the region an entity is at
	 *
	 * @param entity the entity
	 * @return region
	 */
	public static NovaRegion get(Entity entity) {
		return get(entity.getLocation());
	}

	/**
	 * Gets all regions
	 *
	 * @return collection of regions
	 */
	public Collection<NovaRegion> getRegions() {
		Collection<NovaRegion> regions = new HashSet<>();

		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			if(guild.hasRegion()) {
				regions.add(guild.getRegion());
			}
		}

		return regions;
	}

	/**
	 * Loads regions
	 */
	public void load() {
		for(NovaGuild guild : plugin.getGuildManager().getGuilds()) {
			guild.setRegion(null);
		}

		getResourceManager().load();

		LoggerUtils.info("Loaded " + getRegions().size() + " regions.");
	}

	/**
	 * Saves a specified region
	 *
	 * @param region the region
	 */
	public void save(NovaRegion region) {
		getResourceManager().save(region);
	}

	/**
	 * Saves all regions
	 */
	public void save() {
		long startTime = System.nanoTime();

		if(getResourceManager() instanceof AbstractDatabaseResourceManager) {
			AbstractDatabaseResourceManager<NovaRegion> databaseResourceManager = (AbstractDatabaseResourceManager<NovaRegion>) getResourceManager();
			int count = databaseResourceManager.executeUpdateUUID();
			LoggerUtils.info("Region UUIDs updated in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " regions)");
		}

		startTime = System.nanoTime();
		int count = getResourceManager().executeSave() + getResourceManager().save(getRegions());
		LoggerUtils.info("Regions data saved in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " regions)");

		startTime = System.nanoTime();
		count = getResourceManager().executeRemoval();
		LoggerUtils.info("Regions removed in " + TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0 + "s (" + count + " regions)");
	}

	/**
	 * Removes a region
	 *
	 * @param region the region
	 */
	public void remove(NovaRegion region) {
		getResourceManager().addToRemovalQueue(region);

		if(region.getGuild() != null) {
			region.getGuild().setRegion(null);
		}
	}

	/**
	 * Checks selection validity
	 *
	 * @param l1 first corner
	 * @param l2 second corner
	 * @return region validity
	 */
	public RegionValidity checkRegionSelect(Location l1, Location l2) {
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		int difX = Math.abs(x1 - x2) + 1;
		int difZ = Math.abs(z1 - z2) + 1;

		int minSize = Config.REGION_MINSIZE.getInt();
		int maxSize = Config.REGION_MAXSIZE.getInt();

		if(difX < minSize || difZ < minSize) {
			return RegionValidity.TOOSMALL;
		}
		else if(difX > maxSize || difZ > maxSize) {
			return RegionValidity.TOOBIG;
		}
		else if(!getRegionsInsideArea(l1, l2).isEmpty()) {
			return RegionValidity.OVERLAPS;
		}
		else if(!isFarEnough(l1, l2)) {
			return RegionValidity.TOOCLOSE;
		}
		else {
			return RegionValidity.VALID;
		}
	}

	/**
	 * Gets regions inside a rectangle
	 *
	 * @param l1 first corner
	 * @param l2 second corner
	 * @return list of regions
	 */
	public List<NovaRegion> getRegionsInsideArea(Location l1, Location l2) {
		final List<NovaRegion> list = new ArrayList<>();
		int x1 = l1.getBlockX();
		int x2 = l2.getBlockX();
		int z1 = l1.getBlockZ();
		int z2 = l2.getBlockZ();

		boolean i1;
		boolean i2;
		boolean i3;
		boolean i4;

		boolean ov1;
		boolean ov2;
		boolean overlaps;

		for(NovaRegion region : getRegions()) {
			Location c1 = region.getCorner(0);
			Location c2 = region.getCorner(1);

			//c1
			i1 = (c1.getBlockX() <= x1 && c1.getBlockX() >= x2) || (c1.getBlockX() >= x1 && c1.getBlockX() <= x2);
			i2 = (c1.getBlockZ() <= z1 && c1.getBlockZ() >= z2) || (c1.getBlockZ() >= z1 && c1.getBlockZ() <= z2);

			//c2
			i3 = (c2.getBlockX() <= x1 && c2.getBlockX() >= x2) || (c2.getBlockX() >= x1 && c2.getBlockX() <= x2);
			i4 = (c2.getBlockZ() <= z1 && c2.getBlockZ() >= z2) || (c2.getBlockZ() >= z1 && c2.getBlockZ() <= z2);

			ov1 = i1 && i2;
			ov2 = i3 && i4;

			overlaps = ov1 || ov2;

			if(overlaps) {
				list.add(region);
			}
		}

		return list;
	}

	/**
	 * Checks if a player can interact at a location
	 *
	 * @param player   player
	 * @param location location
	 * @return boolean
	 */
	public boolean canInteract(Player player, Location location) {
		NovaRegion region = get(location);
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		return region == null || nPlayer.getBypass() || (nPlayer.hasGuild() && region.getGuild().isMember(nPlayer));
	}

	/**
	 * Checks if a player can interact at a block
	 *
	 * @param player player
	 * @param block  block
	 * @return boolean
	 */
	public boolean canInteract(Player player, Block block) {
		return canInteract(player, block.getLocation());
	}

	/**
	 * Checks is a player can interact at an entity
	 *
	 * @param player player
	 * @param entity entity
	 * @return boolean
	 */
	public boolean canInteract(Player player, Entity entity) {
		return canInteract(player, entity.getLocation());
	}

	/**
	 * Checks if a rectangle is far enough from other regions
	 *
	 * @param l1 first corner
	 * @param l2 second corner
	 * @return boolean
	 */
	private boolean isFarEnough(Location l1, Location l2) {
		return getGuildsTooClose(l1, l2).isEmpty();
	}

	/**
	 * Gets guilds too close to a rectangle
	 * The distance is being taken from the config
	 *
	 * @param l1 first corner
	 * @param l2 second corner
	 * @return list of guilds
	 */
	public List<NovaGuild> getGuildsTooClose(Location l1, Location l2) {
		final List<NovaGuild> list = new ArrayList<>();

		int width = Math.abs(l1.getBlockX() - l2.getBlockX()) + 1;
		int height = Math.abs(l1.getBlockZ() - l2.getBlockZ()) + 1;
		int radius1 = Math.round((int) Math.sqrt((int) (Math.pow(width, 2) + Math.pow(height, 2))) / 2);

		int min = radius1 + Config.REGION_MINDISTANCE.getInt();
		Location centerLocation = RegionUtils.getCenterLocation(l1, l2);

		for(NovaGuild guildLoop : plugin.getGuildManager().getGuilds()) {
			if(!guildLoop.getHome().getWorld().equals(l1.getWorld())) {
				continue;
			}

			int radius2 = 0;

			if(guildLoop.hasRegion()) {
				radius2 = guildLoop.getRegion().getDiagonal() / 2;
			}

			centerLocation.setY(guildLoop.getHome().getY());

			double distance = centerLocation.distance(guildLoop.getHome());
			if(distance < min + radius2) {
				list.add(guildLoop);
			}
		}

		return list;
	}

	/**
	 * Gets executed when a player enters a region
	 *
	 * @param player the player
	 * @param region region he entered
	 */
	public void playerEnteredRegion(Player player, NovaRegion region) {
		PlayerEnterRegionEvent regionEvent = new PlayerEnterRegionEvent(player, region);
		plugin.getServer().getPluginManager().callEvent(regionEvent);

		if(regionEvent.isCancelled()) {
			return;
		}

		if(plugin.getDependencyManager().isEnabled(Dependency.VANISHNOPACKET) && plugin.getDependencyManager().get(Dependency.VANISHNOPACKET, VanishPlugin.class).getManager().isVanished(player)) {
			return;
		}

		if(region == null) {
			return;
		}

		NovaPlayer nPlayer = PlayerManager.getPlayer(player);

		//border particles
		if(Config.REGION_BORDERPARTICLES.getBoolean()) {
			List<Block> blocks = RegionUtils.getBorderBlocks(region);
			for(Block block : blocks) {
				block.getLocation().setY(block.getLocation().getY() + 1);
				block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 100);
			}
		}

		//Chat message
		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.GUILDNAME, region.getGuild().getName());
		vars.put(VarKey.PLAYERNAME, player.getName());
		Message.CHAT_REGION_ENTERED.vars(vars).send(player);

		//Player is at region
		nPlayer.setAtRegion(region);

		if(!region.getGuild().isMember(nPlayer)) {
			checkRaidInit(nPlayer);

			Message.CHAT_REGION_NOTIFYGUILD_ENTERED.vars(vars).broadcast(region.getGuild());
		}

		//Vehicle protection system
		Entity vehicle = player.getVehicle();
		if(vehicle != null && vehicle instanceof Vehicle) {
			nPlayer.addVehicle((Vehicle) vehicle);
		}
	}

	/**
	 * Gets executed when a player leaves a region
	 *
	 * @param player the player
	 */
	public void playerExitedRegion(Player player) {
		NovaPlayer nPlayer = PlayerManager.getPlayer(player);
		NovaRegion region = nPlayer.getAtRegion();

		PlayerExitRegionEvent regionEvent = new PlayerExitRegionEvent(player, region);
		plugin.getServer().getPluginManager().callEvent(regionEvent);

		if(regionEvent.isCancelled()) {
			return;
		}

		if(region == null) {
			return;
		}

		NovaGuild guild = region.getGuild();

		nPlayer.setAtRegion(null);
		Message.CHAT_REGION_EXITED.setVar(VarKey.GUILDNAME, region.getGuild().getName()).send(player);

		if(nPlayer.hasGuild() && nPlayer.getGuild().isWarWith(guild) && guild.isRaid()) {
			guild.getRaid().removePlayerOccupying(nPlayer);
		}
	}

	/**
	 * Initiates the raid if possible
	 *
	 * @param nPlayer the initiator
	 */
	public void checkRaidInit(NovaPlayer nPlayer) {
		if(!Config.RAID_ENABLED.getBoolean() || !nPlayer.hasGuild() || !nPlayer.isAtRegion()) {
			return;
		}

		if(nPlayer.getAtRegion().getGuild().getHome().distance(nPlayer.getPlayer().getLocation()) > nPlayer.getAtRegion().getDiagonal()) {
			LoggerUtils.debug(nPlayer.getAtRegion().getGuild().getHome().distance(nPlayer.getPlayer().getLocation()) + " > " + nPlayer.getAtRegion().getDiagonal());
			return;
		}

		NovaGuild guildDefender = nPlayer.getAtRegion().getGuild();

		if(nPlayer.getGuild().isWarWith(guildDefender)) {
			if(guildDefender.isRaid()) {
				nPlayer.setPartRaid(guildDefender.getRaid());
				guildDefender.getRaid().addPlayerOccupying(nPlayer);
			}
			else {
				if(NumberUtils.systemSeconds() - Config.RAID_TIMEREST.getSeconds() > guildDefender.getTimeRest()) {
					if(guildDefender.getOnlinePlayers().size() >= Config.RAID_MINONLINE.getInt() || guildDefender.getOnlinePlayers().size() == guildDefender.getPlayers().size()) {
						if(NumberUtils.systemSeconds() - guildDefender.getTimeCreated() > Config.GUILD_CREATEPROTECTION.getSeconds()) {
							guildDefender.createRaid(nPlayer.getGuild());
							guildDefender.getRaid().addPlayerOccupying(nPlayer);

							if(!RunnableRaid.isRaidRunnableRunning()) {
								Runnable task = new RunnableRaid();
								NovaGuilds.runTaskLater(task, 1, TimeUnit.SECONDS);
								RunnableRaid.setRaidRunnableRunning(true);
							}
						}
						else {
							Message.CHAT_RAID_PROTECTION.send(nPlayer);
						}
					}
				}
				else {
					final long timeWait = Config.RAID_TIMEREST.getSeconds() - (NumberUtils.systemSeconds() - guildDefender.getTimeRest());

					Message.CHAT_RAID_RESTING.setVar(VarKey.TIMEREST, StringUtils.secondsToString(timeWait)).send(nPlayer);
				}
			}
		}
	}

	/**
	 * Gets the resource manager
	 *
	 * @return resource manager
	 */
	public ResourceManager<NovaRegion> getResourceManager() {
		return plugin.getStorage().getResourceManager(NovaRegion.class);
	}
}
