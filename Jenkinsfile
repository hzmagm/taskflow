pipeline {
    agent any
    triggers {
        pollSCM('H/1 * * * *')
    }
    environment {
        DOCKER_IMAGE = 'hzmasbl/taskflow'
        IMAGE_TAG = "${BUILD_NUMBER}"
        DOCKER_CREDS = 'dockerhub-credentials-id' // ID of credentials stored in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run Tests') {
            steps {
                // Runs Maven tests (including Testcontainers if configured)
                bat './mvnw clean test' // Use 'sh' if Jenkins runs on Linux
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDS}") {
                        def customImage = docker.build("${DOCKER_IMAGE}:${IMAGE_TAG}", ".")
                        customImage.push()
                        customImage.push("latest")
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            environment {
                KUBECONFIG = 'C:\\Users\\33695\\.kube\\config'
            }
            steps {
                        bat 'kubectl apply -f k8s/deployment.yml'

                        bat "kubectl set image deployment/taskflow-app taskflow=hzmasbl/taskflow:${BUILD_NUMBER}"

                        bat 'kubectl rollout status deployment/taskflow-app'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Taskflow successfully built and deployed to Kubernetes!"
        }
        failure {
            echo "Pipeline failed."
        }
    }
}