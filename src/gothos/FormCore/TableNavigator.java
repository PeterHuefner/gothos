package gothos.FormCore;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TableNavigator {

	protected JTable table;

	public TableNavigator(JTable table) {
		this.table = table;

		table.addPropertyChangeListener(new EditListener());
	}

	protected void keybindings(){

		/*InputMap inputMap = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("UP"), "up");
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down");

		table.getActionMap().put("up", new ArrowAction("up"));
		table.getActionMap().put("down", new ArrowAction("down"));*/


		if(table.getEditorComponent() != null){
			JTextField textField = (JTextField) table.getEditorComponent();

			textField.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
			textField.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");

			textField.getActionMap().put("up", new ArrowAction("up"));
			textField.getActionMap().put("down", new ArrowAction("down"));
		}
	}

	class ArrowAction extends AbstractAction {
		protected String direction;
		public ArrowAction(String direction){
			this.direction = direction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			Integer column = table.getEditingColumn();
			Integer row    = table.getEditingRow();

			Integer newColumn = -1;
			Integer newRow    = -1;

			if(direction.equals("down")){
				if((row + 1) < table.getRowCount()){
					newRow = row + 1;
				}else{
					newRow = 0;
				}
				newColumn = column;
			}else if(direction.equals("up")){
				if(row > 0){
					newRow = row - 1;
				}else{
					newRow = table.getRowCount() - 1;
				}
				newColumn = column;
			}

			if(newColumn >= 0 && newRow >= 0 && table.editCellAt(newRow, newColumn)){
				table.setRowSelectionInterval(newRow, newRow);
				table.requestFocus();
				table.getEditorComponent().requestFocusInWindow();
			}
		}
	}

	class EditListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("tableCellEditor")){
				if(table.isEditing()){
					keybindings();
				}else{

				}
			}
		}
	}
}


