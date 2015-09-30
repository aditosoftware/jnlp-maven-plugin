# jnlp-maven-plugin
[![Build Status](https://travis-ci.org/jboesl/jnlp-maven-plugin.svg?branch=master)](https://travis-ci.org/jboesl/jnlp-maven-plugin)


Overview
--------
This plugin lets you copy all dependencies to a directory by using the 'copy-dependencies' goal . In contrast to 
'copy-dependency-plugin' you can define the format for each file and you can define exclusions.

With the 'fill-template' goal all dependencies can be inserted in a file. As with the 'copy-dependencies' goal those
dependencies can be filtered and the output can be formatted.



Common usage
------------
```
<project>
  ...
  <plugins>
    <plugin>
      <groupId>de.adito.maven</groupId>
      <artifactId>jnlp-maven-plugin</artifactId>
      <version>1.0.0</version>
      <configuration>
        <format>$(artifactId)__V$(version).$(type)</format>
        <templateFormat>&lt;jar href="$(format)"/></templateFormat>
        <customTemplateFormats>
          <property>
            <name>org.apache.maven:maven-plugin-api</name>
            <value>&lt;jar href="$(format)" main="true"/></value>
          </property>
        </customTemplateFormats>
      </configuration>
    </plugin>
  </plugins>
  ...
</project>
```
