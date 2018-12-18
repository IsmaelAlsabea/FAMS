abstract class Blk2Handler {

    final static int blkSize = 512;
    Disk disk;
    
    Blk2Handler(Disk disk) {
        this.disk = disk;
        
    }
    

    String getBlk2DataInStringForm() {
        Block blk = disk.get_block(1);
        String str = new String(blk.data);
        return str;
    }
    
    abstract void declareBlock2();
}