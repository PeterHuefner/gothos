package gothos;

import gothos.DatabaseCore.DatabaseStructure;
import gothos.DatabaseCore.SqliteConnection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Start {
    public static JFrame mainFrame;
    public static SqliteConnection database;

    private JPanel startPanel;
    private JLabel titleLabel;
    private JList competitionList;
    private JButton createCompetitionButton;
    private JButton loadSelectedCompetitionButton;
    private JButton deleteSelectedCompetitionButton;
    private JLabel selectedDatabaseFileLabel;
    private JButton createDatabaseFileButton;
    private JButton selectDatabaseFileButton;

    public Start() {
        Start.database = null;

        createCompetitionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null, "Hallo Welt");
            }
        });


        selectDatabaseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = findDatabaseDialog();

                int chooserState = chooser.showOpenDialog(Start.mainFrame);
                if(chooserState == JFileChooser.APPROVE_OPTION){
                    connectToDatabase(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        createDatabaseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = findDatabaseDialog();

                int chooserState = chooser.showSaveDialog(Start.mainFrame);
                if(chooserState == JFileChooser.APPROVE_OPTION){
                    String file = chooser.getSelectedFile().getAbsolutePath();

                    if(!file.matches("\\.sqlite3?$")){
                        file += ".sqlite3";
                    }

                    connectToDatabase(file);
                }
            }
        });
    }

    private JFileChooser findDatabaseDialog(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Datenbanken", "sqlite", "sqlite3");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);

        return chooser;
    }

    private boolean connectToDatabase(String file){
        try {
            Start.database = new SqliteConnection(file);
            selectedDatabaseFileLabel.setText("verbundene Datenbank: " + file);
            DatabaseStructure structure = new DatabaseStructure();
            structure.checkDatabase();
        }catch (Exception e){
            System.out.println("keine Verbindung hergestellt");
            JOptionPane.showConfirmDialog(Start.mainFrame, "Datenbank könnte nicht geöffnet werden");
            JOptionPane.showMessageDialog(Start.mainFrame, "Datenbank könnte nicht geöffnet werden");
        }

        return (Start.database != null);
    }

    public static void main(String[] args) {
        Start.mainFrame = new JFrame("Gothos");
        Start.mainFrame.setContentPane(new Start().startPanel);
        Start.mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Start.mainFrame.pack();
        Start.mainFrame.setVisible(true);
        Start.mainFrame.setLocationRelativeTo(null);
    }
}
