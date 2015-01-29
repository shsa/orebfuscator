package Orebfuscator;

import net.minecraft.block.Block;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkInfo 
{
	// ���������� � ������� ������. ������ - ��� ����� �������� 16�16�16, ������ ������������� ���� ��� ������
	// Chunk ������� �� 16 ������, ������ ����� 16x16x256
	//public boolean[] listLSB = new boolean[16];
	//public boolean[] listMSB = new boolean[16];

    // offsetsLSB[] - �������� ������ ������, � ������� ���������� ����� �������� 16x16x16 ���������� ������ 8 ��� BlockID
    // ������ ������ 16*16*16 = 4096 ����
	public int[] offsetsLSB = new int[16];

	// ������ NibbleArrays ���������� ExtendedBlockStorage.blockMetadataArray
	// ������ ������� (16*16*16)/2 = 2048 (����� �������, �.�. � ����� ����� ���������� ��� �������� �� 4 ����)
	public int[] offsetsMetadata = new int[16];
	
	// ������ NibbleArray ����������� ExtendedBlockStorage.blocklightArray
	public int[] offsetsBlocklight = new int[16];
	
	// NibbleArray ExtendedBlockStorage.blockMSBArray
	public int[] offsetsMSB = new int[16];

	public int startX;
	public int startY;
	
	// ������������ ������+1 ������
	public int len;
	public byte[] data;
	
	public void parse(World world, int chunkX, int chunkZ, int sectionLSB, int sectionMSB, byte[] data)
	{
		this.startX = chunkX << 4;
		this.startY = chunkZ << 4;
		
		this.data = data;
    	int countLSB = 0;
    	len = 0;
    	int l;
    	int pos = 0;
    	int i;
        for (i = 0; i < 16; ++i)
        {
            l = sectionLSB >> i & 1;
            if (l == 1)
            {
            	offsetsLSB[i] = pos;
            	pos += 4096;
            	
            	countLSB++;
            	len = i + 1;
            }
            else
            {
            	offsetsLSB[i] = -1;
            }
        }
        
        for (i = 0; i < len; i++)
        {
        	if (offsetsLSB[i] > -1)
        	{
        		offsetsMetadata[i] = pos;
        		pos += 2048;
        	}
        }
        
        for (i = 0; i < len; i++)
        {
        	if (offsetsLSB[i] > -1)
        	{
        		offsetsBlocklight[i] = pos;
        		pos += 2048;
        	}
        }
       	
       	
        if (!world.provider.hasNoSky)
        {
        	// ���� ���� ����, �� � ������ ����� ������ ExtendedBlockStorage.skylightArray
        	pos += countLSB * 2048;
        }

        for (i = 0; i < len; i++)
        {
            l = sectionMSB >> i & 1;
            if (l == 1)
            {
        		offsetsMSB[i] = pos;
        		pos += 2048;
            }
            else
            {
            	offsetsMSB[i] = -1;
            }
        }
        
        for (i = 0; i < len; i++)
        {
            if (offsetsLSB[i] > -1)
            {
            	l = i << 4;
            	for (int x = 0; x < 16; x++)
            	{
            		for (int y = 0; y < 16; y++)
            		{
            			for (int z = 0; z < 16; z++)
            			{
            				if (neetObfuscate(world, x, l | y, z))
            				{
            					setBlockID(x, l | y, z, Options.getRandomBlock());
            				}
            			}
            		}
            	}
            }
        }
	}
	
    /**
     * Returns the block corresponding to the given coordinates inside a chunk.
     */
	public int getBlockID(World world, int x, int y, int z)
	{
		int section = y >> 4;
        if (this.offsetsLSB[section] > -1)
        {
        	y = y & 15;
        	
        	int id = this.data[this.offsetsLSB[section] + (y << 8 | z << 4 | x)] & 255;
        	if (this.offsetsMSB[section] > -1)
        	{
                int l = y << 4 | z << 4 | x;
                int i1 = l >> 1;
                int j1 = l & 1;
                id |= j1 == 0 ? this.data[this.offsetsMSB[section] + i1] & 15 : this.data[this.offsetsMSB[section] + i1] >> 4 & 15;
        	}
        	
        	return id;
        }
        
		return 0;
	}
	
	public void setBlockID(int x, int y, int z, int blockID)
	{
		int section = y >> 4;
        if (this.offsetsLSB[section] > -1)
        {
        	y = y & 15;
        	
        	this.data[this.offsetsLSB[section] + (y << 8 | z << 4 | x)] = (byte) (blockID & 255);
        	if (this.offsetsMSB[section] > -1)
        	{
                int l = y << 4 | z << 4 | x;
                int i1 = l >> 1;
                int j1 = l & 1;
                int pos = this.offsetsMSB[section] + i1;
                if (j1 == 0)
                {
                    this.data[pos] = (byte)(this.data[pos] & 240 | blockID & 15);
                }
                else
                {
                    this.data[pos] = (byte)(this.data[pos] & 15 | (blockID & 15) << 4);
                }
        	}
        }
	}
	
	public boolean isTransparent(World world, int x, int y, int z)
	{
		if (y < 0 || y > 255)
			return true;
		
		if (x < 0 || x > 15 || z < 0 || z > 15)
		{
			if (Options.engineMode == 2)
			{
				Block block = world.getBlock(this.startX | x, y, this.startY | z);
				return Options.isBlockTransparent(Block.getIdFromBlock(block));
			}
			return true;
		}
			
			
		return Options.isBlockTransparent(getBlockID(world, x, y, z));
	}
	
	public boolean neetObfuscate(World world, int x, int y, int z)
	{
		if (y > Options.maxObfuscateHeight)
			return false;
		
		if (isTransparent(world, x, y, z))
		{
			return false;
		}
		
		return !(
					isTransparent(world, x - 1, y, z) ||
					isTransparent(world, x + 1, y, z) ||
					isTransparent(world, x, y - 1, z) ||
					isTransparent(world, x, y + 1, z) ||
					isTransparent(world, x, y, z - 1) ||
					isTransparent(world, x, y, z + 1)
				);
	}
}
