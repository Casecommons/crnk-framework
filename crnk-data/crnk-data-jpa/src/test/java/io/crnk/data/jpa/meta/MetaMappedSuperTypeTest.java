package io.crnk.data.jpa.meta;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import io.crnk.data.jpa.model.TestMappedSuperclass;
import io.crnk.meta.MetaLookupImpl;
import io.crnk.meta.model.MetaElement;

import java.util.Collections;

public class MetaMappedSuperTypeTest {

	private JpaMetaProvider metaProvider;

	@BeforeEach
	public void setup() {
		metaProvider = new JpaMetaProvider(Collections.emptySet());
		MetaLookupImpl lookup = new MetaLookupImpl();
		lookup.addProvider(metaProvider);
	}

	@Test
	public void testMetaMappedSuperclassDiscovery() {
		MetaElement meta = metaProvider.discoverMeta(TestMappedSuperclass.class);
		Assertions.assertTrue(meta instanceof MetaMappedSuperclass);
	}
}
