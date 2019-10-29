package com.amsdams.demoreviewservice;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class ReviewTest {
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Review.class).withRedefinedSuperclass()
				.suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS, Warning.INHERITED_DIRECTLY_FROM_OBJECT)
				.verify();
	}
}
