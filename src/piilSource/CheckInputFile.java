package piilSource;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class CheckInputFile {
	
	String[] choices = {"tab","comma","space","semicolon","dash"};
	String[] methylationInputTypes = {"beta-values"};
	String[] expressionInputTypes = {"RPKM"};
	JComboBox separatorCombo = new JComboBox(choices);
	JComboBox inputTypeCombo;
	SpinnerModel spinID = new SpinnerNumberModel(1,1,100,1);
	SpinnerModel spinData = new SpinnerNumberModel(2,2,100,1);
	
	JFileChooser fileChooser;
	JSpinner idRowSpinner = new JSpinner(spinID);
	JSpinner dataRowSpinner = new JSpinner(spinData);
	JButton loadData = new JButton("Choose another file");
	JPanel myPanel, containerPanel;
	JLabel separatorLabel, idRowLabel, dataRowLabel, inputTypeLabel, fileLabel;
	File selectedFile;
	boolean validFileLoaded = true;
	String[] header;
	final ImageIcon icon = new ImageIcon(getClass().getResource("/resources/logoIcon.png"));
	int samplesRowNumber, dataRowNumber;
	
	public CheckInputFile(File input, Character type) {
		
		myPanel = new JPanel();
		myPanel.setLayout(new GridBagLayout());
		containerPanel= new JPanel();
		containerPanel.setLayout(new BorderLayout());
		separatorLabel = new JLabel("The columns are separated by:");
		idRowLabel = new JLabel("Sample IDs appear in row number:");
		dataRowLabel = new JLabel("Data starts from row number:");
		inputTypeLabel =  new JLabel("The data type:");
		selectedFile = input;
		fileLabel = new JLabel(input.getName() + " was selected.");
		fileLabel.setPreferredSize(new Dimension(300,20));
		if (type.equals('M')){
			inputTypeCombo = new JComboBox(methylationInputTypes);
		}
		else {
			inputTypeCombo = new JComboBox(expressionInputTypes);
		}
		
		
		addComp(myPanel, fileLabel, 0, 0, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, separatorLabel, 0, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, separatorCombo, 1, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, idRowLabel, 0, 2, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, idRowSpinner, 1, 2, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, dataRowLabel, 0, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, dataRowSpinner, 1, 3, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, inputTypeLabel, 0, 4, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE);
		addComp(myPanel, inputTypeCombo, 1, 4, 2, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
		addComp(myPanel, loadData, 0, 5, 2, 1, GridBagConstraints.LINE_END, GridBagConstraints.NONE);
		
		containerPanel.add(myPanel, BorderLayout.CENTER);
		
		   
		loadData.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent bc) {
			File newFile;
			if (bc.getSource() == loadData) {
				fileChooser = new JFileChooser();
				fileChooser.setFileFilter(null);
				String home = System.getProperty("user.home");
				File directory = new File(home);
				fileChooser.setCurrentDirectory(directory);
				int returnVal = fileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) { 
					newFile = fileChooser.getSelectedFile();
					if (newFile == null){
						JOptionPane.showMessageDialog(Interface.bodyFrame, "No file was loaded!");
					}
					else {
						fileLabel.setText(newFile.getName() + " was selected.");
						selectedFile = newFile;
					}
				}
			}
		}
		});
		
		int result = JOptionPane.showConfirmDialog(null, containerPanel, "Please choose ...", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
		if (result == JOptionPane.CANCEL_OPTION) {
			selectedFile = null;
		}
		else {
			samplesRowNumber = Integer.parseInt(idRowSpinner.getValue().toString());
			dataRowNumber = Integer.parseInt(dataRowSpinner.getValue().toString());
		}
		
	}
	
	public File getChosenFile(){
		return selectedFile;
	}
	
	public int getSampleRow(){
		return samplesRowNumber;
	}
	
	public int getDataRow(){
		return dataRowNumber;
	}
	
	public String getSeparator(){
		String splitBy;
		String separator = separatorCombo.getSelectedItem().toString();
		if (separator.equals("tab")){
			splitBy = "\t";
		} else if (separator.equals("space")){
			splitBy = "\\s+";
		} else if (separator.equals("comma")){
			splitBy = ",";
		}
		else if (separator.equals("dash")){
			splitBy = " ";
		}
		else {
			splitBy = ";";
		}
		return splitBy;
	}
	
	private void addComp(JPanel thePanel, JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch){
		
		GridBagConstraints gridConstraints = new GridBagConstraints();
		
		gridConstraints.gridx = xPos;
		gridConstraints.gridy = yPos;
		gridConstraints.gridwidth = compWidth;
		gridConstraints.gridheight = compHeight;
		gridConstraints.weightx = 1;
		gridConstraints.weighty = 1;
		gridConstraints.insets = new Insets(5,5,5,5);
		gridConstraints.anchor = place;
		gridConstraints.fill = stretch;
		
		thePanel.add(comp, gridConstraints);
		
	}

}
