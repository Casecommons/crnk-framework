package io.crnk.data.jpa.meta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import io.crnk.data.jpa.model.TestMappedSuperclassWithGenerics;
import io.crnk.meta.MetaLookupImpl;
import io.crnk.meta.model.MetaElement;

import java.util.Collections;

public class MetaMappedSuperWithGenericsTypeTest {

	private JpaMetaProvider metaProvider;

	@BeforeEach
	public void setup() {
		metaProvider = new JpaMetaProvider(Collections.emptySet());
		MetaLookupImpl lookup = new MetaLookupImpl();
		lookup.addProvider(metaProvider);
	}

	@Test
	public void testMetaMappedSuperclassDiscovery() {
		MetaElement meta = metaProvider.discoverMeta(TestMappedSuperclassWithGenerics.class);
		Assertions.assertTrue(meta instanceof MetaMappedSuperclass);
	}
}
