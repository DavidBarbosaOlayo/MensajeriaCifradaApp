package com.app.mensajeria;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.*;
import org.bouncycastle.bcpg.ArmoredOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class PGPManager {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // Método para generar y guardar claves PGP (pública y privada)
    public static void generateKeys(String publicKeyPath, String privateKeyPath) throws Exception {
        System.out.println("Generando claves...");
        System.out.println("Clave pública guardada en: " + publicKeyPath);
        System.out.println("Clave privada guardada en: " + privateKeyPath);

        // Iniciamos el generador de claves RSA
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(4096);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Convertimos las claves RSA en un formato compatible con PGP
        PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, keyPair, new Date());

        // Creamos un generador de anillo de claves PGP
        PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(
                PGPSignature.POSITIVE_CERTIFICATION,  // Certificamos la autenticidad de la clave
                pgpKeyPair,
                "usuario@example.com",  // Identificador del usuario
                new JcaPGPDigestCalculatorProviderBuilder().build().get(PGPUtil.SHA1),
                null, null,
                new JcaPGPContentSignerBuilder(pgpKeyPair.getPublicKey().getAlgorithm(), PGPUtil.SHA256).setProvider("BC"),
                new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.CAST5, new JcaPGPDigestCalculatorProviderBuilder().build().get(PGPUtil.SHA1))
                        .setProvider("BC").build("password".toCharArray())
        );

        // Guardamos la clave pública en un archivo .txt
        try (ArmoredOutputStream pubOut = new ArmoredOutputStream(new FileOutputStream(publicKeyPath))) {
            PGPPublicKeyRing publicKeyRing = keyRingGen.generatePublicKeyRing();
            publicKeyRing.encode(pubOut);
            System.out.println("Clave pública generada correctamente.");
        } catch (IOException e) {
            System.out.println("Error al generar la clave pública: " + e.getMessage());
        }

        // Guardamos la clave privada en un archivo .txt
        try (ArmoredOutputStream secOut = new ArmoredOutputStream(new FileOutputStream(privateKeyPath))) {
            PGPSecretKeyRing secretKeyRing = keyRingGen.generateSecretKeyRing();
            secretKeyRing.encode(secOut);
            System.out.println("Clave privada generada correctamente.");
        } catch (IOException e) {
            System.out.println("Error al generar la clave privada: " + e.getMessage());
        }
    }

    // Método para encriptar un mensaje usando la clave pública
    public static String encryptMessage(String message, PGPPublicKey publicKey) throws IOException, PGPException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(outputStream);

        // Crear el generador de datos cifrados
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                        .setWithIntegrityPacket(true)
                        .setSecureRandom(new SecureRandom())
                        .setProvider("BC")
        );

        // Añadir la clave pública del destinatario
        encryptedDataGenerator.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"));

        // Cifrar el mensaje
        try (ByteArrayOutputStream literalData = new ByteArrayOutputStream()) {
            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            literalDataGenerator.open(literalData, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, new java.util.Date(), new byte[4096])
                    .write(message.getBytes());
            literalDataGenerator.close();

            // Escribir los datos cifrados
            try (var encryptedOut = encryptedDataGenerator.open(armoredOutputStream, new byte[4096])) {
                encryptedOut.write(literalData.toByteArray());
            }
        } finally {
            armoredOutputStream.close();
        }

        return new String(outputStream.toByteArray());
    }

    // Método para cargar la clave pública desde un archivo
    public static PGPPublicKey loadPublicKeyFromFile(String filePath) throws IOException, PGPException {
        try (FileInputStream keyIn = new FileInputStream(filePath)) {
            PGPObjectFactory pgpFact = new PGPObjectFactory(PGPUtil.getDecoderStream(keyIn), new BcKeyFingerprintCalculator());
            PGPPublicKeyRing keyRing = (PGPPublicKeyRing) pgpFact.nextObject();
            return keyRing.getPublicKey();
        }
    }
}
