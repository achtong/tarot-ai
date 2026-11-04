withCredentials([
    sshUserPrivateKey(
        credentialsId: 'oci-ssh-key',
        keyFileVariable: 'SSH_KEY',
        usernameVariable: 'SSH_USER'
    )
]) {
    bat 'echo SSH Credentials loaded'
}