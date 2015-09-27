package de.adito.maven.jnlpplugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

/**
 * @author PaL
 *         Date: 25.09.15
 *         Time: 02:20
 */
@Mojo(name = "copy-dependencies", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class CopyDependenciesMojo extends AbstractJnlpMojo
{

  public void execute() throws MojoExecutionException
  {
    try
    {
      Set<Artifact> artifacts = getArtifacts();
      Path path = Files.createDirectories(Paths.get(getOutputDirectory()));

      for (Artifact artifact : artifacts)
        _writeArtifact(artifact, path);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException("create jnlp failed", e);
    }
  }

  private void _writeArtifact(Artifact pArtifact, Path pTargetPath) throws IOException
  {
    File artifactFile = pArtifact.getFile();
    String artifactFileName = getArtifactFileName(pArtifact);

    Path artifactTargetPath = pTargetPath.resolve(artifactFileName);
    Files.copy(artifactFile.toPath(), artifactTargetPath, StandardCopyOption.REPLACE_EXISTING);
  }

}
