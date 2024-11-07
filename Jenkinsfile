pipeline {
    agent any


    stages {
        stage('Start') {
            steps {
                echo "Start Building Pipeline"
            }
        }
        stage('GIT Check') {
            steps {
                git branch: 'EllyssaKhalfaoui_5SAE6_G1',
                    credentialsId: 'git',
                    url: 'git@github.com:TheVIChosen/5SAE6-G1-Kaddem.git'
            }
        }

        stage('Clean') {
            steps {
                echo 'Cleaning previous builds and cache...'
                sh 'mvn clean'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the Spring Boot application...'
                sh 'mvn package'
            }
        }

    //   stage("Run Unit Tests with JUnit and Mockito") {
     //       steps {
                // Runs JUnit tests and generates JaCoCo coverage reports
     //           sh "mvn test jacoco:report"
    //        }
       //     post {
                // Publish JUnit test results in Jenkins
         //       always {
          //          junit 'target/surefire-reports/*.xml'
         //       }
         //   }
      //  }

        stage('SONAR') {
            environment {
                scannerHome = tool 'sonarqubeScanner'
            }
            steps {
                withSonarQubeEnv(credentialsId: 'sonartoken', installationName: 'Sonarqube') {
                    sh "${scannerHome}/bin/sonar-scanner \
                    -Dsonar.projectKey=springellyssa \
                    -Dsonar.java.binaries=target/classes \
                    -Dsonar.sources=src/main/java \
                    -Dsonar.host.url=http://192.168.33.10:9000/"
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                script {
                    echo "Deploying to Nexus..."

                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '192.168.33.10:8081',
                        repository: 'maven-kaddem-repository',
                        credentialsId: 'nexus',
                        groupId: 'tn.esprit.spring',
                        version: '1.0.2',
                        artifacts: [
                            [
                                artifactId: 'kaddem',
                                classifier: '',
                                file: 'target/kaddem-0.0.1-SNAPSHOT.jar',
                                type: 'jar'
                            ]
                        ]
                    )

                    echo "Deployment to Nexus completed!"
                }
            }
        }
    } // Fin de stages
} // Fin de pipeline
