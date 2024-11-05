pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "admin" 
         NEXUS_CREDENTIALS = credentials('nexus') 
    }

    stages {
        stage('Initialization') {
            steps {
                echo "Start Building Pipeline"
                git branch: 'omarbenfathallah-5SAE6-G1',
                url: 'https://github.com/TheVIChosen/5SAE6-G1-Kaddem.git'
            }
        }
        stage('Clean ') {
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
       stage('Static Analysis') {
            environment {
                scannerHome = tool 'sonarqubeScanner'
            }
            steps {
                withCredentials([string(credentialsId: 'sonar', variable: 'SONAR_TOKEN')]) {
                    withSonarQubeEnv('Sonarqube') {
                        sh "${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=kaddemomar \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.host.url=http://10.0.2.15:9002 \
                            -Dsonar.login=${SONAR_TOKEN}"
                    }
                }
            }
        }
        stage("Deploy to Nexus") {
            steps {
                script {
                    sh "mvn deploy -DskipTests -Dnexus.username=${NEXUS_CREDENTIALS_USR} -Dnexus.password=${NEXUS_CREDENTIALS_PSW}"
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
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                                                  usernameVariable: 'DOCKERHUB_USERNAME', 
                                                  passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD"
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to DockerHub...'
                withCredentials([usernamePassword(credentialsId: 'docker',
                                                  usernameVariable: 'DOCKERHUB_USERNAME', 
                                                  passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker push omarbenfathallah/kaddemm-app:v1.0.0"
                }
            }
        }
        stage('Docker Push') {
            steps {
                echo 'testing application ...'
               
                    sh "docker composer up -d"
            }
        }

    }
}
