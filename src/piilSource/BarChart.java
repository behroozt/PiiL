/*  
    PiiL: Pathway Interactive vIsualization tooL
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
/* **** Developed on JFreeChart open-source library available at: http://www.jfree.org/jfreechart/ **** */

package piilSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.freehep.util.export.ExportDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class BarChart extends ApplicationFrame {

	JFrame chartFrame;
	JPanel buttonsPanel;
	JButton exportButton, closeButton;
	String metaLabel;
	String geneName;
	int activeTab = Interface.tabPane.getSelectedIndex();
	TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
	List<Integer> significantSites;
	
    public BarChart(final String title, List<List<String>> list, Character meta) {
    	
        super(title);
        geneName = title;
        metaLabel = (meta.equals('M')) ? "beta" : "expression";
        final CategoryDataset dataset = createDataset(list,metaLabel);
        final JFreeChart chart = createChart(title,dataset,metaLabel);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));
        setContentPane(chartPanel);
        
        exportButton = new JButton("Export to image");
		exportButton.setPreferredSize(new Dimension(150, 30));
		closeButton= new JButton("Close");
		closeButton.setPreferredSize(new Dimension(150,30));
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.setBackground(Color.WHITE);
		buttonsPanel.add(exportButton);
		buttonsPanel.add(closeButton);
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent bc) {
				ExportDialog export = new ExportDialog();
				export.showExportDialog(chartFrame, "Export view as ...",chartPanel, "Barplot of the " + metaLabel + " values for all samples - " + title);
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chartFrame.dispose();
			}
		});
		
        chartFrame = new JFrame();
        chartFrame.setLayout(new BorderLayout());
        chartFrame.setSize(400, 100);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int xPos = (dim.width / 2) - (chartFrame.getWidth() / 2);
		int yPos = (dim.height / 2) - (chartFrame.getHeight() / 2);
		chartFrame.setLocation(xPos,yPos);
		chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chartFrame.add(chartPanel, BorderLayout.CENTER);
		chartFrame.add(buttonsPanel, BorderLayout.SOUTH);
		
		chartFrame.setSize(800,400);
		chartFrame.setVisible(true);

    }

    private CategoryDataset createDataset(List<List<String>> list, String metaLabel) {
        
        // row keys...
        final String series1 = metaLabel + " values";
        boolean grouping = false;
        HashMap<String, List<String>> classes = null ;
        if (pathway.getIDsInGroups() != null){
        	grouping = true;
        	classes = pathway.getIDsInGroups();
        }

        List<String> categories = new ArrayList<String>();
        List<String> ids = ParseKGML.getTabInfo(activeTab).getSamplesIDs();

        for (int i =0; i < ids.size(); i++){
        	categories.add(ids.get(i));
        }
        
        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        String geneCode = null;
        
        for (Entry<String, Genes> item : pathway.getMapedGeneLabel().entrySet()){
        	if (item.getValue().getText().equals(geneName)){
        		geneCode = item.getKey();
        		continue;
        	}
        }
        
        float sum;
    	int invalid = 0;
    	double value;
        
        if (metaLabel.equals("expression")){ // plot for expression values
        	
        	if (grouping){
        		int sampleIndex;
        		for (String group : classes.keySet()){
        			for (String sampleID : classes.get(group)){
        				sum = 0; value = 0; invalid = 0;
        				sampleIndex = categories.indexOf(sampleID);
        				for (int j = 0; j < list.size() ; j ++){
                    		if (!isNumeric(list.get(j).get(sampleIndex))){
                    			invalid ++;
            					continue;
            				}
                    		sum += Float.parseFloat(list.get(j).get(sampleIndex));
                    	}
                    	value = sum / (list.size() - invalid);
                    	dataset.addValue(value, group, sampleID);
        			}
        			
        		}
        	}
        	else{
        		for (int i = 0; i < list.get(0).size() ; i ++){
                	sum = 0; value = 0;
                	for (int j = 0; j < list.size() ; j ++){
                		if (!isNumeric(list.get(j).get(i))){
                			invalid ++;
        					continue;
        				}
                		sum += Float.parseFloat(list.get(j).get(i));
                	}
                	value = sum / (list.size() - invalid);
                	dataset.addValue(value, series1, categories.get(i));
                }
        	}
        }
        
        else if (metaLabel.equals("beta")){  // plot for methylation values
        	
        	significantSites = new ArrayList<Integer>();
            significantSites = pathway.getSelectedSites(geneCode);
            
            if (grouping){
        		int sampleIndex;
        		for (String group : classes.keySet()){
        			for (String sampleID : classes.get(group)){
        				sum = 0; value = 0; invalid = 0;
        				sampleIndex = categories.indexOf(sampleID);
        				if (significantSites == null){
        					for (int j = 0; j < list.size() ; j ++){
                        		if (!isNumeric(list.get(j).get(sampleIndex))){
                        			invalid ++;
                					continue;
                				}
                        		sum += Float.parseFloat(list.get(j).get(sampleIndex));
                        	}
                        	value = sum / (list.size() - invalid);
                        	dataset.addValue(value, group, sampleID);
        				}
        				else { // some sites are selected
        					for (int item : significantSites){
                				if (item != -1){
                					if (!isNumeric(list.get(item).get(sampleIndex))){
                						invalid ++;
                						continue;
                					}
                					sum += Double.parseDouble(list.get(item).get(sampleIndex));
                				}
                				
                			}
                    		value = sum / (significantSites.size() - invalid);
                    		dataset.addValue(value, group, sampleID);
        				}
        				
        			}
        			
        		}
        	}
        	else {
        		for (int i = 0; i < list.get(0).size() ; i ++){
                	sum = 0; value = 0; invalid = 0;
                	if (significantSites == null){
    					for (int j = 0; j < list.size() ; j ++){
                    		if (!isNumeric(list.get(j).get(i))){
                    			invalid ++;
            					continue;
            				}
                    		sum += Float.parseFloat(list.get(j).get(i));
                    	}
                    	value = sum / (list.size() - invalid);
                    	dataset.addValue(value, series1, categories.get(i));
    				}
    				else { // some sites are selected
    					for (int item : significantSites){
            				if (item != -1){
            					if (!isNumeric(list.get(item).get(i))){
            						invalid ++;
            						continue;
            					}
            					sum += Double.parseDouble(list.get(item).get(i));
            				}
            				
            			}
                		value = sum / (significantSites.size() - invalid);
                		dataset.addValue(value, series1, categories.get(i));
    				}
                }
        	}
        }
        
       
        return dataset;       
    }

    private double calculateValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	private static boolean isNumeric(String str) {
		try {  
		    double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe){  
		    return false;  
		}  
		return true;
	}

	private JFreeChart createChart(String title, final CategoryDataset dataset, String metaLabel) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Barplot of the " + metaLabel + " values for all samples - " + title,         // chart title
            "Samples",               // domain axis label
            metaLabel + " values",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips?
            false                     // URLs?
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);       

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.white);

        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        if (metaLabel.equals("beta")){
        	rangeAxis.setRange(0,1);
        	rangeAxis.setTickUnit(new NumberTickUnit(0.2));
        	
        }

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, new Color(0,0,150), 
            0.0f, 0.0f, new Color(0,0,150)
        );
        final GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, new Color(0,80,0), 
            0.0f, 0.0f, new Color(0,80,0)
        );
        final GradientPaint gp2 = new GradientPaint(
            0.0f, 0.0f, Color.red, 
            0.0f, 0.0f, Color.red
        );
        final GradientPaint gp3 = new GradientPaint(
                0.0f, 0.0f, Color.yellow, 
                0.0f, 0.0f, Color.yellow
        );
        final GradientPaint gp4 = new GradientPaint(
                0.0f, 0.0f, Color.cyan, 
                0.0f, 0.0f, Color.cyan
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);
        renderer.setSeriesPaint(3, gp3);
        renderer.setSeriesPaint(4, gp4);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
            );
        if (dataset.getColumnCount() > 40){
        	domainAxis.setTickLabelFont(new Font("Serif", Font.PLAIN, 10));
        	domainAxis.setCategoryLabelPositions(
                    CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2)
                );
        }
        return chart;   
    }
}
