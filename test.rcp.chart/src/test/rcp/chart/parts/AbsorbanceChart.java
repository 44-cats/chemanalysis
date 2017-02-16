package test.rcp.chart.parts;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.IDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.opencsv.CSVWriter;

import test.rcp.chart.serial.Serial;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.widgets.Combo;

public class AbsorbanceChart {

	final String MAIN_PERSPECTIVE_STACK_ID = "MainPerspectiveStack";
	private static final int CARRIAGE_RETURN_CODE = 13;

	private final AtomicBoolean onSerialMode = new AtomicBoolean(false);
	private Trace serialTrace;
	private Map<String, Trace> demoTrace = new HashMap<String, Trace>(5);
	private XYGraph xyGraph;
	private String portName = "";
	private Serial serialPort;

	private Button btnDemoMode;

	private Button btnExportCsv;

	private Button getDataBtn;
	private Button btnStop;
	private Button btnExportCsvS;
	private Button btnExportCsvD;
	private Button btnIntensity;
	private Composite composite;
	private Button btnModeA;
	private Button btnModeT;
	private Button btnRunc;
	private Button btnRunk;
	private Composite composite_1;
	private Button btnSettings;

	private int intTime = -1;
	private boolean demoMode = false;

	private IPerspectiveDescriptor demo;
	private IPerspectiveDescriptor chart;
	
	private static AbsorbanceChart instance;
	public static AbsorbanceChart getInstance() {
		return instance;
	}

	public AbsorbanceChart() {
	}

	@PostConstruct
	public void createComposite(Composite parent) {
		instance = this;
		demo = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("test.rcp.perspective.demo");
		chart = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId("test.rcp.perspective");
//		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//
//		if (modelService == null) {
//			modelService = activeWorkbenchWindow.getService(EModelService.class);
//		}
//		if (partService == null) {
//			partService = activeWorkbenchWindow.getService(EPartService.class);
//		}
//		if (application == null) {
//			application = activeWorkbenchWindow.getService(MApplication.class);
//		}
		parent.setLayout(new GridLayout(5, false));

		composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		GridLayout gl_composite = new GridLayout(14, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		getDataBtn = new Button(composite, SWT.PUSH);
		getDataBtn.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Play-48.png"));
		getDataBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleGetData();
			}
		});

		Button btnGetDataC = new Button(composite, SWT.NONE);
		btnGetDataC.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(new Shell(), "", "Only single spectrum acquisition available in version 1.1. Contact support@analyzechemicals.com for a full access version");
			}
		});
		btnGetDataC.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Play-48 c.png"));

		btnStop = new Button(composite, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(MessageDialog.openQuestion(new Shell(), "", "Stop all data acquisitions?")){
					//TODO
				}
			}
		});
		btnStop.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Stop-48.png"));

		btnExportCsv = new Button(composite, SWT.NONE);
		btnExportCsv.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Save-48(1).png"));
		btnExportCsv.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCsvExport();
			}
		});

		btnExportCsvS = new Button(composite, SWT.NONE);
		btnExportCsvS.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Save-48(2).png"));
		btnExportCsvS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCsvExport();
			}
		});

		btnExportCsvD = new Button(composite, SWT.NONE);
		btnExportCsvD.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Save-48.png"));
		btnExportCsvD.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleCsvExport();
			}
		});

		btnIntensity = new Button(composite, SWT.NONE);
		btnIntensity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(MessageDialog.openConfirm(new Shell(), "", "Switch to Intensity Mode?")){
					xyGraph.primaryXAxis.setTitle("Pixels");
					xyGraph.primaryYAxis.setTitle("Intensity (counts)");
				}
			}
		});
		btnIntensity.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/System Task-48.png"));

		btnModeA = new Button(composite, SWT.NONE);
		btnModeA.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(new Shell(), "", "Only intensity mode is available in version 1.1. Contact support@analyzechemicals.com for a full access version.");
			}
		});
		btnModeA.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/System Task-48 A.png"));

		btnModeT = new Button(composite, SWT.NONE);
		btnModeT.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(new Shell(), "", "Only intensity mode is available in version 1.1. Contact support@analyzechemicals.com for a full access version.");
			}
		});
		btnModeT.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/System Task-48(1).png"));

		btnRunc = new Button(composite, SWT.NONE);
		btnRunc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(new Shell(), "", "Only intensity mode is available in version 1.1. Contact support@analyzechemicals.com for a full access version.");
			}
		});
		btnRunc.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Run Command-48(1).png"));

		btnRunk = new Button(composite, SWT.NONE);
		btnRunk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageDialog.openInformation(new Shell(), "", "Only intensity mode is available in version 1.1. Contact support@analyzechemicals.com for a full access version.");
			}
		});
		btnRunk.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Run Command-48.png"));
		
		btnSettings = new Button(composite, SWT.NONE);
		btnSettings.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				openSettings();
			}
		});
		btnSettings.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Settings 3 Filled-48.png"));


		btnDemoMode = new Button(composite, SWT.TOGGLE);
		btnDemoMode.setImage(ResourceManager.getPluginImage("test.rcp.chart", "icons/Open Folder-48.png"));
		new Label(composite, SWT.NONE);
		btnDemoMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				demoMode = btnDemoMode.getSelection();
				handleDemoMode();
			}

		});
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		Canvas canvas = new Canvas(parent, SWT.NONE);
		GridData gd_canvas = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_canvas.horizontalSpan = 5;
		canvas.setLayoutData(gd_canvas);
		final LightweightSystem lws = new LightweightSystem(canvas);

		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph();
		lws.setContents(toolbarArmedXYGraph);

		xyGraph = toolbarArmedXYGraph.getXYGraph();
		xyGraph.setShowTitle(false);
		xyGraph.primaryXAxis.setTitle("");
		xyGraph.primaryYAxis.setTitle("");
		xyGraph.primaryYAxis.setShowMajorGrid(true);
		xyGraph.setShowLegend(false);

	}
	
	public boolean isDemoMode() {
		return demoMode;
	}
	
	public void setDemoMode(boolean demoMode) {
		this.demoMode = demoMode;
		btnDemoMode.setSelection(demoMode);
	}
	
	public void handleDemoMode(){
		demoModeUI();

		if (demoMode) {
			if (demo != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(demo);
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.navigator.ProjectExplorer");
			} catch (PartInitException e1) {
				e1.printStackTrace();
			}

			onSerialMode.set(false);
			if (serialTrace != null) {
				xyGraph.removeTrace(serialTrace);
				serialTrace = null;
			}
		} else {
			if (chart != null)
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(chart);
			for(IViewReference v : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences()){
				if(v.getId().equals("org.eclipse.ui.navigator.ProjectExplorer"))
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(v);
			}

			for (Trace t : demoTrace.values()) {
				xyGraph.removeTrace(t);
			}
			demoTrace.clear();
		}
	}

	public void handleCsvExport() {
		// no export if no data
		if (serialTrace == null) {
			MessageDialog.openInformation(new Shell(), "No data", "No data to export");
			return;
		}

		FileDialog dialog = new FileDialog(new Shell(), SWT.SAVE);
		dialog.setFilterNames(new String[] { "CSV files" });
		dialog.setFilterExtensions(new String[] { "*.csv" }); // Windows
		String filename = dialog.open();
		if (filename != null) {
			// append csv if needed
			if (!filename.toLowerCase().endsWith(".csv")) {
				filename = filename + ".csv";
			}

			IDataProvider dataProvider = serialTrace.getDataProvider();
			try {
				CSVWriter writer = new CSVWriter(new FileWriter(filename), ',');
				for (int i = 0; i < dataProvider.getSize(); i++) {
					ISample sample = dataProvider.getSample(i);
					String[] entries = new String[] { sample.getXValue() + "", sample.getYValue() + "" };
					writer.writeNext(entries, false);
				}
				writer.close();
			} catch (Exception e) {
				// TODO: use legitimate E4 logger
				e.printStackTrace();
			}
		}
	}

	private void demoModeUI() {
		for(Control c : composite.getChildren()){
			if(c != btnDemoMode)
				c.setEnabled(!btnDemoMode.getSelection());
		}
		
	}

	public void loadFromCSV(String name, double[] x, double[] y) {
		btnDemoMode.setSelection(true);
		demoModeUI();
		onSerialMode.set(false);
		if (serialTrace != null) {
			xyGraph.removeTrace(serialTrace);
			serialTrace = null;
		}

		if (demoTrace.containsKey(name) && demoTrace.get(name) != null) {

			xyGraph.removeTrace(demoTrace.get(name));
		}

		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(x.length);
		traceDataProvider.setCurrentXDataArray(x);
		traceDataProvider.setCurrentYDataArray(y);

		Trace trace = new Trace(name, xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);

		demoTrace.put(name, trace);

		// add the trace to xyGraph
		xyGraph.addTrace(trace);
		xyGraph.performAutoScale();

	}

	public void handleGetData() {

		for (Trace trace : demoTrace.values()) {
			xyGraph.removeTrace(trace);

		}
		demoTrace.clear();

		if (serialTrace != null) {
			xyGraph.removeTrace(serialTrace);
		}

		if (serialPort == null) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Please select a valid Port.");
			return;
		}
		
		if (intTime < 0) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Please select a valid integration time.");
			return;
		}

		serialPort.clear(); // Clear any pending serial data

		serialPort.write(""+intTime); // Write out integration time
		serialPort.write(CARRIAGE_RETURN_CODE); // Start transfer

		double[] x = new double[3700];
		double[] y = new double[3700];
		// Loop for 3700 pixels from CCD
		for (int i = 0; i < 3700; i++) {
			x[i] = i;
			y[i] =  1/((getPixel() - 10000) / 10000.0d); //Get and normalize pixel
		}

		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(3700);
		traceDataProvider.setCurrentXDataArray(x);
		traceDataProvider.setCurrentYDataArray(y);

		serialTrace = new Trace("Absorbance", xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);

		// add the trace to xyGraph
		xyGraph.addTrace(serialTrace);
		xyGraph.performAutoScale();

	}

	// Get one pixel value from serial port
	public int getPixel() {

		String inStr1 = "0"; // Holds the serial input string

		while (serialPort != null && serialPort.available() < 0) // Wait until
			// there is
			// something
			// in
			// buffer
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		do {
			if (serialPort != null)
				inStr1 = serialPort.readStringUntil(CARRIAGE_RETURN_CODE);
		} while (inStr1 == null);

		return (Integer.parseInt(inStr1.trim()));

	}
	
	private void openSettings(){
		SettingDialog dlg = new SettingDialog(new Shell());
		if(dlg.open()!=Window.OK)
			return;
		
		onSerialMode.set(false);
		// clean if already have a selected one
		if (serialPort != null)
			serialPort.dispose();

		if (serialTrace != null) {
			xyGraph.removeTrace(serialTrace);
		}
		for (Trace t : demoTrace.values()) {
			xyGraph.removeTrace(t);
		}
		demoTrace.clear();

		portName = dlg.getPortName();
		intTime  = dlg.getIntegrationTime();
		serialPort = new Serial(portName, 57600);
		onSerialMode.set(true);
	}

	@Focus
	public void setFocus() {
	}

	@PreDestroy
	public void dispose() {
		if (serialPort != null)
			serialPort.dispose();
	}
}