
package test;

import java.io.IOException;

import tweetcipher.Tweetcipher;


public class TcTest
{
	public static void main ( String[] args ) throws IOException
	{
		String key256 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		String key128 = "aaaaaaaaaaaaaaaa";
		
		Tweetcipher tc = new Tweetcipher(key256, key128);
		
		tc.encdec ( "a.txt", "ciphered.txt", 'e' );

		tc.encdec ( "ciphered.txt", "deciphered.txt", 'd' );
	}

}
