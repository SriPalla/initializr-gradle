package io.spring.initializr.packaging

import io.spring.initializr.generator.*
import io.spring.initializr.util.GroovyTemplate
import io.spring.initializr.metadata.InitializrMetadataProvider
import org.springframework.beans.factory.annotation.Autowired
import io.spring.initializr.generator.ProjectRequest

/**
 * Generate a project file structure based on the configured metadata.
 *
 * @author Gabriela Tikhonova
 * @since 1.0.2
 */
public abstract class Project {
    ProjectRequest request
    Map model
    File rootDir
    Map<String,File> directories = new HashMap<String,File>()
    GroovyTemplate groovyTemplate = new GroovyTemplate()
    protected File staticFiles

    @Autowired
    ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator()

    Project(ProjectRequest request, Map model, File rootDir){
        this.request = request
        this.model = model
        this.rootDir = rootDir
        this.staticFiles = 	generateStaticFiles(request.build)
    }

    /**
     * This generates all the static directories. such as: gitignore, gitlab-ci.yml and wrapper files
     * @param buildType(gradle or maven currently)
     */
    protected void generateStaticFiles(String buildType){
        doGenerateGitDirectories(rootDir)
        generateBuildDirs(buildType)
    }
    /**
     * Generates all directories for each Project instance
     * @param dir: root directory
     * @return dir: directory with generated files and directories
     */
    public abstract void doGenerateProjectDirectories()

    /**
     * This method generates a gradle or maven wrapper according to build type
     * @param buildType
     */
    void generateBuildDirs(String buildType){
        if (buildType.equals("gradle")){
            doGenerateGradleDirectories(rootDir)
        }
        else if (buildType.equals("maven")){
            doGenerateMavenDirectories(rootDir)
        }

    }

    /**
     * Makes the directories hash
     */
    void generateDirectories(){
        for (String key:directories.keySet()){
            generateDirectory(directories.get(key))
        }
    }

    /**
     * generate a File from a template, into the directory specified
     * with the specified file name
     * @param dir
     * @param name
     * @param templateName
     * @return
     */
    File generateFile(File dir, String name, String templateName){
        new File(dir,name).write(new String(doGenerateFilefromTemplate(model,templateName)))
    }

    /**
     *
     * @param rootDir: root directory
     * @param dirStruct
     * @return
     */
    File generateDirectory(File dir, String dirStruct){
        File genDir = new File(dir,dirStruct)
        generateDirectory(genDir)
        return genDir
    }

    /**
     *
     * @param rootDir: root directory
     * @param dirStruct
     * @return
     */
    void generateDirectory(File dir){
        dir.mkdirs()
    }

    /**
     *
     * @param target File
     * @param templateName that references template files in the Template folder
     * @param model
     * @return
     */
    def write(File target, String templateName, def model) {
        def tmpl = templateName.endsWith('.groovy') ? templateName + '.tmpl' : templateName
        def body = groovyTemplate.process tmpl, model
        target.write(body)
    }


    /**
     * Generates Gradle wrapper
     * @param Root Directory: Type File java.io
     * @param model
     */
    private void writeGradleWrapper(File dir , Map model) {
        writeTextResource(dir, 'gradlew.bat', 'gradle/gradlew.bat')
        writeTextResource(dir, 'gradlew', 'gradle/gradlew')

        def wrapperDir = new File(dir, 'gradle/wrapper')
        wrapperDir.mkdirs()
        new File(wrapperDir, "gradle-wrapper.properties").write(new String(doGenerateFilefromTemplate(model, "gradle-wrapper.properties")))
        writeBinaryResource(wrapperDir, 'gradle-wrapper.jar',
                'gradle/gradle/wrapper/gradle-wrapper.jar')
    }

    /**
     * Generates the maven wrapper
     * @param root Directory
     */
    private void writeMavenWrapper(File dir) {
        writeTextResource(dir, 'mvnw.cmd', 'maven/mvnw.cmd')
        writeTextResource(dir, 'mvnw', 'maven/mvnw')

        def wrapperDir = new File(dir, '.mvn/wrapper')
        wrapperDir.mkdirs()
        writeTextResource(wrapperDir, 'maven-wrapper.properties',
                'maven/wrapper/maven-wrapper.properties')
        writeBinaryResource(wrapperDir, 'maven-wrapper.jar',
                'maven/wrapper/maven-wrapper.jar')
    }

    /**
     *
     * @param model
     * @param desired file from the templates folder
     * @return generated file
     */
    public byte[] doGenerateFilefromTemplate(Map model, String filename){
        groovyTemplate.process filename, model
    }

    /**
     *
     * @param root dir for the file
     * @param desired filename for the output file
     * @param desired location
     * @return generated resource
     */
    private File writeBinaryResource(File dir, String name, String location) {
        doWriteProjectResource(dir, name, location, true)
    }

    /**
     *
     * @param root dir for the text resource
     * @param desired filename for the output file
     * @param desired location
     * @return generated resource
     */
    private File writeTextResource(File dir, String name, String location) {
        doWriteProjectResource(dir, name, location, false)
    }


    /**
     *
     * @param model
     * @return
     */
    private byte[] doGenerateGradleBuild(Map model) {
        groovyTemplate.process 'starter-build.gradle', model
    }

    /**
     *
     * @param model
     * @return
     */
    private byte[] doGenerateWebXml(Map model) {
        groovyTemplate.process 'web.xml', model
    }

    /**
     *
     * @param dir
     * @param name
     * @param location
     * @param binary
     * @return
     */
    private File doWriteProjectResource(File dir, String name, String location, boolean binary) {
        def target = new File(dir, name)
        if (binary) {
            target << projectResourceLocator.getBinaryResource("classpath:project/$location")
        }
        else {
            target.write(projectResourceLocator.getTextResource("classpath:project/$location"))
        }
        target
    }

    /**
     * @param root directory for Gradle files
     * @return gradle specific files
     */
    File doGenerateGradleDirectories(File dir){
        writeGradleWrapper(dir, model)
        new File(dir, 'gradle.properties').write(new String(doGenerateFilefromTemplate(model, 'starter-gradle.properties')))

    }

    /**
     * @param root directory type File java.io
     * @return File java.io: maven project specific files
     */
    File doGenerateMavenDirectories(File dir){
        new File(dir, 'pom.xml').write(new String(doGenerateFilefromTemplate(model, "starter-pom.xml")))
        writeMavenWrapper(dir)
    }

    /**
     * Generates gitignore and gitlab-ci.yml files
     * @param directory to place the git files
     * @return File java.io: gitlab specific files (gitignore and gitlab-ci.yml)
     */
    File doGenerateGitDirectories(File dir){
        writeTextResource(dir,"gitignore.hidden","gitignore.hidden")
        new File(dir, 'gitlab-ci.yml.hidden').write(new String(doGenerateFilefromTemplate(model,'gitlab-ci.yml.hidden')))
    }

}