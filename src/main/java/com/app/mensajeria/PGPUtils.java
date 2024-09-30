package com.app.mensajeria;

import org.bouncycastle.openpgp.PGPException;
import org.pgpainless.PGPainless;
import org.pgpainless.key.generation.KeySpec;
import org.pgpainless.key.generation.type.RSA_GENERAL;
import org.pgpainless.key.generation.type.length.RsaLength;
import org.pgpainless.util.Passphrase;

import java.io.IOException;

import static org.bouncycastle.bcpg.PublicKeyAlgorithmTags.RSA_GENERAL;

public class PGPUtil {

    // Método para generar un par de claves PGP
    public static PGPainless.KeyRing generateKeyPair() throws PGPException, IOException {
        return PGPainless.generateKeyRing()
                .withMasterKey(
                        KeySpec.getBuilder(RSA_GENERAL.withLength(RsaLength._2048))
                                .withDefaultKeyFlags()
                                .withDefaultAlgorithms())
                .withPrimaryUserId("usuario@example.com") // Aquí puedes usar el nombre del usuario o su email
                .withoutPassphrase()  // Si quieres añadir una contraseña a la clave privada, puedes hacerlo aquí
                .build();
    }
}
