package gothos.FormCore;

import javax.swing.*;

public class DataFormElement {

	protected Object element;
	protected String name;
	protected String table;

	public Object getElement() {
		return element;
	}

	public String getName() {
		return name;
	}

	public String getTable() {
		return table;
	}

	public void setElement(Object element) {
		this.element = element;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getValue(){
		String value = null;
		if(this.element.getClass() == javax.swing.JTextField.class){
			value = ((JTextField) this.element).getText();
		}

		return value;
	}

	public DataFormElement(Object element, String name) {
		this.element = element;
		this.name = name;
	}

	public DataFormElement(Object element, String name, String table) {
		this.element = element;
		this.name = name;
		this.table = table;
	}
}
