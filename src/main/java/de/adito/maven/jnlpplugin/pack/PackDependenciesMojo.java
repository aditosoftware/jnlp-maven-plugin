package de.adito.maven.jnlpplugin.pack;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

/**
 * @author bo
 *         Date: 27.09.15
 *         Time: 01:27
 */
@Mojo(name = "pack", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class PackDependenciesMojo extends AbstractPackJnlp
{

  @Parameter(defaultValue = "true")
  private boolean removeJars;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    try
    {
      pack(removeJars);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

}
