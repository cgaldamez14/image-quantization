package image_quantization;

import java.io.IOException;
import java.util.Scanner;

public class CS4551_Galdamez {
	public static void main(String args[]) throws IOException{
	
		// if there is no command line argument, exit the program
		
		if(args.length != 1){
			usage();
			System.exit(1);
		}
    
		// Create an ImageProcessor object with the input PPM file name.
		ImageProcessor img;

		Scanner in = new Scanner(System.in);

		System.out.println("--Welcome to Multimedia Software System--");
    
		byte choice;
		do{
    	
			img = new ImageProcessor(args[0]);

			System.out.print("\n------------------------------------------- MAIN MENU --------------------------------------------------\n\n"
					+ "\t1. Conversion to Gray-scale Image ( 24 bits -> 8 bits)\n"
					+ "\t2. Conversion to N-level Image\n"
					+ "\t3. Conversion to 8bit Indexed Color Image using Uniform Color Quantization ( 24 bits -> 8 bits)\n"
					+ "\t4. Quit\n"
					+ "\n"
					+ "Please enter the task number: [ 1 - 4 ]: ");
    
			choice = in.nextByte();
        
			switch(choice){
    			case 1:
    				img.convertToGrayscale();
    				break;
    			case 2:
    				System.out.print("Enter the N level: ");
    				img.NLevelConvertion(in.nextInt());
    				break;
    			case 3:
    				img.uniformQuantization();
    				break;
    			case 4:
    				in.close();
    				System.out.println("--Good Bye--");
    				System.exit(1);
    			default: System.err.println("\nYou did not enter a value option. Please try again.\n");
			}
		}while(true);
  }
	
	public static void usage(){
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}
}
