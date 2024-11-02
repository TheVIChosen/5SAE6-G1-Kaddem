pipeline {
    agent any
    environment {
        DOCKERHUB_USERNAME = "saiditayssir"
        BASE_VERSION = "1.0.0" // Starting version
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
                branch: 'saiditayssir_5sae6_g1'
            }
        }

        stage('Increment Version') {
            steps {
                script {
                    // Check if version.txt exists
                    def versionFile = 'version.txt'
                    if (fileExists(versionFile)) {
                        // Read the current version
                        def currentVersion = readFile(versionFile).trim()
                        echo "Current version: ${currentVersion}"

                        // Increment the version (simple patch increment)
                        def (major, minor, patch) = currentVersion.tokenize('.').collect { it as int }
                        patch += 1 // Increment the patch version
                        def newVersion = "${major}.${minor}.${patch}"
                        writeFile file: versionFile, text: newVersion // Update the version file
                        echo "New version: ${newVersion}"

                        // Use the new version for later stages
                        env.NEXUS_VERSION = newVersion
                    } else {
                        // If the version file does not exist, create it with the base version
                        writeFile file: versionFile, text: BASE_VERSION
                        env.NEXUS_VERSION = BASE_VERSION
                        echo "Version file created with base version: ${BASE_VERSION}"
                    }
                }
            }
        }

        stage('Status Mysql') {
            steps {
                sh 'docker start dbmysql_new'
            }
        }

        stage('Clean') {
            steps {
                sh 'mvn clean'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn package'
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
                    echo "Deploying to Nexus with version ${NEXUS_VERSION}..."

                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: '192.168.100.11:8081',
                        repository: 'back_end_repo',
                        credentialsId: 'nexus',
                        groupId: 'tn.esprit.spring',
                        version: "${NEXUS_VERSION}",
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
                sh "docker build -t saiditayssir/springboot-app:v${NEXUS_VERSION} -f Dockerfile ."
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker login -u \$DOCKERHUB_USERNAME -p \$DOCKERHUB_PASSWORD"
                }
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    sh "docker push saiditayssir/springboot-app:v${NEXUS_VERSION}"
                }
            }
        }
    }
    post {
        always {
            jacoco execPattern: 'target/jacoco.exec'
        }
    }
}
