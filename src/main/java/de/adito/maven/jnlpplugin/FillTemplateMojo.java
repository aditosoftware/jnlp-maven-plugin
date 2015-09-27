package de.adito.maven.jnlpplugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

/**
 * @author PaL
 *         Date: 26.09.15
 *         Time: 02:20
 */
@Mojo(name = "fill-template", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class FillTemplateMojo extends AbstractJnlpMojo
{

  private static final String FORMAT_FORMAT = "\\$\\(format\\)";


  @Parameter(defaultValue = "${project.basedir}/src/main/jnlp/template.jnlp")
  private String templateFile;

  @Parameter(defaultValue = "webstarts.jnlp")
  private String fileName;

  @Parameter(defaultValue = "\\n    ")
  private String separator;

  /**
   * Variables are: '$(groupId)', '$(artifactId)', '$(version)', '$(type)', '$(classifier)' and '$(format)'.
   */
  @Parameter(defaultValue = "<jnlp url=\"$(format)\"/>")
  private String templateFormat;

  @Parameter
  private Properties customTemplateFormats;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    StringBuilder dependencyBuilder = new StringBuilder();
    Set<Artifact> artifacts = getArtifacts();
    for (Artifact artifact : artifacts)
    {
      String customFormat = customTemplateFormats == null ? null :
          customTemplateFormats.getProperty(artifact.getGroupId() + ":" + artifact.getArtifactId());
      String entry = getFormatArtifact(customFormat == null ? templateFormat : customFormat, artifact)
          .replaceAll(FORMAT_FORMAT, getArtifactFileName(artifact));

      if (dependencyBuilder.length() != 0)
        dependencyBuilder.append(_getSeparator());
      dependencyBuilder.append(entry);
    }
    String templateString = _readTemplateFile();
    templateString = templateString.replaceAll("\\$\\(dependencies\\)", dependencyBuilder.toString());
    _writeTemplateFile(templateString);
  }

  private String _readTemplateFile() throws MojoExecutionException
  {
    try
    {
      byte[] bytes = Files.readAllBytes(Paths.get(templateFile));
      String encoding = getProject().getProperties().getProperty("project.build.sourceEncoding");
      if (encoding == null || encoding.isEmpty())
        return new String(bytes);
      else
        return new String(bytes, encoding);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private void _writeTemplateFile(String pTemplateString) throws MojoExecutionException
  {
    try
    {
      Path path = Paths.get(getOutputDirectory());
      Files.createDirectories(path);
      path = path.resolve(fileName);

      String encoding = getProject().getProperties().getProperty("project.build.sourceEncoding");
      byte[] bytes;
      if (encoding == null || encoding.isEmpty())
        bytes = pTemplateString.getBytes();
      else
        bytes = pTemplateString.getBytes(encoding);
      Files.write(path, bytes);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private String _getSeparator()
  {
    return separator.replaceAll("\\\\n", "\n")
        .replaceAll("\\\\r", "\r")
        .replaceAll("\\\\t", "\t");
  }

}
