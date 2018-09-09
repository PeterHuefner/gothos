package gothos;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Common {

	public static final String tableNameReqex = "^[a-zA-Z]{1}[a-zA-Z0-9_]*$";

	public static boolean emptyString(String string){
		return (string == null || string.isEmpty() || string.equalsIgnoreCase("null") || string.equalsIgnoreCase("0"));
	}

	public static void systemoutprintln(String string) {
		System.out.println(string);
	}

	public static void printError(String error){
		System.out.println(error);
		JOptionPane.showMessageDialog(WindowManager.mainFrame, error);
	}

	public static void printError(Exception error){
		Common.printError(error.getMessage() + "\n" + error.toString());
	}

	public static void showFormError(String error){
		JOptionPane.showMessageDialog((WindowManager.childFrame != null ? WindowManager.childFrame : WindowManager.mainFrame), error);
	}

	public static void showError(String error){
		JOptionPane.showMessageDialog((WindowManager.childFrame != null ? WindowManager.childFrame : WindowManager.mainFrame), error);
	}

	public static String objectToString(Object object){
		if(object.getClass() == Boolean.class){
			return (((Boolean) object).booleanValue() ? "1" : "0");
		}else if(object.getClass() == Integer.class){
			return Integer.toString((Integer) object);
		}else if(object.getClass() == Double.class){
			return Double.toString((Double) object);
		}else if(object.getClass() == Float.class){
			return Float.toString((Float) object);
		}else if(object.getClass() == Long.class){
			return Long.toString((Long) object);
		}else{
			return (String) object;
		}
	}

	public static void addTableNavigationEvents(JTable tableElement){

		/*tableElement.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);

				int selectedRow = tableElement.getSelectedRow();
				int selectedColumn = tableElement.getSelectedColumn();
				Boolean editing = tableElement.isEditing();

				if(selectedColumn == -1){
					selectedColumn = 0;
				}

				if(selectedRow >= 0){

					if(e.getKeyCode() == KeyEvent.VK_UP && selectedRow > 0){
						tableElement.editCellAt(selectedRow - 1, selectedColumn);
					}else if(e.getKeyCode() == KeyEvent.VK_DOWN && selectedRow < (tableElement.getRowCount() - 1)){
						tableElement.editCellAt(selectedRow + 1, selectedColumn);
					}
				}

			}
		});*/
	}

	public static double round(double value, int places) {
		return round(value, places, RoundingMode.HALF_UP);
	}

	public static double round(double value, int places, RoundingMode mode) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, mode);
		return bd.doubleValue();
	}
}
