/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation.timestamp;

import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.enumerations.TimestampedObjectType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.DigestDocument;
import eu.europa.esig.dss.spi.DSSASN1Utils;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.ValidationLevel;
import eu.europa.esig.dss.validation.scope.DigestSignatureScope;
import eu.europa.esig.dss.validation.scope.FullSignatureScope;
import eu.europa.esig.dss.validation.scope.SignatureScope;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Detached CMS TimestampToken Validator
 *
 */
public class DetachedTimestampValidator extends SignedDocumentValidator implements TimestampValidator {

	/** The type of the timestamp */
	protected TimestampType timestampType;

	/** The TimestampToken to be validated */
	protected TimestampToken timestampToken;

	/**
	 * Empty constructor
	 */
	DetachedTimestampValidator() {
	}

	/**
	 * The default constructor
	 *
	 * @param timestampFile {@link DSSDocument} timestamp document to validate
	 */
	public DetachedTimestampValidator(final DSSDocument timestampFile) {
		this(timestampFile, TimestampType.CONTENT_TIMESTAMP);
	}

	/**
	 * The default constructor with a type
	 *
	 * @param timestampFile {@link DSSDocument} timestamp document to validate
	 * @param timestampType {@link TimestampType}
	 */
	public DetachedTimestampValidator(final DSSDocument timestampFile, TimestampType timestampType) {
		this.document = timestampFile;
		this.timestampType = timestampType;
	}

	@Override
	public boolean isSupported(DSSDocument dssDocument) {
		byte firstByte = DSSUtils.readFirstByte(dssDocument);
		if (DSSASN1Utils.isASN1SequenceTag(firstByte)) {
			return DSSUtils.isTimestampToken(dssDocument);
		}
		return false;
	}

	@Override
	protected void assertConfigurationValid() {
		Objects.requireNonNull(certificateVerifier, "CertificateVerifier is not defined");
	}

	@Override
	public List<TimestampToken> getDetachedTimestamps() {
		return Collections.singletonList(getTimestamp());
	}

	@Override
	public TimestampToken getTimestamp() {
		if (timestampToken == null) {
			timestampToken = createTimestampToken();
		}
		return timestampToken;
	}

	private TimestampToken createTimestampToken() {
		Objects.requireNonNull(certificateVerifier, "CertificateVerifier is not defined");
		Objects.requireNonNull(document, "The timestampFile must be defined!");
		Objects.requireNonNull(timestampType, "The TimestampType must be defined!");
		try {
			timestampToken = new TimestampToken(DSSUtils.toByteArray(document), timestampType);
			timestampToken.setFileName(document.getName());
			timestampToken.matchData(getTimestampedData());
			timestampToken.setTimestampScopes(getTimestampSignatureScopes());
			timestampToken.getTimestampedReferences().addAll(getTimestampedReferences());
			return timestampToken;

		} catch (CMSException | TSPException | IOException e) {
			throw new DSSException(String.format("Unable to create a TimestampToken. Reason : %s", e.getMessage()), e);

		}
	}

	@Override
	public void setValidationLevel(ValidationLevel validationLevel) {
		if (ValidationLevel.BASIC_SIGNATURES == validationLevel) {
			throw new IllegalArgumentException("Minimal level is " + ValidationLevel.TIMESTAMPS);
		}
		super.setValidationLevel(validationLevel);
	}

	/**
	 * Sets the data that has been timestamped
	 *
	 * @param document {@link DSSDocument} timestamped data
	 */
	public void setTimestampedData(DSSDocument document) {
		Objects.requireNonNull(document, "The document is null");
		setDetachedContents(Arrays.asList(document));
	}

	@Override
	public DSSDocument getTimestampedData() {
		int size = Utils.collectionSize(detachedContents);
		if (size == 0) {
			return null;
		} else if (size > 1) {
			throw new IllegalArgumentException("Only one detached document shall be provided for a timestamp validation!");
		}
		return detachedContents.iterator().next();
	}

	/**
	 * Returns a list of timestamp signature scopes (timestamped data)
	 * 
	 * @return a list of {@link SignatureScope}s
	 */
	protected List<SignatureScope> getTimestampSignatureScopes() {
		DSSDocument timestampedData = getTimestampedData();
		if (timestampedData != null) {
			return getTimestampSignatureScopeForDocument(timestampedData);
		}
		return Collections.emptyList();
	}

	/**
	 * Returns a timestamped {@code SignatureScope} for the given document
	 *
	 * @param document {@link DSSDocument}
	 * @return a list of {@link SignatureScope}s
	 */
	protected List<SignatureScope> getTimestampSignatureScopeForDocument(DSSDocument document) {
		String documentName = document.getName();
		if (document instanceof DigestDocument) {
			return Arrays.asList(new DigestSignatureScope(Utils.isStringNotEmpty(documentName) ? documentName : "Digest document",
					((DigestDocument) document).getExistingDigest()));
		} else {
			return Arrays.asList(new FullSignatureScope(Utils.isStringNotEmpty(documentName) ? documentName : "Full document",
					getDigest(document)));
		}
	}

	/**
	 * Returns a list of timestamped references
	 *
	 * @return a list of {@link TimestampedReference}s
	 */
	protected List<TimestampedReference> getTimestampedReferences() {
		List<TimestampedReference> timestampedReferences = new ArrayList<>();
		List<SignatureScope> signatureScopes = getTimestampSignatureScopes();
		for (SignatureScope signatureScope : signatureScopes) {
			if (addReference(signatureScope)) {
				timestampedReferences.add(new TimestampedReference(
						signatureScope.getDSSIdAsString(), TimestampedObjectType.SIGNED_DATA));
			}
		}
		return timestampedReferences;
	}

	/**
	 * Checks if the signature scope shall be added as a timestamped reference
	 *
	 * NOTE: used to avoid duplicates in ASiC with CAdES validator, due to covered signature/timestamp files
	 *
	 * @param signatureScope {@link SignatureScope} to check
	 * @return TRUE if the timestamped reference shall be created for the given {@link SignatureScope}, FALSE otherwise
	 */
	protected boolean addReference(SignatureScope signatureScope) {
		// accept all for a simple detached timestamp
		return true;
	}

	@Override
	public List<DSSDocument> getOriginalDocuments(String signatureId) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public List<DSSDocument> getOriginalDocuments(AdvancedSignature advancedSignature) {
		// TODO
		throw new UnsupportedOperationException();
	}

}
