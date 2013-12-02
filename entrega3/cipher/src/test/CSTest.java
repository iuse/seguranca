
package test;

import java.math.BigInteger;
import java.util.Random;

import test.Default;
import asymCipher.CramerShoup;


public class CSTest
{
	// Tests Cramer-Shoup implementation
	public static void main ( String[] args )
	{
		// Sets global parametres
		Default.setPar ();
		
		
		// Symmetric key
        Random rnd = new Random ();
		BigInteger r = new BigInteger ( 256, rnd );
		r = r.mod ( Default.q );
			
		
		// Sender
		CramerShoup sender = new CramerShoup ();
		
		sender.config ( Default.p, Default.q, Default.g, Default.g1, Default.g2 );
		
		String sender_pw = "senderpassword";
		
		BigInteger[] sender_pk = sender.keyPairGen ( sender_pw );
		
		
		// Receiver
		CramerShoup receiver = new CramerShoup ();
		
		receiver.config ( Default.p, Default.q, Default.g, Default.g1, Default.g2 );
		
		String receiver_pw = "receiverpassword";
		
		BigInteger[] receiver_pk = receiver.keyPairGen ( receiver_pw );
		
		
		// Encrypts symmetric key using receiver's public key
		BigInteger[] s = sender.encrypt ( receiver_pk, r.toByteArray () );


		// Decrypts symmetric key
		byte[] K = receiver.decrypt ( s );
		
		
		BigInteger k = new BigInteger ( K );
		
		System.out.println ( k );
		System.out.println ( r );
		
		
		if ( k == r )
		{
			System.out.println ( "true" );
			
		}
		else
		{
			System.out.println ( "false" );
			
		}
				
	}

}
