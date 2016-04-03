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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.freehep.util.export.ExportDialog;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.ui.ApplicationFrame;

public class Histogram extends ApplicationFrame {

	JFrame chartFrame;
	String metaLabel;
	JPanel histogramPanel, buttonsPanel;
	JButton exportButton, closeButton;
	
	public Histogram(final String s, List<List<String>> list, Character meta)
	{
		super(s);
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
				export.showExportDialog(chartFrame, "Export view as ...",histogramPanel, "Histogram of the " + metaLabel + " values for all samples - " + s);
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
	
	public static JPanel createDemoPanel(String s, List<List<String>> collection, String metaLabel){
		JFreeChart jfreechart = createChart(s,createDataset(collection,metaLabel),metaLabel);
		return new ChartPanel(jfreechart);
	}

	private static IntervalXYDataset createDataset(List<List<String>> betaValues, String metaLabel){

		HistogramDataset histogramdataset = new HistogramDataset();
		double ad[] = new double[betaValues.get(0).size()];
		
		for (int i=0; i< betaValues.get(0).size(); i++){
			for (int j=0; j < betaValues.size(); j++ ){
				ad[i] += Double.parseDouble(betaValues.get(j).get(i));
			}
			ad[i] /= betaValues.size();
		}
		
		if (metaLabel.equals("beta")){
			histogramdataset.addSeries( metaLabel + " values", ad, 50, 0.00, 0.99);
		}
		else {
			histogramdataset.addSeries(metaLabel + " values", ad, 50);
		}
		
		return histogramdataset;
	}

	private static JFreeChart createChart(String s, IntervalXYDataset intervalxydataset, String metaLabel)
	{
		JFreeChart jfreechart = ChartFactory.createHistogram("Histogram of the " + metaLabel + " values for all samples - " + s, null, null, intervalxydataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot)jfreechart.getPlot();
		xyplot.setForegroundAlpha(0.85F);
		XYBarRenderer xybarrenderer = (XYBarRenderer)xyplot.getRenderer();
		xybarrenderer.setDrawBarOutline(false);
		return jfreechart;
	}

}
