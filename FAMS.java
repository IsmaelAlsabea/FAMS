import java.util.Arrays;

abstract class FAMS { // file allocation management system
    final static int blkSize = 512;
    Disk disk;
    FAMS(Disk disk) {
        this.disk = disk;
    }

    int calcNumOfBlks(int sizeOfFileInBytes) { 
        // method below gives the ceiling of devision
        int numOfBlks = (sizeOfFileInBytes + blkSize - 1) / blkSize;
        return numOfBlks;
    }

    Block[] toBlocks(byte[] fileContent) {
        int fileContentSizeInBlocks = calcNumOfBlks(fileContent.length);
        byte[][] arr = new byte[fileContentSizeInBlocks][blkSize];
        Block[] fileAsBlocks = new Block[fileContentSizeInBlocks];
        for (int i = 0; i < fileContentSizeInBlocks; i++) {
            if (i != fileContentSizeInBlocks - 1)
                arr[i] = Arrays.copyOfRange(fileContent, i * blkSize, blkSize * (i + 1));
            else
                // last iteration since the last one might have a length less than blk size
                arr[i] = Arrays.copyOfRange(fileContent, i * blkSize, fileContent.length);
            fileAsBlocks[i] = new Block(arr[i]);
        }
        return fileAsBlocks;
    }
    
    abstract void deleteFile(String name);
    abstract void storeFile(byte[] fileContent, String path);
    abstract byte[] getFile(String name);

}