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

package co.marcin.novaguilds.api.basic;

import co.marcin.novaguilds.api.util.Changeable;
import co.marcin.novaguilds.basic.NovaRank;
import co.marcin.novaguilds.basic.NovaRegion;
import co.marcin.novaguilds.enums.ChatMode;
import co.marcin.novaguilds.enums.Command;
import co.marcin.novaguilds.enums.GuildPermission;
import co.marcin.novaguilds.enums.RegionMode;
import co.marcin.novaguilds.runnable.CommandExecutorHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

import java.util.List;
import java.util.UUID;

public interface NovaPlayer extends Changeable {
	/**
	 * Gets the Player instance (if player is online)
	 *
	 * @return the Player instance
	 */
	Player getPlayer();

	/**
	 * Gets player's guild
	 *
	 * @return the guild
	 */
	NovaGuild getGuild();

	/**
	 * Gets player's name
	 *
	 * @return the name string
	 */
	String getName();

	/**
	 * Gets a list of guilds that the player is invited to
	 *
	 * @return the list
	 */
	List<NovaGuild> getInvitedTo();

	/**
	 * Gets player's unique ID
	 *
	 * @return the UUID
	 */
	UUID getUUID();

	/**
	 * Gets selected location
	 *
	 * @param index 0 or 1
	 * @return the location
	 */
	Location getSelectedLocation(int index);

	/**
	 * Gets selected region
	 *
	 * @return the region
	 */
	NovaRegion getSelectedRegion();

	/**
	 * Gets player's region bypass
	 *
	 * @return the bypass flag
	 */
	boolean getBypass();

	/**
	 * Gets a region that the player is at
	 *
	 * @return the region
	 */
	NovaRegion getAtRegion();

	/**
	 * Gets the corner that the player is resizing
	 *
	 * @return 0 or 1
	 */
	int getResizingCorner();

	/**
	 * Gets the points
	 *
	 * @return the amount
	 */
	int getPoints();

	/**
	 * Gets the amount of deaths
	 *
	 * @return the amount
	 */
	int getDeaths();

	/**
	 * Gets the amount of kills
	 *
	 * @return the amount
	 */
	int getKills();

	/**
	 * Gets the KDR
	 *
	 * @return the rate
	 */
	double getKillDeathRate();

	/**
	 * Gets the amount of money
	 *
	 * @return the amount
	 */
	double getMoney();

	/**
	 * Gets region mode
	 *
	 * @return RegionMode enum
	 */
	RegionMode getRegionMode();

	/**
	 * Gets the tablist
	 *
	 * @return the tablist
	 */
	TabList getTabList();

	/**
	 * Gets command executor handler
	 *
	 * @return the handler
	 */
	CommandExecutorHandler getCommandExecutorHandler();

	/**
	 * Gets a raid player is taking part in
	 *
	 * @return the raid
	 */
	NovaRaid getPartRaid();

	/**
	 * Gets gui inventory
	 *
	 * @return the inventory
	 */
	GUIInventory getGuiInventory();

	/**
	 * Gets list of open gui inventories
	 *
	 * @return the list
	 */
	List<GUIInventory> getGuiInventoryHistory();

	/**
	 * Gets player's rank
	 *
	 * @return the rank
	 */
	NovaRank getGuildRank();

	/**
	 * Gets player's chat mode
	 *
	 * @return the chat mode enum
	 */
	ChatMode getChatMode();

	/**
	 * Gets player's spy mode
	 *
	 * @return spy mode flag
	 */
	boolean getSpyMode();

	/**
	 * Gets player's ID
	 *
	 * @return the ID
	 */
	int getId();

	/**
	 * Sets the guild
	 *
	 * @param guild the guild
	 */
	void setGuild(NovaGuild guild);

	/**
	 * Sets the player
	 *
	 * @param player the player
	 */
	void setPlayer(Player player);

	/**
	 * Sets the name
	 *
	 * @param name the name
	 */
	void setName(String name);

	/**
	 * Sets the list of guilds player is invited to
	 *
	 * @param invitedTo the list
	 */
	void setInvitedTo(List<NovaGuild> invitedTo);

	/**
	 * Sets the region mode flag
	 *
	 * @param regionMode RegionMode enum
	 */
	void setRegionMode(RegionMode regionMode);

	/**
	 * Sets the selected location
	 *
	 * @param index 0 or 1
	 * @param location the location
	 */
	void setSelectedLocation(int index, Location location);

	/**
	 * Sets the selected region
	 *
	 * @param region the region
	 */
	void setSelectedRegion(NovaRegion region);

	/**
	 * Sets the region player is at
	 *
	 * @param region the region
	 */
	void setAtRegion(NovaRegion region);

	/**
	 * Sets the resizing corner
	 *
	 * @param index 0 or 1
	 */
	void setResizingCorner(int index);

	/**
	 * Sets the points
	 *
	 * @param points the amount
	 */
	void setPoints(int points);

	/**
	 * Sets whether the compass should point the guild's home
	 *
	 * @param compassPointingGuild the flag
	 */
	void setCompassPointingGuild(boolean compassPointingGuild);

	/**
	 * Sets the deaths
	 *
	 * @param deaths the amount
	 */
	void setDeaths(int deaths);

	/**
	 * Sets the kills
	 *
	 * @param kills the amount
	 */
	void setKills(int kills);

	/**
	 * Sets the tablist
	 *
	 * @param tabList the tablist
	 */
	void setTabList(TabList tabList);

	/**
	 * Toggles bypass
	 */
	void toggleBypass();

	/**
	 * Sets the raid the player is taking part in
	 *
	 * @param partRaid the raid
	 */
	void setPartRaid(NovaRaid partRaid);

	/**
	 * Sets the gui inventory currently open
	 *
	 * @param guiInventory the gui
	 */
	void setGuiInventory(GUIInventory guiInventory);

	/**
	 * Sets the rank
	 *
	 * @param guildRank the rank
	 */
	void setGuildRank(NovaRank guildRank);

	/**
	 * Sets the chat mode
	 *
	 * @param chatMode the ChatMode enum
	 */
	void setChatMode(ChatMode chatMode);

	/**
	 * Sets the spy mode
	 *
	 * @param spyMode the flag
	 */
	void setSpyMode(boolean spyMode);

	/**
	 * Sets the ID
	 *
	 * @param id the ID
	 */
	void setId(int id);

	/**
	 * Checks if the compass is pointing guild
	 *
	 * @return the flag
	 */
	boolean isCompassPointingGuild();

	/**
	 * Checks if the player has a guild
	 *
	 * @return the flag
	 */
	boolean hasGuild();

	/**
	 * Checks if the player is online
	 *
	 * @return the flag
	 */
	boolean isOnline();

	/**
	 * Checks if the player is invited to a guild
	 *
	 * @param guild the guild
	 * @return the flag
	 */
	boolean isInvitedTo(NovaGuild guild);

	/**
	 * Checks if the player is taking part in a raid
	 *
	 * @return the flag
	 */
	boolean isPartRaid();

	/**
	 * Checks if a vehicle belongs to the player
	 *
	 * @param vehicle the vehicle
	 * @return the flag
	 */
	boolean isVehicleListed(Vehicle vehicle);

	/**
	 * Checks if the player is the leader
	 *
	 * @return the flag
	 */
	boolean isLeader();

	/**
	 * Returns if the player is at a region
	 *
	 * @return the flag
	 */
	boolean isAtRegion();

	/**
	 * Checks if the player has enough money
	 *
	 * @param money the amount
	 * @return the flag
	 */
	boolean hasMoney(double money);

	/**
	 * Checks if the player has guild permission
	 *
	 * @param permission the GuildPermission enum
	 * @return the flag
	 */
	boolean hasPermission(GuildPermission permission);

	/**
	 * Checks if the player has a tablist
	 *
	 * @return the flag
	 */
	boolean hasTabList();

	/**
	 * Checks if the player can get kill points
	 *
	 * @param player the player
	 * @return the flag
	 */
	boolean canGetKillPoints(Player player);

	/**
	 * Adds a guild invitation
	 *
	 * @param guild the guild
	 */
	void addInvitation(NovaGuild guild);

	/**
	 * Adds points
	 *
	 * @param points the amount
	 */
	void addPoints(int points);

	/**
	 * Adds a kill
	 */
	void addKill();

	/**
	 * Adds a death
	 */
	void addDeath();

	/**
	 * Adds money
	 *
	 * @param money the amount
	 */
	void addMoney(double money);

	/**
	 * Adds adds a player to the kill history
	 *
	 * @param player the player
	 */
	void addKillHistory(Player player);

	/**
	 * Adds a vehicle to the owned list
	 *
	 * @param vehicle the Vehicle
	 */
	void addVehicle(Vehicle vehicle);

	/**
	 * Creates a new command executor handler
	 *
	 * @param command the command
	 * @param args array of arguments
	 */
	void newCommandExecutorHandler(Command command, String[] args);

	/**
	 * Deletes an invitation to a guild
	 *
	 * @param guild the guild
	 */
	void deleteInvitation(NovaGuild guild);

	/**
	 * Takes points
	 *
	 * @param points the amount
	 */
	void takePoints(int points);

	/**
	 * Takes money
	 *
	 * @param money the amount
	 */
	void takeMoney(double money);

	/**
	 * Cancels tool progress
	 */
	void cancelToolProgress();

	/**
	 * Deletes the command executor handler
	 */
	void removeCommandExecutorHandler();

	/**
	 * Removes last gui inventory from the history
	 */
	void removeLastGUIInventoryHistory();
}
