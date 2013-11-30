
package tweetcipher;

import java.io.IOException;

/**
 * Escola Politecnica da USP
 * PCS2582 - Seguranca da Informacao
 * 
 * Coursework #1: translation of "Tweetcipher" algorithm from C to Java.
 * (Ref: http://cybermashup.com/2013/06/12/tweetcipher-crypto-challenge/)
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
		Tweetcipher tc = new Tweetcipher(args[0], args[1], args[2]);
		tc.codec();
	}
	
	/**
	 *  Field that takes CLI arguments
	 */
	private String[] v;
	
	/**
	 *  Constructor.
	 *  
	 * @param mode 'e' for encryption, 'd' for decryption. 
	 * @param key256 32 ASCII character string.
	 * @param key128 16 ASCII character string.
	 */
	public Tweetcipher(String mode, String key256, String key128) {
		this.v = new String[3];
		this.v[0] = mode;
		this.v[1] = key256;
		this.v[2] = key128;
	}

	/**
	 *  Takes input stream ASCII characters until EOF then outputs
	 *  ciphered message to the output IO stream.
	 *  
	 * @throws IOException
	 */
	public void codec() throws IOException {
		long[] x = new long[16];

		// assigns true to f if 1st CLI argument is character 'e'
		// which stands for 'encrypt', any other character will
		// set f = false thus activating decrypt mode
		boolean f = "e".equals(this.v[0]);

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

		// Get user input until EOF
		int inChar;
		while ((inChar = System.in.read()) != -1) {

			// If f is false (decrypt mode) and
			// Bitwise OR between c and last 8 bits of scrambled x[0]
			// is equal to 0xA = (ASCII) \n or Line Feed
			//
			// x[0] ^ c (c is elevated to a uint64_t)
			// % 256 = c (last 8 bits of uint64_t c)
			if (!f && 10 == (x[0] ^ inChar) % 256) {
				return;
			}

			// write c
			System.out.write((int) (x[0] ^ inChar));
			System.out.flush();

			// if f = true then
			// x[0] = c ^ x[0]
			// else
			// x[0] = c ^ (x[0] & ~ 255ULL)
			//
			// at first character input x[0] = 0ULL
			// 255ULL = 0x00000000000000FF
			// ~255ULL = 0xFFFFFFFFFFFFFF00
			// x[0] & ~255ULL = 0x0000000000000000
			// c^(x[0] & ~255ULL) = 0x00000000000000XX
			x[0] = inChar ^ (f ? x[0] : x[0] & ~255);

			rounds(x);
		}

		x[0] ^= 1;

		rounds(x);

		for (int i = 0; i < 8; ++i) {
			System.out.write((int) (255 & ((x[4] ^ x[5]) >>> 8 * i)));
			System.out.flush();
		}

		for (int i = 0; i < 8; ++i) {
			System.out.write((int) (255 & ((x[6] ^ x[7]) >>> 8 * i)));
			System.out.flush();
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
