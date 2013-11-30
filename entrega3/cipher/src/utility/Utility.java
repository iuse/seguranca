
package utility;


public class Utility
{
	// Appends a byte array to another
	public static byte[] append ( byte[] a, byte[] b )
	{    
        byte[] c = new byte[a.length + b.length];
        
        System.arraycopy ( a, 0, c, 0, a.length );
        System.arraycopy ( b, 0, c, a.length, b.length );
        
        return c;
    }

}
