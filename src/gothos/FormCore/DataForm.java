package gothos.FormCore;

import javax.swing.*;
import java.util.ArrayList;

abstract public class DataForm {

	protected ArrayList<DataFormElement> columns;
	protected JPanel panel;

	public JPanel getPanel() {
		return panel;
	}

	public DataForm(){
		this.columns = new ArrayList<DataFormElement>();


	}

	abstract protected void connectPanel();
	abstract protected void defineColumns();

}
