package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.sleep;

public class ItemDurability implements InvActionListener, InvActionExecutiveListener {
	@Override
	public boolean blockInvAction(Item item, Player player, String command) {
		return (item.getCatalogId() == ItemId.RING_OF_RECOIL.id() || item.getCatalogId() == ItemId.RING_OF_FORGING.id()
			|| item.getCatalogId() == ItemId.DWARVEN_RING.id())
			&& (command.equalsIgnoreCase("check") || command.equalsIgnoreCase("break"));
	}

	@Override
	public void onInvAction(Item item, Player player, String command) {
		if (command.equalsIgnoreCase("check")) {
			int charges;
			if (item.getCatalogId() == ItemId.RING_OF_RECOIL.id()) {
				if (player.getCache().hasKey("ringofrecoil"))
					charges = player.getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - player.getCache().getInt("ringofrecoil");
				else
					charges = player.getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT;
				player.message("Your Ring of Recoil has " + charges + "/" +
					player.getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.RING_OF_FORGING.id()) {
				if (player.getCache().hasKey("ringofforging"))
					charges = player.getWorld().getServer().getConfig().RING_OF_FORGING_USES - player.getCache().getInt("ringofforging");
				else
					charges = player.getWorld().getServer().getConfig().RING_OF_FORGING_USES;
				player.message("Your Ring of Forging has " + charges + "/" +
					player.getWorld().getServer().getConfig().RING_OF_FORGING_USES + " charges remaining.");
			} else if (item.getCatalogId() == ItemId.DWARVEN_RING.id()) {
				if (player.getCache().hasKey("dwarvenring"))
					charges = player.getWorld().getServer().getConfig().DWARVEN_RING_USES - player.getCache().getInt("dwarvenring");
				else
					charges = player.getWorld().getServer().getConfig().DWARVEN_RING_USES;
				player.message("Your Dwarven Ring has " + charges + "/" +
					player.getWorld().getServer().getConfig().DWARVEN_RING_USES + " charges remaining.");
			}
		} else if (command.equalsIgnoreCase("break")) {
			player.message("Are you sure you want to break your " + item.getDef(player.getWorld()).getName() + "?");
			sleep(300);
			int choice = Functions.showMenu(player, "Yes", "No");
			if (choice != 0)
				return;
			if (item.getCatalogId() == ItemId.RING_OF_RECOIL.id()) {
				player.getCache().remove("ringofrecoil");
				player.getInventory().shatter(item.getCatalogId());
			} else if (item.getCatalogId() == ItemId.RING_OF_FORGING.id()) {
				player.getCache().remove("ringofforging");
				player.getInventory().shatter(item.getCatalogId());
			} else if (item.getCatalogId() == ItemId.DWARVEN_RING.id()) {
				player.getCache().remove("dwarvenring");
				player.getInventory().shatter(item.getCatalogId());
			}
		}
	}
}
