package kif.libfc;

import java.util.Random;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class Tools {
	final private String KEY;
	final private String IV;
	private String nonce = null;

	private void createNonce() {
		final byte bytes[] = new byte[16];
		new Random().nextBytes(bytes);
		nonce = Hex.encodeHexString(bytes);
	}
	
	public String getNonce() {
		if (nonce == null) createNonce();
		return nonce;
	}

	public void setNonce(final String nonce) {
		this.nonce = nonce;
	}

	public Tools(final String KEY, final String IV) {
		this.KEY = KEY;
		this.IV = IV;
	}

	public String encode(final String url, final String state) throws DecoderException, DataLengthException, IllegalStateException, InvalidCipherTextException {
		final String str = url + "?state=" + state + "&nonce=" + getNonce();
		final byte [] info_plaintext = str.getBytes();
        final PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
        final CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(Hex.decodeHex(KEY.toCharArray())), Hex.decodeHex(IV.toCharArray()));
        aes.init(true, ivAndKey);
        final byte [] inputBuf = new byte[aes.getOutputSize(info_plaintext.length)];
        final int length1 = aes.processBytes(info_plaintext, 0, info_plaintext.length, inputBuf, 0);
        final int length2 = aes.doFinal(inputBuf, length1);
        final byte [] info_ciphertext = ArrayUtils.subarray(inputBuf, 0, length1 + length2);
        final String info_ciphertext_hex = new String(Hex.encodeHex(info_ciphertext));
		return info_ciphertext_hex;
	}
	
}
