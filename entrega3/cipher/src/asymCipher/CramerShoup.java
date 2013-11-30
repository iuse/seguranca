package asymCipher;

import java.math.BigInteger;
import java.util.Random;

import blake2bjava.Blake2b;
import utility.Utility;


public class CramerShoup
{
	// Private key
	private BigInteger x1;
	private BigInteger x2;
	private BigInteger y1;
	private BigInteger y2;
	private BigInteger z;
	
	// Public key
	private BigInteger c;
	private BigInteger d;
	private BigInteger h;
	
	// Parametres
	private BigInteger p;
	private BigInteger q;
	private BigInteger g1;
	private BigInteger g2;
	
	
	// Sets parametres
	public void config ( BigInteger p, BigInteger q, BigInteger g, BigInteger g1, BigInteger g2 )
	{
		this.p = p;
		this.q = q;
		this.g1 = g1;
		this.g2 = g2;
		
	}
	
	
	// Generates key pair
	public void keyPairGen ( String pw )
	{
		// Null byte
		byte[] zero = new byte[1];
		

		// Private key
		// x1 = Blake2b ( pw || "x1" ) mod q
		byte[] X1 = Utility.append ( pw.getBytes (), "x1".getBytes () );
		byte[] X1Hash = Blake2b.computeHash ( X1 );
		x1 = new BigInteger ( Utility.append ( zero, X1Hash ) ).mod ( q );
		
		// x2 = Blake2b ( pw || "x2" ) mod q
		byte[] X2 = Utility.append ( pw.getBytes (), "x2".getBytes () );
		byte[] X2Hash = Blake2b.computeHash ( X2 );
		x2 = new BigInteger ( Utility.append ( zero, X2Hash ) ).mod ( q );
		
		// y1 = Blake2b ( pw || "y1" ) mod q
		byte[] Y1 = Utility.append ( pw.getBytes (), "y1".getBytes () );
		byte[] Y1Hash = Blake2b.computeHash ( Y1 );
		y1 = new BigInteger ( Utility.append ( zero, Y1Hash ) ).mod ( q );
				
		// y2 = Blake2b ( pw || "y2" ) mod q
		byte[] Y2 = Utility.append ( pw.getBytes (), "y2".getBytes () );
		byte[] Y2Hash = Blake2b.computeHash ( Y2 );
		y2 = new BigInteger ( Utility.append ( zero, Y2Hash ) ).mod ( q );
		
		// x1 = Blake2b ( pw || "z" ) mod q
		byte[] Z = Utility.append ( pw.getBytes (), "z".getBytes () );
		byte[] ZHash = Blake2b.computeHash ( Z );
		z = new BigInteger ( Utility.append ( zero, ZHash ) ).mod ( q );
		
		
		// Public key
		// c = g1^x1 g2^x2 mod p
		BigInteger gx1 = g1.modPow ( x1, p );
		BigInteger gx2 = g2.modPow ( x2, p );
		c = gx1.multiply ( gx2 ).mod ( p );
		
		// d = g1^y1 g2^y2 mod p
		BigInteger gy1 = g1.modPow ( y1, p );
		BigInteger gy2 = g2.modPow ( y2, p );
		d = gy1.multiply ( gy2 ).mod ( p );
		
		// h = g1^z mod p
		h = g1.modPow ( z, p );
		
	}
	
	
	// Encryption
	public BigInteger[] encrypt ( BigInteger[] pk, byte[] k )
	{
		// Null byte
		byte[] zero = new byte[1];
		
		
		// Public key
		BigInteger c = pk[0];
        BigInteger d = pk[1];
        BigInteger h = pk[2];

        
		// r = random mod q
        Random rnd = new Random ();
		BigInteger r = new BigInteger ( 256, rnd );
		r = r.mod ( q );
		
		// u1 = g1^r mod p
		BigInteger u1 = g1.modPow ( r, p );
		
		// u2 = g2^r mod p
		BigInteger u2 = g2.modPow ( r, p );
		
		// e = h^r k mod p
		BigInteger K = new BigInteger ( Utility.append ( zero, k ) );
		BigInteger hr = h.modPow ( r, p );
		BigInteger e = hr.multiply ( K ).mod ( p );
		
		// alpha = Blake2b ( u1 || u2 || e )
		byte[] u1u2 = Utility.append ( u1.toByteArray (), u2.toByteArray () );
		byte[] alpha = Utility.append ( u1u2, e.toByteArray () );
		byte[] AHash = Blake2b.computeHash ( alpha );
		BigInteger a = new BigInteger ( Utility.append ( zero, AHash ) );
		
		// v = c^r d^(r*alpha) mod p
		BigInteger ra = a.multiply ( r );
		BigInteger cr = c.modPow ( r, p );
		BigInteger dra = d.modPow ( ra, p );
		BigInteger v = cr.multiply ( dra ).mod ( p );
		
		
		return new BigInteger[] { u1, u2, e, v };
		
	}
	
	
	// Decryption
	public byte[] decrypt ( BigInteger[] C )
	{
		// Null byte
		byte[] zero = new byte[1];
		
		
		// Symmetric key
		BigInteger u1 = C[0];
		BigInteger u2 = C[1];
		BigInteger e = C[2];
		BigInteger v = C[3];
		
		// alpha = Blake2b ( u1 || u2 || e )
		byte[] u1u2 = Utility.append ( u1.toByteArray (), u2.toByteArray () );
		byte[] alpha = Utility.append ( u1u2, e.toByteArray () );
		byte[] AHash = Blake2b.computeHash ( alpha );
		BigInteger a = new BigInteger ( Utility.append ( zero, AHash ) );
		
		// Verifies v <=> u1^x1 u2^x2 ( u1^y1 u2^y2 )^alpha mod p
		BigInteger u1x1 = u1.modPow ( x1, p );
		BigInteger u2x2 = u2.modPow ( x2, p );
		BigInteger u1y1 = u1.modPow ( y1, p );
		BigInteger u2y2 = u2.modPow ( y2, p );
		BigInteger v1 = u1y1.multiply ( u2y2 ).modPow ( a, p );
		v1 = v1.multiply ( u1x1 ).multiply ( u2x2 ).mod ( p );
		
		
		if ( v1.equals ( v ) )
		{
						
		}
		else
		{
			return zero;

		}
				
		// k = e ( u1^( q - z ) ) mod p
		BigInteger qz = q.subtract ( z );
		BigInteger u1qz = u1.modPow ( qz, p );
		BigInteger k = e.multiply ( u1qz ).mod ( p );
		
		
		return k.toByteArray ();
		
	}

}
