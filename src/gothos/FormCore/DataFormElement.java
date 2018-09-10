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
		} else if (this.element.getClass() == javax.swing.JComboBox.class) {
			JComboBox selectBox = (JComboBox) this.element;
			Object selectedItem = selectBox.getSelectedItem();

			if (selectedItem.getClass() == SelectboxItem.class) {
				value = ((SelectboxItem) selectedItem).getKey().toString();
			} else {
				value = selectedItem.toString();
			}
		}

		return value;
	}

	public DataFormElement setValue(String value){

		if(this.element.getClass() == javax.swing.JTextField.class){
			((JTextField) this.element).setText(value);
		} else if (this.element.getClass() == javax.swing.JComboBox.class) {
			JComboBox selectBox = (JComboBox) this.element;

			for (int i = 0; i < selectBox.getItemCount(); i++) {
				Object thisItem = selectBox.getItemAt(i);

				if (thisItem.getClass() == SelectboxItem.class) {
					SelectboxItem selectboxItem = (SelectboxItem) thisItem;
					if (selectboxItem.getKey().toString() == value) {
						selectBox.setSelectedItem(selectboxItem);
						break;
					}
				} else if (thisItem.toString() == value){
					selectBox.setSelectedItem(thisItem);
					break;
				}
			}
		}

		return this;
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
