// A. Fenyo - 2016

package kif.libfc;

import java.nio.charset.*;
import java.util.*;
import org.apache.commons.codec.*;
import org.apache.commons.codec.binary.*;
import org.apache.commons.lang3.*;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;
import com.google.gson.*;

public class Tools {
	final private String KEY;
	final private String IV;
	private String nonce = null;
	private String state = null;
	private String json = null;

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

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public Tools(final String KEY, final String IV) {
		this.KEY = KEY;
		this.IV = IV;
	}

	private String getJson() {
		return json;
	}

	private void setJson(final String json) {
		this.json = json;
	}

	public String encode(final String url) throws DecoderException, DataLengthException, IllegalStateException, InvalidCipherTextException {
		final String str = url + "?nonce=" + getNonce() + "&state=" + getState();
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

	public Identity decode(final String ciphertext_hex) throws DecoderException, DataLengthException, IllegalStateException, InvalidCipherTextException {
		final byte [] ciphertext = Hex.decodeHex(ciphertext_hex.toCharArray());
        final PaddedBufferedBlockCipher aes = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
        final CipherParameters ivAndKey = new ParametersWithIV(new KeyParameter(Hex.decodeHex(KEY.toCharArray())), Hex.decodeHex(IV.toCharArray()));
        aes.init(false, ivAndKey);
        final int minSize = aes.getOutputSize(ciphertext.length);
        final byte [] outBuf = new byte[minSize];
        final int length1 = aes.processBytes(ciphertext, 0, ciphertext.length, outBuf, 0);
        final int length2 = aes.doFinal(outBuf, length1);
        json = new String(outBuf, 0, length1 + length2, Charset.forName("UTF-8"));
        final Identity identity = new Gson().fromJson(json, Identity.class);
		return identity;
	}
	
	public boolean checkSecurity() {
        final Gson gson = new Gson();
        final Identity identity = gson.fromJson(getJson(), Identity.class);
        return identity.getState().equals(getState()) && identity.getNonce().equals(getNonce());
	}
}
