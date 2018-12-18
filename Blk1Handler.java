abstract class Blk1Handler {

    Disk disk;
    final static int blkSize = 512;

    Blk1Handler(Disk disk) {
        this.disk = disk;
    }

     String getBlk1DataInStringForm() {
        Block blk = disk.get_block(0);
        String str = new String(blk.data);
        return str;
    }

    String getBlk1Entry(String name) {
        String blk1Data = getBlk1DataInStringForm();    
        int fileNamePos=blk1Data.indexOf(name);
        String str = blk1Data.substring(fileNamePos, blk1Data.indexOf("\n",fileNamePos)+1);
        return str;
    }
    
	void deleteEntryFromBlock1(String name) {
		String blk1Data = getBlk1DataInStringForm();
		if (blk1Data.indexOf(name) == -1) {
			System.out.println("no such file exist");
			return;
		}
		String toBeRemoved = getBlk1Entry(name);
		blk1Data = blk1Data.replace(toBeRemoved, "");
		disk.set_block(new Block(blk1Data.getBytes()), 0);
	}


    void appendToBlock1(String blk1Entry) {
        String blk1Data = getBlk1DataInStringForm();
        blk1Data += blk1Entry;
        disk.set_block(new Block(blk1Data.getBytes()), 0);
    }
    
    abstract void declareBlock1();
    

}