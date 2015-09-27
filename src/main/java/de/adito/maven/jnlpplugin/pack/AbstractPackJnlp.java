package de.adito.maven.jnlpplugin.pack;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

/**
 * @author bo
 *         Date: 27.09.15
 *         Time: 02:15
 */
public abstract class AbstractPackJnlp extends AbstractMojo
{

  @Parameter(defaultValue = "${project.build.directory}/jnlp")
  private String outputDirectory;


  String getOutputDirectory()
  {
    return outputDirectory;
  }


  void pack(boolean pRemoveSource) throws IOException
  {
    Pack200.Packer packer = getPacker();
    Path outputPath = Paths.get(getOutputDirectory());
    if (!Files.isDirectory(outputPath))
      return;
    try (DirectoryStream<Path> children = Files.newDirectoryStream(outputPath, "*.jar"))
    {
      for (Path child : children)
      {
        Path artifactTargetPath = child.getParent().resolve(child.getFileName() + ".pack.gz");
        try (JarInputStream inputStream = new JarInputStream(Files.newInputStream(child));
             OutputStream outputStream = Files.newOutputStream(artifactTargetPath))
        {
          packer.pack(inputStream, outputStream);
        }

        if (pRemoveSource)
          Files.delete(child);
      }
    }
  }

  void unpack(boolean pRemoveSource) throws IOException
  {
    Pack200.Unpacker unpacker = Pack200.newUnpacker();

    Path outputPath = Paths.get(getOutputDirectory());
    if (!Files.isDirectory(outputPath))
      return;
    try (DirectoryStream<Path> children = Files.newDirectoryStream(outputPath, "*.jar.pack.gz"))
    {
      for (Path child : children)
      {
        String fileName = child.getFileName().toString();
        fileName = fileName.substring(0, fileName.length() - ".pack.gz".length());
        Path artifactTargetPath = child.getParent().resolve(fileName);
        try (InputStream inputStream = Files.newInputStream(child);
             JarOutputStream outputStream = new JarOutputStream(Files.newOutputStream(artifactTargetPath)))
        {
          unpacker.unpack(inputStream, outputStream);
        }

        if (pRemoveSource)
          Files.delete(child);
      }
    }
  }

  Pack200.Packer getPacker()
  {
    Pack200.Packer packer = Pack200.newPacker();

    // Initialize the state by setting the desired properties
    Map<String, String> p = packer.properties();
    // take more time choosing codings for better compression
    p.put(Pack200.Packer.EFFORT, "7");  // default is "5"
    // use largest-possible archive segments (>10% better compression).
    p.put(Pack200.Packer.SEGMENT_LIMIT, "-1");
    // reorder files for better compression.
    p.put(Pack200.Packer.KEEP_FILE_ORDER, Pack200.Packer.FALSE);
    // smear modification times to a single value.
    p.put(Pack200.Packer.MODIFICATION_TIME, Pack200.Packer.LATEST);
    // ignore all JAR deflation requests,
    // transmitting a single request to use "store" mode.
    p.put(Pack200.Packer.DEFLATE_HINT, Pack200.Packer.FALSE);
    // throw an error if an attribute is unrecognized
    p.put(Pack200.Packer.UNKNOWN_ATTRIBUTE, Pack200.Packer.ERROR);

    return packer;
  }

}
