package Orebfuscator;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.config.Configuration;

public class Options 
{
	public static class WorldOptions
	{
		public World worldObj;
		public String name;
		public WorldOptions(World world)
		{
			this.worldObj = world;
			this.name = world.getProviderName();
		}
		
		public int maxObfuscateHeight = 128;
		
		private boolean[] isRandomBlock = new boolean[4096];
		public boolean isRandomBlock(int blockID)
		{
			return this.isRandomBlock[blockID];
		}
		
		private int[] rndBlocks;
		private int[] rndBlocksInterval;
		private int[] rndBlocksCount;

		private int rndBlockIndex = 0;
		public int getRandomID()
		{
			while (true)
			{
				rndBlockIndex++;
				if (rndBlockIndex >= rndBlocks.length)
					rndBlockIndex = 0;
				
				if (rndBlocksCount[rndBlockIndex] >= rndBlocksInterval[rndBlockIndex])
				{
					rndBlocksCount[rndBlockIndex] = 1;
					return rndBlocks[rndBlockIndex];
				}
				else
				{
					rndBlocksCount[rndBlockIndex]++;
				}
			}
		}
		
		public void load(final Configuration config, final String[] blockList)
		{
			this.maxObfuscateHeight = clamp(config.get(name, "maxObfuscateHeight", this.maxObfuscateHeight).getInt(), 0, 256);
	    	
			String[] list = config.getStringList("randomBlocks", this.name, blockList, "[blockID]:[interval]");
			int count = validateBlockList(list);
			if (count == 0)
			{
				if (list.length == 0)
					Log.error("%s.randomBlocks.length == 0", this.name);
				else
					Log.error("%s.randomBlocks has errors", this.name);
				
				list = blockList;
				count = list.length;
			}
			
			rndBlocks = new int[count];
			rndBlocksInterval = new int[count];
			rndBlocksCount = new int[count];
			int i = 0;
			for (String value : list)
			{
				String[] values = value.split(":");
				try
				{
					int v0 = Integer.valueOf(values[0]);
					int v1 = Integer.valueOf(values[1]);
					if (v0 >= 0 && v0 < 4096 && v1 > 0)
					{
						rndBlocks[i] = v0;
						rndBlocksCount[i] = 1;
						rndBlocksInterval[i] = v1;
						i++;
					}
				}
				catch (Exception e) { }
			}
		}
		
		private int validateBlockList(String[] list)
		{
			int count = 0;
			for (int i = 0; i < list.length; i++)
			{
				String[] values = list[i].split(":");
				try
				{
					int v0 = Integer.valueOf(values[0]);
					int v1 = Integer.valueOf(values[1]);
					if (v0 >= 0 && v0 < 4096 && v1 > 0)
						count++;
				}
				catch(Exception e)
				{
				}
			}
			return count;
		}
	}

	private static HashMap<String, WorldOptions> worlds = new HashMap<String, WorldOptions>();
	public static WorldOptions getWorldOptions(World world)
	{
		String name = world.getProviderName(); 
		WorldOptions options = worlds.get(name);
		if (options == null)
		{
	    	Configuration config = new Configuration(configFile, false);

	    	options = new WorldOptions(world);
	    	
	    	if (world.provider instanceof WorldProviderSurface)
	    	{
	    		options.load(config, new String[] {
		    			getID(Blocks.gold_ore, 1),
		    			getID(Blocks.iron_ore, 1),
		    			getID(Blocks.coal_ore, 1),
		    			getID(Blocks.lapis_ore, 1),
		    			getID(Blocks.diamond_ore, 1),
		    			getID(Blocks.redstone_ore, 1),
		    			getID(Blocks.emerald_ore, 1),
		    			getID(Blocks.mossy_cobblestone, 1),
		    			getID(Blocks.mob_spawner, 100),
	    		});
	    	}
	    	if (world.provider instanceof WorldProviderHell)
	    	{
				options.load(config, new String[] {
		    			getID(Blocks.glowstone, 1),
		    			getID(Blocks.netherrack, 1),
		    			getID(Blocks.nether_brick, 1),
		    			getID(Blocks.nether_brick_fence, 1),
		    			getID(Blocks.nether_brick_stairs, 1),
		    			getID(Blocks.nether_wart, 1),
		    			getID(Blocks.quartz_ore, 1),
		    	});
	    	}
	    	else
	    	if (world.provider instanceof WorldProviderEnd)
	    	{
				options.load(config, new String[] {
		    			getID(Blocks.end_stone, 1),
		    	});
	    	}
	    	else
	    	{
	    		options.load(config, new String[] {
		    			getID(Blocks.gold_ore, 1),
		    			getID(Blocks.iron_ore, 1),
		    			getID(Blocks.coal_ore, 1),
		    			getID(Blocks.lapis_ore, 1),
		    			getID(Blocks.diamond_ore, 1),
		    			getID(Blocks.redstone_ore, 1),
		    			getID(Blocks.emerald_ore, 1),
		    			getID(Blocks.mossy_cobblestone, 1),
		    			getID(Blocks.mob_spawner, 1000),
	    		});
	    	}
			
			config.save();
		}
		
		return options;
	}
	
	public static WorldOptions worldOptions;
	
	private static boolean[] obfuscateBlocks = new boolean[4096];
	
	public static int[] randomBlocks;
	
	public static boolean[] transparentBlocks = new boolean[4096];
	
	public static File configFile;
	
	public static void load(File modDir)
	{
    	configFile = new File(modDir, Orebfuscator.MODID + ".cfg");
    	Configuration config = new Configuration(configFile, false);
    	
		int[] list = config.get("Options", "obfuscateBlocks", new int[] {
    			getID(Blocks.stone),
    			getID(Blocks.dirt),
    			getID(Blocks.gold_ore),
    			getID(Blocks.iron_ore),
    			getID(Blocks.coal_ore),
    			getID(Blocks.lapis_ore),
    			getID(Blocks.diamond_ore),
    			getID(Blocks.redstone_ore),
    			getID(Blocks.emerald_ore),
    			getID(Blocks.netherrack),
    			getID(Blocks.nether_brick),
    			getID(Blocks.quartz_ore),
    			getID(Blocks.end_stone),
    	}).getIntList();
		
		if (list.length == 0)
		{
			for (int i = 0; i < obfuscateBlocks.length; i++)
			{
				obfuscateBlocks[i] = true;
			}
		}
		else
		{
			updateList(obfuscateBlocks, list);
		}
			
    	
		list = config.get("Options", "transparentBlocks", new int[] {}).getIntList();
		updateList(transparentBlocks, list);

		config.save();
	}

	private static void updateList(boolean[] blocks, int[] list)
	{
		for (int i = 0; i < blocks.length; i++)
		{
			blocks[i] = false;
		}
		for (int i = 0; i < list.length; i++)
		{
			if (list[i] >= 0 || list[i] < blocks.length)
			blocks[list[i]] = true;
		}
	}
	
	private static int getID(Block block)
	{
		return Block.getIdFromBlock(block);
	}
	
	private static String getID(Block block, int interval)
	{
		return String.format("%d:%d", Block.getIdFromBlock(block), interval);
	}
	
	private static int clamp(int value, int min, int max) {
		if (value < min) {
			value = min;
		}
		if (value > max) {
			value = max;
		}
		return value;
	}
	
	public static boolean isObfuscated(int id) {
		return obfuscateBlocks[id];
	}

	public static boolean isObfuscated(Block block) {
		return obfuscateBlocks[Block.getIdFromBlock(block)];
	}
	
	private static boolean[] _transparentBlocks = new boolean[4096];
	private static boolean TransparentCached = false;
	public static boolean isBlockTransparent(int id) 
	{
		if (id < 0)
			return true;
		if (!TransparentCached) 
		{
			// Generate TransparentBlocks by reading them from Minecraft
			for (int i = 0; i < _transparentBlocks.length; i++) {
				if (transparentBlocks[i]) 
				{
					_transparentBlocks[i] = true;
				}
				else
				{
					Block block = Block.getBlockById(i);
					if (block == null)
					{
						_transparentBlocks[i] = true;
					}
					else
					{
						_transparentBlocks[i] = !block.isNormalCube();
					}
				}
			}
			TransparentCached = true;
		}
		return _transparentBlocks[id];
	}
	
	public static boolean isTransparent(Block block)
	{
		return isBlockTransparent(Block.getIdFromBlock(block));
	}
}
