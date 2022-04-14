package eu.europa.esig.dss.pades;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.signature.resources.InMemoryResourcesHandlerBuilder;
import eu.europa.esig.dss.signature.resources.TempFileResourcesHandlerBuilder;
import eu.europa.esig.dss.spi.DSSASN1Utils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class PAdESUtilsTest {

    @Test
    public void replaceSignatureTest() {
        DSSDocument documentToBeSigned = new InMemoryDocument(getClass().getResourceAsStream("/validation/pdf-with-empty-sig.pdf"));
        String hexEncodedCMSSignedData = "30820a5c06092a864886f70d010702a0820a4d30820a49020101310d300b0609608648016503040201300b06092a864886f70d010701a08207c6308203d4308202bca00302010202010a300d06092a864886f70d01010b0500304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031365a170d3233303230313135303031365a304f3112301006035504030c09676f6f642d7573657231193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100c1b55d02c50ecfac393a2dd7596e0071065e014173f63636ac7418b5f4ca3d9f19d9603e6d93df8d8e56b9dcd74fe2e0fa4db94eccd78af6de5ab83afcbf6d9f760fa0f1ddf088ccb5c6e387361f36c412027d8be783baf659b508ca1430d85d7abbeb63b85d02377dd0bbf7e803fc0d714c42887ca976628cde426d06747e8696bd7ea8c394cef83125e6a06fc5c5a5b00297cd341a28c2a65f3b202d65907d1e45da90d1805000b88630c1d527bfbe3cafff126d336280b65687b795fcf62184d226dc2f75ae06cee5d3ba72ffe825ec20c59f3d733fd9cdb2bd9b617d0c920ae1792e92fe6911eadbb54a2cb48d0178fddd373a188b46591d93089f765a890203010001a381bc3081b9300e0603551d0f0101ff04040302064030818706082b06010505070101047b3079303906082b06010505073001862d687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6f6373702f676f6f642d6361303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f676f6f642d63612e637274301d0603551d0e04160414c955b158d2d00b1934135c4cf1cb44602d0f9626300d06092a864886f70d01010b05000382010100170508d04877b17b9424b11a3e26deba9a062bf4725442b38691ec749351905e6fbdd7db899cf18bc97cc75b409908203e133c8ea802976886cf12047750a96467d52485167b682d9252b44b9c1bcff08a9373f249b996978d0250e72347ad67e7016ea6eac982f16b1514086d526ce5687033e8c3fbb4af5d26c09eeaf04fca351b0e226a46d84467b9fcc7d648dd8da73b4647b867691524e5bbd198dfe03de84354128f710da3eb754ff0119a1b792dbc050a333dc374d27683a088e4975e32fd50447fca4309f1de2db4c046c021f93092eb016ae4284d13f5a7c64059a4eb7e10053b5cc5ce54424f3608107a020845d0acf24617c58d5ee90e36bcd692308203ea308202d2a003020102020104300d06092a864886f70d01010b0500304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031355a170d3233303230313135303031355a304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100b8c1570e4e6aa6bbc77cc74b0488d2424ee9ab0a5be5d7963b90e8165fb8145e0e066202f67b5b34fa23be89bda3f3d521d2c60cce3ba03a289cc60766fc605f3a00193deeaed513c59bf790a580a7d7c2e2c5834d0f80c60441f46b650fc276edceb48bc8828a0cde3fab7422b525564ba6297a24e2e9b685600db3e498984dd1d347d59889a4e6ed3f2020f90efe52d053e13bc843e5c632d81ccc4fc176ed4d63e12a29bb00d7351a7969de74a22dedef3eb9e08b86709d9e394a929a6265a6de9c26350f9a6422d7a3743a8215b785df7c3aad04e241434b1db1a46740e5344b3da58529ac81d5a18f95269e3f2b837e31343e28334e213f33a1acbd552f0203010001a381d43081d1300e0603551d0f0101ff04040302010630410603551d1f043a30383036a034a0328630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f63726c2f726f6f742d63612e63726c304c06082b060105050701010440303e303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f726f6f742d63612e637274301d0603551d0e04160414e4dd8eebbb9498c75e99e0947fe37bc5e1cf2465300f0603551d130101ff040530030101ff300d06092a864886f70d01010b050003820101005ebe2b6d1046c9da4fd75a03dd4b32bba4ad46cc44cf9dfb3bdc42905f27d49bc61973e85ff2314b124e6cb2481ecc41d38919d5fd2e0b4b863a1daf01459f87e6223baef6e41e4e01bdf14bcf516743b4c3bebad9901d94fca89a8e828d1e9874bd9892406631107d4548d53b4def41daca35c5edb95588228981a2b1d6f761c5b9d35bc639deb63b4d6436ab60a22637ba36230cc8da8ba7bf8f24d919bad040e6025e6bda031008dbf1e414e631fb7bd921f311eff85fc4f0353a8fe1413c4fdd7779b68c33ed7329604e08e2dd5155f122f556e3522375fe8e66d15bb09bdd4a528d538f39f0329afcc737dadc568c638b2891e5fd8f93a4919e3e853c523182025c308202580201013052304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300b0609608648016503040201a081de301806092a864886f70d010903310b06092a864886f70d010701302f06092a864886f70d01090431220420eff26b2e4dfb288bde8e44e7b03557ddc57c88e22030598255a3e53a6c65b0fa308190060b2a864886f70d010910022f318180307e307c307a042013850c5270fcad4184bb3279338e1b6217951751c3ea673b385f8d8cd9ff87d330563051a44f304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300d06092a864886f70d01010b050004820100401289320753c3f3e66c90bb1a883047a686170a9d97911ca6c74741c5f3eb7bf1642b45eefe4151dd01e800ea97b626624dce05485bf37184d596a4ca7112bafe84b3127950f712217310e0d048d728d1398ef0d93aa39758bf9f20fe6ba46c2abbf0344af82ec6cb76b725d43c809e4a272f60ac85d2d50d52d3e5b33226636fd1764772662d07990725c7c08132f824899e9c2b6e9223fc742bedfd536c250c211ab780e23130166727c71be56343614e4b3dc0e5ce546bda8435d71f4e38b95348d726905cb0abb775a957b5c0289aad45e83b47f828a6de38536a14cc1e955be447e6143c2b59b52edde1599053da73a7d5fa1e1e7ac91aa0e52f35a2c1";

        DSSDocument signedDocument = PAdESUtils.replaceSignature(documentToBeSigned,
                DSSASN1Utils.getDEREncoded(Utils.fromHex(hexEncodedCMSSignedData)), new InMemoryResourcesHandlerBuilder());

        PDFDocumentValidator validator = new PDFDocumentValidator(signedDocument);
        validator.setCertificateVerifier(new CommonCertificateVerifier());

        Reports reports = validator.validateDocument();
        DiagnosticData diagnosticData = reports.getDiagnosticData();
        assertEquals(1, diagnosticData.getSignatures().size());

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertTrue(signature.isSignatureIntact());
        assertTrue(signature.isSignatureValid());
    }

    @Test
    public void replaceSignatureWithTempFileHandlerTest() {
        DSSDocument documentToBeSigned = new InMemoryDocument(getClass().getResourceAsStream("/validation/pdf-with-empty-sig.pdf"));
        String hexEncodedCMSSignedData = "30820a5c06092a864886f70d010702a0820a4d30820a49020101310d300b0609608648016503040201300b06092a864886f70d010701a08207c6308203d4308202bca00302010202010a300d06092a864886f70d01010b0500304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031365a170d3233303230313135303031365a304f3112301006035504030c09676f6f642d7573657231193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100c1b55d02c50ecfac393a2dd7596e0071065e014173f63636ac7418b5f4ca3d9f19d9603e6d93df8d8e56b9dcd74fe2e0fa4db94eccd78af6de5ab83afcbf6d9f760fa0f1ddf088ccb5c6e387361f36c412027d8be783baf659b508ca1430d85d7abbeb63b85d02377dd0bbf7e803fc0d714c42887ca976628cde426d06747e8696bd7ea8c394cef83125e6a06fc5c5a5b00297cd341a28c2a65f3b202d65907d1e45da90d1805000b88630c1d527bfbe3cafff126d336280b65687b795fcf62184d226dc2f75ae06cee5d3ba72ffe825ec20c59f3d733fd9cdb2bd9b617d0c920ae1792e92fe6911eadbb54a2cb48d0178fddd373a188b46591d93089f765a890203010001a381bc3081b9300e0603551d0f0101ff04040302064030818706082b06010505070101047b3079303906082b06010505073001862d687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6f6373702f676f6f642d6361303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f676f6f642d63612e637274301d0603551d0e04160414c955b158d2d00b1934135c4cf1cb44602d0f9626300d06092a864886f70d01010b05000382010100170508d04877b17b9424b11a3e26deba9a062bf4725442b38691ec749351905e6fbdd7db899cf18bc97cc75b409908203e133c8ea802976886cf12047750a96467d52485167b682d9252b44b9c1bcff08a9373f249b996978d0250e72347ad67e7016ea6eac982f16b1514086d526ce5687033e8c3fbb4af5d26c09eeaf04fca351b0e226a46d84467b9fcc7d648dd8da73b4647b867691524e5bbd198dfe03de84354128f710da3eb754ff0119a1b792dbc050a333dc374d27683a088e4975e32fd50447fca4309f1de2db4c046c021f93092eb016ae4284d13f5a7c64059a4eb7e10053b5cc5ce54424f3608107a020845d0acf24617c58d5ee90e36bcd692308203ea308202d2a003020102020104300d06092a864886f70d01010b0500304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031355a170d3233303230313135303031355a304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100b8c1570e4e6aa6bbc77cc74b0488d2424ee9ab0a5be5d7963b90e8165fb8145e0e066202f67b5b34fa23be89bda3f3d521d2c60cce3ba03a289cc60766fc605f3a00193deeaed513c59bf790a580a7d7c2e2c5834d0f80c60441f46b650fc276edceb48bc8828a0cde3fab7422b525564ba6297a24e2e9b685600db3e498984dd1d347d59889a4e6ed3f2020f90efe52d053e13bc843e5c632d81ccc4fc176ed4d63e12a29bb00d7351a7969de74a22dedef3eb9e08b86709d9e394a929a6265a6de9c26350f9a6422d7a3743a8215b785df7c3aad04e241434b1db1a46740e5344b3da58529ac81d5a18f95269e3f2b837e31343e28334e213f33a1acbd552f0203010001a381d43081d1300e0603551d0f0101ff04040302010630410603551d1f043a30383036a034a0328630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f63726c2f726f6f742d63612e63726c304c06082b060105050701010440303e303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f726f6f742d63612e637274301d0603551d0e04160414e4dd8eebbb9498c75e99e0947fe37bc5e1cf2465300f0603551d130101ff040530030101ff300d06092a864886f70d01010b050003820101005ebe2b6d1046c9da4fd75a03dd4b32bba4ad46cc44cf9dfb3bdc42905f27d49bc61973e85ff2314b124e6cb2481ecc41d38919d5fd2e0b4b863a1daf01459f87e6223baef6e41e4e01bdf14bcf516743b4c3bebad9901d94fca89a8e828d1e9874bd9892406631107d4548d53b4def41daca35c5edb95588228981a2b1d6f761c5b9d35bc639deb63b4d6436ab60a22637ba36230cc8da8ba7bf8f24d919bad040e6025e6bda031008dbf1e414e631fb7bd921f311eff85fc4f0353a8fe1413c4fdd7779b68c33ed7329604e08e2dd5155f122f556e3522375fe8e66d15bb09bdd4a528d538f39f0329afcc737dadc568c638b2891e5fd8f93a4919e3e853c523182025c308202580201013052304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300b0609608648016503040201a081de301806092a864886f70d010903310b06092a864886f70d010701302f06092a864886f70d01090431220420eff26b2e4dfb288bde8e44e7b03557ddc57c88e22030598255a3e53a6c65b0fa308190060b2a864886f70d010910022f318180307e307c307a042013850c5270fcad4184bb3279338e1b6217951751c3ea673b385f8d8cd9ff87d330563051a44f304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300d06092a864886f70d01010b050004820100401289320753c3f3e66c90bb1a883047a686170a9d97911ca6c74741c5f3eb7bf1642b45eefe4151dd01e800ea97b626624dce05485bf37184d596a4ca7112bafe84b3127950f712217310e0d048d728d1398ef0d93aa39758bf9f20fe6ba46c2abbf0344af82ec6cb76b725d43c809e4a272f60ac85d2d50d52d3e5b33226636fd1764772662d07990725c7c08132f824899e9c2b6e9223fc742bedfd536c250c211ab780e23130166727c71be56343614e4b3dc0e5ce546bda8435d71f4e38b95348d726905cb0abb775a957b5c0289aad45e83b47f828a6de38536a14cc1e955be447e6143c2b59b52edde1599053da73a7d5fa1e1e7ac91aa0e52f35a2c1";

        TempFileResourcesHandlerBuilder tempFileResourcesHandlerBuilder = new TempFileResourcesHandlerBuilder();
        tempFileResourcesHandlerBuilder.setTempFileDirectory(new File("target"));

        DSSDocument signedDocument = PAdESUtils.replaceSignature(documentToBeSigned,
                DSSASN1Utils.getDEREncoded(Utils.fromHex(hexEncodedCMSSignedData)),tempFileResourcesHandlerBuilder);

        PDFDocumentValidator validator = new PDFDocumentValidator(signedDocument);
        validator.setCertificateVerifier(new CommonCertificateVerifier());

        Reports reports = validator.validateDocument();
        DiagnosticData diagnosticData = reports.getDiagnosticData();
        assertEquals(1, diagnosticData.getSignatures().size());

        SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
        assertTrue(signature.isSignatureIntact());
        assertTrue(signature.isSignatureValid());
    }

    @Test
    public void replaceSignatureInEmptyFileTest() {
        DSSDocument documentToBeSigned = new InMemoryDocument(getClass().getResourceAsStream("/sample.pdf"));
        String hexEncodedCMSSignedData = "30820a5c06092a864886f70d010702a0820a4d30820a49020101310d300b0609608648016503040201300b06092a864886f70d010701a08207c6308203d4308202bca00302010202010a300d06092a864886f70d01010b0500304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031365a170d3233303230313135303031365a304f3112301006035504030c09676f6f642d7573657231193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100c1b55d02c50ecfac393a2dd7596e0071065e014173f63636ac7418b5f4ca3d9f19d9603e6d93df8d8e56b9dcd74fe2e0fa4db94eccd78af6de5ab83afcbf6d9f760fa0f1ddf088ccb5c6e387361f36c412027d8be783baf659b508ca1430d85d7abbeb63b85d02377dd0bbf7e803fc0d714c42887ca976628cde426d06747e8696bd7ea8c394cef83125e6a06fc5c5a5b00297cd341a28c2a65f3b202d65907d1e45da90d1805000b88630c1d527bfbe3cafff126d336280b65687b795fcf62184d226dc2f75ae06cee5d3ba72ffe825ec20c59f3d733fd9cdb2bd9b617d0c920ae1792e92fe6911eadbb54a2cb48d0178fddd373a188b46591d93089f765a890203010001a381bc3081b9300e0603551d0f0101ff04040302064030818706082b06010505070101047b3079303906082b06010505073001862d687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6f6373702f676f6f642d6361303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f676f6f642d63612e637274301d0603551d0e04160414c955b158d2d00b1934135c4cf1cb44602d0f9626300d06092a864886f70d01010b05000382010100170508d04877b17b9424b11a3e26deba9a062bf4725442b38691ec749351905e6fbdd7db899cf18bc97cc75b409908203e133c8ea802976886cf12047750a96467d52485167b682d9252b44b9c1bcff08a9373f249b996978d0250e72347ad67e7016ea6eac982f16b1514086d526ce5687033e8c3fbb4af5d26c09eeaf04fca351b0e226a46d84467b9fcc7d648dd8da73b4647b867691524e5bbd198dfe03de84354128f710da3eb754ff0119a1b792dbc050a333dc374d27683a088e4975e32fd50447fca4309f1de2db4c046c021f93092eb016ae4284d13f5a7c64059a4eb7e10053b5cc5ce54424f3608107a020845d0acf24617c58d5ee90e36bcd692308203ea308202d2a003020102020104300d06092a864886f70d01010b0500304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031355a170d3233303230313135303031355a304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100b8c1570e4e6aa6bbc77cc74b0488d2424ee9ab0a5be5d7963b90e8165fb8145e0e066202f67b5b34fa23be89bda3f3d521d2c60cce3ba03a289cc60766fc605f3a00193deeaed513c59bf790a580a7d7c2e2c5834d0f80c60441f46b650fc276edceb48bc8828a0cde3fab7422b525564ba6297a24e2e9b685600db3e498984dd1d347d59889a4e6ed3f2020f90efe52d053e13bc843e5c632d81ccc4fc176ed4d63e12a29bb00d7351a7969de74a22dedef3eb9e08b86709d9e394a929a6265a6de9c26350f9a6422d7a3743a8215b785df7c3aad04e241434b1db1a46740e5344b3da58529ac81d5a18f95269e3f2b837e31343e28334e213f33a1acbd552f0203010001a381d43081d1300e0603551d0f0101ff04040302010630410603551d1f043a30383036a034a0328630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f63726c2f726f6f742d63612e63726c304c06082b060105050701010440303e303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f726f6f742d63612e637274301d0603551d0e04160414e4dd8eebbb9498c75e99e0947fe37bc5e1cf2465300f0603551d130101ff040530030101ff300d06092a864886f70d01010b050003820101005ebe2b6d1046c9da4fd75a03dd4b32bba4ad46cc44cf9dfb3bdc42905f27d49bc61973e85ff2314b124e6cb2481ecc41d38919d5fd2e0b4b863a1daf01459f87e6223baef6e41e4e01bdf14bcf516743b4c3bebad9901d94fca89a8e828d1e9874bd9892406631107d4548d53b4def41daca35c5edb95588228981a2b1d6f761c5b9d35bc639deb63b4d6436ab60a22637ba36230cc8da8ba7bf8f24d919bad040e6025e6bda031008dbf1e414e631fb7bd921f311eff85fc4f0353a8fe1413c4fdd7779b68c33ed7329604e08e2dd5155f122f556e3522375fe8e66d15bb09bdd4a528d538f39f0329afcc737dadc568c638b2891e5fd8f93a4919e3e853c523182025c308202580201013052304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300b0609608648016503040201a081de301806092a864886f70d010903310b06092a864886f70d010701302f06092a864886f70d01090431220420eff26b2e4dfb288bde8e44e7b03557ddc57c88e22030598255a3e53a6c65b0fa308190060b2a864886f70d010910022f318180307e307c307a042013850c5270fcad4184bb3279338e1b6217951751c3ea673b385f8d8cd9ff87d330563051a44f304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300d06092a864886f70d01010b050004820100401289320753c3f3e66c90bb1a883047a686170a9d97911ca6c74741c5f3eb7bf1642b45eefe4151dd01e800ea97b626624dce05485bf37184d596a4ca7112bafe84b3127950f712217310e0d048d728d1398ef0d93aa39758bf9f20fe6ba46c2abbf0344af82ec6cb76b725d43c809e4a272f60ac85d2d50d52d3e5b33226636fd1764772662d07990725c7c08132f824899e9c2b6e9223fc742bedfd536c250c211ab780e23130166727c71be56343614e4b3dc0e5ce546bda8435d71f4e38b95348d726905cb0abb775a957b5c0289aad45e83b47f828a6de38536a14cc1e955be447e6143c2b59b52edde1599053da73a7d5fa1e1e7ac91aa0e52f35a2c1";

        Exception exception = assertThrows(DSSException.class, () -> PAdESUtils.replaceSignature(documentToBeSigned,
                DSSASN1Utils.getDEREncoded(Utils.fromHex(hexEncodedCMSSignedData)), new InMemoryResourcesHandlerBuilder()));
        assertEquals("Preserved space to insert a signature was not found!", exception.getMessage());
    }

    @Test
    public void replaceSignatureInAFileWithDoubleSpaceTest() {
        DSSDocument documentToBeSigned = new InMemoryDocument(getClass().getResourceAsStream("/validation/pdf-with-two-empty-sigs.pdf"));
        String hexEncodedCMSSignedData = "30820a5c06092a864886f70d010702a0820a4d30820a49020101310d300b0609608648016503040201300b06092a864886f70d010701a08207c6308203d4308202bca00302010202010a300d06092a864886f70d01010b0500304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031365a170d3233303230313135303031365a304f3112301006035504030c09676f6f642d7573657231193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100c1b55d02c50ecfac393a2dd7596e0071065e014173f63636ac7418b5f4ca3d9f19d9603e6d93df8d8e56b9dcd74fe2e0fa4db94eccd78af6de5ab83afcbf6d9f760fa0f1ddf088ccb5c6e387361f36c412027d8be783baf659b508ca1430d85d7abbeb63b85d02377dd0bbf7e803fc0d714c42887ca976628cde426d06747e8696bd7ea8c394cef83125e6a06fc5c5a5b00297cd341a28c2a65f3b202d65907d1e45da90d1805000b88630c1d527bfbe3cafff126d336280b65687b795fcf62184d226dc2f75ae06cee5d3ba72ffe825ec20c59f3d733fd9cdb2bd9b617d0c920ae1792e92fe6911eadbb54a2cb48d0178fddd373a188b46591d93089f765a890203010001a381bc3081b9300e0603551d0f0101ff04040302064030818706082b06010505070101047b3079303906082b06010505073001862d687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6f6373702f676f6f642d6361303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f676f6f642d63612e637274301d0603551d0e04160414c955b158d2d00b1934135c4cf1cb44602d0f9626300d06092a864886f70d01010b05000382010100170508d04877b17b9424b11a3e26deba9a062bf4725442b38691ec749351905e6fbdd7db899cf18bc97cc75b409908203e133c8ea802976886cf12047750a96467d52485167b682d9252b44b9c1bcff08a9373f249b996978d0250e72347ad67e7016ea6eac982f16b1514086d526ce5687033e8c3fbb4af5d26c09eeaf04fca351b0e226a46d84467b9fcc7d648dd8da73b4647b867691524e5bbd198dfe03de84354128f710da3eb754ff0119a1b792dbc050a333dc374d27683a088e4975e32fd50447fca4309f1de2db4c046c021f93092eb016ae4284d13f5a7c64059a4eb7e10053b5cc5ce54424f3608107a020845d0acf24617c58d5ee90e36bcd692308203ea308202d2a003020102020104300d06092a864886f70d01010b0500304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3231303430313135303031355a170d3233303230313135303031355a304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100b8c1570e4e6aa6bbc77cc74b0488d2424ee9ab0a5be5d7963b90e8165fb8145e0e066202f67b5b34fa23be89bda3f3d521d2c60cce3ba03a289cc60766fc605f3a00193deeaed513c59bf790a580a7d7c2e2c5834d0f80c60441f46b650fc276edceb48bc8828a0cde3fab7422b525564ba6297a24e2e9b685600db3e498984dd1d347d59889a4e6ed3f2020f90efe52d053e13bc843e5c632d81ccc4fc176ed4d63e12a29bb00d7351a7969de74a22dedef3eb9e08b86709d9e394a929a6265a6de9c26350f9a6422d7a3743a8215b785df7c3aad04e241434b1db1a46740e5344b3da58529ac81d5a18f95269e3f2b837e31343e28334e213f33a1acbd552f0203010001a381d43081d1300e0603551d0f0101ff04040302010630410603551d1f043a30383036a034a0328630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f63726c2f726f6f742d63612e63726c304c06082b060105050701010440303e303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f726f6f742d63612e637274301d0603551d0e04160414e4dd8eebbb9498c75e99e0947fe37bc5e1cf2465300f0603551d130101ff040530030101ff300d06092a864886f70d01010b050003820101005ebe2b6d1046c9da4fd75a03dd4b32bba4ad46cc44cf9dfb3bdc42905f27d49bc61973e85ff2314b124e6cb2481ecc41d38919d5fd2e0b4b863a1daf01459f87e6223baef6e41e4e01bdf14bcf516743b4c3bebad9901d94fca89a8e828d1e9874bd9892406631107d4548d53b4def41daca35c5edb95588228981a2b1d6f761c5b9d35bc639deb63b4d6436ab60a22637ba36230cc8da8ba7bf8f24d919bad040e6025e6bda031008dbf1e414e631fb7bd921f311eff85fc4f0353a8fe1413c4fdd7779b68c33ed7329604e08e2dd5155f122f556e3522375fe8e66d15bb09bdd4a528d538f39f0329afcc737dadc568c638b2891e5fd8f93a4919e3e853c523182025c308202580201013052304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300b0609608648016503040201a081de301806092a864886f70d010903310b06092a864886f70d010701302f06092a864886f70d01090431220420eff26b2e4dfb288bde8e44e7b03557ddc57c88e22030598255a3e53a6c65b0fa308190060b2a864886f70d010910022f318180307e307c307a042013850c5270fcad4184bb3279338e1b6217951751c3ea673b385f8d8cd9ff87d330563051a44f304d3110300e06035504030c07676f6f642d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5502010a300d06092a864886f70d01010b050004820100401289320753c3f3e66c90bb1a883047a686170a9d97911ca6c74741c5f3eb7bf1642b45eefe4151dd01e800ea97b626624dce05485bf37184d596a4ca7112bafe84b3127950f712217310e0d048d728d1398ef0d93aa39758bf9f20fe6ba46c2abbf0344af82ec6cb76b725d43c809e4a272f60ac85d2d50d52d3e5b33226636fd1764772662d07990725c7c08132f824899e9c2b6e9223fc742bedfd536c250c211ab780e23130166727c71be56343614e4b3dc0e5ce546bda8435d71f4e38b95348d726905cb0abb775a957b5c0289aad45e83b47f828a6de38536a14cc1e955be447e6143c2b59b52edde1599053da73a7d5fa1e1e7ac91aa0e52f35a2c1";

        Exception exception = assertThrows(DSSException.class, () -> PAdESUtils.replaceSignature(documentToBeSigned,
                DSSASN1Utils.getDEREncoded(Utils.fromHex(hexEncodedCMSSignedData)), new InMemoryResourcesHandlerBuilder()));
        assertEquals("PDF document contains more than one empty signature!", exception.getMessage());
    }

}
