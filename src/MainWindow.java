import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import static information.StockIndexes.MSFT;
import static information.StockIndexes.AAPL;
import static information.StockIndexes.AMZN;
import static information.StockIndexes.FB;
import static information.StockIndexes.GOOG;
import static information.StockIndexes.IBM;
import static information.StockIndexes.INTC;
import static information.StockIndexes.YHOO;
import static information.StockIndexes.TWTR;
import static information.StockIndexes.NVDA;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;






public class MainWindow {

	private JFrame frame;
	
	
	String[] stockIndexes = {AAPL, AMZN, FB, GOOG, IBM, INTC, MSFT, NVDA, TWTR, YHOO };
	
	JComboBox comboBox = null;
	
	JLabel infoLabel = null;
	
	JLabel infoClassLabel = null;
	
	JButton classifyButton = null;
	
	JButton saveButton = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel lblChooseIndexOf = new JLabel("Choose index of a stock you wish to classify or save new data into database:");
		GridBagConstraints gbc_lblChooseIndexOf = new GridBagConstraints();
		gbc_lblChooseIndexOf.gridwidth = 21;
		gbc_lblChooseIndexOf.insets = new Insets(0, 0, 5, 5);
		gbc_lblChooseIndexOf.gridx = 1;
		gbc_lblChooseIndexOf.gridy = 2;
		frame.getContentPane().add(lblChooseIndexOf, gbc_lblChooseIndexOf);
		
		classifyButton = new JButton("Classify");
		classifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoLabel.setText("Please wait");
				infoClassLabel.setText("");
				
				classifyButton.setEnabled(false);
				saveButton.setEnabled(false);
				
				String stockIndex = comboBox.getSelectedItem().toString();
				
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						
						try {
							Runner.StockInfo classification = Runner.classify(stockIndex);
							infoLabel.setText("Stock is in reality " + (classification.isIncreasing ? " rising " : " faling ") + ", while my classifier got the stock"
									+ " is " + (classification.classified ? "rising." : "falling."));
							
							if (classification.isIncreasing == classification.classified) {
								infoClassLabel.setText("CORRECT CLASSIFICATION");
								infoClassLabel.setForeground(Color.GREEN);
							}
							else {
								infoClassLabel.setText("WRONG CLASSIFICATION");
								infoClassLabel.setForeground(Color.RED);
							}
							
							classifyButton.setEnabled(true);
							saveButton.setEnabled(true);
						} catch (ParserConfigurationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (SAXException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						
					}
					
				};
				
				Thread fetchDataThread = new Thread(runnable);
				
				fetchDataThread.start();
				
			
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridheight = 5;
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 8, 5);
		gbc_btnNewButton.gridx = 18;
		gbc_btnNewButton.gridy = 1;
		frame.getContentPane().add(classifyButton, gbc_btnNewButton);
		
		saveButton = new JButton("Save into database");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoLabel.setText("Please wait");
				infoClassLabel.setText("");
				
				classifyButton.setEnabled(false);
				saveButton.setEnabled(false);
				
				String stockIndex = comboBox.getSelectedItem().toString();
				
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						try {
							Runner.saveStuffIntoDatabase(stockIndex);
						} catch (ParserConfigurationException | SAXException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						infoLabel.setText("Data saved into database");
						classifyButton.setEnabled(true);
						saveButton.setEnabled(true);
						
					}
					
				};
				
				Thread thread = new Thread(runnable);
				
				thread.start();
			}
		});
		
		comboBox = new JComboBox(stockIndexes);
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.gridwidth = 13;
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 4;
		gbc_comboBox.gridy = 3;
		frame.getContentPane().add(comboBox, gbc_comboBox);
		
		infoLabel = new JLabel("sdfsdfsdf");
		GridBagConstraints gbc_infoLabel = new GridBagConstraints();
		gbc_infoLabel.anchor = GridBagConstraints.SOUTHWEST;
		gbc_infoLabel.insets = new Insets(0, 0, 5, 5);
		gbc_infoLabel.gridwidth = 15;
		gbc_infoLabel.gridx = 5;
		gbc_infoLabel.gridy = 10;
		frame.getContentPane().add(infoLabel, gbc_infoLabel);
		saveButton.setBackground(Color.LIGHT_GRAY);
		saveButton.setForeground(Color.RED);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 19;
		gbc_btnNewButton_1.gridy = 5;
		frame.getContentPane().add(saveButton, gbc_btnNewButton_1);
		
		infoClassLabel = new JLabel("sfdf");
		infoClassLabel.setFont(new Font("Skia", Font.BOLD, 21));
		GridBagConstraints gbc_infoClassLabel = new GridBagConstraints();
		gbc_infoClassLabel.anchor = GridBagConstraints.WEST;
		gbc_infoClassLabel.insets = new Insets(0, 0, 5, 5);
		gbc_infoClassLabel.gridheight = 3;
		gbc_infoClassLabel.gridwidth = 15;
		gbc_infoClassLabel.gridx = 5;
		gbc_infoClassLabel.gridy = 11;
		frame.getContentPane().add(infoClassLabel, gbc_infoClassLabel);
		
		JButton btnAboutApp = new JButton("About app");
		btnAboutApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "App by Nenad Banfic");
			}
		});
		GridBagConstraints gbc_btnAboutApp = new GridBagConstraints();
		gbc_btnAboutApp.insets = new Insets(0, 0, 5, 5);
		gbc_btnAboutApp.gridx = 19;
		gbc_btnAboutApp.gridy = 14;
		frame.getContentPane().add(btnAboutApp, gbc_btnAboutApp);
		
		
	}

}
