import java.util.Arrays;

class Contiguous extends FAMS {
	Blk1Handler_contig_chained blk1Handler;
	Blk2Handler_contig blk2Handler;

	Contiguous(Disk disk, Blk1Handler_contig_chained blk1Handler, Blk2Handler_contig blk2Handler) {
		super(disk);
		this.blk1Handler = blk1Handler;
		this.blk2Handler = blk2Handler;
	}

	int[] allocateSpace(int numOfBlocks) {

		String block2Str = blk2Handler.getBlk2DataInStringForm();
		int counter = 0; 
		int[] startingPtAndLength = new int[2];
		//Arrays.fill(startingPtAndLength, -1);

		/*
		 * while loop that checks every 0 if the zero is followed by a streak of 0s,
		 * such that the number of zeros equals the number of blocks that are needed,
		 * return the starting point of this streak.
		 */
		for (int i = 2; i < block2Str.length(); i++) { // 0 and 1 are already occupied, no need to check them.
			counter = 0;
			startingPtAndLength[0] = -1;
			if (block2Str.charAt(i) == '0') {
				startingPtAndLength[0] = i;
				while (block2Str.charAt(i) == '0') {
					counter += 1;
					if (counter == numOfBlocks) {
						startingPtAndLength[1]=counter;
						return startingPtAndLength;
					}
					i++;
				}
			}
		}
		// if we are here, then this means that there is no empty place that the file
		// can get in.
		Arrays.fill(startingPtAndLength, -1);
		return startingPtAndLength;
	}

	void storeFile(byte[] fileContent, String path) {
		Block[] fileAsBlocks = toBlocks(fileContent);
		int[] startingPtAndLength = allocateSpace(fileAsBlocks.length);
		for (int i = 0; i < startingPtAndLength[1]; i++)
			disk.set_block(fileAsBlocks[i], startingPtAndLength[0] + i);
		String name = path.substring(path.lastIndexOf("/") + 1);
		String blk1Entry = blk1Handler.formatToBlk1Entry(name, startingPtAndLength[0], startingPtAndLength[1]);
		blk1Handler.appendToBlock1(blk1Entry);
		blk2Handler.setPartOfBitMapTo1(startingPtAndLength[0], startingPtAndLength[1]);
	}

	byte[] getFile(String name) {
		String blk1Entry = blk1Handler.getBlk1Entry(name);
		int[] startingPtAndLength = blk1Handler.parseBlock1Entry(blk1Entry);
		byte[] fileContentMinusLastBlock = new byte[blkSize * (startingPtAndLength[1] - 1)];
		byte[] deliverable = null;
		for (int i = 0; i < startingPtAndLength[1]; i++) {

			byte[] blkData = disk.get_block(startingPtAndLength[0] + i).data;
			if (i == startingPtAndLength[1] - 1) {
				deliverable = new byte[fileContentMinusLastBlock.length + blkData.length];
				System.arraycopy(fileContentMinusLastBlock, 0, deliverable, 0, fileContentMinusLastBlock.length);
				System.arraycopy(blkData, 0, deliverable, fileContentMinusLastBlock.length, blkData.length);
				break;
			}
			System.arraycopy(blkData, 0, fileContentMinusLastBlock, blkData.length * i, blkData.length);
		}
		return deliverable;
	}

	@Override
	void deleteFile(String name) {
		// TODO Auto-generated method stub
		String blk1Entry=blk1Handler.getBlk1Entry(name);
		int[] startingPtAndLength= blk1Handler.parseBlock1Entry(blk1Entry);
		for (int i=0;i< startingPtAndLength[1];i++)
			disk.delete_block(startingPtAndLength[0]+1);
		blk1Handler.deleteEntryFromBlock1(name);
		blk2Handler.setPartOfBitMapTo0(startingPtAndLength[0], startingPtAndLength[1]);
	}
	
	
	

}
