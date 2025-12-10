# Keystores Configuration

This directory contains the keystores and certificates for the MIRUVIC application.

## Files

- **server.jks** - Server keystore containing:
  - `server` (PrivateKeyEntry) - Server's private key and certificate
  - `client1` (trustedCertEntry) - Client 1's trusted certificate
  - `client2` (trustedCertEntry) - Client 2's trusted certificate

- **server.cer** - Exported certificate of the server (imported in client keystores)

- **client1.jks** - Client 1 keystore containing:
  - `client1` (PrivateKeyEntry) - Client 1's private key and certificate
  - `server` (trustedCertEntry) - Server's trusted certificate

- **client1.cer** - Exported certificate of client 1 (imported in server keystore)

- **client2.jks** - Client 2 keystore containing:
  - `client2` (PrivateKeyEntry) - Client 2's private key and certificate
  - `server` (trustedCertEntry) - Server's trusted certificate

- **client2.cer** - Exported certificate of client 2 (imported in server keystore)

## Keystore Passwords

| Keystore | Password | Alias | Type |
|----------|----------|-------|------|
| server.jks | serverPassword123 | server | PrivateKey |
| client1.jks | client1Password123 | client1 | PrivateKey |
| client2.jks | client2Password123 | client2 | PrivateKey |

## Certificate Details

All certificates are:
- Algorithm: RSA (2048 bits)
- Validity: 365 days
- Signature Algorithm: SHA384withRSA
- Self-signed certificates

### Server Certificate
- CN: localhost
- OU: Server
- O: MIRUVIC
- L: Barcelona
- ST: Catalonia
- C: ES

### Client Certificates
- CN: client1/client2
- OU: Client
- O: MIRUVIC
- L: Barcelona
- ST: Catalonia
- C: ES

## Usage

The keystores are configured in `crypto.properties`:
- Keystore path: `keystores/miruvic-keystore.jks` (update this path as needed)
- Keystore password: Configured in `crypto.properties`
- Keystore type: JKS

## Security Notes

- These are self-signed certificates for development/testing only
- Do NOT use these in production
- For production, use properly signed certificates from a Certificate Authority (CA)
- Store passwords securely and never commit them to version control
