package com.vanhal.progressiveautomation.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.vanhal.progressiveautomation.ProgressiveAutomation;
import com.vanhal.progressiveautomation.entities.generator.TileGenerator;
import com.vanhal.progressiveautomation.entities.generator.TileGeneratorDiamond;
import com.vanhal.progressiveautomation.entities.generator.TileGeneratorIron;
import com.vanhal.progressiveautomation.entities.generator.TileGeneratorStone;
import com.vanhal.progressiveautomation.items.ItemBlockMachine;
import com.vanhal.progressiveautomation.items.PAItems;
import com.vanhal.progressiveautomation.ref.Ref;
import com.vanhal.progressiveautomation.ref.ToolHelper;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGenerator extends BaseBlock {
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public BlockGenerator(int level) {
		super("Generator", level);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(ACTIVE, false));
	}
	
	public TileEntity createNewTileEntity(World world, int var2) {
		if (blockLevel >= ToolHelper.LEVEL_DIAMOND) return new TileGeneratorDiamond();
		else if (blockLevel == ToolHelper.LEVEL_IRON) return new TileGeneratorIron();
		else if (blockLevel == ToolHelper.LEVEL_STONE) return new TileGeneratorStone();
		else return new TileGenerator();
	}
	
	public static final Block firstTier = Blocks.redstone_block;
	
	public void addRecipe(Block previousTier) {
		ShapedOreRecipe recipe = null;
		
		if (blockLevel == ToolHelper.LEVEL_STONE) {
			recipe = new ShapedOreRecipe(new ItemStack(this), new Object[]{
				"ses", "scs", "sss", 's', Blocks.stone, 'c', previousTier, 'e', PAItems.rfEngine});
		} else if (blockLevel == ToolHelper.LEVEL_IRON) {
			recipe = new ShapedOreRecipe(new ItemStack(this), new Object[]{
				"ses", "scs", "sbs", 's', Items.iron_ingot, 'c', previousTier, 'b', Blocks.iron_block, 'e', PAItems.rfEngine});
		} else if (blockLevel >= ToolHelper.LEVEL_DIAMOND) {
			recipe = new ShapedOreRecipe(new ItemStack(this), new Object[]{
				"ses", "scs", "sss", 's', Items.diamond, 'c', previousTier, 'e', PAItems.rfEngine});
		} else {
			recipe = new ShapedOreRecipe(new ItemStack(this), new Object[]{
				"sps", "ses", "srs", 's', "logWood", 'r', Blocks.furnace, 'p', previousTier, 'e', PAItems.rfEngine});
		}
		
		
		GameRegistry.addRecipe(recipe);
	}
	
	@Override
    public IBlockState getStateFromMeta(int meta) {
    	EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] {FACING, ACTIVE});
    }
    
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.func_174811_aO());
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.func_174811_aO()), 2);
    }
    
    
    @Override
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }
    
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
            world.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    
    /*@Override
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis){
    	int metadata = worldObj.getBlockMetadata(x, y, z) + 1;
    	if (metadata>5) metadata = 2;
    	worldObj.setBlockMetadataWithNotify(x, y, z, metadata, 2);
        return true;
    }*/
}
