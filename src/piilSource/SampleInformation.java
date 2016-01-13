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

