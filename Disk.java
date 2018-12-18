
//disk deliver bytes, and store bytes.
class Disk {
    private final static int numberOfBlocks = 256;
    private Block[] blocks;
    Disk() {
        blocks = new Block[numberOfBlocks];
    }

    Block get_block(int block_key) {
        return blocks[block_key];
    }

    void set_block(Block data, int block_key) {
        this.blocks[block_key] = data;
    }
    void delete_block(int block_key) {	
    	this.blocks[block_key]=null;
    }
    
    int getSize() {
    	return numberOfBlocks;
    }
}