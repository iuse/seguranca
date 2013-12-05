
package signature;

import java.math.BigInteger;

import blake2bjava.Blake2b;
import utility.Utility;


/**
 * Schnorr algorithm.
 * 
 * Implementation of the Schnorr algorithm for digital signature.
 * This class has methods to generate a key pair, sign, and verify signatures.
 * 
 * @author Andre Hashimoto Oku
 * @author Soon Hyung Kwon
 */

public class Schnorr
{
	/**
	 * Private key (mod q)
	 * 
	 */
	private BigInteger x;
	
	/**
	 * Public key (mod p)
	 * 
	 */
	private BigInteger y;
	
	/**
	 * Message
	 * 
	 */
	private byte[] m;
	
	// Parametres
	private BigInteger p;
	private BigInteger q;
	private BigInteger g;
	
	
	/**
	 * Sets parametres used in calculation
	 * 
	 * @param p
	 * @param q
	 * @param g
	 */
	public void config ( BigInteger p, BigInteger q, BigInteger g )
	{
		this.p = p;
		this.q = q;
		this.g = g;
		
	}
	
	
	/**
	 * Updates message
	 *  
	 * @param M Message
	 */
	public void update ( String M )
	{
		m = M.getBytes ();
		
	}
	
	
	/**
	 * Generates key pair from a user-defined password
	 * 
	 * @param pw User-defined password
	 * @return Returns the public key
	 */
	public BigInteger keyPairGen ( String pw )
	{
		// Null byte
		byte[] zero = new byte[1];

		// Hashed password
		// Blake2b ( password )
		byte[] pwHash = Blake2b.computeHash ( pw.getBytes () );
		
		// Private key
		// x = Blake2b ( password ) mod q
		x = new BigInteger ( Utility.append ( zero, pwHash ) ).mod ( q );
		
		// Public key
		// y = g^x mod p
		y = g.modPow ( x, p );
		
		
		return y;
		
	}
	
	/**
	 * Signs message
	 * 
	 * @return Returns the signed message
	 */
	public BigInteger[] sign ()
	{
		// Null byte
		byte[] zero = new byte[1];
		
		
		// k = Blake2b ( x || m) mod q
		byte[] xm = Utility.append ( x.toByteArray(), m );
		byte[] xmHash = Blake2b.computeHash ( xm );
		BigInteger k = new BigInteger ( Utility.append ( zero, xmHash ) ).mod ( q );
		
		// u = g^k mod p
		BigInteger u = g.modPow ( k, p );
		
		// h = Blake2b ( m || u )
		byte[] mu = Utility.append ( m, u.toByteArray() );
		byte[] muHash = Blake2b.computeHash ( mu );
		BigInteger h = new BigInteger ( Utility.append ( zero, muHash ) );
		
		// s = ( k - xh ) mod q
		BigInteger xh = x.multiply ( h );
		BigInteger s = k.subtract ( xh ).mod ( q );
		
		// sigma = ( h, s )
		return new BigInteger[] { h, s };
		
	}
	
	
	/**
	 * Compares the signed message with the raw message
	 * 
	 * @param sigma Signed message
	 * @param key Sender public key
	 * @return Returns true if the signature is valid
	 */
	public boolean verify ( BigInteger[] sigma, BigInteger key )
	{
		// Null byte
		byte[] zero = new byte[1];
		
		
		BigInteger h = sigma[0];
		BigInteger s = sigma[1];
		
		// u = g^s y^h mod p
		BigInteger gs = g.modPow ( s, p );
		BigInteger yh = key.modPow ( h, p );
		BigInteger u = gs.multiply ( yh ).mod ( p );
		
		// Accept <=> Blake2b ( m || u ) == h
		byte[] mu = Utility.append ( m, u.toByteArray() );
		byte[] muHash = Blake2b.computeHash ( mu );
		BigInteger h1 = new BigInteger ( Utility.append ( zero, muHash ) );
		
		if ( h1.equals ( h ) )
		{
			return true;
			
		}
		else
		{
			return false;
					
		}
		
	}

}
