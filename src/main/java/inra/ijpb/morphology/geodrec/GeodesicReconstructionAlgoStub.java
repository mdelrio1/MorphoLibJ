/**
 * 
 */
package inra.ijpb.morphology.geodrec;

import inra.ijpb.algo.AlgoStub;

/**
 * Implementation basis for geodesic reconstruction algorithms for planar images.
 * 
 * @author dlegland
 *
 */
public abstract class GeodesicReconstructionAlgoStub extends AlgoStub implements
		GeodesicReconstructionAlgo
{
	/**
	 * The connectivity of the algorithm, either 4 or 8.
	 */
	protected int connectivity = 4;
	
	/**
	 * Boolean flag for the display of debugging infos.
	 */
	public boolean verbose = false;
	
	/**
	 * Boolean flag for the display of algorithm state in ImageJ status bar
	 */
	public boolean showStatus = true;
	
	/**
	 * Boolean flag for the display of algorithm progress in ImageJ status bar
	 */
	public boolean showProgress = false; 

	
	/* (non-Javadoc)
	 * @see inra.ijpb.morphology.geodrec.GeodesicReconstructionAlgo#getConnectivity()
	 */
	@Override
	public int getConnectivity()
	{
		return this.connectivity;
	}

	/* (non-Javadoc)
	 * @see inra.ijpb.morphology.geodrec.GeodesicReconstructionAlgo#setConnectivity(int)
	 */
	@Override
	public void setConnectivity(int conn)
	{
		this.connectivity = conn;
	}

}
