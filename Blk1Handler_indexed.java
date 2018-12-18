import java.util.List;

class Blk1Handler_indexed extends Blk1Handler {

	 Blk1Handler_indexed(Disk disk) {
		super(disk);
		declareBlock1();
	}
	 
	 void declareBlock1() {
			byte[] block1Data = new byte[blkSize];
			String tableEntry = formatToBlk1Entry("blk1", 0);
			block1Data = tableEntry.getBytes();
			Block block1 = new Block(block1Data);
			disk.set_block(block1, 0);
		}
		
		int parseBlock1Entry(String blk1Entry) {
			int indexBlkPtr=0; 
			// string will look like {fileName,tab , index_block, newLine}
			indexBlkPtr = Integer.parseInt(blk1Entry.substring(blk1Entry.indexOf("\t")+1, 
					blk1Entry.indexOf("\n")));
			return indexBlkPtr;
		}
	
		String formatToBlk1Entry(String name, int indexBLkPtr) {
			String str = name + "\t" + indexBLkPtr + "\n";
			return str;
		}
		
		
		
		
		
		
		
}
