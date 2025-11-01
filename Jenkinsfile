pipeline {
    agent any

    tools {
    	jdk 'jdk17'
    }


    environment {
        ANDROID_HOME = "/Users/jenkins/Library/Android/sdk"
        GRADLE_OPTS = "-Xms512m -Xmx2048m"
    }

stage('Env check') {
    steps {
        sh "java -version"
        sh "echo JAVA_HOME=$JAVA_HOME"
    }
}


    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Gradle Clean') {
            steps {
                sh "./gradlew clean"
            }
        }

        stage('Build Shared KMM Code') {
            steps {
                sh "./gradlew :shared:assemble"
            }
        }

        stage('Build Android App') {
            steps {
                sh "./gradlew :androidApp:assembleDebug"
            }
        }
        stage('Unit Tests') {
            steps {
                sh "./gradlew test"
            }
        }

        stage('Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/outputs/**/*.apk', fingerprint: true
                archiveArtifacts artifacts: '**/build/XCFrameworks/**/*', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✅ ✅ KMM build success!"
        }
        failure {
            echo "❌ Build failed!"
        }
    }
}

