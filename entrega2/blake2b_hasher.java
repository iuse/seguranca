
import java.math.*;


public class blake2b_hasher
{
	private blake2b_core core = new blake2b_core();

	private BigInteger[] rawConfig;
	
	private byte[] key;
	
	private int outputSizeInBytes;
	
	private static Blake2BConfig DefaultConfig = new Blake2BConfig();

	
	// TODO
	public void Init()
	{
		core.Initialise ( rawConfig );
		
		if ( key != null )
		{
	
			core.HashCore ( key, 0, key.length );
		}
		
	}

	
	// TODO
	public byte[] Finish()
	{
		var fullResult = core.HashFinal();
		
		if (outputSizeInBytes != fullResult.Length)
		{
			var result = new byte[outputSizeInBytes];
			Array.Copy(fullResult, result, result.Length);
			return result;
		
		}
		else return fullResult;
		
	}

	
	// TODO
	public Blake2BHasher ( Blake2BConfig config )
	{
		if (config == null)
		{
			config = DefaultConfig;
		
		}
		
		rawConfig = Blake2IvBuilder.ConfigB(config, null);
		
		if (config.Key != null && config.Key.Length != 0)
		{
			key = new byte[128];
			Array.Copy(config.Key, key, config.Key.Length);
		
		}
		
		outputSizeInBytes = config.OutputSizeInBytes;
		
		Init();
		
	}

	
	// TODO
	public override void Update(byte[] data, int start, int count)
	{
		core.HashCore(data, start, count);
	
	}

}
