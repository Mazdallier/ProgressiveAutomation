package com.vanhal.progressiveautomation.items;

import java.util.List;

import com.vanhal.progressiveautomation.ProgressiveAutomation;
import com.vanhal.progressiveautomation.entities.BaseTileEntity;
import com.vanhal.progressiveautomation.entities.UpgradeableTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemBlockMachine extends ItemBlock {

	public ItemBlockMachine(Block baseBlock) {
		super(baseBlock);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par) {
		if ( (itemStack != null) && (itemStack.stackTagCompound != null) ) {
			list.add(EnumChatFormatting.GRAY + "Pre-Configured");
		}
		
    }
	
	public boolean placeBlockAt(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {	
		boolean result = super.placeBlockAt(itemStack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		if (result) {
			if (!world.isRemote) {
				if (world.getTileEntity(x, y, z) instanceof BaseTileEntity) {
					BaseTileEntity tileEntity = (BaseTileEntity) world.getTileEntity(x, y, z);
					tileEntity.readFromItemStack(itemStack);
				}
			}
		}
		return result;
    }

}
