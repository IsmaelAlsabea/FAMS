import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class UI {
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int router = -1;
		int exit_flag = 1;
		FAMS fams = createAFAMS(in);
		while (true) {
			if (exit_flag == -1)
				break;
			router = uiMenu(in);
			switch (router) {
			case 1:
				displayAFile(in, fams);
				break;
			case 2:
				displayTheFileTable(fams);
				break;
			case 3:
				displayTheBitMap(fams);
				break;
			case 4:
				displayADiskBlock(in, fams);
				break;
			case 5:
				copyFromPrgrmToRealSys(in, fams);
				break;
			case 6:
				copyFromRealSysToPrgrm(in, fams);
				break;
			case 7:
				deleteAFile(in, fams);
				break;
			case 8:
				exit_flag = -1;
				System.out.println("Program Finished Running.");
				break;
			default:
				System.out.println("main's switch getting an unanticipated value");
			}
		}
		in.close();

	}

	static int uiMenu(Scanner in) {

		int router = -1;
		System.out.println("Please choose what you want to do. \n" 
				+ "1) Display a file\n"
				+ "2) Display the file table\n" + "3) Display the free space bitmap\n" 
				+ "4) Display a disk block\n"
				+ "5) Copy a file from the simulation to a file on the real system\n"
				+ "6) Copy a file from the real system to a file in the simulation\n" 
				+ "7) Delete a file\n"
				+ "8) Exit");
		router = in.nextInt();

		return router;
	}

	static void displayAFile(Scanner in, FAMS fams) {

		System.out.println("Please Enter the name of the file you want display");
		String  nameOfFile = readAString(in);
		String fileData= new String (fams.getFile(nameOfFile));
		System.out.println(fileData);
	}

	static void displayTheFileTable(FAMS fams) {
		String bitMap = new String(fams.disk.get_block(0).data);
		System.out.println(bitMap);
	}

	static void displayTheBitMap(FAMS fams) {
		String bitMap = new String(fams.disk.get_block(1).data);
		System.out.println(bitMap);

	}

	static void displayADiskBlock(Scanner in, FAMS fams) {
		System.out.println("Please Enter the block_number");
		int block_number = readAnInt(in);
		Block blockData=fams.disk.get_block(block_number);
		String diskData;
		if (blockData==null) {
			System.out.println(0);
		} else {
			diskData = new String(blockData.data);
			System.out.println(diskData);
		}
	}

	static void copyFromPrgrmToRealSys(Scanner in, FAMS fams) {

		System.out.println("Please Enter the file Path of file you want to copy to from program");
		String filePath = readAString(in);
		System.out.println("Please Enter the name of the file you want to be copied to System");
		String nameOfFileToBeCopied = readAString(in);
		File outFile = new File(filePath);
		byte[] fileData = null;
		OutputStream x = null;
		try {
			fileData = fams.getFile(nameOfFileToBeCopied);
			x = new BufferedOutputStream(new FileOutputStream(outFile));
			x.write(fileData);
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				x.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

	}

	static void copyFromRealSysToPrgrm(Scanner in, FAMS fams) {
		System.out.println("Please Enter the file Path you want to copy to program");
		String filePath = readAString(in);
		File file = new File(filePath);
		FileInputStream fin = null;
		byte fileContent[] = null;
		try {
			fin = new FileInputStream(file);
			fileContent = new byte[(int) file.length()];
			// Reads up to certain bytes of data from this input stream into an array of bytes.
			fin.read(fileContent);
			fams.storeFile(fileContent, filePath);

		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while reading file " + ioe);
		} finally {
			// close the streams using close method
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}

	}

	static String readAString(Scanner in) {
		String readString = "";
		while (readString.equals(""))
			readString = in.nextLine();
		return readString;
	}

	static void deleteAFile(Scanner in, FAMS fams) {
		System.out.println("Please write the name of the file you want to delete");
		String fileName = readAString(in);
		fams.deleteFile(fileName);
	}

	static FAMS createAFAMS(Scanner in) {
		System.out.println("Please choose which File Allocation Management System Scheme you want \n"
				+ "1) Contiguous FAMS\n" + "2) Chained FAMS\n" + "3) Indexed FAMS\n");
		FAMS fams = null;
		switch (readAnInt(in)) {
		case 1:
			fams = createAContiguousFAMS();
			break;
		case 2:
			fams= creatAChainedFAMS();
			break;
		case 3:
			fams= createAnIndexedFAMS();
			break;
		}
		return fams;
	}

	

	 static FAMS createAContiguousFAMS() {
		Disk disk = new Disk();
		Blk1Handler_contig_chained blk1Handler = new Blk1Handler_contig_chained(disk);
		Blk2Handler_contig blk2Handler = new Blk2Handler_contig(disk, blk1Handler);
		FAMS contig = new Contiguous(disk, blk1Handler, blk2Handler);
		return contig;
	}
	 
	 static FAMS creatAChainedFAMS() {
		// TODO Auto-generated method stub
		Disk disk = new Disk();
		Blk1Handler_contig_chained blk1Handler = new Blk1Handler_contig_chained(disk);
		Blk2Handler_chained blk2Handler = new Blk2Handler_chained(disk, blk1Handler);
		FAMS chained = new Chained(disk, blk1Handler, blk2Handler);
		return chained;
	}
	 static FAMS createAnIndexedFAMS() {
		Disk disk = new Disk();
		Blk1Handler_indexed blk1Handler = new Blk1Handler_indexed(disk);
		Blk2Handler_indexed  blk2Handler = new Blk2Handler_indexed(disk, blk1Handler);
		FAMS indexed = new Indexed (disk, blk1Handler, blk2Handler);
		return indexed;
	}

	static int readAnInt(Scanner in) {
		int readInt = -1;
		while (!in.hasNextInt())
			in.nextLine();
		readInt = in.nextInt();
		return readInt;
	}
}
