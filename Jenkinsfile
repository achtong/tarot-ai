pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('React Build') {
            steps {
                dir('frontend') {
                    bat 'call npm ci'
                    bat 'call npm run build'
                }
            }
        }

        stage('Copy React to Spring Boot') {
            steps {
                powershell '''
                    $static = "backend/src/main/resources/static"

                    New-Item -ItemType Directory -Force -Path $static | Out-Null

                    Get-ChildItem $static -Force |
                        Remove-Item -Recurse -Force

                    Copy-Item "frontend/dist/*" $static -Recurse -Force

                    Write-Host "React dist 복사 완료"
                '''
            }
        }

        stage('Spring Boot Build') {
            steps {
                dir('backend') {
                    bat 'call gradlew.bat clean bootJar'
                }
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'backend/build/libs/*.jar',
                                 fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'CI 빌드 성공!'
        }
        failure {
            echo 'CI 빌드 실패! 콘솔 로그를 확인하세요.'
        }
    }
}