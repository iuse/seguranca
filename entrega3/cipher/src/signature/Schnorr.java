
package signature;

import java.math.BigInteger;

import blake2bjava.Blake2b;
import utility.Utility;


public class Schnorr
{
	// Private key (mod q)
	private BigInteger x;
	
	// Public key (mod p)
	private BigInteger y;
	
	// Message
	private byte[] m;
	
	// Parametres
	private BigInteger p;
	private BigInteger q;
	private BigInteger g;
	
	
	// Sets parametres
	public void config ( BigInteger p, BigInteger q, BigInteger g )
	{
		this.p = p;
		this.q = q;
		this.g = g;
		
	}
	
	
	// Updates message
	public void update ( String M )
	{
		m = M.getBytes ();
		
	}
	
	
	// Generates key pair
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
	
	// Sign
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
	
	
	// Verify
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
