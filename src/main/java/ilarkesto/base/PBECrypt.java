/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.base;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PBECrypt {

	public static void main(String[] args) {
		String s = "hello world";
		String encrypted = encrypt(s, "geheim");
		System.out.flush();
		System.out.println(encrypted);
		System.out.flush();
		System.out.println(decrypt(encrypted, "geheim"));
		System.out.println(decrypt(encrypted, "falsch"));
	}

	/*
	 * The "iteration count" for the key generation algorithm. Basically, this means that the processing that
	 * is done to generate the key happens 1000 times. You won't even notice the difference while encrypting
	 * or decrypting text, but an attacker will notice a *big* difference when brute forcing keys!
	 */
	static final int ITERATION_COUNT = 1000;

	/* Length of the salt (see below for details on what the salt is) */
	static final int SALT_LENGTH = 8;

	/* Which encryption algorithm we're using. */
	static final String ALGORITHM = "PBEWithMD5AndDES";

	/*
	 * The name of a provider class to add to the system before running, if using a provider that's not
	 * permanently installed.
	 */
	static final String EXTRA_PROVIDER = null;

	public static String encrypt(String s, String password) {
		try {
			return new String(encrypt(s.getBytes(), password.toCharArray()));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static byte[] encrypt(byte[] input, char[] password) throws Exception {
		/*
		 * Get ourselves a random number generator, needed in a number of places for encrypting.
		 */
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

		/*
		 * A "salt" is considered an essential part of password-based encryption. The salt is selected at
		 * random for each encryption. It is not considered "sensitive", so it is tacked onto the generated
		 * ciphertext without any special processing. It doesn't matter if an attacker actually gets the salt.
		 * The salt is used as part of the key, with the very useful result that if you encrypt the same
		 * plaintext with the same password twice, you get *different* ciphertexts. There are lots of pages on
		 * the 'net with information about salts and password-based encryption, so read them if you want more
		 * details. Suffice to say salt=good, no salt=bad.
		 */
		byte[] salt = new byte[SALT_LENGTH];
		sr.nextBytes(salt);

		/*
		 * We've now got enough information to build the actual key. We do this by encapsulating the variables
		 * in a PBEKeySpec and using a SecretKeyFactory to transform the spec into a key.
		 */
		PBEKeySpec keyspec = new PBEKeySpec(password, salt, ITERATION_COUNT);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey key = skf.generateSecret(keyspec);

		/*
		 * We'll use a ByteArrayOutputStream to conveniently gather up data as it's encrypted.
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/*
		 * We've to a key, but to actually encrypt something, we need a "cipher". The cipher is created, then
		 * initialized with the key, salt, and iteration count. We use a PBEParameterSpec to hold the salt and
		 * iteration count needed by the Cipher object.
		 */
		PBEParameterSpec paramspec = new PBEParameterSpec(salt, ITERATION_COUNT);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, paramspec, sr);

		/*
		 * First, in our output, we need to save the salt in plain unencrypted form.
		 */
		baos.write(salt);

		/*
		 * Next, encrypt our plaintext using the Cipher object, and write it into our output buffer.
		 */
		baos.write(cipher.doFinal(input));

		/*
		 * We're done. For security reasons, we probably want the PBEKeySpec object to clear its internal copy
		 * of the password, so it can't be stolen later.
		 */
		keyspec.clearPassword();
		return baos.toByteArray();
	}

	public static final String decrypt(String s, String password) {
		try {
			return new String(decrypt(s.getBytes(), password.toCharArray()));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static byte[] decrypt(final byte[] input, final char[] password) throws Exception {
		/*
		 * The first SALT_LENGTH bytes of the input ciphertext are actually the salt, not the ciphertext.
		 */
		byte[] salt = new byte[SALT_LENGTH];
		System.arraycopy(input, 0, salt, 0, SALT_LENGTH);

		/*
		 * We can now create a key from our salt (extracted just above), password, and iteration count. Same
		 * procedure to create the key as in encrypt().
		 */
		PBEKeySpec keyspec = new PBEKeySpec(password, salt, ITERATION_COUNT);
		SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey key = skf.generateSecret(keyspec);

		/*
		 * Once again, create a PBEParameterSpec object and a Cipher object.
		 */
		PBEParameterSpec paramspec = new PBEParameterSpec(salt, ITERATION_COUNT);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, paramspec);

		/*
		 * Decrypt the data. The parameters we pass into doFinal() instruct it to skip the first SALT_LENGTH
		 * bytes of input (which are actually the salt), and then to encrypt the next (length - SALT_LENGTH)
		 * bytes, which are the real ciphertext.
		 */
		byte[] output = cipher.doFinal(input, SALT_LENGTH, input.length - SALT_LENGTH);

		/* Clear the password and return the generated plaintext. */
		keyspec.clearPassword();
		return output;
	}

}
