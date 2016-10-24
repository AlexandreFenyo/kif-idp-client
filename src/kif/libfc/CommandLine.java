package kif.libfc;

import org.apache.commons.codec.DecoderException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;

import kif.libfc.Tools;

public class CommandLine {

	public static void main(String[] args) throws DataLengthException, IllegalStateException, InvalidCipherTextException, DecoderException {
		// TODO Auto-generated method stub
		final Tools france_connect = new Tools("a6a7ee7abe681c9c4cede8e3366a9ded96b92668ea5e26a31a4b0856341ed224", "87b7225d16ea2ae1f41d0b13fdce9bba");
		//final String callback_url = "https://www.mon.rsi.fr/identite";
		final String callback_url = "https://fenyo.net/fc/identite.cgi";
		france_connect.setNonce("2ff22cb9663990d009fd0dfe87d997c6");
		final String s = france_connect.encode(callback_url, "f894bb7061a7c2a2");
		System.out.println("[" + s + "]");
		
//		System.out.println(france_connect.getNonce());
//		System.out.println("Fin.");
	}
}
