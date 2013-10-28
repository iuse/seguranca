
import java.math.*;


public class schnorr
{
	// Private key
	private BigInteger _x;
	
	// Public key
	private BigInteger _y;
	
	// Message
	private byte[] _m;
	
	// Parameters
	private BigInteger _q, _w, _p, _g, _g1, _g2;
	
	// Hash
	private byte[] _h;


	// Sets parameters
	private void set_par ( BigInteger q, BigInteger w, BigInteger g, BigInteger g1, BigInteger g2 )
	{
		_q = q;
		_w = w;

		// p = 2wq + 1
		_p = w.multiply ( q );	// p = wq
		_p = _p.multiply ( new BigInteger ( "2" ) );	// p = 2wq
		_p = _p.add ( new BigInteger ( "1" ) );	// p = 2wq + 1
		
		BigInteger _2w = w.multiply ( new BigInteger ( "2" ) );

		// g = 2^2w mod p
		_g = ( new BigInteger ( "2" ) ).modPow ( _2w, _p );

		// g = 2055^2w mod p
		_g1 = ( new BigInteger ( "2055" ) ).modPow ( _2w, _p );

		// g = 2582^2w mod p
		_g2 = ( new BigInteger ( "2582" ) ).modPow ( _2w, _p );
		
	}
	
	
	// Generate key pair
	private void keyPairGen ( BigInteger h )
	{
		_x = h.mod ( _q );
		
		_y = _g.modPow ( _x, _p );
		
	}
	
	
	// Generate signature
	// TODO
	private void sign ()
	{
		/*
		k = BLAKE2b ( x || m ) mod q
		u = g^k mod p
		h = BLAKE2b ( m || u )
		s = ( k – xh ) mod q
		σ = (h, s)
		*/
		
	}
	
	
	// Verify signature
	// TODO
	private void verify ()
	{
		/*
		u = ( g^s * y^h ) mod p
		aceitar <=> BLAKE2b ( m || u ) == h
		*/
		
	}

}
