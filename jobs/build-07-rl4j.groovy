    tool name: 'M339', type: 'maven'
   def mvnHome
   mvnHome = tool 'M339'
   stage('Rl4j Preparation')    {
    checkout([$class: 'GitSCM',
       branches: [[name: '*/intropro']],
       doGenerateSubmoduleConfigurations: false,
       extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: '$RL4J_PROJECT'], [$class: 'CloneOption', honorRefspec: true, noTags: true, reference: '', shallow: true]],
       submoduleCfg: [],
       userRemoteConfigs: [[url: 'https://github.com/$ACCOUNT/$RL4J_PROJECT.git']]])
    }
    echo 'Releasing version $RELEASE_VERSION ($SNAPSHOT_VERSION) to repository $STAGING_REPOSITORY'
    echo 'Check if $RELEASE_VERSION has been released already'
    dir("$RL4J_PROJECT") {
      def exitValue = sh (returnStdout: true, script: """git tag -l \"$RL4J_PROJECT-$RELEASE_VERSION\"""")
      if (exitValue != null) {
        //  echo "Error: Version $RELEASE_VERSION has already been released!"
        error 'Version $RELEASE_VERSION has already been released!'
      }
      sh ("sed -i 's/<nd4j.version>.*<\\/nd4j.version>/<nd4j.version>$RELEASE_VERSION<\\/nd4j.version>/' pom.xml")
      sh ("sed -i 's/<datavec.version>.*<\\/datavec.version>/<datavec.version>$RELEASE_VERSION<\\/datavec.version>/' pom.xml")
      sh ("sed -i 's/<dl4j.version>.*<\\/dl4j.version>/<dl4j.version>$RELEASE_VERSION<\\/dl4j.version>/' pom.xml")
      sh ("'${mvnHome}/bin/mvn' versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=$RELEASE_VERSION")
    }

   stage ('Rl4j Build') {
    dir("$RL4J_PROJECT") {
    //  configFileProvider(
    //   [configFile(fileId: '$MAVENSETS', variable: 'MAVEN_SETTINGS')]) {
     //sh "'${mvnHome}/bin/mvn' clean deploy -Dgpg.executable=gpg2 -DperformRelease -Psonatype-oss-release -DskipTests -DstagingRepositoryId=$STAGING_REPOSITORY"
    //sh "git commit -a -m 'Update to version $RELEASE_VERSION'"
    //sh "git tag -a -m '$RL4J_PROJECT-$RELEASE_VERSION" "$RL4J_PROJECT-$RELEASE_VERSION'"
     sh "sed -i 's/<nd4j.version>.*<\\/nd4j.version>/<nd4j.version>$SNAPSHOT_VERSION<\\/nd4j.version>/' pom.xml"
     sh "sed -i 's/<datavec.version>.*<\\/datavec.version>/<datavec.version>$SNAPSHOT_VERSION<\\/datavec.version>/' pom.xml"
     sh "sed -i 's/<dl4j.version>.*<\\/dl4j.version>/<dl4j.version>$SNAPSHOT_VERSION<\\/dl4j.version>/' pom.xml"

     sh ("'${mvnHome}/bin/mvn' versions:set -DallowSnapshots=true -DgenerateBackupPoms=false -DnewVersion=$SNAPSHOT_VERSION")
     //sh "git commit -a -m 'Update to version $SNAPSHOT_VERSION'"
     sh "echo 'Successfully performed release of version $RELEASE_VERSION ($SNAPSHOT_VERSION) to repository $STAGING_REPOSITORY'"
    }
   }
   // Message for debugging 
   echo 'MARK: end of build-07-rl4j.groovy'
