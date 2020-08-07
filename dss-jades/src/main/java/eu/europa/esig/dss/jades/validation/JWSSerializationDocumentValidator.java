package eu.europa.esig.dss.jades.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.jades.JWSJsonSerializationObject;
import eu.europa.esig.dss.jades.JWSJsonSerializationParser;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.AdvancedSignature;

//@formatter:off
/**
 * {
 * 
 * "payload":"payload contents",
 * 
 * "signatures":[
 * 
 * {"protected":"integrity-protected header 1 contents",
 * "header":non-integrity-protected header 1 contents, 
 * "signature":"signature 1 contents"},
 * 
 * ...
 * 
 * {"protected":"integrity-protected header N contents",
 * "header":non-integrity-protected header N contents, 
 * "signature":"signature N contents"}
 * 
 * ]
 * 
 * }
 */
//@formatter:on
public class JWSSerializationDocumentValidator extends AbstractJWSDocumentValidator {

	private static final Logger LOG = LoggerFactory.getLogger(JWSSerializationDocumentValidator.class);
	
	private List<AdvancedSignature> signatures;

	public JWSSerializationDocumentValidator() {
	}

	public JWSSerializationDocumentValidator(DSSDocument document) {
		super(document);
	}

	@Override
	public boolean isSupported(DSSDocument document) {
		JWSJsonSerializationParser jwsJsonSerializationParser = new JWSJsonSerializationParser(document);
		return jwsJsonSerializationParser.isSupported();
	}

	@Override
	public List<AdvancedSignature> getSignatures() {
		if (signatures == null) {
			signatures = new ArrayList<>();
			
			JWSJsonSerializationParser jwsJsonSerializationParser = new JWSJsonSerializationParser(document);
			JWSJsonSerializationObject jwsJsonSerializationObject = jwsJsonSerializationParser.parse();
			if (!jwsJsonSerializationObject.isValid()) {
				LOG.warn("The file parsing finished with the following errors : {}", jwsJsonSerializationObject.getErrorMessages());
			}
			
			List<JWS> foundSignatures = jwsJsonSerializationObject.getSignatures();
			LOG.info("{} signature(s) found", Utils.collectionSize(foundSignatures));
			for (JWS jws : foundSignatures) {
				JAdESSignature jadesSignature = new JAdESSignature(jws);
				jadesSignature.setSigningCertificateSource(signingCertificateSource);
				jadesSignature.setDetachedContents(detachedContents);
				jadesSignature.prepareOfflineCertificateVerifier(certificateVerifier);
				signatures.add(jadesSignature);
			}
		}
		return signatures;
	}

}
