class Block {
    final static int blkSize = 512;
    // boolean occupied;
    byte[] data;

    Block(byte[] data) {
        if (data.length > blkSize) {
            System.out.println("Error data size is more than the block size");
            System.exit(1);
        } else {
            this.data = new byte[blkSize];
            this.data = data;
        }
    }
}