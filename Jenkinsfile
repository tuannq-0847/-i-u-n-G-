pipeline {
    agent any

    environment {
        ANDROID_HOME = "/Users/jenkins/Library/Android/sdk"
        JAVA_HOME = "/usr/local/Cellar/openjdk/25.0.1/libexec/openjdk.jdk/Contents/Home"
        GRADLE_OPTS = "-Xms512m -Xmx2048m"
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

