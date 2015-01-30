package Orebfuscator;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraftforge.common.config.Configuration;

public class Options 
{
	public static class WorldOptions
	{
		public static int maxObfuscateHeight = 128;
		
		public int[] randomBlocks;

		private int randomBlock = 0;
		public int getRandomBlock()
		{
			randomBlock++;
			if (randomBlock >= randomBlocks.length)
				randomBlock = 0;
			
			return randomBlocks[randomBlock];
		}
	}

	private static HashMap<String, WorldOptions> worlds = new HashMap<String, WorldOptions>();
	public static WorldOptions getWorldOptions(WorldProvider provider)
	{
		String name = provider.getClass().getSimpleName(); 
		WorldOptions options = worlds.get(name);
		if (options == null)
		{
	    	Configuration config = new Configuration(configFile, false);

	    	options = new WorldOptions();
	    	
			options.maxObfuscateHeight = clamp(config.get(name, "maxObfuscateHeight", options.maxObfuscateHeight).getInt(), 0, 256);
	    	
	    	if (provider instanceof WorldProviderSurface)
	    	{
				options.randomBlocks = config.get(name, "randomBlocks", new int[] {
		    			getID(Blocks.gold_ore),
		    			getID(Blocks.iron_ore),
		    			getID(Blocks.coal_ore),
		    			getID(Blocks.lapis_ore),
		    			getID(Blocks.diamond_ore),
		    			getID(Blocks.redstone_ore),
		    			getID(Blocks.emerald_ore),
		    			getID(Blocks.end_portal_frame),
		    			getID(Blocks.end_portal),
		    			getID(Blocks.mob_spawner),
		    			getID(Blocks.mossy_cobblestone),
		    	}).getIntList();
				if (options.randomBlocks.length == 0)
					options.randomBlocks = new int[] {getID(Blocks.stone)};
	    	}
	    	if (provider instanceof WorldProviderHell)
	    	{
				options.randomBlocks = config.get(name, "randomBlocks", new int[] {
		    			getID(Blocks.glowstone),
		    			getID(Blocks.netherrack),
		    			getID(Blocks.nether_brick),
		    			getID(Blocks.nether_brick_fence),
		    			getID(Blocks.nether_brick_stairs),
		    			getID(Blocks.nether_wart),
		    			getID(Blocks.quartz_ore),
		    			getID(Blocks.mob_spawner),
		    	}).getIntList();
				if (options.randomBlocks.length == 0)
					options.randomBlocks = new int[] {getID(Blocks.nether_brick)};
	    	}
	    	else
	    	if (provider instanceof WorldProviderEnd)
	    	{
				options.randomBlocks = config.get(name, "randomBlocks", new int[] {
		    			getID(Blocks.end_portal_frame),
		    			getID(Blocks.end_portal),
		    			getID(Blocks.end_stone),
		    	}).getIntList();
				if (options.randomBlocks.length == 0)
					options.randomBlocks = new int[] {getID(Blocks.end_stone)};
	    	}
	    	else
	    	{
				options.randomBlocks = config.get(name, "randomBlocks", new int[] {
		    			getID(Blocks.stone),
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
		    			getID(Blocks.end_portal_frame),
		    			getID(Blocks.end_portal),
		    			getID(Blocks.end_stone),
		    			getID(Blocks.mob_spawner),
		    	}).getIntList();
				if (options.randomBlocks.length == 0)
					options.randomBlocks = new int[] {getID(Blocks.stone)};
	    	}
			if (options.randomBlocks.length == 0)
				options.randomBlocks = new int[] {getID(Blocks.stone)};
			
			config.save();
		}
		
		return options;
	}
	
	public static WorldOptions worldOptions;
	
	public static int engineMode = 2;
	
	private static boolean[] obfuscateBlocks = new boolean[4096];
	
	public static int[] randomBlocks;
	
	public static boolean[] transparentBlocks = new boolean[4096];
	
	public static File configFile;
	
	public static void load(File modDir)
	{
    	configFile = new File(modDir, Orebfuscator.MODID + ".cfg");
    	Configuration config = new Configuration(configFile, false);
    	
		//engineMode = clamp(config.get("Options", "engineMode", engineMode).getInt(), 1, 2);

		//BlockChange.updateRadius = clamp(config.get("Options", "updateRadius", BlockChange.updateRadius).getInt(), 1, 5);
		
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
}
