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


import java.math.BigInteger;
import java.util.*;


public class BLAKE2B
{
	// Constructor
	// TODO
	public BLAKE2B ()
	{
		
	}
	
	// Initialisation flag
	private static boolean _isInitialised = false;
	
	// Parameter block
	private static byte[] P = new byte[64];
	
	// Message blocks
	// Max of 1024 blocks of 128 bytes each
	private static byte[][] _m_blocks = new byte[1024][128];
		
	// Hash
	private static BigInteger[] _h = new BigInteger[8];
	
	// Counters
	private static BigInteger _t0;
	private static BigInteger _t1;
		
	// Finalisation flags
	private static BigInteger _f0;
	private static BigInteger _f1;
	
	// Internal state
	private static BigInteger[] v = new BigInteger[16];
	
	// IV
	private static final BigInteger[] IV =
		{
			new BigInteger ( "6a09e667f3bcc908", 16 ),
			new BigInteger ( "bb67ae8584caa73b", 16 ),
			new BigInteger ( "3c6ef372fe94f82b", 16 ),
			new BigInteger ( "a54ff53a5f1d36f1", 16 ),
			new BigInteger ( "510e527fade682d1", 16 ),
			new BigInteger ( "9b05688c2b3e6c1f", 16 ),
			new BigInteger ( "1f83d9abfb41bd6b", 16 ),
			new BigInteger ( "5be0cd19137e2179", 16 )
		};
	
	// Sigma
	private static final int[][] sigma =
		{
			{  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 } ,
			{ 14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3 } ,
			{ 11,  8, 12,  0,  5,  2, 15, 13, 10, 14,  3,  6,  7,  1,  9,  4 } ,
			{  7,  9,  3,  1, 13, 12, 11, 14,  2,  6,  5, 10,  4,  0, 15,  8 } ,
			{  9,  0,  5,  7,  2,  4, 10, 15, 14,  1, 11, 12,  6,  8,  3, 13 } ,
			{  2, 12,  6, 10,  0, 11,  8,  3,  4, 13,  7,  5, 15, 14,  1,  9 } ,
			{ 12,  5,  1, 15, 14, 13,  4, 10,  0,  7,  6,  3,  9,  2,  8, 11 } ,
			{ 13, 11,  7, 14, 12,  1,  3,  9,  5,  0, 15,  4,  8,  6,  2, 10 } ,
			{  6, 15, 14,  9, 11,  3,  0,  8, 12,  2, 13,  7,  1,  4, 10,  5 } ,
			{ 10,  2,  8,  4,  7,  6,  1,  5, 15, 11,  9, 14,  3, 12, 13 , 0 } ,
			{  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 } ,
			{ 14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3 }
		};
	
	
	// Initialise hash
	public static void Initialise ()
	{
		// Set counters and flags
		_t0 = new BigInteger ( "0" );
		_t1 = new BigInteger ( "0" );
		_f0 = new BigInteger ( "0" );
		_f1 = new BigInteger ( "0" );
		_isInitialised = true;
		
		// Fills parameter block ( P ) with zeros
		Arrays.fill ( P, ( byte ) 0 );
		
		// h = IV
		_h[0] = IV[0];
		_h[1] = IV[1];
		_h[2] = IV[2];
		_h[3] = IV[3];
		_h[4] = IV[4];
		_h[5] = IV[5];
		_h[6] = IV[6];
		_h[7] = IV[7];
		
		// h = IV xor P
		// TODO
		
		
		/*
		// Prints h
		for ( int k = 0; k < 8; k++ )
		{
			System.out.print ( _h[k] );
			
		}
		
		System.out.print ( "\n" );
		*/
		
	}
	
	// G function
	// Helper method for Compression
	private static void G ( int r, int i, int a, int b, int c, int d, BigInteger[] m )
	{
		//System.out.println ( "mi: " + m[sigma[r][2 * i]] );
		//System.out.println ( "mj: " + m[sigma[r][2 * i + 1]] );
		
		/*
		System.out.println ( "v[" + a + "]: " + v[a] );
		System.out.println ( "v[" + b + "]: " + v[b] );
		System.out.println ( "v[" + c + "]: " + v[c] );
		System.out.println ( "v[" + d + "]: " + v[d] );
		System.out.println ();
		*/
		
		v[a] = v[a].add ( v[b] );
		v[a] = v[a].add ( m[sigma[r][2 * i]] );
		
		v[d] = v[d].xor ( v[a] );
		v[d] = v[d].shiftRight ( 32 );
		
		v[c] = v[c].add ( v[d] );
		
		v[b] = v[b].xor ( v[c] );
		v[b] = v[b].shiftRight ( 24 );
		
		v[a] = v[a].add ( v[b] );
		v[a] = v[a].add ( m[sigma[r][2 * i + 1]] );
		
		v[d] = v[d].xor ( v[a] );
		v[d] = v[d].shiftRight ( 16 );
		
		v[c] = v[c].add ( v[d] );
		
		v[b] = v[b].xor ( v[c] );
		v[b] = v[b].shiftRight ( 63 );
		
		
		/*
		System.out.println ( "New values (round " + i + "):" );
		System.out.println ( "v[" + a + "]: " + v[a] );
		System.out.println ( "v[" + b + "]: " + v[b] );
		System.out.println ( "v[" + c + "]: " + v[c] );
		System.out.println ( "v[" + d + "]: " + v[d] );
		System.out.println ();
		*/
		
	}
	
	// Helper method for Compression
	private static void ROUND ( int r, BigInteger[] m )
	{
		G ( r, 0, 0, 4, 8, 12, m );
	    G ( r, 1, 1, 5, 9, 13, m );
	    G ( r, 2, 2, 6, 10, 14, m );
	    G ( r, 3, 3, 7, 11, 15, m );
	    G ( r, 4, 0, 5, 10, 15, m );
	    G ( r, 5, 1, 6, 11, 12, m );
	    G ( r, 6, 2, 7, 8, 13, m );
	    G ( r, 7, 3, 4, 9, 14, m );
	    
		
	    /*
		for ( int k = 0; k < 16; k++ )
		{
			System.out.println ( v[k] );
			
		}
		*/
		
	}
	
	// Compression method
	private static void Compress ( byte[] block )
	{
		// Message block words
		BigInteger[] m_words = new BigInteger[16];
		
		// Group bytes into words
		for ( int k = 0; k < 16; k++ )
		{
			byte[] aux = new byte[8];
			
			System.arraycopy ( block, k * 8, aux, 0, 8 );
			
			m_words[k] = new BigInteger ( aux );
			
			//System.out.println ( m_words[k] );

		}
		
		
		v[0] = _h[0];
		v[1] = _h[1];
		v[2] = _h[2];
		v[3] = _h[3];
		v[4] = _h[4];
		v[5] = _h[5];
		v[6] = _h[6];
		v[7] = _h[7];
		
		v[8] = IV[0];
		v[9] = IV[1];
		v[10] = IV[2];
		v[11] = IV[3];
		
		v[12] = IV[4].xor ( _t0 );
		v[13] = IV[5].xor ( _t1 );
		v[14] = IV[6].xor ( _f0 );
		v[15] = IV[7].xor ( _f1 );

		
		/*
		for ( int k = 0; k < 16; k++ )
		{
			System.out.println ( v[k] );
			
		}
		*/
		
		
		for ( int r = 0; r < 12; r++ )
		{
			//System.out.println ( "ROUND: " + r );
			
			ROUND ( r, m_words );
			
		}
		
		
		// New Chain value after ROUND
		for ( int i = 0; i < 8; i++ )
		{
			//System.out.println ( "h[i]: " + _h[i] );
			
			_h[i] = _h[i].xor ( v[i] );
			
			//System.out.println ( "h[i] xor v[i]: " + _h[i] );

			_h[i] = _h[i].xor ( v[i + 8] );
			
			//System.out.println ( "h[i] xor v[i] xor v[i + 8]: " + _h[i] );
			
		}
		
		
		/*
		System.out.println ( "\nPartial hash: " );
		
		for ( int i = 0; i < 8; i++ )
		{
			System.out.print ( _h[i] );
			
		}
		*/
		
	}
	
	// Hash function
	public static BigInteger Hash ( String m ) throws Exception
	{
		// Initialises hash parameters
		Initialise ();
		
		
		if ( !_isInitialised )
		{
			throw new Exception ( "Not initialised" );
		}
		
		
		// Converts message into array of bytes
		byte[] m_array = m.getBytes ();
		
		// Number of message blocks
		int N = m_array.length / 128;
		
		if ( m_array.length % 128 != 0 )
		{
			N += 1;
			
		}
		
		//System.out.println ( m_array.length );
		//System.out.println ( N );
		
		// Insert padding if necessary
		byte[] m_array_padded = new byte[N * 128];
		System.arraycopy ( m_array, 0, m_array_padded, 0, m_array.length );
		
		//System.out.println ( m_array_padded.length );
			
		// Converts array of bytes into blocks of 128 bytes
		for ( int k = 0; k < N; k++ )
		{
			System.arraycopy ( m_array_padded, k * 128, _m_blocks[k], 0, 128 );
			
		}
		
		// Apply compress function to each message block
		for ( int k = 0; k < N; k++ )
		{
			Compress ( _m_blocks[k] );
			
			// Increments counter
			_t0 = _t0.add ( new BigInteger ( "128" ) );

			// TODO: Adjust counter boundaries ( 64 bits )
			
			
			if ( k == N - 1 )
			{
				// Sets flag to 0xF...FF if it's the last block processed
				_f0 = new BigInteger ( "FFFFFFFFFFFFFFFF", 16 );
				_f1 = new BigInteger ( "FFFFFFFFFFFFFFFF", 16 );
				
			}
			
		}
		
		
		/*
		// Prints hashed message
		System.out.println ( "\nHashed message: " );
		
		for ( int k = 0; k < 8; k++ )
		{
			System.out.print ( _h[k] );
			
		}
		*/
		
		
		// Allocate to variable and return
		String hash = "";

		for ( int k = 0; k < 8; k++ )
		{
			hash += _h[k].toString ();
		}
		
		
		//System.out.println ( "\nHashed message:\n" + hash );
		

		// Sets initialisation flag to false
		_isInitialised = false;
		
		
		return new BigInteger ( hash );
		
	}
	
}
