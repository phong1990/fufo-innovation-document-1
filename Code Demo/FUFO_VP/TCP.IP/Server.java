package Server;


import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

	public class Server extends JFrame implements ActionListener{
		/**
		 * 
		 */
		//private static final long serialVersionUID = 1L;
		ServerSocket srvr;
        Socket skt ;
        
        BufferedReader in;
        PrintWriter out ;
		String text= null;
		JButton jbtnStart;
		JTextField jtfInput;
		JTextArea jtAreaOutput;
		JButton jbtnEnter;
	
		StringBuffer toAppend = new StringBuffer("");
		StringBuffer toSend = new StringBuffer("");
		
		public Server() {
			createGui();
		}
		public void createGui() {
			jbtnStart= new JButton("Start");
			 
			jtfInput = new JTextField(50);
			jbtnEnter = new JButton("Enter");
			jtfInput.addActionListener(this);
			jtAreaOutput = new JTextArea(5, 20);
			jtAreaOutput.setCaretPosition(jtAreaOutput.getDocument()
					.getLength());
			jtAreaOutput.setEditable(false);
			
			JScrollPane scrollPane = new JScrollPane(jtAreaOutput,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
			
			// tao bang Grid
			GridBagLayout gridBag = new GridBagLayout();
			Container contentPane = getContentPane();
			contentPane.setLayout(gridBag);
			GridBagConstraints gridCons1 = new GridBagConstraints();
			gridCons1.gridwidth = GridBagConstraints.REMAINDER;
			gridCons1.fill = GridBagConstraints.HORIZONTAL;
			contentPane.add(jtfInput, gridCons1);
		
			GridBagConstraints gridCons2 = new GridBagConstraints();
			//gridCons2.weightx = 1.0;
			//gridCons2.weighty = 1.0;
			gridCons1.gridwidth = GridBagConstraints.REMAINDER;
			gridCons1.fill = GridBagConstraints.HORIZONTAL;
			contentPane.add(scrollPane, gridCons2);
			
			GridBagConstraints gridCons3 = new GridBagConstraints();
			gridCons3.weightx= 1.0;
			gridCons3.weighty= 2.0;
			contentPane.add(jbtnEnter, gridCons3);
			
			GridBagConstraints gridCons4 = new GridBagConstraints();
			gridCons3.weightx= 1.0;
			gridCons3.weighty= 2.0;
			contentPane.add(jbtnStart, gridCons4);
			
			// Bắt sự kiện bấm nút Enter
			jbtnEnter.setMnemonic(KeyEvent.VK_I);
			jbtnEnter.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					text = jtfInput.getText();
					out.print(text);
					jtAreaOutput.append(text + "\n");
					jtfInput.selectAll();
					sendString(text);
				}		
			});
		

		// Bắt sự kiện bấm nút Start

			jbtnStart.setMnemonic(KeyEvent.VK_I);
			jbtnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

				      try {
				          srvr = new ServerSocket(1234);
				          skt = srvr.accept();
				        // text = "Hello Client";
				         System.out.print("Server has connected!\n");
				         /*
				          out = new PrintWriter(skt.getOutputStream(), true);
				          text = "HelloClient";
				        // System.out.print("Sending string: '" + text + "'\n");
				         out.print(text);
				         
				         in = new BufferedReader(new 
				                  InputStreamReader(skt.getInputStream()));
		          
				         text = in.readLine();
		                  if ((text != null) &&  (text.length() != 0)) {
		                	  System.out.print("Error!\n");
		                  } else {
		                	  appendToChatBox("INCOMING: " + text + "\n");
		                  }
						  */

				      }
				      catch(Exception e1) {
				    	  System.out.println(e1.getMessage());
				      }   
				}
				
				});
			
		}
			
		
	
		public static void main(String[] args) {
			Server jtfTfDemo = new Server();
			jtfTfDemo.pack();
			jtfTfDemo.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			jtfTfDemo.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		 private  void sendString(String s) {
		      synchronized (toSend) {
		         toSend.append(s + "\n");
		      }
		   }
		 private void appendToChatBox(String s) {
			      synchronized (toAppend) {
			         toAppend.append(s);
			      }
			   }
		  // Cleanup for disconnect
		   private void cleanUp() {
		      try {
		         if (srvr != null) {
		        	 srvr.close();
		        	 srvr = null;
		         }
		      }
		      catch (IOException e) { srvr = null; }

		      try {
		         if (skt != null) {
		        	 skt.close();
		        	 skt = null;
		         }
		      }
		      catch (IOException e) { skt  = null; }

		      try {
		         if (in != null) {
		            in.close();
		            in = null;
		         }
		      }
		      catch (IOException e) { in = null; }

		      if (out != null) {
		         out.close();
		         out = null;
		      }
		   }
	}