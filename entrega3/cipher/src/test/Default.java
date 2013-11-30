package test;

import java.math.BigInteger;

public class Default
{
	// Global parametres
	public static BigInteger q;
	public static BigInteger w;
	public static BigInteger p;
	public static BigInteger g;
	public static BigInteger g1;
	public static BigInteger g2;
	
	
	public static void setPar ()
	{
		q = new BigInteger ( "2" );
		w = new BigInteger ( "2" );
		
		// q = 2^256 - 2^168 + 1
		q = q.pow ( 256 );	// q = 2^256
		q = q.subtract ( new BigInteger ( "2" ).pow ( 168 ) );	// q = 2^256 - 2^168
		q = q.add ( new BigInteger ( "1" ) );	// q = 2^256 - 2^168 + 1
		
		// w = 2^2815 + 231
		w = w.pow ( 2815 );	// w = 2^2815
		w = w.add ( new BigInteger ( "231" ) );	// w = 2^2815 + 231

		// p = 2wq + 1
		p = w.multiply ( q );	// p = wq
		p = p.multiply ( new BigInteger ( "2" ) );	// p = 2wq
		p = p.add ( new BigInteger ( "1" ) );	// p = 2wq + 1
		
		BigInteger _2w = w.multiply ( new BigInteger ( "2" ) );

		// g = 2^2w mod p
		g = ( new BigInteger ( "2" ) ).modPow ( _2w, p );

		// g = 2055^2w mod p
		g1 = ( new BigInteger ( "2055" ) ).modPow ( _2w, p );

		// g = 2582^2w mod p
		g2 = ( new BigInteger ( "2582" ) ).modPow ( _2w, p );
		
		
		/*
		// Prints parametres
		System.out.println ( "q == " + _q );
		System.out.println ( "w == " + _w );
		System.out.println ( "p == " + _p );
		System.out.println ( "g == " + _g );
		System.out.println ( "g1 == " + _g1 );
		System.out.println ( "g2 == " + _g2 );
		*/
		
	}

}
