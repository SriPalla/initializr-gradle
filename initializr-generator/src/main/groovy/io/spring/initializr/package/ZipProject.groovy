package io.spring.initializr.packaging
import io.spring.initializr.generator.ProjectRequest
public class ZipProject extends JavaProject{
    static final String KEY_JAR = "jar"
    static final String KEY_DIST = "dist"
    static final String KEY_RESOURCE_METAINF = "resourceMetaInf"

    ZipProject(ProjectRequest request, Map model, File rootDir) {
        super(request,model,rootDir)
    }

    /**
     * generate all directories for a Zip Project
     */
    void doGenerateProjectDirectories() {
        generateFile(rootDir, 'README.md','zipREADME.md')
        generateFile(rootDir, 'settings.gradle','settings-zip.gradle')
        directories.put(KEY_JAR,new File(rootDir,request.name))
        directories.put(KEY_DIST,new File(rootDir,request.name+"Dist"))
        generateDirectories()

        generateFile(directories.get(KEY_JAR),'settings.gradle','settings-zip-proj.gradle')
        generateFile(directories.get(KEY_JAR),'build.gradle','zip-java-proj.gradle')

        doGenerateJavaDirectories(directories.get(KEY_JAR))
        directories.put(KEY_RESOURCE_METAINF,new File(directories.get(KEY_JAR),"src/main/resources/META-INF"))
        generateDirectories()
        generateFile(directories.get(KEY_DIST),'build.gradle','zip-proj-dist.gradle')
        generateFile(directories.get(KEY_DIST),'settings.gradle','settings-zip-proj-dist.gradle')
        generateDirectory(directories.get(KEY_DIST),"src/main/dist")


    }
}
