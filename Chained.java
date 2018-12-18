import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Chained extends FAMS {

	private Blk1Handler_contig_chained blk1Handler;
	private Blk2Handler_chained blk2Handler;
	private int maxSizeOfPtr;
	private static final String slash = "/"; // used to take the extra space if the ptr didn't need it
	// the two below are used in delete method and get method.

	Chained(Disk disk, Blk1Handler_contig_chained blk1Handler, Blk2Handler_chained blk2Handler) {
		super(disk);
		this.blk1Handler = blk1Handler;
		this.blk2Handler = blk2Handler;
		maxSizeOfPtr = ("" + blkSize).getBytes().length; // should be 3
		// at most we need 3 bytes for the pointer since blkSize is 512 (3 digits).
	}

	@Override
	void deleteFile(String name) {
		String blk1Entry = blk1Handler.getBlk1Entry(name);
		int[] startingPtAndLength = blk1Handler.parseBlock1Entry(blk1Entry);
		int blkKey = startingPtAndLength[0];
		byte[] blkData = null;
		List<Integer> toBeEmptiedBlks = new ArrayList<>();
		for (int i = 0; i < startingPtAndLength[1] - 1; i++) {
			blkData = disk.get_block(blkKey).data;
			toBeEmptiedBlks.add(blkKey);
			disk.delete_block(blkKey);
			blkKey = getNextBlkKey(blkData);
		}
		// for last block
		toBeEmptiedBlks.add(blkKey);
		disk.delete_block(blkKey);

		blk1Handler.deleteEntryFromBlock1(name);
		blk2Handler.setPartOfBitMapTo0(toBeEmptiedBlks);
	}

	@Override
	void storeFile(byte[] fileContent, String path) {
		int numOfBlks = calcNumOfBlks(fileContent.length);
		int sizeWithAdditionalSpace = calcNewSize(numOfBlks, fileContent.length);
		numOfBlks = calcNumOfBlks(sizeWithAdditionalSpace);
		List<Integer> ArrOfFreeBlks = allocateSpace(numOfBlks);
		byte[] arrWithMoreSpace = embedSpaceInFileDataForThePtr(fileContent, sizeWithAdditionalSpace);
		byte[] formattedFileDataArr = putThePtrsInTheDataArr(arrWithMoreSpace, ArrOfFreeBlks);
		Block[] fileAsBlocks = toBlocks(formattedFileDataArr);
		for (int i = 0; i < ArrOfFreeBlks.size(); i++)
			disk.set_block(fileAsBlocks[i], ArrOfFreeBlks.get(i));
		String name = path.substring(path.lastIndexOf("/") + 1);
		String blk1Entry = blk1Handler.formatToBlk1Entry(name, ArrOfFreeBlks.get(0), ArrOfFreeBlks.size());
		blk1Handler.appendToBlock1(blk1Entry);
		blk2Handler.setPartOfBitMapTo1(ArrOfFreeBlks);
	}

	private int calcNewSize(int numOfBlks, int sizeInBytes) {
		if ((numOfBlks * blkSize - sizeInBytes) > (numOfBlks * maxSizeOfPtr))
			return sizeInBytes + numOfBlks * maxSizeOfPtr;
		else
			return numOfBlks * blkSize;
	}

	private byte[] embedSpaceInFileDataForThePtr(byte[] fileData, int sizeWithAdditionalSpace) {
		byte[] newFileDataArr = new byte[sizeWithAdditionalSpace];
		int newArrSizeInBlks = calcNumOfBlks(sizeWithAdditionalSpace);
		int numOfElementsToCopy = blkSize - maxSizeOfPtr;
		// if it runs more than arrSizeInBlks, I am doing something wrong.
		int i = 0;
		for (; i < newArrSizeInBlks - 1; i++) {
		
		System.arraycopy(fileData, i * numOfElementsToCopy, newFileDataArr, i * numOfElementsToCopy + maxSizeOfPtr,
					numOfElementsToCopy);
		}
		int j = fileData.length - i * numOfElementsToCopy;
		int r = i * numOfElementsToCopy;
		int x=fileData.length - i * numOfElementsToCopy;
		System.arraycopy(fileData, i * numOfElementsToCopy, newFileDataArr, i * numOfElementsToCopy + maxSizeOfPtr,
				fileData.length - i * numOfElementsToCopy);
		return newFileDataArr;
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
			key = 0;
			elementTaken = false;
			while (!(key > 2)) // while key is less than 2 keep looping
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

	private byte[] putThePtrsInTheDataArr(byte[] data, List<Integer> listOfFreeBlks) {
		byte[] blkKeyInByteForm = null;
		int slashSizeInBytes = slash.getBytes().length;
		for (int i = 0; i < listOfFreeBlks.size(); i++) {
			if (i != listOfFreeBlks.size() - 1)
				blkKeyInByteForm = ("" + listOfFreeBlks.get(i + 1)).getBytes();
			System.arraycopy(blkKeyInByteForm, 0, data, i * blkSize, blkKeyInByteForm.length);
			if (blkKeyInByteForm.length < maxSizeOfPtr)
				System.arraycopy(slash.getBytes(), 0, data, i * blkSize + blkKeyInByteForm.length, slashSizeInBytes);
		}
		return data;
	}

	@Override
	byte[] getFile(String name) {
		String blk1Entry = blk1Handler.getBlk1Entry(name);
		int[] startingPtAndLength = blk1Handler.parseBlock1Entry(blk1Entry);
		int numOfBlks = startingPtAndLength[1];
		int blkKey = startingPtAndLength[0];
		byte[] blkData = null;
		byte[] fileData = null;
		byte[] fileContentMinusLastBlk = new byte[(numOfBlks - 1) * blkSize];
		int elementsToCopy = blkSize - maxSizeOfPtr;
		int i = 0;
		for (; i < numOfBlks - 1; i++) {
			blkData = disk.get_block(blkKey).data;
			blkKey = getNextBlkKey(blkData);
			System.arraycopy(blkData, maxSizeOfPtr, fileContentMinusLastBlk, elementsToCopy * i, elementsToCopy);
		}
		blkData = disk.get_block(blkKey).data;
		fileData = new byte[fileContentMinusLastBlk.length + blkData.length];
		System.arraycopy(fileContentMinusLastBlk, 0, fileData, 0, fileContentMinusLastBlk.length);
		System.arraycopy(blkData, maxSizeOfPtr, fileData, fileContentMinusLastBlk.length,
				blkData.length - maxSizeOfPtr);
		return fileData;
	}

	private int getNextBlkKey(byte[] blkData) {
		int blkKey = 0;
		String ptr = new String(Arrays.copyOfRange(blkData, 0, maxSizeOfPtr));
		if (ptr.contains("/")) // if ptr is made of 3 digits, it will not have the slash char.
			blkKey = Integer.parseInt(ptr.substring(0, ptr.indexOf("/")));
		else
			blkKey = Integer.parseInt(ptr);
		return blkKey;
	}

}
