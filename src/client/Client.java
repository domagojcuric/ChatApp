package client;

import java.io.*; // output i input kroz streamove, searilizaciju i file sisteme, za slanje i primanje objekata kroz mrezu
import java.net.*; // koristenje socketa, za komunikaciju izmedu machina na mrezi, na transport layeru
import java.awt.*; // apstract window toolkit, omoguæuje prozore, panele, prostore za tekst
import java.awt.event.*;  // omogucuje promjenu nekog stanja  (event) npr. zatvaranje prozora
import javax.swing.*; // kreiranje GUI -a , veliki izbor Gui komponenata

	public class Client extends JFrame
{
		private JTextField userText;  // za pisanje poruka koje zelimo poslati
		private JTextArea chatWindow;  //prostor gdje pisemo poruke
		private ObjectOutputStream output; //preko njega se salju poruke
		private ObjectInputStream input; //preko njega se primaju poruke
		private String serverIP;  // sadrzi IP adresu sebe      RAZLIKA  (SOCKETser)
		private Socket connection;	// spajanje preko kojeg komuniciramo
	
	
	public Client(String host) //dafaultni konstruktor
	{
		super("AbstractThinking Client!"); // pozvati konstruktor iz bazne klase JFrame, koji ce inicijalizirat ime prozora na vrijednost koja se prosljeduje
		serverIP = host; ////dodan serverIP
		userText = new JTextField(); //inicijalizira se usertext  polje na novi Jtext field objekt- prostor gdje se ukucava tekst
		userText.setEditable(false); // nemoze se mijenjati, ne zelimo pisati dok se konekcija ne dogodi
		userText.addActionListener(  // kada se poruka napise da nestane - treba se modificirat
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)  //prati action kad posaljemo poruku
					{
						sendMessage(event.getActionCommand()); // implementira se kasnije,pojavljuje se greska,predviðeno
						userText.setText(""); //resetira text - prazan string
					}
				}
			);
		add(userText, BorderLayout.SOUTH); //metoda koja prvi parametar koji joj je prosljeden postavlja na lokaciju drugog parametra
		chatWindow = new JTextArea(); // chat window
		chatWindow.setEditable(false); // u pocetku na false jer nema konekcije
		add(new JScrollPane(chatWindow), BorderLayout.CENTER); // mogucnost skrolanja kroz poruke
		setSize(1280, 720); // velicina 
		setVisible(true); // da bude vidljiv prozor true
		//podesene Gui komponente u konstrukturu
	}
	
	public void startRunning() // pokretanje, neprima parametre - void
	{
				try
				{
					Connection();
					Setup();
					Chatting();
				}
				catch(EOFException eofException) //obavijest da se desila greska
				{
					showMessage("\n Klijent je prekinio konekciju"); //prima poruku kroz string
				}
				catch(IOException ioException)
				{
					ioException.printStackTrace();
				}
				finally
				{
					closeAll(); // izvrsava se bez obzira na sve,zatvara se sve prethodno upotrebljene resurse - stramove...
				}
	}
	
	private void Connection() throws IOException // private samo unutar klase, diktira kako server radi ....dodan exception jer omogucuje prikaz greske koja je moguca 
	{
		showMessage("Connected...\n"); 
		connection = new Socket(InetAddress.getByName(serverIP), 3000); //treba ostvarit konekciju , port isti
		showMessage("Connected on" + connection.getInetAddress().getHostName() + "\n"); //vucemo adresu (getinet) nakon sto je konkcija uspostavljena, ispisuje tko se spojio
	}
	
	private void Setup() throws IOException // unutar ove metode inicijaliziraju se output  i input streamovi
	{
		output = new ObjectOutputStream(connection.getOutputStream()); // vraca odlazeci stream koji ide prema klijentu, vrijednost koju ova metoda 
																		//vrati ce se iskoristit za inicijalizaciju output streama
		output.flush(); // prazni se,treba biti prazan
		
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("Sve je naredeno! \n");
	}
	
	private void Chatting () throws IOException //metoda za komunikaciju korisnika
	{
		String message = "Connected"; //obavjest da je povezan
		sendMessage(message); // pozove metodu sendMasege i prosljedio joj string
		canType(true); // potrebno za zakljucavanje prostora u kojem pisemo text, tako se nemoze slati poruke prije nego se povezemo iLi dok se streamove ne usklade
		do
		{
			try
			{
				message = (String) input.readObject(); //poruku koju napisemo spasavamo u meessage varijablu, posto je ta poruka objekt
													   //potrebno ju je castat u tip string
				showMessage(message + "\n");
			}
			catch(ClassNotFoundException e)
			{
				showMessage("Nepoznata interakcija.\n");
			}
		}
		while(!message.equals("Server - End")); //uvjet, dok je ispunjen nastavljat ce se rad petlje
			
	}
	
	private void closeAll() // zatvaranje....cleanup
	{
		showMessage("Closing connections...\n");
		canType(false); // zakljucan prostor za pisanje teksta
		try
		{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException ioException)
		{
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage (String message) 
	{
		try
		{
			output.writeObject("Client - " + message); // ova metoda pise string u output string, naglassavamo da je  server taj salje poruku
			output.flush(); //ciscenje, mora bit prazan
			showMessage("Client - " + message + "\n");
		}
		catch(IOException ioException)
		{
		chatWindow.append("Error - Message cannot be sent");	
		}
		
	}
	
	private void showMessage(final String text) // kljucan rijec finali oznacave da se nemogu daljnnje promjene vrsiti nad ovim stringom, kad si poslo poslo si
	{
		SwingUtilities.invokeLater(  // invokeLater (prima nit kao parametar) izvrsava thread(niti) koji joj je prosljeden kao parametar i unutar tog parametra
				new Runnable() {	// kreira se novi thread i u metodi run je dodana poruka na prostor za text u Gui-u
					public void run() {
					chatWindow.append(text);
					}
				});
	}
	
	private void canType(final boolean tof) // kao test za kasnije,  za zakljucavanje odredenih radnji, kada se prosljedi true or false nema mijenjanja
	{
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(tof);
						}
					});
				
	}
	
	
	
}


