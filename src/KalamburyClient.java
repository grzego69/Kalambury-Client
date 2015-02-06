import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class KalamburyClient 
{
	
	final static int idProvider = 1;
	static String host = "127.0.0.1";
	static int portNumber = 4442;
	static Socket sc = null;
	static PrintWriter pwOut = null;
	static BufferedReader buffReaderSoc = null;
	String strWrite = "BRAK ";
	static BufferedReader buffReaderSys = null;
	String strRead;
	Scanner reader = new Scanner(System.in);
	static boolean canIDraw = false;
	static String line;
	String input;
	SimplePaint sp = null;
	boolean canPlayG = false;
	
	public KalamburyClient(SimplePaint simplePaint) throws UnknownHostException, IOException 
	{	
		sc = new Socket(host,portNumber);
		this.sp=simplePaint;
		try {
			pwOut = new PrintWriter(new OutputStreamWriter(sc.getOutputStream(),"UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			buffReaderSoc = new BufferedReader(new InputStreamReader(sc.getInputStream(),"UTF-8"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		buffReaderSys = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
		Thread streamReader = new Thread(new Runnable()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run() 
			{
				DataInputStream in = null;
				try {
					in = new DataInputStream (sc.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//we
				String prev = "0";
				try {
//					while(true)
//					{
					while((line = in.readLine()) != null) 
					{
						System.out.println("Poprzednie: " + prev);
//						System.out.println("Klient odebra≥ od serwera " + line);
						
							if (line.equals("canPlay"))
							{
								canPlayG = true;
						  	}
							
							if (prev.equals("addLine"))
							{
								if (canPlayG==true)
								{
									sp.drawLine(line);
								}
						  	}
							
							if (prev.equals("winnerToClient"))
							{
								if(sp.canIDraw==false)
								{
								sp.output.setText("Za pÛüno! Uøytkownik o id: " + line + " zgad≥a wczeúniej!");
								}
								else
								{
									sp.output.setText("Uøytkownik o id " + line + " zgad≥a twoje has≥o.");
								}
						  	}
							
							if (line.equals("youWon"))
							{
								sp.output.setText("Brawo! Zgad≥eú! +1 pkt!");
								sp.points++;
						  	}
							
							if (line.equals("wrongAnserw") && (sp.canIDraw==false))
							{
								sp.output.setText("èle. SprÛbuj jeszcze raz!");
						  	}
							
							if (prev.equals("questionWord"))
							{
//								sp.setQuestionWord(line);
								line = line.toUpperCase();
								sp.output.setText("Teraz rysujesz! Has≥o: " + line);
								System.out.println("Has≥o: " + line);
						  	}
							
							if (line.equals("changeFlags"))
							{
								sp.canIDraw = true;
						  	}
							
							if (line.equals("resetFlags"))
							{
								sp.canIDraw = false;
								sp.output.setText("Teraz zgadujesz!");
								System.out.println("xD Zmieni≥em ");
						  	}
							
							if (line.equals("repaint"))
							{
								sp.repaint();
								System.out.println("Repaint");
						  	}
							
							if (line.equals("clearScreen"))
							{
								String tmp = sp.output.getText();
								sp.repaint();
								try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								sp.output.setText("Repaint. " + tmp);
								System.out.println("clearScreen");
								System.out.println("Repaint. " + tmp);
								sp.setVisible(true);
						  	}
							
						prev = line;
					}
//				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					System.out.println("Klient juø siÍ roz≥πczy≥");
					e.printStackTrace();
				}

			}
			
		});//koniec dekracji wπtku odczytu od serwera
		streamReader.start();
	}
		
	public void sendData() throws UnknownHostException, IOException
	{
//		sendClientId();
		pwOut.println("addLine");
		pwOut.flush();
		pwOut.println(strWrite);
		pwOut.flush();
	}
	
	public void connectionClose()
	{
		System.out.println("KC koniec \n");
		if(sp.canIDraw==true)
		{
			pwOut.println("closeDrawer");
		}
		else
		{
			pwOut.println("close");
		}
		pwOut.flush();
	}
	
	public void sendAnserw(String msg)
	{
		if(sp.canIDraw==false)
		{
			pwOut.println("anserw");
			pwOut.flush();
			msg = msg.toLowerCase();
			pwOut.println(msg);
			pwOut.flush();
		}

	}
	
	public void sendClearRequest()
	{
		pwOut.println("clearRequest");
		pwOut.flush();
	}
	
}
