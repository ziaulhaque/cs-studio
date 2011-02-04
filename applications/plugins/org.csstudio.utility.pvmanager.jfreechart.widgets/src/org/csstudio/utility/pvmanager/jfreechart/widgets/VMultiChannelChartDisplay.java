/**
 * 
 */
package org.csstudio.utility.pvmanager.jfreechart.widgets;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.epics.pvmanager.data.VDouble;
import org.epics.pvmanager.data.VMultiDouble;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;

/**
 * @author shroffk
 * 
 */
public class VMultiChannelChartDisplay extends Canvas {

	public static final int DOMAIN_AXIS_TYPE_AUTO = 0;
	public static final int DOMAIN_AXIS_TYPE_POSITION = 1;
	private ChartComposite chartDisplay;
	private JFreeChart chart;
	private XYSeriesCollection dataset;

	private List<String> channelNames;
	private List<Double> positions;
	private int domainAxisType;

	/**
	 * 
	 * @param parent
	 * @param style
	 */
	public VMultiChannelChartDisplay(Composite parent, int style) {
		this(parent, style, 0);
	}

	/**
	 * Creates a new display for data of type VMultiDouble
	 * 
	 * @param parent
	 * @param style
	 * @param domainAxisType
	 */
	public VMultiChannelChartDisplay(Composite parent, int style,
			int domainAxisType) {
		super(parent, style);
		this.domainAxisType = domainAxisType;
		setLayout(new FormLayout());
		chartDisplay = new ChartComposite(this, SWT.NONE, null, false, false,
				false, false, true);
		FormData fd_chartDisplay = new FormData();
		fd_chartDisplay.bottom = new FormAttachment(100);
		fd_chartDisplay.right = new FormAttachment(100);
		fd_chartDisplay.top = new FormAttachment(0);
		fd_chartDisplay.left = new FormAttachment(0);
		chartDisplay.setLayoutData(fd_chartDisplay);
	}

	public void setPVData(List<VMultiDouble> pvdata) {
		if (pvdata != null) {
			if (chartDisplay.getChart() == null) {
				dataset = new XYSeriesCollection();
				chart = ChartFactory.createXYLineChart("Title", "X Axis",
						"Y Axis", dataset, PlotOrientation.VERTICAL, true,
						true, true);
				chartDisplay.setChart(chart);
				chart.getXYPlot().setDomainCrosshairVisible(false);
				chart.getXYPlot().setDomainGridlinesVisible(false);
				chart.getXYPlot().setRangeCrosshairVisible(false);
				chart.getXYPlot().setRangeGridlinesVisible(false);
				// customizing the axis greatly reduces performance
//				ValueAxis axis;
//				if (domainAxisType == DOMAIN_AXIS_TYPE_POSITION){
//					axis = chart.getXYPlot().getDomainAxis();
//					axis.setLabel("Channel Position.");
//					axis.setVerticalTickLabels(true);
//				} else {
//					axis = new SymbolAxis("Channel Names",
//							this.channelNames.toArray(new String[channelNames.size()]));
//					axis.setVerticalTickLabels(true);
//					chart.getXYPlot().setDomainAxis(axis);
//				}
				
			} else {
				chartDisplay.setRedraw(false);
				dataset.removeAllSeries();
				for (VMultiDouble vMultiDouble : pvdata) {
					// long startTime = System.nanoTime();
					XYSeries series = createXYSeries("seriesName", vMultiDouble);
					// System.out.println("dataset created in "
					// + (System.nanoTime() - startTime) + " ns.");
					dataset.addSeries(series);
				}
				chartDisplay.setRedraw(true);
				chartDisplay.redraw();
			}
		} else {
			throw new IllegalArgumentException("pvdata cannot be null");
		}
	}

	private XYSeries createXYSeries(String string, VMultiDouble vMultiDouble) {
		XYSeries series = new XYSeries(vMultiDouble.toString(), false, false);
		switch (this.domainAxisType) {
		case DOMAIN_AXIS_TYPE_POSITION:
			// A strong one-to-one relation is expected between the list
			// of positions and the pv values
			if (this.positions == null
					|| vMultiDouble.getValues().size() != this.positions.size()) {
				this.domainAxisType = DOMAIN_AXIS_TYPE_AUTO;
			} else if (this.positions.size() == vMultiDouble.getValues().size()) {
				List<VDouble> values = vMultiDouble.getValues();
				for (int index = 0; index < values.size(); index++) {
					series.add((double) this.positions.get(index),
							(double) values.get(index).getValue());
				}
			}
			break;
		default:
			double autoIndex = 0;
			for (VDouble value : vMultiDouble.getValues()) {
				if (value != null)
					series.add(autoIndex, (double) value.getValue(), false);
				autoIndex++;
			}
			break;
		}
		return series;
	}

	/**
	 * list which is a union of all the channel Names connected to via the
	 * multidouble pv
	 * 
	 * @param channelNames
	 */
	public void setNames(List<String> channelNames) {
		this.channelNames = channelNames;
	}

	/**
	 * specify the position for the channels along the domain axis
	 * 
	 * @param positions
	 *            null permitted
	 */
	public void setPositions(List<Double> positions) {
		this.positions = positions;
	}

	public void setPlotUsing(int plotUsing) {
		this.domainAxisType = plotUsing;
	}

	public void clear() {
		this.positions = null;
		this.channelNames = null;
		this.chartDisplay.setChart(null);
	}

	public void addListener(EventListener listener) {
		if (this.chartDisplay != null) {
			this.chartDisplay.addSWTListener(listener);
		}
	}

	public void addChartListener(EventListener listener) {
		this.chartDisplay.addChartMouseListener((ChartMouseListener) listener);
	}
}