import java.util.List;

class Blk2Handler_chained extends Blk2Handler {

	Blk1Handler_contig_chained blk1Handler;
	 Blk2Handler_chained(Disk disk, Blk1Handler_contig_chained blk1Handler) {
		// TODO Auto-generated constructor stub
		 super(disk);
		 this.blk1Handler = blk1Handler;
		 declareBlock2();
	}

	@Override
	void declareBlock2() {
		// TODO Auto-generated method stub
		byte[] blk2Data = new byte[blkSize];
		String tableEntry = blk1Handler.formatToBlk1Entry("blk2", 1, 1);
		blk1Handler.appendToBlock1(tableEntry);
		String bitMap = "11";
		for (int i = 2; i < 256; i++)
			bitMap += "0";
		blk2Data = bitMap.getBytes();
		Block block2 = new Block(blk2Data);
		disk.set_block(block2, 1);
		
	}

	void setPartOfBitMapTo0(List<Integer> keysOfEmptiedBlks) {
		changeBitMapValue(keysOfEmptiedBlks, '0');
	}


	void setPartOfBitMapTo1(List<Integer> keysOfOccupiedBlks) {
		changeBitMapValue(keysOfOccupiedBlks, '1');
	}
	
	private void changeBitMapValue(List<Integer> blk_keys,char valueToBeInserted) {
		StringBuilder blk2Data = new StringBuilder(getBlk2DataInStringForm());
		for (int i = 0; i < blk_keys.size(); i++)
			blk2Data.setCharAt(blk_keys.get(i), valueToBeInserted);
		disk.set_block(new Block(blk2Data.toString().getBytes()), 1);
	}



}
