pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "admin"
        NEXUS_CREDENTIALS = credentials('nexus') // This assumes you have the correct 'nexus' credentials ID
    }

    stages {
        stage('Get Started') {
            steps {
                echo "Start Building Pipeline"
            }
        }

        stage("Clone from Git") {
            steps {
                git url: 'git@github.com:TheVIChosen/5SAE6-G1-Kaddem.git',
                    credentialsId: 'git',
                    branch: 'omarbenfathallah-5SAE6-G1'
            }
        }
       

        stage('Clean') {
            steps {
                echo 'Cleaning previous builds and cache...'
                sh 'mvn clean'
            }
        }

        stage('Build project') {
            steps {
                echo 'Building the application...'
                sh 'mvn package -DskipTests'
            }
        }

     /*   stage("Run Unit Tests with JUnit and Mockito") {
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
            environment {
                scannerHome = tool 'sonarqubeScanner'
            }
            steps {
                withCredentials([string(credentialsId: 'sonartokenn', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('Sonarqube') {
                        sh "${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=springproject \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.host.url=http://10.0.2.15:9002 \
                            -Dsonar.login=${SONAR_TOKEN}"
                    }
                }
            }
        }
        stage('Nexus') {
            steps {
                script {
                    echo "Deploying to Nexus..."
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '10.0.2.15:8081',
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
                    echo "Deployment to Nexus completed!"
                }
            }
        }

        

        stage('Docker Image') {
            steps {
                echo 'Building Docker image for Spring Boot...'
                sh 'docker build -t omarbenfathallah/kaddemm-app:v1.0.0 -f Dockerfile .'
            }
        }

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD"
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to DockerHub...'
                sh "docker push omarbenfathallah/kaddemm-app:v1.0.0"
            }
        }

       stage('Docker Compose Up') {
            steps {
               echo 'Testing application using Docker Compose...'
             // sh "docker compose  -f ./docker-compose.yml up -d kaddemomar"
                sh "docker-compose up -d"
          }
        }

       
    }
}
