package io.spring.initializr.packaging

import io.spring.initializr.generator.ProjectRequest

public class EarProject extends JavaProject {
    static final String KEY_EAR = "ear"
    static final String KEY_WAR = "war"
    static final String KEY_APP_METAINF = "appMetaInf"

    EarProject(ProjectRequest request, Map model,File rootDir){
        super(request, model,rootDir)
    }

    /**
     * generates all directories for an Ear Project
     */
    void doGenerateProjectDirectories() {
        generateEarDirs()
        generateWarDirs()
    }

    /**
     * generate all ear specific directories
     */
    void generateEarDirs(){
        directories.put(KEY_EAR,new File(rootDir,request.name+"EAR"))
        directories.put(KEY_WAR,new File(rootDir,request.name))
        generateDirectories()
        generateFile(rootDir,"README.md","earREADME.md")
        generateFile(rootDir,"settings.gradle","settings.gradle")
        generateFile(directories.get(KEY_EAR),"build.gradle","starter-ear-build.gradle")
        generateFile(generateDirectory(directories.get(KEY_EAR),"src/main/application/META-INF"),"application.xml","application.xml")
    }

    /**
     * generate all war specific directories.
     * Calls for a new WarProject instance
     */
    void generateWarDirs(){
        new WarProject(request,model,directories.get(KEY_WAR)).doGenerateProjectDirectories()
    }



}