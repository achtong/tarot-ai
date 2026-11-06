
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        JAR_NAME = 'demo-0.0.1-SNAPSHOT.jar'
        OCI_DIR = '/home/ubuntu'
        DOCKER_IMAGE = 'tarot-app:1.1'
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
                    $ErrorActionPreference = "Stop"

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
                archiveArtifacts(
                    artifacts: 'backend/build/libs/*.jar',
                    fingerprint: true
                )
            }
        }

        stage('Deploy to OCI') {
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'oci-ssh-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    ),
                    string(
                        credentialsId: 'oci-host',
                        variable: 'OCI_HOST'
                    )
                ]) {
                    powershell '''
                        $ErrorActionPreference = "Stop"

                        $keyPath = $env:SSH_KEY
                        $sshUser = $env:SSH_USER
                        $ociHost = $env:OCI_HOST

                        $jar = "backend/build/libs/$($env:JAR_NAME)"
                        $ociDir = $env:OCI_DIR
                        $dockerImage = $env:DOCKER_IMAGE

                        # 필수 환경변수 검증
                        if ([string]::IsNullOrWhiteSpace($keyPath)) {
                            throw "SSH_KEY가 비어 있습니다."
                        }

                        if ([string]::IsNullOrWhiteSpace($sshUser)) {
                            throw "SSH_USER가 비어 있습니다."
                        }

                        if ([string]::IsNullOrWhiteSpace($ociHost)) {
                            throw "OCI_HOST가 비어 있습니다."
                        }

                        if (-not (Test-Path $keyPath)) {
                            throw "SSH 개인키 파일을 찾을 수 없습니다."
                        }

                        if (-not (Test-Path $jar)) {
                            throw "JAR 파일을 찾을 수 없습니다: $jar"
                        }

                        # SSH 개인키 권한 설정
                        $currentUser = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name

                        icacls $keyPath /inheritance:r | Out-Null
                        if ($LASTEXITCODE -ne 0) {
                            throw "SSH 키 ACL 상속 제거 실패"
                        }

                        icacls $keyPath /grant:r "${currentUser}:(R)" | Out-Null
                        if ($LASTEXITCODE -ne 0) {
                            throw "SSH 키 읽기 권한 설정 실패"
                        }

                        $target = "${sshUser}@${ociHost}"

                        # 1. OCI SSH 연결 확인
                        Write-Host "OCI SSH 연결 테스트 시작"

                        & ssh.exe -i "$keyPath" -o "BatchMode=yes" -o "ConnectTimeout=15" $target "echo OCI SSH CONNECTION SUCCESS"

                        if ($LASTEXITCODE -ne 0) {
                            throw "OCI SSH 연결 실패"
                        }

                        # 2. JAR 업로드
                        Write-Host "OCI JAR 업로드 시작"

                        $destination = "${target}:${ociDir}/$($env:JAR_NAME).new"

                        # 대문자 O: 기존 SCP 프로토콜 사용
                        & scp.exe -O -i "$keyPath" -o "BatchMode=yes" "$jar" "$destination"

                        if ($LASTEXITCODE -ne 0) {
                            throw "OCI JAR 전송 실패"
                        }

                        Write-Host "OCI JAR 전송 성공!"

                        # 3. 원격 배포 스크립트 생성
                        $commands = @(
                            "set -eu",
                            "cd /home/ubuntu",
                            "test -s demo-0.0.1-SNAPSHOT.jar.new",
                            "sudo -n docker info > /dev/null",
                            "if sudo -n docker image inspect tarot-app:1.1 > /dev/null 2>&1; then sudo -n docker tag tarot-app:1.1 tarot-app:backup; fi",
                            "if [ -f demo-0.0.1-SNAPSHOT.jar ]; then cp demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar.backup; fi",
                            "mv demo-0.0.1-SNAPSHOT.jar.new demo-0.0.1-SNAPSHOT.jar",
                            "sudo -n docker build -t tarot-app:1.1 .",
                            "sudo -n docker compose up -d --no-deps --force-recreate tarot-app",
                            "sudo -n docker compose ps tarot-app"
                        )

                        $remoteScript = ($commands -join "`n") + "`n"

                        # BOM 없는 UTF-8 파일 생성
                        $scriptPath = Join-Path $env:WORKSPACE "oci-deploy.sh"
                        $utf8NoBom = New-Object System.Text.UTF8Encoding($false)

                        [System.IO.File]::WriteAllText(
                            $scriptPath,
                            $remoteScript,
                            $utf8NoBom
                        )

                        # 4. OCI Docker 배포
                        Write-Host "OCI Docker 배포 시작"

                        $sshCommand = 'ssh -i "{0}" -o BatchMode=yes "{1}" "bash -se" < "{2}"' -f $keyPath, $target, $scriptPath

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
            echo 'CI/CD 빌드 및 OCI 배포 성공!'
        }

        failure {
            echo 'CI/CD 빌드 또는 OCI 배포 실패! 콘솔 로그를 확인하세요.'
        }
    }
}
