/**
 * 
 */
package inra.ijpb.plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import inra.ijpb.label.LabelImages;
import inra.ijpb.measure.GeometricMeasures2D;
import inra.ijpb.measure.RegionAdjacencyGraph;
import inra.ijpb.measure.RegionAdjacencyGraph.LabelPair;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

/**
 * @author dlegland
 *
 */
public class RegionAdjacencyGraphPlugin implements PlugIn
{

	/* (non-Javadoc)
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	@Override
	public void run(String arg0)
	{
		ImagePlus imagePlus = IJ.getImage();
		
		boolean isPlanar = imagePlus.getStackSize() == 1;
		boolean showRAG = false;
		ImagePlus targetPlus = imagePlus;

		if (isPlanar)
		{
			// create the list of image names
			int[] indices = WindowManager.getIDList();
			String[] imageNames = new String[indices.length];
			for (int i = 0; i < indices.length; i++)
			{
				imageNames[i] = WindowManager.getImage(indices[i]).getTitle();
			}
			
			// name of selected image
			String selectedImageName = IJ.getImage().getTitle();

			GenericDialog gd = new GenericDialog("Create RAG");
			gd.addCheckbox("Show RAG", true);
			gd.addChoice("Image to overaly", imageNames, selectedImageName);
			
			gd.showDialog();
			if (gd.wasCanceled())
			{
				return;
			}
			
			showRAG = gd.getNextBoolean();
			int targetImageIndex = gd.getNextChoiceIndex();
			targetPlus = WindowManager.getImage(indices[targetImageIndex]);
		}

		Set<LabelPair> adjList = RegionAdjacencyGraph.computeAdjacencies(imagePlus);
		
		if (showRAG)
		{
			overlayRAG(adjList, imagePlus, targetPlus);
		}
		
		ResultsTable table = createTable(adjList);
		String newName = imagePlus.getShortTitle() + "-RAG";
		table.show(newName);
	}
	
	private void overlayRAG(Set<LabelPair> adjList, ImagePlus imagePlus, ImagePlus targetPlus)
	{
		IJ.log("display RAG");
		
		// first compute centroids
		ImageProcessor image = imagePlus.getProcessor();
		int[] labels = LabelImages.findAllLabels(image);
		Map<Integer, Integer> labelMap = LabelImages.mapLabelIndices(labels);
		double[][] centroids = GeometricMeasures2D.centroids(image, labels);
		
		// create an overlay for drawing edges
		Overlay overlay = new Overlay();
		
		// iterate over adjacencies to add edges to overlay
		for (LabelPair pair : adjList)
		{
			// first retrieve index in centroid array
			int ind1 = labelMap.get(pair.label1);
			int ind2 = labelMap.get(pair.label2);
			
			// coordinates of edge extremities
			int x1 = (int) centroids[ind1][0];
			int y1 = (int) centroids[ind1][1];
			int x2 = (int) centroids[ind2][0];
			int y2 = (int) centroids[ind2][1];
			
			// draw current edge
			Roi roi = new Line(x1, y1, x2, y2);
			
			roi.setStrokeColor(Color.GREEN);
			roi.setStrokeWidth(2);
			overlay.add(roi);
		}
		
		targetPlus.setOverlay(overlay);
	}
	
	private ResultsTable createTable(Set<LabelPair> adjList)
	{
		ResultsTable table = new ResultsTable();
		table.setPrecision(0);
		
		// populate the table with the list of adjacencies
		for (LabelPair pair : adjList)
		{
			table.incrementCounter();
			table.addValue("Label 1", pair.label1);
			table.addValue("Label 2", pair.label2);
		}
		
		return table;
	}
}
