## IMAGE QUANTIZATION <br>
@Author: Carlos Galdamez <br>
California State University, Los Angeles <br>


### Project file structure is as follows:

.

├── src

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── image_quantization
&emsp;&emsp;&emsp;&emsp;<strong>Source files</strong>


├── imgs
&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;<strong>All images used for testing</strong>


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;├── results&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<strong>All resulting images after processing will be store here </strong>


├── .gitignore


└── README.md


<strong>NOTE:</strong> 
> 	All resulting images will be automatically saved to the results directory. If you will be using images from the imgs directory make sure to put the correct image path.
      
<strong>DESCRIPTION:</strong>
>	When program is run you will be prompted to choose an option for image processing. It will beeither 1, 2, or 3. If 4 is chosen the program will exit. If anything else is chosen an error message will be displayed and the menu will be shown again. If option 2 is chosen there will be an additional prompt to enter the N-level for conversion.
	After each conversion the main menu will be displayed again.
ENJOY!
	

### Compile requirement
JDK Version 7.0 or above


### Compile Instruction on Command Line:
javac *.java


### Execution Instruction on Command Line:
java CS4551_Main [imagePath]
e.g.
java CS4551_Main imgs/Ducky.ppm 

NOTE: Keep in mind that the images are located in the imgs folder.
