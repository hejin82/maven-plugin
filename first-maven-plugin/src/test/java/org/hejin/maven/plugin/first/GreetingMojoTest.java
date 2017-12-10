package org.hejin.maven.plugin.first;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class GreetingMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGreetingMojoGoal() throws Exception {
		File testPom = new File(getBasedir(), "src/test/resources/plugin-pom.xml");
		GreetingMojo mojo = (GreetingMojo) lookupMojo("sayhi", testPom);
		assertNotNull(mojo);
		mojo.execute();
	}
}
