
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import asymCipher.CramerShoup;
import signature.Schnorr;


public class App
{
	// Global parametres
	private static BigInteger q;
	private static BigInteger w;
	private static BigInteger p;
	private static BigInteger g;
	private static BigInteger g1;
	private static BigInteger g2;
	
	
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
		
	}
	
	
	private static void keyParGen ()
	{
		CramerShoup user = new CramerShoup ();
		
		// Configuration
		user.config ( p, q, g, g1, g2 );
		
		
		// Sets user password
		System.out.println ( "Digite uma senha:" );
		System.out.print ( "> " );
		
		BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String pw = new String();
		
		try
		{
			pw = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		BigInteger[] pk = user.keyPairGen ( pw );
		
		
		// Prints public key on terminal
		System.out.println ( "\nChave pública:" );
		System.out.println ( "c:" + pk[0].toString ( 16 ) );
		System.out.println ( "d:" + pk[1].toString ( 16 ) );
		System.out.println ( "h:" + pk[2].toString ( 16 ) );
		
	}
	
	
	// TODO
	private static void encryptFile ()
	{
		
	}
	
	
	// TODO
	private static void decryptFile ()
	{
		
	}
	
	
	private static void digitalSign ()
	{
		Schnorr ds = new Schnorr ();
		
		// Configuration
		ds.config ( p, q, g );
		
		
		// Message
		System.out.println ( "Digite a mensagem a ser assinada:" );
		System.out.print ( "> " );
		
		BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String m = new String();
		
		try
		{
			m = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		// Password
		System.out.println ( "\nDigite uma senha:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String pw = new String();
		
		try
		{
			pw = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		// Updates message
		ds.update ( m );
		
		
		// Generates key pair
		BigInteger key = ds.keyPairGen ( pw );

		
		// Signs message
		BigInteger[] sigma = ds.sign ();
		
		
		// Prints public key and signed message on terminal
		System.out.println ( "\nChave Pública:" );
		System.out.println ( key.toString ( 16 ) );
		System.out.println ( "\nAssinatura digital:" );
		System.out.println ( "h: " + sigma[0].toString ( 16 ) );
		System.out.println ( "s: " + sigma[1].toString ( 16 ) );
		
	}
	
	
	private static void verifySign ()
	{
		Schnorr ds = new Schnorr ();
		
		// Configuration
		ds.config ( p, q, g );
		
		
		// Message
		System.out.println ( "Digite a mensagem:" );
		System.out.print ( "> " );
		
		BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String m = new String();
		
		try
		{
			m = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		// Signature
		System.out.println ( "\nDigite a assinatura a ser verificada:" );
		System.out.print ( "h > " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String h = new String();
		
		try
		{
			h = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		System.out.print ( "\ns > " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String s = new String();
		
		try
		{
			s = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		// Public key
		System.out.println ( "\nDigite a chave pública:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String key = new String();
		
		try
		{
			key = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		// Updates message
		ds.update ( m );
		
		BigInteger[] sigma = { new BigInteger ( h, 16 ), new BigInteger ( s, 16 ) };
		
		
		// Verifies message
		boolean valid = ds.verify ( sigma, new BigInteger ( key, 16 ) );
		
		System.out.println ( valid );
		
	}
	
	
	// TODO
	private static void tc ()
	{
		
	}
	
	
	public static void main ( String[] args ) throws IOException
	{
		System.out.println ( "PCS2582 - Segurança da Informação" );
		System.out.println ( "Projeto prático - Cifrador híbrido completo" );
		
		System.out.println ( "\nEntre com a opção desejada:" );
		System.out.println ( "1 - Gerar de par de chaves" );
		System.out.println ( "2 - Encriptar arquivo" );
		System.out.println ( "3 - Decriptar arquivo" );
		System.out.println ( "4 - Assinatura digital" );
		System.out.println ( "5 - Verificar assinatura" );
		System.out.println ( "6 - Encriptação/decriptação 'pura' com Tweetcipher" );
		System.out.println ( "0 - Sair" );
		
		System.out.print ( "\n> " );
		
		
		// Sets global parametres
		setPar ();
		
		
		while ( true )
		{
			BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
			
			String option = new String();
			
			try
			{
				option = input.readLine ();
				
			}
			catch ( Exception e )
			{
				
			}
			
			
			System.out.println ( "" );
		
			
			if ( option.equals ( Integer.toString ( 1 ) ) )
			{
				keyParGen ();
			
			}
			else if ( option.equals ( Integer.toString ( 2 ) ) )
			{
				encryptFile ();
				
			}
			else if ( option.equals ( Integer.toString ( 3 ) ) )
			{
				decryptFile ();
				
			}
			else if ( option.equals ( Integer.toString ( 4 ) ) )
			{
				digitalSign ();
				
			}
			else if ( option.equals ( Integer.toString ( 5 ) ) )
			{
				verifySign ();
				
			}
			else if ( option.equals ( Integer.toString ( 6 ) ) )
			{
				tc ();
				
			}
			else if ( option.equals ( Integer.toString ( 0 ) ) )
			{
				break;
				
			}
			else
			{
				System.out.println ( "Opção inválida" );
				
			}
			
			
			System.out.println ( "\nEntre com a opção desejada:" );
			
			System.out.print ( "\n> " );
			
		}
		
	}

}
