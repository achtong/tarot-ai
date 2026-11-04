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

        stage('Test OCI SSH') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'oci-ssh-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"

                        # Jenkins가 생성한 개인키 파일의 ACL 확인
                        $keyPath = $env:SSH_KEY
                        $currentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name

                        # 상속된 권한 제거
                        icacls $keyPath /inheritance:r
                        if ($LASTEXITCODE -ne 0) { throw "ACL 상속 제거 실패" }

                        # 현재 Jenkins 실행 사용자에게 읽기 권한 부여
                        icacls $keyPath /grant:r "${currentUser}:(R)"
                        if ($LASTEXITCODE -ne 0) { throw "ACL 설정 실패" }

                        # OCI SSH 연결 테스트
                        & ssh -i $keyPath -o BatchMode=yes "$($env:SSH_USER)@150.230.62.194" "echo OCI SSH CONNECTION SUCCESS"
                        if ($LASTEXITCODE -ne 0) { throw "OCI SSH 연결 실패" }
                    '''
                }
            }
        }

        stage('Upload JAR to OCI') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'oci-ssh-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"

                        $keyPath = $env:SSH_KEY
                        $currentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name

                        icacls $keyPath /inheritance:r | Out-Null
                        if ($LASTEXITCODE -ne 0) { throw "SSH 키 권한 설정 실패" }

                        icacls $keyPath /grant:r "${currentUser}:(R)" | Out-Null
                        if ($LASTEXITCODE -ne 0) { throw "SSH 키 읽기 권한 설정 실패" }

                        $jar = "backend/build/libs/demo-0.0.1-SNAPSHOT.jar"

                        if (-not (Test-Path $jar)) {
                            throw "JAR 파일을 찾을 수 없습니다: $jar"
                        }

                        & scp -i $keyPath -o BatchMode=yes $jar "$($env:SSH_USER)@150.230.62.194:/home/ubuntu/demo-0.0.1-SNAPSHOT.jar.new"

                        if ($LASTEXITCODE -ne 0) {
                            throw "OCI JAR 전송 실패"
                        }

                        Write-Host "OCI JAR 전송 성공!"
                    '''
                }
            }
        }

        stage('Deploy to OCI') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'oci-ssh-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"

                        $keyPath = $env:SSH_KEY
                        $currentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name

                        icacls $keyPath /inheritance:r | Out-Null
                        if ($LASTEXITCODE -ne 0) {
                            throw "SSH 키 권한 설정 실패"
                        }

                        icacls $keyPath /grant:r "${currentUser}:(R)" | Out-Null
                        if ($LASTEXITCODE -ne 0) {
                            throw "SSH 키 읽기 권한 설정 실패"
                        }

                        $commands = @(
                            "set -eu",
                            "cd /home/ubuntu",
                            "test -s demo-0.0.1-SNAPSHOT.jar.new",
                            "sudo -n docker image inspect tarot-app:1.1 > /dev/null",
                            "sudo -n docker tag tarot-app:1.1 tarot-app:backup",
                            "cp demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar.backup",
                            "mv demo-0.0.1-SNAPSHOT.jar.new demo-0.0.1-SNAPSHOT.jar",
                            "sudo -n docker build -t tarot-app:1.1 .",
                            "sudo -n docker compose up -d --no-deps --force-recreate tarot-app",
                            "sudo -n docker compose ps tarot-app"
                        )

                        # BOM 없는 UTF-8 파일 생성
                        $scriptPath = Join-Path $env:WORKSPACE "oci-deploy.sh"
                        $utf8NoBom = New-Object System.Text.UTF8Encoding($false)

                        [System.IO.File]::WriteAllText(
                            $scriptPath,
                            $remoteScript,
                            $utf8NoBom
                        )

                        # SSH 표준 입력으로 파일 내용 전달
                        $sshCommand = 'ssh -i "{0}" -o BatchMode=yes "{1}@150.230.62.194" "bash -se" < "{2}"' -f $keyPath, $env:SSH_USER, $scriptPath

                        cmd.exe /d /c $sshCommand

                        if ($LASTEXITCODE -ne 0) {
                            throw "OCI Docker 배포 실패"
                        }

                        Write-Host "OCI Docker 배포 명령 실행 완료!"
                    '''
                }
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