
package test;

import java.math.BigInteger;

import signature.Schnorr;
import test.Default;


public class SignatureTest
{
	// Tests implementation
	public static void main ( String[] args )
	{
		String m = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		
		//System.out.println ( "Message:\n" + m );
		
		String pw = "0123456789abcdefghijklmnopqrstuvwxyz";
		
		//System.out.println ( "Password:\n" + pw );
		
		// Sets global parametres
		Default.setPar ();
		
		Schnorr ds = new Schnorr ();
		
		// Sets Schnorr parametres
		ds.config ( Default.p, Default.q, Default.g );
		
		// Updates message
		ds.update ( m );
		
		// Generates key pair
		ds.keyPairGen ( pw );
		
		// Signs message
		BigInteger[] sigma = ds.sign ();
		
		// Comment/uncomment the line below to test with a different message 
		//m = "The quick brown fox jumps over a lazy dog";
		
		// Updates message
		ds.update( m );
		
		// Verifies message
		boolean valid = ds.verify ( sigma );
		
		System.out.println ( valid );
		
	}

}
