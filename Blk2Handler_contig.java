class Blk2Handler_contig extends Blk2Handler {

	private Blk1Handler_contig_chained blk1Handler;

	Blk2Handler_contig(Disk disk, Blk1Handler_contig_chained blk1Handler) {
		super(disk);
		this.blk1Handler = blk1Handler;
		declareBlock2();
	}

	void declareBlock2() {
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

	void setPartOfBitMapTo0(int startingPt, int length) {
		changeBitMapValue(startingPt, length, '0');
	}

	void setPartOfBitMapTo1(int startingPt, int length) {
		changeBitMapValue(startingPt, length, '1');
	}

	private void changeBitMapValue(int startingPt, int length, char valueToBeInserted) {
		StringBuilder blk2Data = new StringBuilder(getBlk2DataInStringForm());
		for (int i = 0; i < length; i++)
			blk2Data.setCharAt(startingPt + i, valueToBeInserted);
		disk.set_block(new Block(blk2Data.toString().getBytes()), 1);
	}
	
	
	
	
	
	
	
	
	
	
	
	

}








