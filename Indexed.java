import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Indexed extends FAMS{

	private Blk2Handler_indexed blk2Handler;
	private Blk1Handler_indexed blk1Handler;

	Indexed(Disk disk, Blk1Handler_indexed blk1Handler, Blk2Handler_indexed blk2Handler) {
		 super (disk);
		 this.blk1Handler=blk1Handler;
		 this.blk2Handler=blk2Handler;
	 }

	@Override
	void deleteFile(String name) {
		String blk1Entry=blk1Handler.getBlk1Entry(name);
		int indexBlkIndex= blk1Handler.parseBlock1Entry(blk1Entry);
		List<Integer> toBeEmptiedBlks=getIndexBlkData(indexBlkIndex);
		for (int i=0;i< toBeEmptiedBlks.size();i++) {
			disk.delete_block(toBeEmptiedBlks.get(i));
		}
		blk1Handler.deleteEntryFromBlock1(name);
		blk2Handler.setPartOfBitMapTo0(toBeEmptiedBlks);
		
	}
	
	@Override
	void storeFile(byte[] fileContent, String path) {
		int numOfBlks = calcNumOfBlks(fileContent.length)+1; //the one for the index block
		List<Integer> ArrOfFreeBlks = allocateSpace(numOfBlks);
		Block[] fileAsBlocks = toBlocks(fileContent);
		int firstBlkIndex=ArrOfFreeBlks.remove(0); //remove returns the element that was removed.
		disk.set_block(formatIndexBlk(ArrOfFreeBlks), firstBlkIndex);
		for (int i = 0; i < ArrOfFreeBlks.size(); i++)
			disk.set_block(fileAsBlocks[i], ArrOfFreeBlks.get(i));
		String name = path.substring(path.lastIndexOf("/") + 1);
		String blk1Entry = blk1Handler.formatToBlk1Entry(name, firstBlkIndex);
		blk1Handler.appendToBlock1(blk1Entry);
		blk2Handler.setPartOfBitMapTo1(ArrOfFreeBlks);
	}
	
	private List<Integer> allocateSpace(int numOfBlocks) {
		String blk2Str = blk2Handler.getBlk2DataInStringForm();
		// starting pt is at the beginning of the array of free blocks.
		List<Integer> ArrOfFreeBlks = new ArrayList<>();
		int key = 0;// it should never be zero, zero here is a flag.
		boolean elementTaken; 
		// 0 and 1 are already occupied, no need to check them.
		Random rand = new Random();
		while (true) {
			key=0; elementTaken=false;
			while(!(key>2)) //while key is less than 2 keep looping
			key = rand.nextInt(disk.getSize() - 2) + 2; 

			if (blk2Str.charAt(key) == '1')
				continue;

			if (ArrOfFreeBlks.size() == 0) {
				ArrOfFreeBlks.add(key);
			} else {
				for (int x = 0; x < ArrOfFreeBlks.size(); x++)
					if (ArrOfFreeBlks.get(x) == key) {
						elementTaken = true;
						break;
					}
				if (!elementTaken)
					ArrOfFreeBlks.add(key);
			}

			if (ArrOfFreeBlks.size() == numOfBlocks)
				return ArrOfFreeBlks;
		}
	}
	
	Block formatIndexBlk(List<Integer> listOfPtrs) {
		String indexBlkData="";
		for (int i=0;i<listOfPtrs.size();i++)
			indexBlkData+=listOfPtrs.get(i)+"\t";
		Block indexBlk= new Block(indexBlkData.getBytes());
		return indexBlk;
	}


	@Override
	byte[] getFile(String name) {
		String blk1Entry = blk1Handler.getBlk1Entry(name);
		int indexBlkIndex= blk1Handler.parseBlock1Entry(blk1Entry);
		List<Integer> indexBlkData= getIndexBlkData(indexBlkIndex);
		int numOfBlks= indexBlkData.size();
		byte[] blkData;
		int i=0;
		byte[] fileContent = null;
		byte[] fileContentMinusLastBlk=new byte[(numOfBlks-1)*blkSize];
		for (;i < numOfBlks-1; i++) {
			blkData = disk.get_block(indexBlkData.get(i)).data;
			System.arraycopy(blkData, 0, fileContentMinusLastBlk, i*blkSize, blkSize);
		}
			blkData = disk.get_block(indexBlkData.get(i)).data;
			fileContent= new byte[fileContentMinusLastBlk.length+blkData.length];
			System.arraycopy(fileContentMinusLastBlk, 0, fileContent, 0, fileContentMinusLastBlk.length);
			System.arraycopy(blkData, 0, fileContent, fileContentMinusLastBlk.length, blkData.length);
		return fileContent;
	}

	private List<Integer> getIndexBlkData(int indexBlkIndex) {
		String indexBlkData=new String(disk.get_block(indexBlkIndex).data);
		List<Integer> listOfOccupiedBlks= new ArrayList<>();
		int blkIndex=0; //0 here is a flag.
		while(!indexBlkData.equals("")){
			blkIndex= Integer.parseInt(indexBlkData.substring(0,indexBlkData.indexOf("\t")));
			listOfOccupiedBlks.add(blkIndex);
			indexBlkData=indexBlkData.substring(indexBlkData.indexOf("\t")+1);
		}
		
		return listOfOccupiedBlks;
	}
	
	
	

}
