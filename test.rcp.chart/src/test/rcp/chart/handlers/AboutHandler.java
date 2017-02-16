package test.rcp.chart.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class AboutHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MessageDialog.openInformation(new Shell(), "About", ""
				+ "Go to www.AnalyzeChemicals.com for documentation on ChemAnalysis Workbench v1.1"
				+ "\n"
				+ "\n"
				+ "Copyright (C) 2017"
				+ "\n"
				+ "This program is free software: you can redistribute it and/or modify"
				+ "it under the terms of the GNU General Public License as published by"
				+ "the Free Software Foundation, either version 3 of the License, or"
				+ "(at your option) any later version."
				+ "\n"
				+ "This program is distributed in the hope that it will be useful,"
				+ "but WITHOUT ANY WARRANTY; without even the implied warranty of"
				+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the"
				+ "GNU General Public License for more details."
				+ "\n"
				+ "You should have received a copy of the GNU General Public License"
				+ "along with this program.  If not, see <http://www.gnu.org/licenses/>.");
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {

	}
}
