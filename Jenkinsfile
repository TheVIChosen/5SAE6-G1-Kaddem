pipeline {
    agent any
      environment {
            NEXUS_CREDENTIALS = credentials('nexus')
}
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
                    echo "Deploying ..."
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '192.168.33.10:8081',
                        repository: 'repo',
                        credentialsId: 'nexus',
                        groupId: 'tn.esprit.spring',
                        version: '1.0.0',
                        artifacts: [
                            [
                                artifactId: 'kaddem',
                                classifier: '',
                                file: 'target/kaddem-0.0.1-SNAPSHOT.jar',
                                type: 'jar'
                            ]
                        ]
                    )
                    echo "Deployment succesfully!"
                }
            }
        }
        
          stage('Docker Image') {
            steps {
                echo 'Building Docker image for Spring Boot...'
                sh 'docker build -t ellyssa378/kaddemdevops-app:v1.0.0 -f Dockerfile .'
            }
        }
        

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', 
                  usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD"
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', 
                  usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker push ellyssa378/kaddemdevops-app:v1.0.0"
                }
            }
        }


        
    } // Fin de stages
} // Fin de pipeline
