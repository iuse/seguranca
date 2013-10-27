
import java.math.*;
import java.util.*;


public class blake2b_core 
{
	private boolean _isInitialised = false;

	private int _bufferFilled;
	private byte[] _buf = new byte[128];

	private BigInteger[] _m = new BigInteger[16];
	private BigInteger[] _h = new BigInteger[8];
	private BigInteger _t0;
	private BigInteger _t1;
	private BigInteger _f0;
	private BigInteger _f1;

	private final int BlockSizeInBytes = 128;
	
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
	private void Initialise ( BigInteger[] P ) throws Exception
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
		
		_t0 = new BigInteger ( "0" );
		_t1 = new BigInteger ( "0" );
		
		_f0 = new BigInteger ( "0" );
		_f1 = new BigInteger ( "0" );
		
		_bufferFilled = 0;

		// h = IV
		BigInteger[] _h = { IV[0], IV[1], IV[2], IV[3], IV[4], IV[5], IV[6], IV[7] };
				
		// h = h xor P
		for ( int i = 0; i < 8; i++ )
		{
			_h[i] = _h[i].xor ( P[i] );
		
		}
		
	}
	
	// G function
	// Helper method for Compression
	private void G ( int r, int i, BigInteger a, BigInteger b, BigInteger c, BigInteger d )
	{    
		a = a.add ( b );
		a = a.add ( _m[sigma[r][2 * i + 0]] );
		
		d = d.xor ( a );
		d = d.shiftRight ( 32 );
		
		c = c.add ( d );
		
		b = b.xor ( c );
		b = b.shiftRight ( 24 );
		
		a = a.add ( b );
		a = a.add ( _m[sigma[r][2 * i + 1]]);
		
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
	// TODO
	private void Compress ( byte[] block, int start )
	{
		for ( int i = 0; i < 16; i++ )
		{
			_m[i] = BytesToUInt64 ( block, start + ( i << 3 ) );

		}
		
		BigInteger[] v = new BigInteger[16];
		
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
		
		
		for ( int r = 0; r < 12; r++ )
		{
			ROUND ( r, v );
		}
		
		
		for ( int i = 0; i < 8; i++ )
		{
			_h[i] = _h[i].xor ( v[i] );
			_h[i] = _h[i].xor ( v[i + 8] );
		}
		
	}
	

	public void HashCore ( byte[] array, int start, int count ) throws Exception
	{
		if (!_isInitialised)
		{
			throw new Exception ("Not initialized");
			
		}
		
		if (array == null)
		{
			throw new Exception ( "array" );
			
		}
		
		if (start < 0)
		{
			throw new Exception ( "start" );
		
		}
		
		if (count < 0)
		{
			throw new Exception ( "count" );
		
		}
		
		if ( ( long ) start + ( long ) count > array.length )
		{
			throw new Exception ( "start+count" );
		
		}
		
		int offset = start;
		
		int bufferRemaining = BlockSizeInBytes - _bufferFilled;

		if ( ( _bufferFilled > 0 ) && ( count > bufferRemaining ) )
		{
			System.arraycopy ( array, offset, _buf, _bufferFilled, bufferRemaining );
			
			_t0 = _t0.add ( new BigInteger ( String.valueOf ( BlockSizeInBytes ) ) );
			
			if ( _t0 == ( new BigInteger ( "0" ) ) )
			{
				_t1 = _t1.add ( new BigInteger ( "1" ) );
			
			}
			
			Compress  ( _buf, 0 );
			
			offset += bufferRemaining;
			
			count -= bufferRemaining;
			
			_bufferFilled = 0;
		}

		while ( count > BlockSizeInBytes )
		{
			_t0 = _t0.add ( new BigInteger ( String.valueOf ( BlockSizeInBytes ) ) );
			
			if ( _t0 == ( new BigInteger ( "0" ) ) )
			{
				_t1 = _t1.add ( new BigInteger ( "1" ) );
			
			}
			
			Compress ( array, offset );
			
			offset += BlockSizeInBytes;
			
			count -= BlockSizeInBytes;
		}

		if ( count > 0 )
		{
			System.arraycopy ( array, offset, _buf, _bufferFilled, count );
			
			_bufferFilled += count;
		}
	}
	
	
	// TODO
	public byte[] HashFinal ( boolean isEndOfLayer )
	{
		if ( !_isInitialised )
		{
			throw new Exception ( "Not initialised ");
		
		}
		
		_isInitialised = false;

		// Last compression
		_t0 = _t0.add ( new BigInteger ( String.valueOf ( _bufferFilled ) ) );
		
		_f0 = new BigInteger ( "FFFFFFFFFFFFFFFF", 16 );
		
		if ( isEndOfLayer )
		{
			_f1 = new BigInteger ( "FFFFFFFFFFFFFFFF", 16 );
		
		}
		
		for ( int i = _bufferFilled; i < _buf.length; i++ )
		{
			_buf[i] = 0;
		
		}
		
		Compress ( _buf, 0 );

		//Output
		byte[] hash = new byte[64];

		for ( int i = 0; i < 8; ++i )
		{
			UInt64ToBytes ( _h[i], hash, i << 3 );

		}
		
		return hash;
	}
	
}
