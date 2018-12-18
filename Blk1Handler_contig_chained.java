
class Blk1Handler_contig_chained extends Blk1Handler {

	Blk1Handler_contig_chained(Disk disk) {
		super(disk);
		declareBlock1();
	}

	void declareBlock1() {
		byte[] block1Data = new byte[blkSize];
		String tableEntry = formatToBlk1Entry("blk1", 0, 1);
		block1Data = tableEntry.getBytes();
		Block block1 = new Block(block1Data);
		disk.set_block(block1, 0);
	}
	
	int[] parseBlock1Entry(String blk1Entry) {
		// string will look like {fileName,tab , starting_block, tab, length}
		int[] x = new int[2]; // [0] starting block, [1] length
		//String name = s.substring(0, s.indexOf((char) 9)); // for testing
		blk1Entry = blk1Entry.substring(blk1Entry.indexOf((char) 9) + 1);
		x[0] = Integer.parseInt(blk1Entry.substring(0, blk1Entry.indexOf((char) 9)));
		blk1Entry = blk1Entry.substring(blk1Entry.indexOf((char) 9) + 1);
		x[1] = Integer.parseInt(blk1Entry.substring(0, blk1Entry.indexOf("\n")));
		// returns x[0] starting point, x[1] length
		return x;
	}

	String formatToBlk1Entry(String name, int startingPt, int length) {
		String str = name + "\t" + startingPt + "\t" + length + "\n";
		return str;
	}

}