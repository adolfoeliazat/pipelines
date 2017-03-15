properties([
    [$class: "BuildDiscarderProperty",
        strategy: [
            $class: "LogRotator",
            artifactDaysToKeepStr: "",
            artifactNumToKeepStr: "",
            daysToKeepStr: "",
            numToKeepStr: "10"
        ]
    ],
    [$class: "ParametersDefinitionProperty",
        parameterDefinitions: [
            [$class: "StringParameterDefinition",
                name: "VERSION",
                defaultValue: "0.7.3-SNAPSHOT",
                description: "Deeplearning component release version"
            ],
            [$class: "BooleanParameterDefinition",
                name: "SKIP_TEST",
                defaultValue: false,
                description: "Select to run tests during mvn execution"
            ],
            [$class: "BooleanParameterDefinition",
                name: "SONAR",
                defaultValue: false,
                description: "Select to check code with SonarQube"
            ],
            [$class: "StringParameterDefinition",
                name: "GIT_BRANCHNAME",
                defaultValue: "intropro072-01",
                description: "Default Git branch value"
            ],
            [$class: "CredentialsParameterDefinition",
                name: "GITCREDID",
                required: false,
                defaultValue: "github-private-deeplearning4j-id-1",
                description: "Credentials to be used for cloning, pushing and tagging deeplearning4j repositories"
            ],
            [$class: "ChoiceParameterDefinition",
                name: "PROFILE_TYPE",
                choices: "nexus\njfrog\nbintray\nsonatype",
                description: "Profile type"
            ],
            [$class: "BooleanParameterDefinition",
                name: "CBUILD",
                defaultValue: true,
                description: "Select to build libnd4j"
            ],
        ]
    ]
])


node("master") {

    env.PDIR = "jobs/dl4j"

    stage("Build-multiplatform-parallel") {
        parallel (
            "Stream 0 linux-x86_64" : {
                build job: 'devel/dl4j/all-linux-x86_64', parameters:
                    [[$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "linux-x86_64"],
                    [$class: 'StringParameterValue',name: 'VERSION', value: VERSION],
                    [$class: 'StringParameterValue',name: 'GIT_BRANCHNAME', value: GIT_BRANCHNAME],
                    [$class: 'StringParameterValue',name: 'GITCREDID', value: GITCREDID],
                    [$class: 'StringParameterValue',name: 'PROFILE_TYPE', value: PROFILE_TYPE]]
            },
            "Stream 1 linux-ppc64le" : {
                build job: 'devel/dl4j/all-linux-ppc64le', parameters:
                    [[$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "linux-ppc64le"],
                    [$class: 'StringParameterValue',name: 'VERSION', value: VERSION],
                    [$class: 'StringParameterValue',name: 'GIT_BRANCHNAME', value: GIT_BRANCHNAME],
                    [$class: 'StringParameterValue',name: 'GITCREDID', value: GITCREDID],
                    [$class: 'StringParameterValue',name: 'PROFILE_TYPE', value: PROFILE_TYPE]]
            },
            "Stream 2 android-x86" : {
                build job: 'devel/dl4j/all-android-x86', parameters:
                    [[$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "android-x86"],
                    [$class: 'StringParameterValue',name: 'VERSION', value: VERSION],
                    [$class: 'StringParameterValue',name: 'GIT_BRANCHNAME', value: GIT_BRANCHNAME],
                    [$class: 'StringParameterValue',name: 'GITCREDID', value: GITCREDID],
                    [$class: 'StringParameterValue',name: 'PROFILE_TYPE', value: PROFILE_TYPE]]
             },
            "Stream 3 android-arm" : {
                build job: 'devel/dl4j/all-android-arm', parameters:
                    [[$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "android-arm"],
                    [$class: 'StringParameterValue',name: 'VERSION', value: VERSION],
                    [$class: 'StringParameterValue',name: 'GIT_BRANCHNAME', value: GIT_BRANCHNAME],
                    [$class: 'StringParameterValue',name: 'GITCREDID', value: GITCREDID],
                    [$class: 'StringParameterValue',name: 'PROFILE_TYPE', value: PROFILE_TYPE]]
            },
            "Stream 4 windows-x86_64" : {
                build job: 'devel/dl4j/all-windows', parameters:
                    [[$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "windows-x86_64"],
                    [$class: 'StringParameterValue',name: 'VERSION', value: VERSION],
                    [$class: 'StringParameterValue',name: 'GIT_BRANCHNAME', value: GIT_BRANCHNAME],
                    [$class: 'StringParameterValue',name: 'GITCREDID', value: GITCREDID],
                    [$class: 'StringParameterValue',name: 'PROFILE_TYPE', value: PROFILE_TYPE]]
            },
            "Stream 5 macosx-x86_64" : {
                build job: 'devel/dl4j/all-macosx', parameters:
                    [[$class: 'StringParameterValue', name:'PLATFORM_NAME', value: "macosx"],
                    [$class: 'StringParameterValue',name: 'VERSION', value: VERSION],
                    [$class: 'StringParameterValue',name: 'GIT_BRANCHNAME', value: GIT_BRANCHNAME],
                    [$class: 'StringParameterValue',name: 'GITCREDID', value: GITCREDID],
                    [$class: 'StringParameterValue',name: 'PROFILE_TYPE', value: PROFILE_TYPE]]
            }
        )
    }
}
