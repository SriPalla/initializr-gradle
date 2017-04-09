This project demonstrates, Package and distribute Zip archive that includes application jar generated and also dependency jars, configs, bin and deploy files.
This is handled via multi project dependencies. Similar to the combined WAR & EAR project.

JAR generated from the application project is not published.
Instead the zip archive generated from GradleZipProjDistTemplate project is published.

Here, the build script (build.gradle) file defines minimum configurations for a typical build

gradle-**build-defaults**-plugin will apply the usaa defaults. For more information, refer to README.md @ https://gitlab.usaa.com/grp-itps-java/gradle-build-defaults-plugin


The .gitlab-ci.yml file is used for builds on every commit.

Usage:-
1. Make changes to include appropriate files, ex: java, bin, config, deploy files.
2. Edit the build gradle and update your project dependencies.
3. Edit gradle.properties and update group, version information
4. Run the appropriate tasks

On developers workstation(s):
`./gradlew clean build`
  Note: Developers will NOT be publishing to SonarQube or Artifactory from workstation(s)

In gitlab-CI workflow:
`./gradlew build jacoco sonarqube artifactoryPublish`

Note: "./gradlew build" will create the application jar and we can verify the build output.
"./gradlew distZip" will create the zip and we can verify the build output before publishing.