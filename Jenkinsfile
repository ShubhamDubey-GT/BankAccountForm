pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/your-org/BankAccountForm.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    // JUnit test results
                    junit 'target/surefire-reports/*.xml'

                    // Cucumber JSON report
                    cucumber fileIncludePattern: 'target/cucumber-reports/cucumber.json'

                    // Archive Extent & other reports
                    archiveArtifacts artifacts: 'Reports/*.html, target/cucumber-reports/*.html, test-output/*.png', fingerprint: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
            }
            steps {
                echo 'Deploying application...'
                // Add deployment steps here
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
        }
    }
}
