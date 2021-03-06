package com.vanhal.progressiveautomation.gui;

import java.lang.reflect.Constructor;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import com.vanhal.progressiveautomation.ProgressiveAutomation;
import com.vanhal.progressiveautomation.entities.chopper.TileChopper;
import com.vanhal.progressiveautomation.entities.generator.TileGenerator;
import com.vanhal.progressiveautomation.entities.miner.TileMiner;
import com.vanhal.progressiveautomation.entities.planter.TilePlanter;
import com.vanhal.progressiveautomation.gui.client.GUIChopper;
import com.vanhal.progressiveautomation.gui.client.GUIGenerator;
import com.vanhal.progressiveautomation.gui.client.GUIMiner;
import com.vanhal.progressiveautomation.gui.client.GUIPlanter;
import com.vanhal.progressiveautomation.gui.container.ContainerChopper;
import com.vanhal.progressiveautomation.gui.container.ContainerGenerator;
import com.vanhal.progressiveautomation.gui.container.ContainerMiner;
import com.vanhal.progressiveautomation.gui.container.ContainerPlanter;

import net.minecraft.inventory.Container;
import net.minecraft.network.Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/*
 * Contains some code from cofh Core
 */

public class SimpleGuiHandler implements IGuiHandler {
	private int guiIdCounter = 0;
	
	private final TMap containerMap = new THashMap();
	private final TMap guiMap = new THashMap();
	
	public int registerGui(Class gui, Class container) {
		guiIdCounter++;
		guiMap.put(guiIdCounter, gui);
		containerMap.put(guiIdCounter, container);
		return guiIdCounter;
	}
	

	public int registerServerGui(Class container) {
		guiIdCounter++;
		containerMap.put(guiIdCounter, container);
		return guiIdCounter;
	}

	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (containerMap.containsKey(ID)) {
			if (!world.blockExists(x, y, z)) {
				return null;
			}
			TileEntity tile = world.getTileEntity(x, y, z);
			try {
				if (!world.isRemote) {
					Packet packet = tile.getDescriptionPacket();
					if (packet != null) {
						((EntityPlayerMP)player).playerNetServerHandler.sendPacket(packet);
					}
					
				}
				Class<? extends Container> containerClass = (Class<? extends Container>) containerMap.get(ID);
				Constructor containerConstructor = containerClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class, TileEntity.class });
				return containerConstructor.newInstance(player.inventory, tile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (guiMap.containsKey(ID)) {
			if (!world.blockExists(x, y, z)) {
				return null;
			}
			TileEntity tile = world.getTileEntity(x, y, z);
			try {
				Class<? extends GuiScreen> guiClass = (Class<? extends GuiScreen>) guiMap.get(ID);
				Constructor guiConstructor = guiClass.getDeclaredConstructor(new Class[] { InventoryPlayer.class, TileEntity.class });
				return guiConstructor.newInstance(player.inventory, tile);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		return null;
	}


}
