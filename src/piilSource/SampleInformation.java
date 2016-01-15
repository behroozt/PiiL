/*  
    PiiL: Pathways Interactive vIsualization tooL
    Copyright (C) 2015  Behrooz Torabi Moghadam

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package piilSource;

import java.io.File;

public class SampleInformation {
	
	private int idIndex;
	private String separator;
	private int fieldCount;
	private int[] chosenColumns;
	private File chosenFile;

	public SampleInformation(int index, String sep, int counter, int[] cols, File theFile){
		
		idIndex = index;
		separator = sep;
		fieldCount = counter;
		chosenColumns = cols;
		chosenFile = theFile;
	}
	
	public int getIndex(){
		return idIndex;
	}
	
	public String getSeparator(){
		return separator;
	}
	
	public int getFieldCount(){
		return fieldCount;
	}
	
	public int[] getColumns(){
		return chosenColumns;
	}
	
	public File getFile(){
		return chosenFile;
	}
}

