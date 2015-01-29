package Orebfuscator;

import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.world.World;

public class ChunkInfo 
{
	// информаци€ о наличии секции. —екции - это блоки размером 16х16х16, секции располагаютс€ друг над другом
	// Chunk состоит из 16 секций, размер чанка 16x16x256
	//public boolean[] listLSB = new boolean[16];
	//public boolean[] listMSB = new boolean[16];

    // offsetsLSB[] - содержит список позиций, с которых начинаютс€ блоки размером 16x16x16 содержащие первые 8 бит BlockID
    // размер секции 16*16*16 = 4096 байт
	public int[] offsetsLSB = new int[16];

	// начало NibbleArrays содержащих ExtendedBlockStorage.blockMetadataArray
	// размер массива (16*16*16)/2 = 2048 (делим попалам, т.к. в одном байте содержитс€ два значени€ по 4 бита)
	public int[] offsetsMetadata = new int[16];
	
	// начало NibbleArray содержащего ExtendedBlockStorage.blocklightArray
	public int[] offsetsBlocklight = new int[16];
	
	// NibbleArray ExtendedBlockStorage.blockMSBArray
	public int[] offsetsMSB = new int[16];

	public int len;
	public byte[] data;
	
	public void parse(World world, byte[] data, int sectionLSB, int sectionMSB)
	{
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
        	// если есть небо, то в буфере будет массив ExtendedBlockStorage.skylightArray
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
            	for (int x = 0; x < 16; x++)
            	{
            		for (int y = 0; y < 16; y++)
            		{
            			for (int z = 0; z < 16; z++)
            			{
            				if ((x > 0 && x < 16) && (y == 0 || y == 15) && (z == 0 || z == 15))
            					setBlockID(x, y, z, 1);
            				if ((x == 0 || x == 15) && (y > 0 || y < 16) && (z == 0 || z == 15))
            					setBlockID(x, y, z, 1);
            				if ((x == 0 || x == 15) && (y == 0 || y == 15) && (z > 0 || z < 16))
            					setBlockID(x, y, z, 1);
            				
            				if (!Orebfuscator.isBlockTransparent(getBlockID(x, y, z)))
            				{
            					//data[pos + n] = 1;
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
	public int getBlockID(int x, int y, int z)
	{
		int section = y >> 4;
        if (this.offsetsLSB[section] > -1)
        {
        	y = y & 15;
        	
        	int id = this.data[this.offsetsLSB[section] + y << 8 | z << 4 | x] & 255;
        	if (this.offsetsMSB[section] > -1)
        	{
                int l = y << 4 | z << 4 | x;
                int i1 = l >> 1;
                int j1 = l & 1;
                id |= j1 == 0 ? this.data[this.offsetsMSB[section] + i1] & 15 : this.data[this.offsetsMSB[section] + i1] >> 4 & 15;
        	}
        }
		
		return 0;
	}
	
	public void setBlockID(int x, int y, int z, int blockID)
	{
		int section = y >> 4;
        if (this.offsetsLSB[section] > -1)
        {
        	y = y & 15;
        	
        	this.data[this.offsetsLSB[section] + y << 8 | z << 4 | x] = (byte) (blockID & 255);
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
}
