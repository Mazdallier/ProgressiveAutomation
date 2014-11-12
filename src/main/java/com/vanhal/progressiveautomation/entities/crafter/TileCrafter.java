package com.vanhal.progressiveautomation.entities.crafter;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import com.vanhal.progressiveautomation.ProgressiveAutomation;
import com.vanhal.progressiveautomation.blocks.BlockMiner;
import com.vanhal.progressiveautomation.entities.UpgradeableTileEntity;
import com.vanhal.progressiveautomation.ref.ToolHelper;
import com.vanhal.progressiveautomation.util.OreHelper;

public class TileCrafter extends UpgradeableTileEntity {
	
	public int CRAFT_GRID_START = 1;
	public int CRAFT_GRID_END = 9;
	public int CRAFT_RESULT = 10;
	public int OUTPUT_SLOT = 11;
	
	public int craftTime = 100;
	public int currentTime = 0;

	public TileCrafter() {
		super(20);
		setUpgradeLevel(ToolHelper.LEVEL_WOOD);
		setCraftTime(120);
	}
	
	public void setCraftTime(int time) {
		this.craftTime = time;
	}
	
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (isBurning()) {
				if (readyToBurn()) {
					if ( (currentTime > 0) && (currentTime<=craftTime) ) {
						currentTime++;
					} else if (currentTime>craftTime) {
						currentTime = 0;
						if (consumeIngredients()) {
							//create an item, put it in the right slot
							if (slots[OUTPUT_SLOT]!=null) {
								if (canCraft()) {
									slots[OUTPUT_SLOT].stackSize += slots[CRAFT_RESULT].stackSize;
								}
							} else {
								slots[OUTPUT_SLOT] = slots[CRAFT_RESULT].copy();
							}
						}
					} else {
						currentTime = 1;
					}
				} else {
					currentTime = 0;
				}
			}
		}
	}
	
	public boolean readyToBurn() {
		if ( (validRecipe()) && (canCraft()) ) {
			if (hasIngredients()) {
				return true;
			}
		}
		return false;
	}
	
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		//save the current chopping time
		nbt.setInteger("currentTime", currentTime);
	}

	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		//load the current chopping time
		currentTime = nbt.getInteger("currentTime");
	}
	
	//test to see if the output slot can accept the resulting craft
	public boolean canCraft() {
		return ( (slots[OUTPUT_SLOT]==null) || 
			( (slots[OUTPUT_SLOT].isItemEqual(slots[CRAFT_RESULT])) && 
			((slots[OUTPUT_SLOT].stackSize + slots[CRAFT_RESULT].stackSize) <= slots[OUTPUT_SLOT].getMaxStackSize()) ) 
		);
	}
	
	public boolean validRecipe() {
		return (slots[CRAFT_RESULT]!=null);
	}
	
	public boolean hasIngredients() {
		return checkIngredients(false);
	}
	
	public boolean consumeIngredients() {
		if (checkIngredients(false)) {
			return checkIngredients(true);
		} else {
			return false;
		}
	}
	
	//need to deal with ore dic as well :(
	protected boolean checkIngredients(boolean consume) {
		List<ItemStack> required = new ArrayList<ItemStack>();
		//get the list of things we need
		for (int i = CRAFT_GRID_START; i <= CRAFT_GRID_END; i++) {
			if (this.slots[i] != null) {
				required.add(slots[i].copy());
			}
		}
		if (required.size()==0) return false;
		
		//go through the inventory and see if anything matches up with the requirements
		for (int i = SLOT_INVENTORY_START; i <= SLOT_INVENTORY_END; i++) {
			if (slots[i]!=null) {
				int amtItems = slots[i].stackSize;
				for (int j = 0; j < required.size(); j++) {
					if (required.get(j)!=null) {
						if ( OreHelper.ItemOreMatch(required.get(j), slots[i]) ) {
							if (amtItems>0) {
								amtItems--;
								required.set(j, null);
							}
						}
					}
				}
				//actually consume the item
				if (consume) {
					if (amtItems<=0) {
						slots[i] = null;
					} else {
						slots[i].stackSize = amtItems;
					}
				}
			}
		}
		
		//check to see if it's all good
		for (int j = 0; j < required.size(); j++) {
			if (required.get(j)!=null) {
				required = null;
				return false;
			}
		}
		required = null;
		return true;
	}
	
	public float getPercentCrafted() {
		if ( currentTime != 0 ) {
			float done = (float)(craftTime - currentTime);
			done = done/(float)craftTime;
			done = 1 - done;
			return done;
		} else {
			return 0;
		}
	}
	
	public int getScaledCrafted(int scale) {
		return (int) Math.floor(scale * getPercentCrafted());
	}
	

	/* ISided Stuff */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if ( (slot >= SLOT_INVENTORY_START) && (slot <= SLOT_INVENTORY_END) ) {
    		return true;
    	}
		return super.isItemValidForSlot(slot, stack);
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		if (slot==OUTPUT_SLOT) {
			return true;
		} else if ( (slot>=SLOT_INVENTORY_START) && (slot<=SLOT_INVENTORY_END) && (side!=0) ) {
			return true;
		}
		return false;
	}

}
