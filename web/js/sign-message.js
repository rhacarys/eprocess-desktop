/**
 * Depends:
 *     - jsrsasign-latest-all-min.js
 *     - qz-tray.js
 *
 * Steps:
 *     1. Convert private key to jsrsasign compatible format:
 *        openssl rsa -in private-key.key -out private-key-updated.key
 *
 *     2. Include jsrsasign into your web page
 *        <script src="https://cdn.rawgit.com/kjur/jsrsasign/master/jsrsasign-latest-all-min.js"></script>
 *
 *     3. Include this script into your web page
 *        <script src="path/to/sign-message.js"></script>
 *
 *     4. Remove any other references to setSignaturePromise
 */
var privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
				"MIIEogIBAAKCAQEAyQDP9HIBZ/hXtdm1zzlzOEmfFeH91KfGxAwYVYciFmwIPc/I\n" +
				"CEw6hCOts0J9rtY4xl+4KM9Mijv1AK7vY9DVIkpLUIq8KaCR03Rwa9FVSEtUHZ3x\n" +
				"FDehp6cwfcwROTZYhk7AyVZ/EQ5yjbWF3syqoz3yLyiomgkWV0Dwli8nZxF/cYdd\n" +
				"FAYH8+8YwD/8x39MCGu2wrVjHv7Q0mBEa9pPJpAqg6tW7vHushQORbX0foDRJtz8\n" +
				"EACHGB25xEWy46GqJT9P8hoAJQS0Bu++0sQAdlStzS123wXoknRfXrWNF4ohyzdi\n" +
				"/kLXQQDYhkLW2tz9Oq/lPCPYD2LtcQ1XeEcS/QIDAQABAoIBAG7KNtVavXGsTdQb\n" +
				"Qv4MgXimA7zRCtLbA1DuBb+ms2A+fbBsRaplMd/Btq1W2eWziCm3es9rq5mKALo2\n" +
				"shaHdeLnpmmnfp1HNvmhdZHIGUS22I9WjhoBq+W33p+ICnpJfTPSVqrjNTiWiQeY\n" +
				"5Ufv8aH2s/XQaFY26gx/69wr84D4BvoDTyF3y77bI+lqDQM5UIy6UICX6QbWfDS2\n" +
				"chULzq6ZE3UXxfxk0kKKY4zJu9i1a/Xr3kpqirFt4RSiOJ2vpcV4TPNvESOP3YWp\n" +
				"fVghovgQUOexrNNJRqkj7W14XUvpS8Oz3R6gmX2elRma0qe716RZKNWrfEssIWeC\n" +
				"kMys2BUCgYEA5jRw8FonkaDptOqJtIgj3lS+iOryi3Mi7/2QKUNHJnXurFf+08hw\n" +
				"tSmxckdHm1oEUmDBFGWXx687/88HSk6tsvU8RzANtebOZecwi36TAr/xVRbTYts5\n" +
				"jKrOKW4A/nH3t6PZxtL/4iI7yCkrt0SmrUKwUG3r/GwlIyyb9YpA0x8CgYEA34a0\n" +
				"SpquGYYarkAZzr2db/9qRSGKNOiIhZfamJ1Gi19Ek35u2xG8BSTS6F48nz4z6A3r\n" +
				"97j5U6Oy7ZF3GEfnAeqRG/5kj3L+zMxu5+ms6W6PctID11R7XPx5NkNwcIon1kzC\n" +
				"PsgN1yJMbPeADNaFsKdoVqeXozxHqHckjSE60mMCgYAVQ+bi7REWrMyp/7h0yta1\n" +
				"ZRKsxzdclmg1zzCP/z0FWoOPvBk4Kx3uPkm8CsWcnjYj/fgYEEJzGcbrHI7J2HbH\n" +
				"P0wwL0o08XtDgpXDbQ/bgyuh9pfUW/f2/JhKz3ScDJ8bgbFOYpNsqkjndpYpOdhD\n" +
				"zRNIer7/9GWB2E8X4YuXdQKBgEOoZVgTm9ET+21075rZYBcva35DWa8MF47AWSVK\n" +
				"rbiv/HTclzduFchEziYRqC3XSSTbWepgre84JIeuOSs7UFwPIR8lEFEvUJqy4nsG\n" +
				"CxWZJohts0XxR2ns7c+T+CvIVaiTEVSpfy74LcPp4nOAV3USJw3bqpV5QINolMK3\n" +
				"JTPbAoGAJs+0BQ+Y6hQFCW+TPDI11hNJgjdCr5RvBl/Ej9nYere1yhDTa+bbqO4d\n" +
				"if44PU4VUzWQQAWfbbdKgIfr7BEhmOln/nDISRPWihv6K/pva8m55jy9w3Qg5Rql\n" +
				"AGcvcpEaARgeIfZaMaHh7Ms9YwKdSQ8OSYp5omJW9Mm6OzuQb3c=\n" +
				"-----END RSA PRIVATE KEY-----\n";

qz.security.setSignaturePromise(function(toSign) {
    return function(resolve, reject) {
        try {
            var pk = new RSAKey();
            pk.readPrivateKeyFromPEMString(strip(privateKey));
            var hex = pk.signString(toSign, 'sha1');
            console.log("DEBUG: \n\n" + stob64(hextorstr(hex)));
            resolve(stob64(hextorstr(hex)));
        } catch (err) {
            console.error(err);
            reject(err);
        }
    };
});

function strip(key) {
    if (key.indexOf('-----') !== -1) {
        return key.split('-----')[2].replace(/\r?\n|\r/g, '');
    }
}