package de.adito.maven.jnlpplugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.*;

/**
 * Holds common properties shared between CopyDependenciesMojo and FillTemplateMojo.
 *
 * @author bo
 *         Date: 27.09.15
 *         Time: 00:01
 */
abstract class AbstractJnlpMojo extends AbstractMojo
{

  private static final String FORMAT_GROUP_ID = "\\$\\(groupId\\)";
  private static final String FORMAT_ARTIFACT_ID = "\\$\\(artifactId\\)";
  private static final String FORMAT_VERSION = "\\$\\(version\\)";
  private static final String FORMAT_TYPE = "\\$\\(type\\)";
  private static final String FORMAT_CLASSIFIER = "\\$\\(classifier\\)";


  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  /**
   * A list with 'groupId:artifactId' to specify which dependencies should be excluded.
   */
  @Parameter
  private List<String> exclusions;

  /**
   * The directory in which new files are crated in.
   */
  @Parameter(defaultValue = "${project.build.directory}/jnlp")
  private String outputDirectory;

  /**
   * Describes the format that is used to write out dependencies.<br/>
   * Variables are: '$(groupId)', '$(artifactId)', '$(version)', '$(type)' and '$(classifier)'.<br/>
   * Remember you have to quote some characters in xml to be able to use it. E.g. to use a <b><</b> (lesser than) sign
   * you have to use <b>&amp;lt;</b> in xml.
   */
  @Parameter(defaultValue = "$(artifactId)-$(version).$(type)")
  private String format;

  /**
   * Special treatment for some 'dependencies'. You can describe the affected artifact with 'groupId:artifactId' and set
   * a custom format for that artifact.<br/>
   * For example:<br/>
   * <pre>
   * &lt;customFormats>
   *   &lt;property>
   *     &lt;name>org.apache.maven:maven-plugin-api</name>
   *     &lt;value>$(artifactId)-$(version)-special.$(type)&lt;/value>
   *   &lt;/property>
   * &lt;/customFormats>
   * </pre>
   */
  @Parameter
  private Properties customFormats;


  Set<Artifact> getArtifacts()
  {
    Set<Artifact> artifacts = new HashSet<>();
    for (Artifact artifact : project.getArtifacts())
    {
      String id = artifact.getGroupId() + ":" + artifact.getArtifactId();
      if (exclusions == null || !exclusions.contains(id))
        artifacts.add(artifact);
    }
    return artifacts;
  }


  String getArtifactFileName(Artifact pArtifact)
  {
    String customFormat = getCustomFormats() == null ? null :
        getCustomFormats().getProperty(pArtifact.getGroupId() + ":" + pArtifact.getArtifactId());

    return getFormatArtifact(customFormat == null ? getFormat() : customFormat, pArtifact);
  }

  String getFormatArtifact(String pFormat, Artifact pArtifact)
  {
    return pFormat
        .replaceAll(FORMAT_GROUP_ID, pArtifact.getGroupId())
        .replaceAll(FORMAT_ARTIFACT_ID, pArtifact.getArtifactId())
        .replaceAll(FORMAT_VERSION, pArtifact.getVersion())
        .replaceAll(FORMAT_TYPE, pArtifact.getType())
        .replaceAll(FORMAT_CLASSIFIER, pArtifact.getClassifier());
  }

  MavenProject getProject()
  {
    return project;
  }

  List<String> getExclusions()
  {
    return exclusions;
  }

  String getOutputDirectory()
  {
    return outputDirectory;
  }

  String getFormat()
  {
    return format;
  }

  public Properties getCustomFormats()
  {
    return customFormats;
  }
}
