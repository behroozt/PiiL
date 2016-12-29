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
/***** Developed on JFreeChart open-source library available at: http://www.jfree.org/jfreechart/ *****/

package piilSource;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;

public class Histogram extends ApplicationFrame {

	JFrame chartFrame;
	String metaLabel;
	JPanel histogramPanel, buttonsPanel;
	JButton exportButton, closeButton;
	int activeTab = Interface.tabPane.getSelectedIndex();
	TabsInfo pathway = ParseKGML.getTabInfo(activeTab);
	List<Integer> significantSites;
	String geneName;
	String chartLabel = "";
	
	public Histogram(final String s, List<List<String>> list, Character meta)
	{
		super(s);
		geneName = s;
		metaLabel = (meta.equals('M')) ? "beta" : "expression"; 
		histogramPanel = createDemoPanel(s,list,metaLabel);
		histogramPanel.setPreferredSize(new Dimension(500, 270));
		setContentPane(histogramPanel);
		
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
				export.showExportDialog(chartFrame, "Export view as ...",histogramPanel, chartLabel);
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
		chartFrame.add(histogramPanel, BorderLayout.CENTER);
		chartFrame.add(buttonsPanel, BorderLayout.SOUTH);
		chartFrame.setSize(800,400);
		chartFrame.setVisible(true);

	}
	
	public JPanel createDemoPanel(String s, List<List<String>> collection, String metaLabel){
		JFreeChart jfreechart = createChart(s,createDataset(collection,metaLabel),metaLabel);
		return new ChartPanel(jfreechart);
	}

	private IntervalXYDataset createDataset(List<List<String>> betaValues, String metaLabel){

		boolean grouping = false;
        HashMap<String, List<String>> classes = null ;
        if (pathway.getIDsInGroups() != null){
        	grouping = true;
        	classes = pathway.getIDsInGroups();
        }
		
		HistogramDataset histogramdataset = new HistogramDataset();
		double ad[] = new double[betaValues.get(0).size()];
		
		float sum;
    	int invalid = 0;
    	double value;
    	
    	List<String> categories = new ArrayList<String>();
        List<String> ids = ParseKGML.getTabInfo(activeTab).getSamplesIDs();

        for (int i =0; i < ids.size(); i++){
        	categories.add(ids.get(i));
        }
    	
    	
    	if (metaLabel.equals("expression")){ // plot for expression values
    		if (grouping){
    			
    			for (Entry<String, List<String>> item : classes.entrySet()){
    				double groupValues[] = new double[item.getValue().size()]; 
    				for (int i = 0; i < item.getValue().size(); i ++){
    					int index = categories.indexOf(item.getValue().get(i));
    					groupValues[i] = Double.parseDouble(betaValues.get(0).get(index));
    				}
    				histogramdataset.addSeries(item.getKey(), groupValues, 100);
        		}
    		}
    		
    		else {
    			for (int i=0; i< betaValues.get(0).size(); i++){
    				
    				if (!isNumeric(betaValues.get(0).get(i))){
						continue;
					}
					ad[i] = Double.parseDouble(betaValues.get(0).get(i));
    			}
    			histogramdataset.addSeries(metaLabel + " values", ad, 100);
    			
    		}
        }
    	
    	else if (metaLabel.equals("beta")){
    		String geneCode = null;
            for (Entry<String, Genes> item : pathway.getMapedGeneLabel().entrySet()){
            	if (item.getValue().getText().equals(geneName)){
            		geneCode = item.getKey();
            		continue;
            	}
            }
    		significantSites = new ArrayList<Integer>();
            significantSites = pathway.getSelectedSites(geneCode);
            
            if (significantSites == null){ // include all CpG sites
            	for (int i = 0; i < betaValues.get(0).size() ; i ++){
                	sum = 0; value = 0; invalid = 0;
                	for (int j = 0; j < betaValues.size() ; j ++){
                		if (!isNumeric(betaValues.get(j).get(i))){
                			invalid ++;
        					continue;
        				}
                		sum += Float.parseFloat(betaValues.get(j).get(i));
                	}
                	ad[i] = sum / (betaValues.size() - invalid);
            	}
            }
            else { // if there is a sd filter or site selection
                
            	for (int i = 0; i < betaValues.get(0).size() ; i ++){
            		sum =0; invalid = 0;
            		for (int item : significantSites){
        				if (item != -1){
        					if (!isNumeric(betaValues.get(item).get(i))){
        						invalid ++;
        						continue;
        					}
        					sum += Double.parseDouble(betaValues.get(item).get(i));
        				}
            		}
            		ad[i] = sum / (significantSites.size() - invalid);
            	}
            } // end of sd filter
            
            if (grouping){
            	for (Entry<String, List<String>> item : classes.entrySet()){
            		double groupValues[] = new double[item.getValue().size()]; 
            		for (int i = 0; i < item.getValue().size() - 1; i ++){
            			int index = categories.indexOf(item.getValue().get(i));
            			groupValues[i] = ad[index];
            		}
            		histogramdataset.addSeries(item.getKey(), groupValues, 100, 0.00, 0.99);
            	}
            }
            else {
            	histogramdataset.addSeries(metaLabel + " values", ad, 50, 0.00, 0.99);
            }
    	} // end of if beta
		
		return histogramdataset;
		
	} // end of createDataset

	private static boolean isNumeric(String str) {
		try {  
		    double d = Double.parseDouble(str);  
		}  
		catch(NumberFormatException nfe){  
		    return false;  
		}  
		return true;
	}

	private JFreeChart createChart(String s, IntervalXYDataset intervalxydataset, String metaLabel)
	{
		chartLabel = metaLabel + " values of all samples - " + s;
		
		if (pathway.getViewMode() == 2){
			String grpTag = ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex()).getSamplesInfo().get("-1").get(ParseKGML.getTabInfo(Interface.tabPane.getSelectedIndex()).getGroupingIndex());
			chartLabel += " - grouped by: " + grpTag; 
		}
		JFreeChart jfreechart = ChartFactory.createHistogram(chartLabel, null, null, intervalxydataset, PlotOrientation.VERTICAL, true, true, false);
		jfreechart.setBackgroundPaint(Color.white); 
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setBackgroundPaint(Color.lightGray);
		xyplot.setDomainGridlinePaint(Color.red);
		xyplot.setRangeGridlinePaint(Color.white);
		xyplot.setForegroundAlpha(0.5F);
//		xyplot.setBackgroundImageAlpha(0.0F);
		XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
		xybarrenderer.setDrawBarOutline(false);
        
        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, new Color(255,140,0), 
            0.0f, 0.0f, new Color(255,140,0)
        );
        final GradientPaint gp1 = new GradientPaint(
            0.0f, 0.0f, new Color(0,80,0), 
            0.0f, 0.0f, new Color(0,80,0)
        );
        final GradientPaint gp2 = new GradientPaint(
            0.0f, 0.0f, Color.MAGENTA, 
            0.0f, 0.0f, Color.MAGENTA
        );
        final GradientPaint gp3 = new GradientPaint(
                0.0f, 0.0f, Color.yellow, 
                0.0f, 0.0f, Color.yellow
        );
        final GradientPaint gp4 = new GradientPaint(
                0.0f, 0.0f, Color.cyan, 
                0.0f, 0.0f, Color.cyan
        );
        xybarrenderer.setSeriesPaint(0, gp0);
        xybarrenderer.setSeriesPaint(1, gp1);
        xybarrenderer.setSeriesPaint(2, gp2);
        xybarrenderer.setSeriesPaint(3, gp3);
        xybarrenderer.setSeriesPaint(4, gp4);

		
		return jfreechart;
	}

}
