
import java.io.*;
import java.math.*;
import java.util.*;


public class tweetcipher
{
	private static void rounds ( int i, long[] x )
	{
		int r;
		
		for ( r = 6; r >= 0; r-- )
		{
			for ( i = 0; i < 4; ++i )
			{
				x[i] += x[i + 4];
				x[i + 12] = ( ( ( x[i + 12]^x[i] ) << ( 64 - 32 ) ) | ( ( x[i + 12]^x[i] ) >>> 32 ) );

				x[i + 8] += x[i + 12];
				x[i + 4] = ( ( ( x[i + 4]^x[i + 8] ) << ( 64 - 25 ) ) | ( ( x[i + 4]^x[i + 8] ) >>> 25 ) );

				x[i] += x[i + 4];
				x[i + 12] = ( ( ( x[i + 12]^x[i] ) << ( 64 - 16 ) ) | ( ( x[i + 12]^x[i] ) >>> 16 ) );

				x[i + 8] += x[i + 12];
				x[i + 4] = ( ( ( x[i + 4]^x[i + 8] ) << ( 64 - 11 ) ) | ( ( x[i + 4]^x[i + 8] ) >>> 11 ) );

			}

			for ( i = 0; i < 4; ++i )
			{
				x[i] += x[( i + 1 ) % 4 + 4];
				x[( i + 3 ) % 4 + 12] = ( ( ( x[( i + 3 ) % 4 + 12]^x[i] ) << ( 64 - 32 ) ) | ( ( x[( i + 3 ) % 4 + 12]^x[i] ) >>> 32 ) );

				x[( i + 2 ) % 4 + 8] += x[( i + 3 ) % 4 + 12];
				x[( i + 1 ) % 4 + 4] = ( ( ( x[( i + 1 ) % 4 + 4]^x[( i + 2 ) % 4 + 8] ) << ( 64 - 25 ) ) | ( ( x[( i + 1 ) % 4 + 4]^x[( i + 2 ) % 4 + 8] ) >>> 25 ) );

				x[i] += x[( i + 1 ) % 4 + 4];
				x[( i + 3 ) % 4 + 12] = ( ( ( x[( i + 3 ) % 4 + 12]^x[i] ) << ( 64 - 16 ) ) | ( ( x[( i + 3 ) % 4 + 12]^x[i] ) >>> 16 ) );

				x[( i + 2 ) % 4 + 8] += x[( i + 3 ) % 4 + 12];
				x[( i + 1 ) % 4 + 4] = ( ( ( x[( i + 1 ) % 4 + 4]^x[( i + 2 ) % 4 + 8] ) << ( 64 - 11 ) ) | ( ( x[( i + 1 ) % 4 + 4]^x[( i + 2 ) % 4 + 8] ) >>> 11 ) );

			}

		}

	}
	
	
	public static void main ( String[] args )
	{
		String[] s;
		long[] x = new long[16];
		int i, r;
		long c;
		boolean f;
		
		if ( s[1] == "e" )
		{
			f = true;
		}
		

		for ( i = 0; i < 16; ++i )
		{			
			x[i] = i * 0x7477697468617369;
		}

		for ( i = 0; i < 4; ++i )
		{
			x[i] = ( (uint64_t*) v[2] )[i];
		}

		for ( i = 0; i < 2; ++i )
		{
			x[i + 4] = ( (uint64_t*) v[3] )[i] ;
		}

		rounds ( i, x );

		
		int ch;
		
		while ( ( ch = System.in.read () ) != -1 )
		{
			c = (char) ch;
			
			if (!f&&10 == ( x[0]^c ) % 256)
			{
				System.exit (0);
			}

			System.out.println (x[0]^c);

			x[0] = c^(f?x[0]:x[0]&~255);

			
			rounds ( i, x );
			
		}



		x[0] ^= 1;


		rounds ( i, x );
		

		for ( i = 0; i < 8; ++i )
		{
			System.out.println ( 255 & ( ( x[4]^x[5]) >>> 8 * i ) );
		}

		for ( i = 0; i < 8; ++i )
		{
			System.out.println ( 255 & ( ( x[6]^x[7] ) >>> 8 * i ) );
		}
		
	}
	
}
