package com.jaiva;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.PublicLibrary;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@Mojo(
        name = "json",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
public class GenerateLibJsonMojo extends AbstractMojo {

    /**
     * The base package to scan for BaseLibrary subclasses. Such as
     * `com.app.jaiva`. It will only scan for BaseLibrary instances which
     * have the PublicLibrary annotation.
     */
    @Parameter(
            required = true,
            property = "jaiva.basePackage"
    )
    private String basePackage;

    /**
     * The folder to write the generated JSON files. If your project is hosted on github or some other
     * cdn-like place, it's best this be the project's base directory/jaiva (The default value) such
     * as to allow easy configuration for the VSCode extension.
     */
    @Parameter(
            defaultValue = "${project.basedir}/jaiva/",
            property = "jaiva.outputDir",
            required = true
    )
    private File outputDir;

    /**
     * Whether this build should fail if the output directory
     * already contains files. Defaults to OVERWRITE which will
     * overwrite the files that it needs to
     */
    @Parameter(
            defaultValue = "OVERWRITE",
            property = "jaiva.ifOutDirNotEmpty"
    )
    private OutDirNotEmpty ifOutDirNotEmpty;

    /**
     * Whether to skip this mojo.
     */
    @Parameter(
            defaultValue = "false",
            property = "jaiva.json.skip"
    )
    private boolean skip;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) return;
        prepareOutputDirectory();
        load(basePackage, outputDir);
    }

    private void load(String basePackage, File outputFolder) throws MojoExecutionException {

        List<String> classpathElements = getClasspathElements();

        try (URLClassLoader projectClassLoader =
                     createProjectClassLoader(classpathElements)) {

            try (ScanResult scanResult = new ClassGraph()
                    .overrideClasspath(classpathElements)
                    .acceptPackages(basePackage)
                    .enableClassInfo()
                    .enableAnnotationInfo()
                    .scan()) {

                ClassInfoList matchingClasses = scanResult.getClassesWithAnnotation(PublicLibrary.class.getName())
                        .filter(classInfo -> classInfo.extendsSuperclass(BaseLibrary.class.getName()));


                Class<?> pluginClass = Class.forName(Plugin.class.getName(), true, projectClassLoader);

                Method method = pluginClass.getMethod("generateToolingJSON", ArrayList.class);

                Properties props = new Properties();

                ArrayList<String> classNames = new ArrayList<>();
                ArrayList<String> paths = new ArrayList<>();

                for (ClassInfo classInfo : matchingClasses) {

                    String className = classInfo.getName();

                    AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(PublicLibrary.class.getName());

                    if (annotationInfo == null)
                        continue;

                    String path = (String) annotationInfo.getParameterValues().getValue("path");

                    getLog().info("Found PublicLibrary: " + className + " [path = " + path + "]");

                    paths.add(path);

                    classNames.add(className);
                }

                ArrayList<String> out = invokeToolingJSON(method, classNames);

                for (int i = 0; i < out.size(); i++) {
                    String json = out.get(i);
                    String path = paths.get(i);
                    String className = classNames.get(i);

                    String fileName = sanitizeToFileName(path) + ".json";

                    Path jsonFile = outputFolder.toPath().resolve(fileName);

                    try {
                        Files.writeString(jsonFile, json, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new MojoExecutionException("Failed to write tooling JSON for " + className, e);
                    }

                    props.setProperty(path, fileName);

                    getLog().info("Generated: " + jsonFile);
                }

                writeFetchProperties(props);
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to close project classloader", e);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Could not load project-side Jaiva Plugin", e);
        } catch (NoSuchMethodException e) {
            throw new MojoExecutionException("Project-side Jaiva Plugin doesnt have generateToolingJSON(ArrayList)", e);
        }
    }

    private ArrayList<String> invokeToolingJSON(
            Method method,
            ArrayList<String> className
    ) throws MojoExecutionException {

        try {
            return (ArrayList) method.invoke(null, className);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Could not access project-side Jaiva Plugin", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new MojoExecutionException("Project-side Jaiva tooling generation failed for " + className, cause);
        }
    }

    private URLClassLoader createProjectClassLoader(
            List<String> classpathElements
    ) throws MojoExecutionException {

        try {
            URL[] urls = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); i++) {
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }

            return new URLClassLoader(urls, ClassLoader.getPlatformClassLoader());

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to create project classloader", e);
        }
    }

    private void prepareOutputDirectory()
            throws MojoExecutionException {

        if (!outputDir.exists()) {

            boolean created = outputDir.mkdirs();

            if (!created)
                throw new MojoExecutionException("Failed to create output directory: " + outputDir.getAbsolutePath());

            getLog().info(
                    "Created output directory: "
                            + outputDir.getAbsolutePath()
            );

        } else if (!outputDir.isDirectory())
            throw new MojoExecutionException("Output path exists but is not a directory: " + outputDir.getAbsolutePath());


        if (isDirectoryNotEmpty(outputDir.toPath()))
            if ((ifOutDirNotEmpty == OutDirNotEmpty.ERROR)) {
                throw new MojoExecutionException(
                        "Output directory is not empty: " + outputDir.getAbsolutePath()
                                + ". Clean the directory or run with -Djaiva.ifOutDirNotEmpty=OVERWRITE"
                );
            } else {
                getLog().info("Output directory exists: " + outputDir.getAbsolutePath() + ". Relevant files will be overwritten.");
            }

    }

    private boolean isDirectoryNotEmpty(Path path) throws MojoExecutionException {
        try (Stream<Path> entries = Files.list(path)) {
            return entries.findFirst().isPresent();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to inspect output directory: " + path, e);
        }
    }

    private void writeFetchProperties(Properties props) throws MojoExecutionException {

        props.setProperty("version", Main.version);

        Path filePath = outputDir.toPath().resolve("fetch.properties");

        File file = filePath.toFile();

        try {
            file.createNewFile();

        } catch (IOException e) {
            throw new MojoExecutionException("Could not create fetch.properties", e);
        }

        try (OutputStream out = new FileOutputStream(file)) {

            props.store(out, "Jaiva Fetch Configg");

        } catch (IOException e) {
            throw new MojoExecutionException("An IO Problem occurred", e);
        }
    }

    private String sanitizeToFileName(String input) {

        if (input == null || input.isBlank())
            return "unnamed";


        return input
                .replaceAll("[\\\\/]", "_")
                .replaceAll("[<>:\"|?*]", "")
                .trim()
                .replaceAll("^[.]+", "");
    }

    private List<String> getClasspathElements() throws MojoExecutionException {

        try {
            return project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to resolve compile classpath", e);
        }
    }
}