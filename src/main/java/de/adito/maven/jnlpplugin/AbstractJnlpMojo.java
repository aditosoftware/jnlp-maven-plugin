package de.adito.maven.jnlpplugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
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

  @Parameter
  private List<String> exclusions;

  @Parameter(defaultValue = "${project.build.directory}/jnlp")
  private String outputDirectory;

  /**
   * Variables are: '$(groupId)', '$(artifactId)', '$(version)', '$(type)' and '$(classifier)'
   */
  @Parameter(defaultValue = "$(artifactId)-$(version).$(type)")
  private String format;


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
    return getFormatArtifact(getFormat(), pArtifact);
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
}
