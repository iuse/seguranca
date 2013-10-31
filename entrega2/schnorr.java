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


import java.math.*;


public class schnorr
{
	// Constructor
	public schnorr ()
	{
		
	}
	
	// Private key
	private static BigInteger _x;
	
	// Public key
	private static BigInteger _y;
	
	// Parameters
	private static BigInteger _q, _w, _p, _g, _g1, _g2;
	
	// Signature
	private static BigInteger[] signature = new BigInteger[2];
	

	// Sets parameters
	public static void set_par ()
	{
		_q = new BigInteger ( "2" );
		_w = new BigInteger ( "2" );
		
		// q = 2^256 - 2^168 + 1
		_q = _q.pow ( 256 );	// q = 2^256
		_q = _q.subtract ( new BigInteger ( "2" ).pow ( 168 ) );	// q = 2^256 - 2^168
		_q = _q.add ( new BigInteger ( "1" ) );	// q = 2^256 - 2^168 + 1
		
		// w = 2^2815 + 231
		_w = _w.pow ( 2815 );	// w = 2^2815
		_w = _w.add ( new BigInteger ( "231" ) );	// w = 2^2815 + 231

		// p = 2wq + 1
		_p = _w.multiply ( _q );	// p = wq
		_p = _p.multiply ( new BigInteger ( "2" ) );	// p = 2wq
		_p = _p.add ( new BigInteger ( "1" ) );	// p = 2wq + 1
		
		BigInteger _2w = _w.multiply ( new BigInteger ( "2" ) );

		// g = 2^2w mod p
		_g = ( new BigInteger ( "2" ) ).modPow ( _2w, _p );

		// g = 2055^2w mod p
		_g1 = ( new BigInteger ( "2055" ) ).modPow ( _2w, _p );

		// g = 2582^2w mod p
		_g2 = ( new BigInteger ( "2582" ) ).modPow ( _2w, _p );
		
		
		/*
		// Prints parameters
		System.out.println ( "q == " + _q );
		System.out.println ( "w == " + _w );
		System.out.println ( "p == " + _p );
		System.out.println ( "g == " + _g );
		System.out.println ( "g1 == " + _g1 );
		System.out.println ( "g2 == " + _g2 );
		*/
		
	}
	
	
	// Generates key pair
	public static void keyPairGen ( String pw ) throws Exception
	{
		BLAKE2B.Initialise ();
		
		BigInteger hash = BLAKE2B.Hash ( pw );
		
		_x = hash.mod ( _q );
		
		_y = _g.modPow ( _x, _p );
		
	}
	
	
	// Generates signature
	public static void sign ( String _m ) throws Exception
	{
		// Converts x and m to byte array and concatenate
		byte[] x_array = _x.toByteArray ();
		byte[] m_array = _m.getBytes ( "US-ASCII" );
		
		byte[] xm = new byte [x_array.length + m_array.length];
		System.arraycopy ( x_array, 0, xm, 0, x_array.length );
		System.arraycopy ( m_array, 0, xm, x_array.length, m_array.length);
		
		// Converts ( x || m ) to string
		String xm_string = new String ( xm, "UTF-8" );

		//System.out.println ( xm_string );
		
		// Initialises hash function
		BLAKE2B.Initialise();
		
		// Hashes xm
		// BLAKE2b ( x || m )
		BigInteger k = BLAKE2B.Hash ( xm_string );
		
		// BLAKE2b ( x || m ) mod q
		k = k.mod ( _q );
		
		// u = g^k mod p
		BigInteger u = _g.modPow ( k, _p );
		
		// Converts m and u to byte array and concatenate
		byte[] u_array = u.toByteArray ();
				
		byte[] mu = new byte [m_array.length + u_array.length];
		System.arraycopy ( m_array, 0, mu, 0, m_array.length );
		System.arraycopy ( u_array, 0, mu, m_array.length, u_array.length);
		
		// Converts ( m || u ) to string
		String mu_string = new String ( mu, "UTF-8" );
		
		// Initialises hash function
		BLAKE2B.Initialise();
			
		// Hashes xm
		// h = BLAKE2b ( m || u )
		BigInteger h = BLAKE2B.Hash ( mu_string );
		
		// s = ( k – xh ) mod q
		BigInteger s = _x.multiply ( h );	// s = xh
		s = k.subtract ( s );	// s = k - xh
		s = k.mod ( _q );

		// σ = (h, s)
		signature[0] = h;
		signature[1] = s;
		
		System.out.println ( "h == " + signature[0] );
		System.out.println ( "s == " + signature[1] );
		
	}
	
	
	// Verifies signature
	// TODO
	public static boolean verify ()
	{
		/*
		u = ( g^s * y^h ) mod p
		aceitar <=> BLAKE2b ( m || u ) == h
		*/
		
		// u = ( g^s * y^h ) mod p
		
		// BLAKE2b ( m || u )
		

		return false;
		
	}

}
