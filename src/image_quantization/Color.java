package image_quantization;

/***********************************************************
 * 
 * Color Class is a representation of an RGB color pixel.   
 * @author: Carlos M. Galdamez 
 * 
 **********************************************************/
public class Color {

	private int r;
	private int g;
	private int b;
	
	/**
	 * Constructor set the red, green and blue values
	 * @param r - red value ( 0 - 255 )
	 * @param g - green value ( 0 - 255 )
	 * @param b - blue value ( 0 - 255 )
	 */
	public Color(int r, int g, int b){
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/**
	 * Gets the red pixel value.
	 * @return - integer representing red value
	 */
	public int getR() {
		return r;
	}

	/**
	 * Gets the green pixel value.
	 * @return - integer representing green value
	 */
	public int getG() {
		return g;
	}

	/**
	 * Gets the blue pixel value.
	 * @return - integer representing blue value
	 */
	public int getB() {
		return b;
	}
	
	
}
