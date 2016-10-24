// A. Fenyo - 2016 - exemple d'implémentation du chiffrement / déchiffrement d'une application dialoguant avec KIF-IdP

package kif.libfc;

import java.net.*;
import org.apache.commons.codec.*;
import org.bouncycastle.crypto.*;
import com.google.gson.*;

public class CommandLine {
	// Démonstration d'une authentification s'appuyant sur le médiateur FranceConnect KIF-IdP

	public static void main(String[] args) throws DataLengthException, IllegalStateException, InvalidCipherTextException, DecoderException, MalformedURLException {
		// Définition des secrets partagés entre le client et le médiateur
		final String KEY = "a6a7ee7abe681c9c4cede8e3366a9ded96b92668ea5e26a31a4b0856341ed224";
		final String IV = "87b7225d16ea2ae1f41d0b13fdce9bba";

		////////////////////////////////////////////////////////
		// PHASE 1
		// Le client construit l'URL de callback, la chiffre et renvoie le navigateur vers KIF-IdP en insérant cette URL de callback en paramètre de la requête GET sur KIF-IdP 

		// Création d'une instance de kif.libfc.Tools, avec la clé a6a7ee7abe681c9c4cede8e3366a9ded96b92668ea5e26a31a4b0856341ed224 et l'IV 87b7225d16ea2ae1f41d0b13fdce9bba
		final Tools france_connect = new Tools(KEY, IV);

		// Préparation de l'URL de callback vers le client
		final String callback_url = "https://application.societe.fr/fc/identite.cgi";

		// Création de la variable d'état qui fait la jointure entre cet appel et le retour du navigateur après authentification, par exemple avec le contenu de JSESSIONID
		// exemple : france_connect.setState(request.getSession().getId());
		// pour cette démo, on choisit arbitrairement la valeur f894bb7061a7c2a2
		france_connect.setState("f894bb7061a7c2a2");

		// OPTIONNEL : création du nonce de manière aléatoire (s'il n'est pas créé, il le sera automatiquement par kif.libfc.Tools)
		// pour cette démo, on positionne une valeur arbitraire donc non aléatoire
		france_connect.setNonce("2ff22cb9663990d009fd0dfe87d997c6");

		// Sauvegarde du nonce dans la session pour réutilisation lors du déchiffrement
		// exemple : request.getSession().setAttribute("fc_nonce", france_connect.getNonce());
		// Pour cette démo, on ne dispose pas de session, on réutilisera le nonce en utilisant sa valeur positionnée arbitrairement

		// Chiffrement de l'URL de callback qui contient le nonce et la variable d'état
		final String msg = france_connect.encode(callback_url);

		// Création de l'URL de KIF-IdP vers laquelle renvoyer le navigateur, et contenant l'URL de callback chiffrée
		final String url_to_call="https://mediateur.societe.fr/idp?msg=" + msg;
		System.out.println("Renvoyer l'utilisateur vers le médiateur via une redirection vers l'URL suivante :\n" +url_to_call);

		////////////////////////////////////////////////////////
		// PHASE 2
		// Le médiateur reçoit la requête du navigateur, authentifie l'assuré et le redirige vers l'URL de callback du client, en y ajoutant l'identité chiffrée de l'assuré

		////////////////////////////////////////////////////////
		// PHASE 3
		// Le client reçoit donc une requête comme celle-ci et doit la déchiffrer pour récupérer l'identité de l'utilisateur authentifié :
		// https://application.societe.fr/fc/identite.cgi?nonce=2ff22cb9663990d009fd0dfe87d997c6&state=f894bb7061a7c2a2&info=b74f31907bb2d9be0ab2750c29dc4839061af809ada6217b237690a577f96f76d3f0f8633b28c8125aa89225b47930929e1e406a09ab6488614c312d51ddc8d61f924e11d0b7df694abc197706b9ff4cbbc398c31368c36b54adb232e8bb99ff06f587f97c72c7936d39261126531ce5d0fde886f48f01a3e6b4737f054b9b24acac6d0b6aec2c9d73b2a3e8fa5aee68819e33a083496e712a103bd6adb0abc83521c6c4e1e2d0e28ccf4f35c06c9473e399c258ee98775cda1c83b0c07eaa1072ba513ad7c301376899bd65cb77edc736eb8fff9fd3b41400c1cc455c6dbc6b9f9c8dc464e3f2327ee143f6aa22ee8e3900aba48c7a04998329cfbfc4119788b4b4a61441f059c5c5aa3dfa45de2676ffdfa38c5735c6e6711b2e531c2e11c283fc9fae15922c0ecfdb347fc83832bb88f5bf6820462f9fb683a7b6b0fa0225e5ac13c786eacba05caee8ea1ae97dbd7c851b7fd55fb62a4a30619829c4987a5d723a2f817711fd31996ef95d56500c257315b800f16688926786387d953d7cedd3a1f4e59e689ba0d3ecf61bb1b15059bbdfb3e57b22879a7df34fdb2e41b9e5cf432919f9d3aa90e1c8c3ad78cf87d913735bfd35e8ba31c7013e1778c7670be5c173e7e93e31b3cf923b31c356d7e514ed355bf2b4af7196e2beaee84de254da8e407dae29bdc4071a26a4af9c7d
		 
		// Le client utiliser la méthode HttpServletRequest.getParameter("info") pour récupérer le message chiffré
		final String ciphertext_hex = "b74f31907bb2d9be0ab2750c29dc4839061af809ada6217b237690a577f96f76d3f0f8633b28c8125aa89225b47930929e1e406a09ab6488614c312d51ddc8d61f924e11d0b7df694abc197706b9ff4cbbc398c31368c36b54adb232e8bb99ff06f587f97c72c7936d39261126531ce5d0fde886f48f01a3e6b4737f054b9b24acac6d0b6aec2c9d73b2a3e8fa5aee68819e33a083496e712a103bd6adb0abc83521c6c4e1e2d0e28ccf4f35c06c9473e399c258ee98775cda1c83b0c07eaa1072ba513ad7c301376899bd65cb77edc736eb8fff9fd3b41400c1cc455c6dbc6b9f9c8dc464e3f2327ee143f6aa22ee8e3900aba48c7a04998329cfbfc4119788b4b4a61441f059c5c5aa3dfa45de2676ffdfa38c5735c6e6711b2e531c2e11c283fc9fae15922c0ecfdb347fc83832bb88f5bf6820462f9fb683a7b6b0fa0225e5ac13c786eacba05caee8ea1ae97dbd7c851b7fd55fb62a4a30619829c4987a5d723a2f817711fd31996ef95d56500c257315b800f16688926786387d953d7cedd3a1f4e59e689ba0d3ecf61bb1b15059bbdfb3e57b22879a7df34fdb2e41b9dde035d5a02e084c88e062ac25a8ae0bc85b56a68403548a92e9fc39ed31b1144541931f774d746865af4b9125874999ef6770beec35490a5090cc24d03969bc";

		// Création d'une instance de kif.libfc.Tools, avec la clé a6a7ee7abe681c9c4cede8e3366a9ded96b92668ea5e26a31a4b0856341ed224 et l'IV 87b7225d16ea2ae1f41d0b13fdce9bba
		final Tools france_connect2 = new Tools(KEY, IV);
		final Identity identity = france_connect2.decode(ciphertext_hex);
        
		////////////////////////////////////////////////////////
        // PHASE 4
		// le client valide la sécurité de l'échange puis récupère les champs de l'état civil

        // Récupération de l'ID de session (JSESSIONID)
		// exemple : france_connect2.setState(request.getSession().getId());
        // pour cette démo, on reprend la valeur positionnée arbitrairement dans la phase 1
        france_connect2.setState("f894bb7061a7c2a2");

        // Récupération du nonce sauvegardé dans la session
        // exemple : france_connect2.setNonce(request.getSession().getAttribute("fc_nonce"));
        // pour cette démo, on reprend la valeur positionnée arbitrairement dans la phase 1
        france_connect2.setNonce("2ff22cb9663990d009fd0dfe87d997c6");

        // Validation de la sécurité (pas de rejeu et session correspondant à l'appel initial)
        if (france_connect2.checkSecurity() == false) {
        	System.err.println("erreur d'authentification");
        	System.exit(1);
        }

        // Affichage de l'identité
        System.out.println("sub: " + identity.getSub());
        System.out.println("gender: " + identity.getGender());
        System.out.println("birthdate: " + identity.getBirthdate());
        System.out.println("birthcountry: " + identity.getBirthcountry());
        System.out.println("birthplace: " + identity.getBirthplace());
        System.out.println("given_name: " + identity.getGiven_name());
        System.out.println("family_name: " + identity.getFamily_name());
        System.out.println("address: formatted: " + identity.getAddress().getFormatted());
        System.out.println("address: street_address: " + identity.getAddress().getStreet_address());
        System.out.println("address: locality: " + identity.getAddress().getLocality());
        System.out.println("address: region: " + identity.getAddress().getRegion());
        System.out.println("address: postal_code: " + identity.getAddress().getPostal_code());
        System.out.println("address: country: " + identity.getAddress().getCountry());

		System.out.println("Fin.");
	}
}
