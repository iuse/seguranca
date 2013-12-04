
package application;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Random;

import asymCipher.CramerShoup;
import signature.Schnorr;
import tweetcipher.Tweetcipher;


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
	
	
	private static void keyParGen () throws IOException
	{
		CramerShoup user = new CramerShoup ();
		
		// Configuration
		user.config ( p, q, g, g1, g2 );
		
		
		// Sets user password
		System.out.println ( "Digite a senha:" );
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
		
		
		// Generates text file with public key
		FileWriter key = new FileWriter ( "pk.txt" );
		
		key.write ( pk[0].toString ( 16 ) + "\n" );
		key.write ( pk[1].toString ( 16 ) + "\n" );
		key.write ( pk[2].toString ( 16 ) );
		
		key.close ();
		
	}
	
	
	private static void encryptFile () throws IOException
	{
		System.out.println ( "Digite o nome do arquivo a ser encriptado:" );
		System.out.print ( "> " );
		
		BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String fileIn = new String ();
		
		try
		{
			fileIn = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		System.out.println ( "\nDigite o nome do arquivo que contém a chave pública do remetente:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String rpkFile = new String();
		
		try
		{
			rpkFile = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
				
		
		// Generates symmetric key
        Random rnd = new Random ();
		BigInteger key = new BigInteger ( 256, rnd );
		key = key.mod ( q );
		
		
		// Generates nonce
        rnd = new Random ();
		BigInteger nonce = new BigInteger ( 128, rnd );
				
		
		// Opens files
		// Contains recipient public key
		FileInputStream rpk = new FileInputStream ( rpkFile );

		// Contains ciphertext
		FileWriter ct = new FileWriter ( fileIn + "_ciphertext.txt" );
		
		
		// Encrypt file
		Tweetcipher tc = new Tweetcipher ( key.toString ( 16 ), nonce.toString ( 16 ) );
		
		tc.encdec ( fileIn, fileIn + "_ciphered.txt", 'e' );
		
		
		// Encrypts symmetric key
		CramerShoup user = new CramerShoup ();
		
		user.config ( p, q, g, g1, g2 );
		
		BigInteger[] pk = new BigInteger[3];
		
		BufferedReader rpkBuffer = new BufferedReader ( new InputStreamReader ( rpk ) );
		
		String rpkLine;
		
		for ( int k = 0; ( rpkLine = rpkBuffer.readLine () ) != null; k++ )
		{
			pk[k] = new BigInteger ( rpkLine, 16 );
			
			//System.out.println ( pk[k].toString ( 16 ) );
		
		}
		
		BigInteger[] s = user.encrypt ( pk, key.toByteArray () );
		
		ct.write ( s[0].toString ( 16 ) + "\n" );
		ct.write ( s[1].toString ( 16 ) + "\n" );
		ct.write ( s[2].toString ( 16 ) + "\n" );
		ct.write ( s[3].toString ( 16 ) + "\n" );
		ct.write ( nonce.toString ( 16 ) );
		
		
		// Closes files
		rpk.close ();
		ct.close ();
		
	}
	
	
	private static void decryptFile () throws IOException
	{
		System.out.println ( "Digite o nome do arquivo a ser decriptado:" );
		System.out.print ( "> " );
		
		BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String fileIn = new String ();
		
		try
		{
			fileIn = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		System.out.println ( "\nDigite o nome do arquivo de saída:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String fileOut = new String();
		
		try
		{
			fileOut = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		System.out.println ( "\nDigite o nome do arquivo que contém o criptograma:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String ctFile = new String();
		
		try
		{
			ctFile = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		System.out.println ( "\nDigite a senha:" );
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
		
		
		// Opens files
		// Contains ciphertext
		FileInputStream ct = new FileInputStream ( ctFile );
		
		
		// Reads ciphertext file
		BigInteger[] c = new BigInteger[5];

		BufferedReader ctBuffer = new BufferedReader ( new InputStreamReader ( ct ) );
		
		String ctLine;
		
		for ( int k = 0; ( ctLine = ctBuffer.readLine () ) != null; k++ )
		{
			c[k] = new BigInteger ( ctLine, 16 );
			
			// System.out.println ( c[k].toString ( 16 ) );
		
		}
		
		
		// Decrypts symmetric key
		CramerShoup user = new CramerShoup ();
		
		user.config ( p, q, g, g1, g2 );
		
		BigInteger[] user_pk = user.keyPairGen ( pw );
		
		byte[] k = user.decrypt ( c );
		
		BigInteger kHex = new BigInteger ( k );
		
		
		// Decrypts file
		Tweetcipher tc = new Tweetcipher ( kHex.toString ( 16 ), c[4].toString ( 16 ) );
		
		tc.encdec ( fileIn, fileOut, 'd' );
		
		
		// Closes files
		ct.close ();
		
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
	
	
	private static void tc () throws IOException
	{
		System.out.println ( "Selecione o modo ('e' para encriptar, 'd' para decriptar):" );
		System.out.print ( "> " );
		
		BufferedReader input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String mode = new String ();
		
		try
		{
			mode = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		System.out.println ( "\nDigite o nome do arquivo a ser encriptado/decriptado:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String fileIn = new String();
		
		try
		{
			fileIn = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		System.out.println ( "\nDigite a chave:" );
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
		
		
		System.out.println ( "Digite o nonce:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String nonce = new String();
		
		try
		{
			nonce = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
		
		
		
		System.out.println ( "\nDigite o nome do arquivo de saída:" );
		System.out.print ( "> " );
		
		input = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		String fileOut = new String();
		
		try
		{
			fileOut = input.readLine ();
			
		}
		catch ( Exception e )
		{
			
		}
				
		
		Tweetcipher tc = new Tweetcipher ( key, nonce );
		
		if ( mode.equals ( "e" ) )
		{
			//String fileOut = "ciphered_" + fileIn;
			
			tc.encdec ( fileIn, fileOut, 'e' );
			
		}
		else
		{
			//String fileOut = "deciphered_" + fileIn;
			
			tc.encdec ( fileIn, fileOut, 'd' );
			
		}
		
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
