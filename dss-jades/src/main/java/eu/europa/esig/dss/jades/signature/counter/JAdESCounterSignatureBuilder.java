package eu.europa.esig.dss.jades.signature.counter;

import java.util.List;

import org.jose4j.json.JsonUtil;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.lang.JoseException;

import eu.europa.esig.dss.jades.JAdESHeaderParameterNames;
import eu.europa.esig.dss.jades.JWSJsonSerializationGenerator;
import eu.europa.esig.dss.jades.JWSJsonSerializationObject;
import eu.europa.esig.dss.jades.JWSJsonSerializationParser;
import eu.europa.esig.dss.jades.JsonObject;
import eu.europa.esig.dss.jades.signature.JAdESExtensionBuilder;
import eu.europa.esig.dss.jades.validation.JAdESSignature;
import eu.europa.esig.dss.jades.validation.JWS;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.AdvancedSignature;

public class JAdESCounterSignatureBuilder extends JAdESExtensionBuilder {
	
	/**
	 * Extract SignatureValue binaries from the provided JAdES signature
	 * 
	 * @param signatureDocument {@link DSSDocument} to be counter-signed
	 * @param parameters {@link JAdESCounterSignatureParameters}
	 * @return {@link DSSDocument} extracted SignatureValue
	 */
	public DSSDocument getSignatureValueToBeSigned(DSSDocument signatureDocument, JAdESCounterSignatureParameters parameters) {
		JWSJsonSerializationParser jwsJsonSerializationParser = new JWSJsonSerializationParser(signatureDocument);
		JWSJsonSerializationObject jwsJsonSerializationObject = jwsJsonSerializationParser.parse();
		
		JAdESSignature jadesSignature = extractSignatureById(jwsJsonSerializationObject, parameters.getSigningSignatureId());
		return new InMemoryDocument(jadesSignature.getSignatureValue());
	}
	
	/**
	 * Embeds and returns the embedded counter signature into the original JAdES signature
	 * 
	 * @param signatureDocument {@link DSSDocument} the original document containing the signature to be counter signed
	 * @param counterSignature {@link DSSDocument} the counter signature
	 * @param parameters {@link JAdESCounterSignatureParameters}
	 * @return {@link DSSDocument} original signature enveloping the {@code counterSignature} in an unprotected header
	 */
	public DSSDocument buildEmbeddedCounterSignature(DSSDocument signatureDocument, DSSDocument counterSignature, 
			JAdESCounterSignatureParameters parameters) {
		JWSJsonSerializationParser jwsJsonSerializationParser = new JWSJsonSerializationParser(signatureDocument);
		JWSJsonSerializationObject jwsJsonSerializationObject = jwsJsonSerializationParser.parse();
		
		JAdESSignature jadesSignature = extractSignatureById(jwsJsonSerializationObject, parameters.getSigningSignatureId());
		
		List<Object> unsignedProperties = getUnsignedProperties(jadesSignature);
		
		addCSig(unsignedProperties, counterSignature, parameters);
		
		JWSJsonSerializationGenerator generator = new JWSJsonSerializationGenerator(jwsJsonSerializationObject, 
				jwsJsonSerializationObject.getJWSSerializationType());
		return new InMemoryDocument(generator.generate());
	}
	
	@SuppressWarnings("unchecked")
	private void addCSig(List<Object> unsignedProperties, DSSDocument counterSignature, JAdESCounterSignatureParameters parameters) {
		JSONObject cSigItem = new JSONObject();
		
		String signatureString = new String(DSSUtils.toByteArray(counterSignature));
		
		Object cSig;
		switch (parameters.getJwsSerializationType()) {
			case COMPACT_SERIALIZATION:
				cSig = signatureString;
				break;
			case FLATTENED_JSON_SERIALIZATION:
				try {
					cSig = new JsonObject(JsonUtil.parseJson(signatureString));
				} catch (JoseException e) {
					throw new DSSException(String.format("An error occurred during a Counter Signature creation. Reason : %s", e.getMessage()), e);
				}
				break;
			default:
				throw new DSSException(String.format("The JWSSerializarionType '%s' is not supported for a Counter Signature!", 
						parameters.getJwsSerializationType()));
		}
		cSigItem.put(JAdESHeaderParameterNames.C_SIG, cSig);
		unsignedProperties.add(cSigItem);
	}
	
	private JAdESSignature extractSignatureById(JWSJsonSerializationObject jwsJsonSerializationObject, String signatureId) {
		if (!jwsJsonSerializationObject.isValid()) {
			throw new DSSException(String.format("Counter signature is not supported for invalid RFC 7515 files "
					+ "(shall be a Serializable JAdES signature). Reason(s) : %s", jwsJsonSerializationObject.getErrorMessages()));
		}
		List<JWS> jwsSignatures = jwsJsonSerializationObject.getSignatures();
		if (Utils.isCollectionEmpty(jwsSignatures)) {
			throw new DSSException("The provided signatureDocument does not contain JAdES Signatures!");
		}
		for (JWS jws : jwsSignatures) {
			JAdESSignature jadesSignature = new JAdESSignature(jws);
			JAdESSignature signatureById = getSignatureOrItsCounterSignature(jadesSignature, signatureId);
			if (signatureById != null) {
				return signatureById;
			}
		}
		throw new DSSException(String.format("The requested JAdES Signature with id '%s' has not been found in the provided file!", signatureId));
	}
	
	private JAdESSignature getSignatureOrItsCounterSignature(AdvancedSignature signature, String signatureId) {
		if (signatureId == null || signatureId.equals(signature.getId())) {
			return (JAdESSignature) signature;
		}
		List<AdvancedSignature> counterSignatures = signature.getCounterSignatures();
		if (Utils.isCollectionNotEmpty(counterSignatures)) {
			for (AdvancedSignature counterSignature : counterSignatures) {
				JAdESSignature signatureById = getSignatureOrItsCounterSignature(counterSignature, signatureId);
				if (signatureById != null) {
					return signatureById;
				}
			}
		}
		return null;
	}

}
