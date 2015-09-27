package de.adito.maven.jnlpplugin.pack;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

/**
 * @author bo
 *         Date: 27.09.15
 *         Time: 02:23
 */
@Mojo(name = "repack", requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class RepackDependenciesMojo extends AbstractPackJnlp
{

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    try
    {
      pack(false);
      unpack(true);
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

}
