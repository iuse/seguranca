/**
 * Escola Politecnica da USP
 * PCS2582 - Seguranca da Informacao
 * 
 * Coursework #2: BLAKE2B and Schnorr Algorithms implementation
 * 
 * @author Andre Hashimoto Oku
 * @author Soon Hyung Kwon
 * 
 */


//import java.math.BigInteger;


public class DigitalSignature
{	
	// Message to be hashed and signed
	private static String m;
	
	// Password
	private static String pw;
	
	
	public static void main ( String[] args ) throws Exception
	{
		m = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		
		System.out.println ( "Message:\n" + m );
		
		pw = "0123456789abcdefghijklmnopqrstuvwxyz";
		
		System.out.println ( "Password:\n" + pw );
		
		// Sets Schnorr parameters
		schnorr.set_par ();
		
		// Generates key pair
		schnorr.keyPairGen ( pw );
		
		// Signs message
		schnorr.sign ( m );
		
		// Verifies message
		//boolean valid = schnorr.verify ( m );
		
	}

}