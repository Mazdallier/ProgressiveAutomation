package com.vanhal.progressiveautomation.compat.mods;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import com.vanhal.progressiveautomation.PAConfig;
import com.vanhal.progressiveautomation.ProgressiveAutomation;
import com.vanhal.progressiveautomation.compat.BaseMod;
import com.vanhal.progressiveautomation.util.Point3I;

import cpw.mods.fml.common.Loader;

/*
 * Seed Mapping:
 * 0: Squid - Water
 * 1: Fire - Netherrack
 * 2: Creeper - Dirt, Grass, Farmland
 * 3: Slime - Dirt, Grass, Farmland
 * 4: Rain - Dirt, Grass, Farmland
 * 5: Ender - Endstone
 * 6: Lightning - Dirt, Grass, Farmland
 * 7: 
 * 8: Burst - Dirt, Grass, Farmland
 * 9: Potion - Dirt, Grass, Farmland
 * 10: Repulsion - Dirt, Grass, Farmland
 * 11: Helium - Netherrack (Upside down)
 * 12: Chopper - Dirt, Grass, Farmland
 * 13: 
 * 14: Propulsion - Dirt, Grass, Farmland
 * 15: Flying - Dirt, Grass, Farmland
 *
 */

public class Pneumaticcraft extends BaseMod {
	
	public Pneumaticcraft() {
		modID = "PneumaticCraft";
	}
	
	@Override
	public boolean isPlantible(ItemStack item) {
		return (item.getUnlocalizedName().startsWith("item.plasticPlant"));
	}
	
	@Override
	public boolean isPlant(Block plantBlock, int metadata) {
		return (plantBlock.getClass().getName().startsWith("pneumaticCraft.common.block.pneumaticPlants"));
	}
	
	@Override
	public boolean isGrown(Point3I plantPoint, Block plantBlock, int metadata, World worldObj) {
		return (plantBlock.getDrops(worldObj, plantPoint.getX(), plantPoint.getY(), plantPoint.getZ(), metadata, 0).size()>=2);
	}
	
	@Override
	public boolean validBlock(World worldObj, ItemStack itemStack, Point3I testPoint) {
		Point3I point = new Point3I(testPoint);
		
		int seedMeta = itemStack.getItemDamage();
		//check the planting block is air first
		if (!worldObj.isAirBlock(point.getX(), point.getY(), point.getZ())) {
			return false;
		}
		
		//select which block we want to check.
		if (seedMeta == 11) {
			point.setY(point.getY()+1);
		} else {
			point.setY(point.getY()-1);
		}
		
		//grab the block
		Block testBlock = worldObj.getBlock(point.getX(), point.getY(), point.getZ());
		
		//now test it
		if (seedMeta == 0) {
			if (testBlock == Blocks.water) return true;
		} else if (seedMeta == 5) {
			if (testBlock == Blocks.end_stone) return true;
		} else if ( (seedMeta == 1) || (seedMeta == 11) ) {
			if (testBlock == Blocks.netherrack) return true;
		} else {
			if ( (testBlock == Blocks.dirt) || (testBlock == Blocks.grass) || (testBlock == Blocks.farmland) ) return true;
		}
		return false;
	}
	
	@Override
	public boolean placeSeed(World worldObj, ItemStack itemStack, Point3I point, boolean doAction) {
		if (checkClear(worldObj, point)) {
			if (doAction) {
				int seedMeta = itemStack.getItemDamage();
				ItemStack items = new ItemStack(itemStack.getItem(), 1, seedMeta);
				EntityItem entItem = new EntityItem(worldObj, (float)point.getX() + 0.5f, (float)point.getY() + 0.5f, (float)point.getZ() + 0.5f, items);
				entItem.motionX = 0.0f;
				entItem.motionY = 0.0f;
				entItem.motionZ = 0.0f;
				entItem.delayBeforeCanPickup = 20;
				entItem.hoverStart = 0.0f;
				entItem.yOffset = 0.0f;
				if (items.hasTagCompound()) {
					entItem.getEntityItem().setTagCompound((NBTTagCompound)items.getTagCompound().copy());
		        }
				
				
				worldObj.spawnEntityInWorld(entItem);
			}
			return true;
		}
		return false;
	}
	
	private static boolean checkClear(World world, Point3I point) {
		AxisAlignedBB block = AxisAlignedBB.getBoundingBox(point.getX(), point.getY() - 1, point.getZ(), point.getX()+1, point.getY()+1, point.getZ()+1);
		
		List entities = world.getEntitiesWithinAABB(EntityItem.class, block);
		if (entities.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
