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

package co.marcin.novaguilds.impl.util.guiinventory.guild.rank;

import co.marcin.novaguilds.api.basic.GUIInventory;
import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.basic.NovaRank;
import co.marcin.novaguilds.api.util.SignGUI;
import co.marcin.novaguilds.enums.Config;
import co.marcin.novaguilds.enums.Message;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.impl.basic.NovaRankImpl;
import co.marcin.novaguilds.impl.util.AbstractGUIInventory;
import co.marcin.novaguilds.impl.util.guiinventory.GUIInventoryGuildPermissionSelect;
import co.marcin.novaguilds.impl.util.signgui.SignGUIPatternImpl;
import co.marcin.novaguilds.manager.RankManager;
import co.marcin.novaguilds.util.ChestGUIUtils;
import co.marcin.novaguilds.util.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryGuildRankSettings extends AbstractGUIInventory {
	private final NovaRank rank;
	private final NovaGuild guild;

	private ItemStack editPermissionsItem;
	private ItemStack setDefaultItem;
	private ItemStack cloneItem;
	private ItemStack renameItem;
	private ItemStack deleteItem;
	private ItemStack memberListItem;

	/**
	 * The constructor
	 *
	 * @param rank the rank
	 */
	public GUIInventoryGuildRankSettings(NovaRank rank) {
		super(9, Message.INVENTORY_GUI_RANK_SETTINGS_TITLE.setVar(VarKey.RANKNAME, rank.getName()));
		this.rank = rank;
		this.guild = rank.getGuild();
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		ItemStack clickedItemStack = event.getCurrentItem();

		if(clickedItemStack.equals(editPermissionsItem)) {
			new GUIInventoryGuildPermissionSelect(rank).open(getViewer());
		}
		else if(clickedItemStack.equals(setDefaultItem)) {
			NovaRank clonedRank = rank;
			if(rank.isGeneric() && !RankManager.getDefaultRank().equals(rank)) {
				clonedRank = cloneRank();
			}

			if(!getGuild().getDefaultRank().isGeneric()) {
				getGuild().getDefaultRank().setDefault(false);
			}

			clonedRank.setDefault(true);
			generateContent();
		}
		else if(clickedItemStack.equals(cloneItem)) {
			cloneRank();
		}
		else if(clickedItemStack.equals(renameItem)) {
			if(Config.SIGNGUI_ENABLED.getBoolean()) {
				final SignGUIPatternImpl pattern = new SignGUIPatternImpl(Message.SIGNGUI_GUILD_RANKS_SET_NAME.setVar(VarKey.INPUT, rank.getName()));
				plugin.getSignGUI().open(getViewer().getPlayer(), pattern, new SignGUI.SignGUIListener() {
					@Override
					public void onSignDone(Player player, String[] lines) {
						rank.setName(lines[pattern.getInputLine()]);
						close();
						GUIInventoryGuildRankSettings gui = new GUIInventoryGuildRankSettings(rank);
						gui.open(getViewer());
					}
				});
			}
		}
		else if(clickedItemStack.equals(deleteItem)) {
			rank.delete();
			close();
		}
		else if(clickedItemStack.equals(memberListItem)) {
			new GUIInventoryGuildRankMembers(getGuild(), rank).open(getViewer());
		}
	}

	@Override
	public void generateContent() {
		inventory.clear();

		editPermissionsItem = Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_EDITPERMISSIONS.getItemStack();
		setDefaultItem = Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_SETDEFAULT.getItemStack();
		cloneItem = Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_CLONE.getItemStack();
		renameItem = Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_RENAME.getItemStack();
		deleteItem = Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_DELETE.getItemStack();
		memberListItem = Message.INVENTORY_GUI_RANK_SETTINGS_ITEM_MEMBERLIST.getItemStack();

		if(!rank.isGeneric()) {
			add(editPermissionsItem);
		}

		if(!rank.equals(getGuild().getDefaultRank()) && !RankManager.getLeaderRank().equals(rank)) {
			add(setDefaultItem);
		}

		if(!RankManager.getLeaderRank().equals(rank)) {
			add(cloneItem);
		}

		if(!rank.isGeneric()) {
			add(renameItem);
		}

		if(!rank.isGeneric()) {
			add(deleteItem);
		}

		if(!GUIInventoryGuildRankMembers.getMembers(getGuild(), rank).isEmpty()) {
			add(memberListItem);
		}

		ChestGUIUtils.addBackItem(this);
	}

	/**
	 * Gets the guild
	 *
	 * @return the guild
	 */
	public NovaGuild getGuild() {
		return guild;
	}

	/**
	 * Clones a rank
	 *
	 * @return the rank
	 */
	private NovaRank cloneRank() {
		String clonePrefix = Message.INVENTORY_GUI_RANK_SETTINGS_CLONEPREFIX.get();
		String cloneName = rank.getName().startsWith(clonePrefix) || rank.isGeneric() ? rank.getName() : clonePrefix + rank.getName();

		if(StringUtils.contains(cloneName, ' ')) {
			String[] split = StringUtils.split(cloneName, ' ');

			if(NumberUtils.isNumeric(split[split.length - 1])) {
				cloneName = cloneName.substring(0, cloneName.length() - split[split.length - 1].length() - 1);
			}
		}

		NovaRank clone = new NovaRankImpl(cloneName);
		clone.setClone(rank.isGeneric());
		NovaGuild guild;

		if(rank.isGeneric()) {
			GUIInventory previousGui = getViewer().getGuiInventoryHistory().get(getViewer().getGuiInventoryHistory().size() - 2);
			guild = ((GUIInventoryGuildRankList) previousGui).getGuild();
		}
		else {
			guild = rank.getGuild();
		}

		boolean doubleName;
		int i = 1;
		do {
			if(i > 999) {
				break;
			}

			doubleName = false;
			for(NovaRank loopRank : guild.getRanks()) {
				if(!loopRank.isGeneric() && loopRank.getName().equalsIgnoreCase(clone.getName())) {
					doubleName = true;
				}
			}

			if(doubleName) {
				clone.setName(cloneName + " " + i);
			}

			i++;
		} while(doubleName);

		clone.setPermissions(rank.getPermissions());
		guild.addRank(clone);
		return clone;
	}
}
