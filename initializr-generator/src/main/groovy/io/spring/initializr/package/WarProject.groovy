package io.spring.initializr.packaging
import io.spring.initializr.generator.ProjectRequest

public class WarProject extends JavaProject {
    static final String KEY_WEBAPP = "webApp"
    static final String KEY_WEBINF = "webInf"
    static final String KEY_METAINF = "webApp"

    WarProject (ProjectRequest request, Map model, File rootDir) {
        super(request,model,rootDir)
    }

    /**
     * generates not static files for a War Project.
     * @param buildType
     */
    @Override
    protected void generateStaticFiles(String buildType){
    }

    /**
     * generate all directories for an War Project
     */
    void doGenerateProjectDirectories() {
        doGenerateJavaDirectories(rootDir)
        directories.put(KEY_WEBAPP,new File(rootDir,"src/main/webapp"))
        generateDirectories()
        directories.put(KEY_WEBINF,new File(directories.get(KEY_WEBAPP),"WEB-INF"))
        directories.put(KEY_METAINF,new File(directories.get(KEY_WEBAPP),"META-INF"))
        generateDirectories()
        new File(directories.get(KEY_WEBINF), "web.xml").write(new String(doGenerateFilefromTemplate(model,"web.xml")))
        new File(rootDir, 'build.gradle').write(new String(doGenerateFilefromTemplate(model, "starter-war-build.gradle")))

    }

}