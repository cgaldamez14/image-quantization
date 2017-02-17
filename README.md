CS4551 Multimedia Software Systems
@Author: Carlos Galdamez
California State University, Los Angeles


Project file structure is as follows:
======================================

.
├── src                     		
	  ├── image_quantization		# Source files
├── imgs                    		# All images used for testing
	  ├── results					# All resulting images after processing will be store here
└── readme.txt


NOTE: All resulting images will be automatically saved to the results directory.
      If you will be using images from the imgs directory make sure to put the correct 
      image path.
      
	DESCRIPTION: Homework 1

		When program is run you will be prompted to choose an option for image processing. It will be
	either 1, 2, or 3. If 4 is chosen the program will exit. If anything else is chosen an error message will be displayed
	and the menu will be shown again. If option 2 is chosen there will be an additional prompt to enter the N-level for 
	conversion.
		After each conversion the main menu will be displayed again.
	
	ENJOY!
	

Compile requirement
======================================
JDK Version 7.0 or above


Compile Instruction on Command Line:
======================================
javac *.java


Execution Instruction on Command Line:
======================================
java CS4551_Main [imagePath]
e.g.
java CS4551_Main imgs/Ducky.ppm 

NOTE: Keep in mind that the images are located in the imgs folder.