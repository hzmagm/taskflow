pipeline {
    agent any

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
            steps {
                // Updates image tag dynamically and applies manifests
                bat """
                    kubectl set image -f k8s/deployment.yml taskflow=${DOCKER_IMAGE}:${IMAGE_TAG} --local -o yaml | kubectl apply -f -
                """
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