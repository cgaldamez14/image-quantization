package image_quantization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***********************************************************
 * 
 * ImageProcessor Class extends the Image class. Provides methods for image
 * quantization such as conversion to grayscale image, conversion to a N level
 * image using thresholding and error diffusion, and uniform color quantization
 * 24 bit to 8 bit color image.   
 * @author: Carlos M. Galdamez 
 * 
 **********************************************************/
public class ImageProcessor extends Image{

	// Weights for r,g,b to convert to a grayscale image
	private final static double RED_WEIGHT = 0.299;
	private final static double GREEN_WEIGHT = 0.587;
	private final static double BLUE_WEIGHT = 0.114;
	
	// Error distributions for Floyd-Steinberg dithering using error diffusion
	private final static double E_NEIGHBOR = 7.0 / 16.0;
	private final static double S_NEIGHBOR = 5.0 / 16.0;
	private final static double SW_NEIGHBOR = 3.0 / 16.0;
	private final static double SE_NEIGHBOR = 1.0 / 16.0;
	
	// Calculates the size of each segment in the 256 color scale. For example if we decide to have
	// 8 different colors for red then this will result in having 8 separate segments from 0 - 255.
	// The segment size in this case would be 32 for red. This is used for uniform color quantization.
	private final static int RED_SEG_SIZE = 256/8;          // 32
	private final static int GREEN_SEG_SIZE = 256/8;        // 32
	private final static int BLUE_SEG_SIZE = 256/4;         // 64
	
	private String fileName;
	
	// Average intensity for grayscale image
	private int average;


	/**
	 * Constructor calls constructor for super class and extracts image name
	 * without the path and without the file extension.
	 * @param fn
	 */
	public ImageProcessor(String fn) {
		super(fn);
		fileName = new File(fn).getName().split("\\.")[0];
	}
	
	//---------------------------------------------- GRAYSCALE CONVERSION -------------------------------------------//
	
	/**
	 * Converts a 24 bit Image to an 8 bit grayscale image.
	 * @throws IOException
	 */
	public void convertToGrayscale() throws IOException{
		
		int rgb[] = new int[3];
		int intensity;
		int sum = 0;
		
		for(int row = 0; row < getH(); row++){
			for (int column = 0; column < getW(); column++){
				getPixel(column, row, rgb);
				intensity = (int) Math.round(RED_WEIGHT * rgb[0] + GREEN_WEIGHT * rgb[1] + BLUE_WEIGHT * rgb[2]);
				sum += intensity;
				setPixel(column, row, new int[]{intensity,intensity,intensity});
			}
		}
		
		// Average of all intensities in the grayscale image
		average = (int) Math.round( sum / (this.getH() * this.getW() * 1.0));				
		
		exportProcessedImage(fileName + "-gray");
	}
	
	//---------------------------------------------------- END -----------------------------------------------------//
	
	
	
	//-------------------------------------------- N LEVEL CONVERSIONS  -------------------------------------------//
	
	/**
	 * Converts a grayscale image into an N-Level Image using to different methods. The first method is
	 * thresholding, the second is error diffusion. Resulting images are exported to the imgs folder.
	 * @param n - level of conversion
	 * @throws IOException
	 * @see Image
	 */
	public void NLevelConvertion(int n) throws IOException{
		convertToGrayscale();
		
		Image img;
		int thresholdValues[] = new int[n-1];
		int intensityValues[] = new int[n];
		
		int segmentLength = Math.round( 255 / (n -1)); 
	
		// Generates threshold values based on the n value
		for(int i = 0; i < thresholdValues.length;i++)
			thresholdValues[i] = (n == 2)? average : segmentLength / 2 + segmentLength * i;	                      		

		// Generates intensity values that will be used based on the n value
		for(int i = 0; i < intensityValues.length;i++)
			intensityValues[i] = segmentLength * i;
		
		img = new Image(new java.io.File( "." ).getCanonicalPath() + "/imgs/results/" + fileName + "-gray.ppm");
		convertUsingThresholding(img,thresholdValues, intensityValues);
		exportProcessedImage(img,fileName + "-threshold-"+n+"level");

		
		img = new ImageProcessor(new java.io.File( "." ).getCanonicalPath() + "/imgs/results/" + fileName + "-gray.ppm");
		errorDiffusion(img,thresholdValues, intensityValues);
		exportProcessedImage(img,fileName + "-errordiffusion-"+n+"level");

	}
	
	/**
	 * Converts a grayscale image to an N-Level image using the thresholding method.
	 * @param img - grayscale image that will be converted to an N-Level image
	 * @param thresholdValues - values that will be used as the threshold for the image
	 * @param intensityValues - values that will replace the original values of the grayscale image
	 * @throws IOException
	 */
	private void convertUsingThresholding(Image img, int[] thresholdValues, int[] intensityValues) throws IOException{

		int rgb[] = new int[3];
		int intensity;

		for(int row = 0; row < img.getH(); row++){
			for (int column = 0; column < img.getW(); column++){
				img.getPixel(column, row, rgb);
				// gets the nearest value (based on thresholding) to the original grayscale value from the intensity value list 
				intensity = getNearestValue(rgb[0], thresholdValues, intensityValues);
				img.setPixel(column, row, new int[]{intensity,intensity,intensity});
			}
		}	
		
	}
	
	/**
	 * Converts a grayscale image to an N-Level image using the error diffusion method.
	 * @param img - grayscale image that will be converted to an N-Level image
	 * @param thresholdValues - values that will be used as the threshold for the image
	 * @param intensityValues - values that will replace the original values of the grayscale image
	 * @throws IOException
	 */
	private void errorDiffusion(Image img, int[] thresholdValues, int[] intensityValues) throws IOException{
		
		int intensity;
		int quantizationError;
		
		int rgb[] = new int[3];
		int errorArray[][] = new int[img.getH()][img.getW()];                      // Need to keep track of values in a separate array 
																				   // because there might be negative values
		
		// Sets up 2D array with intensities from grayscale image
		for(int row = 0; row < img.getH(); row++){
			for (int column = 0; column < img.getW(); column++){
				img.getPixel(column, row, rgb);
				errorArray[row][column] = rgb[0];
			}
		}


		for(int row = 0; row < img.getH(); row++){
			for (int column = 0; column < img.getW(); column++){
				int current = errorArray[row][column];
				
				// Use thresholding to get nearest value
				intensity = getNearestValue(current, thresholdValues, intensityValues);
				quantizationError = current - intensity;
				
				img.setPixel(column, row, new int[]{intensity,intensity,intensity});
				
				// Update neighbors to reflect neihghboring errors
				diffuseErrors(column,row,errorArray,quantizationError);

			}
		}

	}
	
	/**
	 * Gets the nearest value to the original value based on thresholding
	 * @param current - pixel value
	 * @param thresholdValues - values that will be used as the threshold for the image
	 * @param intensityValues - values that will replace the original values of the grayscale image
	 * @return
	 */
	private int getNearestValue(int current, int[] thresholdValues, int[] intensityValues){
		
		int i = 0;
		
		// Needs to be a while loop otherwise 255 will not be choosen even if it is the closest
		while(i < thresholdValues.length){
			if(current < thresholdValues[i]) break;
			i++;
		}
		return intensityValues[i];
		
	}
	
	/**
	 * Updates all neighbors of specified pixel to reflect its error using error distributions stated
	 * by Floyd-Steinberg dithering.
	 * @param column - pixel column index
	 * @param row - pixel row index
	 * @param intermediate - array keeping track of all errors
	 * @param error - error for current pixel
	 */
	private void diffuseErrors(int column, int row, int[][] intermediate, int error){
		updateNeighbor(column+1,row,intermediate,error * E_NEIGHBOR);
		updateNeighbor(column - 1,row + 1,intermediate,error * SW_NEIGHBOR);
		updateNeighbor(column, row + 1, intermediate, error * S_NEIGHBOR);
		updateNeighbor(column + 1, row + 1, intermediate, error * SE_NEIGHBOR);

	}
	
	/**
	 * Updates pixel value for specified pixel by adding specified error to
	 * current pixel value. 
	 * @param column - pixel column index
	 * @param row - pixel row index
	 * @param intermediate - array keeping track of all errors
	 * @param error - error including Floyd-Steinberg error distribution
	 */
	private void updateNeighbor(int column, int row, int[][] intermediate, double error){
		if (column < 0 || column >= getW() || row >= getH()) return;
		intermediate[row][column] = (int) Math.round(intermediate[row][column] + error);
	}
	
	//---------------------------------------------------- END -----------------------------------------------------//

	//---------------------------------------- UNIFORM COLOR QUANTIZATION  -----------------------------------------//

	/**
	 * Converts a 24 bit color image to an 8 bit color image using the uniform color quantization method.
	 * @throws IOException
	 */
	public void uniformQuantization() throws IOException{
		int rgb[] = new int[3];
		
		// Generate the look up table
		Map<Integer, Color> LUT = generateLUT();
		
		// Generates 8 bit index for 24 bit image and saves it as a .ppm file
		writeIndexPPM();

		Image img = new Image(new java.io.File( "." ).getCanonicalPath() + "/imgs/results/" + fileName + "-index.ppm");

		for(int row = 0; row < this.getH(); row++){
			for (int column = 0; column < this.getW(); column++){
				img.getPixel(column, row, rgb);

				int value = rgb[0];

				Color c = LUT.get(value);                           // Gets coresponding color from generated look up table

				img.setPixel(column, row, new int[]{c.getR(),c.getG(),c.getB()});
			}
		}
		
		exportProcessedImage(img,fileName + "-QT8");
	}
	
	/**
	 * Generates an 8 bit index correspoding to a 24 bit value in the look up table. Exports 
	 * the generated indexing as .ppm file. 
	 * @throws IOException
	 */
	private void writeIndexPPM() throws IOException{

		int rgb[] = new int[3];

		for(int row = 0; row < this.getH(); row++){
			for (int column = 0; column < this.getW(); column++){
				getPixel(column, row, rgb);
				
				// Scales down rgb values, then uses bit shifting to get final 8 bit value
				int index = rgb[0]/RED_SEG_SIZE;
				index <<= 3;
				index |= rgb[1]/GREEN_SEG_SIZE;
				index <<= 2;
				index |= rgb[2]/BLUE_SEG_SIZE;

				setPixel(column, row, new int[]{index,index,index});
			}
		}
		
		exportProcessedImage(fileName + "-index");
	}

	/**
	 * Generates and prints look up table to console.
	 * @return - map with Integer and Color key value pairs (look up table)
	 * @see Map
	 * @see StringBuilder
	 * @see Color
	 */
	private Map<Integer, Color> generateLUT(){
		
		// Create instance of HashMap to hold color values for look up table
		Map<Integer, Color> LUT = new HashMap<>();
		int r,g,b,rVal,gVal,bVal;

		
		System.out.println("\nCreating Lookup Table ....\n");
		
		StringBuilder lut = new StringBuilder("   ----------------------------------------------\n"
											+ "\tindex\t\tr\tg\tb\n"
											+ "   ----------------------------------------------\n"); 

		for(int i = 0; i < 256; i++){
			
			r = i >> 5;						// Shift bits to the right 5 times
			g = i >> 2;						// Shift bits to the right 2 times
			g &= 7;							// Bit and operation with first 3 bits all on
			b = i & 3;                      // Bit and operation as well but only first 2 bits on

			rVal = 16 + 32 * r;
			gVal = 16 + 32 * g;
			bVal = 32 + 64 * b;

			LUT.put(i, new Color(rVal,gVal,bVal));
			lut.append("\t" + i +"\t\t" + rVal + "\t" + gVal + "\t" + bVal + "\n");

		}
		
		System.out.println(lut.toString());
		
		return LUT;
		
	}
	
	//---------------------------------------------------- END -----------------------------------------------------//

	
	/**
	 * Write the current instance of the Image as a .ppm file to the imgs folder.
	 * @param fileName - image name without extension
	 * @throws IOException
	 */
	private void exportProcessedImage(String fileName) throws IOException{
		
		write2PPM(new java.io.File( "." ).getCanonicalPath() + "/imgs/results/" + fileName + ".ppm");
		
	}
	
	/**
	 * Write a specified instance of the Image class as a .ppm file to the imgs folder
	 * @param img - Image instance to export
	 * @param fileName - image name without extension
	 * @throws IOException
	 */
	private void exportProcessedImage(Image img, String fileName) throws IOException{
		
		img.write2PPM(new java.io.File( "." ).getCanonicalPath() + "/imgs/results/" + fileName + ".ppm");
		
	}

}
