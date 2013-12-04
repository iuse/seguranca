package tweetcipher;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tweetcipher Version 3
 * 
 * Escola Politecnica da USP
 * PCS2582 - Seguranca da Informacao
 * 
 * Coursework #3: adaptation of "Tweetcipher" algorithm from C to Java.
 * (Ref: http://cybermashup.com/2013/06/12/tweetcipher-crypto-challenge/)
 * Has methods for file input and output.
 * 
 * @author Soon Hyung Kwon
 * @author Andre Hashimoto Oku
 */
public class Tweetcipher {
	
	/**
	 * The main method runs the codec mechanism.
	 * 3 arguments are required as described for the only Constructor.
	 * 
	 * @param args mode, key256, key128
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String key256 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		String key128 = "aaaaaaaaaaaaaaaa";
		
		Tweetcipher tc = new Tweetcipher(key256, key128);
		tc.encdec("/a.in", "/ciphered.ts", 'e');
		tc.encdec("/ciphered.ts", "/deciphered.ts", 'd');
	}
	
	/**
	 *  Field that takes CLI arguments
	 */
	private String[] v;
	
	private FileReader input;
	private FileWriter output;
	
	/**
	 *  Creates a Tweetcipher with given keys.
	 *  
	 * @param key256 32 ASCII character string.
	 * @param key128 16 ASCII character string.
	 */
	public Tweetcipher(String key256, String key128) {
		this.v = new String[3];
		this.v[1] = key256;
		this.v[2] = key128;
	}

	/**
	 *  Reads the file at pathIn path, ciphers the content and writes
	 *  the ciphered text to the file at pathOut.
	 *  
	 *  +Paths must be absolute.
	 *  
	 *  +Input file must be ASCII encoded.
	 *  
	 *  +Output file is HEX encoded.
	 *  
	 *  +Encoding mode is a CHARACTER, it must be single quoted (eg. 'e')
	 *  
	 * @param pathIn absolut path to the input file.
	 * @param pathOut absolut path to the output file.
	 * @param mode 'e' for encoding, 'd' for decoding.
	 * 
	 * @throws IOException
	 */
	public void encdec(String pathIn, String pathOut, char mode) throws IOException {
		long[] x = new long[16];

		// assigns true to f if 1st CLI argument is character 'e'
		// which stands for 'encrypt', any other character will
		// set f = false thus activating decrypt mode
		boolean m = mode == 'e';

		for (int i = 0; i < x.length; ++i)
			// (HEX) 0x7477697468617369 = (ASCII) "twithasi"
			x[i] = i * Long.parseLong("7477697468617369", 16);

		// x[0], x[1], x[2], x[3] take 2nd CLI argument divided in
		// 4 pieces. As each of these four array elements are assigned
		// with a 64 bit unsigned long we need the 2nd CLI argument
		// to be 64 * 4 = 256 bits or 32 Bytes long
		for (int i = 0; i < 4; ++i)
			x[i] = Long.parseLong(
					convertStringToHex(this.v[1].substring(i * 8, (i + 1) * 8)), 16);

		// similar to above
		// x[4], x[5] take 3rd CLI argument divided in 2 pieces.
		// Therefore we need 64 * 2 = 128 bits or 16 Bytes long 3rd
		// CLI argument.
		for (int i = 0; i < 2; ++i)
			x[i + 4] = Long.parseLong(
					convertStringToHex(this.v[2].substring(i * 8, (i + 1) * 8)), 16);

		rounds(x);


		try {
			// Initialize file reader and writer
			input = new FileReader(pathIn);
            output = new FileWriter(pathOut);
            
			long tempChar;
			long mask = 255;
			int inChar;
			
			while ((inChar = input.read()) != -1) {
				// If deciphering then read one more character
				if ( !m )
				{
					inChar = Integer.parseInt(Character.toString((char)inChar)+Character.toString((char)input.read()),16);
			
				}
				
				// if decoding then all the bytes except the last one must be 0
				tempChar = m ? inChar : inChar & mask;
				
				if ( m )
				{
					String s = Integer.toHexString((int)((x[0] ^ tempChar) & mask));
					
					if ( s.length () == 1 )
					{
						output.write( "0" );
					}
					
					output.write( s );
					
				}
				else
				{
					output.write((int)((x[0] ^ tempChar) & mask));
					
				}
				
				x[0] = tempChar ^ (m ? x[0] : x[0] & ~255);
	
				rounds(x);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	/** 
	 * Helper method for ciphering.
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @param c
	 * @param r
	 */
	private void axr(long x[], int a, int b, int c, int r) {
		x[a] += x[b];
		long temp = x[c] ^ x[a];
		x[c] = (temp << 64 - r) | (temp >>> r);
	}

	/**
	 * Helper method for ciphering.
	 * 
	 * @param x
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	private void g(long x[], int a, int b, int c, int d) {
		axr(x, a, b, d, 32);
		axr(x, c, d, b, 25);
		axr(x, a, b, d, 16);
		axr(x, c, d, b, 11);
	}

	/**
	 * Helper method for ciphering.
	 * 
	 * @param x
	 */
	private void rounds(long x[]) {
		for (int r = 5; r >= 0; r--) {
			for (int i = 0; i < 4; i++)
				g(x, i, i + 4, i + 8, i + 12);

			for (int i = 0; i < 4; i++)
				g(x, i, (i + 1) % 4 + 4, (i + 2) % 4 + 8, (i + 3) % 4 + 12);
		}
	}

	/**
	 * Converts a given string to its characters' ASCII HEX values.
	 * 
	 * @param str ASCII string for conversion. 
	 * @return a String containing HEX value of the corresponding ASCII input string.
	 */
	private String convertStringToHex(String str) {

		char[] chars = str.toCharArray();

		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}

		return hex.toString();
	}
}
