package test.rcp.chart.parts;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import test.rcp.chart.serial.Serial;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

public class SettingDialog extends Dialog {

	private String portName;
	private Spinner spnIntTime;
	protected int intTime;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SettingDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 20;
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginHeight = 30;
		container.setLayout(gl_container);
		String[] serialPorts = Serial.list();

		Composite cmpSettings = new Composite(container, SWT.NONE);
		GridLayout gl_cmpSettings = new GridLayout(5, false);
		cmpSettings.setLayout(gl_cmpSettings);
		cmpSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));

		Label lblPort = new Label(cmpSettings, SWT.NONE);
		lblPort.setSize(22, 15);
		lblPort.setText("Port:");

		ComboViewer ports = new ComboViewer(cmpSettings);
		Combo combo = ports.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		combo.setSize(106, 23);
		ports.setContentProvider(ArrayContentProvider.getInstance());
		ports.setInput(serialPorts);

		// enable if need to auto load first port automatically
		// if (serialPorts.length > 0) {
		// try {
		// String portName = serialPorts[0];
		// serialPort = new Serial(portName, 57600);
		// ports.setSelection(new StructuredSelection(portName));
		// } catch (Throwable e) {
		// e.printStackTrace();
		// }
		// }
		ports.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				if (selection.getFirstElement() != null) {
					portName = (String) selection.getFirstElement();
				}

			}
		});

		Label lblIntegrationTimems = new Label(cmpSettings, SWT.NONE);
		lblIntegrationTimems.setText("Integration Time (ms):");

		spnIntTime = new Spinner(cmpSettings, SWT.BORDER);
		spnIntTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));
		spnIntTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				intTime = spnIntTime.getSelection();
			}
		});
		spnIntTime.setMaximum(Integer.MAX_VALUE);

		Label lblNumofscan = new Label(cmpSettings, SWT.NONE);
		lblNumofscan.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblNumofscan.setBounds(0, 0, 55, 15);
		lblNumofscan.setText("Number of scans to average:");

		text = new Text(cmpSettings, SWT.BORDER);
		text.setEnabled(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));

		Label lblXAxisType = new Label(cmpSettings, SWT.NONE);
		lblXAxisType.setText("X-Axis Type:");

		Combo cmbXAxisType = new Combo(cmpSettings, SWT.READ_ONLY);
		cmbXAxisType.setEnabled(false);
		cmbXAxisType.setItems(new String[] {"Wavelength (nm)", "Microns", "Wavenumber (in cm-1)", "Pixel Number"});
		GridData gd_cmbXAxisType = new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1);
		gd_cmbXAxisType.widthHint = 150;
		cmbXAxisType.setLayoutData(gd_cmbXAxisType);

		Label lblWavelength = new Label(cmpSettings, SWT.NONE);
		lblWavelength.setText("Wavelength Range:");

		text_1 = new Text(cmpSettings, SWT.BORDER);
		text_1.setEnabled(false);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblTo = new Label(cmpSettings, SWT.NONE);
		lblTo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTo.setText("To");

		text_2 = new Text(cmpSettings, SWT.BORDER);
		text_2.setEnabled(false);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button btnUploadWavelengthCalibration = new Button(cmpSettings, SWT.NONE);
		btnUploadWavelengthCalibration.setEnabled(false);
		btnUploadWavelengthCalibration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnUploadWavelengthCalibration.setText("Upload");

		Label lblSamplingRatemins = new Label(cmpSettings, SWT.NONE);
		lblSamplingRatemins.setText("Sampling Rate (Mins):");

		text_3 = new Text(cmpSettings, SWT.BORDER);
		text_3.setEnabled(false);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	public String getPortName() {
		return portName;
	}

	public int getIntegrationTime() {
		return intTime;
	}
}
