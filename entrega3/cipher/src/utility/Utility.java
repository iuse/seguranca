
package utility;

import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Contains helper functions
 * 
 * @author Andre Hashimoto Oku
 * @author Soon Hyung Kwon
 */
public class Utility
{
	/**
	 * Appends a byte array to another
	 * 
	 * @param a Byte array to append to
	 * @param b Byte array appended
	 * @return Returns appended byte array
	 */
	public static byte[] append ( byte[] a, byte[] b )
	{    
        byte[] c = new byte[a.length + b.length];
        
        System.arraycopy ( a, 0, c, 0, a.length );
        System.arraycopy ( b, 0, c, a.length, b.length );
        
        return c;
    }

}
