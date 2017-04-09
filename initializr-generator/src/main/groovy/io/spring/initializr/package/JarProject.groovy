package io.spring.initializr.packaging

import io.spring.initializr.generator.ProjectRequest

public class JarProject extends JavaProject {
    static final String KEY_RESOURCE_METAINF = "resourceMetaInf"

    JarProject(ProjectRequest request, Map model,File rootDir){
        super(request, model, rootDir)
    }

    /**
     * generates all directories for an Jar Project
     */
    void doGenerateProjectDirectories() {
        doGenerateJavaDirectories(rootDir)
        directories.put(KEY_RESOURCE_METAINF,new File(directories.get(KEY_RESOURCES),"META-INF"))
        generateDirectories()
        generateFile(rootDir,"README.md","jarREADME.md")
        generateFile(rootDir,"build.gradle","starter-jar-build.gradle")

    }


}