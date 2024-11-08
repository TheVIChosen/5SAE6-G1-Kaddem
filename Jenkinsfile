pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "saiditayssir"
    }


stages {
        stage("Clone from Git") {
            steps {
                git url: 'git@github.com:TheVIChosen/5SAE6-G1-Kaddem.git',
                    credentialsId: 'git',
                    branch: 'saiditayssir_5sae6_g1'
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

	////stage('OWASP SCAN'){
	//	steps{
	//////			dependencyCheck additionalArguments: '', odcInstallation: 'DP-check'
	//			dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
	////	}
	//}
	stage("JUnit and Mockito") {
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
        }
        stage('Static Analysis') {
            environment {
                scannerHome = tool 'sonnarqubeScanner'
            }
            steps {
                withCredentials([string(credentialsId: 'token_sonar_backend', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('Sonarqube') {
                        sh "${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=backend_kaddem \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.host.url=http://192.168.100.11:9000 \
                            -Dsonar.login=${SONAR_TOKEN}"
                    }
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
            steps {
                echo 'Building Docker image for Spring Boot...'
                sh 'docker build -t saiditayssir/springboot-app:v1.0.0 -f Dockerfile .'
            }
        }

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'docker',
                                                  usernameVariable: 'DOCKERHUB_USERNAME', 
                                                  passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD"
                }
            }
        }

	 //   stage('Trivy') {
        //  //  steps {
          ////      sh 'trivy --skip-update image springboot-app:v1.0.0 '
          //  }
	//    }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'docker',
                                                  usernameVariable: 'DOCKERHUB_USERNAME', 
                                                  passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker push saiditayssir/springboot-app:v1.0.0"
                }
            }
        }
	

	stage('Docker compose BackEnd') {
          steps {
               script {
               //    sh 'docker stop dbmysql_new'
                   sh 'docker compose up -d'
              }
           }
     }
		    stage('Prometheus & Grafana') {
          steps {
               script {
                   sh 'docker start 1517a07aab38'
                   sh 'docker start 47c63a862074'
              }
           }
     }


        stage('Slack Notification') {
            steps {
                slackSend channel: '#thevchosen', message: "Successful completion of ${env.JOB_NAME}", teamDomain: 'devops-d4e9866', tokenCredentialId: 'slack1'
            }
        }
    }
post {
        success {
            echo 'Build and tests were successful!'
            emailext(
                to: 'taycyrsaidi456@gmail.com',
                from: 'Saidi.Tayssir@esprit.tn', // Set the sender email address
                replyTo: 'Saidi.Tayssir@esprit.tn', // Set the reply-to address
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS",
                body: "The build ${env.BUILD_NUMBER} has completed successfully.\nCheck details at: ${env.BUILD_URL}"
            )
        }
        
        failure {
            echo 'Build or tests failed!'
            emailext(
                to: 'taycyrsaidi456@gmail.com',
                from: 'Saidi.Tayssir@esprit.tn', // Set the sender email address
                replyTo: 'Saidi.Tayssir@esprit.tn', // Set the reply-to address
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE",
                body: "The build ${env.BUILD_NUMBER} has failed.\nCheck details at: ${env.BUILD_URL}"
            )
        }
    }
   // post {
     //   always {
     //       jacoco execPattern: 'target/jacoco.exec'
     //   }
 //   }
}
