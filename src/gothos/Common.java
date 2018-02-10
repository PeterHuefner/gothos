package gothos;

import javax.swing.*;

public class Common {

    public static void printError(String error){
        System.out.println(error);
        JOptionPane.showMessageDialog(WindowManager.mainFrame, error);
    }

    public static void printError(Exception error){
        Common.printError(error.getMessage());
    }

    public static void showFormError(String error){
        JOptionPane.showMessageDialog(WindowManager.mainFrame, error);
    }
}
