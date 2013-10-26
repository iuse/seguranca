
import java.math.*;



public class blake2b
{
	// Counters
	private BigInteger t0, t1;
	
	// Finalization flags
	private BigInteger f0, f1;
	
	// Initialisation flag
	private boolean _isInitialised;
	
	// Message blocks ( Padded )
	private BigInteger[] m = new BigInteger[16];
	

	private BigInteger[] h = new BigInteger[8];
	
	
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
	
	
	private static final byte[][] sigma =
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
	
	
	
	// Initialises
	// Incomplete
	private void init ( BigInteger[] P ) throws Exception
	{
		if ( P == null )
		{
			throw new Exception("config");
		}
		
		if ( P.length != 8 )
		{
			throw new Exception("config length must be 8 words");
		}
		
		
		_isInitialised = true;
		
	}
	

	// Helper method for Compression
	private void G ( int r, int i, BigInteger a, BigInteger b, BigInteger c, BigInteger d )
	{
		/*
		a = a + b + m[blake2b_sigma[r][2*i+0]];
	    d = rotr64(d ^ a, 32);
	    c = c + d;
	    b = rotr64(b ^ c, 24);
	    a = a + b + m[blake2b_sigma[r][2*i+1]];
	    d = rotr64(d ^ a, 16);
	    c = c + d;
	    b = rotr64(b ^ c, 63);
	    */
	    
		a = a.add ( b );
		a = a.add ( m[sigma[r][2 * i + 0]] );
		
		d = d.xor ( a );
		d = d.shiftRight ( 32 );
		
		c = c.add ( d );
		
		b = b.xor ( c );
		b = b.shiftRight ( 24 );
		
		a = a.add ( b );
		a = a.add ( m[sigma[r][2 * i + 1]]);
		
		d = d.xor ( a );
		d = d.shiftRight ( 16 );
		
		c = c.add ( d );
		
		b = b.xor ( c );
		b = b.shiftRight ( 63 );
		
	}
	
	
	// Helper method for Compression
	private void ROUND ( int r, BigInteger[] v )
	{
		G ( r, 0, v[ 0], v[ 4], v[ 8], v[12] );
	    G ( r, 1, v[ 1], v[ 5], v[ 9], v[13] );
	    G ( r, 2, v[ 2], v[ 6], v[10], v[14] );
	    G ( r, 3, v[ 3], v[ 7], v[11], v[15] );
	    G ( r, 4, v[ 0], v[ 5], v[10], v[15] );
	    G ( r, 5, v[ 1], v[ 6], v[11], v[12] );
	    G ( r, 6, v[ 2], v[ 7], v[ 8], v[13] );
	    G ( r, 7, v[ 3], v[ 4], v[ 9], v[14] );
		
	}
	
	// Compression method
	// Incomplete
	private void compression ( BigInteger[] h, BigInteger[] m, BigInteger[] l )
	{
		/*
		var v = _v;
		var h = _h;
		var m = _m;

		for (int i = 0; i < 16; ++i)
			m[i] = BytesToUInt64(block, start + (i << 3));

		v[0] = h[0];
		v[1] = h[1];
		v[2] = h[2];
		v[3] = h[3];
		v[4] = h[4];
		v[5] = h[5];
		v[6] = h[6];
		v[7] = h[7];

		v[8] = IV0;
		v[9] = IV1;
		v[10] = IV2;
		v[11] = IV3;
		v[12] = IV4 ^ _counter0;
		v[13] = IV5 ^ _counter1;
		v[14] = IV6 ^ _finaliziationFlag0;
		v[15] = IV7 ^ _finaliziationFlag1;

		for (int r = 0; r < NumberOfRounds; ++r)
		{
			G(0, 4, 8, 12, r, 0);
			G(1, 5, 9, 13, r, 2);
			G(2, 6, 10, 14, r, 4);
			G(3, 7, 11, 15, r, 6);
			G(3, 4, 9, 14, r, 14);
			G(2, 7, 8, 13, r, 12);
			G(0, 5, 10, 15, r, 8);
			G(1, 6, 11, 12, r, 10);
		}

		for (int i = 0; i < 8; ++i)
			h[i] ^= v[i] ^ v[i + 8];
		*/
		
		BigInteger[] v = new BigInteger[16];
		
		v[0] = h[0];
		v[1] = h[1];
		v[2] = h[2];
		v[3] = h[3];
		v[4] = h[4];
		v[5] = h[5];
		v[6] = h[6];
		v[7] = h[7];
		
		v[8] = IV[0];
		v[9] = IV[1];
		v[10] = IV[2];
		v[11] = IV[3];
		v[12] = IV[4].xor ( t0 );
		v[13] = IV[5].xor ( t1 );
		v[14] = IV[6].xor ( f0 );
		v[15] = IV[7].xor ( f1 );
		
		
		for ( int r = 0; r < 12; r++ )
		{
			ROUND ( r, v );
		}
		
		
		for ( int i = 0; i < 8; i++ )
		{
			h[i] = h[i].xor ( v[i] );
			h[i] = h[i].xor ( v[i + 8] );
		}
		
	}
	
	
	public static void main ( String[] args )
	{		
		/*
		// Prints IV
		for ( int k = 0; k < 8; k++)
		{
			System.out.println ( IV[k] );
		
		}
		*/

	}

}