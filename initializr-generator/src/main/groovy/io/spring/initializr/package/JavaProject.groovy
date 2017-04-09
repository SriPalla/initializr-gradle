package io.spring.initializr.packaging
import io.spring.initializr.generator.*
import java.util.Map
import io.spring.initializr.util.GroovyTemplate

public class JavaProject extends Project {
    static final String KEY_JAVA = "java"
    static final String KEY_RESOURCES = "resources"
    static final String KEY_TESTJAVA = "testJava"
    static final String KEY_TESTRESOURCES = "testResources"

    JavaProject(ProjectRequest request, Map model, File rootDir){
        super(request, model, rootDir)
    }

    void doGenerateProjectDirectories(){
        doGenerateJavaDirectories(rootDir)

    }
    /**
     * Generates Java specific directories. Such as src/main/java/userInputtedPackagingStructure/UserApplication.java and src/main/resources AND src/test/java/etc
     * @param base directory user wants java files
     * @return java specific directories of type File java.io
     */
    void doGenerateJavaDirectories(File dir){
        directories.put(KEY_JAVA,new File(dir,"src/main/java/"+request.packageName.replace('.', '/')))
        directories.put(KEY_RESOURCES,new File(dir,"src/main/resources"))
        directories.put(KEY_TESTJAVA,new File(dir,"src/test/java/"+request.packageName.replace('.', '/')))
        directories.put(KEY_TESTRESOURCES,new File(dir,"src/test/resources"))
        generateDirectories()

        generateFile(directories.get(KEY_JAVA),"${request.applicationName}.java","Application.java")
        generateFile(directories.get(KEY_TESTJAVA),"${request.applicationName}Tests.java","ApplicationTests.java")

    }

}