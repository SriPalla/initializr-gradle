This project demonstrates build, package and publish a JAR archive.

Here, the build script (build.gradle) file defines minimum configurations for a typical build

gradle-**build-defaults**-plugin will apply the usaa defaults. For more information, refer to https://gitlab.usaa.com/grp-itps-java/gradle-build-defaults-plugin

The .gitlab-ci.yml file is used for builds on every commit.


Usage:-
1. Create your own project within gitlab
2. Set this project's remote to your new gitlab project
  * git remote add origin git@gitlab.usaa.com:grp-name/project-name.git
3. Make changes to include appropriate files, ex: java, config files.
4. Edit the build gradle and update your project dependencies.
5. Edit gradle properties and update group, version information
6. Run the appropriate tasks

On developers workstation(s):
`./gradlew clean build`
  Note: Developers will NOT be publishing to SonarQube or Artifactory from workstation(s)

In gitlab-CI workflow:
`./gradlew build jacoco sonarqube artifactoryPublish`

Note: "./gradlew build" will create the application jar and we can verify the "build" output directory.