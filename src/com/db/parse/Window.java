package com.db.parse;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Window extends JFrame implements ActionListener {
	
	private JButton b_Compare;
	private JButton b_ChooseOldFile;
	private JButton b_ChooseNewFile;
	private JButton b_SaveFile;
	private JLabel l_Old;
	private JLabel l_New;
	private JTextField tf_ChooseOldFile;
	private JTextField tf_ChooseNewFile;	
	private JTextArea ta_Result;
	private JScrollPane sp_Result;
	private JTextArea ta_Old;
	private JScrollPane sp_Old;
	private JTextArea ta_New;
	private JScrollPane sp_New;
	
	public Window () {
		super("Tables");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.b_Compare = new JButton("compare");
		this.b_ChooseOldFile = new JButton("...");
		this.b_ChooseNewFile = new JButton("...");
		this.b_SaveFile = new JButton("Save");
		
		this.l_Old = new JLabel("Old: ");
		this.l_New = new JLabel("New: ");

		this.tf_ChooseOldFile = new JTextField(App.newPath, 20);
		this.tf_ChooseNewFile = new JTextField(App.oldPath, 20);
		
		this.ta_Result = new JTextArea();
		this.sp_Result = new JScrollPane(ta_Result);
		this.ta_Result.setEditable(false);
		this.ta_Result.setColumns(81);
		this.ta_Result.setRows(18);
		
		this.ta_Old = new JTextArea();
		this.sp_Old = new JScrollPane(ta_Old);
		this.ta_Old.setColumns(40);
		this.ta_Old.setRows(15);
		
		this.ta_New = new JTextArea();
		this.sp_New = new JScrollPane(ta_New);
		this.ta_New.setColumns(40);
		this.ta_New.setRows(15);
		
		this.setBounds(200, 80, 950, 650);
		this.setResizable(false);
		
		JPanel panelN = new JPanel();
		panelN.setLayout(new FlowLayout());
		panelN.add(l_Old);
		panelN.add(tf_ChooseNewFile);
		panelN.add(b_ChooseNewFile);
		panelN.add(l_New);
		panelN.add(tf_ChooseOldFile);
		panelN.add(b_ChooseOldFile);
		panelN.add(b_Compare);
		panelN.add(sp_Old, BorderLayout.CENTER);
		panelN.add(sp_New, BorderLayout.CENTER);
		panelN.add(sp_Result, BorderLayout.CENTER);
		panelN.add(b_SaveFile);
		
		b_Compare.addActionListener(this);
		b_ChooseOldFile.addActionListener(this);
		b_ChooseNewFile.addActionListener(this);
		b_SaveFile.addActionListener(this);
		
		this.add(panelN);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		Window window = new Window();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == b_Compare) {
			App app;
			if ((!"".equals(ta_Old.getText()))&&(!"".equals(ta_New.getText()))) {
				app = new App();
				try {
					app.main(ta_Old.getText(), ta_New.getText());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else {
				app = new App(tf_ChooseOldFile.getText(), tf_ChooseNewFile.getText());
				try {
					app.main();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
				List<String> listQueries = app.getResult();
				StringBuilder builder = new StringBuilder();
				for(String tempStrings : listQueries) {
					builder.append(tempStrings + "\n");
				}
				ta_Result.setText(builder.toString());
		} else if (e.getSource() == b_ChooseNewFile) {
			JFileChooser fileOpenNew = new JFileChooser();
			fileOpenNew.setCurrentDirectory(new File("C:/Users/Andreas/Desktop"));
			int approve = fileOpenNew.showDialog(null, "Open file");
			if (approve==JFileChooser.APPROVE_OPTION) {
				tf_ChooseNewFile.setText(fileOpenNew.getSelectedFile().getAbsolutePath().replace('\\', '/'));
			}
		} else if (e.getSource() == b_ChooseOldFile) {
			JFileChooser fileOpenOld = new JFileChooser();
			fileOpenOld.setCurrentDirectory(new File("C:/Users/Andreas/Desktop"));
			int approve = fileOpenOld.showDialog(null, "Open file");
			if (approve==JFileChooser.APPROVE_OPTION) {
				tf_ChooseOldFile.setText(fileOpenOld.getSelectedFile().getAbsolutePath().replace('\\', '/'));
			}
		} else if (e.getSource() == b_SaveFile) {
			if (!"".equals(ta_Result.getText())) {
				JFileChooser fileSave = new JFileChooser();
				fileSave.setCurrentDirectory(new File("C:/Users/Andreas/Desktop"));
				int ret3 = fileSave.showSaveDialog(null);
				if (ret3==JFileChooser.APPROVE_OPTION) {
					try{						
							FileWriter fw = new FileWriter(fileSave.getSelectedFile());
						    fw.write(ta_Result.getText().toString());
						    fw.close();
					} catch(IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
