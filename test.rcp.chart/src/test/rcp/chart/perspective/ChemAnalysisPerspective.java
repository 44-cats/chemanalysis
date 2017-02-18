package test.rcp.chart.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import test.rcp.chart.views.AbsorbanceChartView;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class ChemAnalysisPerspective implements IPerspectiveFactory {

	private IPageLayout factory;

	public ChemAnalysisPerspective() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
	}

	private void addViews() {
		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining
		// EditorArea.
		factory.setEditorAreaVisible(true);
		IFolderLayout topLeft = factory.createFolder("topLeft", // NON-NLS-1
				IPageLayout.LEFT, 0.25f, factory.getEditorArea());
		topLeft.addPlaceholder("org.eclipse.ui.navigator.ProjectExplorer");
	}

	private void addActionSets() {

		factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); // NON-NLS-1
	}

	private void addPerspectiveShortcuts() {

	}

	private void addNewWizardShortcuts() {

	}

	private void addViewShortcuts() {

		factory.addShowViewShortcut(AbsorbanceChartView.ID);
		factory.addShowViewShortcut("org.eclipse.ui.navigator.ProjectExplorer");
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
	}

}
