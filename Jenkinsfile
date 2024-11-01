pipeline {
    agent any
    environment {
    DOCKERHUB_USERNAME = "saiditayssir"
  }

    stages {
         stage('Get Started') {
      steps {
        echo "Start Building Pipeline"
      }
    }
    // stage('GIT Checkout') {
     // steps {
      //  git branch: 'master',
       // url: 'https://github.com/SaidiTA/5SAE6_G1_Kaddem'
     // }
   // }




    stage("Clone from Git") {
        steps {
            git url: 'git@github.com:TheVIChosen/5SAE6-G1-Kaddem.git',
            credentialsId: 'git',
            branch: 'saiditayssir_5sae6_g1'
        }
    }
    stage('Status Mysql') {
        steps {
                script {
                    sh 'docker start dbmysql_new'
                }
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
   /*     stage("Run Tests with JUnit") {
            steps {
                // Runs JUnit tests and generates JaCoCo coverage reports
                sh "mvn test jacoco:report"
            }
            post {
                // Publish JUnit test results in Jenkins
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }*/

  stage('Static Analysis') {

            //environment {
            //    SONAR_URL = "http://192.168.100.11:9000/"
            //    SONAR_TOKEN = "sqp_40cb2dfefc432f64ac7cc2f21ae379d40924b0e7"
           // }
           environment {
                scannerHome = tool 'sonnarqubeScanner';
            }
            steps {
                 withSonarQubeEnv(credentialsId: 'token_sonar', installationName: 'Sonarqube') {
                  //  sh 'mvn sonar:sonar -Dsonar.token=${SONAR_TOKEN} -Dsonar.host.url=${SONAR_URL} -Dsonar.java.binaries=target/classes'
               sh "${scannerHome}/bin/sonar-scanner \
        -Dsonar.projectKey=backend_kaddem \
        -Dsonar.java.binaries=target/classes \
        -Dsonar.sources=src/main/java \
        -Dsonar.host.url=http://192.168.100.11:9000/"


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
                        nexusUrl: '192.168.100.11:8081',
                        repository: 'back_end_repo',
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

                    echo "Deployment to Nexus completed!"
                }
            }
        }

        stage('Docker Image') {
           // steps {
            //    echo 'Building Docker image for Spring Boot...'
           //     sh 'docker build -t saiditayssir/springboot-app:v1.0.0 . '
          //  }
          steps {
    echo 'Building Docker image for Spring Boot...'
    sh 'docker build -t saiditayssir/springboot-app:v1.0.0 -f Dockerfile .'
}
        }

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'docker',
                  usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD"
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'docker',
                  usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker push saiditayssir/springboot-app:v1.0.0"
                }
            }
        }
    }
     post {
        always {
            jacoco execPattern: 'target/jacoco.exec'
           // junit '**/target/surefire-reports/*.xml'
        }
    }
}