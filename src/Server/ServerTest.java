package Server;

import javax.swing.JFrame;

public class ServerTest {

	public static void main(String[] args) 
	{
		Server Host = new Server(); // inicijalizacija
		Host.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //kad se klikne na x na prozoru da se zatvori
		Host.startRunning(); // otvara konekcije za novi socket
	}

}
