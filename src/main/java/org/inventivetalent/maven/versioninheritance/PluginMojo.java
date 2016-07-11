package org.inventivetalent.maven.versioninheritance;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;

@Mojo(name = "inheritVersion",
	  defaultPhase = LifecyclePhase.INITIALIZE)
public class PluginMojo extends AbstractMojo {

	@Parameter(name = "parentPom",
			   defaultValue = "${project.basedir}/pom.xml")
	File parentPom;

	@Parameter(name = "modulePoms",
			   required = true)
	List<File> modulePoms;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().debug("Parent: " + parentPom);
		getLog().debug("Modules: (" + modulePoms.size() + ")");
		for (File file : modulePoms) {
			getLog().debug("  Module: " + file);
		}

		try {
			Xpp3Dom parentDom = Xpp3DomBuilder.build(new FileInputStream(parentPom), "UTF-8");
			if (parentDom.getChild("version") != null) {
				// Read parent version
				String parentVersion = parentDom.getChild("version").getValue();
				getLog().debug("Parent Version: " + parentVersion);

				for (File file : modulePoms) {
					// Create pom.xml backup
					File backup = new File(file.getParentFile(), "inheritance-backup-pom.xml");
					if (backup.exists()) {
						// Delete old backup file
						File oldBackup = new File(file.getParentFile(), "inheritance-backup-pom.xml.old");
						backup.renameTo(oldBackup);
						backup.delete();
						oldBackup.deleteOnExit();
					}
					Files.copy(file.toPath(), backup.toPath());

					Xpp3Dom modulePom = Xpp3DomBuilder.build(new FileInputStream(file), "UTF-8");

					Xpp3Dom moduleVersionDom = modulePom.getChild("version");
					if (moduleVersionDom != null) {// Change module <version> if it exists
						moduleVersionDom.setValue(parentVersion);
					}

					Xpp3Dom moduleParentDom = modulePom.getChild("parent");
					if (moduleParentDom == null) {
						getLog().warn("Missing <parent> dom for module " + file);
					} else {// Update <parent><version>
						Xpp3Dom moduleParentVersionDom = moduleParentDom.getChild("version");
						moduleParentVersionDom.setValue(parentVersion);
					}

					try (Writer writer = new FileWriter(file)) {
						Xpp3DomWriter.write(writer, modulePom);// Write the modified module pom.xml
					}
				}
			}
		} catch (Exception e) {
			getLog().error("Parent pom.xml parsing failed", e);
		}
	}
}
